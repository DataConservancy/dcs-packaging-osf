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
package org.dataconservancy.cos.osf.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.annotations.Type;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.dataconservancy.cos.osf.client.config.JacksonOsfConfigurationService;
import org.dataconservancy.cos.osf.client.config.OsfConfigurationService;
import org.dataconservancy.cos.osf.client.support.AuthInterceptor;
import org.dataconservancy.cos.osf.client.support.LoggingInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory used to produce clients for testing the OSF API endpoint.  Specifically, this factory uses default
 * implementations for collaborating objects like the HTTP client and OSF Configuration service.  These defaults reduce
 * the boilerplate for tests (and they are the only implementations available).
 * <p>
 * Importantly, this factory exposes the {@link OkHttpClient#interceptors() interceptors} that are used by the HTTP
 * client.  This allows a test class to configure an interceptor and add it to the chain.  By default, this factory
 * configures an instance of {@link AuthInterceptor} on the client.
 * </p>
 * <p>
 * Sample usage:
 * <pre>
 * // create an instance of the factory, referencing a configuration for the client formatted as JSON
 * TestingOsfServiceFactory factory = new TestingOsfServiceFactory("osf-client-local.json");
 *
 * // configure any interceptors on the client (e.g. to mutate HTTP headers)
 * factory.interceptors().add((chain) -> chain.proceed(
 *   chain.request().newBuilder().addHeader(X_RESPONSE_RESOURCE, "project-node-only-ref-rels.json").build()
 * ));
 *
 * // Obtain an instance of the client and execute a request.
 * OsfService svc = factory.getOsfService(OsfService.class)
 * Node n = svc.node("v8x57").execute().body();
 * </pre>
 * </p>
 */
public class TestingOsfServiceFactory {

    private final RetrofitOsfServiceFactory factory;

    private final OkHttpClient httpClient;

    private final OsfConfigurationService osfConfigurationService;

    /**
     * Creates a new RetrofitOsfServiceFactory with default implementations for the required collaborators.  Not
     * recommended for production.
     *
     * @param jsonConfigurationResource a classpath resource containing the configuration for the OSF API; must be JSON
     */
    public TestingOsfServiceFactory(String jsonConfigurationResource) {
        // Configure the configuration service.
       osfConfigurationService = new JacksonOsfConfigurationService(jsonConfigurationResource);

        // Wiring for the RetrofitOsfService Factory

        // ... the OK HTTP client used by Retrofit to make calls
        httpClient = new OkHttpClient();
        httpClient.interceptors().add(new AuthInterceptor(osfConfigurationService.getConfiguration().getAuthHeader()));

        // ... the JSON-API converter used by Retrofit to map JSON documents to Java objects
        List<Class<?>> domainClasses = new ArrayList<>();

        new FastClasspathScanner("org.dataconservancy.cos.osf.client.model")
                .matchClassesWithAnnotation(Type.class, domainClasses::add)
                .scan();

        ResourceConverter resourceConverter = new ResourceConverter(new ObjectMapper(),
                domainClasses.toArray(new Class[]{}));

        resourceConverter.setGlobalResolver(relUrl -> {
            com.squareup.okhttp.Call req = httpClient.newCall(new Request.Builder().url(relUrl).build());
            try {
                return req.execute().body().bytes();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });

        JSONAPIConverterFactory jsonApiConverterFactory = new JSONAPIConverterFactory(resourceConverter);

        factory = new RetrofitOsfServiceFactory(osfConfigurationService, httpClient, jsonApiConverterFactory);
    }

    /**
     * Expose a mutable list of {@link Interceptor}s used by the HTTP client to mutate requests and responses.  Clients
     * may wish to mutate this list to add custom interceptors (useful for adding headers, etc.).
     *
     * @return a mutable list of {@code Interceptor}s
     */
    public List<Interceptor> interceptors() {
        return httpClient.interceptors();
    }

    /**
     * Expose the {@link OsfConfigurationService} used by this factory.
     *
     * @return the configuration service
     */
    public OsfConfigurationService getConfigurationService() {
        return osfConfigurationService;
    }

    /**
     * Answers a configured Retrofit-based OSF client that communicates with the OSF V2 API at the base URL obtained
     * from the OSF configuration service.  The JSON responses from the OSF V2 API will be mapped to Java objects
     * according to the JSON API converter factory.  HTTP requests and responses are sent via the OK HTTP client.
     *
     * @param osfService the Retrofit interface that contains the methods used by the developer to communicate with the
     *                   OSF API.
     * @param <T>        the Retrofit interface type
     * @return a configured Retrofit interface, ready to service requests.
     */
    public <T> T getOsfService(Class<T> osfService) {
        return factory.getOsfService(osfService);
    }
}
