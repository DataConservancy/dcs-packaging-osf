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
package org.dataconservancy.cos.osf.packaging.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by esm on 6/1/16.
 */
public class Rdf {

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

    public static class OwlClass {

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

    public static class Property {
        private String ns = Ns.OSF;
        private String name;
        private boolean isObject = false;

        public Property(String name) {
            this.name = name;
        }

        public Property(String name, boolean isObject) {
            this.name = name;
            this.isObject = isObject;
        }

        public Property(String ns, String name) {
            this.ns = ns;
            this.name = name;
        }

        public Property(String ns, String name, boolean isObject) {
            this.ns = ns;
            this.name = name;
            this.isObject = isObject;
        }

        public String getNs() {
            return ns;
        }

        public void setNs(String ns) {
            this.ns = ns;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isObject() {
            return isObject;
        }

        public void setObject(boolean object) {
            isObject = object;
        }
    }

    public static class ObjectProperty {

        public static final Property OSF_HASFILE = new Property("hasFile", true);

        public static final Property OSF_HASCONTRIBUTOR = new Property("hasContributor", true);

        public static final Property OSF_HASLINK = new Property("hasLink", true);

        public static final Property OSF_HASNODE = new Property("hasNode", true);

        public static final Property OSF_HASRELATIONSHIP = new Property("hasRelationship", true);

        public static final Property OSF_HASRELATEDLINK = new Property("hasRelatedLink", true);

        public static final Property OSF_REGISTERED_BY = new Property("registeredBy", true);

        public static final Property OSF_REGISTERED_FROM = new Property("registeredFrom", true);

    }

    public static class DatatypeProperty {

        public static final Property DCTERMS_DESCRIPTION = new Property("description");

        public static final Property DCTERMS_IDENTIFIER = new Property("identifier");

        public static final Property DCTERMS_TITLE = new Property("title");

        public static final Property OSF_HAS_ACADEMICINSTITUTION = new Property("hasAcademicInstitution");

        public static final Property OSF_HAS_ACADEMICPROFILEID = new Property("hasAcademicProfileId");

        public static final Property OSF_IS_ACTIVE = new Property("isActive");

        public static final Property OSF_HAS_BAIDUID = new Property("hasBaiduId");

        public static final Property OSF_IS_BIBLIOGRAPHIC = new Property("isBibliographic");

        public static final Property OSF_HAS_CATEGORY = new Property("hasCategory");

        public static final Property OSF_IS_COLLECTION = new Property("isCollection");

        public static final Property OSF_IS_BOOKMARK = new Property("isBookmark");

        public static final Property OSF_HAS_CONTENTTYPE = new Property("hasContentType");

        public static final Property OSF_HAS_CURRENTUSERPERMISSION = new Property("hasCurrentUserPermissions");

        public static final Property OSF_IS_DASHBOARD = new Property("isDashboard");

        public static final Property OSF_HAS_DATECREATED = new Property("hasDateCreated");

        public static final Property OSF_HAS_DATEMODIFIED = new Property("hasDateModified");

        public static final Property OSF_HAS_DATEREGISTERED = new Property("hasDateRegistered");

        public static final Property OSF_HAS_DATEUSERREGISTERED = new Property("hasDateUserRegistered");

        public static final Property OSF_HAS_DESCRIPTION = new Property("hasDescription");

        public static final Property OSF_HAS_EMBARGOENDDATE = new Property("hasEmbargoEndDate");

        public static final Property OSF_HAS_ETAG = new Property("hasEtag");

        public static final Property OSF_HAS_EXTRA = new Property("hasExtra");

        public static final Property OSF_HAS_HASFAMILYNAME = new Property("hasFamilyName");

        public static final Property OSF_IS_FORK = new Property("isFork");

        public static final Property OSF_HAS_FULLNAME = new Property("hasFullName");

        public static final Property OSF_HAS_HASGITHUB = new Property("hasGitHub");

        public static final Property OSF_HAS_GIVENNAME = new Property("hasGivenName");

        public static final Property OSF_HAS_HREF = new Property("hasHref");

        public static final Property OSF_HAS_ID = new Property("hasId");

        public static final Property OSF_HAS_IMPACTSTORY = new Property("hasImpactStory");

        public static final Property OSF_HAS_HASKIND = new Property("hasKind");

        public static final Property OSF_HAS_LASTTOUCHED = new Property("hasLastTouched");

        public static final Property OSF_HAS_LINKEDIN = new Property("hasLinkedIn");

        public static final Property OSF_HAS_LOCALE = new Property("hasLocale");

        public static final Property OSF_HAS_MATERIALIZEDPATH = new Property("hasMaterializedPath");

        public static final Property OSF_HAS_META = new Property("hasMeta");

        public static final Property OSF_HAS_MIDDLENAMES = new Property("hasMiddleNames");

        public static final Property OSF_HAS_NAME = new Property("hasName");

        public static final Property OSF_HAS_PATH = new Property("hasPath");

        public static final Property OSF_IS_PENDINGEMBARGOAPPROVAL = new Property("isPendingEmbargoApproval");

        public static final Property OSF_IS_PENDINGWITHDRAWL = new Property("isPendingRegistrationApproval");

        public static final Property OSF_IS_PENDINGREGISTRATIONAPPROVAL = new Property("isPendingRegistrationApproval");

        public static final Property OSF_HAS_PERSONALWEBSITE = new Property("hasPersonalWebsite");

        public static final Property OSF_HAS_PERMISSION = new Property("hasPermission");

        public static final Property OSF_HAS_HASPROVIDER = new Property("hasProvider");

        public static final Property OSF_IS_PUBLIC = new Property("isPublic");

        public static final Property OSF_IS_REGISTRATION = new Property("isRegistration");

        public static final Property OSF_HAS_REGISTRATIONSUPPLEMENT = new Property("hasRegistrationSupplement");

        public static final Property OSF_HAS_RELATIONSHIPTYPE = new Property("hasRelationshipType");

        public static final Property OSF_HAS_RELATEDLINKTYPE = new Property("hasRelatedLinkType");

        public static final Property OSF_HAS_RESEARCHERID = new Property("hasResearcherId");

        public static final Property OSF_HAS_RESEARCHGATE = new Property("hasResearchGate");

        public static final Property OSF_IS_RETRACTED = new Property("isRetracted");

        public static final Property OSF_HAS_RETRACTIONJUSTIFICATION = new Property("hasRetractionJustification");

        public static final Property OSF_HAS_SCHOLAR = new Property("hasScholar");

        public static final Property OSF_HAS_SIZE = new Property("hasSize");

        public static final Property OSF_HAS_SUFFIX = new Property("hasSuffix");

        public static final Property OSF_HAS_HASTIMEZONE = new Property("hasTimezone");

        public static final Property OSF_HAS_TITLE = new Property("hasTitle");

        public static final Property OSF_HAS_TWITTER = new Property("hasTwitter");

        public static final Property OSF_HAS_TYPE = new Property("hasType");

        public static final Property OSF_IS_WITHDRAWN = new Property("isWithdrawn");
    }

}
