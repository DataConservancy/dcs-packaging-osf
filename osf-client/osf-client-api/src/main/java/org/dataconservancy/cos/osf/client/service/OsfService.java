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

import com.squareup.okhttp.ResponseBody;
import org.dataconservancy.cos.osf.client.model.Comment;
import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.FileVersion;
import org.dataconservancy.cos.osf.client.model.Log;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.Institution;
import org.dataconservancy.cos.osf.client.model.License;
import org.dataconservancy.cos.osf.client.model.MetaSchema;
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
     * @param logsUrl
     * @return
     */
    @GET
    Call<List<Log>> logs(@Url String logsUrl);

    /**
     *
     * @param commentsUrl
     * @return
     */
    @GET
    Call<List<Comment>> getComments(@Url String commentsUrl);

    /**
     *
     * @param commentUrl
     * @return
     */
    @GET
    Call<Comment> comment(@Url String commentUrl);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<Comment>> comments(@Url String url);

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
     * @param nodeId
     * @return
     */
    @GET("nodes/{id}/")
    Call<Node> nodeById(@Path("id") String nodeId);

    /**
     *
     * @param nodeUrl
     * @return
     */
    @GET
    Call<Node> node(@Url String nodeUrl);

    /**
     *
     * @param nodesUrl
     * @return
     */
    @GET
    Call<List<Node>> nodes(@Url String nodesUrl);

    /**
     *
     * @param id
     * @return
     */
    @GET("registrations/{id}/")
    Call<Registration> registrationById(@Path("id") String id);

    /**
     *
     * @param registrationUrl
     * @return
     */
    @GET
    Call<Registration> registration(@Url String registrationUrl);

    /**
     *
     * @param registrationsUrl
     * @return
     */
    @GET
    Call<List<Registration>> registrations(@Url String registrationsUrl);

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
    @GET
    Call<List<User>> users(@Url String usersUrl);

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
    Call<User> userById(@Path("id") String id);

    /**
     *
     * @param userUrl
     * @return
     */
    @GET
    Call<User> user(@Url String userUrl);

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
     * @param url
     * @return
     */
    @GET
    Call<Wiki> wiki(@Url String url);

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
     * @param institutionsUrl
     * @return
     */
    @GET
    Call<List<Institution>> institutions(@Url String institutionsUrl);

    /**
     *
     * @param fileUrl
     * @return
     */
    @GET
    Call<File> file(@Url String fileUrl);

    /**
     *
     * @param filesUrl
     * @return
     */
    @GET
    Call<List<File>> files(@Url String filesUrl);

    /**
     *
     * @param logUrl
     * @return
     */
    @GET
    Call<Log> log(@Url String logUrl);

    /**
     *
     * @param fileVersionUrl
     * @return
     */
    @GET
    Call<FileVersion> fileversion(@Url String fileVersionUrl);

    /**
     *
     * @param fileVersionsUrl
     * @return
     */
    @GET
    Call<FileVersion> fileversions(@Url String fileVersionsUrl);

    /**
     *
     * @param metaSchemaUrl
     * @return
     */
    @GET
    Call<MetaSchema> metaschema(@Url String metaSchemaUrl);

}
