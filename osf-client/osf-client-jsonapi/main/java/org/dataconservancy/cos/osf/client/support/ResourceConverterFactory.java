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
package org.dataconservancy.cos.osf.client.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.RelationshipResolver;
import com.github.jasminb.jsonapi.ResourceConverter;

/**
 * Produces instances of the JSON API ResourceConverter.
 */
public class ResourceConverterFactory {

    /**
     * Creates a new ResourceConverter instance configured with the supplied parameters.
     *
     * @param mapper the Jackson ObjectMapper the converter will use
     * @param scanner scanner used to detect the classes that represent JSON-API types
     * @param globalResolver the resolver used to resolve JSON-API relationships
     * @return the JSON API resource converter
     */
    public ResourceConverter newConverter(ObjectMapper mapper, ModelClassScanner scanner, RelationshipResolver globalResolver) {
        ResourceConverter converter = new ResourceConverter(mapper, scanner.getDetectedClasses().toArray(new Class[]{}));
        converter.setGlobalResolver(globalResolver);
        return converter;
    }

}
