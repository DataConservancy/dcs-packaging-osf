package org.dataconservancy.cos.osf.client.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Default implementation that can read a JSON configuration and return a {@code WbClientConfiguration}.
 * Example configuration:
 * <pre>
 * {
 *   "wb:" {
 *     "v1": {
 *       "host": "192.168.99.100",
 *       "port": "7777",
 *       "basePath": "/v1/"
 *     }
 *   }
 * }
 * </pre>
 */
public class DefaultWbJacksonConfigurer<T> implements JacksonConfigurer<T> {

    @Override
    public T configure(JsonNode configRoot, ObjectMapper mapper, Class<T> configurationClass) {
        try {
            return mapper.treeToValue(configRoot.get("wb").get("v1"), configurationClass);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(
                            AbstractJacksonConfigurationService.ERR_MAPPING_NODE, e.getMessage(), configRoot.asText()),
                    e);
        }
    }
}
