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
package org.dataconservancy.cos.osf.client.support;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import org.dataconservancy.cos.osf.client.config.OsfClientConfiguration;
import org.dataconservancy.cos.osf.client.config.OsfConfigurationService;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Insures proper functioning of the {@link ApiVersionInterceptor}.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class ApiVersionInterceptorTest {

    /**
     * Insures the header value supplied to the constructor is used.
     *
     * @throws Exception
     */
    @Test
    public void testApiVersionStringConstructor() throws Exception {
        final String version = "foo";
        assertTrue(new ApiVersionInterceptor(version).acceptHeader().endsWith(version));
    }

    /**
     * Insures the header value is retrieved from the configuration service.
     *
     * @throws Exception
     */
    @Test
    public void testApiVersionOsfConfigServiceConstructor() throws Exception {
        final String version = "foo";
        final OsfClientConfiguration config = new OsfClientConfiguration();
        config.setApiVersion(version);
        final OsfConfigurationService configService = mock(OsfConfigurationService.class);
        when(configService.getConfiguration()).thenReturn(config);

        assertTrue(new ApiVersionInterceptor(configService).acceptHeader().endsWith(version));
        verify(configService).getConfiguration();
    }

    /**
     * Insures the HTTP header is added with the supplied version.
     *
     * @throws Exception
     */
    @Test
    public void testChain() throws Exception {
        final String version = "foo";
        final Interceptor.Chain chain = mock(Interceptor.Chain.class);
        final Request req = new Request.Builder().url("http://example.org").build();
        when(chain.request()).thenReturn(req);
        when(chain.proceed(any(Request.class))).then(invocation -> {
            final Request request = (Request)invocation.getArguments()[0];
            assertTrue(request.header("Accept").endsWith(version));
            return null; // response is ignored in this test.
        });

        new ApiVersionInterceptor(version).intercept(chain);

        verify(chain).proceed(any(Request.class));
    }

    /**
     * Insures the acceptHeader method formulates the header value properly.
     *
     * @throws Exception
     */
    @Test
    public void testAcceptHeader() throws Exception {
        final String version = "foo";

        assertEquals(String.format(ApiVersionInterceptor.ACCEPT_HEADER, version),
                new ApiVersionInterceptor(version).acceptHeader());

    }

}