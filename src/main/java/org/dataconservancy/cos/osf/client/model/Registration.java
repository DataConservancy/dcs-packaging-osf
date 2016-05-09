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

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Type;

/**
 * OSF Registration model, an extension of the Node model
 * @author khanson
 */
@Type("registrations")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Registration extends Node  {

	/**Is this registered node visible on the user dashboard?**/
	private Boolean isDashboard;
	
	/**Has this registration been retracted?**/
	private Boolean isRetracted;

	/**	Timestamp that the registration was created */
	private DateTime date_registered;
	
	/** When the embargo on this registration will be lifted (if applicable) */
	private DateTime embargo_end_date;
	
	/**Reasons for retracting the registration*/
	private String retraction_justification;

	/** Is this registration pending retraction?*/
	private Boolean isPending_retraction;
	
	/**Is this registration pending approval?*/
	private Boolean isPending_registration_approval; 
	
	/** Is the associated Embargo awaiting approval by project admins? */
	private Boolean isPending_embargo_approval;           
	
	/**registration supplementary information*/
	private RegistrationMetadata registered_meta;
	
	/**registration template used*/
	private String registration_supplement;
		
	public Boolean isDashboard() {
		return isDashboard;
	}

	public void setDashboard(Boolean isDashboard) {
		this.isDashboard = isDashboard;
	}

	public Boolean isRetracted() {
		return isRetracted;
	}

	public void setRetracted(Boolean isRetracted) {
		this.isRetracted = isRetracted;
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

	public String getRetraction_justification() {
		return retraction_justification;
	}

	public void setRetraction_justification(String retraction_justification) {
		this.retraction_justification = retraction_justification;
	}

	public Boolean isPending_retraction() {
		return isPending_retraction;
	}

	public void setPending_retraction(Boolean isPending_retraction) {
		this.isPending_retraction = isPending_retraction;
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

	
}
