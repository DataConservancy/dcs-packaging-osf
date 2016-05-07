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

import org.joda.time.DateTime;

import com.github.jasminb.jsonapi.RelType;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

/**
 * POJO representation of OSF Node in OSF API V2
 * Created by esm on 4/25/16.
 * Updated: khanson 2016-05-06: added comments
 * @author esm
 * @author khanson
 */
@Type("nodes")
public class Node {

	/**List of nodes that are children of this node.*/
	@Relationship(value = "children", resolve = true, relType = RelType.RELATED)
	private List<Node> children;	

	/**List of users who are contributors to this node. */
	//@Relationship(value = "contributors", resolve = true, relType = RelType.RELATED)
	//private List<Contributor> contributors;

	/**If this node is a child node of another node, the parent's canonical endpoint will 
	 * be available in the /parent/links/related/href key. Otherwise, it will be null.*/
    //@Relationship(value = "parent", resolve = true, relType = RelType.RELATED)
    //private Node parent;

	/**Root node if you walk up the tree of projects/components.*/
    //@Relationship(value = "root", resolve = true, relType = RelType.RELATED)
    //private Node root;
	
	/**List of top-level folders (actually cloud-storage providers) associated with this node.
	 * This is the starting point for accessing the actual files stored within this node.*/
    @Relationship(value = "files", resolve = true, relType = RelType.RELATED)
    private List<File> files;

    /**If this node was forked from another node, the canonical endpoint of the node that was 
     * forked from will be available in the /forked_from/links/related/href key. Otherwise, it will be null.*/
    //@Relationship(value = "forked_from", resolve = true, relType = RelType.RELATED)
    //private Node forked_from;
        
    /**Pagination links**/
    private Links links;

    /**Node category, must be one of the allowed values.*/
    private Category category;
    
    /**description of the node*/
    private String description;

    /**title of project or component*/
    private String title;

    /**list of tags that describe the node*/
    private List<String> tags;

    /**node id*/
    @Id
    private String id;

    /**List of strings representing the permissions for the current user on this node*/
    private List<Permission> current_user_permissions;

    /**timestamp that the node was created*/
    private DateTime date_created;

    /**timestamp when the node was last updated*/
    private DateTime date_modified;

    /**is this node a fork of another node?*/
    private Boolean isFork;

    /**as this node been made publicly-visible?*/
    private Boolean isPublic;

    /**has this project been registered?*/
    private Boolean isRegistration;

    /**is project a collection?*/
    private Boolean isCollection;
   
    
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}
/*
    public List<Contributor> getContributors() {
		return contributors;
	}

	public void setContributors(List<Contributor> contributors) {
		this.contributors = contributors;
	}*/

	public List<Permission> getCurrent_user_permissions() {
        return current_user_permissions;
    }

    public void setCurrent_user_permissions(List<Permission> current_user_permissions) {
        this.current_user_permissions = current_user_permissions;
    }

    public String getDate_created() {
    	if (this.date_created!=null) {
    		return this.date_created.toString(DATE_TIME_FORMATTER);
    	} else {
    		return null;
    	}
    }

    public void setDate_created(String date_created) {
    	if (date_created!=null){
    		this.date_created = DATE_TIME_FORMATTER.parseDateTime(date_created);
    	} else {
    		this.date_created = null;
    	}
    }

    public String getDate_modified() {
    	if (this.date_modified!=null) {
    		return this.date_modified.toString(DATE_TIME_FORMATTER);
    	} else {
    		return null;
    	}
    }

    public void setDate_modified(String date_modified) {
    	if (date_modified!=null){
    		this.date_modified = DATE_TIME_FORMATTER.parseDateTime(date_modified);
    	} else {
    		date_modified=null;
    	}
    }

    public Boolean isFork() {
        return isFork;
    }

    public void setFork(Boolean fork) {
        isFork = fork;
    }

    public Boolean isPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean isRegistration() {
        return isRegistration;
    }

    public void setRegistration(Boolean registration) {
        isRegistration = registration;
    }

    public Boolean isCollection() {
        return isCollection;
    }

    public void setCollection(Boolean collection) {
        isCollection = collection;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
