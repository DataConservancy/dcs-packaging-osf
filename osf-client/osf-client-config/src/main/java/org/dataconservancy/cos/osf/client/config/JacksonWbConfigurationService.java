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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;

/**
 * A configuration service implementation that reads JSON-formatted Waterbutler client configuration from the classpath
 * using Jackson.  By default this service reads the configuration from the classpath resource
 * {@link #DEFAULT_CONFIGURATION_RESOURCE /org/dataconservancy/cos/osf/client/config/osf-client.json}.
 * <p>
 * The supported configuration file format is a function of the {@link DefaultWbJacksonConfigurer}.
 * </p>
 *
 * @see DefaultWbJacksonConfigurer
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class JacksonWbConfigurationService extends AbstractJacksonConfigurationService
        implements WbConfigurationService {

    /**
     * A new configuration service using the {@link #DEFAULT_CONFIGURATION_RESOURCE} and a default instance of a
     * Jackson {@code ObjectMapper}.
     *
     * @throws IllegalArgumentException if the default configuration resource is not found on the classpath
     */
    public JacksonWbConfigurationService() {
        super(DEFAULT_CONFIGURATION_RESOURCE);

        try {
            getConfigurationResource(configurationResource);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     *
     * @param configurationResource
     */
    public JacksonWbConfigurationService(final String configurationResource) {
        super(configurationResource);
    }

    /**
     *
     * @param configurationResource
     * @param mapper
     */
    public JacksonWbConfigurationService(final String configurationResource, final ObjectMapper mapper) {
        super(configurationResource, mapper);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws RuntimeException if the configuration resource cannot be found on the classpath, or if there is trouble
     *                          reading the resource.
     */
    public WbClientConfiguration getConfiguration() {
        final URL configUrl = getConfigurationResource(configurationResource);

        try {
            final JsonNode config = mapper.readTree(IOUtils.toString(configUrl, "UTF-8"));
            return new DefaultWbJacksonConfigurer<WbClientConfiguration>()
                    .configure(config, mapper, WbClientConfiguration.class);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(ERR_READING_RESOURCE, configurationResource, e.getMessage()), e);
        }
    }
}
