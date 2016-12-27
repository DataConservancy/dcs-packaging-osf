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
package org.dataconservancy.cos.rdf.support;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class Util {

    private static final String RELATIVE_ID_PREFIX = ":";

    private Util() {
        // prevent instantiation
    }

    /**
     * Prefixes the supplied identifier with a {@code :} to make it relative to the current resource.  If the identifier
     * is already prefixed, it is returned unchanged.
     *
     * @param id the resource identifier
     * @return the prefixed identifier
     */
    public static String relativeId(final String id) {
        if (id == null || id.trim().length() == 0) {
            throw new IllegalArgumentException("id must not be null or empty");
        }
        if (id.startsWith(RELATIVE_ID_PREFIX)) {
            return id;
        }

        return RELATIVE_ID_PREFIX + id;
    }

    /**
     * Returns the supplied uri as a {@code Resource}.
     *
     * @param uriRef the resource identifier
     * @return the Resource
     */
    public static Resource asResource(final String uriRef) {
        if (uriRef == null || uriRef.trim().length() == 0) {
            throw new IllegalArgumentException("uriRef must not be null or empty");
        }
        return ResourceFactory.createResource(uriRef);
    }


    /**
     * Returns the supplied uri as a {@code Property}.
     *
     * @param uriRef the properties identifier
     * @return the Property
     */
    public static Property asProperty(final String uriRef) {
        if (uriRef == null || uriRef.trim().length() == 0) {
            throw new IllegalArgumentException("uriRef must not be null or empty");
        }
        return ResourceFactory.createProperty(uriRef);
    }
}
