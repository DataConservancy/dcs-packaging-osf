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

import java.io.File;
import java.net.URL;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Application for generating packages from package descriptions.
 * <p>
 * Required arguments are the URL to the OSF registration,
 *  the path to the  OSF java client configuration file,
 *  the output file/directory that will contain the generated package,
 *  and the required BagIt metadata: the bag name on the command line, and the other metadata
 *      in a properties file
 * </p>
 *
 *
 */
public class PackageGenerationApp {

    @Argument(multiValued = false, usage="URL to the registration to be packaged" )
    private URL registrationUrl = null;

     /** Request for help/usage documentation */
    @Option(name = "-h", aliases = {"-help", "--help"}, usage = "print help message")
    private boolean help = false;

    /** the path to the OSF Java client configuration */
    @Option(name = "-c", aliases = {"-configuration", "--configuration"}, usage = "path to the OSF Java client configuration")
    private File confFile;

    /** the output directory for the package */
    @Option(name = "-o", aliases = {"-output", "--output"}, usage = "path to the directory where the package will be written")
    private File outputLocation;

    /** the bag name (required by the BagIt specification) **/
    @Option(name = "-n", aliases = {"-name", "--name"}, required = true, usage = "the name for the bag")
    private String bagName;

   /** other bag metadata properties file location */
    @Option(name = "-m", aliases = {"-metadata", "--metadata"}, usage = "the path to the metadata properties file for additional bag metadata")
    private File bagMetadataFile;

    /** Requests the current version number of the cli application. */
	@Option(name = "-v", aliases = { "-version", "--version" }, usage = "print version information")
	private boolean version = false;


    public static void main(String[] args) {

        final PackageGenerationApp application = new PackageGenerationApp();

		CmdLineParser parser = new CmdLineParser(application);
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
		} catch (Exception e){
            System.err.println(e.getMessage());
           // System.exit(e.getCode());
        }
    }

   	private void run() throws Exception {
        //boolean useDefaults = true;
        System.err.println("MOOOOOOOOOOOOOO");
    }

}

