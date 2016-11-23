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
import com.github.jasminb.jsonapi.annotations.Type;
import org.dataconservancy.cos.osf.client.support.LicenseHashUriGenerator;
import org.dataconservancy.cos.osf.client.support.TruncatingTransform;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;

/**
 * Encapsulates a license for a OSF Node or Registration.
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
