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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Type;
/**
 * File versions model for OSF
 * @author khanson
 *
 */
@Type("file_versions")
public class FileVersion {

	@Id
	private String id;
	
	/**size of file in bytes*/
	private Integer size;

	/**MIME content-type for the file. May be null if unavailable.*/
	private String content_type;

    
	/**Gets other links found in data.links:{} section of JSON**/
	@Links
	Map<String, ?> links;       
	
	/** pagination links for multiple records*/
	private org.dataconservancy.cos.osf.client.model.Links pageLinks;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public org.dataconservancy.cos.osf.client.model.Links getPageLinks() {
		return pageLinks;
	}

    @JsonProperty("links")
	public void setPageLinks(org.dataconservancy.cos.osf.client.model.Links links) {
		this.pageLinks = links;
	}

	public Map<String, ?> getLinks() {
		return links;
	}

	public void setLinks(Map<String, ?> links) {
		this.links = links;
	}
	
	
	
}
