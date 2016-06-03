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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by esm on 6/1/16.
 */
public class OldRdf {

    public static class Ns {

        public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        public static final String OWL = "http://www.w3.org/2002/07/owl#";

        public static final String XSD = "http://www.w3.org/2001/XMLSchema#";

        public static final String DCTERMS = "http://purl.org/dc/terms/";

        public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";

        public static final String ORE = "http://www.openarchives.org/ore/terms/";

        public static final String OSF = "http://www.dataconservancy.org/osf-business-object-model#";

        public static final Map<String, String> PREFIXES = new HashMap<String, String>() {
            {
                put("rdf", RDF);
                put("owl", OWL);
                put("xsd", XSD);
                put("dcterms", DCTERMS);
                put("rdfs", RDFS);
                put("ore", ORE);
                put("osf", OSF);
            }
        };

    }

    public class ObjectProperty {

        public static final String OSF_HASFILE = "hasFile";

        public static final String OSF_HASCONTRIBUTOR = "hasContributor";

        public static final String OSF_HASLINK = "hasLink";

        public static final String OSF_HASNODE = "hasNode";

        public static final String OSF_HASRELATIONSHIP = "hasRelationship";

        public static final String OSF_HASRELATEDLINK = "hasRelatedLink";

        public static final String OSF_REGISTERED_BY = "registeredBy";

        public static final String OSF_REGISTERED_FROM = "registeredFrom";

    }

    public class DatatypeProperty {

        public static final String DCTERMS_DESCRIPTION = "description";

        public static final String DCTERMS_IDENTIFIER = "identifier";

        public static final String DCTERMS_TITLE = "title";

        public static final String OSF_HAS_ACADEMICINSTITUTION = "hasAcademicInstitution";

        public static final String OSF_HAS_ACADEMICPROFILEID = "hasAcademicProfileId";

        public static final String OSF_IS_ACTIVE = "isActive";

        public static final String OSF_HAS_BAIDUID = "hasBaiduId";

        public static final String OSF_IS_BIBLIOGRAPHIC = "isBibliographic";

        public static final String OSF_HAS_CATEGORY = "hasCategory";

        public static final String OSF_IS_COLLECTION = "isCollection";

        public static final String OSF_IS_BOOKMARK = "isBookmark";

        public static final String OSF_HAS_CONTENTTYPE = "hasContentType";

        public static final String OSF_HAS_CURRENTUSERPERMISSION = "hasCurrentUserPermissions";

        public static final String OSF_IS_DASHBOARD = "isDashboard";

        public static final String OSF_HAS_DATECREATED = "hasDateCreated";

        public static final String OSF_HAS_DATEMODIFIED = "hasDateModified";

        public static final String OSF_HAS_DATEREGISTERED = "hasDateRegistered";

        public static final String OSF_HAS_DATEUSERREGISTERED = "hasDateUserRegistered";

        public static final String OSF_HAS_DESCRIPTION = "hasDescription";

        public static final String OSF_HAS_EMBARGOENDDATE = "hasEmbargoEndDate";

        public static final String OSF_HAS_ETAG = "hasEtag";

        public static final String OSF_HAS_EXTRA = "hasExtra";

        public static final String OSF_HAS_HASFAMILYNAME = "hasFamilyName";

        public static final String OSF_IS_FORK = "isFork";

        public static final String OSF_HAS_FULLNAME = "hasFullName";

        public static final String OSF_HAS_HASGITHUB = "hasGitHub";

        public static final String OSF_HAS_GIVENNAME = "hasGivenName";

        public static final String OSF_HAS_HREF = "hasHref";

        public static final String OSF_HAS_ID = "hasId";

        public static final String OSF_HAS_IMPACTSTORY = "hasImpactStory";

        public static final String OSF_HAS_HASKIND = "hasKind";

        public static final String OSF_HAS_LASTTOUCHED = "hasLastTouched";

        public static final String OSF_HAS_LINKEDIN = "hasLinkedIn";

        public static final String OSF_HAS_LOCALE = "hasLocale";

        public static final String OSF_HAS_MATERIALIZEDPATH = "hasMaterializedPath";

        public static final String OSF_HAS_META = "hasMeta";

        public static final String OSF_HAS_MIDDLENAMES = "hasMiddleNames";

        public static final String OSF_HAS_NAME = "hasName";

        public static final String OSF_HAS_PATH = "hasPath";

        public static final String OSF_IS_PENDINGEMBARGOAPPROVAL = "isPendingEmbargoApproval";

        public static final String OSF_IS_PENDINGREGISTRATIONAPPROVAL = "isPendingRegistrationApproval";

        public static final String OSF_HAS_PERSONALWEBSITE = "hasPersonalWebsite";

        public static final String OSF_HAS_PERMISSION = "hasPermission";

        public static final String OSF_HAS_HASPROVIDER = "hasProvider";

        public static final String OSF_IS_PUBLIC = "isPublic";

        public static final String OSF_IS_REGISTRATION = "isRegistration";

        public static final String OSF_HAS_REGISTRATIONSUPPLEMENT = "hasRegistrationSupplement";

        public static final String OSF_HAS_RELATIONSHIPTYPE = "hasRelationshipType";

        public static final String OSF_HAS_RELATEDLINKTYPE = "hasRelatedLinkType";

        public static final String OSF_HAS_RESEARCHERID = "hasResearcherId";

        public static final String OSF_HAS_RESEARCHGATE = "hasResearchGate";

        public static final String OSF_IS_RETRACTED = "isRetracted";

        public static final String OSF_HAS_RETRACTIONJUSTIFICATION = "hasRetractionJustification";

        public static final String OSF_HAS_SCHOLAR = "hasScholar";

        public static final String OSF_HAS_SIZE = "hasSize";

        public static final String OSF_HAS_SUFFIX = "hasSuffix";

        public static final String OSF_HAS_HASTIMEZONE = "hasTimezone";

        public static final String OSF_HAS_TITLE = "hasTitle";

        public static final String OSF_HAS_TWITTER = "hasTwitter";

        public static final String OSF_HAS_TYPE = "hasType";

    }

    public class OwlClass {

        public static final String OSF_BO = "OsfObject";

        public static final String OSF_USER = "User";

        public static final String OSF_NODEBASE = "NodeBase";

        public static final String OSF_NODE = "Node";

        public static final String OSF_REGISTRATION = "Registration";

        public static final String OSF_COLLECTION = "Collection";

        public static final String OSF_DATAENTITY = "DataEntity";

        public static final String OSF_CONTRIBUTOR = "Contributor";

        public static final String OSF_FILEBASE = "FileBase";

        public static final String OSF_FILE = "File";

        public static final String OSF_FOLDER = "Folder";

        public static final String OSF_FILEENTITY = "FileEntity";

    }

}
