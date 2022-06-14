package org.sakaiproject.modi;

import org.junit.Ignore;
import org.junit.Test;
import org.sakaiproject.util.SakaiProperties;
import org.springframework.beans.PropertyValue;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

public class PropertiesFilesTest {
    @Test
    public void givenNoSakaiHomeValue_everythingBlowsUp() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.load("classpath:org/sakaiproject/config/modi-configuration.xml");

        assertThatExceptionOfType(InitializationException.class).isThrownBy(() -> {
            context.refresh();
        }).withMessageContaining("sakai.home system property is not set");
    }

    @Test
    public void givenASakaiHomeValue_thenThePropFilesAreResolved() {
        System.setProperty("sakai.home", Path.of("src/test/resources/fake-tomcat/sakai").toAbsolutePath().toString());
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.load("classpath:org/sakaiproject/config/modi-configuration.xml");
        context.refresh();

        PropertyValue locations = context.getBeanDefinition("optionalProperties")
                .getPropertyValues().getPropertyValue("locations");
        assertThat(locations.getValue().toString()).contains("fake-tomcat/sakai/testing.properties");
    }

    @Test
    public void givenASystemPropUsedAsBeanValue_thenItIsExpandedOnFirstPass() {
        System.setProperty("sakai.home", Path.of("src/test/resources/fake-tomcat/sakai").toAbsolutePath().toString());
        System.setProperty("kernel.test.key", "set from system");
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.load("classpath:org/sakaiproject/config/modi-configuration.xml");
        context.refresh();

        StubbyStub stub = (StubbyStub) context.getBean("stubbyBean");
        assertThat(stub.getSomethingFromKernelProps()).isEqualTo("set from system");
    }

    @Test
    public void givenAPlaceholderBeanValue_whenItIsSetInPropsFile_thenItIsResolved() {
        System.setProperty("sakai.home", Path.of("src/test/resources/fake-tomcat/sakai").toAbsolutePath().toString());
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.load("classpath:org/sakaiproject/config/modi-configuration.xml");
        context.refresh();

        StubbyStub stub = (StubbyStub) context.getBean("stubbyBean");
        assertThat(stub.getSomethingFromKernelProps()).isEqualTo("kernel");
    }

    // The inital config beans should only get system props expanded
    // Kernel is all literals that can be expanded or overridden elsewhere
    // Custom properties files can use placeholders, filled in by kernel/sys props (or cascading files?) --  but don't change beans yet
    // Once everything is merged, THEN we can fill in all the component beans properties.
    // Just more evidence that the config/kernel should get their own context to be parent of components...
    @Test
    public void givenALiteralInKernel_whenItIsSetInPropsFile_thenItIsOverridden() {
        System.setProperty("sakai.home", Path.of("src/test/resources/fake-tomcat/sakai").toAbsolutePath().toString());
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.load("classpath:org/sakaiproject/config/modi-configuration.xml");
        context.refresh();

        StubbyStub stub = (StubbyStub) context.getBean("stubbyBean");
        assertThat(stub.getAutoDdl()).isEqualTo("is overridden in testing.propertiesx");
    }

    @Test
    public void givenASakaiHomeValue_weCanGetRawProperties() {
        System.setProperty("sakai.home", Path.of("src/test/resources/fake-tomcat/sakai").toAbsolutePath().toString());
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.load("classpath:org/sakaiproject/config/modi-configuration.xml");
        context.refresh();

        SakaiProperties properties = (SakaiProperties) context.getBean("org.sakaiproject.component.SakaiProperties");
        assertThat(properties.getRawProperties().getProperty("placeholder.test")).isEqualTo("looooooooooooool");
    }
}
