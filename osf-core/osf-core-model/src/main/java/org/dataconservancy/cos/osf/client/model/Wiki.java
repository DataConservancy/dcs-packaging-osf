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

import static org.dataconservancy.cos.osf.client.support.JodaSupport.DATE_TIME_FORMATTER_ALT;

import java.util.List;
import java.util.Map;

import org.dataconservancy.cos.osf.client.support.DateTimeTransform;
import org.dataconservancy.cos.osf.client.support.DownloadLinkTransform;
import org.dataconservancy.cos.osf.client.support.JodaSupport;
import org.dataconservancy.cos.osf.client.support.UrlToIdTransform;
import org.dataconservancy.cos.osf.client.support.VersionTransform;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.RelType;
import com.github.jasminb.jsonapi.ResolutionStrategy;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

/**
 *
 * @author Ben Trumbore (wbt3@cornell.edu)
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Type("wikis")
@OwlIndividual(OwlClasses.OSF_WIKI)
public class Wiki {

    @Id
    @IndividualUri
    private String id;

    @Relationship(value = "comments", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    @OwlProperty(OwlProperties.OSF_HAS_COMMENT)
    private List<Comment> comments;

    @Links
    @OwlProperty(value = OwlProperties.OSF_HAS_BINARYURI, transform = DownloadLinkTransform.class)
    private Map<String, ?> links;

    @OwlProperty(OwlProperties.OSF_HAS_NAME)
    private String name;

    @OwlProperty(OwlProperties.OSF_HAS_HASKIND)
    private String kind;

    @OwlProperty(OwlProperties.OSF_HAS_MATERIALIZEDPATH)
    private String materialized_path;

    @OwlProperty(value = OwlProperties.OSF_HAS_DATEMODIFIED, transform = DateTimeTransform.class)
    private DateTime date_modified;

    @Relationship(value = "node", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    @OwlProperty(value = OwlProperties.OSF_HAS_NODE, transform = UrlToIdTransform.class)
    private String node;

    @Relationship(value = "user", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    @OwlProperty(OwlProperties.OSF_AUTHORED_BY)
    private User user;

    @OwlProperty(OwlProperties.OSF_HAS_PATH)
    private String path;

    @OwlProperty(OwlProperties.OSF_HAS_CONTENTTYPE)
    private String content_type;

    @OwlProperty(value = OwlProperties.OSF_VERSION, transform = VersionTransform.class)
    private Map<String, ?> extra; // for "version"

    @OwlProperty(OwlProperties.OSF_HAS_SIZE)
    private int size;

    /**
     *
     * @return
     */
    public String getKind() {
        return kind;
    }

    /**
     *
     * @param kind
     */
    public void setKind(final String kind) {
        this.kind = kind;
    }

    /**
     *
     * @return
     */
    public String getNode() {
        return node;
    }

    /**
     *
     * @param node
     */
    public void setNode(final String node) {
        this.node = node;
    }

    /**
     *
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     *
     * @param user
     */
    public void setUser(final User user) {
        this.user = user;
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
    public String getMaterialized_path() {
        return materialized_path;
    }

    /**
     *
     * @param materialized_path
     */
    public void setMaterialized_path(final String materialized_path) {
        this.materialized_path = materialized_path;
    }

    /**
     *
     * @return
     */
    public String getDate_modified() {
        if (this.date_modified != null) {
            return this.date_modified.toString(DATE_TIME_FORMATTER_ALT);
        } else {
            return null;
        }
    }

    /**
     *
     * @param date_modified
     */
    public void setDate_modified(final String date_modified) {
        if (date_modified != null) {
            this.date_modified = JodaSupport.parseDateTime(date_modified);
        }
    }

    /**
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     *
     * @param path
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     *
     * @return
     */
    public String getContent_type() {
        return content_type;
    }

    /**
     *
     * @param content_type
     */
    public void setContent_type(final String content_type) {
        this.content_type = content_type;
    }

    /**
     *
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     *
     * @param size
     */
    public void setSize(final int size) {
        this.size = size;
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
    public List<Comment> getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     */
    public void setComments(final List<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Return the version of the Wiki page.
     *
     * @return the version
     */
    public int getVersion() {
        return Integer.parseInt(extra.get("version").toString());
    }

}
