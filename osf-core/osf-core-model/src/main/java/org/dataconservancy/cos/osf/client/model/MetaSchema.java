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
package org.dataconservancy.cos.osf.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Type;

import java.util.Map;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@Type("metaschemas")
public class MetaSchema {

    @Id
    private String id;

    private String name;

    private int schema_version;

    private Schema schema;

    @Links
    private Map<String, ?> links;

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
    public int getSchema_version() {
        return schema_version;
    }

    /**
     *
     * @param schema_version
     */
    public void setSchema_version(final int schema_version) {
        this.schema_version = schema_version;
    }

    /**
     *
     * @return
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     *
     * @param schema
     */
    public void setSchema(final Schema schema) {
        this.schema = schema;
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
    @JsonProperty("links")
    public void setLinks(final Map<String, ?> links) {
        this.links = links;
    }
}
