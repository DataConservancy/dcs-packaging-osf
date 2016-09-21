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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

/**
 * Extracts a key named "version" from the supplied map.  If the key is not present, {@code null} is returned.
 */
public class VersionTransform implements Function<Map<String, ?>, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(VersionTransform.class);

    @Override
    public Integer apply(Map<String, ?> map) {
        if (map.containsKey("version")) {
            try {
                final String val = (String) map.get("version");
                if (val != null && val.trim().length() > 0) {
                    return Integer.parseInt(val);
                }
            } catch (NumberFormatException|ClassCastException e) {
                LOG.warn("Unable to parse the value of 'version' key into an integer.  " +
                        "Value was: '" + map.get("version") + "': " + e.getMessage(), e);
            }
        }

        return null;
    }

}
