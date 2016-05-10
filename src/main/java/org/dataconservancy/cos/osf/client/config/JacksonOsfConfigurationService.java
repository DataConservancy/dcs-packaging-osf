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
 *   "v2": {
 *     "host": "192.168.99.100",
 *     "port": "8000",
 *     "basePath": "/v2/",
 *     "authHeader": "Basic ADFlkdsfadUdfjaLjfoir=="
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

    public JacksonOsfConfigurationService() {
        this.configurationResource = DEFAULT_CONFIGURATION_RESOURCE;
        this.mapper = new ObjectMapper();
    }

    public JacksonOsfConfigurationService(String configurationResource) {
        this.configurationResource = configurationResource;
        this.mapper = new ObjectMapper();
    }

    public JacksonOsfConfigurationService(String configurationResource, ObjectMapper mapper) {
        this.configurationResource = configurationResource;
        this.mapper = mapper;
    }

    public OsfClientConfiguration getConfiguration() {
        URL configUrl = this.getClass().getResource(configurationResource);

        if (configUrl == null) {
            throw new RuntimeException(
                    String.format(ERR_RESOLVE_RESOURCE, configurationResource, "not found on classpath"));
        }

        try {
            JsonNode config = mapper.readTree(IOUtils.toString(configUrl, "UTF-8"));
            return mapper.treeToValue(config.get("v2"), OsfClientConfiguration.class);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(ERR_READING_RESOURCE, configurationResource, e.getMessage()), e);
        }
    }
}
