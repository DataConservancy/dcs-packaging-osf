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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.FileVersion;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Test;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * Created by esm on 4/25/16.
 * @author esm
 * @author khanson
 */
public class TestClient {

    @Test
    public void testNodeApi() throws Exception {

//      final String auth = "";
//      final String auth = "Basic ZW1ldHNnZXJAZ21haWwuY29tOmZvb2JhcmJheg==";

        // Create object mapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Set serialisation/deserialisation options if needed (property naming strategy, etc...)

        OkHttpClient client = new OkHttpClient();
//      client.interceptors().add(new LoggingInterceptor());
//      client.interceptors().add(new AuthInterceptor(auth));

        ResourceConverter converter = new ResourceConverter(objectMapper, Node.class, File.class, 
        													FileVersion.class, Contributor.class, User.class);
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

        //HashMap<String, String> params = new HashMap<>();
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
            System.out.println("Node: id " + n.getId() + " category " + n.getCategory() + "; title " + n.getTitle() + "; public " + n.isPublic() + "; Rootpath: "  + n.getRoot());
            System.out.println("Links: " + n.getPageLinks());
        });

        //String nodeWithFilesId = "v8x57";  //temp test id
        String nodeWithFilesId = "fu8zc"; //temp test id
        Node withFiles = osfSvc.node(nodeWithFilesId).execute().body();
        assertNotNull(withFiles);
        assertNotNull(withFiles.getFiles());

        withFiles.getFiles().stream().forEach(provider -> System.err.println("Provider (" + provider.getNode() + "): " + provider.getProvider() + " path: " + provider.getPath()));

        File osfStorageProvider = withFiles.getFiles().get(0);
        File osfStorage = osfStorageProvider.getFiles().get(0);

        assertNotNull(osfStorage);
        assertNotNull(osfStorage.getFiles());
        osfStorage.getFiles().stream().forEach(file -> System.err.println("File (" + file.getName() + "): " + file.getProvider() 
        												+ " path: " + file.getPath()));

        List<Contributor> contributors = withFiles.getContributors();
        
        assertNotNull(contributors);
        assertTrue(contributors.size()>0);
        
        contributors.stream().forEach(contrib ->System.err.println("Contributor - " + contrib.getId() ));
       
        //users
        Call<List<User>> userListCall = osfSvc.userList();
        assertNotNull(userListCall);
        Response<List<User>> usr = userListCall.execute();
        assertNotNull(usr);
              
        String userId = "km4wh"; //temp test id
        User testUser = osfSvc.user(userId).execute().body();
        assertNotNull(testUser);
        assertNotNull(testUser.getFull_name());
        System.err.println(testUser.getFull_name());
    }

    @Test
    public void testRegistrationApi() throws Exception {

    	// Create object mapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Set serialisation/deserialisation options if needed (property naming strategy, etc...)

        OkHttpClient client = new OkHttpClient();

        ResourceConverter converter = new ResourceConverter(objectMapper, Registration.class, File.class, 
        													FileVersion.class, Contributor.class, User.class, Node.class);
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
        params.put("filter[public]", "true");
        Call<List<Registration>> listCall = osfSvc.registrationList(params);
        assertNotNull(listCall);
        Response<List<Registration>> res = listCall.execute();
        assertNotNull(res);
        
        List<Registration> registrations = null;
        if (!res.isSuccess()) {
            assertNotNull(res.errorBody());
            System.err.println(res.errorBody().string());
        } else {
        	registrations = res.body();
            assertNotNull(registrations);
            assertFalse(registrations.isEmpty());
        }

        assertNotNull(registrations);

        registrations.stream().forEach(n -> {
            System.out.println("Registrations: id " + n.getId() + " category " + n.getCategory() + "; title " + n.getTitle() + "; public " + n.isPublic() + "; Rootpath: "  + n.getRoot());
        });
               
        String registrationId = "v28mf"; //temp test id
        Registration testreg = osfSvc.registration(registrationId).execute().body();
        assertNotNull(testreg);
        assertNotNull(testreg.getChildren());
       testreg.getChildren().stream().forEach(child -> System.err.println("ChildReg (" + child.getId() + "): DateCreated: " + child.getDate_created() 
        										+ " root: " + child.getRoot() + " title:" + child.getTitle()));
               
    }

}
