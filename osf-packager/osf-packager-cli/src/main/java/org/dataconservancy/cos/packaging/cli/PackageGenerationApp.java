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

package org.dataconservancy.cos.packaging.cli;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.retrofit.OsfService;
import org.dataconservancy.cos.osf.packaging.OsfPackageGraph;
import org.dataconservancy.cos.packaging.OsfContentProvider;
import org.dataconservancy.packaging.shared.IpmPackager;
import org.dataconservancy.packaging.tool.api.Package;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Application for generating packages from package descriptions.
 * <p>
 * Required arguments are the URL to the OSF registration,
 * the path to the  OSF java client configuration file,
 * the output file/directory that will contain the generated package,
 * and the required BagIt metadata: the bag name on the command line, and the other metadata
 * in a properties file
 * </p>
 *
 * @author jrm
 */
public class PackageGenerationApp {

    private static ClassPathXmlApplicationContext CTX;


    @Argument(multiValued = false, usage = "URL to the registration to be packaged")
    private static String registrationUrl;

    /**
     * Request for help/usage documentation
     */
    @Option(name = "-h", aliases = {"-help", "--help"}, usage = "print help message")
    private boolean help = false;

    /**
     * the path to the OSF Java client configuration
     */
    @Option(name = "-c", aliases = {"-configuration", "--configuration"}, required = true, usage = "path to the OSF Java client configuration")
    private static File confFile;

    /**
     * the output directory for the package
     */
    @Option(name = "-o", aliases = {"-output", "--output"}, required = false, usage = "path to the directory where the package will be written")
    private static File outputLocation;

    /**
     * the package name
     **/
    @Option(name = "-n", aliases = {"-name", "--name"}, required = false, usage = "the name for the package")
    private static String packageName;

    /**
     * other bag metadata properties file location
     */
    @Option(name = "-m", aliases = {"-metadata", "--metadata"}, usage = "the path to the metadata properties file for additional bag metadata")
    private static File bagMetadataFile;

    /**
     * Requests the current version number of the cli application.
     */
    @Option(name = "-v", aliases = {"-version", "--version"}, usage = "print version information")
    private boolean version = false;

    /**
     * @param args
     */
    public static void main(final String[] args) {

        final PackageGenerationApp application = new PackageGenerationApp();

        final CmdLineParser parser = new CmdLineParser(application);
        parser.setUsageWidth(80);

        try {
            parser.parseArgument(args);

            /* Handle general options such as help, version */
            if (application.help) {
                parser.printUsage(System.err);
                System.err.println();
                System.exit(0);
            } else if (application.version) {
                System.err.println(PackageGenerationApp.class.getPackage()
                        .getImplementationVersion());
                System.exit(0);
            }

            final Properties props = System.getProperties();

            if (confFile.exists() && confFile.isFile()) {
                props.setProperty("osf.client.conf", confFile.toURI().toString());
            } else {
                System.err.println("Supplied OSF Client Configuration File " + confFile.getCanonicalPath() +
                        " does not exist or is not a file.");
                System.exit(1);
            }

            CTX = new ClassPathXmlApplicationContext(
                    "classpath*:org/dataconservancy/cos/osf/client/config/applicationContext.xml",
                    "classpath*:org/dataconservancy/cos/osf/client/retrofit/applicationContext.xml",
                    "classpath:/org/dataconservancy/cos/packaging/config/applicationContext.xml");

            final Response response = CTX.getBean("okHttpClient", OkHttpClient.class).newCall(
                    new Request.Builder()
                            .head()
                            .url(registrationUrl)
                            .build()
            ).execute();

            if (response.code() != 200) {
                System.err.println("There was an error executing '" + registrationUrl + "', response code " +
                        response.code() + " reason: '" + response.message() + "'");
                System.err.print("Please be sure you are using a valid API URL to a node or registration, ");
                System.err.println("and have properly configured authorization credentials, if necessary.");
                System.exit(1);
            }

            if (!response.header("Content-Type").contains("json")) {
                System.err.println("Provided URL '" + registrationUrl + "' does not return JSON (Content-Type was '" +
                        response.header("Content-Type") + "')");
                System.err.println("Please be sure you are using a valid API URL to a node or registration.");
                System.exit(1);
            }

            final String guid = parseGuid(registrationUrl);

            if (packageName == null) {
                packageName = guid;
            } else if (!(packageName.length() > 0)) {
                System.err.println("Bag name must have positive length.");
                System.exit(1);
            }

            if (outputLocation == null) {
                outputLocation = new File(packageName);
            }

            if (outputLocation.exists()) {
                System.err.println("Destination directory " + outputLocation.getCanonicalPath() + " already exists!  " +
                        "Either (re)move the directory, or choose a different output location.");
                System.exit(1);
            }

            FileUtils.forceMkdir(outputLocation);

            if (bagMetadataFile != null && (!bagMetadataFile.exists() || !bagMetadataFile.isFile())) {
                System.err.println("Supplied bag metadata file " + bagMetadataFile.getCanonicalPath() +
                        " does not exist or is not a file.");
                System.exit(1);
            }

            /* Run the package generation application proper */
            application.run();

        } catch (CmdLineException e) {
            /*
             * This is an error in command line args, just print out usage data
             * and description of the error.
             */
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.err.println();
            System.exit(1);
        } catch (Exception e) {
            if (e.getMessage() == null || e.getMessage().equals("null")) {
                System.err.println("There was an unrecoverable error:");
                e.printStackTrace(System.err);
            } else {
                System.err.println("There was an unrecoverable error: " + e.getMessage());
                e.printStackTrace(System.err);
            }

            System.exit(1);
        }
    }


