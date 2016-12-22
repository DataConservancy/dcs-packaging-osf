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

import java.io.IOException;

/**
 * Default implementation that can read a JSON configuration and return an {@code OsfClientConfiguration}.
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
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class DefaultOsfJacksonConfigurer<T> implements JacksonConfigurer<T> {

    @Override
    public T configure(final JsonNode configRoot, final ObjectMapper mapper, final Class<T> configurationClass) {
        try {
            return mapper.treeToValue(configRoot.get("osf").get("v2"), configurationClass);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(
                            AbstractJacksonConfigurationService.ERR_MAPPING_NODE, e.getMessage(), configRoot.asText()),
                    e);
        }

    }

}
