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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Maintains constants relating to the use of RDF.
 */
public class Rdf {

    public static final Function IDENTITY = Function.identity();

    /**
     * Common RDF namespaces and namespace prefixes.
     */
    public static class Ns {

        /**
         * The RDF namespace http://www.w3.org/1999/02/22-rdf-syntax-ns#
         */
        public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        /**
         * The OWL namespace http://www.w3.org/2002/07/owl#
         */
        public static final String OWL = "http://www.w3.org/2002/07/owl#";

        /**
         * The XSD namespace http://www.w3.org/2001/XMLSchema#
         */
        public static final String XSD = "http://www.w3.org/2001/XMLSchema#";

        /**
         * The DCTerms namespace http://purl.org/dc/terms/
         */
        public static final String DCTERMS = "http://purl.org/dc/terms/";

        /**
         * The RDFS namespace http://www.w3.org/2000/01/rdf-schema#
         */
        public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";

        /**
         * The ORE namespace http://www.openarchives.org/ore/terms/
         */
        public static final String ORE = "http://www.openarchives.org/ore/terms/";

        /**
         * The Data Conservancy OSF namespace http://www.dataconservancy.org/osf-business-object-model#
         */
        public static final String OSF = "http://www.dataconservancy.org/osf-business-object-model#";

        /**
         * A map of namespace prefixes to namespaces.
         */
        public static final Map<String, String> PREFIXES = new HashMap<String, String>() {
            {
                put("rdf", RDF);
                put("owl", OWL);
                put("xsd", XSD);
                put("dcterms", DCTERMS);
                put("rdfs", RDFS);
                put("ore", ORE);
                put("osf", OSF);
            }
        };

    }

}
