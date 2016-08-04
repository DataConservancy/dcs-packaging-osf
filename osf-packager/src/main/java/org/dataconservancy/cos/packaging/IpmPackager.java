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
package org.dataconservancy.cos.packaging;

import com.github.jasminb.jsonapi.RelationshipResolver;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFFormat;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.packaging.OsfPackageGraph;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.dataconservancy.cos.rdf.support.Rdf;
import org.dataconservancy.packaging.tool.api.Package;
import org.dataconservancy.packaging.tool.api.PackageGenerationService;
import org.dataconservancy.packaging.tool.impl.IpmRdfTransformService;
import org.dataconservancy.packaging.tool.model.GeneralParameterNames;
import org.dataconservancy.packaging.tool.model.PackageGenerationParameters;
import org.dataconservancy.packaging.tool.model.PackageState;
import org.dataconservancy.packaging.tool.model.PropertiesConfigurationParametersBuilder;
import org.dataconservancy.packaging.tool.model.RDFTransformException;
import org.dataconservancy.packaging.tool.model.ipm.FileInfo;
import org.dataconservancy.packaging.tool.model.ipm.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by esm on 6/22/16.
 *
 * @author apb
 * @author esm
 */
public class IpmPackager {

    static final Logger LOG = LoggerFactory.getLogger(IpmPackager.class);

    private String PACKAGE_NAME = "MyPackage"; // this will get overridden by the CLI  using the setter

    static final ClassPathXmlApplicationContext cxt =
            new ClassPathXmlApplicationContext("classpath*:applicationContext.xml",
                    "classpath*:org/dataconservancy/config/applicationContext.xml",
                    "classpath*:org/dataconservancy/packaging/tool/ser/config/applicationContext.xml",
                    "classpath*:org/dataconservancy/cos/osf/client/config/applicationContext.xml",
                    "classpath:/org/dataconservancy/cos/packaging/config/applicationContext.xml");

    /**
     * Jena Property instances used by the IpmPackager.
     */
    private static final class RdfProperties {
        private static final Property OSF_FILE_NAME = ResourceFactory.createProperty(OwlProperties.OSF_HAS_NAME.fqname());

        private static final Property OSF_BINARY_URI =
                ResourceFactory.createProperty(OwlProperties.OSF_HAS_BINARYURI.fqname());

        private static final Resource OSF_FILE = ResourceFactory.createResource(OwlClasses.OSF_FILE.fqname());

        private static final Property OSF_PROVIDER_NAME =
                ResourceFactory.createProperty(OwlProperties.OSF_PROVIDER_NAME.fqname());

        private static final Property RDF_TYPE = ResourceFactory.createProperty(Rdf.Ns.RDF, "type");
    }

    private static final RelationshipResolver DEFAULT_RESOLVER = cxt.getBean("jsonApiRelationshipResolver", RelationshipResolver.class);

    private RelationshipResolver resolver;

    public IpmPackager() {
        resolver = DEFAULT_RESOLVER;
    }

    public IpmPackager(RelationshipResolver resolver) {
        this.resolver = resolver;
    }

    public RelationshipResolver getResolver() {
        return resolver;
    }

    public void setResolver(RelationshipResolver resolver) {
        this.resolver = resolver;
    }

    public void setPackageName(String packageName) { this.PACKAGE_NAME = packageName; }


