package org.dataconservancy.cos.osf.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract base class for client configurations that use JSON.  This class provides a Jackson {@code ObjectMapper} to
 * concrete subclasses for reading JSON configurations.
 */
public abstract class AbstractJacksonConfigurationService extends BaseConfigurationService {

    public static final String DEFAULT_CONFIGURATION_RESOURCE = "osf-client.json";

    final ObjectMapper mapper;

    /**
     * A new configuration service using the supplied {@code configurationResource} and a default instance of a
     * Jackson {@code ObjectMapper}.
     *
     * @param configurationResource a classpath resource containing the OSF client configuration
     * @throws IllegalArgumentException if the default configuration resource is not found on the classpath, or any
     *                                  construction parameters are empty or {@code null}.
     */
    public AbstractJacksonConfigurationService(String configurationResource) {
        if (configurationResource == null || configurationResource.trim().length() == 0) {
            throw new IllegalArgumentException("Configuration resource must not be empty or null.");
        }
        this.configurationResource = configurationResource;
        this.mapper = new ObjectMapper();

        try {
            getConfigurationResource(this.configurationResource);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * A new configuration service using the supplied {@code configurationResource} and the supplied Jackson
     * {@code ObjectMapper}.
     *
     * @param configurationResource a classpath resource containing the OSF client configuration
     * @param mapper                a configured Jackson {@code ObjectMapper}, used to deserialize the OSF client
     *                              configuration
     * @throws IllegalArgumentException if the default configuration resource is not found on the classpath, or any
     *                                  construction parameters are empty or {@code null}.
     */
    public AbstractJacksonConfigurationService(String configurationResource, ObjectMapper mapper) {
        if (configurationResource == null || configurationResource.trim().length() == 0) {
            throw new IllegalArgumentException("Configuration resource must not be empty or null.");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("Jackson ObjectMapper must not be empty or null.");
        }
        this.configurationResource = configurationResource;
        this.mapper = mapper;

        try {
            getConfigurationResource(this.configurationResource);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }


}
