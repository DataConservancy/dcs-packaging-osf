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
import com.squareup.okhttp.ResponseBody;
import org.dataconservancy.cos.osf.client.model.Comment;
import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.Log;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.Institution;
import org.dataconservancy.cos.osf.client.model.License;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.RegistrationId;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.model.Wiki;

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

    /**
     *
     * @return
     */
    @GET("nodes/")
    Call<List<Node>> nodeList();

    /**
     *
     * @return
     */
    @GET("nodes/")
    Call<ResourceList<Node>> paginatedNodeList();

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<ResourceList<Node>> paginatedNodeList(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<ResourceList<Log>> getLogs(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<ResourceList<Comment>> getComments(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<Comment> comment(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @Streaming
    @GET
    Call<ResponseBody> stream(@Url String url);

    /**
     *
     * @param params
     * @return
     */
    @GET("nodes/")
    Call<List<Node>> nodeList(@QueryMap Map<String, String> params);

    /**
     *
     * @param page
     * @param params
     * @return
     */
    @GET("nodes/")
    Call<List<Node>> nodeList(@Query("page") int page, @QueryMap Map<String, String> params);

    /**
     *
     * @param id
     * @return
     */
    @GET("nodes/{id}/")
    Call<Node> node(@Path("id") String id);

    /**
     *
     * @param nodeUrl
     * @return
     */
    @GET
    Call<Node> nodeByUrl(@Url String nodeUrl);

    /**
     *
     * @return
     */
    @GET("registrations/")
    Call<List<Registration>> registrationList();

    /**
     *
     * @param params
     * @return
     */
    @GET("registrations/")
    Call<List<Registration>> registrationList(@QueryMap Map<String, String> params);

    /**
     *
     * @param page
     * @param params
     * @return
     */
    @GET("registrations/")
    Call<List<Registration>> registrationList(@Query("page") int page, @QueryMap Map<String, String> params);

    /**
     *
     * @param id
     * @return
     */
    @GET("registrations/{id}/")
    Call<Registration> registration(@Path("id") String id);

    /**
     *
     * @param registrationUrl
     * @return
     */
    @GET
    Call<Registration> registrationByUrl(@Url String registrationUrl);

    /**
     *
     * @return
     */
    @GET("registrations/")
    Call<List<RegistrationId>> registrationIdList();

    /**
     *
     * @param params
     * @return
     */
    @GET("registrations/")
    Call<List<RegistrationId>> registrationIdList(@QueryMap Map<String, String> params);

    /**
     *
     * @param page
     * @param params
     * @return
     */
    @GET("registrations/")
    Call<List<RegistrationId>> registrationIdList(@Query("page") int page, @QueryMap Map<String, String> params);

    /**
     *
     * @return
     */
    @GET("users/")
    Call<List<User>> userList();

    /**
     *
     * @param params
     * @return
     */
    @GET("users/")
    Call<List<User>> userList(@QueryMap Map<String, String> params);

    /**
     *
     * @param page
     * @param params
     * @return
     */
    @GET("users/")
    Call<List<User>> userList(@Query("page") int page, @QueryMap Map<String, String> params);

    /**
     *
     * @param id
     * @return
     */
    @GET("users/{id}/")
    Call<User> user(@Path("id") String id);

    /**
     *
     * @param userUrl
     * @return
     */
    @GET
    Call<User> userByUrl(@Url String userUrl);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<Contributor>> contributors(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<Contributor> contributor(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<Wiki>> wikis(@Url String url);

    /**
     *
     * @param licenseUrl
     * @return
     */
    @GET
    Call<License> license(@Url String licenseUrl);

    /**
     *
     * @param institutionUrl
     * @return
     */
    @GET
    Call<Institution> institution(@Url String institutionUrl);

    /**
     *
     * @param fileUrl
     * @return
     */
    @GET
    Call<File> file(@Url String fileUrl);

    /**
     *
     * @param logUrl
     * @return
     */
    @GET
    Call<Log> log(@Url String logUrl);



}
