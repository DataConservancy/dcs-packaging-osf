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

package org.dataconservancy.cos.osf.client.model;

import static org.dataconservancy.cos.osf.client.support.JodaSupport.DATE_TIME_FORMATTER_ALT;

import java.util.List;
import java.util.Map;

import org.dataconservancy.cos.osf.client.support.DateTimeTransform;
import org.dataconservancy.cos.osf.client.support.JodaSupport;
import org.dataconservancy.cos.osf.client.support.UrlToIdTransform;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.RelType;
import com.github.jasminb.jsonapi.ResolutionStrategy;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;

/**
 * OSF Registration model, an extension of the Node model
 * @author khanson
 */
@Type("registrations")
@JsonIgnoreProperties(ignoreUnknown = true)
@OwlIndividual(OwlClasses.OSF_REGISTRATION)
public class Registration extends NodeBase  {

    /**List of nodes that are children of this node.*/
    @Relationship(value = "children", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
    @OwlProperty(OwlProperties.OSF_HAS_CHILD)
    protected List<Registration> children;

    /**Is this registered node visible on the user dashboard?**/
    @OwlProperty(OwlProperties.OSF_IS_DASHBOARD)
    private Boolean isDashboard;

    /**Has this registration been withdrawn?**/
    @OwlProperty(OwlProperties.OSF_IS_WITHDRAWN)
    private Boolean isWithdrawn;

    /**    Timestamp that the registration was created */
    @OwlProperty(value = OwlProperties.OSF_HAS_DATEREGISTERED, transform = DateTimeTransform.class)
    private DateTime date_registered;

    /** When the embargo on this registration will be lifted (if applicable) */
    @OwlProperty(value = OwlProperties.OSF_HAS_EMBARGOENDDATE, transform = DateTimeTransform.class)
    private DateTime embargo_end_date;

    /**Reasons for withdrawing the registration*/
    // TODO: @OwlProperty()
    private String withdrawal_justification;

    /** Is this registration pending withdrawal?*/
    @OwlProperty(OwlProperties.OSF_IS_PENDINGWITHDRAWL)
    private Boolean isPending_withdrawal;

    /**Is this registration pending approval?*/
    @OwlProperty(OwlProperties.OSF_IS_PENDINGREGISTRATIONAPPROVAL)
    private Boolean isPending_registration_approval;

    /** Is the associated Embargo awaiting approval by project admins? */
    @OwlProperty(OwlProperties.OSF_IS_PENDINGEMBARGOAPPROVAL)
    private Boolean isPending_embargo_approval;

    /**registration supplementary information*/
    // TODO @OwlProperty
    private Map<String, RegistrationMetadata> registered_meta;

    /**registration template used*/
    @OwlProperty(OwlProperties.OSF_HAS_REGISTRATIONSUPPLEMENT)
    private String registration_supplement;

    /**Node registered from who are contributors to this node. */
    @Relationship(value = "registered_from", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    @OwlProperty(value = OwlProperties.OSF_REGISTERED_FROM, transform = UrlToIdTransform.class)
    private String registered_from;

    @Relationship(value = "registered_by", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    @OwlProperty(value = OwlProperties.OSF_REGISTERED_BY, transform = UrlToIdTransform.class)
    private String registered_by;

    @Relationship(value = "linked_registrations", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String linked_registrations;

    @Relationship(value = "registration_schema", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String registration_schema;

    /**
     *
     * @return
     */
    public List<Registration> getChildren() {
        return children;
    }

    /**
     *
     * @param children
     */
    public void setChildren(final List<Registration> children) {
        this.children = children;
    }

    /**
     *
     * @return
     */
    public Boolean isDashboard() {
        return isDashboard;
    }

    /**
     *
     * @param isDashboard
     */
    public void setDashboard(final Boolean isDashboard) {
        this.isDashboard = isDashboard;
    }

    /**
     *
     * @return
     */
    public Boolean isWithdrawn() {
        return isWithdrawn;
    }

    /**
     *
     * @param isWithdrawn
     */
    public void setWithdrawn(final Boolean isWithdrawn) {
        this.isWithdrawn = isWithdrawn;
    }

    /**
     *
     * @return
     */
    public String getDate_registered() {
        if (this.date_registered != null) {
            return this.date_registered.toString(DATE_TIME_FORMATTER_ALT);
        } else {
            return null;
        }
    }

    /**
     *
     * @param date_registered
     */
    public void setDate_registered(final String date_registered) {
        if (date_registered != null) {
            this.date_registered = JodaSupport.parseDateTime(date_registered);
        } else {
            this.date_registered = null;
        }
    }

    /**
     *
     * @return
     */
    public String getEmbargo_end_date() {
        if (this.embargo_end_date != null) {
            return this.embargo_end_date.toString(DATE_TIME_FORMATTER_ALT);
        } else {
            return null;
        }
    }

    /**
     *
     * @param embargo_end_date
     */
    public void setEmbargo_end_date(final String embargo_end_date) {
        if (embargo_end_date != null) {
            this.embargo_end_date = JodaSupport.parseDateTime(embargo_end_date);
        } else {
            this.embargo_end_date = null;
        }
    }

    /**
     *
     * @return
     */
    public String getWithdrawal_justification() {
        return withdrawal_justification;
    }

    /**
     *
     * @param withdrawal_justification
     */
    public void setWithdrawal_justification(final String withdrawal_justification) {
        this.withdrawal_justification = withdrawal_justification;
    }

    /**
     *
     * @return
     */
    public Boolean isPending_withdrawal() {
        return isPending_withdrawal;
    }

    /**
     *
     * @param isPending_withdrawal
     */
    public void setPending_withdrawal(final Boolean isPending_withdrawal) {
        this.isPending_withdrawal = isPending_withdrawal;
    }

    /**
     *
     * @return
     */
    public Boolean isPending_registration_approval() {
        return isPending_registration_approval;
    }

    /**
     *
     * @param isPending_registration_approval
     */
    public void setPending_registration_approval(final Boolean isPending_registration_approval) {
        this.isPending_registration_approval = isPending_registration_approval;
    }

    /**
     *
     * @return
     */
    public Boolean isPending_embargo_approval() {
        return isPending_embargo_approval;
    }

    /**
     *
     * @param isPending_embargo_approval
     */
    public void setPending_embargo_approval(final Boolean isPending_embargo_approval) {
        this.isPending_embargo_approval = isPending_embargo_approval;
    }

    /**
     *
     * @return
     */
    public Map<String, RegistrationMetadata> getRegistered_meta() {
        return registered_meta;
    }

    /**
     *
     * @param registered_meta
     */
    public void setRegistered_meta(final Map<String, RegistrationMetadata> registered_meta) {
        this.registered_meta = registered_meta;
    }

    /**
     *
     * @return
     */
    public String getRegistration_supplement() {
        return registration_supplement;
    }

    /**
     *
     * @param registration_supplement
     */
    public void setRegistration_supplement(final String registration_supplement) {
        this.registration_supplement = registration_supplement;
    }

    /**
     *
     * @return
     */
    public String getRegistered_from() {
        return registered_from;
    }

    /**
     *
     * @param registered_from
     */
    public void setRegistered_from(final String registered_from) {
        this.registered_from = registered_from;
    }

    /**
     *
     * @return
     */
    public String getRegistered_by() {
        return registered_by;
    }

    /**
     *
     * @param registered_by
     */
    public void setRegistered_by(final String registered_by) {
        this.registered_by = registered_by;
    }

    /**
     *
     * @return
     */
    public RegistrationMetadata getRegistrationMetadataSummary() {
        return registered_meta.get("summary");
    }

    /**
     *
     * @return
     */
    public String getLinked_registrations() {
        return linked_registrations;
    }

    /**
     *
     * @param linked_registrations
     */
    public void setLinked_registrations(final String linked_registrations) {
        this.linked_registrations = linked_registrations;
    }

    /**
     *
     * @return
     */
    public String getRegistration_schema() {
        return registration_schema;
    }

    /**
     *
     * @param registration_schema
     */
    public void setRegistration_schema(final String registration_schema) {
        this.registration_schema = registration_schema;
    }
}
