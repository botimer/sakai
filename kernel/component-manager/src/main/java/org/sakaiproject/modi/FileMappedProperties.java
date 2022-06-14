package org.sakaiproject.modi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.PropertySource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class FileMappedProperties extends PropertySourcesPlaceholderConfigurer {
    public Map<String, Properties> getMappedProperties() {
        return getAppliedPropertySources().stream()
                .map(source -> (PropertySource<Properties>) source)
                .collect(Collectors.toMap(PropertySource::getName, PropertySource::getSource));
    }

    @Override
    public Properties mergeProperties() throws IOException {
        return super.mergeProperties();
    }
}
