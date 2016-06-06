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
package org.dataconservancy.cos.osf.packaging;

import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.Resource;
import org.dataconservancy.cos.osf.packaging.ont.OntologyManager;
import org.dataconservancy.cos.rdf.support.OwlClasses;

/**
 * Created by esm on 6/5/16.
 */
public class PackageGraph {

    private OntologyManager ontMgr;

    public PackageGraph(OntologyManager ontMgr) {
        this.ontMgr = ontMgr;
    }

    /**
     * Create an anonymous individual in the model.  Each invocation results in a new instance of an OWL individual
     * added to the model.
     * <h4>Implementation note</h4>
     * This method returns the actual {@code Individual} instead of a URI, because anonymous Individuals cannot be
     * referenced by any kind of string identifier.
     *
     * @param owlClass
     * @return
     */
    public Individual newIndividual(OwlClasses owlClass) {
        Individual individual = ontMgr.individual(owlClass.ns(), owlClass.localname());
        return individual;
    }

    /**
     * Create an individual.  Invoking this method with the same OWL class and individual id will return the existing
     * instance of the {@code Individual}, otherwise a new instance is created.
     *
     * @param owlClass
     * @param individualId
     * @return
     */
    public String newIndividual(OwlClasses owlClass, Object individualId) {
        Individual individual =  ontMgr.individual(individualId.toString(), owlClass.ns(), owlClass.localname());
        return individual.getURI();
    }

    /**
     * Add an individual as an object of a property.  Convenience method for adding a new individual in one method call,
     * then setting that individual as the object of a property in the second.  This method will create a new
     * individual for {@code objectIndividualUri}, and use it as a object resource for the {@code propertyUri}.
     *
     * @param subjectIndividualUri
     * @param propertyUri
     * @param objectIndividualUri
     */
    public void addIndividual(String subjectIndividualUri, String propertyUri, String objectIndividualUri) {
        ontMgr.individual(subjectIndividualUri).addProperty(ontMgr.objectProperty(propertyUri),
                ontMgr.individual(objectIndividualUri));
    }

    /**
     * Adds an anonymous individual to the model.
     *
     * @param individualUri
     * @param propertyUri
     * @param anonIndividual
     */
    public void addAnonIndividual(String individualUri, String propertyUri, Individual anonIndividual) {
        if (anonIndividual.getURI() != null) {
            throw new IllegalArgumentException(String.format("Found URI on an anonymous individual: '%s'.  OWL " +
                    "Individuals withURIs must be added by invoking 'newIndividual(OwlClasses, Object)'",
                    anonIndividual.getURI()));
        }
        ontMgr.individual(individualUri).addProperty(ontMgr.objectProperty(propertyUri), anonIndividual);
    }

    /**
     * Adds a literal as the object of the individual.
     *
     * @param individualUri
     * @param propertyUri
     * @param literal
     */
    public void addLiteral(String individualUri, String propertyUri, Object literal) {
        Individual individual = ontMgr.individual(individualUri);
        individual.addLiteral(ontMgr.datatypeProperty(propertyUri), literal);
    }

    /**
     * Adds a resource as the object of the individual.
     *
     * @param individualUri
     * @param propertyUri
     * @param resource
     */
    public void addResource(String individualUri, String propertyUri, Resource resource) {
        Individual individual = ontMgr.individual(individualUri);
        individual.addProperty(ontMgr.objectProperty(propertyUri), resource);
    }



}
