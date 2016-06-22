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

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.dataconservancy.cos.rdf.support.Rdf;

import java.io.IOException;
import java.net.URL;

/**
 * Provides a facade for creating OWL Individuals and Properties over a Jena {@code OntModel}.
 * <p>
 * The purpose of this class is to insure consistency between the ontology as expressed on disk, and the
 * encapsulated {@code OntModel}. For example, many methods in this class will throw an {@code IllegalArgumentException}
 * if the URI referencing an OWL class or property doesn't exist in the underlying model.  This prevents calling code
 * from creating OWL individuals or properties that don't exist in the underlying ontology, and encourages the developer
 * to keep the ontology as expressed on disk aligned with the classes and properties used in the code.
 * </p>
 * <p>
 * The {@link PackageGraph} uses this class to create OWL individuals and properties.
 * </p>
 * <p>
 * It may be a little obtuse, and subject to change in the future, but this is how the facades relate to each other and
 * to other classes in this package
 * </p>
 * <pre>
 * Jena OntModel <-- OntologyManager <-- Package Graph <-- AnnotationsProcessor
 *                                                                   /
 *                                       OwlAnnotationsProcessor <--+
 * </pre>
 *
 * <h4>Shortcomings</h4>
 * <ul>
 *   <li>No explicit support for sub classes of {@code ObjectProperty} (TransitiveProperty, SymmetricProperty, etc.)</li>
 *   <li>No support for AnnotationProperty or FunctionalProperty or their sub classes</li>
 *   <li>Unsure (untested) if {@code owl:imports} are followed in the supplied ontology resource</li>
 * </ul>
 */
public class OntologyManager {

    /**
     * The class path resource containing the OSF ontology.  This is the ontology that populates the backing OntModel
     * when the no-arg constructor is called.
     */
    public static final String DEFAULT_ONTOLOGY_RESOURCE = "/ontology/osf-model.rdf";

    /**
     * The base URI resources using relative URIs in the backing OntModel.
     */
    public static final String OSF_BASE_URI = "http://www.dataconservancy.org/osf-business-object-model#";

    /**
     * The serialization of the {@link #DEFAULT_ONTOLOGY_RESOURCE OSF ontology}
     */
    public static final String RDF_XML = "RDF/XML";

    /**
     * The mutable {@code OntModel} that contains the triples for the ontology, plus any triples added by clients of
     * this {@code OntologyManager}
     */
    private OntModel ontModel;

    /**
     * This is a read-only {@code OntModel} that contains the triples for the ontology only.  Triples that are added
     * by clients of this {@code OntologyManager} will be placed in the {@link #ontModel mutable OntModel}.
     */
    private OntModel readOnlyOntModel;

    /**
     * Constructs a facade around the OSF ontology.
     */
    public OntologyManager() {
        this(DEFAULT_ONTOLOGY_RESOURCE, OSF_BASE_URI, RDF_XML);
    }

