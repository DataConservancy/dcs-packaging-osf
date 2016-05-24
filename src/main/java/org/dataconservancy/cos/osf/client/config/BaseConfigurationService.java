package org.dataconservancy.cos.osf.client.config;

import java.net.URL;

/**
 * Abstract base class for client configuration.  Provides a method to subclasses for resolving a classpath resource
 * containing the client configuration.
 */
public abstract class BaseConfigurationService {

    /**
     * Error message when a classpath resource cannot be resolved.  Parameters are: name of the resource, error message.
     */
    static final String ERR_RESOLVE_RESOURCE = "Error resolving classpath resource %s: %s";

    /**
     * Error message when a classpath resource cannot be read.  Parameters are: name of the resource, error message.
     */
    static final String ERR_READING_RESOURCE = "Error reading classpath resource %s: %s";

    /**
     * The classpath resource that contains the client configuration.  Subclasses are responsible for setting this
     * member, typically by invoking {@link #getConfigurationResource(String)}.
     */
    String configurationResource;

    /**
     * Resolve the supplied classpath resource to a URL.  If the resource is not able to be resolved, a
     * {@code RuntimeException} is thrown.
     *
     * @param configurationResource the classpath resource containing the client configuration
     * @return the URL of the resolved resource
     * @throws RuntimeException if the classpath resource cannot be resolved
     */
    public static URL getConfigurationResource(String configurationResource) {
        URL configUrl = BaseConfigurationService.class.getResource(configurationResource);

        if (configUrl == null) {
            throw new RuntimeException(
                    String.format(ERR_RESOLVE_RESOURCE, configurationResource, "not found on classpath"));
        }

        return configUrl;
    }

}
