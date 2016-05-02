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

import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.NodeFile;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

import java.util.List;
import java.util.Map;

/**
 * Created by esm on 4/25/16.
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

}
