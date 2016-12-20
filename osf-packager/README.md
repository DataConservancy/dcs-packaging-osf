# COS OSF Proposal: Packages for import/export

## Proposal 
Develop external services (separate from OSF code base) to export OSF business objects and associated resources to DC Packages. (Import from DC packages to OSF is anticipated as a next step.)

## Importance 

If we envision DC as providing curation services to data in OSF, package import/export provides a basis for mechanisms of transfer of archivally-relevant materials between OSF and DC services.

## Value add 

Data Conservancy develops an RDF-based OSF data model, which could be leveraged for future use cases.  OSF would be able to include preservation and curation events in the OSF UI activity feeds.

![Overview of the OSF packaging proposal][overview-img]

## Key Business Concepts 

* OSF is intended to be a part of a researcher's’ workflow
* Many data curation activities are not a part of a researcher's’ workflow
* Individual institutions may have data curation requirements not satisfied by the general OSF framework or individual OSF storage providers chosen by a researcher (e.g. figshare).
* The OSF UI and model allows collaborators with different roles (e.g. curators) to contribute to a project in OSF
Package export/import would be the basis for incorporation of curation activities that do not occur through the OSF user interface, including
  * Automated activities (e.g. archiving, format migration, content type detection, etc)
  * Activities that occur through specialized tooling (e.g. Package Tool GUI)

## Key Technical Concepts

* The [Bagit Spec][dc-bagit-profile] provides a mechanism for packaging and verifying digital content for transfer or archiving
* The [DC Packaging spec][dc-packaging-spec] builds on BagIt, and provides:
  * Mechanisms for distinguishing domain/business objects from binary content
  * Mechanisms for handling link resolution between objects
* The DC Packaging spec can be used to package data files in OSF as well as OSF business object(s) that describe the component that contains them, the provider they came from, metadata data which may establish provenance, etc.

## Anticipated Future Use Cases

Import/export of packages from OSF could be a building block of several kinds of use cases:
* Pulling content into a tool for manual curation (e.g. the [Package tool GUI][dc-ptg])
* Separately archiving or preserving content in OSF, regardless of its native provider (e.g. figshare, github, dropbox, etc)
* Synchronizing locally modified (manual or automated) content with OSF content
* Importing locally generated content into the OSF.
* Specialized (local) indexing of content stored in OSF
* Exposing OSF content as linked data (via the [Package Ingest Service][dc-pis])

## Risks

* In the DC Packaging spec, domain objects must have an RDF representation.  In theory, OSF uses JSON to represent to represent “its business objects”.   There could possibly be  json schemas available, but it is unclear where.  We’d need to ask.  
* It’s unclear if they have a defined object model at all
* We would need to confirm that the Django API supports everything that we need to do.

# Developer Notes

Currently Maven SNAPSHOT artifacts are not published to a reachable Maven repository.  However, our release artifacts are reachable.  We plan to publish SNAPSHOT artifacts to Sonatype; in the interim you may either check out and build a tag (which will pull publicly available release artifacts), or you may manually build and install SNAPSHOT dependencies.

## Build Requirements

* Java 8
* Maven 3.3.x

## Test Drive

If you simply want give the CLI a test drive, the best way to do this right now is to clone this repository, check out the latest tag, and perform a `mvn install`.  The CLI will be compiled and assembled in the `osf-cli/target/osf-cli-<version>.jar`  The [README](osf-cli/README.md) contains further instructions on getting started with the CLI.

## Snapshot Dependencies

If you wish to build and install our SNAPSHOTs, read on.  You need three dependencies, installed in the following order:

* The [jsonapi-converter](https://github.com/DataConservancy/jsonapi-converter)
* Our home-grown (!) [RDF annotation framework](https://github.com/DataConservancy/osf-rdf)
* Finally, the [OSF Java Client](https://github.com/DataConservancy/osf-client)

After installing these dependencies locally (by running `mvn install` for each dependency), you ought to be able to perform a `mvn install` of this code repository.




[dc-bagit-profile]: http://dataconservancy.github.io/dc-packaging-spec/dc-bagit-profile-1.0.html "Data Conservancy BagIt Profile"
[dc-packaging-spec]: http://dataconservancy.github.io/dc-packaging-spec/dc-packaging-spec-1.0.html "Data Conservancy Packaging Specification"
[dc-ptg]: https://github.com/DataConservancy/dcs-packaging-tool "Data Conservancy Package Tool GUI"
[dc-pis]: https://github.com/DataConservancy/dcs-package-ingest "Data Conservancy Package Ingest Tool"
[overview-img]: https://github.com/DataConservancy/osf-packaging/blob/master/src/site/resources/images/osf-package-ingest.png "OSF Packaging Overview"
