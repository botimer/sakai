package org.sakaiproject.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * SakaiProperties is in an intermediate state. The two configurers are now registered as beans,
 * but they do not have access to these, the real, raw properties by default. They need to be
 * connected, and that happens in the {@link PropertyFixerUpper}. If we can determine that the
 * giant proxy interface on this class is not used anywhere, we could finish the inversion,
 * injecting SakaiProperties into them (setting the "properties" property as ref to that bean).
 * Then they would function as normal BeanFactoryPostProcessors, and the Fixer Upper can go away.
 */
public class PropertyFixerUpper implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        PropertyPlaceholderConfigurer placeholder = (PropertyPlaceholderConfigurer) beanFactory
                .getBean("org.sakaiproject.component.PropertyPlaceholderConfigurer");
        ReversiblePropertyOverrideConfigurer overrides = (ReversiblePropertyOverrideConfigurer) beanFactory
                .getBean("org.sakaiproject.component.ReversiblePropertyOverrideConfigurer");
        SakaiProperties props = (SakaiProperties) beanFactory
                .getBean("org.sakaiproject.component.SakaiProperties");

        placeholder.setProperties(props.getRawProperties());
        placeholder.postProcessBeanFactory(beanFactory);

        overrides.setProperties(props.getRawProperties());
        overrides.postProcessBeanFactory(beanFactory);
    }
}
