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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.jasminb.jsonapi.ResourceList;
import org.apache.commons.io.IOUtils;
import org.dataconservancy.cos.osf.client.config.OsfClientConfiguration;
import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.FileVersion;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.RegistrationId;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.client.service.TestingOsfServiceFactory;
import org.junit.Before;
import org.junit.Ignore;
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
    public void testNodeApi() throws Exception {

        final OsfService osfSvc = osfServiceFactory.getOsfService(OsfService.class);

        //HashMap<String, String> params = new HashMap<>();
//        params.put("filter[public]", "true");
        final Call<List<Node>> listCall = osfSvc.nodeList();
        assertNotNull(listCall);
        final Response<List<Node>> res = listCall.execute();
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
            System.out.println("Node: id " + n.getId() + " category " + n.getCategory() + "; title " + n.getTitle() +
                    "; public " + n.isPublic() + "; Rootpath: " + n.getRoot());
            System.out.println("Links: " + n.getPageLinks());
        });

        //String nodeWithFilesId = "v8x57";  //temp test id
        final String nodeWithFilesId = "fu8zc"; //temp test id
        final Node withFiles = osfSvc.node(nodeWithFilesId).execute().body();
        assertNotNull(withFiles);
        assertNotNull(withFiles.getFiles());

        withFiles.getFiles().stream().forEach(provider -> System.err.println("Provider (" + provider.getNode() + "): "
                + provider.getProvider() + " path: " + provider.getPath()));

        final File osfStorageProvider = withFiles.getFiles().get(0);
        final File osfStorage = osfStorageProvider.getFiles().get(0);

        assertNotNull(osfStorage);
        assertNotNull(osfStorage.getFiles());
        osfStorage.getFiles().stream().forEach(file -> System.err.println("File (" + file.getName() + "): " +
                file.getProvider()
                + " path: " + file.getPath()));

        final List<Contributor> contributors = withFiles.getContributors();

        assertNotNull(contributors);
        assertTrue(contributors.size() > 0);

        contributors.stream().forEach(contrib -> System.err.println("Contributor - " + contrib.getId()));

        //users
        final Call<List<User>> userListCall = osfSvc.userList();
        assertNotNull(userListCall);
        final Response<List<User>> usr = userListCall.execute();
        assertNotNull(usr);

        final String userId = "km4wh"; //temp test id
        final User testUser = osfSvc.user(userId).execute().body();
        assertNotNull(testUser);
        assertNotNull(testUser.getFull_name());
        System.err.println(testUser.getFull_name());
    }

    @Test
    public void testPagination() throws Exception {
        osfServiceFactory.interceptors().add(chain -> {
            System.out.println("Requesting: " + chain.request().urlString());
            return chain.proceed(chain.request());
        });
        final OsfService osfSvc = osfServiceFactory.getOsfService(OsfService.class);
        final ResourceList<Node> nodesPageOne = osfSvc.paginatedNodeList().execute().body();
        assertNotNull(nodesPageOne);
        assertEquals(10, nodesPageOne.size());
        assertNotNull(nodesPageOne.getNext());

        final ResourceList<Node> nodesPageTwo = osfSvc.paginatedNodeList(nodesPageOne.getNext()).execute().body();
        assertNotNull(nodesPageTwo);
        assertEquals(3, nodesPageTwo.size());
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
    public void testRegistrationApi() throws Exception {

        // Create object mapper
        final ObjectMapper objectMapper = new ObjectMapper();
        final OkHttpClient client = new OkHttpClient();

        final ResourceConverter converter = new ResourceConverter(objectMapper, Registration.class, File.class,
                                                            FileVersion.class, Contributor.class, User.class,
                                                            Node.class);
        converter.setGlobalResolver(relUrl -> {
            System.err.println("Resolving " + relUrl);
            final com.squareup.okhttp.Call req = client.newCall(new Request.Builder().url(relUrl).build());
            try {
                final byte[] bytes = req.execute().body().bytes();
                System.err.println(IOUtils.toString(bytes, "UTF-8"));
                return bytes;
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
        final JSONAPIConverterFactory converterFactory = new JSONAPIConverterFactory(converter);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl.toString())
                .addConverterFactory(converterFactory)
                .client(client)
                .build();

        final OsfService osfSvc = retrofit.create(OsfService.class);

        final HashMap<String, String> params = new HashMap<>();
        params.put("filter[public]", "true");
        final Call<List<Registration>> listCall = osfSvc.registrationList(params);
        assertNotNull(listCall);
        final Response<List<Registration>> res = listCall.execute();
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
            System.out.println("Registrations: id " + n.getId() + " category " + n.getCategory() + "; title " +
                    n.getTitle() + "; public " + n.isPublic() + "; Rootpath: "  + n.getRoot());
        });

        final String registrationId = "v28mf"; //temp test id
        final Registration testreg = osfSvc.registration(registrationId).execute().body();
        assertNotNull(testreg);
        assertNotNull(testreg.getChildren());

        testreg.getChildren().stream().forEach(child -> System.err.println("ChildReg (" + child.getId() + "): " +
                "DateCreated: " + child.getDate_created() + " root: " + child.getRoot() + " title:" +
                child.getTitle()));

        final Map<String,?> links = testreg.getLinks();

        assertNotNull(links);
        assertTrue(links.size() > 0);

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
        final Call<List<RegistrationId>> listCall = osfSvc.registrationIdList(params);
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
