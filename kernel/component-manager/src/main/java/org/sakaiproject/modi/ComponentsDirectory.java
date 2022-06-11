package org.sakaiproject.modi;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A collection of "traditional" components. These are the typical impl jars packaged with the "component" type, which
 * means that they are expanded on disk with a {@code WEB-INF} directory, with a {@code lib} directory and
 * {@code components.xml} inside. The {@code components} directory is almost always within Tomcat's main directory
 * (CATALINA_HOME / CATALINA_BASE), a sibling to {@code webapps}.
 *
 * This class is designed as immutable, with all fields being set at construction.
 */
@Slf4j
public class ComponentsDirectory {
    protected final Path rootPath;
    protected final Path overridePath;

    protected final List<TraditionalComponent> components;

    protected final Optional<ComponentOverrides> overrides;

    /**
     * Construct a set of traditional components from a root directory and a directory of override files.
     * @param rootPath the root directory for components (one per directory); may not be null
     * @param overridePath a directory with override files; each matching a component directory name
     *                       with .xml extension; may be null
     */
    public ComponentsDirectory(@NonNull Path rootPath, Path overridePath) {
        this.rootPath = rootPath;
        this.overridePath = overridePath;
        components = findComponents();
        overrides = findOverrides();
    }

    /**
     * Signal that the container has started and components should load.
     */
    public void starting(SharedApplicationContext context) {
        log.info("Starting traditional components in: {}", rootPath);
        components.forEach(context::registerBeanSource);
        overrides.ifPresent(context::registerBeanSource);
    }

    /**
     * Signal that the container is stopping and components should shut down. Components are typically stateless,
     * so there is usually very little to do, but this unwinds the application context.
     */
    public void stopping() {
        log.info("Stopping traditional components in: {}", rootPath);
    }

    /**
     * Find all of the directories that are valid components and construct those objects.
     *
     * This does not register or instantiate them, but holds onto them until we start.
     *
     * @return all of the components within this directory, sorted in directory name order
     */
    protected List<TraditionalComponent> findComponents() {
        try (Stream<Path> stream = Files.list(rootPath)) {
            return stream
                    .filter(Files::isDirectory)
                    .sorted()
                    .map(TraditionalComponent::fromDisk)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("Error locating components in: {}", rootPath, e);
            return List.of();
        }
    }

    /**
     * Look for overrides matching the components we have located.
     *
     * It isn't strictly necessary to have overrides, so we use the Optional API.
     *
     * @return any overrides for our components in the override path supplied; may be empty
     */
    protected Optional<ComponentOverrides> findOverrides() {
        return overridePath != null && Files.isDirectory(overridePath)
                ? Optional.of(new ComponentOverrides(components, overridePath))
                : Optional.empty();
    }
}