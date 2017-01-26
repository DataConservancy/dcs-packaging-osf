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

package org.dataconservancy.cos.osf.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;
import com.squareup.okhttp.OkHttpClient;
import org.apache.commons.io.IOUtils;
import org.dataconservancy.cos.osf.client.config.OsfClientConfiguration;
import org.dataconservancy.cos.osf.client.model.RegistrationId;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.client.service.TestingOsfServiceFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by esm on 4/25/16.
 * @author esm
 * @author khanson
 */
@Ignore("Regularly run unit tests belong in NodeTest, or a similar class.  This class assumes too much about the local developer environment to be useful for systematic unit testing.")
public class TestClient {

    /**
     * Encapsulation of the OSF client configuration, including the base url of the API and authentication
     * parameters.  Configured by the classpath resource {@code osf-client-test.json}.
     */
    private OsfClientConfiguration config;

    /**
     * The base URL of the v2 API, especially useful for relativizing URLs
     */
    private URI baseUrl;

    /**
     * Provides instances of the OsfService, or any Retrofit-compatible interface.
     */
    private TestingOsfServiceFactory osfServiceFactory;

    @Before
    public void setUp() throws Exception {

        osfServiceFactory = new TestingOsfServiceFactory("osf-client-test.json");

        // The base URL of the v2 api
        config = osfServiceFactory.getConfigurationService().getConfiguration();
        assertNotNull(config);
        baseUrl = config.getBaseUri();
        assertNotNull(baseUrl);
    }

    @Test
    public void testDownload() throws Exception {
        final String binaryUrl = "http://192.168.99.100:7777/v1/resources/ts6h8/providers/osfstorage/" +
                "57435501d3a5520045b18098";
        final OsfService osfSvc = osfServiceFactory.getOsfService(OsfService.class);

        final byte[] content = IOUtils.toByteArray(osfSvc.stream(binaryUrl).execute().body().byteStream());

        assertNotNull(content);
        assertEquals(1514, content.length);
    }

    @Test
    public void testRegistrationIdList() throws Exception {

        // Create object mapper
        final ObjectMapper objectMapper = new ObjectMapper();
        final OkHttpClient client = new OkHttpClient();

        final ResourceConverter converter = new ResourceConverter(objectMapper, RegistrationId.class);

        final JSONAPIConverterFactory converterFactory = new JSONAPIConverterFactory(converter);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl.toString())
                .addConverterFactory(converterFactory)
                .client(client)
                .build();

        final OsfService osfSvc = retrofit.create(OsfService.class);

        final HashMap<String, String> params = new HashMap<>();
        params.put("filter[public]", "true");
        final Call<List<RegistrationId>> listCall = osfSvc.registrationIds(params);
        assertNotNull(listCall);
        final Response<List<RegistrationId>> res = listCall.execute();
        assertNotNull(res);

        List<RegistrationId> registrations = null;
        if (!res.isSuccess()) {
            assertNotNull(res.errorBody());
            System.err.println(res.errorBody().string());
        } else {
            registrations = res.body();
        }

        assertNotNull(registrations);
        assertTrue (registrations.size() > 0);

        registrations.stream().forEach(n -> {
            System.out.println("Registrations: id " + n.getId());
        });

    }

}
