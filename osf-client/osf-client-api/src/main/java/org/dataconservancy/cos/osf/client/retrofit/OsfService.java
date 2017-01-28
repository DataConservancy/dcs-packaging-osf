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

package org.dataconservancy.cos.osf.client.retrofit;

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
import org.dataconservancy.cos.osf.client.model.LightNode;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.LightRegistration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.model.LightUser;
import org.dataconservancy.cos.osf.client.model.Wiki;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import retrofit.http.Streaming;
import retrofit.http.Url;

/**
 * Abstracts the execution of HTTP queries against the OSF version 2 JSON API and unmarshals the JSON response to
 * Java domain objects, which represent instances of the types presented by the OSF JSON API.
 * <p>
 * For each domain object, there are typically two methods: 1) given a URL to a object, unmarshal it; 2) given a URL to
 * a collection of objects of the same type, unmarshal the collection into a List&lt;T&gt;.  Because {@link Node},
 * {@link Registration}, and {@link User} are of broad interest likely to accommodate a number of use cases,
 * they have additional consideration.
 * </p>
 * <p>
 * Firstly, these classes have lightweight representations in {@link LightNode}, {@link LightRegistration}, and
 * {@link LightUser}.  Their lightweight nature is achieved by omitting most properties of the object except for
 * identifiers and dates, and omitting relationships to other objects that would require link traversal (resulting in
 * additional HTTP and object creation overhead).  Secondly, the {@code OsfService} provides additional methods that
 * support retrieval according to criteria expressed as query parameters, allowing for, e.g. server-side filtering of
 * results.  This allows clients who may need to process a large number of objects to retrieve them quickly and
 * efficiently.  After identifying the lightweight objects of interest, their "heavy" counterparts can be retrieved.
 * </p>
 * <p>
 * Collections returned by {@code OsfService} are transparently paginated by the underlying {@code List} implementation.
 * Clients are encouraged to keep the creation of streams (e.g. via {@link List#stream()}) to a minimum, and to
 * code defensively, recognizing that there is HTTP request overhead when traversing the elements of a stream.  It is
 * difficult to provide specific recommendations, as what is considered efficient will depend on the use case.  However,
 * here are some options to consider when working with streams:
 * </p>
 * <ul>
 *     <li>Most <a href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#StreamOps">
 *         terminal operations</a> are eager, meaning that the entire
 *     stream will be processed to exhaustion.  Consider use of {@link java.util.stream.Stream#iterator()} when
 *     processing the entire stream is not necessary.</li>
 *     <li>If multiple traversals of the same collection are required, consider streaming the result into a new data
 *     structure (e.g. a new {@code Collection} or {@code Map}) before executing additional operations.</li>
 *     <li>Alternately, retrieve the stream once, and perform all the operations in a single traversal of the
 *     stream.</li>
 *     <li>Understand that {@code RuntimeException} may be thrown while processing a stream if network interruptions or
 *     latency prevent the retrieval of a results page.</li>
 * </ul>
 * <p>
 * URL as method parameters, encoding, etc.
 * </p>
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 * @author Karen Hanson (karen.hanson@jhu.edu)
 */
public interface OsfService {

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
     * @param url
     * @return
     */
    @GET
    Call<File> file(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<File>> files(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<FileVersion> fileversion(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<FileVersion> fileversions(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<Institution> institution(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<Institution>> institutions(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<License> license(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<License>> licenses(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<Log> log(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<Log>> logs(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<MetaSchema> metaschema(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<MetaSchema>> metaschemas(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<Node> node(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<Node>> nodes(@Url String url);

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
    Call<List<LightNode>> nodeIds();

    /**
     *
     * @param params
     * @return
     */
    @GET("nodes/")
    Call<List<LightNode>> nodeIds(@QueryMap Map<String, String> params);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<LightNode> lightnode(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<LightNode>> lightnodes(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<Registration> registration(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<Registration>> registrations(@Url String url);

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
    Call<List<LightRegistration>> registrationIds();

    /**
     *
     * @param params
     * @return
     */
    @GET("registrations/")
    Call<List<LightRegistration>> registrationIds(@QueryMap Map<String, String> params);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<LightRegistration> lightregistration(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<LightRegistration>> lightregistrations(@Url String url);


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
     * @param url
     * @return
     */
    @GET
    Call<User> user(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<User>> users(@Url String url);

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
    Call<List<LightUser>> userIds();

    /**
     *
     * @param params
     * @return
     */
    @GET("users/")
    Call<List<LightUser>> userIds(@QueryMap Map<String, String> params);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<LightUser> lightuser(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    Call<List<LightUser>> lightusers(@Url String url);

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
