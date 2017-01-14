/*
 * Copyright 2017 Johns Hopkins University
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
package org.dataconservancy.cos.osf.client.model.mapping.v2_2.graphs;

import org.dataconservancy.cos.osf.client.model.Institution;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class InstitutionGraphMappingTest extends BaseGraphMappingTest {

    @Test
    public void testInstitutionRelationships() throws Exception {
        final Institution jhu = osfService.institution("http://localhost:8000/v2/institutions/jhu/").execute().body();

        // nodes
        assertEquals("http://localhost:8000/v2/institutions/jhu/nodes/", jhu.getNodes());

        // registrations
        assertEquals("http://localhost:8000/v2/institutions/jhu/registrations/", jhu.getRegistrations());

        // users
        assertEquals("http://localhost:8000/v2/institutions/jhu/users/", jhu.getUsers());
    }
}
