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
import com.squareup.okhttp.OkHttpClient;

import java.util.Collections;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Wraps an OkHttpClient providing getters and setters for Spring and any other frameworks that follow the JavaBean
 * standard.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class BeanAccessibleOkHttpClient extends OkHttpClient {

    /**
     * Returns an immutable list of interceptors on the client.
     *
     * @return immutable list of interceptors
     * @see #interceptors
     */
    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(super.interceptors());
    }

    /**
     * Sets the supplied interceptors on the client by copying the supplied list.  Any existing interceptors are
     * cleared.
     *
     * @param interceptors interceptors to set on the client
     */
    public void setInterceptors(final List<Interceptor> interceptors) {
        this.interceptors().clear();
        for (Interceptor toAdd : interceptors) {
            this.interceptors().add(toAdd);
        }
    }

    /**
     *
     * @param timeoutMs
     */
    public void setReadTimeout(final int timeoutMs) {
        setReadTimeout(timeoutMs, MILLISECONDS);
    }

    /**
     *
     * @param timeoutMs
     */
    public void setWriteTimeout(final int timeoutMs) {
        setWriteTimeout(timeoutMs, MILLISECONDS);
    }

    /**
     *
     * @param timeoutMs
     */
    public void setConnectTimeout(final int timeoutMs) {
        setConnectTimeout(timeoutMs, MILLISECONDS);
    }
}
