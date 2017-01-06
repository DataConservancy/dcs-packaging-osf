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
import com.github.jasminb.jsonapi.RelType;
import com.github.jasminb.jsonapi.ResolutionStrategy;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import org.dataconservancy.cos.osf.client.support.ContributorHashUriGenerator;
import org.dataconservancy.cos.osf.client.support.ContributorIdSplitter;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.dataconservancy.cos.rdf.support.ToStringTransform;

import java.util.Map;

/**
 * Contributor model for OSF
 * @author khanson
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Type("contributors")
@OwlIndividual(OwlClasses.OSF_CONTRIBUTOR)
public class Contributor {

    /**
     * contributor ID - this corresponds to User ID
     */
    @Id
    @OwlProperty(value = OwlProperties.OSF_HAS_USER, transform = ContributorIdSplitter.class)
    private String id;

    /**
     * Form of Contributor ID used when generating RDF.
     */
    @IndividualUri(transform = ContributorHashUriGenerator.class)
    private String contributorIndividualId;

    /**Whether the user will be included in citations for this node. Default is true.*/
    @OwlProperty(OwlProperties.OSF_IS_BIBLIOGRAPHIC)
    private Boolean isBibliographic;

    /**User permission level. Must be "read", "write", or "admin". Default is "write".*/
    @OwlProperty(value = OwlProperties.OSF_HAS_PERMISSION, transform = ToStringTransform.class)
    private Permission permission;

    /**Gets other links found in data.links:{} section of JSON**/
    @Links
    Map<String, ?> links;

    /**pagination links, applies when list is returned**/
    private PageLinks pageLinks;

    //TODO: the user is embedded in the JSON and won't work as is.
    //TODO: We can use the ID to retrieve it since contribId=userId.
    //TODO: this has been brought to the attention of Brian Geiger (bgeiger@cos.io), logged on the public OSF v2 API,
    //TODO: and created a GitHub issue https://github.com/CenterForOpenScience/osf.io/issues/5590 which has been rolled
    //TODO: into CoS' internal Jira
    //Commenting out for now.

    @Relationship(value = "users", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String userRel;

    private int index;

    /**
     * Contributor's assigned name if contributor hasn't yet claimed account
     */
    private String unregistered_contributor;

    /**
     * The Contributor ID used when emitting RDF.
     * @return the RDF identifier of this contributor
     */
    public String getContributorIndividualId() {
        return contributorIndividualId;
    }

    /**
     * The Contributor ID used when emitting RDF.
     * @param contributorIndividualId the RDF identifier of this contributor
     */
    public void setContributorIndividualId(final String contributorIndividualId) {
        this.contributorIndividualId = contributorIndividualId;
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
    public Boolean isBibliographic() {
        return isBibliographic;
    }

    /**
     *
     * @param isBibliographic
     */
    public void setBibliographic(final Boolean isBibliographic) {
        this.isBibliographic = isBibliographic;
    }

    /**
     *
     * @return
     */
    public Permission getPermission() {
        return permission;
    }

    /**
     *
     * @param permission
     */
    public void setPermission(final Permission permission) {
        this.permission = permission;
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
    public String getUserRel() {
        return userRel;
    }

    /**
     *
     * @param userRel
     */
    public void setUserRel(final String userRel) {
        this.userRel = userRel;
    }

    /**
     *
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     *
     * @param index
     */
    public void setIndex(final int index) {
        this.index = index;
    }

    /**
     *
     * @return
     */
    public String getUnregistered_contributor() {
        return unregistered_contributor;
    }

    /**
     *
     * @param unregistered_contributor
     */
    public void setUnregistered_contributor(final String unregistered_contributor) {
        this.unregistered_contributor = unregistered_contributor;
    }

}
