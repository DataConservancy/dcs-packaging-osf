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

import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;
import com.squareup.okhttp.OkHttpClient;
import org.dataconservancy.cos.osf.client.config.OsfConfigurationService;
import retrofit.Retrofit;

/**
 * Factory which wires collaborating objects and produces a Retrofit service interface.
 */
public class RetrofitOsfServiceFactory {

    private static final String NOT_NULL_IAE = "%s must not be null.";

    /**
     * Configured OSF configuration service, provides access to the base URL of the OSF v2 API
     */
    private final OsfConfigurationService osfConfigSvc;

    /**
     * The OK HTTP client used by Retrofit for HTTP requests
     */
    private final OkHttpClient httpClient;

    /**
     * The JSON-API converter factory used to map JSON documents to Java objects.
     */
    private final JSONAPIConverterFactory jsonApiConverterFactory;

    /**
     * Constructs a new RetrofitOsfServiceFactory with all the required collaborators.
     *
     * @param osfConfigSvc the OSF configuration service
     * @param httpClient the OK HTTP Client
     * @param jsonApiConverterFactory the JSON API converter factory
     */
    public RetrofitOsfServiceFactory(OsfConfigurationService osfConfigSvc, OkHttpClient httpClient,
                                     JSONAPIConverterFactory jsonApiConverterFactory) {
        if (osfConfigSvc == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, "OsfConfigurationService"));
        }

        if (httpClient == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, "OkHttpClient"));
        }

        if (jsonApiConverterFactory == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, "JSONAPIConverterFactory"));
        }

        this.osfConfigSvc = osfConfigSvc;
        this.httpClient = httpClient;
        this.jsonApiConverterFactory = jsonApiConverterFactory;
    }

    /**
     * Answers a configured Retrofit-based OSF client that communicates with the OSF V2 API at the base URL obtained
     * from the OSF configuration service.  The JSON responses from the OSF V2 API will be mapped to Java objects
     * according to the JSON API converter factory.  HTTP requests and responses are sent via the OK HTTP client.
     *
     * @param osfService the Retrofit interface that contains the methods used by the developer to communicate with the
     *                   OSF API.
     * @param <T> the Retrofit interface type
     * @return a configured Retrofit interface, ready to service requests.
     */
    public <T> T getOsfService(Class<T> osfService) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(osfConfigSvc.getConfiguration().getBaseUri().toString())
                .addConverterFactory(jsonApiConverterFactory)
                .client(httpClient)
                .build();

        return retrofit.create(osfService);
    }

}
