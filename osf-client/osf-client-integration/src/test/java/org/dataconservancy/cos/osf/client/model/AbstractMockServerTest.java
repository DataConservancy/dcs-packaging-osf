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
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.io.IOUtils;
import org.dataconservancy.cos.osf.client.config.DefaultOsfJacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.DefaultWbJacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.JacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.OsfClientConfiguration;
import org.dataconservancy.cos.osf.client.config.WbClientConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.rules.TestName;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockserver.model.HttpCallback.callback;
import static org.mockserver.model.HttpRequest.request;

/**
 * Test fixture providing a {@link MockServerClient} used to configure HTTP expectations.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public abstract class AbstractMockServerTest extends AbstractOsfClientTest {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractMockServerTest.class);
    /**
     * Custom HTTP header sent by a test request, used to tell the {@code MockServer} what JSON document
     * to return.  The values are interpreted as classpath resources.
     */
    public static final String X_RESPONSE_RESOURCE = "X-Response-Resource";
    /**
     * Base path of the file system hierarchy containing JSON response documents
     */
    public static final String JSON_ROOT = "/json/";

    /**
     * Set by {@link #startMockServer()}.
     */
    static MockServerClient mockServer;

    /**
     * Set by {@link #startMockServer()}.
     */
    static MockServerClient wbMockServer;

    /**
     * Starts mock HTTP servers on the port specified by the OSF client configuration and the Waterbutler client
     * configuration
     */
    @Before
    public void startMockServer() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        final JacksonConfigurer<OsfClientConfiguration> osfConfigurer = new DefaultOsfJacksonConfigurer<>();
        final JacksonConfigurer<WbClientConfiguration> wbConfigurer = new DefaultWbJacksonConfigurer<>();

        final ResourceLoader loader = new DefaultResourceLoader();

        final Resource configuration = loader.getResource(getOsfServiceConfigurationResource());
        assertTrue("Unable to resolve configuration resource: '" + getOsfServiceConfigurationResource() + "'",
                configuration.exists());

