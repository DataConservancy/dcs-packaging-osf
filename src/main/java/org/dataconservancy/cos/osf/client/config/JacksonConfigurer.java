package org.dataconservancy.cos.osf.client.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Responsible for taking a JSON node that represents a configuration file, and returning an instance of a Java object that
 * encapsulates that configuration.
 *
 * @param <T> the type of the Java object that encapsulates the JSON configuration
 */
@FunctionalInterface
public interface JacksonConfigurer<T> {

    /**
     * Given a Jackson object mapper, the JSON, and the configuration class to be created, map the configuration JSON into an instance
     * of the {@code configurationClass}.
     *
     * @param configRoot the JSON node containing the configuration
     * @param mapper the Jackson object mapper
     * @param configurationClass the class that will be used to encapsulate the JSON configuration found in the {@param configRoot}
     * @return
     */
    T configure(JsonNode configRoot, ObjectMapper mapper, Class<T> configurationClass);

}
