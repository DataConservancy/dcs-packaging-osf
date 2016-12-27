# OSF Command Line Interface 
This module supplies a command line interface (CLI) for retrieving a registration from an OSF instance and writing it locally into a package which conforms to the Data Conservancy packaging specification.

# How It Works
The CLI uses functionality of the Java client in the [osf-client](../osf-client) module to attach to the OSF REST API on a running OSF instance specified by a configuration file whose location must be supplied by the user. Additional parameters will need to be supplied as indicated below. The packager code leverages a workflow from the Data Conservancy Package Tool GUI to construct the package once the content from the target registration has been retrieved. The package is then saved as a gzipped tar file into a directory specified by the user as a command line option.


# Command Line Usage
The CLI takes a single argument which is a URL pointing to the registration which is to be packaged (for example, `https://api.osf.io/v2/registrations/hejx2`
This is preceded by a list of command line options as follows:

```
-c (-configuration, --configuration)  FILE   : path to the OSF Java client configuration
-h (-help, --help)                           : print help message
-m (-metadata, --metadata) FILE              : the path to the metadata properties file for additional bag metadata
-n (-name, --name) VAL                       : the name for the package
-o (-output, --output) FILE                  : path to the directory where the package will be written
-v (-version, --version)                     : print version information
```
The `-c, -n` and `-o` options are required. The OSF Java client must be configured (`-c`) so that it knows which running OSF instance to attach to, and how to perform authentication to the instance.  The package name (`-n`) is used to both name the root directory for the package and the package file. Finally, the output location (`-o`) will tell the CLI where to write the package.

The `-m` flag is optional. Additional metadata may be specified in a bag metadata properties file, and is used to describe the bag in accordance with the [Data Conservancy BagIt Profile](http://dataconservancy.github.io/dc-packaging-spec/dc-bagit-profile-1.0.html) . There are some [reserved metadata names and cardinality restrictions](http://dataconservancy.github.io/dc-packaging-spec/dc-bagit-profile-1.0.html#a2.2.1) .

# OSF Java Client Configuration
Configuration must be supplied for both the OSF API and Waterbutler endpoints, since both are needed to build the package.  An example is below:
```
{
  "osf": {
    "v2": {
      "host": "api.osf.io",
      "port": "443",
      "basePath": "/v2/",
      "authHeader": "Basic ZW138fTnZXJAZ21haWwu98wIOmZvb2JuU43heg==",
      "scheme": "https"
    }
  },
  "wb": {
    "v1": {
      "host": "files.osf.io",
      "port": "443",
      "basePath": "/v1/",
      "scheme": "https"
    }
  }
}
```
The absolute path to the file containing this configuration is what must be supplied as the value for the `-c` option.

Be sure to configure an authHeader in the OSF v2 API configuration, which will be sent on every request to the API. You can do this by base64 encoding your login id concatenated to your password with a colon (on MacOS or Linux):

```
$ echo 'c3po@tatooine.com:excuseme' | base64
YzNwb0B0YXRvb2luZS5jb206ZXhjdXNlbWUK
$
```

## Bag Metadata File
The `-m` option also takes an absolute path as its value. This file contains bag metadata which will conform to the Data Conservancy BagIt Profile specification. Metadata entries will consist of key - value pairs, separated by either a `=` or a `:` , one per line.
## Output Directory
The `-o` option takes an absolute path as its value. This path must point to an existing directory on the filesystem.

## Bag Name
The `-n` option takes a string value. This string will be used to name the root directory of the package as well as the package file.

# Example
A command line invocation might look something like this:

```java -jar osf-cli-1.0.0-SNAPSHOT.jar -c /home/luser/OSF-Java-Client.conf -m /home/luser/bag.properties -n ImportantPackage -o /home/luser/packages https://api.osf.io/v2/registrations/hejx2 ```

In this case, the command is executed in the working directory where the executable jar file osf-cli-1.0.0-SNAPSHOT.jar resides.

# Current Status
The first iteration of the CLI has some simplifying constraints in order to deliver some kind of useful functionality right away. Right now, only registrations may be processed. We also have limited some of the options which are available to the GUI tool. The following bag metadata and package generation parameters are supplied to the CLI internally:
```
#***************************************************
# The following fields are REQUIRED to be supplied
#***************************************************
Package-Format-Id = BOREM
BagIt-Profile-Identifier = http://dataconservancy.org/formats/data-conservancy-pkg-1.0

#***************************************************
# The following fields SHOULD be supplied
#***************************************************
#Options for Checksum algorimth are: md5, sha1
Checksum-Algs = md5
#Options for Archiving-format are: tar, zip, none
Archiving-Format = tar
#Options for Compression-format are: gz, none
Compression-Format = gz
#Options for ReM-Serialization-Format are: json, turtle, xml
ReM-Serialization-Format = TURTLE
```
