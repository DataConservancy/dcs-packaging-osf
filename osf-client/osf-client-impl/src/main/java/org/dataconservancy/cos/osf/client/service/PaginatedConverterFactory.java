/*
 * Copyright 2017 Johns Hopkins University
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

import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;
import com.github.jasminb.jsonapi.retrofit.RetrofitType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;
import retrofit.Converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A Retrofit Converter factory that enables pagination without tying the API client to the use of the JSONAPI-converter
 * {@code ResourceList}.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class PaginatedConverterFactory extends JSONAPIConverterFactory {

    private final OkHttpClient okHttp;

    private final ResourceConverter parser;

    /**
     * Constructs a new converter factory with the supplied HTTP client and ResourceConverter.
     *
     * @param okHttp the http client used to retrieve pages
     * @param converter the JSONAPI converter used to unmarshal the JSON from a page into Java objects
     */
    public PaginatedConverterFactory(final OkHttpClient okHttp, final ResourceConverter converter) {
        super(converter);
        if (okHttp == null) {
            throw new IllegalArgumentException("OkHttp client must not be null.");
        }

        if (converter == null) {
            throw new IllegalArgumentException("ResourceConverter must not be null.");
        }
        this.okHttp = okHttp;
        this.parser = converter;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Implementation note: returns a {@code ResponseBodyConverter} that wraps collections in a
     * {@link PaginatedListAdapter}.
     * </p>
     * @param type {@inheritDoc}
     * @param annotations {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Converter<ResponseBody, ?> fromResponseBody(final Type type, final Annotation[] annotations) {
        final RetrofitType retrofitType = new RetrofitType(type);

        if (retrofitType.isValid() && parser.isRegisteredType(retrofitType.getType())) {
            return new PaginatedResponseBodyConverter<>(okHttp, parser, retrofitType.getType(),
                    retrofitType.isCollection());
        }

        return null;
    }
}
