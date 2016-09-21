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

import static org.dataconservancy.cos.osf.client.support.JodaSupport.DATE_TIME_FORMATTER;

import java.util.Map;

import org.dataconservancy.cos.osf.client.support.JodaSupport;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Type;

/**
 * Comment model for OSF
 *
 * @author khanson
 * @author esm
 */
@Type("comments")
public class Comment {

    /**
     * Unique OSF id for comment
     */
    @Id
    private String id;

    /**
     * content of the comment
     */
    private String content;

    /**
     * timestamp that the comment was created
     */
    private DateTime date_created;

    /**
     * timestamp when the comment was last updated
     */
    private DateTime date_modified;

    /**
     * has this comment been edited?
     */
    private Boolean isModified;

    /**
     * is this comment deleted?
     */
    private Boolean isDeleted;

    /**
     * has this comment been reported by the current user?
     */
    private Boolean is_abuse;

    /**
     * does this comment have replies?
     */
    private Boolean has_children;

    /**
     * can the current user edit this comment?
     */
    private Boolean can_edit;

    /**
     * Gets other links found in data.links:{} section of JSON
     **/
    @Links
    Map<String, ?> links;

    /**
     * pagination links for multiple records
     */
    private org.dataconservancy.cos.osf.client.model.Links pageLinks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate_created() {
        if (this.date_created != null) {
            return this.date_created.toString(DATE_TIME_FORMATTER);
        } else {
            return null;
        }
    }

    public void setDate_created(String date_created) {
        if (date_created != null) {
            this.date_created = JodaSupport.parseDateTime(date_created);
        } else {
            this.date_created = null;
        }
    }

    public String getDate_modified() {
        if (this.date_modified != null) {
            return this.date_modified.toString(DATE_TIME_FORMATTER);
        } else {
            return null;
        }
    }

    public void setDate_modified(String date_modified) {
        if (date_modified != null) {
            this.date_modified = JodaSupport.parseDateTime(date_modified);
        } else {
            date_modified = null;
        }
    }

    public Boolean isModified() {
        return isModified;
    }

    public void setModified(Boolean isModified) {
        this.isModified = isModified;
    }

    public Boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean isIs_abuse() {
        return is_abuse;
    }

    public void setIs_abuse(Boolean is_abuse) {
        this.is_abuse = is_abuse;
    }

    public Boolean isHas_children() {
        return has_children;
    }

    public void setHas_children(Boolean has_children) {
        this.has_children = has_children;
    }

    public Boolean isCan_edit() {
        return can_edit;
    }

    public void setCan_edit(Boolean can_edit) {
        this.can_edit = can_edit;
    }

    public org.dataconservancy.cos.osf.client.model.Links getPageLinks() {
        return pageLinks;
    }

    @JsonProperty("links")
    public void setPageLinks(org.dataconservancy.cos.osf.client.model.Links pageLinks) {
        this.pageLinks = pageLinks;
    }

    public Map<String, ?> getLinks() {
        return links;
    }

    public void setLinks(Map<String, ?> links) {
        this.links = links;
    }

}
