package org.sakaiproject.modi;

import lombok.extern.slf4j.Slf4j;
import org.sakaiproject.util.ReversiblePropertyOverrideConfigurer;
import org.sakaiproject.util.SakaiProperties;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SakaiPropertiesShim extends SakaiProperties implements PriorityOrdered {
    private final FileMappedProperties properties;
    private boolean finishedInit = false;

    public SakaiPropertiesShim(FileMappedProperties properties) {
        this.properties = properties;
    }

    // These first setters and getters are my noise that will be deleted on revert of the first attempt to open up.
    @Override
    public SakaiPropertiesFactoryBean getPropertiesFactoryBean() {
        throw new RuntimeException();
    }

    @Override
    public ReversiblePropertyOverrideConfigurer getPropertyOverrideConfigurer() {
        throw new RuntimeException();
    }

    @Override
    public PropertyPlaceholderConfigurer getPropertyPlaceholderConfigurer() {
        throw new RuntimeException();
    }

    @Override
    public void setPropertyOverrideConfigurer(ReversiblePropertyOverrideConfigurer propertyOverrideConfigurer) {
        throw new RuntimeException();
    }

    @Override
    public void setPropertyPlaceholderConfigurer(PropertyPlaceholderConfigurer propertyPlaceholderConfigurer) {
        throw new RuntimeException();
    }
    ////////////////////////////////////////////////////////////////////

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("afterPropertiesSet() -- any settings are now final.");
        finishedInit = true;
    }

    @Override
    public void setOrder(int order) {
        if (finishedInit) {
            throw new InitializationException("Attempted to adjust load order after properties are loaded.");
        } else {
            log.debug("Delegating setOrder() to properties.");
            properties.setOrder(order);
        }
    }

    @Override
    public int getOrder() {
        log.debug("Delegating getOrder() to properties.");
        return properties.getOrder();
    }

    @Override
    public Collection<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        log.debug("getBeanFactoryPostProcessors() ignored");
        return Collections.emptyList();
    }

    @Override
    public Map<String, Properties> getSeparateProperties() {
        return properties.getAppliedPropertySources().stream()
                .map(s -> (PropertySource<Properties>) s)
                .collect(Collectors.toMap(PropertySource::getName, PropertySource::getSource));
    }

    @Override
    public Properties getProperties() {
        try {
            return properties.mergeProperties();
        } catch (IOException e) {
            log.error("Getting base Sakai properties from shim threw IOException... This should never happen because "
                    + "the real properties should already be loaded and injected. Attempting to proceed by returning "
                    + "an empty properties object.", e);
            return new Properties();
        }
    }

    @Override
    public Properties getRawProperties() {
        return getProperties();
//        FIXME: This is definitely bugged... Need to figure out how to actually get the raw props
//        List<Properties> allProperties = properties.getAppliedPropertySources().stream()
//                .map(s -> (PropertySource<Properties>) s)
//                .map(s -> s.getSource())
//                .collect(Collectors.toList());
//        Properties fullyMerged = new Properties();
//        for (Properties theseProps : allProperties) {
//            theseProps.forEach((key, value) -> {
//                fullyMerged.setProperty((String) key, (String) value);
//            });
//        }
//        return fullyMerged;
    }

    @Override
    public void setProperties(Properties properties) {
        log.error("setProperties() called on shim; ignored. Properties should only be injected at construction.");
    }

    @Override
    public void setPropertiesArray(Properties[] propertiesArray) {
        log.error("setPropertiesArray() called on shim; ignored. Properties should only be injected at construction.");
    }

    @Override
    public void setLocation(Resource location) {
        log.error("setLocation() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setLocations(Resource[] locations) {
        log.error("setLocations() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setFileEncoding(String encoding) {
        log.error("setFileEncoding() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
        log.error("setIgnoreResourceNotFound() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setLocalOverride(boolean localOverride) {
        log.error("setLocalOverride() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
        log.error("setIgnoreUnresolvablePlaceholders() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setPlaceholderPrefix(String placeholderPrefix) {
        log.error("setPlaceholderPrefix() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setPlaceholderSuffix(String placeholderSuffix) {
        log.error("setPlaceholderSuffix() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setSearchSystemEnvironment(boolean searchSystemEnvironment) {
        log.error("setSearchSystemEnvironment() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setSystemPropertiesMode(int systemPropertiesMode) {
        log.error("setSystemPropertiesMode() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException {
        log.error("setSystemPropertiesModeName() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setBeanNameAtEnd(boolean beanNameAtEnd) {
        log.error("setBeanNameAtEnd() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setBeanNameSeparator(String beanNameSeparator) {
        log.error("setBeanNameSeparator() called on shim; ignored. All configuration should be done on underlying properties.");
    }

    @Override
    public void setIgnoreInvalidKeys(boolean ignoreInvalidKeys) {
        log.error("setIgnoreInvalidKeys() called on shim; ignored. All configuration should be done on underlying properties.");
    }
}
