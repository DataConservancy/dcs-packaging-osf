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

package org.dataconservancy.cos.osf.client.support;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by esm on 4/26/16.
 */
public class AuthInterceptor implements Interceptor {

    private String authHeader;

    public AuthInterceptor(String authHeader) {
        this.authHeader = authHeader;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (authHeader == null || authHeader.trim().equals("")) {
            return chain.proceed(chain.request());
        }

        Request req = chain.request();

        if (req.header("Authorization") == null) {
            req = req.newBuilder()
                    .addHeader("Authorization", authHeader)
                    .build();
        }

        return chain.proceed(req);
    }
}
