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
import org.dataconservancy.cos.osf.client.support.JodaSupport;
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
 * OSF User model
 * @author khanson
 */
@Type("users")
@JsonIgnoreProperties(ignoreUnknown = true)
@OwlIndividual(OwlClasses.OSF_USER)
public class User {

    /** Unique OSF identifier for User ID */
    @Id
    @IndividualUri
    private String id;

    /**Link to list of nodes associated with the User*/
    @Relationship(value = "nodes", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String nodes;

    /**Institutions associated with the User*/
    @Relationship(value = "institutions", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    private List<Institution> institutions;

    /**Link to list of registrations associated with the User*/
    @Relationship(value = "registrations", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String registrations;

    /**Gets other links found in data.links:{} section of JSON**/
    @Links
    Map<String, ?> links;

    /**pagination links, applies when list is returned**/
    private PageLinks pageLinks;

    /** full name of the user; used for display*/
    @OwlProperty(OwlProperties.OSF_HAS_FULLNAME)
    private String full_name;

    /** given name of the user; for bibliographic citations*/
    @OwlProperty(OwlProperties.OSF_HAS_GIVENNAME)
    private String given_name;

    /** middle name of user; for bibliographic citations */
    // TODO: check- should this be a collection?
    @OwlProperty(OwlProperties.OSF_HAS_MIDDLENAMES)
    private String middle_names;

    /** family name of user; for bibliographic citations*/
    @OwlProperty(OwlProperties.OSF_HAS_HASFAMILYNAME)
    private String family_name;

    /** suffix of user's name for bibliographic citations*/
    @OwlProperty(OwlProperties.OSF_HAS_SUFFIX)
    private String suffix;

    /** timestamp when the user's account was created*/
    @OwlProperty(value = OwlProperties.OSF_HAS_DATEUSERREGISTERED, transform = DateTimeTransform.class)
    private DateTime date_registered;

    /**github account name (not full url) e.g. "karenhanson" **/
    //TODO @OwlProperty(OwlProperties.OSF_HAS_GITHUB)
    private String gitHub;

    /**personal website**/
    @OwlProperty(OwlProperties.OSF_HAS_PERSONALWEBSITE)
    private String personal_website;

    /**academic institution name*/
    @OwlProperty(OwlProperties.OSF_HAS_ACADEMICINSTITUTION)
    private String academicaInstitution;

    /**Baidu Scholar ID*/
    @OwlProperty(OwlProperties.OSF_HAS_BAIDUID)
    private String baiduScholar;

    /**twitter handle e.g. bobsmith*/
    @OwlProperty(OwlProperties.OSF_HAS_TWITTER)
    private String twitter;

    /**orcid id e.g. 0000-1234-1234-1234"*/
    @OwlProperty(OwlProperties.OSF_HAS_ORCID)
    private String orcid;

    /**Thomson Reuters Researcher ID e.g. H-9999-9999 **/
    @OwlProperty(OwlProperties.OSF_HAS_RESEARCHERID)
    private String researcherId;

    /** linkedin profile path (not full url) e.g. "in/karenlhanson" */
    @OwlProperty(OwlProperties.OSF_HAS_LINKEDIN)
    private String linkedIn;

    /** Impact Story profile ID (not full url) e.g. bobsmith*/
    @OwlProperty(OwlProperties.OSF_HAS_IMPACTSTORY)
    private String impactStory;

    /** Google scholar profile ID (not full url) e.g. bobsmith*/
    @OwlProperty(OwlProperties.OSF_HAS_SCHOLAR)
    private String scholar;

    /** Academia Profile ID */
    @OwlProperty(OwlProperties.OSF_HAS_ACADEMICPROFILEID)
    private String academiaProfileId;

    /**ResearchGate ID*/
    @OwlProperty(OwlProperties.OSF_HAS_RESEARCHGATE)
    private String researchGate;

    /** User active?*/
    @OwlProperty(OwlProperties.OSF_IS_ACTIVE)
    private Boolean isActive;

    /** user timezone */
    // TODO? @OwlProperty(OwlProperties.OSF_TIMEZONE)
    private String timezone;

    /**locale e.g. en_US*/
    @OwlProperty(OwlProperties.OSF_HAS_LOCALE)
    private String locale;

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
    public String getFull_name() {
        return full_name;
    }

    /**
     *
     * @param full_name
     */
    public void setFull_name(final String full_name) {
        this.full_name = full_name;
    }

    /**
     *
     * @return
     */
    public String getGiven_name() {
        return given_name;
    }

    /**
     *
     * @param given_name
     */
    public void setGiven_name(final String given_name) {
        this.given_name = given_name;
    }

    /**
     *
     * @return
     */
    public String getMiddle_names() {
        return middle_names;
    }

    /**
     *
     * @param middle_names
     */
    public void setMiddle_names(final String middle_names) {
        this.middle_names = middle_names;
    }

    /**
     *
     * @return
     */
    public String getFamily_name() {
        return family_name;
    }

    /**
     *
     * @param family_name
     */
    public void setFamily_name(final String family_name) {
        this.family_name = family_name;
    }

    /**
     *
     * @return
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     *
     * @param suffix
     */
    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

    /**
     *
     * @return
     */
    public String getDate_registered() {
        if (date_registered != null) {
            return date_registered.toString(DATE_TIME_FORMATTER_ALT);
        } else {
            return null;
        }
    }

    /**
     *
     * @param date_registered
     */
    public void setDate_registered(final String date_registered) {
        if (date_registered != null) {
            this.date_registered = JodaSupport.parseDateTime(date_registered);
        }
    }

    /**
     *
     * @return
     */
    public String getGitHub() {
        return gitHub;
    }

    /**
     *
     * @param gitHub
     */
    public void setGitHub(final String gitHub) {
        this.gitHub = gitHub;
    }

    /**
     *
     * @return
     */
    public String getPersonal_website() {
        return personal_website;
    }

    /**
     *
     * @param personal_website
     */
    public void setPersonal_website(final String personal_website) {
        this.personal_website = personal_website;
    }

    /**
     *
     * @return
     */
    public String getAcademicaInstitution() {
        return academicaInstitution;
    }

    /**
     *
     * @param academicaInstitution
     */
    public void setAcademicaInstitution(final String academicaInstitution) {
        this.academicaInstitution = academicaInstitution;
    }

    /**
     *
     * @return
     */
    public String getBaiduScholar() {
        return baiduScholar;
    }

    /**
     *
     * @param baiduScholar
     */
    public void setBaiduScholar(final String baiduScholar) {
        this.baiduScholar = baiduScholar;
    }

    /**
     *
     * @return
     */
    public String getTwitter() {
        return twitter;
    }

    /**
     *
     * @param twitter
     */
    public void setTwitter(final String twitter) {
        this.twitter = twitter;
    }

    /**
     *
     * @return
     */
    public String getOrcid() {
        return orcid;
    }

    /**
     *
     * @param orcid
     */
    public void setOrcid(final String orcid) {
        this.orcid = orcid;
    }

    /**
     *
     * @return
     */
    public String getResearcherId() {
        return researcherId;
    }

    /**
     *
     * @param researcherId
     */
    public void setResearcherId(final String researcherId) {
        this.researcherId = researcherId;
    }

    /**
     *
     * @return
     */
    public String getLinkedIn() {
        return linkedIn;
    }

    /**
     *
     * @param linkedIn
     */
    public void setLinkedIn(final String linkedIn) {
        this.linkedIn = linkedIn;
    }

    /**
     *
     * @return
     */
    public String getImpactStory() {
        return impactStory;
    }

    /**
     *
     * @param impactStory
     */
    public void setImpactStory(final String impactStory) {
        this.impactStory = impactStory;
    }

    /**
     *
     * @return
     */
    public String getScholar() {
        return scholar;
    }

    /**
     *
     * @param scholar
     */
    public void setScholar(final String scholar) {
        this.scholar = scholar;
    }

    /**
     *
     * @return
     */
    public String getAcademiaProfileId() {
        return academiaProfileId;
    }

    /**
     *
     * @param academiaProfileId
     */
    public void setAcademiaProfileId(final String academiaProfileId) {
        this.academiaProfileId = academiaProfileId;
    }

    /**
     *
     * @return
     */
    public String getResearchGate() {
        return researchGate;
    }

    /**
     *
     * @param researchGate
     */
    public void setResearchGate(final String researchGate) {
        this.researchGate = researchGate;
    }

    /**
     *
     * @return
     */
    public Boolean isActive() {
        return isActive;
    }

    /**
     *
     * @param isActive
     */
    public void setActive(final Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     *
     * @return
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     *
     * @param timezone
     */
    public void setTimezone(final String timezone) {
        this.timezone = timezone;
    }

    /**
     *
     * @return
     */
    public String getLocale() {
        return locale;
    }

    /**
     *
     * @param locale
     */
    public void setLocale(final String locale) {
        this.locale = locale;
    }

    /**
     *
     * @return
     */
    public String getNodes() {
        return nodes;
    }

    /**
     *
     * @param nodes
     */
    public void setNodes(final String nodes) {
        this.nodes = nodes;
    }

    /**
     *
     * @return
     */
    public List<Institution> getInstitutions() {
        return institutions;
    }

    /**
     *
     * @param institutions
     */
    public void setInstitutions(final List<Institution> institutions) {
        this.institutions = institutions;
    }

    /**
     *
     * @return
     */
    public String getRegistrations() {
        return registrations;
    }

    /**
     *
     * @param registrations
     */
    public void setRegistrations(final String registrations) {
        this.registrations = registrations;
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

}
