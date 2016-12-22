/*
 * Copyright 2016 Johns Hopkins University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dataconservancy.cos.osf.client.support;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

/**
 * Extracts a link named "download" from the supplied map.  If the link is not present, {@code null} is returned.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class DownloadLinkTransform implements Function<Map<String, ?>, URI> {

    @Override
    public URI apply(final Map<String, ?> links) {
        final String link = (String) links.get("download");
        if (link != null) {
            return URI.create(link);
        }

        return null;
    }
}
