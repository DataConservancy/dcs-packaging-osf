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

import java.util.List;
import java.util.Map;

import org.dataconservancy.cos.osf.client.support.DateTimeTransform;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.ont.support.OwlClasses;
import org.dataconservancy.cos.ont.support.OwlProperties;
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
    private org.dataconservancy.cos.osf.client.model.Links pageLinks;

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
	// TODO: @OwlProperty()
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
	// TODO @OwlProperty(OwlProperties.OSF_HAS_ORCID)
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getGiven_name() {
		return given_name;
	}

	public void setGiven_name(String given_name) {
		this.given_name = given_name;
	}

	public String getMiddle_names() {
		return middle_names;
	}

	public void setMiddle_names(String middle_names) {
		this.middle_names = middle_names;
	}

	public String getFamily_name() {
		return family_name;
	}

	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}	

    public String getDate_registered() {
    	if (date_registered != null){
            return date_registered.toString(DATE_TIME_FORMATTER);
    	}
    	else {
    		return null;    		
    	}
    }

    public void setDate_registered(String date_registered) {
    	if (date_registered!=null){
    		this.date_registered = DATE_TIME_FORMATTER.parseDateTime(date_registered);
    	} else {
    		this.date_registered=null;
    	}
    }    

	public String getGitHub() {
		return gitHub;
	}

	public void setGitHub(String gitHub) {
		this.gitHub = gitHub;
	}

	public String getPersonal_website() {
		return personal_website;
	}

	public void setPersonal_website(String personal_website) {
		this.personal_website = personal_website;
	}

	public String getAcademicaInstitution() {
		return academicaInstitution;
	}

	public void setAcademicaInstitution(String academicaInstitution) {
		this.academicaInstitution = academicaInstitution;
	}

	public String getBaiduScholar() {
		return baiduScholar;
	}

	public void setBaiduScholar(String baiduScholar) {
		this.baiduScholar = baiduScholar;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getOrcid() {
		return orcid;
	}

	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}

	public String getResearcherId() {
		return researcherId;
	}

	public void setResearcherId(String researcherId) {
		this.researcherId = researcherId;
	}

	public String getLinkedIn() {
		return linkedIn;
	}

	public void setLinkedIn(String linkedIn) {
		this.linkedIn = linkedIn;
	}

	public String getImpactStory() {
		return impactStory;
	}

	public void setImpactStory(String impactStory) {
		this.impactStory = impactStory;
	}

	public String getScholar() {
		return scholar;
	}

	public void setScholar(String scholar) {
		this.scholar = scholar;
	}

	public String getAcademiaProfileId() {
		return academiaProfileId;
	}

	public void setAcademiaProfileId(String academiaProfileId) {
		this.academiaProfileId = academiaProfileId;
	}

	public String getResearchGate() {
		return researchGate;
	}

	public void setResearchGate(String researchGate) {
		this.researchGate = researchGate;
	}

	public Boolean isActive() {
		return isActive;
	}

	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getNodes() {
		return nodes;
	}

	public void setNodes(String nodes) {
		this.nodes = nodes;
	}

	public List<Institution> getInstitutions() {
		return institutions;
	}

	public void setInstitutions(List<Institution> institutions) {
		this.institutions = institutions;
	}

    public String getRegistrations() {
		return registrations;
	}

	public void setRegistrations(String registrations) {
		this.registrations = registrations;
	}

	public Map<String, ?> getLinks() {
		return links;
	}

	public void setLinks(Map<String, ?> links) {
		this.links = links;
	}

	public org.dataconservancy.cos.osf.client.model.Links getPageLinks() {
		return pageLinks;
	}

    @JsonProperty("links")
	public void setPageLinks(org.dataconservancy.cos.osf.client.model.Links pageLinks) {
		this.pageLinks = pageLinks;
	}
    
}
