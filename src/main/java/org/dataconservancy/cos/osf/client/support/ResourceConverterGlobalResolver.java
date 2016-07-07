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
package org.dataconservancy.cos.osf.client.support;

import com.github.jasminb.jsonapi.RelationshipResolver;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;

/**
 * Default {@code RelationshipResolver} used by the JSON-API Converter to retrieve the contents of JSON-API
 * relationships.
 */
public class ResourceConverterGlobalResolver implements RelationshipResolver {

    private OkHttpClient httpClient;

    /**
     * Constructs a new resolver using the supplied {@code httpClient} to retrieve relationship urls.
     *
     * @param httpClient the configured http client
     */
    public ResourceConverterGlobalResolver(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public byte[] resolve(String relationshipURL) {
        com.squareup.okhttp.Call req = httpClient.newCall(new Request.Builder().url(relationshipURL).build());
        try {
            return req.execute().body().bytes();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
