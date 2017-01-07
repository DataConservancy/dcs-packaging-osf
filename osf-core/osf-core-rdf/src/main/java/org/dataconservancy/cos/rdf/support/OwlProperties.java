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
 * Supported OSF OWL properties.  Java fields annotated with {@code org.dataconservancy.cos.rdf.annotations.OwlProperty}
 * may be mapped to the OWL properties enumerated here.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public enum OwlProperties {

    OSF_HAS_FILE ("hasFile", true),

    OSF_HAS_CONTRIBUTOR ("hasContributor", true),

    OSF_HAS_LINK ("hasLink", true),

    OSF_HAS_NODE ("hasNode", true),

    OSF_HAS_CHILD ("hasChild", true),

    OSF_HAS_PARENT ("hasParent", true),

    OSF_HAS_ROOT ("hasRoot", true),

    OSF_HAS_RELATIONSHIP ("hasRelationship", true),

    OSF_HAS_RELATEDLINK ("hasRelatedLink", true),

    OSF_REGISTERED_BY ("registeredBy", true),

    OSF_REGISTERED_FROM ("registeredFrom", true),

    OSF_FORKED_FROM ("forkedFrom", true),

    OSF_HAS_USER ("hasUser", true),

    OSF_HAS_LOG_AGENT("hasLogAgent", true),

    OSF_HAS_LOG_SOURCE("hasLogSource", true),

    OSF_HAS_LOG_TARGET("hasLogTarget", true),

    OSF_HAS_LICENSE ("hasLicense", true),

    OSF_HAS_HASPROVIDER ("hasProvider", true),

    OSF_PROVIDED_BY ("providedBy", true),

    OSF_AUTHORED_BY ("authoredBy", true),

    OSF_HAS_WIKI ("hasWiki", true),

    OSF_HAS_COMMENT ("hasComment", true),

    DCTERMS_DESCRIPTION (Rdf.Ns.DCTERMS, "description"),

    DCTERMS_IDENTIFIER (Rdf.Ns.DCTERMS, "identifier"),

    DCTERMS_TITLE (Rdf.Ns.DCTERMS, "title"),

    OSF_HAS_ACADEMICINSTITUTION ("hasAcademicInstitution"),

    OSF_HAS_ACADEMICPROFILEID ("hasAcademicProfileId"),

    OSF_IS_ACTIVE ("isActive"),

    OSF_HAS_BAIDUID ("hasBaiduId"),

    OSF_IS_BIBLIOGRAPHIC ("isBibliographic"),

    OSF_HAS_CATEGORY ("hasCategory"),

    OSF_IS_COLLECTION ("isCollection"),

    OSF_IS_BOOKMARK ("isBookmark"),

    OSF_HAS_CONTENT ("hasContent"),

    OSF_HAS_CONTENTTYPE ("hasContentType"),

    OSF_IS_DASHBOARD ("isDashboard"),

    OSF_HAS_DATECREATED ("hasDateCreated"),

    OSF_HAS_DATEMODIFIED ("hasDateModified"),

    OSF_HAS_DATEREGISTERED ("hasDateRegistered"),

    OSF_HAS_DATEUSERREGISTERED ("hasDateUserRegistered"),

    OSF_HAS_DESCRIPTION ("hasDescription"),

    OSF_HAS_EMBARGOENDDATE ("hasEmbargoEndDate"),

    OSF_HAS_TAG ("hasTag"),

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

    OSF_IS_WITHDRAWN ("isWithdrawn"),

    OSF_HAS_WITHDRAW_JUSTIFICATION ("hasWithdrawJustification"),

    OSF_HAS_LOG_DATE("hasLogDate"),

    OSF_HAS_LOG_ACTION("hasLogAction"),

    OSF_HAS_LICENSE_NAME ("hasLicenseName"),

    OSF_HAS_LICENSE_TEXT ("hasLicenseText"),

    OSF_HAS_BINARYURI ("hasBinaryUri"),

    OSF_PROVIDER_NAME ("providerName"),

    OSF_HAS_ORCID ("hasOrcid"),

    OSF_VERSION ("version"),

    OSF_IN_REPLY_TO ("inReplyTo");

    private String ns = Rdf.Ns.OSF;

    private final String localname;

    private boolean isObject = false;

    private OwlProperties(final String localname) {
        this.localname = localname;
    }

    private OwlProperties(final String ns, final String localname) {
        this.ns = ns;
        this.localname = localname;
    }

    private OwlProperties(final String localname, final boolean isObject) {
        this.localname = localname;
        this.isObject = isObject;
    }

    private OwlProperties(final String ns, final String localname, final boolean isObject) {
        this.ns = ns;
        this.localname = localname;
        this.isObject = isObject;
    }

    /**
     * The RDF namespace of the property.
     *
     * @return the namespace
     */
    public String ns() {
        return ns;
    }

    /**
     * The RDF name of the property sans namespace.
     *
     * @return the local name
     */
    public String localname() {
        return localname;
    }

    /**
     * The fully qualified RDF name of this property.
     *
     * @return the fully qualified name
     */
    public String fqname() {
        return ns + localname;
    }

    /**
     * TODO: Reconsider if this needs to be represented in the Java.  The property definition in the ontology
     * TODO: will have this information.  If the Java code retrieves the property definition from the ontology,
     * TODO: it can determine whether or not it is an ObjectProperty.  Removing this information from the Java model
     * TODO: eliminates a potential issue where a property is considered a ObjectProperty according to Java but
     * TODO: is defined as a DatatypeProperty according to the ontology.
     * Whether or not the property represents an OWL Object property or Datatype property.
     *
     * @return true if the property is an OWL Object property
     */
    public boolean object() {
        return isObject;
    }
}
