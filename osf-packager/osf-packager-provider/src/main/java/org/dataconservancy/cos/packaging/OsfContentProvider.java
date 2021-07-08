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

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFFormat;

import org.dataconservancy.cos.osf.packaging.OsfPackageGraph;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.dataconservancy.cos.rdf.support.Rdf;
import org.dataconservancy.packaging.shared.AbstractContentProvider;
import org.dataconservancy.packaging.tool.model.ipm.FileInfo;
import org.dataconservancy.packaging.tool.model.ipm.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Implementation of ContentProvider for OSF source data.
 * Adapted from previous implementation of IpmPackager in this project.
 *
 * @author Ben Trumbore on 12/1/2016.
 */
public class OsfContentProvider extends AbstractContentProvider {

    /**
     * Jena Property instances used by the IpmPackager.
     */
    private static final class RdfProperties {
        private static final Property OSF_FILE_NAME =
                ResourceFactory.createProperty(OwlProperties.OSF_HAS_NAME.fqname());

        private static final Property OSF_BINARY_URI =
                ResourceFactory.createProperty(OwlProperties.OSF_HAS_BINARYURI.fqname());

        private static final Resource OSF_FILE = ResourceFactory.createResource(OwlClasses.OSF_FILE.fqname());

        private static final Property OSF_PROVIDER_NAME =
                ResourceFactory.createProperty(OwlProperties.OSF_PROVIDER_NAME.fqname());

        private static final Property RDF_TYPE = ResourceFactory.createProperty(Rdf.Ns.RDF, "type");
    }

    private Logger                  log = LoggerFactory.getLogger(OsfContentProvider.class);
    private OsfContentResolver      contentResolver;
    private Model                   domainObjects = null;
    private File                    temporaryDirectory;

    private static final String missingProvider = "missing_storage_provider";

    /**
     * Construct a content provider from the given graph and content resolver.
     *
     * @param graph the OSF package graph containing the package content.
     * @param resolver the resolver to use when resolving URIs in the package graph
     */
    public OsfContentProvider(final OsfPackageGraph graph, final OsfContentResolver resolver) {
        this.contentResolver = resolver;

        // Allocate a unique location for storing any binary content that will go into the package.
        // If another thread or JVM is running simultaneously, content will go into unique directory,
        // and avoid any file name conflicts.
        // The owner of this object must call close() when finished, and the folder is deleted there.
        try {
            temporaryDirectory = allocateTempDir();
        } catch (IOException e) {
            throw new RuntimeException("Unable to allocate a temporary directory:" + e.getMessage(), e);
        }

        // Initialize the domain objects
        final ByteArrayOutputStream sink = new ByteArrayOutputStream();
        graph.serialize(sink, RDFFormat.TURTLE_PRETTY, graph.OSF_SELECTOR);

        try {
            log.debug("Packaging graph:\n{}", IOUtils.toString(sink.toByteArray(), "UTF-8"));
        } catch (IOException e) {
            // ignore
        }

        domainObjects = ModelFactory.createDefaultModel()
                .read(new ByteArrayInputStream(sink.toByteArray()), null, "TTL");
    }

    /**
     * Construct a content provider from the given graph and HTTP client.  The HTTP client will be wrapped as a
     * simplified {@link OsfContentResolver}.
     *
     * @param graph the OSF package graph containing the package content.
     * @param httpClient The http client to use when resolving URIs in the package graph
     */
    public OsfContentProvider(final OsfPackageGraph graph, final OkHttpClient httpClient) {
        this(graph, (uri) -> {
            final Request get = new Request.Builder().get().url(uri).build();
            try {
                return httpClient.newCall(get).execute().body().byteStream();
            } catch (IOException e) {
                throw new RuntimeException("Unable to resolve or retrieve '" + uri + "': " + e.getMessage(), e);
            }
        });
    }

    /**
     * Returns an RDF representation of the domain model from the OSF package graph.
     * @return The Model representing the domain objects.
     */
    public Model getDomainModel() {
        return domainObjects;
    }

    /**
     * Returns the IPM tree corresponding to the provided OSF package graph.
     * We manually build the IPM tree here. Fundamentally, we're doing three things:
     * 1) Creating "directory" nodes that correspond to a domain object.
     * 2) Creating "content" nodes that correspond to a domain object that describes associated content.
     * 3) Arranging these nodes into a tree structure of our liking.
     * @return The root Node for the IPM tree representing the content.
     */
    public Node getIpmModel() {

        // Synthesize a root node to anchor the objects in the domain object graph
        final  Node root = new Node(URI.create(UUID.randomUUID().toString()));
        root.setFileInfo(directory("root"));
        root.setIgnored(true);

        // For each subject resource that is not anonymous, create a node
        //   - If the type of the node is a osf:File, then it will be a content node
        //   - Otherwise, make a directory node
        domainObjects.listSubjects().forEachRemaining(subject -> {

            if (subject.isAnon()) {
                log.debug("Skipping IPM node creation for anonymous resource '{}'", subject.getId().toString());
                return;
            }

            final URI u = URI.create(subject.getURI());

            // Hash URIs do not get their own node; they will be considered to be a single node.
            if (u.getFragment() != null) {
                log.debug("Skipping IPM node creation for hash URI resource '{}'", subject.getURI());
                return;
            }

            final String msgFmt = "Creating %s IPM node named %s for domain object %s";

            final Node n = new Node(u);
            n.setDomainObject(u);

            if (isFile(subject)) {
                final String binaryUri = getBinaryUri(subject);
                final String filename = getFileName(subject);
                log.info(String.format(msgFmt, "binary file", filename, subject.getURI()));

                n.setFileInfo(
                        contentFromUrl(
                                filename,
                                binaryUri));

            } else {
                final String filename;
                if (u.getPath() != null) {
                    final String[] pathElements = u.getPath().split("\\/");
                    filename = escape(pathElements[pathElements.length - 1]);
                } else {
                    filename = escape(subject.getURI());
                }
                log.info(String.format(msgFmt, "directory", filename, subject.getURI()));
                n.setFileInfo(directory(filename));
            }

            root.addChild(n);

        });

        return root;
    }

