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

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests behaviors of the Jena {@code OntModel}
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class OntTest {


    @Test
    public void testFoo() throws Exception {

        final OntModel model = ModelFactory.createOntologyModel();

        final Individual ind = model.createIndividual("someref", ResourceFactory.createResource());
        assertNotNull(ind);
        assertNotNull(model.getIndividual("someref"));
        assertSame(ind, model.getIndividual("someref"));

        final Individual anonInd = model.createIndividual(ResourceFactory.createResource());
        assertNotNull(anonInd);
        // anonymous individuals have no uri
        assertNull(anonInd.getURI());
        // but they do have an AnonId
        assertNotNull(anonInd.getId());

        // sad-face: can't get an anonymous individual from the model
        assertNull(model.getIndividual(anonInd.getId().toString()));
        // but can get it as a resource
        final Resource anonResource = model.getResource(anonInd.toString());
        assertNotNull(anonResource);

        // Try to retrieve the same Individual using the anonymous resource
        final Individual fromResource = model.createIndividual(anonResource);
        assertNotNull(fromResource);

        // sad-face: they aren't the same
        assertNotSame(anonInd, fromResource);



    }
}

