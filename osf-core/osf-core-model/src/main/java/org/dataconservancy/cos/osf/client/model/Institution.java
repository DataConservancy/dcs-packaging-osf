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
package org.dataconservancy.cos.osf.client.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Type;

/**
 * Institution model for OSF
 * @author khanson
 *
 */
@Type("institutions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Institution {

    /**unique institution identifier in the OSF*/
    @Id
    private String id;

    /**title of the institution*/
    private String name;

    /**a path to the institution's static logo*/
    private String logo_path;

    /**Gets other links found in data.links:{} section of JSON**/
    @Links
    Map<String, ?> links;

    /**pagination links, applies when list is returned**/
    private PageLinks pageLinks;

    private String auth_url;

    private String description;

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getLogo_path() {
        return logo_path;
    }

    /**
     *
     * @param logo_path
     */
    public void setLogo_path(final String logo_path) {
        this.logo_path = logo_path;
    }

    /**
     *
     * @return
     */
    public PageLinks getPageLinks() {
        return pageLinks;
    }

    /**
     *
     * @param pageLinks
     */
    @JsonProperty("links")
    public void setPageLinks(final PageLinks pageLinks) {
        this.pageLinks = pageLinks;
    }

    /**
     *
     * @return
     */
    public Map<String, ?> getLinks() {
        return links;
    }

    /**
     *
     * @param links
     */
    public void setLinks(final Map<String, ?> links) {
        this.links = links;
    }

    /**
     *
     * @return
     */
    public String getAuth_url() {
        return auth_url;
    }

    /**
     *
     * @param auth_url
     */
    public void setAuth_url(final String auth_url) {
        this.auth_url = auth_url;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

}
