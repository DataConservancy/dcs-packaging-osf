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
import org.dataconservancy.cos.osf.client.model.NodeId;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.RegistrationId;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.model.UserId;
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
    @GET
    Call<Contributor> contributor(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<Contributor>> contributors(@Url String url);

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
     * @param licenseUrl
     * @return
     */
    @GET
    Call<License> license(@Url String licenseUrl);

    /**
     *
     * @param licenseUrl
     * @return
     */
    @GET
    Call<List<License>> licenses(@Url String licenseUrl);

    /**
     *
     * @param logUrl
     * @return
     */
    @GET
    Call<Log> log(@Url String logUrl);

    /**
     *
     * @param logsUrl
     * @return
     */
    @GET
    Call<List<Log>> logs(@Url String logsUrl);

    /**
     *
     * @param metaSchemaUrl
     * @return
     */
    @GET
    Call<MetaSchema> metaschema(@Url String metaSchemaUrl);

    /**
     *
     * @param metaSchemasUrl
     * @return
     */
    @GET
    Call<List<MetaSchema>> metaschemas(@Url String metaSchemasUrl);

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
     * @param nodeId
     * @return
     */
    @GET("nodes/{id}/")
    Call<Node> nodeById(@Path("id") String nodeId);

    /**
     *
     * @return
     */
    @GET("nodes/")
    Call<List<NodeId>> nodeIds();

    /**
     *
     * @param page
     * @param params
     * @return
     */
    @GET("registrations/")
    Call<List<NodeId>> nodeIds(@Query("page") int page, @QueryMap Map<String, String> params);

    /**
     *
     * @param params
     * @return
     */
    @GET("registrations/")
    Call<List<NodeId>> nodeIds(@QueryMap Map<String, String> params);

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
     * @param id
     * @return
     */
    @GET("registrations/{id}/")
    Call<Registration> registrationById(@Path("id") String id);

    /**
     *
     * @return
     */
    @GET("registrations/")
    Call<List<RegistrationId>> registrationIds();

    /**
     *
     * @param page
     * @param params
     * @return
     */
    @GET("registrations/")
    Call<List<RegistrationId>> registrationIds(@Query("page") int page, @QueryMap Map<String, String> params);

    /**
     *
     * @param params
     * @return
     */
    @GET("registrations/")
    Call<List<RegistrationId>> registrationIds(@QueryMap Map<String, String> params);

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
     * @param userUrl
     * @return
     */
    @GET
    Call<User> user(@Url String userUrl);

    /**
     *
     * @return
     */
    @GET
    Call<List<User>> users(@Url String usersUrl);

    /**
     *
     * @param id
     * @return
     */
    @GET("users/{id}/")
    Call<User> userById(@Path("id") String id);

    /**
     * 
     * @return
     */
    @GET("users/")
    Call<List<UserId>> userIds();

    /**
     *
     * @param page
     * @param params
     * @return
     */
    @GET("users/")
    Call<List<UserId>> userIds(@Query("page") int page, @QueryMap Map<String, String> params);

    /**
     *
     * @param params
     * @return
     */
    @GET("users/")
    Call<List<UserId>> userIds(@QueryMap Map<String, String> params);

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
     * @param url
     * @return
     */
    @GET
    Call<Wiki> wiki(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<Wiki>> wikis(@Url String url);

}
