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

import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Before;

/**
 * A base test class that operates over a shared graph of OSF objects rooted under {@code
 * /model-mapping/2.2/graphs/shared/}
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class BaseGraphMappingTest extends AbstractMockServerTest {

    protected OsfService osfService;

    @Before
    public void setUp() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor("/model-mapping/2.2/graphs/shared/", this.getClass()));
        osfService = factory.getOsfService(OsfService.class);
    }

}
