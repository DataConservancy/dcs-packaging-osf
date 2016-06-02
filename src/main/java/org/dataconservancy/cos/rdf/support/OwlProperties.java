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
package org.dataconservancy.cos.rdf.support;

/**
 * Supported OWL properties.  Java fields annotated with {@link org.dataconservancy.cos.rdf.annotations.OwlProperty} may
 * be mapped to the OWL properties enumerated here.
 */
public enum OwlProperties {

    OSF_HASFILE ("hasFile", true),

    OSF_HASCONTRIBUTOR ("hasContributor", true),

    OSF_HASLINK ("hasLink", true),

    OSF_HASNODE ("hasNode", true),

    OSF_HASRELATIONSHIP ("hasRelationship", true),

    OSF_HASRELATEDLINK ("hasRelatedLink", true),

    OSF_REGISTERED_BY ("registeredBy", true),

    OSF_REGISTERED_FROM ("registeredFrom", true),

    DCTERMS_DESCRIPTION ("description"),

    DCTERMS_IDENTIFIER ("identifier"),

    DCTERMS_TITLE ("title"),

    OSF_HAS_ACADEMICINSTITUTION ("hasAcademicInstitution"),

    OSF_HAS_ACADEMICPROFILEID ("hasAcademicProfileId"),

    OSF_IS_ACTIVE ("isActive"),

    OSF_HAS_BAIDUID ("hasBaiduId"),

    OSF_IS_BIBLIOGRAPHIC ("isBibliographic"),

    OSF_HAS_CATEGORY ("hasCategory"),

    OSF_IS_COLLECTION ("isCollection"),

    OSF_IS_BOOKMARK ("isBookmark"),

    OSF_HAS_CONTENTTYPE ("hasContentType"),

    OSF_HAS_CURRENTUSERPERMISSION ("hasCurrentUserPermissions"),

    OSF_IS_DASHBOARD ("isDashboard"),

    OSF_HAS_DATECREATED ("hasDateCreated"),

    OSF_HAS_DATEMODIFIED ("hasDateModified"),

    OSF_HAS_DATEREGISTERED ("hasDateRegistered"),

    OSF_HAS_DATEUSERREGISTERED ("hasDateUserRegistered"),

    OSF_HAS_DESCRIPTION ("hasDescription"),

    OSF_HAS_EMBARGOENDDATE ("hasEmbargoEndDate"),

    OSF_HAS_ETAG ("hasEtag"),

    OSF_HAS_EXTRA ("hasExtra"),

    OSF_HAS_HASFAMILYNAME ("hasFamilyName"),

    OSF_IS_FORK ("isFork"),

    OSF_HAS_FULLNAME ("hasFullName"),

    OSF_HAS_HASGITHUB ("hasGitHub"),

    OSF_HAS_GIVENNAME ("hasGivenName"),

    OSF_HAS_HREF ("hasHref"),

    OSF_HAS_ID ("hasId"),

    OSF_HAS_IMPACTSTORY ("hasImpactStory"),

    OSF_HAS_HASKIND ("hasKind"),

    OSF_HAS_LASTTOUCHED ("hasLastTouched"),

    OSF_HAS_LINKEDIN ("hasLinkedIn"),

    OSF_HAS_LOCALE ("hasLocale"),

    OSF_HAS_MATERIALIZEDPATH ("hasMaterializedPath"),

    OSF_HAS_META ("hasMeta"),

    OSF_HAS_MIDDLENAMES ("hasMiddleNames"),

    OSF_HAS_NAME ("hasName"),

    OSF_HAS_PATH ("hasPath"),

    OSF_IS_PENDINGEMBARGOAPPROVAL ("isPendingEmbargoApproval"),

    OSF_IS_PENDINGWITHDRAWL ("isPendingRegistrationApproval"),

    OSF_IS_PENDINGREGISTRATIONAPPROVAL ("isPendingRegistrationApproval"),

    OSF_HAS_PERSONALWEBSITE ("hasPersonalWebsite"),

    OSF_HAS_PERMISSION ("hasPermission"),

    OSF_HAS_HASPROVIDER ("hasProvider"),

    OSF_IS_PUBLIC ("isPublic"),

    OSF_IS_REGISTRATION ("isRegistration"),

    OSF_HAS_REGISTRATIONSUPPLEMENT ("hasRegistrationSupplement"),

    OSF_HAS_RELATIONSHIPTYPE ("hasRelationshipType"),

    OSF_HAS_RELATEDLINKTYPE ("hasRelatedLinkType"),

    OSF_HAS_RESEARCHERID ("hasResearcherId"),

    OSF_HAS_RESEARCHGATE ("hasResearchGate"),

    OSF_IS_RETRACTED ("isRetracted"),

    OSF_HAS_RETRACTIONJUSTIFICATION ("hasRetractionJustification"),

    OSF_HAS_SCHOLAR ("hasScholar"),

    OSF_HAS_SIZE ("hasSize"),

    OSF_HAS_SUFFIX ("hasSuffix"),

    OSF_HAS_HASTIMEZONE ("hasTimezone"),

    OSF_HAS_TITLE ("hasTitle"),

    OSF_HAS_TWITTER ("hasTwitter"),

    OSF_HAS_TYPE ("hasType"),

    OSF_IS_WITHDRAWN ("isWithdrawn");

    private String ns = Rdf.Ns.OSF;
    
    private String localname;
    
    private boolean isObject = false;
    
    private OwlProperties(String localname) {
        this.localname = localname;
    }
    
    private OwlProperties(String ns, String localname) {
        this.ns = ns;
        this.localname = localname;
    }
    
    private OwlProperties(String localname, boolean isObject) {
        this.localname = localname;
        this.isObject = isObject;
    }

    private OwlProperties(String ns, String localname, boolean isObject) {
        this.ns = ns;
        this.localname = localname;
        this.isObject = isObject;
    }

    public String ns() {
        return ns;
    }

    public String localname() {
        return localname;
    }
    
    public String fqlocalname() {
        return ns + localname;
    }

    public boolean object() {
        return isObject;
    }
}
