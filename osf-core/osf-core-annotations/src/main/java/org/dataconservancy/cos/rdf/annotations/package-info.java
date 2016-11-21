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

/**
 * Provides annotations used to map Java classes and members to OWL individuals and properties.
 * <p>
 * Java classes annotated with {@code @OwlIndividual} will have instances of those classes represented in RDF as an
 * OWL individual.  Each class possessing the {@code @OwlIndividual} annotation should have exactly one field annotated
 * {@code @IndividualUri}, to be used as the resource identifier of the {@code @OwlIndividual}.
 * </p>
 * <p>
 * Class members annotated with {@code @OwlProperties} will be represented in RDF as an OWL Datatype property or Object
 * property, depending on the value of the annotation.  The subject of the property will be the {@code @OwlIndividual},
 * and the object of the property will be the value of the annotated field.
 * </p>
 * <p>
 * The values of class members may be transformed prior to being represented in RDF.  The {@code @IndividualUri} and
 * {@code @OwlProperties} annotation possess a {@code transform} attribute, which specifies a
 * {@code Class<Function<Object,String>>} to be applied to the field value prior to being serialized as RDF.
 * </p>
 */
package org.dataconservancy.cos.rdf.annotations;