// assertNotNull("Unable to resolve configuration resource: '" + getOsfServiceConfigurationResource() + "'",
// configuration);

        mockServer = ClientAndServer.startClientAndServer(
                osfConfigurer.configure(
                        mapper.readTree(IOUtils.toString(configuration.getURL(), "UTF-8")),
                        mapper,
                        OsfClientConfiguration.class
                ).getPort()
        );

        wbMockServer = ClientAndServer.startClientAndServer(
                wbConfigurer.configure(
                        mapper.readTree(IOUtils.toString(configuration.getURL(), "UTF-8")),
                        mapper,
                        WbClientConfiguration.class
                ).getPort()
        );

        /* Sets up the expectations of the mock http server.
         *
         * Invokes the NodeResponseCallback when the HTTP header "X-Response-Resource" is present.
         * The header value is a classpath resource to the JSON document to be serialized for the
         * response.
         */
        mockServer.when(
                request()
                        .withHeader(X_RESPONSE_RESOURCE)
        )
                .callback(
                        callback()
                                .withCallbackClass(NodeResponseCallback.class.getName())
                );
        wbMockServer.when(
                request()
                        .withHeader(X_RESPONSE_RESOURCE)
        )
                .callback(
                        callback()
                                .withCallbackClass(NodeResponseCallback.class.getName())
                );
    }

    /**
     * Stops mock HTTP servers
     */
    @After
    public void stopMockServer() throws Exception {
        mockServer.stop();
        wbMockServer.stop();
    }

    public static String resourceBase(final TestName testName) {
        assertNotNull(testName);
        final StringBuilder base = new StringBuilder(JSON_ROOT);
        base.append(NodeTest.class.getSimpleName()).append("/");
        base.append(testName.getMethodName()).append("/");

        LOG.trace("Test resource base path: {}", base);
        return base.toString();
    }

    public static String resourceBase(final TestName testName, final Class testClass) {
        assertNotNull(testName);
        final StringBuilder base = new StringBuilder(JSON_ROOT);
        base.append(testClass.getSimpleName()).append("/");
        base.append(testName.getMethodName()).append("/");

        LOG.trace("Test resource base path: {}", base);
        return base.toString();
    }

    /**
     * Adds an interceptor to the OSF service factory which adds a {@code X_RESPONSE_RESOURCE} header to the HTTP
     * request.  Test classes can use this instead of the {@link RecursiveInterceptor}/{@link ResponseResolver}
     * combination if the tests do not need to resolve relationships.
     *
     * @param resourcePath the resource to resolve and return in the response.
     */
    protected void addResponseInterceptor(final String resourcePath) {
        factory.interceptors().add(chain ->
                chain.proceed(chain.request()
                        .newBuilder()
                        .addHeader(X_RESPONSE_RESOURCE, resourcePath)
                        .build()));
    }

    /**
     * Responsible for resolving the JSON response resource for OSF v2 API calls that are recursive.
     * <p>
     * For example, the {@link com.github.jasminb.jsonapi.ResolutionStrategy} for {@code Node}
     * {@link Node#contributors contributors} says that when a {@code Node} is retrieved using the
     * {@link org.dataconservancy.cos.osf.client.service.OsfService}, the "contributors" relationship should be
     * recursively retrieved in the same API call, and deserialized into a {@code List} of {@code Contributor} objects.
     * Once the {@code Node} is retrieved, the caller can iterate over the {@code Contributor} objects without issuing
     * subsequent calls to {@link org.dataconservancy.cos.osf.client.service.OsfService}.  This behavior is governed by
     * the {@code ResolutionStrategy} annotations on the model classes.
     * </p>
     * <p>
     * This interceptor will map a response based on attributes of the request (and test name).  This insures that
     * {@link org.dataconservancy.cos.osf.client.service.OsfService} API calls which are recursive will result in proper
     * responses.
     * </p>
     */
    public static class RecursiveInterceptor implements Interceptor {

        private static final Logger LOG = LoggerFactory.getLogger(RecursiveInterceptor.class);

        private final URI baseUri;

        private final TestName testName;

        private final Class testClass;

        private ResponseResolver resolver;

        public RecursiveInterceptor(final TestName testName, final Class testClass, final URI baseUri) {
            this.baseUri = baseUri;
            this.testName = testName;
            this.testClass = testClass;

            resolver = (name, base, req) -> {
                // http://localhost:8000/v2/nodes/v8x57/files/osfstorage/ -> nodes/v8x57/files/osfstorage/
                final URI relativizedRequestUri = baseUri.relativize(req);
                final String requestPath = relativizedRequestUri.getPath();

                // /json/NodeTest/testGetNodeObjectResolution/
                final String fsBase = resourceBase(testName, testClass);

                // If there's a "page" query parameter, use it to return 'index-0?.json'
                if (req.getQuery() != null && req.getQuery().contains("page=")) {
                    final int startIndex = req.getQuery().indexOf("page=") + "page=".length();
                    // HACK unlikely to have double-digit pages
                    final int page = Integer.parseInt(req.getQuery().substring(startIndex, startIndex + 1));
                    final String jsonFile = String.format("index-0%s.json", page);
                    LOG.trace("Request carried 'page' parameter, using JSON resource {}", jsonFile);
                    return fsBase + requestPath + jsonFile;
                } else {
                    LOG.trace("  Request did not carry 'page' parameter.");
                }

                // If there is no "page" query parameter, and the request ends in a "/", and there is
                // no 'index.json' file, then see if there is an 'index-01.json' file, and return that.

                String jsonPath = fsBase + requestPath + "index.json";
                if (req.getPath().endsWith("/")) {
                    // /json/NodeTest/testGetNodeObjectResolution/nodes/v8x57/files/osfstorage/index.json
                    if (testClass.getResource(jsonPath) != null) {
                        return jsonPath;
                    } else {
                        LOG.debug("  JSON resource '{}' does not exist.", jsonPath);
                    }

                    // If there's no 'index.json' file, then perhaps the request is for a paginated response
                    jsonPath = fsBase + requestPath + "index-01.json";
                    if (testClass.getResource(jsonPath) != null) {
                        return jsonPath;
                    } else {
                        LOG.trace("  JSON resource '{}' does not exist.", jsonPath);
                        throw new IllegalArgumentException(
                                String.format("Unable to resolve request '%s' to a classpath resource '%s'",
                                        req, jsonPath));
                    }
                } else {
                    // This is binary content, e.g.: /v1/resources/vae86/providers/osfstorage/57570a07c7950c0045ac8051
                    jsonPath = fsBase + requestPath;
                    if (testClass.getResource(jsonPath) != null) {
                        return jsonPath;
                    } else {
                        LOG.debug("  JSON resource '{}' does not exist.", jsonPath);
                        throw new IllegalArgumentException(
                                String.format("Unable to resolve request '%s' to a classpath resource '%s'",
                                        req, jsonPath));
                    }
                }



            };
        }

        public RecursiveInterceptor(final TestName testName, final Class testClass, final URI baseUri,
                                    final ResponseResolver resolver) {
            this.testName = testName;
            this.testClass = testClass;
            this.baseUri = baseUri;
            this.resolver = resolver;
        }

        @Override
        public Response intercept(final Chain chain) throws IOException {
            Request req = chain.request();
            LOG.debug("HTTP request: {}", req.urlString());

            // Resolve the request URI to a path on the filesystem.
            final String resourcePath = resolver.resolve(testName, baseUri, req.uri());
            LOG.debug("Response resource: {}", resourcePath);

            req = req.newBuilder().addHeader(X_RESPONSE_RESOURCE, resourcePath).build();

            return chain.proceed(req);
        }
    }

    /**
     * Responsible for resolving the JSON response for OSF v2 API calls.
     */
    @FunctionalInterface
    public static interface ResponseResolver {

        /**
         * Resolves a JSON response document based on properties of the OSF API, the HTTP request, and the test being
         * executed.  This method should return a classpath resource containing the JSON for the HTTP response body.
         *
         * @param testName contains metadata about the current test method.
         * @param baseUri the baseUri of the OSF V2 API
         * @param requestUri the full request URI
         * @return a Path that identifies a classpath resource containing the JSON response document
         */
        String resolve(TestName testName, URI baseUri, URI requestUri);

    }
}
