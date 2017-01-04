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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Model for extra OSF Registration metadata
 * @author khanson
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationMetadata {
/*
 * Lots of fields
 * examples:
 * 
    "registered_meta": {
        "summary": {
            "value": "This is the summary field for \"an open ended registration\" for a private project",
            "comments": [],
            "extra": {}
        }
    },

    "registered_meta": {
        "looked": {
            "value": "Yes",
            "comments": [],
            "extra": {}
            },
        "datacompletion": {
            "value": "Yes, data collection is underway or complete",
            "comments": [],
            "extra": {}
        },
        "comments": {
            "value": "This is the other comments field for \"an OSF standard registration\" for a private project",
            "comments": [],
            "extra": {}
        }
    },

 */

    private String value;

    private List<String> comments;

    private Map<String, ?> extra;

    /**
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @param value
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     *
     * @return
     */
    public List<String> getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     */
    public void setComments(final List<String> comments) {
        this.comments = comments;
    }

    /**
     *
     * @return
     */
    public Map<String, ?> getExtra() {
        return extra;
    }

    /**
     *
     * @param extra
     */
    public void setExtra(final Map<String, ?> extra) {
        this.extra = extra;
    }
}
