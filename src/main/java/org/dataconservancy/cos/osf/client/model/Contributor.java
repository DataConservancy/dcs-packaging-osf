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
import com.github.jasminb.jsonapi.annotations.Link;
import com.github.jasminb.jsonapi.annotations.Type;

/**
 * Contributor model for OSF
 * @author khanson
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Type("contributors")
public class Contributor {

	/** contributor ID - this corresponds to User ID*/
    @Id
    private String id;
    
    /**Whether the user will be included in citations for this node. Default is true.*/
    private Boolean isBibliographic;
    
    /**User permission level. Must be "read", "write", or "admin". Default is "write".*/
    private Permission permission;
    	    
	/**Gets other links found in data.links:{} section of JSON**/
	@Link 
	Map<String, ?> links;        
	
	/**pagination links, applies when list is returned**/
	private Links pageLinks;
    
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

	public Links getPageLinks() {
		return pageLinks;
	}

	@JsonProperty("links")
	public void setPageLinks(Links pageLinks) {
		this.pageLinks = pageLinks;
	}

	
}
