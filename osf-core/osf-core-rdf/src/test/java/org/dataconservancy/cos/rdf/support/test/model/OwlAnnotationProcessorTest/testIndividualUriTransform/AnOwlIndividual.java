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
package org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testIndividualUriTransform;

import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.support.OwlClasses;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@OwlIndividual(OwlClasses.OSF_BO)
public class AnOwlIndividual {

    @IndividualUri(transform = TransformerProbeWrapper.class)
    public String ID = "AnOwlIndividualId";


}
