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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.dataconservancy.cos.osf.packaging.support.AnnotationsProcessor;
import org.dataconservancy.cos.osf.packaging.support.OntologyManager;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.Rdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.Set;

/**
 * Provides a facade for managing OWL individuals and properties over an {@link OntologyManager}.
 * <p>
 * The intent of this class is to represent a graph of objects that will eventually be packaged according to the Data
 * Conservancy Packaging Specification.  In the future this class will support methods that facilitate packaging.
 * At this time, however, it simply offers convenience methods for creating and retrieving OWL individuals and
 * properties from the underlying {@code OntologyManager}.
 * </p>
 * <p>
 * The {@link AnnotationsProcessor} uses this class to create OWL individuals and properties.
 * </p>
 * <p>
 * It may be a little obtuse, and subject to change in the future, but this is how the facades relate to each other and
 * to other classes in this package
 * </p>
 * <pre>
 * Jena OntModel &lt;-- OntologyManager &lt;-- Package Graph &lt;-- AnnotationsProcessor
 *                                                                   /
 *                                       OwlAnnotationsProcessor &lt;--+
 * </pre>
 *
 * <h3>Shortcomings</h3>
 * <ul>
 *   <li>No explicit methods for supporting graph operations or packages</li>
 * </ul>
 */
public class PackageGraph {

    protected static final Logger LOG = LoggerFactory.getLogger(PackageGraph.class);

    protected OntologyManager ontMgr;

    /**
     * A {@link Selector} which selects all statements.
     */
    public final Selector ALWAYS_TRUE_SELECTOR = new Selector() {
        @Override
        public boolean isSimple() {
            return false;
        }

        @Override
        public Resource getSubject() {
            return null;
        }

        @Override
        public Property getPredicate() {
            return null;
        }

        @Override
        public RDFNode getObject() {
            return null;
        }

        @Override
        public boolean test(Statement statement) {
            return true;
        }
    };

    public PackageGraph(OntologyManager ontMgr) {
        this.ontMgr = ontMgr;
    }

    /**
     * Create an anonymous individual in the model.  Each invocation results in a new instance of an OWL individual
     * added to the model.
     * <h3>Implementation note</h3>
     * This method returns the actual {@code Individual} instead of a URI, because anonymous Individuals cannot be
     * referenced by any kind of string identifier.
     *
     * @param owlClass the class the created individual will be a member of
     * @return the OWL individual
     */
    public Individual newIndividual(OwlClasses owlClass) {
        Individual individual = ontMgr.individual(owlClass.ns(), owlClass.localname());
        return individual;
    }

    /**
     * Create an individual.  Invoking this method with the same OWL class and individual id will return the existing
     * instance of the {@code Individual}, otherwise a new instance is created.
     *
     * @param owlClass the class the created individual will be a member of
     * @param individualId the identifier to be assigned to the newly created individual
     * @return the OWL individual
     */
    public Individual newIndividual(OwlClasses owlClass, Object individualId) {
        Individual individual =  ontMgr.individual(individualId.toString(), owlClass.ns(), owlClass.localname());
        return individual;
    }

    /**
     * Add an individual as an object of a property.  Convenience method for adding a new individual in one method call,
     * then setting that individual as the object of a property in the second.  This method will create a new
     * individual for {@code objectIndividualUri}, and use it as a object resource for the {@code propertyUri}.
     *
     * @param subjectIndividualUri the subject identifying an OWL individual
     * @param propertyUri the predicate relating the {@code subjectIndividualUri} to the {@code objectIndividualUri}
     * @param objectIndividualUri the object identifying an OWL individual
     */
    public void addIndividual(String subjectIndividualUri, String propertyUri, String objectIndividualUri) {
        ontMgr.individual(subjectIndividualUri).addProperty(ontMgr.objectProperty(propertyUri),
                ontMgr.individual(objectIndividualUri));
    }

