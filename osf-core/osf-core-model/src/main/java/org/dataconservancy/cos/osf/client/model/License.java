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

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Type;
import org.dataconservancy.cos.osf.client.support.LicenseHashUriGenerator;
import org.dataconservancy.cos.osf.client.support.TruncatingTransform;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;

import java.util.List;
import java.util.Map;

/**
 * Encapsulates a license for a OSF Node or Registration.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@Type("licenses")
@OwlIndividual(value = OwlClasses.OSF_LICENSE)
public class License {

    @Id
    @IndividualUri(transform = LicenseHashUriGenerator.class)
    private String id;

    @OwlProperty(value = OwlProperties.OSF_HAS_LICENSE_TEXT, transform = TruncatingTransform.class)
    private String text;

    @OwlProperty(OwlProperties.OSF_HAS_LICENSE_NAME)
    private String name;

    private List<String> required_fields;

    @Links
    private Map<String, ?> links;

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
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public List<String> getRequired_fields() {
        return required_fields;
    }

    /**
     *
     * @param required_fields
     */
    public void setRequired_fields(final List<String> required_fields) {
        this.required_fields = required_fields;
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
