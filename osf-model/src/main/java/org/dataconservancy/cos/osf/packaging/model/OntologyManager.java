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
package org.dataconservancy.cos.osf.packaging.model;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

/**
 * Created by esm on 6/1/16.
 */
public class OntologyManager {

    private static final String DEFAULT_ONTOLOGY_RESOURCE = "/ontology/osf-model.rdf";

    private OntModel ontModel;

    private OntModel readOnlyOntModel;

    public OntologyManager() {
        ontModel = ModelFactory.createOntologyModel();
        ontModel.read(this.getClass().getResourceAsStream(DEFAULT_ONTOLOGY_RESOURCE),
                "http://www.dataconservancy.org/osf-business-object-model#",
                "RDF/XML");
        readOnlyOntModel = ModelFactory.createOntologyModel();
        readOnlyOntModel.read(this.getClass().getResourceAsStream(DEFAULT_ONTOLOGY_RESOURCE),
                "http://www.dataconservancy.org/osf-business-object-model#",
                "RDF/XML");
//        System.err.println("On construction:");
//        writeModel(this.instances());
    }

    public OntModel getOntModel() {
        return ontModel;
    }

    public DatatypeProperty datatypeProperty(String ns, String propertyName) {
        return datatypeProperty(ns + propertyName);
    }

    public DatatypeProperty datatypeProperty(String propertyName) {
        return (DatatypeProperty) getProperty(propertyName, DatatypeProperty.class);
    }

    public ObjectProperty objectProperty(String ns, String propertyName) {
        return objectProperty(ns + propertyName);
    }

    public ObjectProperty objectProperty(String propertyName) {
        return (ObjectProperty) getProperty(propertyName, ObjectProperty.class);
    }

    public OntClass owlClass(String ns, String className) {
        OntClass result = ontModel.getOntClass(ns + className);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not find OWL class %s", ns + className));
        }
        return result;
    }

    public Individual individual(String uri) {
        Individual result = ontModel.getIndividual(uri);

        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not find individual %s", uri));
        }

        return result;
    }

    public Individual individual(String ns, String className) {
        String fqcn = ns + className;
        Individual result = ontModel.createIndividual(owlClass(ns, className));
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not create individual for class %s", fqcn));
        }

        return result;
    }

    public Individual individual(String uri, String ns, String className) {
        String fqcn = ns + className;
        Individual result = ontModel.createIndividual(uri, owlClass(ns, className));

        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not create individual for class %s", fqcn));
        }

        return result;
    }

    public Model instances() {
        return ontModel.getBaseModel().difference(readOnlyOntModel.getBaseModel());
    }

    private OntProperty getProperty(String name, Class<?> propertyClass) {
        OntProperty result = null;
        boolean isObject = false;
        if (DatatypeProperty.class.isAssignableFrom(propertyClass)) {
            result = ontModel.getDatatypeProperty(name);
        }

        if (ObjectProperty.class.isAssignableFrom(propertyClass)) {
            isObject = true;
            result = ontModel.getObjectProperty(name);
        }

        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not find %s property %s (maybe the property is a %s instead, or the property is not present in the ontology)", ((isObject) ? "object" : "datatype"), name, ((isObject) ? "datatype" : "object")));
        }

        return result;
    }

    private void writeModel(Model m) {
        System.err.println("Using RDFDataMgr:");
        RDFDataMgr.write(System.err, m, RDFFormat.TURTLE_PRETTY);
        System.err.println("---");
        System.err.println("Using Model.write:");
        m.write(System.err, "TTL", "");
        System.err.println("---");
        System.err.println();
    }
}
