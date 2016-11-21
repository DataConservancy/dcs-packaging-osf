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
package org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testClassHierarchy;

import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;

/**
 * Created by esm on 6/9/16.
 */
@OwlIndividual(OwlClasses.OSF_FILE)
public class Child {

    @IndividualUri
    private String id;

    @OwlProperty(OwlProperties.OSF_HAS_CHILD)
    private SomeOtherClass foo = new SomeOtherClass();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
