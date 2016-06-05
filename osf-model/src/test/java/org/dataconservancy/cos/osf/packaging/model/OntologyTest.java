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
package org.dataconservancy.cos.osf.packaging.model;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by esm on 6/1/16.
 */
public class OntologyTest {

    private OntologyManager underTest = new OntologyManager();

    @Test(expected = IllegalArgumentException.class)
    public void testGetNonExistentDatatypeProperty() throws Exception {
        underTest.datatypeProperty("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNonExistentObjectProperty() throws Exception {
        underTest.objectProperty("foo");
    }

    @Test
    public void testGetDatatypeProperty() throws Exception {
        // Create a data type property 'osf:hasName' in the underlying ontology model
        DatatypeProperty p = underTest.datatypeProperty(OwlProperties.OSF_HAS_NAME.fqname());
        assertNotNull(p);

        // Insure the newly created datatype property has the expected local name and namespace
        assertEquals(OwlProperties.OSF_HAS_NAME.localname(), p.getLocalName());
        assertEquals(OwlProperties.OSF_HAS_NAME.ns(), p.getNameSpace());
    }

    @Test
    public void testGetObjectProperty() throws Exception {
        // Create a object type property 'osf:hasRoot' in the underlying ontology model
        ObjectProperty p = underTest.objectProperty(OwlProperties.OSF_HAS_ROOT.fqname());
        assertNotNull(p);

        // Insure the newly created object property has the expected local name and namespace
        assertEquals(OwlProperties.OSF_HAS_ROOT.localname(), p.getLocalName());
        assertEquals(OwlProperties.OSF_HAS_ROOT.ns(), p.getNameSpace());
    }
}