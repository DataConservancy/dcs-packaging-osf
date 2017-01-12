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
package org.dataconservancy.cos.osf.client.model;

import org.junit.Test;

import java.net.URI;

import static org.dataconservancy.cos.osf.client.model.AbstractMockServerTest.resourcePathFrom;
import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class AbstractMockServerTestTest {

    @Test
    public void testHttpUriToPath() throws Exception {
        assertEquals("localhost/8080/v2/nodes/abc123",
                resourcePathFrom(URI.create("http://localhost:8080/v2/nodes/abc123/")));

        assertEquals("localhost/v2/nodes/abc123",
                resourcePathFrom(URI.create("http://localhost/v2/nodes/abc123/")));

        assertEquals("localhost/v2/nodes/abc123/foo.txt",
                resourcePathFrom(URI.create("http://localhost/v2/nodes/abc123/foo.txt")));
    }
}