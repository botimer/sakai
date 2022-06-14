package org.sakaiproject.modi;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;

public class SakaiHomeCheck implements BeanFactoryPostProcessor, PriorityOrdered {
    @Getter
    @Setter
    private int order = 0;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (System.getProperty("sakai.home") == null) {
            throw new InitializationException(
                    "The sakai.home system property is not set. Cannot load Sakai configuration.\n"
                    + "The Environment will ensure this is set immediately before starting the Spring context,\n"
                    + "so something has fouled the system properties during boot."
            );
        }
    }
}
