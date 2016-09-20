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
package org.dataconservancy.cos.osf.packaging;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dataconservancy.cos.osf.packaging.support.OntologyManager;
import org.dataconservancy.cos.rdf.support.OwlProperties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by esm on 9/20/16.
 */
class RdfTestUtil {

    private final OntologyManager ontologyManager;

    RdfTestUtil(OntologyManager ontologyManager) {
        this.ontologyManager = ontologyManager;
    }

    /**
     * Asserts that the supplied individual carries the specified OWL ObjectProperty, with the expected value.
     *
     * @param individual
     * @param expectedProperty
     * @param expectedValue
     */
    void assertHasPropertyWithValue(Individual individual, OwlProperties expectedProperty, Resource expectedValue) {
        if (expectedProperty.object()) {
            assertEquals(expectedValue, individual.getPropertyResourceValue(ontologyManager.objectProperty(expectedProperty.fqname())));
        } else {
            fail("Cannot assert a Resource value for a Datatype property.");
        }
    }

    /**
     * Asserts that the supplied individual carries the specified OWL Datatype property, with the expected value.
     *
     * @param individual
     * @param expectedProperty
     * @param expectedValue
     */
    void assertHasPropertyWithValue(Individual individual, OwlProperties expectedProperty, String expectedValue) {
        if (!expectedProperty.object()) {
            assertEquals(expectedValue, individual.getPropertyValue(ontologyManager.datatypeProperty(expectedProperty.fqname())).toString());
        } else {
            fail("Cannot assert a Literal value for a object property.");
        }
    }

    /**
     * Asserts that the supplied individual carries the specified OWL Datatype property, with the expected value and type.
     *
     * @param individual
     * @param expectedProperty
     * @param expectedValue
     * @param expectedType
     */
    void assertHasTypedLiteralWithValue(Individual individual, OwlProperties expectedProperty, String expectedValue, XSDDatatype expectedType) {
        if (!expectedProperty.object()) {
            Literal expectedLiteral = ResourceFactory.createTypedLiteral(expectedValue, expectedType);
            assertEquals(expectedLiteral, individual.getPropertyValue(ontologyManager.datatypeProperty(expectedProperty.fqname())));
        } else {
            fail("Cannot assert a Literal value for a object property.");
        }
    }

    Property asProperty(OwlProperties property) {
        if (property.object()) {
            return ontologyManager.objectProperty(property.fqname());
        }

        return ontologyManager.datatypeProperty(property.fqname());
    }

}