    /**
     * This method must be called when the owner is finished with the object.
     */
    public void close() {
        // Clean up the downloaded binary files
        try {
            FileUtils.deleteDirectory(temporaryDirectory);
        } catch (IOException e) {
            log.warn("Clean up of package download directory failed: " + e.getMessage(), e);
        }
    }

    /**
     * Create a FileInfo that points to file content present at a URL.
     * The content from the URL is downloaded and stored in a temporary file.
     * <p>
     * The logical name of the file represented in the FileInfo is the {@code name} parameter.
     * </p>
     *
     * @param filename the logical name of the content represented by the returned {@code FileInfo}
     * @param contentUrl resolvable URL to the content
     * @return populated FileInfo
     * @throws RuntimeException if the content cannot be downloaded or saved to a temporary file
     */
    private FileInfo contentFromUrl(final String filename, final String contentUrl) {
        log.debug("  Retrieving '{}' content from '{}'", filename, contentUrl);

        final File outFile;
        try {
            outFile = new File(temporaryDirectory, filename);
            outFile.getParentFile().mkdirs();
            IOUtils.copy(contentResolver.resolve(contentUrl), new FileOutputStream(outFile));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        final FileInfo info = new FileInfo(outFile.toPath());
        info.setIsFile(true);
        return info;
    }

    /**
     * Creates a temporary directory under {@code java.io.tmpdir}.  The directory will be uniquely named, so as to
     * avoid any filename conflicts with simultaneously executing downloads (e.g. another packager running in another
     * JVM).
     *
     * @return the temporary directory
     * @throws IOException if the directory cannot be allocated
     */
    private File allocateTempDir() throws IOException {
        // Use createTempFile to generate a unique filename in the temporary directory, then remove it and create
        // a directory of the same name
        File tmpDir = File.createTempFile("IpmPackager", ".content");
        if (tmpDir.delete()) {
            // Windows may have a problem if we simply attempt to call tmpDir.mkdir(), so create a new file with "-dir"
            // suffixed to the original directory name
            tmpDir = new File(tmpDir.getParentFile(), tmpDir.getName() + "-dir");
            if (!tmpDir.mkdir()) {
                throw new IOException("Unable to allocate temporary directory:" +
                        "failed to make the directory '" + tmpDir + "'");
            }
        } else {
            throw new IOException("Unable to allocate temporary directory:" +
                    "cannot delete the template file '" + tmpDir + "'");
        }

        return tmpDir;
    }

    /**
     * Create a FileInfo that names a directory.
     * <p>
     * This is a *logical* name for a directory in an IPM tree. The physical
     * directory pointed to by the resulting FileInfo is irrelevant. Only the
     * given name matters.
     * </p>
     *
     * @param name Given directory name
     * @return FileInfo for a node corresponding to this directory.
     */
    private FileInfo directory(final String name) {
        /*
         * DomainObjectResourceBuilder has a sanity check that files and
         * directories must exist. This check is not meaningful here, since
         * we're not creating an IPM tree from a filesystem. We should eliminate
         * or move that sanity check. In the meantime, just use cwd as a
         * workaround"
         */
        final FileInfo info = new FileInfo(Paths.get(".").toUri(), name);
        info.setIsDirectory(true);
        return info;
    }

    /**
     * Escapes troublesome characters from file names.
     *
     * @param candidateFilename a candidate filename that may contain characters to be escaped
     * @return the escaped filename
     */
    private String escape(final String candidateFilename) {
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
     * @return true if the {@code subject} is a {@code osf:File}
     */
    private boolean isFile(final Resource subject) {
        return domainObjects.contains(subject,
                OsfContentProvider.RdfProperties.RDF_TYPE, OsfContentProvider.RdfProperties.OSF_FILE);
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
     * @return a logical filename for the supplied {@code subject}
     */
    private String getFileName(final Resource subject) {
        final String baseName = escape(domainObjects.getProperty(subject,
                OsfContentProvider.RdfProperties.OSF_FILE_NAME).getObject().toString());
        final Statement providerNameProperty = domainObjects.getProperty(subject,
                OsfContentProvider.RdfProperties.OSF_PROVIDER_NAME);
        // TODO: correct model for wikis.  Either they are a File, and have a provider, or they are something else.
        // Currently wikis are very much like files, but they don't have a provider.  So this workaround supplies a
        // stand-in storage provider for now.
        final String providerName;
        if (providerNameProperty != null) {
            providerName = providerNameProperty.getObject().toString();
        } else {
            providerName = missingProvider;
        }
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
     * @return the value of the {@code osf:hasBinaryUri} predicate for the supplied {@code subject}
     */
    private String getBinaryUri(final Resource subject) {
        return domainObjects.getProperty(subject,
                OsfContentProvider.RdfProperties.OSF_BINARY_URI).getObject().asLiteral().getString();
    }

}
