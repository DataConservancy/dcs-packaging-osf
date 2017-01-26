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
import com.github.jasminb.jsonapi.ResourceList;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This iterator implementation supplies elements from an initial collection of results.  Once the initial collection
 * has been exhausted, the next page of results will be retrieved from the API.  Subsequent pages are retrieved in the
 * same fashion, until there are no more pages of results.
 * <p>
 * This class is not thread-safe; it cannot be accessed by simultaneous threads and performs no internal
 * synchronization.
 * </p>
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
class PagingIterator<T> implements Iterator<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PagingIterator.class);

    private final OkHttpClient okHttp;

    private final ResourceConverter converter;

    private final Class<T> type;

    ResourceList<T> currentList;

    Iterator<T> currentItr;

    /**
     * @param okHttp
     * @param initial
     * @param type
     */
    public PagingIterator(final OkHttpClient okHttp, final ResourceConverter converter, final ResourceList<T> initial,
                          final Class<T> type) {
        if (okHttp == null) {
            throw new IllegalArgumentException("OsfService must not be null.");
        }

        if (converter == null) {
            throw new IllegalArgumentException("ResourceConverter must not be null");
        }

        if (initial == null) {
            throw new IllegalArgumentException("PaginatedList must not be null.");
        }

        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }

        this.okHttp = okHttp;
        this.converter = converter;
        this.type = type;
        this.currentList = initial;
        this.currentItr = initial.iterator();
    }

    @Override
    public boolean hasNext() {
        if (currentItr == null) {
            return false;
        }

        if (currentItr.hasNext()) {
            return true;
        }

        // can we get more pages?
        if (getNextInternal()) {
            return currentItr.hasNext();
        }

        return false;
    }

    @Override
    public T next() {
        if (currentItr == null) {
            throw new NoSuchElementException();
        }

        if (currentItr.hasNext()) {
            return currentItr.next();
        }

        // can we get more pages?
        if (getNextInternal()) {
            return currentItr.next();
        }

        throw new NoSuchElementException();
    }

    /**
     * Manages the state of {@code currentList} and {@code currentItr}
     *
     *
     * @throws IOException
     */
    boolean getNextInternal() {
        final String next = currentList.getNext();
        if (next == null) {
            currentList = null;
            currentItr = null;
            return false;
        }

        try {
            currentList = converter.readObjectCollection(
                    okHttp.newCall(new Request.Builder().get().url(next).build())
                            .execute().body().bytes(), type);
            currentItr = currentList.iterator();
            return true;
        } catch (IOException | RuntimeException e) {
            LOG.info("Error retrieving results page '{}': {}", next, e.getMessage(), e);
            currentList = null;
            currentItr = null;
        }

        return false;
    }

}