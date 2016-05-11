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
package org.dataconservancy.cos.osf.client.model;

import org.junit.Rule;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

/**
 * Test fixture providing a {@link MockServerClient} used to configure HTTP expectations.
 */
public abstract class AbstractMockServerTest extends AbstractOsfClientTest {

    /**
     * Custom HTTP header sent by a test request, used to tell the {@code MockServer} what JSON document
     * to return.  The values are interpreted as classpath resources.
     */
    protected static final String X_RESPONSE_RESOURCE = "X-Response-Resource";

    /**
     * Starts a mock HTTP server on the port specified by the OSF client configuration
     */
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, false, factory.getConfigurationService()
                                                                                    .getConfiguration()
                                                                                        .getPort());

}
