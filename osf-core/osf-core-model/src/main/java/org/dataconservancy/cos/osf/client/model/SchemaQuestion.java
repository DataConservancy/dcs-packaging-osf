/*
 * Copyright 2017 Johns Hopkins University
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

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class SchemaQuestion {

    private String description;

    private String format;

    private String nav;

    private String qid;

    private String title;

    private String type;

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getFormat() {
        return format;
    }

    /**
     *
     * @param format
     */
    public void setFormat(final String format) {
        this.format = format;
    }

    /**
     *
     * @return
     */
    public String getNav() {
        return nav;
    }

    /**
     *
     * @param nav
     */
    public void setNav(final String nav) {
        this.nav = nav;
    }

    /**
     *
     * @return
     */
    public String getQid() {
        return qid;
    }

    /**
     *
     * @param qid
     */
    public void setQid(final String qid) {
        this.qid = qid;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     */
    public void setType(final String type) {
        this.type = type;
    }
}
