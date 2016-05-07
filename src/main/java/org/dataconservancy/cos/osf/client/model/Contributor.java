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
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

/**
 * OSF Contributor model 
 * @author khanson
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Type("contributors")
public class Contributor {

	/** contributor ID - this corresponds to User*/
    @Id
    private String id;
    
    /**Whether the user will be included in citations for this node. Default is true.*/
    private Boolean isBibliographic;
    
    /**User permission level. Must be "read", "write", or "admin". Default is "write".*/
    private Permission permission;
    
    //TODO: the user is embedded in the JSON, or we can use the ID to retrieve it. Comenting out for now.
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

	
}
