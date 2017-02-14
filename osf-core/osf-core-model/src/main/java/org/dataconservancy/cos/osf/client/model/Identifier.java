/*
 *
 *  * Copyright 2017 Johns Hopkins University
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.dataconservancy.cos.osf.client.model;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import org.dataconservancy.cos.osf.client.support.IdentifierReferentToUriTransform;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;

import java.util.Map;

import static com.github.jasminb.jsonapi.RelType.RELATED;
import static com.github.jasminb.jsonapi.ResolutionStrategy.REF;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@Type("identifiers")
@OwlIndividual(OwlClasses.OSF_IDENTIFIER)
public class Identifier {

    @Id
    @IndividualUri
    private String id;

    @OwlProperty(OwlProperties.OSF_HAS_IDENTIFIER_CATEGORY)
    private String category;

    @OwlProperty(OwlProperties.OSF_HAS_IDENTIFIER_VALUE)
    private String value;

    @Links
    private Map<String, ?> links;

    @OwlProperty(value = OwlProperties.OSF_HAS_IDENTIFIER_REFERENT, transform = IdentifierReferentToUriTransform.class)
    @Relationship(value = "referent", resolve = true, relType = RELATED, strategy = REF)
    private String referent;

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
    public String getCategory() {
        return category;
    }

    /**
     *
     * @param category
     */
    public void setCategory(final String category) {
        this.category = category;
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @param value
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     *
     * @return
     */
    public String getReferent() {
        return referent;
    }

    /**
     *
     * @param referent
     */
    public void setReferent(final String referent) {
        this.referent = referent;
    }

    /**
     *
     * @return
     */
    public Map<String, ?> getLinks() {
        return links;
    }

    /**
     *
     * @param links
     */
    public void setLinks(final Map<String, ?> links) {
        this.links = links;
    }
}
