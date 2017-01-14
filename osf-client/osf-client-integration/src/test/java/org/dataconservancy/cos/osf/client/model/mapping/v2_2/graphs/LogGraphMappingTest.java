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

import org.dataconservancy.cos.osf.client.model.Log;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class LogGraphMappingTest extends BaseGraphMappingTest {

    @Test
    public void testLogRelationships() throws Exception {
        // We don't have the individual log json at /v2/logs/579b8a35594d9001fa2fccfb, so we access it through the
        // registration
        final Registration registration = osfService.registrationByUrl("http://localhost:8000/v2/registrations/tgzhk/")
                .execute().body();
        final Log log = osfService.getLogs(registration.getLogs()).execute().body()
                .stream()
                .filter(l -> l.getId().equals("579b8a35594d9001fa2fccfb"))
                .findFirst().orElseThrow(() ->
                        new RuntimeException("Missing expected log entry '579b8a35594d9001fa2fccfb"));

        // node
        assertEquals("http://localhost:8000/v2/registrations/tgzhk/", log.getNode());

        // original_node
        assertEquals("http://localhost:8000/v2/registrations/tgzhk/", log.getOriginal_node());

        // user
        assertEquals("http://localhost:8000/v2/users/gb6f3/", log.getUser());
    }


}
