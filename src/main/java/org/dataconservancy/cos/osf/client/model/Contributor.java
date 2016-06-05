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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Type;
import org.dataconservancy.cos.rdf.annotations.AnonIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.dataconservancy.cos.rdf.support.ToStringTransform;

/**
 * Contributor model for OSF
 * @author khanson
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Type("contributors")
public class Contributor {

	/** contributor ID - this corresponds to User ID*/
    @Id
	@OwlProperty(OwlProperties.OSF_HAS_USER)
    private String id;
    
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
	private org.dataconservancy.cos.osf.client.model.Links pageLinks;
    
    //TODO: the user is embedded in the JSON and won't work as is.  We can use the ID to retrieve it since contribId=userId.
	//TODO: this has been brought to the attention of Brian Geiger (bgeiger@cos.io), logged on the public OSF v2 API,
	//TODO: and created a GitHub issue https://github.com/CenterForOpenScience/osf.io/issues/5590 which has been rolled
	//TODO: into CoS' internal Jira
    //Commenting out for now.
    //User user;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
    
	public Boolean isBibliographic() {
		return isBibliographic;
	}

	public void setBibliographic(Boolean isBibliographic) {
		this.isBibliographic = isBibliographic;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
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
