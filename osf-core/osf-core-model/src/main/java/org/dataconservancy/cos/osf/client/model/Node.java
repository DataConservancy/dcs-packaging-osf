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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.RelType;
import com.github.jasminb.jsonapi.ResolutionStrategy;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;

/**
 * POJO representation of OSF Node in OSF API V2
 * Created by esm on 4/25/16.
 * @author esm
 * @author khanson
 */
@Type("nodes")
@JsonIgnoreProperties(ignoreUnknown = true)
@OwlIndividual(OwlClasses.OSF_NODE)
public class Node extends NodeBase {

    /**List of nodes that are children of this node.*/
    @Relationship(value = "children", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    @OwlProperty(OwlProperties.OSF_HAS_CHILD)
    protected List<Node> children;

    @Relationship(value = "linked_nodes", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String linked_nodes;

    /**Link to list of registrations related to the current node*/
    @Relationship(value = "registrations", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    // TODO: @OwlProperty(....)
    private String registrations;

    /**Link to list of registrations related to the current node*/
    @Relationship(value = "draft_registrations", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String draft_registrations;

    /**Link to list of registrations related to the current node*/
    @Relationship(value = "preprints", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String preprints;

    /**
     *
     * @return
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     *
     * @param children
     */
    public void setChildren(final List<Node> children) {
        this.children = children;
    }

    /**
     *
     * @return
     */
    public String getRegistrations() {
        return registrations;
    }

    /**
     *
     * @param registrations
     */
    public void setRegistrations(final String registrations) {
        this.registrations = registrations;
    }

}
