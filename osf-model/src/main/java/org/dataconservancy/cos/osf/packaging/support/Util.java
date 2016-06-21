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
package org.dataconservancy.cos.osf.packaging.support;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Created by esm on 6/1/16.
 */
public class Util {

    private static final String RELATIVE_ID_PREFIX = ":";


    public static String relativeId(String id) {
        if (id == null || id.trim().length() == 0) {
            throw new IllegalArgumentException("id must not be null or empty");
        }
        if (id.startsWith(RELATIVE_ID_PREFIX)) {
            return id;
        }

        return RELATIVE_ID_PREFIX + id;
    }

    public static Resource asResource(String uriRef) {
        if (uriRef == null || uriRef.trim().length() == 0) {
            throw new IllegalArgumentException("uriRef must not be null or empty");
        }
        return ResourceFactory.createResource(uriRef);
    }
}
