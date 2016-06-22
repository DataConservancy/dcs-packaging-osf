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
 * Created by esm on 6/5/16.
 */
public class OntTest {


    @Test
    public void testFoo() throws Exception {

        OntModel model = ModelFactory.createOntologyModel();

        Individual ind = model.createIndividual("someref", ResourceFactory.createResource());
        assertNotNull(ind);
        assertNotNull(model.getIndividual("someref"));
        assertSame(ind, model.getIndividual("someref"));

        Individual anonInd = model.createIndividual(ResourceFactory.createResource());
        assertNotNull(anonInd);
        // anonymous individuals have no uri
        assertNull(anonInd.getURI());
        // but they do have an AnonId
        assertNotNull(anonInd.getId());

        // sad-face: can't get an anonymous individual from the model
        assertNull(model.getIndividual(anonInd.getId().toString()));
        // but can get it as a resource
        Resource anonResource = model.getResource(anonInd.toString());
        assertNotNull(anonResource);

        // Try to retrieve the same Individual using the anonymous resource
        Individual fromResource = model.createIndividual(anonResource);
        assertNotNull(fromResource);

        // sad-face: they aren't the same
        assertNotSame(anonInd, fromResource);



    }
}