    private void run() throws Exception {
        // Prepare the OSF registration and users information
        final OsfService osfService = CTX.getBean("osfService", OsfService.class);
        final Node registration = osfService.node(registrationUrl).execute().body();

        if (registration == null) {
            System.err.println("Failed to obtain registration " + registrationUrl + " from endpoint. " +
                    "\nEither the connection failed, or a registration does not exist at the provided URL.");
            System.exit(1);
        }

        final List<User> users = registration.getContributors().stream()
                .map(c -> {
                    try {
                        if (c.getUserRel() != null) {
                            return osfService.user(c.getUserRel()).execute().body();
                        } else {
                            String contributorId = c.getId();
                            if (contributorId.contains("-")) {
                                contributorId = contributorId.split("-")[1];
                            }
                            return osfService.userById(contributorId).execute().body();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());

        // Prepare package graph
        final OsfPackageGraph packageGraph = CTX.getBean("packageGraph", OsfPackageGraph.class);
        packageGraph.add(registration);
        users.forEach(packageGraph::add);

        // Prepare content provider using package graph
        // TODO - Does this work without the lambda-specified resolver used in OsfContentProviderTest?
        final OsfContentProvider contentProvider = new OsfContentProvider(packageGraph,
                CTX.getBean("okHttpClient", OkHttpClient.class));

        // Create the package in the default location with the supplied name.
        // No package generation parameters are supplied.
        final IpmPackager ipmPackager = new IpmPackager();
        ipmPackager.setPackageName(packageName);
        ipmPackager.setPackageLocation(outputLocation.getPath());
        final Package pkg;
        if (bagMetadataFile == null) {
            pkg = ipmPackager.buildPackage(contentProvider, null, null);
        } else {
            try (final FileInputStream metadataStream = new FileInputStream(bagMetadataFile)) {
                pkg = ipmPackager.buildPackage(contentProvider, metadataStream, null);
            }
        }

        // Now just write the package out to a file in the output location
        // this must agree with the package root directory name according to our
        // dataconservancy bagit profile
        // TODO: can the user specify the kind of archive?  tar vs tar.gz?
        final File packageFile = new File(outputLocation.getAbsolutePath(), packageName + ".tar");
        final FileOutputStream out;
        try {
            out = new FileOutputStream(packageFile);
            IOUtils.copy(pkg.serialize(), out);
            out.close();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        pkg.cleanupPackage();
    }

    private static String parseGuid(final String registrationUrl) {
        String mutableUrl = registrationUrl.trim();

        if (mutableUrl.endsWith("/")) {
            mutableUrl = mutableUrl.substring(0, mutableUrl.length() - 1);
        }

        return mutableUrl.substring(mutableUrl.lastIndexOf("/") + 1);
    }

}

