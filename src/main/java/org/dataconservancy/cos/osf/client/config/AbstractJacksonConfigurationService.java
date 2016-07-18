/*
 * Copyright 2016 Johns Hopkins University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dataconservancy.cos.osf.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract base class for client configurations that use JSON.  This class provides a Jackson {@code ObjectMapper} to
 * concrete subclasses for reading JSON configurations.
 */
public abstract class AbstractJacksonConfigurationService extends BaseConfigurationService {

    public static final String DEFAULT_CONFIGURATION_RESOURCE = System.getProperty("osf.client.conf",
            "/org/dataconservancy/cos/osf/client/config/osf-client.json");

    /**
     * Error message when a JSON node cannot be mapped to a configuration.  Parameters are: error message, contents
     * of the JSON node
     */
    static final String ERR_MAPPING_NODE = "Error mapping JSON resource (Error: %s): %s";

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
            LOG.debug("Configuring {} with classpath resource {}",
                    this.getClass().getName(), this.configurationResource);
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
            LOG.debug("Configuring {} with classpath resource {}",
                    this.getClass().getName(), this.configurationResource);
            getConfigurationResource(this.configurationResource);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }


}
