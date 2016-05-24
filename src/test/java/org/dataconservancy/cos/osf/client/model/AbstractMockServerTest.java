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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.dataconservancy.cos.osf.client.config.BaseConfigurationService;
import org.dataconservancy.cos.osf.client.config.DefaultOsfJacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.DefaultWbJacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.JacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.OsfClientConfiguration;
import org.dataconservancy.cos.osf.client.config.WbClientConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;

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
     * Set by {@link #startMockServer()}.
     */
    static MockServerClient mockServer;

    /**
     * Set by {@link #startMockServer()}.
     */
    static MockServerClient wbMockServer;

    /**
     * Starts mock HTTP servers on the port specified by the OSF client configuration and the Waterbutler client configuration
     */
    @BeforeClass
    public static void startMockServer() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        JacksonConfigurer<OsfClientConfiguration> osfConfigurer = new DefaultOsfJacksonConfigurer<>();
        JacksonConfigurer<WbClientConfiguration> wbConfigurer = new DefaultWbJacksonConfigurer<>();

        mockServer = ClientAndServer.startClientAndServer(
                osfConfigurer.configure(
                        mapper.readTree(IOUtils.toString(BaseConfigurationService.getConfigurationResource("osf-client-local.json"), "UTF-8")),
                        mapper,
                        OsfClientConfiguration.class
                ).getPort()
        );

        wbMockServer = ClientAndServer.startClientAndServer(
                wbConfigurer.configure(
                        mapper.readTree(IOUtils.toString(BaseConfigurationService.getConfigurationResource("osf-client-local.json"), "UTF-8")),
                        mapper,
                        WbClientConfiguration.class
                ).getPort()
        );
    }

    /**
     * Stops mock HTTP servers
     */
    @AfterClass
    public static void stopMockServer() throws Exception {
        mockServer.stop();
        wbMockServer.stop();
    }
}