    public static void main(String[] args) throws Exception {

        final OsfPackageGraph packageGraph = cxt.getBean("packageGraph", OsfPackageGraph.class);
        final OsfService osfService = cxt.getBean("osfService", OsfService.class);
        final String registrationUrl = "https://api.osf.io/v2/registrations/0zqbo/";

        final Registration registration = osfService.registrationByUrl(registrationUrl).execute().body();
        final List<User> users = registration.getContributors().stream()
                .map(c -> {
                    try {
                        if (c.getUserRel() != null) {
                            return osfService.userByUrl(c.getUserRel()).execute().body();
                        } else {
                            String contributorId = c.getId();
                            if (contributorId.contains("-")) {
                                contributorId = contributorId.split("-")[1];
                            }
                            return osfService.user(contributorId).execute().body();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());

        packageGraph.add(registration);
        users.forEach(packageGraph::add);

        IpmPackager packager = new IpmPackager();


        packager.buildPackage(packageGraph, null);

    }

    public Package buildPackage(OsfPackageGraph graph,  LinkedHashMap<String, List<String>> metadata) {

        IpmRdfTransformService ipm2rdf =
                cxt.getBean(IpmRdfTransformService.class);

        PackageState state = new PackageState();

        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        graph.serialize(sink, RDFFormat.TURTLE_PRETTY, graph.OSF_SELECTOR);

        try {
            LOG.debug("Packaging graph:\n{}", IOUtils.toString(sink.toByteArray(), "UTF-8"));
        } catch (IOException e) {
            // ignore
        }

        /* Put the domain object RDF into the package state */
        state.setDomainObjectRDF(ModelFactory.createDefaultModel()
                .read(new ByteArrayInputStream(sink.toByteArray()),
                        null,
                        "TTL"));

        /*
         * Now put the IPM tree in the package state. We build it in java via
         * buildContentTree(), then serialize to RDF
         */
        try {
            state.setPackageTree(ipm2rdf.transformToRDF(buildContentTree(state.getDomainObjectRDF(), resolver)));
        } catch (RDFTransformException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        if (metadata != null) {
            state.setPackageMetadataList(metadata);
        }

        /* Construct the package */
        Package pkg = null;
        try {
            pkg = buildPackage(state);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return pkg;
    }
    
    /*
     * We manually build the IPM tree here. Fundamentally, we're doing three
     * things: 1) Creating "directory" nodes that correspond to a domain object.
     * 2) Creating "content" nodes that correspond to a domain object that
     * describes associated content. 3) Arranging these nodes into a tree
     * structure of our liking.
     */
    private static Node buildContentTree(Model domainObjects, RelationshipResolver resolver) {
        // Synthesize a root node to anchor the objects in the domain object graph
        Node root = new Node(URI.create(UUID.randomUUID().toString()));
        root.setFileInfo(directory("root"));
        root.setIgnored(true);

        // For each subject resource that is not anonymous, create a node
        //   - If the type of the node is a osf:File, then it will be a content node
        //   - Otherwise, make a directory node

        domainObjects.listSubjects().forEachRemaining(subject -> {

            if (subject.isAnon()) {
                LOG.debug("Skipping IPM node creation for anonymous resource '{}'", subject.getId().toString());
                return;
            }

            URI u = URI.create(subject.getURI());

            // Hash URIs do not get their own node; they will be considered to be a single node.
            if (u.getFragment() != null) {
                LOG.debug("Skipping IPM node creation for hash URI resource '{}'", subject.getURI());
                return;
            }

            String msgFmt = "Creating %s IPM node named %s for domain object %s";

            Node n = new Node(u);
            n.setDomainObject(u);

            if (isFile(subject, domainObjects)) {
                String binaryUri = getBinaryUri(subject, domainObjects);
                String filename = getFileName(subject, domainObjects);
                LOG.info(String.format(msgFmt, "binary file", filename, subject.getURI()));

                n.setFileInfo(
                        contentFromUrl(
                                filename,
                                binaryUri,
                                resolver));

            } else {
                String filename;
                if (u.getPath() != null) {
                    String[] pathElements = u.getPath().split("\\/");
                    filename = escape(pathElements[pathElements.length - 1]);
                } else {
                    filename = escape(subject.getURI());
                }
                LOG.info(String.format(msgFmt, "directory", filename, subject.getURI()));
                n.setFileInfo(directory(filename));
            }

            root.addChild(n);

        });

        return root;
    }

    /**
     * Create a FileInfo that points to file content present at a URL.  The content from the URL is downloaded and
     * stored in a temporary file.
     * <p>
     * The logical name of the file represented in the FileInfo is the {@code name} parameter.
     * </p>
     *
     * @param filename the logical name of the content represented by the returned {@code FileInfo}
     * @param contentUrl resolvable URL to the content
     * @return populated FileInfo
     * @throws RuntimeException if the content cannot be downloaded or saved to a temporary file
     */
    private static FileInfo contentFromUrl(String filename, String contentUrl, RelationshipResolver resolver) {
        LOG.debug("  Retrieving '{}' content from '{}'", filename, contentUrl);

        File outFile = null;
        try {
            outFile = new File(System.getProperty("java.io.tmpdir"), filename);
            byte[] data = resolver.resolve(contentUrl);
            if (data == null || data.length == 0) {
                throw new RuntimeException("Unable to retrieve content from '" + contentUrl + "'");
            }
            IOUtils.write(data,
                    new FileOutputStream(outFile));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        FileInfo info = new FileInfo(outFile.toPath());
        info.setIsFile(true);
        return info;
    }

    /**
     * Create a FileInfo that names a directory.
     * <p>
     * This is a *logical* name for a directory in an IPM tree. The physical
     * directory pointed to by the resulting FileInfo is irrelevant. Only the
     * given name matters.
     * </p>
     *
     * @param name
     *        Given directory name
     * @return FileInfo for a node corresponding to this directory.
     */
    private static FileInfo directory(String name) {
        /*
         * DomainObjectResourceBuilder has a sanity check that files and
         * directories must exist. This check is not meaningful here, since
         * we're not creating an IPM tree from a filesystem. We should eliminate
         * or move that sanity check. In the meantime, just use cwd as a
         * workaround"
         */
        FileInfo info = new FileInfo(Paths.get(".").toUri(), name);
        info.setIsDirectory(true);
        return info;
    }

    /* Package building boilerplate */
    private Package buildPackage(PackageState state) throws Exception {
        PackageGenerationParameters params =
                new PropertiesConfigurationParametersBuilder()
                        .buildParameters(IpmPackager.class
                                .getResourceAsStream("/PackageGenerationParams.properties"));

        PackageGenerationService generator =
                cxt.getBean(PackageGenerationService.class);

        params.addParam(GeneralParameterNames.PACKAGE_LOCATION,
                System.getProperty("java.io.tmpdir"));
        params.addParam(GeneralParameterNames.PACKAGE_NAME, PACKAGE_NAME);

        return generator.generatePackage(state, params);
    }

    /**
     * Escapes troublesome characters from file names.
     * 
     * @param candidateFilename a candidate filename that may contain characters to be escaped
     * @return the escaped filename
     */
    private static String escape(String candidateFilename) {
        return candidateFilename
                .replace(":", "_")
                .replace(" ", "_");
    }

    /**
     * Examines the RDF type of the resource which represents a subject in the supplied {@code domainObjects} model,
     * and returns {@code true} if it is an {@code osf:File}.  Objects that have an {@code rdf:type} of {@code osf:File}
     * are expected to have the following predicates present in {@code domainObjects}:
     * <pre>
     * &lt;subject&gt; a osf:File                              .
     * &lt;subject&gt;   osf:hasName      "some file name"     .
     * &lt;subject&gt;   osf:providerName "file provider name" .
     * &lt;subject&gt;   osf:hasBinaryUri "&lt;url to content&gt;"   .
     * </pre>
     *
     * @param subject a resource from the supplied {@code domainObjects} which may be an {@code osf:File}
     * @param domainObjects Jena model containing the triples for all the domain objects in a package
     * @return true if the {@code subject} is a {@code osf:File}
     */
    private static boolean isFile(Resource subject, Model domainObjects) {
        return domainObjects.contains(subject, RdfProperties.RDF_TYPE, RdfProperties.OSF_FILE);

    }

    /**
     * Uses the values of the {@code osf:hasName} and {@code osf:providerName} predicates to generate a logical filename
     * for the file represented by the domain object with the supplied {@code subject}. Objects that have an
     * {@code rdf:type} of {@code osf:File} are expected to have the following predicates present in
     * {@code domainObjects}:
     * <pre>
     * &lt;subject&gt; a osf:File                              .
     * &lt;subject&gt;   osf:hasName      "some file name"     .
     * &lt;subject&gt;   osf:providerName "file provider name" .
     * &lt;subject&gt;   osf:hasBinaryUri "&lt;url to content&gt;"   .
     * </pre>
     *
     * @param subject a resource from the supplied {@code domainObjects} which is an {@code osf:File}
     * @param domainObjects Jena model containing the triples for all the domain objects in a package
     * @return a logical filename for the supplied {@code subject}
     */
    private static String getFileName(Resource subject, Model domainObjects) {
        String baseName = escape(domainObjects.getProperty(subject, RdfProperties.OSF_FILE_NAME).getObject().toString());
        String providerName = domainObjects.getProperty(subject, RdfProperties.OSF_PROVIDER_NAME).getObject().toString();
        return escape(providerName + "_" + baseName);
    }

    /**
     * Obtains the value of the {@code osf:hasBinaryUri} predicate from {@code domainObjects} for the supplied
     * {@code subject}.  Objects that have an {@code rdf:type} of {@code osf:File} are expected to have the following
     * predicates present in {@code domainObjects}:
     * <pre>
     * &lt;subject&gt; a osf:File                              .
     * &lt;subject&gt;   osf:hasName      "some file name"     .
     * &lt;subject&gt;   osf:providerName "file provider name" .
     * &lt;subject&gt;   osf:hasBinaryUri "&lt;url to content&gt;"   .
     * </pre>
     *
     * @param subject a resource from the supplied {@code domainObjects} which is an {@code osf:File}
     * @param domainObjects Jena model containing the triples for all the domain objects in a package
     * @return the value of the {@code osf:hasBinaryUri} predicate for the supplied {@code subject}
     */
    private static String getBinaryUri(Resource subject, Model domainObjects) {
        return domainObjects.getProperty(subject, RdfProperties.OSF_BINARY_URI).getObject().asLiteral().getString();
    }

}
