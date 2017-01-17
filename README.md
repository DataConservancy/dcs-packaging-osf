# About

The code in this repository represents an effort to actively archive and preserve the content maintained by the [Open Science Framework][osf-home].  To mitigate any coupling to the internal implementation of the OSF, this code interacts with the [OSF HTTP-based API][osf-api]; the services developed here are external to the OSF.

# Key Business Concepts 

* The OSF is intended to be a part of a researcher's workflow
* Many data curation activities are not a part of a researcher's workflow
* Individual institutions may have data curation requirements not satisfied by the general OSF framework or individual OSF storage providers chosen by a researcher (e.g. figshare).
* The OSF UI and model allows collaborators with different roles (e.g. curators) to contribute to a project in OSF

Package export/import would be the basis for incorporation of curation activities that do not occur through the OSF user interface, including:

* Automated activities (e.g. archiving, format migration, content type detection, etc)
* Activities that occur through specialized tooling (e.g. Data Conservancy package tool GUI)

# Key Technical Concepts

* The [BagIt specification][bagit-spec] provides a mechanism for packaging and verifying digital content for transfer or archiving
* The [Data Conservancy packaging specification][dc-packaging-spec] builds on BagIt, and provides:
  * Mechanisms for distinguishing domain/business objects from binary content
  * Mechanisms for handling link resolution between objects
* The DC packaging specification can be used to package data files in OSF as well as OSF business object(s) that describe the component that contains them, the provider they came from, metadata data which may establish provenance, etc.

# Anticipated Future Use Cases

Import/export of packages from OSF could be a building block of several kinds of use cases:
* Pulling content into a tool for manual curation (e.g. the [Data Conservancy package tool GUI][dc-ptg])
* Separately archiving or preserving content in OSF, regardless of its native provider (e.g. figshare, github, dropbox, etc)
* Synchronizing locally modified (manual or automated) content with OSF content
* Importing locally generated content into the OSF.
* Specialized (local) indexing of content stored in OSF
* Exposing OSF content as linked data (via the [Data Conservancy package ingest service][dc-pis])

# Project Descriptions

## [Core OSF Libraries](osf-core/)

Provides the OSF Java object model and an annotations library used to generate an RDF model from instances of the Java model.

## [OSF Client Libraries](osf-client/)

Provides a Java API for communicating with the HTTP-based OSF API.

## [OSF Packaging Libraries](osf-packager/)

Provides a Java command-line client for producing Data Conservancy packages from OSF registrations.


[dc-packaging-spec]: http://dataconservancy.github.io/dc-packaging-spec/dc-packaging-spec-1.0.html "Data Conservancy Packaging Specification"
[dc-ptg]: https://github.com/DataConservancy/dcs-packaging-tool "Data Conservancy Package Tool GUI"
[dc-pis]: https://github.com/DataConservancy/dcs-package-ingest "Data Conservancy Package Ingest Tool"
[overview-img]: https://github.com/DataConservancy/osf-packaging/blob/master/src/site/resources/images/osf-package-ingest.png "OSF Packaging Overview"
[osf-api]: https://api.osf.io/v2/ "OSF v2 API"
[bagit-spec]: https://tools.ietf.org/html/draft-kunze-bagit-14 "IETF BagIt Draft Specification"
[osf-home]: http://osf.io "Open Science Framework homepage"