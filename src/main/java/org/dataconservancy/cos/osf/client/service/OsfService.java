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

import java.util.List;
import java.util.Map;

import com.github.jasminb.jsonapi.ResourceList;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.Event;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.RegistrationId;
import org.dataconservancy.cos.osf.client.model.User;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.http.Streaming;
import retrofit.http.Url;

/**
 * Created by esm on 4/25/16.
 * @author esm
 * @author khanson
 */
public interface OsfService {

    @GET("nodes/")
    Call<List<Node>> nodeList();

    @GET("nodes/")
    Call<ResourceList<Node>> paginatedNodeList();

    @GET
    Call<ResourceList<Node>> paginatedNodeList(@Url String url);

    @GET
    Call<ResourceList<Event>> getLogs(@Url String url);

    @Streaming
    @GET
    Call<ResponseBody> stream(@Url String url);

    @GET("nodes/")
    Call<List<Node>> nodeList(@QueryMap Map<String, String> params);

    @GET("nodes/")
    Call<List<Node>> nodeList(@Query("page") int page, @QueryMap Map<String, String> params);

    @GET("nodes/{id}/")
    Call<Node> node(@Path("id") String id);

    @GET("registrations/")
    Call<List<Registration>> registrationList();

    @GET("registrations/")
    Call<List<Registration>> registrationList(@QueryMap Map<String, String> params);

    @GET("registrations/")
    Call<List<Registration>> registrationList(@Query("page") int page, @QueryMap Map<String, String> params);

    @GET("registrations/{id}/")
    Call<Registration> registration(@Path("id") String id);
    
    @GET("registrations/")
    Call<List<RegistrationId>> registrationIdList();

    @GET("registrations/")
    Call<List<RegistrationId>> registrationIdList(@QueryMap Map<String, String> params);

    @GET("registrations/")
    Call<List<RegistrationId>> registrationIdList(@Query("page") int page, @QueryMap Map<String, String> params);
    
    @GET("users/")
    Call<List<User>> userList();

    @GET("users/")
    Call<List<User>> userList(@QueryMap Map<String, String> params);

    @GET("users/")
    Call<List<User>> userList(@Query("page") int page, @QueryMap Map<String, String> params);

    @GET("users/{id}/")
    Call<User> user(@Path("id") String id);

    @GET
    Call<List<Contributor>> contributors(@Url String url);
       
    //Call<List<NodeFile>> listFiles(@Path(""))
}
