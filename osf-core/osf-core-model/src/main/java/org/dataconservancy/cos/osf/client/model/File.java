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

import static java.util.function.Function.identity;
import static org.dataconservancy.cos.osf.client.support.JodaSupport.DATE_TIME_FORMATTER_ALT;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.dataconservancy.cos.osf.client.support.DateTimeTransform;
import org.dataconservancy.cos.osf.client.support.DownloadLinkTransform;
import org.dataconservancy.cos.osf.client.support.JodaSupport;
import org.dataconservancy.cos.osf.client.support.ProviderIdTransform;
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
 * File model for OSF
 * Created by esm on 5/2/16.
 * @author esm
 * @author khanson
 */
@Type("files")
@JsonIgnoreProperties(ignoreUnknown = true)
@OwlIndividual(OwlClasses.OSF_FILEBASE)
public class File {

    /*unique OSF ID for the file**/
    @Id
    @IndividualUri(transform = ProviderIdTransform.class)
    private String id;

    /**list of files down next level of file tree*/
    @Relationship(value = "files", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    @OwlProperty(OwlProperties.OSF_HAS_FILE)
    private List<File> files;

    /**list of versions associated with file*/
    @Relationship(value = "versions", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    private List<FileVersion> versions;

    /**list of comments associated with file*/
    @Relationship(value = "comments", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    private List<Comment> comments;

    /**Gets other links found in data.links:{} section of JSON**/
    @Links
    @OwlProperty(value = OwlProperties.OSF_HAS_BINARYURI, transform = DownloadLinkTransform.class)
    Map<String, ?> links;

    /**pagination links, applies when list is returned**/
    private PageLinks pageLinks;

    /**name of the file or folder; used for display*/
    @OwlProperty(OwlProperties.OSF_HAS_NAME)
    private String name;

    /**"file" or "folder"*/
    @OwlProperty(OwlProperties.OSF_HAS_HASKIND)
    private String kind;

    /**the unix-style path to the file relative to the provider root*/
    @OwlProperty(OwlProperties.OSF_HAS_MATERIALIZEDPATH)
    private String materialized_path;

    /**timestamp of when this file was created**/
    @OwlProperty(value = OwlProperties.OSF_HAS_DATECREATED, transform = DateTimeTransform.class)
    private DateTime date_created;

    /**timestamp of when this file was last updated*/
    @OwlProperty(value = OwlProperties.OSF_HAS_DATEMODIFIED, transform = DateTimeTransform.class)
    private DateTime date_modified;

    /**storage provider for this file. "osfstorage" if stored on the
     * OSF.  other examples include "s3" for Amazon S3, "googledrive"   */
    @OwlProperty(value = OwlProperties.OSF_PROVIDER_NAME)
    private String provider;

    /**node this provider belongs to*/
    @OwlProperty(OwlProperties.OSF_HAS_NODE)
    private String node;

    /**same as for corresponding WaterButler entity*/
    @OwlProperty(OwlProperties.OSF_HAS_PATH)
    private String path;

    /**size of file in bytes, null for folders*/
    @OwlProperty(OwlProperties.OSF_HAS_SIZE)
    private Integer size;

    /**list of hashes of the hashes for the files*/
    private Set<Checksum> hashes;

    private DateTime last_touched;

    private int current_version;

    private String guid;

    private List<String> tags;

    private Map<String, ?> extra;

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
    public String getDate_created() {
        if (this.date_created != null) {
            return this.date_created.toString(DATE_TIME_FORMATTER_ALT);
        } else {
            return null;
        }
    }

    /**
     *
     * @param date_created
     */
    public void setDate_created(final String date_created) {
        if (date_created != null) {
            this.date_created = JodaSupport.parseDateTime(date_created);
        } else {
            this.date_created = null;
        }
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
    public String getLast_touched() {
        if (this.last_touched != null) {
            return this.last_touched.toString(DATE_TIME_FORMATTER_ALT);
        }

        return null;
    }

    /**
     *
     * @param last_touched
     */
    public void setLast_touched(final String last_touched) {
        if (last_touched != null) {
            this.last_touched = JodaSupport.parseDateTime(last_touched);
        }
    }

    /**
     *
     * @return
     */
    public String getProvider() {
        return provider;
    }

    /**
     *
     * @param provider
     */
    public void setProvider(final String provider) {
        this.provider = provider;
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
    public Integer getSize() {
        return size;
    }

    /**
     *
     * @param size
     */
    public void setSize(final Integer size) {
        this.size = size;
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
    public List<File> getFiles() {
        return files;
    }

    /**
     *
     * @param files
     */
    public void setFiles(final List<File> files) {
        this.files = files;
    }

    /**
     *
     * @param hashes
     */
    public void setHashes(final Set<Checksum> hashes) {
        this.hashes = hashes;
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
     *
     * @return
     */
    public List<FileVersion> getVersions() {
        return versions;
    }

    /**
     *
     * @param versions
     */
    public void setVersions(final List<FileVersion> versions) {
        this.versions = versions;
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
    public int getCurrent_version() {
        return current_version;
    }

    /**
     *
     * @param current_version
     */
    public void setCurrent_version(final int current_version) {
        this.current_version = current_version;
    }

    /**
     *
     * @return
     */
    public String getGuid() {
        return guid;
    }

    /**
     *
     * @param guid
     */
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    /**
     *
     * @return
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     *
     * @param tags
     */
    public void setTags(final List<String> tags) {
        this.tags = tags;
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
    public Map<String, Checksum> getHashes() {
        if (this.extra != null && this.extra.containsKey("hashes")) {
            return ((Map<String, String>)this.extra.get("hashes"))
                    .entrySet()
                    .stream()
                    .map((entry) ->
                            new Checksum(Checksum.Algorithm.valueOf(entry.getKey().toUpperCase()), entry.getValue()))
                    .collect(Collectors.toMap(
                            (checksum) -> checksum.getAlgorithm().name().toLowerCase(), identity()));
        }

        return null;
    }
}
