/*
 * Copyright 2016 Johns Hopkins University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dataconservancy.cos.osf.client.model;

import com.github.jasminb.jsonapi.RelType;
import com.github.jasminb.jsonapi.ResolutionStrategy;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import org.dataconservancy.cos.osf.client.support.DateTimeTransform;
import org.dataconservancy.cos.osf.client.support.JodaSupport;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.joda.time.DateTime;

import java.util.Map;

import static org.dataconservancy.cos.osf.client.support.JodaSupport.DATE_TIME_FORMATTER_ALT;

/**
 * Encapsulates an event in the OSF.  Events in the OSF are expressed as the JSON-API type "logs".
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@Type("logs")
@OwlIndividual(OwlClasses.OSF_EVENT)
public class Log {

    @Id
    @IndividualUri
    private String id;

    @OwlProperty(value = OwlProperties.OSF_HAS_LOG_DATE, transform = DateTimeTransform.class)
    private DateTime date;

    // TODO: verify that osf:eventTarget is the correct semantics for 'node'
    @Relationship(value = "node", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    @OwlProperty(OwlProperties.OSF_HAS_LOG_TARGET)
    private String node;

    // TODO: verify that osf:eventSource is the correct semantics for 'original_node'
    @Relationship(value = "original_node", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    @OwlProperty(OwlProperties.OSF_HAS_LOG_SOURCE)
    private String original_node;

    @Relationship(value = "contributors", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String contributors;

    @Relationship(value = "user", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String user;

    @OwlProperty(OwlProperties.OSF_HAS_LOG_ACTION)
    private String action;

    private Map<String, ?> params;

    /**
     *
     * @return
     */
    public String getAction() {
        return action;
    }

    /**
     *
     * @param action
     */
    public void setAction(final String action) {
        this.action = action;
    }

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
    public String getDate() {
        if (this.date != null) {
            return date.toString(DATE_TIME_FORMATTER_ALT);
        }
        return null;
    }

    /**
     *
     * @param date
     */
    public void setDate(final String date) {
        if (date != null) {
            this.date = JodaSupport.parseDateTime(date);
        }
    }

    /**
     *
     * @return
     */
    public String getNode() {
        return node;
    }

    /**
     *
     * @param node
     */
    public void setNode(final String node) {
        this.node = node;
    }

    /**
     *
     * @return
     */
    public String getOriginal_node() {
        return original_node;
    }

    /**
     *
     * @param original_node
     */
    public void setOriginal_node(final String original_node) {
        this.original_node = original_node;
    }

    /**
     *
     * @return
     */
    public String getContributors() {
        return contributors;
    }

    /**
     *
     * @param contributors
     */
    public void setContributors(final String contributors) {
        this.contributors = contributors;
    }

    /**
     *
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @param user
     */
    public void setUser(final String user) {
        this.user = user;
    }

    /**
     *
     * @return
     */
    public Map<String, ?> getParams() {
        return params;
    }

    /**
     *
     * @param params
     */
    public void setParams(final Map<String, ?> params) {
        this.params = params;
    }
}
