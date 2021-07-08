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
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.Statement;

import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.rdf.support.AnnotationsProcessor;
import org.dataconservancy.cos.rdf.support.OntologyManager;
import org.dataconservancy.cos.rdf.support.ManagedGraph;
import org.dataconservancy.cos.rdf.support.Rdf;

import java.util.Map;

import static org.dataconservancy.cos.rdf.support.Util.asProperty;

@SuppressWarnings("checkstyle:linelength")
/**
 * Responsible for creating a package graph from OSF domain objects.
 * <p>
 * Note that the {@code OsfPackageGraph} creates an instance of the {@link AnnotationsProcessor} internally, and
 * provides a reference to {@code this} instance.  When OSF domain-specific methods are called (e.g.
 * {@link #add(Registration)}), the internal instance of {@code AnnotationsProcessor} is invoked, which populates the
 * underlying {@code OntModel} (via {@code OntologyManager}).
 * </p>
 * <pre>
 * Jena OntModel &lt;-- OntologyManager &lt;-- Managed Graph &lt;-- extends - OsfPackageGraph - creates --&gt; AnnotationsProcessor
 *                                                /
 *                    OwlAnnotationsProcessor &lt;--+
 * </pre>
 * <h3>Example</h3>
 * <pre>
 * // Create an OntologyManager, which is a facade around a Jena OntModel
 * // This facade insures that any OWL classes or properties added to the OntModel are defined in an ontology.
 *
 * OntologyManager ontMgr = new OntologyManager(); // uses defaults for the ontology location and URI resolution
 *
 * // Create an OsfPackageGraph, which can manipulate the underlying OntModel via the OntologyManager.
 *
 * OsfPackageGraph graph = new OsfPackageGraph(ontMgr);
 *
 * // Even though the OsfPackageGraph exposes methods to mutate the underlying OntModel, the preferred mechanism is to
 * // add objects via domain-specific methods.
 *
 * Registration osfRegistration = retrieveRegistration("abcde");  // implementation elided for brevity
 *
 * // Add the registration to the graph.
 * // Under the hood the Registration object will be converted to RDF and added to the underlying model
 *
 * &lt;Map&gt;String, Individual&lt; individuals = graph.add(osfRegistration);
 *
 * // The OWL individuals that are added to the graph are returned, and keyed by their identifier.  Note that the
 * // Map may contain anonymous OWL individuals.
 * </pre>
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class OsfPackageGraph extends ManagedGraph {

    /**
     * Used to process the annotations on OSF java objects
     */
    private AnnotationsProcessor processor;

    /**
     * A {@link Selector} which selects statements whose subjects have an {@code rdf:type} in the OSF
     * namespace.  Statements which have anonymous nodes as an object for an {@code rdf:type} are excluded.
     */
    public final Selector OSF_SELECTOR = new Selector() {
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
        public boolean test(final Statement statement) {
            // if the subject of the statement has an 'rdf:type' in the OSF namespace, we want to keep the statement.
            // if the object of the statement is an anonymous 'rdf:type', then we do not want to keep it.

            return hasOsfType(statement) && !isAnonymousRdfType(statement);
        }
    };

    /**
     * Constructs a new instance, ready to accept OSF domain objects and convert them to an RDF graph.
     *
     * @param ontMgr the underlying ontology manager used to create and store RDF according to the OSF ontology
     */
    public OsfPackageGraph(final OntologyManager ontMgr) {
        super(ontMgr);
        this.processor = new AnnotationsProcessor(this);
    }

    /**
     * Adds an OSF {@link Registration} to the graph.
     *
     * @param registration the OSF Registration
     * @return a {@code Map} containing the URIs and OWL individuals added to the graph
     */
    public Map<String, Individual> add(final Registration registration) {
        return processor.process(registration);
    }

    /** Adds an OSF node to the graph.
     * 
     * @param node OSF node
     * @return a {@code Map} containing the URIs and OWL individuals added to the graph
     */
    public Map<String, Individual> add(final Node node) {
        return processor.process(node);
    }

    /**
     * Adds an OSF {@link User} to the graph.
     *
     * @param user the OSF user
     * @return a {@code Map} containing the URIs and OWL individuals added to the graph
     */
    public Map<String, Individual> add(final User user) {
        return processor.process(user);
    }

    /**
     * Returns true if the predicate of the {@code statement} is {@code rdf:type} and the object of the
     * {@code statement} is an anonymous node.
     * <h3>Examples</h3>
     * <p>
     * If the {@code statement} supplied to this method was: {@code <vae86> rdf:type _:b3}, then this method would
     * return {@code true} because the object of the statement is an anonymous node.
     * </p>
     * <p>
     * If the {@code statement} supplied to this method was: {@code <vae86> osf:hasContributor _:b3}, then this method
     * would return {@code false} because the predicate of the statement is not {@code rdf:type}.
     * </p>
     *
     * @param statement the statement which may contain an rdf:type predicate with an anonymous node as an object
     * @return true if the statement contains an rdf:type predicate with an anonymous node as the object
     */
    private boolean isAnonymousRdfType(final Statement statement) {
        if (statement.getPredicate().equals(asProperty(Rdf.Ns.RDF + "type"))) {
            if (statement.getObject().isAnon()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the subject of the {@code statement} has an {@code rdf:type} that is in the OSF namespace.
     * <p>
     * The underlying Jena {@code OntModel} is consulted for all objects that have the subject of the {@code statement}
     * and a predicate of {@code rdf:type}.  If any of the objects (the objects represent an RDF type) are in the OSF
     * namespace, then this method returns true.
     * </p>
     * <h3>Examples</h3>
     * <p>
     * If the {@code statement} supplied to this method was: {@code <vae86> osf:hasCategory "DATA"}, and the model
     * encapsulated by this graph contained a triple {@code <vae86> a osf:OSFBusinessObject}, then this method would
     * return {@code true}, because the subject of the statement {@code <vae86>} has an {@code rdf:type} that is in the
     * OSF namespace.
     * </p>
     *
     * @param statement the statement whose subject may be a resource that has an {@code rdf:type} from the OSF
     *                  namespace
     * @return true if the statement's subject has an {@code rdf:type} from the OSF namespace
     */
    private boolean hasOsfType(final Statement statement) {
        final StringBuilder msg = new StringBuilder();
        msg.append(String.format("Testing Statement: %s %s %s\n",
                statement.getSubject(), statement.getPredicate(), statement.getObject()));
        msg.append(String.format("Does the subject %s have an rdf:type in the OSF namespace?\n",
                statement.getSubject()));
        final NodeIterator itr = ontMgr.getOntModel()
                .listObjectsOfProperty(statement.getSubject(), asProperty(Rdf.Ns.RDF + "type"));
        boolean result = false;
        while (itr.hasNext()) {
            final RDFNode objectNode = itr.nextNode();
            msg.append(String.format("  - Testing rdf:type: %s\n", objectNode));
            if (!objectNode.isAnon()) {
                result = objectNode.asResource().getURI().startsWith(Rdf.Ns.OSF);
            }
            msg.append(result);
            msg.append("\n");
            if (result) {
                LOG.trace(msg.toString());
                break;
            }
        }

        return result;
    }
}
