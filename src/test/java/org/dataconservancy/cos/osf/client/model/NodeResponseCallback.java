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
package org.dataconservancy.cos.osf.client.model;

import org.apache.commons.io.IOUtils;
import org.mockserver.mock.action.ExpectationCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

import static org.dataconservancy.cos.osf.client.model.NodeTest.X_RESPONSE_RESOURCE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpStatusCode.BAD_REQUEST_400;
import static org.mockserver.model.HttpStatusCode.NOT_FOUND_404;
import static org.mockserver.model.HttpStatusCode.OK_200;

/**
 * Created by esm on 5/11/16.
 */
public class NodeResponseCallback implements ExpectationCallback {

    private static final Logger LOG = LoggerFactory.getLogger(NodeResponseCallback.class);

    @Override
    public HttpResponse handle(HttpRequest req) {
        if (!req.containsHeader(X_RESPONSE_RESOURCE)) {
            return response()
                    .withStatusCode(BAD_REQUEST_400.code());
        }

        String resource = req.getFirstHeader(X_RESPONSE_RESOURCE);
        LOG.trace("Retrieving classpath resource: {}", resource);
        byte[] responseContent = getResource(resource);
        assertNotNull("No classpath resource found for '" + resource + "'", responseContent);
        assertTrue("No classpath resource found for '" + resource + "', or resource is empty",
                responseContent.length > 0);

        return response()
                .withStatusCode(OK_200.code())
                .withHeader("Content-Length", String.valueOf(responseContent.length))
                .withBody(responseContent);
    }

    private byte[] getResource(String resource) {
        URL resourceUrl = this.getClass().getResource(resource);
        if (resourceUrl == null) {
            return null;
        }

        try {
            return IOUtils.toByteArray(resourceUrl);
        } catch (IOException e) {
            return null;
        }
    }

}
