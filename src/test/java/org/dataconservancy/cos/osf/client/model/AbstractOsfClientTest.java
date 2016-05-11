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

import org.dataconservancy.cos.osf.client.config.OsfClientConfiguration;
import org.dataconservancy.cos.osf.client.service.TestingOsfServiceFactory;
import org.junit.Before;

import java.net.URI;

/**
 * Test fixture providing access to an {@link TestingOsfServiceFactory} and its configuration, to be used by tests
 * when accessing real or mocked instances of the OSF v2 API.
 */
public abstract class AbstractOsfClientTest {

    /**
     * The default configuration of the OSF client, represented as a classpath resource.
     */
    protected static final String DEFAULT_OSF_CONFIGURATION_RESOURCE = "osf-client-local.json";

    /**
     * Instance of the {@link TestingOsfServiceFactory} provided by this test fixture
     */
    protected TestingOsfServiceFactory factory;

    /**
     * Configures the {@link #factory instance} of the {@link TestingOsfServiceFactory}.  By default the
     * factory is configured by the classpath resource {@code osf-client-local.json}.
     */
    public AbstractOsfClientTest() {
        factory = new TestingOsfServiceFactory(getOsfServiceConfigurationResource());
    }

    /**
     * Answers a String representing the classpath resource of the OSF client configuration JSON document.
     * <p>
     * By default this returns {@link #DEFAULT_OSF_CONFIGURATION_RESOURCE}
     * </p>
     *
     * @return the classpath resource used to configure the OSF client
     */
    protected String getOsfServiceConfigurationResource() {
        return DEFAULT_OSF_CONFIGURATION_RESOURCE;
    }

    /**
     * The base URI of the OSF v2 API; equivalent to {@code getConfiguration().getBaseUri()}
     *
     * @return the base URI of the OSF v2 API, e.g. 'http://localhost:8000/v2/'
     */
    protected URI getBaseUri() {
        return getConfiguration().getBaseUri();
    }

    /**
     * The {@code OsfClientConfiguration} encapsulating the parameters to be used by the test fixtures
     *
     * @return the {@code OsfClientConfiguration}
     */
    protected OsfClientConfiguration getConfiguration() {
        return factory.getConfigurationService().getConfiguration();
    }

}
