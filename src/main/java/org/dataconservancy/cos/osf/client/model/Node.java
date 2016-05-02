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

import com.github.jasminb.jsonapi.JSONAPISpecConstants;
import com.github.jasminb.jsonapi.RelType;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import org.dataconservancy.cos.osf.client.support.JodaSupport;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import static org.dataconservancy.cos.osf.client.support.JodaSupport.DATE_TIME_FORMATTER;

/**
 * Created by esm on 4/25/16.
 */
@Type("nodes")
public class Node {


    @Relationship(value = "files", resolve = true, relType = RelType.RELATED)
    private List<StorageProvider> files;

    private Links links;

    private String category;

    private String description;

    private String title;

    private List<String> tags;

    @Id
    private String id;

    @Relationship(value = "root", resolve = true, relType = RelType.RELATED)
    private Node root;

    private List<Permission> current_user_permissions;

    private DateTime date_created;

    private DateTime date_modified;

    private boolean isFork;

    private boolean isPublic;

    private boolean isRegistration;

    private boolean isCollection;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public List<Permission> getCurrent_user_permissions() {
        return current_user_permissions;
    }

    public void setCurrent_user_permissions(List<Permission> current_user_permissions) {
        this.current_user_permissions = current_user_permissions;
    }

    public String getDate_created() {
        return date_created.toString(DATE_TIME_FORMATTER);
    }

    public void setDate_created(String date_created) {
        this.date_created = DATE_TIME_FORMATTER.parseDateTime(date_created);
    }

    public String getDate_modified() {
        return this.date_modified.toString(DATE_TIME_FORMATTER);
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = DATE_TIME_FORMATTER.parseDateTime(date_modified);
    }

    public boolean isFork() {
        return isFork;
    }

    public void setFork(boolean fork) {
        isFork = fork;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isRegistration() {
        return isRegistration;
    }

    public void setRegistration(boolean registration) {
        isRegistration = registration;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<StorageProvider> getFiles() {
        return files;
    }

    public void setFiles(List<StorageProvider> files) {
        this.files = files;
    }
}