    /**
     * Constructs a facade around the supplied ontology.  Resolves the classpath resource {@code ontologyResource} and
     * loads it into a Jena {@code OntModel}.  Resources added to this ontology will use the {@code baseUri} to resolve
     * relative URIs.
     *
     * @param ontologyResource the classpath resource which resolves to an OWL ontology
     * @param baseUri the baseUri used to resolve relative URIs
     * @param serializationFormat the serialization format of the OWL ontology
     */
    public OntologyManager(String ontologyResource, String baseUri, String serializationFormat) {
        ontModel = ModelFactory.createOntologyModel();
        readOnlyOntModel = ModelFactory.createOntologyModel();

        URL ontologyUrl = this.getClass().getResource(ontologyResource);

        if (ontologyUrl == null) {
            throw new IllegalArgumentException(
                    String.format("Could not resolve ontology classpath resource %s", ontologyResource));
        }

        try {
            ontModel.read(ontologyUrl.openStream(), baseUri, serializationFormat);
            readOnlyOntModel.read(ontologyUrl.openStream(), baseUri, serializationFormat);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(
                            "Unable to read ontology resource from %s: %s", ontologyUrl.toString(), e.getMessage()), e);
        }
    }

    /**
     * Provides access to the underlying, mutable, Jena {@code OntModel}
     *
     * @return the mutable ontology model encapsulated by this class
     */
    OntModel getOntModel() {
        return ontModel;
    }

    /**
     * Obtain an OWL DatatypeProperty from the underlying Jena {@code OntModel} using the namespace and local name of
     * the property.
     *
     * @param ns property name space
     * @param propertyName the local name of the property
     * @return the OWL Datatype property
     * @throws IllegalArgumentException if the property cannot be found in the underlying {@code OntModel}, or if the
     * property exists but is not a Datatype property
     */
    public DatatypeProperty datatypeProperty(String ns, String propertyName) {
        return datatypeProperty(ns + propertyName);
    }

    /**
     * Obtain an OWL DatatypeProperty from the underlying Jena {@code OntModel} using the fully qualified name of
     * the property
     *
     * @param fqname the fully qualified name of the property
     * @return the OWL Datatype property
     * @throws IllegalArgumentException if the property cannot be found in the underlying {@code OntModel}, or if the
     * property exists but is not a Datatype property
     */
    public DatatypeProperty datatypeProperty(String fqname) {
        return getProperty(fqname, DatatypeProperty.class);
    }

    /**
     * Obtain an OWL ObjectProperty from the underlying Jena {@code OntModel} using the namespace and local name of
     * the property.
     *
     * @param ns property name space
     * @param propertyName the local name of the property
     * @return the OWL Object property
     * @throws IllegalArgumentException if the property cannot be found in the underlying {@code OntModel}, or if the
     * property exists but is not a Object property
     */
    public ObjectProperty objectProperty(String ns, String propertyName) {
        return objectProperty(ns + propertyName);
    }

    /**
     * Obtain an OWL ObjectProperty from the underlying Jena {@code OntModel} using the fully qualified name of
     * the property
     *
     * @param fqname the fully qualified name of the property
     * @return the OWL Object property
     * @throws IllegalArgumentException if the property cannot be found in the underlying {@code OntModel}, or if the
     * property exists but is not a Object property
     */
    public ObjectProperty objectProperty(String fqname) {
        return getProperty(fqname, ObjectProperty.class);
    }

    /**
     * Obtain an OWL Class from the underlying Jena {@code OntModel} using the namespace and local name of
     * the class.
     *
     * @param ns property name space
     * @param className the local name of the class
     * @return the OWL {@code OntClass}
     * @throws IllegalArgumentException if the class cannot be found in the underlying {@code OntModel}
     */
    public OntClass owlClass(String ns, String className) {
        OntClass result = ontModel.getOntClass(ns + className);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not find OWL class %s", ns + className));
        }
        return result;
    }

    /**
     * Obtains (creates?) the identified OWL Individual from the underlying model.
     *
     * @param uri the OWL individual's public URI
     * @return the OWL individual
     * @throws IllegalArgumentException if the individual cannot be found in the underlying model
     */
    public Individual individual(String uri) {
        Individual result = ontModel.getIndividual(uri);

        // TODO: will this ever happen?  Does getIndividual(String) create the individual behind the scenes if it
        // doesn't already exist?
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not find individual %s", uri));
        }

        return result;
    }

    /**
     * Creates an anonymous OWL Individual of the supplied class.
     *
     * @param ns the namespace of the class
     * @param className the local name of the class
     * @return the OWL individual
     * @throws IllegalArgumentException if the individual could not be created, presumably because the class was not
     * found in the underlying model
     */
    public Individual individual(String ns, String className) {
        String fqcn = ns + className;
        Individual result = ontModel.createIndividual(owlClass(ns, className));
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not create individual for class %s", fqcn));
        }

        return result;
    }

    /**
     * Creates an OWL Individual of the supplied class.
     *
     * @param uri the public URI to give the created individual
     * @param ns the namespace of the class
     * @param className the local name of the class
     * @return the OWL individual
     * @throws IllegalArgumentException if the individual could not be created, presumably because the class was not
     * found in the underlying model
     */
    public Individual individual(String uri, String ns, String className) {
        String fqcn = ns + className;
        Individual result = ontModel.createIndividual(uri, owlClass(ns, className));

        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not create individual for class %s", fqcn));
        }

        return result;
    }

    /**
     * Returns true if the supplied {@code uri} identifies an OWL individual in the underlying model.
     *
     * @param uri a URI which may identify an OWL individual in the underlying model
     * @return true if the underlying model contains the identified individual
     */
    public boolean hasIndividual(String uri) {
        return ontModel.getIndividual(uri) != null;
    }

    /**
     * Attempts to return the difference between the read-only model which contains the ontology only, and the
     * mutable model which contains individuals.
     *
     * @return a model containing individuals
     */
    OntModel instances() {
        Model m = ontModel.getBaseModel().difference(readOnlyOntModel.getBaseModel());
        OntModel ontM = ModelFactory.createOntologyModel();
        ontM.setNsPrefixes(Rdf.Ns.PREFIXES);
        ontM.add(m.listStatements());
        return ontM;
    }

    /**
     * Obtains a the property from the underlying ontology, and uses the supplied {@code propertyClass} to determine
     * whether a DatatypeProperty or ObjectProperty is being asked for.
     *
     * @param uri the full URI of the property to retrieve
     * @param propertyClass the expected class of the property, typically {@code DatatypeProperty} or
     * {@code ObjectProperty}
     * @return the {@code OntProperty} from the underlying {@code OntModel}
     * @throws IllegalArgumentException if the property is not found in the underlying {@code OntModel}, or if the
     * property is found but is not of the expected class.
     */
    private <T extends OntProperty> T getProperty(String uri, Class<T> propertyClass) {
        OntProperty result = null;
        boolean isObject = false;
        if (DatatypeProperty.class.isAssignableFrom(propertyClass)) {
            result = ontModel.getDatatypeProperty(uri);
        }

        if (ObjectProperty.class.isAssignableFrom(propertyClass)) {
            isObject = true;
            result = ontModel.getObjectProperty(uri);
        }

        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not find %s property %s (maybe the property is a %s instead, or the property is not present in the ontology)", ((isObject) ? "object" : "datatype"), uri, ((isObject) ? "datatype" : "object")));
        }

        return (T) result;
    }

}
