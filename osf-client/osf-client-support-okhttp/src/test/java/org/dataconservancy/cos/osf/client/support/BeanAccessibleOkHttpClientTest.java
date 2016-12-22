/*
 *
 *  * Copyright 2016 Johns Hopkins University
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.dataconservancy.cos.osf.client.support;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static junit.framework.TestCase.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class BeanAccessibleOkHttpClientTest {

    @Test
    public void testSetInterceptorsClearsExistingInterceptors() throws Exception {
        final BeanAccessibleOkHttpClient underTest = new BeanAccessibleOkHttpClient();
        final MockInterceptor initialInterceptor = new MockInterceptor();

        // Set an interceptor on the client
        underTest.setInterceptors(Collections.singletonList(initialInterceptor));
        assertEquals(1, underTest.getInterceptors().size());
        assertSame(initialInterceptor, underTest.getInterceptors().get(0));

        // When we call setInterceptors, the list of interceptors managed by
        // BeanAccessibleOkHttpClient should be cleared, and it should contain our new instance

        final MockInterceptor subsequentInterceptor = new MockInterceptor();
        assertNotSame(initialInterceptor, subsequentInterceptor);  // sanity
        underTest.setInterceptors(Collections.singletonList(subsequentInterceptor));
        assertEquals(1, underTest.getInterceptors().size());
        assertSame(subsequentInterceptor, underTest.getInterceptors().get(0));
    }

    private class MockInterceptor implements Interceptor {

        @Override
        public Response intercept(final Chain chain) throws IOException {
            return null;
        }
    }
}