    /**
     * Add an individual as an object of a property.  Convenience method for adding a new individual in one method call,
     * then setting that individual as the object of a property in the second.  This method will create a new
     * individual for {@code objectIndividualUri}, and use it as a object resource for the {@code propertyUri}.
     *
     * @param individual the subject, an OWL individual
     * @param propertyUri the predicate relating the {@code individual} to the {@code objectIndividualUri}
     * @param objectIndividualUri the object identifying an OWL individual
     */
    public void addIndividual(Individual individual, String propertyUri, String objectIndividualUri) {
        individual.addProperty(ontMgr.objectProperty(propertyUri), ontMgr.individual(objectIndividualUri));
    }

    /**
     * Adds an anonymous individual to the model.
     *
     * @param individualUri the subject identifying an OWL individual
     * @param propertyUri the predicate relating the {@code individualUri} to the {@code anonIndividual}
     * @param anonIndividual the object, an anonymous OWL individual
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
     * Adds an anonymous individual to the model.
     *
     * @param individual the subject, an OWL individual
     * @param propertyUri the predicate relating the {@code individualUri} to the {@code anonIndividual}
     * @param anonIndividual the object, an anonymous OWL individual
     */
    public void addAnonIndividual(Individual individual, String propertyUri, Individual anonIndividual) {
        if (anonIndividual.getURI() != null) {
            throw new IllegalArgumentException(String.format("Found URI on an anonymous individual: '%s'.  OWL " +
                            "Individuals withURIs must be added by invoking 'newIndividual(OwlClasses, Object)'",
                    anonIndividual.getURI()));
        }
        individual.addProperty(ontMgr.objectProperty(propertyUri), anonIndividual);
    }

    /**
     * Adds a literal as the object of the individual.
     *
     * @param individualUri the subject identifying an OWL individual
     * @param propertyUri the predicate relating the {@code individualUri} to the {@code literal}
     * @param literal the object, a literal
     */
    public void addLiteral(String individualUri, String propertyUri, Object literal) {
        Individual individual = ontMgr.individual(individualUri);
        individual.addLiteral(ontMgr.datatypeProperty(propertyUri), literal);
    }

    /**
     * Adds a literal as the object of the individual.
     *
     * @param individual the subject, an OWL individual
     * @param propertyUri the predicate relating the {@code individualUri} to the {@code literal}
     * @param literal the object, a literal
     */
    public void addLiteral(Individual individual, String propertyUri, Object literal) {
        individual.addLiteral(ontMgr.datatypeProperty(propertyUri), literal);
    }

    /**
     * Adds a resource as the object of the individual.
     *
     * @param individualUri the subject identifying an OWL individual
     * @param propertyUri the predicate relating the {@code individualUri} to the {@code resource}
     * @param resource the object, a resource
     */
    public void addResource(String individualUri, String propertyUri, Resource resource) {
        Individual individual = ontMgr.individual(individualUri);
        individual.addProperty(ontMgr.objectProperty(propertyUri), resource);
    }

    /**
     * Adds a resource as the object of the individual.
     *
     * @param individual the subject, an OWL individual
     * @param propertyUri the predicate relating the {@code individualUri} to the {@code resource}
     * @param resource the object, a resource
     */
    public void addResource(Individual individual, String propertyUri, Resource resource) {
        individual.addProperty(ontMgr.objectProperty(propertyUri), resource);
    }

    /**
     * Obtain the OWL individuals that have been added to the graph.
     *
     * @return a {@code Set} of OWL individuals.
     */
    public Set<Individual> individuals() {
        return ontMgr.getOntModel().listIndividuals().toSet();
    }

    /**
     * Serialize the statements in this graph to the supplied output stream.
     *
     * @param out output stream receiving the serialized graph
     * @param format the format the statements should be serialized in
     * @param selector used to select the statements to be serialized
     */
    public void serialize(OutputStream out, RDFFormat format, Selector selector) {
        if (selector != ALWAYS_TRUE_SELECTOR) {
            Model selected = ModelFactory.createDefaultModel();
            selected.setNsPrefixes(Rdf.Ns.PREFIXES);
            ontMgr.getOntModel().listStatements(selector).forEachRemaining(statement -> {
                LOG.debug("Statement selected for Package serialization: {} {} {}",
                        statement.getSubject(), statement.getPredicate(), statement.getObject());
                selected.add(statement);
            });
            RDFDataMgr.write(out, selected, format);
        } else {
            RDFDataMgr.write(out, ontMgr.getOntModel(), format);
        }
    }

}
