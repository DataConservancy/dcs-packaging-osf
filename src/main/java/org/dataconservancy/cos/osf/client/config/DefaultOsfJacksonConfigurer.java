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
 */
public class DefaultOsfJacksonConfigurer<T> implements JacksonConfigurer<T> {

    @Override
    public T configure(JsonNode configRoot, ObjectMapper mapper, Class<T> configurationClass) {
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
