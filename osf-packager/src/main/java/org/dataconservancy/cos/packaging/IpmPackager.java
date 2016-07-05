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

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFFormat;
import org.dataconservancy.cos.osf.packaging.OsfPackageGraph;
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
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.text.html.HTMLDocument;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by esm on 6/22/16.
 *
 * @author apb
 * @author esm
 */
public class IpmPackager {
    static final String PACKAGE_NAME = "MyPackage";

    static final ClassPathXmlApplicationContext cxt =
            new ClassPathXmlApplicationContext("classpath*:applicationContext.xml",
                    "classpath*:org/dataconservancy/config/applicationContext.xml",
                    "classpath*:org/dataconservancy/packaging/tool/ser/config/applicationContext.xml");

    public static void main(String[] args) throws Exception {

        IpmRdfTransformService ipm2rdf =
                cxt.getBean(IpmRdfTransformService.class);

        PackageState state = new PackageState();

        /* Put the domain object RDF into the package state */
        state.setDomainObjectRDF(ModelFactory.createDefaultModel()
                .read(IpmPackager.class
                                .getResourceAsStream("/content/domainObjects.ttl"),
                        null,
                        "TTL"));

        /*
         * Now put the IPM tree in the package state. We build it in java via
         * buildContentTree(), then serialize to RDF
         */
        state.setPackageTree(ipm2rdf.transformToRDF(buildContentTree()));

        /* Construct the package */
        Package pkg = buildPackage(state);

        /* Now just write the package out to a file */
        FileOutputStream out = new FileOutputStream(PACKAGE_NAME + ".tar.gz");
        IOUtils.copy(pkg.serialize(), out);
        out.close();
        pkg.cleanupPackage();

        System.out.println("DONE");

    }

    public static void build(OsfPackageGraph graph) {

        IpmRdfTransformService ipm2rdf =
                cxt.getBean(IpmRdfTransformService.class);

        PackageState state = new PackageState();

        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        graph.serialize(sink, RDFFormat.TURTLE_PRETTY, graph.OSF_SELECTOR);

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
            state.setPackageTree(ipm2rdf.transformToRDF(buildContentTree(state.getDomainObjectRDF())));
        } catch (RDFTransformException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        /* Construct the package */
        Package pkg = null;
        try {
            pkg = buildPackage(state);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        /* Now just write the package out to a file */
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(PACKAGE_NAME + ".tar.gz");
            IOUtils.copy(pkg.serialize(), out);
            out.close();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        pkg.cleanupPackage();

        System.out.println("DONE");
    }

    private static Node buildContentTree(Model domainObjects) {
        // Synthesize a root node to anchor the objects in the domain object graph
        Node root = new Node(URI.create(UUID.randomUUID().toString()));
        root.setFileInfo(directory("root"));
        root.setIgnored(true);

        // For each subject resource that is not anonymous, create a node
        //   - If the type of the node is a osf:File, then it will be a content node
        //   - Otherwise, make a directory node

        domainObjects.listSubjects().forEachRemaining(subject -> {
            if (!subject.isAnon()) {
                URI u = URI.create(subject.getURI());

                // Hash URIs do not get their own node; they will be considered to be a single node.
                if (u.getFragment() != null) {
                    return;
                }

                String fileName;
                if (u.getPath() != null) {
                    String[] pathElements = u.getPath().split("\\/");
                    fileName = escape(pathElements[pathElements.length - 1]);
                } else {
                    fileName = escape(subject.getURI());
                }

                System.err.println("Creating node for subject " + subject + " with file name " + fileName);
                Node n = new Node(u);
                n.setDomainObject(u);
                n.setFileInfo(directory(fileName));
                root.addChild(n);
            }
        });

        return root;
    }

    /*
     * We manually build the IPM tree here. Fundamentally, we're doing three
     * things: 1) Creating "directory" nodes that correspond to a domain object.
     * 2) Creating "content" nodes that correspond to a domain object that
     * describes associated content. 3) Arranging these nodes into a tree
     * structure of our liking.
     */
    private static Node buildContentTree() throws Exception {

        /*
         * First the nodes - correlate each node with a domain object and
         * possibly content
         */

        /*
         * These are the IDs of our domain objects. In this quick example, we
         * know this a priori
         */
        final String id1 = "test:/1";
        final String id2 = "test:/2";
        final String id3 = "test:/3";

        Node one = new Node(URI.create(id1));
        one.setDomainObject(URI.create(id1));
        one.setFileInfo(directory("one"));

        Node two = new Node(URI.create(id2));
        two.setDomainObject(URI.create(id2));
        two.setFileInfo(directory("two"));

        Node three = new Node(URI.create(id3));
        three.setDomainObject(URI.create(id3));
        three.setFileInfo(contentFromClasspath("/content/three.txt"));

        /* Now the hierarchy */

        one.addChild(two);
        two.addChild(three);

        return one;
    }

    /**
     * Create a FileInfo that points to file content present in the classpath.
     * <p>
     * The logical name of the file represented in the FileInfo is the file's
     * name, so the /path/to/file.txt will hafe name file.txt
     * </p>
     *
     * @param path
     *        Path to the file content in the classpath.
     * @return populated FileInfo
     * @throws Exception
     */
    private static FileInfo contentFromClasspath(String path) throws Exception {
        FileInfo info = new FileInfo(Paths
                .get(IpmPackager.class.getResource(path).toURI()));
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
    private static Package buildPackage(PackageState state) throws Exception {
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

    private static String escape(String candidateFilename) {
        return candidateFilename.replace(":", "_");
    }
}
