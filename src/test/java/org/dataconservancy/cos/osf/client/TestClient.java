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
import com.squareup.okhttp.Request;
import org.apache.commons.io.IOUtils;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.client.support.AuthInterceptor;
import org.junit.Test;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by esm on 4/25/16.
 */
public class TestClient {

    @Test
    public void testFoo() throws Exception {

        final String auth = "Basic ZW1ldHNnZXJAZ21haWwuY29tOmZvb2JhcmJheg==";

        // Create object mapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Set serialisation/deserialisation options if needed (property naming strategy, etc...)

        OkHttpClient client = new OkHttpClient();
//        client.interceptors().add(new LoggingInterceptor());
        client.interceptors().add(new AuthInterceptor(auth));

        ResourceConverter converter = new ResourceConverter(objectMapper, Node.class);
        converter.setGlobalResolver(relUrl -> {
            System.err.println("Resolving " + relUrl);
            com.squareup.okhttp.Call req = client.newCall(new Request.Builder().url(relUrl).build());
            try {
                byte[] bytes = req.execute().body().bytes();
                System.err.println(IOUtils.toString(bytes, "UTF-8"));
                return bytes;
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
        JSONAPIConverterFactory converterFactory = new JSONAPIConverterFactory(converter);



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.99.100:8000/v2/")
                .addConverterFactory(converterFactory)
                .client(client)
                .build();

        OsfService osfSvc = retrofit.create(OsfService.class);

        HashMap<String, String> params = new HashMap<>();
//        params.put("filter[public]", "true");
        Call<List<Node>> listCall = osfSvc.nodeList();
        assertNotNull(listCall);
        Response<List<Node>> res = listCall.execute();
        assertNotNull(res);

        List<Node> nodes = null;
        if (!res.isSuccess()) {
            assertNotNull(res.errorBody());
            System.err.println(res.errorBody().string());
        } else {
            nodes = res.body();
            assertNotNull(nodes);
            assertFalse(nodes.isEmpty());
        }

        assertNotNull(nodes);

        nodes.stream().forEach(n -> {
            System.out.println("Node: id " + n.getId() + " category " + n.getCategory() + " title " + n.getTitle() + " public " + n.isPublic() + " root " + n.getRoot());
            System.out.println("Links: " + n.getLinks());
        });


        String nodeWithFilesId = "356n8";
        Node withFiles = osfSvc.node(nodeWithFilesId).execute().body();
        assertNotNull(withFiles);
        assertNotNull(withFiles.getFiles());

        withFiles.getFiles().stream().forEach(provider -> System.err.println("Provider: " + provider.getProvider()));


    }
}
