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

import java.net.URI;
import java.util.Map;

import com.github.jasminb.jsonapi.RelType;
import com.github.jasminb.jsonapi.ResolutionStrategy;
import com.github.jasminb.jsonapi.annotations.Relationship;
import org.dataconservancy.cos.osf.client.support.DateTimeTransform;
import org.dataconservancy.cos.osf.client.support.JodaSupport;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
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
@OwlIndividual(OwlClasses.OSF_COMMENT)
public class Comment {

    /**
     * Unique OSF id for comment
     */
    @Id
    @IndividualUri
    private String id;

    /**
     * content of the comment
     */
    @OwlProperty(OwlProperties.OSF_HAS_CONTENT)
    private String content;

    /**
     * timestamp that the comment was created
     */
    @OwlProperty(value = OwlProperties.OSF_HAS_DATECREATED, transform = DateTimeTransform.class)
    private DateTime date_created;

    /**
     * timestamp when the comment was last updated
     */
    @OwlProperty(value = OwlProperties.OSF_HAS_DATEMODIFIED, transform = DateTimeTransform.class)
    private DateTime date_modified;

    /**
     * has this comment been edited?
     */
    private boolean isModified;

    /**
     * is this comment deleted?
     */
    private boolean isDeleted;

    /**
     * has this comment been marked as abuse?
     */
    private boolean is_abuse;

    /**
     * has this comment been reported?
     */
    private boolean has_report;

    /**
     * does this comment have replies?
     */
    private boolean has_children;

    /**
     * can the current user edit this comment?
     */
    private boolean can_edit;

    /**
     * Gets other links found in data.links:{} section of JSON
     **/
    @Links
    Map<String, ?> links;

    /**
     * pagination links for multiple records
     */
    private PageLinks pageLinks;

