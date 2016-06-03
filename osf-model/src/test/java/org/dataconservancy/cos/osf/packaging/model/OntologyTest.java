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
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by esm on 6/1/16.
 */
public class OntologyTest {

    private Ontology underTest = new Ontology();

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
        DatatypeProperty p = underTest.datatypeProperty(OldRdf.Ns.OSF, OldRdf.DatatypeProperty.OSF_HAS_NAME);
        assertNotNull(p);

        assertEquals(OldRdf.DatatypeProperty.OSF_HAS_NAME, p.getLocalName());
        assertEquals(OldRdf.Ns.OSF, p.getNameSpace());
    }

    @Test
    public void testGetObjectProperty() throws Exception {
        ObjectProperty p = underTest.objectProperty(OldRdf.Ns.OSF, OldRdf.ObjectProperty.OSF_HASNODE);
        assertNotNull(p);

        assertEquals(OldRdf.ObjectProperty.OSF_HASNODE, p.getLocalName());
        assertEquals(OldRdf.Ns.OSF, p.getNameSpace());
    }
}