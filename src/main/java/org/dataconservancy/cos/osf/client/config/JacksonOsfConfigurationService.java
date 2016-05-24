/*
 * Copyright 2016 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dataconservancy.cos.osf.client.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;

/**
 * A configuration service implementation that reads JSON-formatted OSF client configuration from the classpath using
 * Jackson.  By default this service reads the configuration from the classpath resource
 * {@link #DEFAULT_CONFIGURATION_RESOURCE /org/dataconservancy/cos/osf/client/config/osf-client.json}.
 * Example configuration:
 * <pre>
 * {
 *   "osf:" {
 *     "v2": {
 *       "host": "192.168.99.100",
 *       "port": "8000",
 *       "basePath": "/v2/",
 *       "authHeader": "Basic ADFlkdsfadUdfjaLjfoir=="
 *     }
 *   }
 * }
 * </pre>
 */
public class JacksonOsfConfigurationService implements OsfConfigurationService {

    public static final String DEFAULT_CONFIGURATION_RESOURCE = "osf-client.json";

    private static final String ERR_RESOLVE_RESOURCE = "Error resolving classpath resource %s: %s";

    private static final String ERR_READING_RESOURCE = "Error reading classpath resource %s: %s";

    private final String configurationResource;

    private final ObjectMapper mapper;

    /**
     * A new configuration service using the {@link #DEFAULT_CONFIGURATION_RESOURCE} and a default instance of a
     * Jackson {@code ObjectMapper}.
     *
     * @throws IllegalArgumentException if the default configuration resource is not found on the classpath
     */
    public JacksonOsfConfigurationService() {
        this.configurationResource = DEFAULT_CONFIGURATION_RESOURCE;
        this.mapper = new ObjectMapper();

        try {
            getConfiguration();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

    }

    /**
     * A new configuration service using the supplied {@code configurationResource} and a default instance of a
     * Jackson {@code ObjectMapper}.
     *
     * @param configurationResource a classpath resource containing the OSF client configuration
     * @throws IllegalArgumentException if the default configuration resource is not found on the classpath, or any
     *                                  construction parameters are empty or {@code null}.
     */
    public JacksonOsfConfigurationService(String configurationResource) {
        if (configurationResource == null || configurationResource.trim().length() == 0) {
            throw new IllegalArgumentException("Configuration resource must not be empty or null.");
        }
        this.configurationResource = configurationResource;
        this.mapper = new ObjectMapper();

        try {
            getConfiguration();
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
    public JacksonOsfConfigurationService(String configurationResource, ObjectMapper mapper) {
        if (configurationResource == null || configurationResource.trim().length() == 0) {
            throw new IllegalArgumentException("Configuration resource must not be empty or null.");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("Jackson ObjectMapper must not be empty or null.");
        }
        this.configurationResource = configurationResource;
        this.mapper = mapper;

        try {
            getConfiguration();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws RuntimeException if the configuration resource cannot be found on the classpath, or if there is trouble
     *                          reading the resource.
     */
    public OsfClientConfiguration getConfiguration() {
        URL configUrl = this.getClass().getResource(configurationResource);

        if (configUrl == null) {
            throw new RuntimeException(
                    String.format(ERR_RESOLVE_RESOURCE, configurationResource, "not found on classpath"));
        }

        try {
            JsonNode config = mapper.readTree(IOUtils.toString(configUrl, "UTF-8"));
            return mapper.treeToValue(config.get("osf").get("v2"), OsfClientConfiguration.class);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(ERR_READING_RESOURCE, configurationResource, e.getMessage()), e);
        }
    }
}
