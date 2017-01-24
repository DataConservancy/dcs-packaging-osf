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
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;
import retrofit.Converter;

import java.io.IOException;

/**
 * A Retrofit Converter that wraps collections in a {@code PaginatedList}.  This allows clients of the API to avoid
 * direct dependencies on the JSONAPI-converter {@code ResourceList}.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class PaginatedResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final boolean isCollection;

    private final ResourceConverter resourceConverter;

    private final Class<T> clazz;

    private final OkHttpClient okHttp;

    /**
     * Contructs a new converter capable of iterating over the pages of a collection.  If the response is not a
     * collection, then pagination capabilites are not required, and the response will not be wrapped.
     *
     * @param okHttp the OkHttp client used to request additional pages of a paginated response
     * @param resourceConverter the JSONAPI-converter used to unmarshal JSON from a response into Java objects
     * @param clazz the type of object being retrieved in the response
     * @param isCollection true if the supplied {@code clazz} represents a collection
     */
    public PaginatedResponseBodyConverter(final OkHttpClient okHttp, final ResourceConverter resourceConverter,
                                          final Class<T> clazz, final boolean isCollection) {
        if (okHttp == null) {
            throw new IllegalArgumentException("OkHttp must not be null.");
        }
        if (resourceConverter == null) {
            throw new IllegalArgumentException("ResourceConverter must not be null.");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Class type must not be null.");
        }

        this.okHttp = okHttp;
        this.resourceConverter = resourceConverter;
        this.clazz = clazz;
        this.isCollection = isCollection;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Implementation note: if {@code isCollection} is {@code true}, the collection returned by the ResourceConverter
     * will be adapted to a {@code PaginatedList} and returned.
     * </p>
     * @param responseBody the response returned from the API
     * @return a potentially paginated response
     * @throws IOException {@inheritDoc}
     */
    @Override
    public T convert(final ResponseBody responseBody) throws IOException {
        if (isCollection) {
            return (T) new PaginatedListAdapter<>(okHttp, resourceConverter, clazz,
                    resourceConverter.readObjectCollection(responseBody.bytes(), clazz));
        } else {
            return resourceConverter.readObject(responseBody.bytes(), clazz);
        }
    }
}