    /**
     * the node this comment belongs to (distinct from the target)
     */
    @Relationship(value = "node", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    @OwlProperty(OwlProperties.OSF_HAS_NODE)
    private Node node;

    /**
     * API url to replies to this comment
     */
    @Relationship(value = "replies", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String replies;

    /**
     * API url to the target of this comment (i.e. the entity that the comment was placed on)
     */
    @Relationship(value = "target", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String target;

    /**
     * API url to the target of this comment (i.e. the entity that the comment was placed on), typed as a URI
     */
    @OwlProperty(OwlProperties.OSF_IN_REPLY_TO)
    private URI targetUri;

    /**
     * The user that authored the comment
     */
    @Relationship(value = "user", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    @OwlProperty(OwlProperties.OSF_HAS_USER)
    private User user;

    @Relationship(value = "reports", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String reports;

    /**
     * whether or not this comment is ham
     */
    private boolean is_ham;

    /**
     * The "type" of thing being commented on (e.g. "page", "wiki")
     */
    private String page;

    /**
     * String identifying this comment.
     *
     * @return the identifier
     */
    public String getId() {
        return id;
    }

    /**
     * String identifying this comment.
     *
     * @param id the string identifying this comment
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * The textual content of the comment.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * The textual content of the comment.
     *
     * @param content the content
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * The date the comment was created, may be {@code null}.  The date will be formatted according to the format
     * string {@code yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}, and be in the UTC time zone.  See also
     * {@link JodaSupport#DATE_TIME_FORMATTER}.
     *
     * @return the date the comment was created
     */
    public String getDate_created() {
        if (this.date_created != null) {
            return this.date_created.toString(DATE_TIME_FORMATTER_ALT);
        }

        return null;
    }

    /**
     * The date the comment was created.  A variety of date time format strings are supported.
     *
     * @param date_created the date creation string
     * @throws RuntimeException if the supplied {@code date_created} is not recognized as a dateTime
     */
    public void setDate_created(final String date_created) {
        if (date_created != null) {
            this.date_created = JodaSupport.parseDateTime(date_created);
        }
    }

    /**
     * The date the comment was modified, may be {@code null}.  The date will be formatted according to the format
     * string {@code yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}, and be in the UTC time zone.  See also
     * {@link JodaSupport#DATE_TIME_FORMATTER}.
     *
     * @return the date the comment was modified
     */
    public String getDate_modified() {
        if (this.date_modified != null) {
            return this.date_modified.toString(DATE_TIME_FORMATTER_ALT);
        }

        return null;
    }

    /**
     * The date the comment was created, may be {@code null}.  The date will be formatted according to the format
     * string {@code yyyy-MM-dd'T'HH:mm:ss.SSSSSS}, and be in the UTC time zone.  See also
     * {@link JodaSupport#DATE_TIME_FORMATTER}.
     *
     * @param date_modified the date creation string
     * @throws RuntimeException if the supplied {@code date_modified} is not recognized as a dateTime
     */
    public void setDate_modified(final String date_modified) {
        if (date_modified != null) {
            this.date_modified = JodaSupport.parseDateTime(date_modified);
        }
    }

    /**
     * If the comment has been edited
     *
     * @return true if the content was modified
     */
    public boolean isModified() {
        return isModified;
    }

    /**
     * If the comment has been edited
     *
     * @param isModified if the content was modified
     */
    public void setModified(final Boolean isModified) {
        this.isModified = isModified;
    }

    /**
     * If the comment has been deleted
     *
     * @return true if the comment was deleted
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * If the comment has been deleted
     *
     * @param isDeleted if the comment was deleted
     */
    public void setDeleted(final Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * If the comment is abuse
     *
     * @return true if the comment is abuse
     */
    public boolean isIs_abuse() {
        return is_abuse;
    }

    /**
     * If the comment is abuse
     *
     * @param is_abuse if the comment is abuse
     */
    public void setIs_abuse(final Boolean is_abuse) {
        this.is_abuse = is_abuse;
    }

    /**
     * If there are child comments
     *
     * @return true if this comment has child comments
     */
    public boolean isHas_children() {
        return has_children;
    }

    /**
     * If there are child comments
     *
     * @param has_children if this comment has child comments
     */
    public void setHas_children(final Boolean has_children) {
        this.has_children = has_children;
    }

    /**
     * If this comment can be edited
     *
     * @return true if the comment can be edited
     */
    public boolean isCan_edit() {
        return can_edit;
    }

    /**
     * If this comment can be edited
     *
     * @param can_edit if the comment can be edited
     */
    public void setCan_edit(final Boolean can_edit) {
        this.can_edit = can_edit;
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
    public Node getNode() {
        return node;
    }

    /**
     *
     * @param node
     */
    public void setNode(final Node node) {
        this.node = node;
    }

    /**
     *
     * @return
     */
    public String getReplies() {
        return replies;
    }

    /**
     *
     * @param replies
     */
    public void setReplies(final String replies) {
        this.replies = replies;
    }

    /**
     *
     * @return
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the target of this comment, which is the URI (typed as a {@code String}) of the thing being commented on.
     * <p>
     * <em>N.B.</em> this method calls {@link #setTargetUri(URI)} to provide a type-safe way of obtaining the URI.
     * </p>
     *
     * @param target the URI of the thing being commented on
     */
    public void setTarget(final String target) {
        this.target = target;
        setTargetUri(URI.create(target));
    }

    /**
     *
     * @return
     */
    public URI getTargetUri() {
        return targetUri;
    }

    /**
     * Sets the target of this comment, which is the URI (typed as a {@code URI}) of the thing being commented on.
     * <p>
     * <em>N.B.</em> this method is invoked by {@link #setTarget(String)} to provide a type-safe way of obtaining the
     * URI.
     * </p>
     *
     * @param targetUri the URI of the thing being commented on
     */
    public void setTargetUri(final URI targetUri) {
        this.targetUri = targetUri;
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
    public boolean is_ham() {
        return is_ham;
    }

    /**
     *
     * @param is_ham
     */
    public void setIs_ham(final boolean is_ham) {
        this.is_ham = is_ham;
    }

    /**
     *
     * @return
     */
    public boolean isHas_report() {
        return has_report;
    }

    /**
     *
     * @param has_report
     */
    public void setHas_report(final boolean has_report) {
        this.has_report = has_report;
    }

    /**
     *
     * @return
     */
    public String getPage() {
        return page;
    }

    /**
     *
     * @param page
     */
    public void setPage(final String page) {
        this.page = page;
    }
}
