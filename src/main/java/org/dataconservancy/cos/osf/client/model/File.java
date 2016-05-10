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
import java.util.Set;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.RelType;
import com.github.jasminb.jsonapi.ResolutionStrategy;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Link;
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
public class File {

	/*unique OSF ID for the file**/
    @Id
    private String id;
    
    /**list of files down next level of file tree*/
    @Relationship(value = "files", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    private List<File> files;

	/**list of versions associated with file*/
    @Relationship(value = "versions", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    private List<FileVersion> versions;

    /**list of comments associated with file*/
    @Relationship(value = "comments", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    private List<Comment> comments;
        
    /**Gets other links found in data.links:{} section of JSON**/
    @Link 
    Map<String, ?> links;        
    
    /**pagination links, applies when list is returned**/
    private Links pageLinks;
    
    /**name of the file or folder; used for display*/
    private String name;    
        
	/**"file" or "folder"*/
    private String kind;

    /**the unix-style path to the file relative to the provider root*/
    private String materialized_path;

    /**timestamp of when this file was created**/
    private DateTime date_created;

    /**timestamp of when this file was last updated*/
    private DateTime date_modified;

    /**storage provider for this file. "osfstorage" if stored on the
     * OSF.  other examples include "s3" for Amazon S3, "googledrive"   */
    private String provider;

    /**node this provider belongs to*/
    private String node;    
    
    /**same as for corresponding WaterButler entity*/
    private String path;

    /**size of file in bytes, null for folders*/
    private Integer size;

    /**list of hashes of the hashes for the files*/
    private Set<Checksum> hashes;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterialized_path() {
        return materialized_path;
    }

    public void setMaterialized_path(String materialized_path) {
        this.materialized_path = materialized_path;
    }

    public String getDate_created() {
    	if (this.date_created!=null) {
    		return this.date_created.toString(DATE_TIME_FORMATTER_ALT);
    	} else {
    		return null;
    	}
    }

    public void setDate_created(String date_created) {
    	if (date_created!=null){
    		this.date_created = DATE_TIME_FORMATTER_ALT.parseDateTime(date_created);
    	} else {
    		this.date_created = null;
    	}
    }

    public String getDate_modified() {
    	if (this.date_modified!=null) {
    		return this.date_modified.toString(DATE_TIME_FORMATTER_ALT);
    	} else {
    		return null;
    	}
    }

    public void setDate_modified(String date_modified) {
    	if (date_modified!=null){
    		this.date_modified = DATE_TIME_FORMATTER_ALT.parseDateTime(date_modified);
    	} else {
    		date_modified=null;
    	}
    }
    
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

	public Set<Checksum> getHashes() {
		return hashes;
	}

	public void setHashes(Set<Checksum> hashes) {
		this.hashes = hashes;
	}	

    public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<FileVersion> getVersions() {
		return versions;
	}

	public void setVersions(List<FileVersion> versions) {
		this.versions = versions;
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
