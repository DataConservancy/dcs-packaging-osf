/*
 *
 *  * Copyright 2017 Johns Hopkins University
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.dataconservancy.cos.osf.client.model.mapping.v2_2.graphs;

import org.dataconservancy.cos.osf.client.model.Identifier;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class IdentifierGraphMappingTest extends BaseGraphMappingTest {

    /**
     * Ability to retrieve an Identifier and its referent relationship.
     *
     * @throws Exception
     */
    @Test
    public void testIdentifierRelationships() throws Exception {
        final Identifier id = osfService.identifier("http://localhost:8000/v2/identifiers/564366998c5e4a74a2e66147/")
                .execute().body();
        assertEquals("https://localhost:8000/v2/registrations/nmbwq/", id.getReferent());
    }

}
