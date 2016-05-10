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

import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.RegistrationId;
import org.dataconservancy.cos.osf.client.model.User;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by esm on 4/25/16.
 * @author esm
 * @author khanson
 */
public interface OsfService {

    @GET("nodes/")
    Call<List<Node>> nodeList();

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
       
    //Call<List<NodeFile>> listFiles(@Path(""))
}
