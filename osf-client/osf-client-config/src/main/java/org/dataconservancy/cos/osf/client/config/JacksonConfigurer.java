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

/**
 * Responsible for taking a JSON node that represents a configuration file, and returning an instance of a Java object that
 * encapsulates that configuration.
 *
 * @param <T> the type of the Java object that encapsulates the JSON configuration
 */
@FunctionalInterface
public interface JacksonConfigurer<T> {

    /**
     * Given a Jackson object mapper, the JSON, and the configuration class to be created, map the configuration JSON
     * into an instance of the {@code configurationClass}.
     *
     * @param configRoot the JSON node containing the configuration
     * @param mapper the Jackson object mapper
     * @param configurationClass the class that will be used to encapsulate the JSON configuration found in the {@code configRoot}
     * @return an instance of the the configuration class specified by {@code <T>}
     */
    T configure(JsonNode configRoot, ObjectMapper mapper, Class<T> configurationClass);

}
