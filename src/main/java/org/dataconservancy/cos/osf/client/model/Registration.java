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

import static org.dataconservancy.cos.osf.client.support.JodaSupport.DATE_TIME_FORMATTER;

import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.RelType;
import com.github.jasminb.jsonapi.ResolutionStrategy;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

/**
 * OSF Registration model, an extension of the Node model
 * @author khanson
 */
@Type("registrations")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Registration extends NodeBase  {

	/**List of nodes that are children of this node.*/
	@Relationship(value = "children", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.OBJECT)
	protected List<Registration> children;

	/**Is this registered node visible on the user dashboard?**/
	private Boolean isDashboard;
	
	/**Has this registration been withdrawn?**/
	private Boolean isWithdrawn;

	/**	Timestamp that the registration was created */
	private DateTime date_registered;
	
	/** When the embargo on this registration will be lifted (if applicable) */
	private DateTime embargo_end_date;
	
	/**Reasons for withdrawing the registration*/
	private String withdrawal_justification;

	/** Is this registration pending withdrawal?*/
	private Boolean isPending_withdrawal;
	
	/**Is this registration pending approval?*/
	private Boolean isPending_registration_approval; 
	
	/** Is the associated Embargo awaiting approval by project admins? */
	private Boolean isPending_embargo_approval;           
	
	/**registration supplementary information*/
	private RegistrationMetadata registered_meta;
	
	/**registration template used*/
	private String registration_supplement;
	
	/**Node registered from who are contributors to this node. */
	@Relationship(value = "registered_from", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
	private String registered_from;
	
	public List<Registration> getChildren() {
		return children;
	}	
	
	public void setChildren(List<Registration> children) {
		this.children = children;
	}
	
	public Boolean isDashboard() {
		return isDashboard;
	}

	public void setDashboard(Boolean isDashboard) {
		this.isDashboard = isDashboard;
	}

	public Boolean isWithdrawn() {
		return isWithdrawn;
	}

	public void setWithdrawn(Boolean isWithdrawn) {
		this.isWithdrawn = isWithdrawn;
	}
	
	public String getDate_registered() {
    	if (this.date_registered!=null) {
    		return this.date_registered.toString(DATE_TIME_FORMATTER);
    	} else {
    		return null;
    	}
	}

	public void setDate_registered(String date_registered) {
    	if (date_registered!=null){
    		this.date_registered = DATE_TIME_FORMATTER.parseDateTime(date_registered);
    	} else {
    		this.date_registered = null;
    	}
	}

	public String getEmbargo_end_date() {
    	if (this.embargo_end_date!=null) {
    		return this.embargo_end_date.toString(DATE_TIME_FORMATTER);
    	} else {
    		return null;
    	}
	}

	public void setEmbargo_end_date(String embargo_end_date) {
    	if (embargo_end_date!=null){
    		this.embargo_end_date = DATE_TIME_FORMATTER.parseDateTime(embargo_end_date);
    	} else {
    		this.embargo_end_date = null;
    	}
	}

	public String getWithdrawal_justification() {
		return withdrawal_justification;
	}

	public void setWithdrawal_justification(String withdrawal_justification) {
		this.withdrawal_justification = withdrawal_justification;
	}

	public Boolean isPending_withdrawal() {
		return isPending_withdrawal;
	}

	public void setPending_withdrawal(Boolean isPending_withdrawal) {
		this.isPending_withdrawal = isPending_withdrawal;
	}

	public Boolean isPending_registration_approval() {
		return isPending_registration_approval;
	}

	public void setPending_registration_approval(
			Boolean isPending_registration_approval) {
		this.isPending_registration_approval = isPending_registration_approval;
	}

	public Boolean isPending_embargo_approval() {
		return isPending_embargo_approval;
	}

	public void setPending_embargo_approval(Boolean isPending_embargo_approval) {
		this.isPending_embargo_approval = isPending_embargo_approval;
	}

	public RegistrationMetadata getRegistered_meta() {
		return registered_meta;
	}

	public void setRegistered_meta(RegistrationMetadata registered_meta) {
		this.registered_meta = registered_meta;
	}

	public String getRegistration_supplement() {
		return registration_supplement;
	}

	public void setRegistration_supplement(String registration_supplement) {
		this.registration_supplement = registration_supplement;
	}

	public String getRegistered_from() {
		return registered_from;
	}

	public void setRegistered_from(String registered_from) {
		this.registered_from = registered_from;
	}

	
}
