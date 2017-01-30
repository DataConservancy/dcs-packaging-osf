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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import org.dataconservancy.cos.osf.client.support.JodaSupport;
import org.joda.time.DateTime;

import static org.dataconservancy.cos.osf.client.support.JodaSupport.DATE_TIME_FORMATTER_ALT;

/**
 * Model exposes Node ID and pagination links only, so that expensive full model call is not necessary
 *
 * @author khanson
 */
@Type("nodes")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LightNode {

    /**
     * node id
     */
    @Id
    private String id;

    /**
     * pagination links, applies when list is returned
     */
    private PageLinks pageLinks;

    /**
     * timestamp that the node was created
     */
    private DateTime date_created;

    /**
     * timestamp when the node was last updated
     */
    private DateTime date_modified;

    /**
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return
     */
    public PageLinks getPageLinks() {
        return pageLinks;
    }

    /**
     * @param pageLinks
     */
    @JsonProperty("links")
    public void setPageLinks(final PageLinks pageLinks) {
        this.pageLinks = pageLinks;
    }

    /**
     * @return
     */
    public String getDate_created() {
        if (this.date_created != null) {
            return this.date_created.toString(DATE_TIME_FORMATTER_ALT);
        }
        return null;
    }

    /**
     * @param date_created
     */
    public void setDate_created(final String date_created) {
        if (date_created != null) {
            this.date_created = JodaSupport.parseDateTime(date_created);
        }
    }

    /**
     * @return
     */
    public String getDate_modified() {
        if (this.date_modified != null) {
            return this.date_modified.toString(DATE_TIME_FORMATTER_ALT);
        }
        return null;
    }

    /**
     * @param date_modified
     */
    public void setDate_modified(final String date_modified) {
        if (date_modified != null) {
            this.date_modified = JodaSupport.parseDateTime(date_modified);
        }
    }
}
