/*
 * Copyright 2010-2012 swisscom.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 *
 * This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.swisscom.refimpl.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class Subscription extends Payment implements Serializable {
	
	private DateTimeFormatter fmt = ISODateTimeFormat.dateTime();	
	
	public static final SimpleDateFormat RFC_822_DATE_FORMAT_TZ_GMT = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss", Locale.US);
	
	public enum DurationUnit {MINUTE, DAY, WEEK, MONTH, YEAR, EVENT}
	
	public enum Operation {COMMIT, REJECT, CANCEL}
	
	public enum Status {
		RESERVED, ACTIVE, INACTIVE, SUSPENDED, CANCELLED, REJECTED;
	}

	private static final long serialVersionUID = 1L;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String serviceId;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Integer duration;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String durationUnit;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String trialAmount;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String createdOn;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String nextPayment;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String validTill;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String language;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Boolean hasTrial;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Boolean isRecurrent;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String operation;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String status;
		

	/**
	 * @return the validTill
	 */
	public String getValidTill() {
		if (validTill != null) {
			DateTime dt = fmt.parseDateTime(validTill);
			return RFC_822_DATE_FORMAT_TZ_GMT.format(dt.toDate());
		} else {
			return validTill;
		}
	}

	/**
	 * @param validTill the validTill to set
	 */
	public void setValidTill(String validTill) {
		this.validTill = validTill;
	}

	/**
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * @return the duration
	 */
	public Integer getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	/**
	 * @return the durationUnit
	 */
	public String getDurationUnit() {
		return durationUnit;
	}

	/**
	 * @param durationUnit the durationUnit to set
	 */
	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	/**
	 * @return the trialAmount
	 */
	public String getTrialAmount() {
		return trialAmount;
	}

	/**
	 * @param trialAmount the trialAmount to set
	 */
	public void setTrialAmount(String trialAmount) {
		this.trialAmount = trialAmount;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getNextPayment() {
		if (nextPayment != null) {
			DateTime dt = fmt.parseDateTime(nextPayment);
			return RFC_822_DATE_FORMAT_TZ_GMT.format(dt.toDate());
		} else {
			return nextPayment;
		}
	}

	public void setNextPayment(String nextPayment) {
		this.nextPayment = nextPayment;
	}

	public Boolean getIsRecurrent() {
		return isRecurrent;
	}

	public void setIsRecurrent(Boolean isRecurrent) {
		this.isRecurrent = isRecurrent;
	}

	public Boolean getHasTrial() {
		return hasTrial;
	}

	public void setHasTrial(Boolean hasTrial) {
		this.hasTrial = hasTrial;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	
	public static Set<Status> getOnGoingStates(){
		Set<Subscription.Status> status = new HashSet<Subscription.Status>();
		status.add(Subscription.Status.ACTIVE);
		status.add(Subscription.Status.CANCELLED);
		status.add(Subscription.Status.SUSPENDED);
		return status;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public Status getStatus() {
		return status == null ? null : Status.valueOf(status.toUpperCase());
	}
	
	public void setStatus(Status status) {
		this.status = (status == null ? null : status.toString());
	}
	
	public String getSubscriptionId(){
		Matcher matcher = Pattern.compile("/subscriptions/[\\w-]*").matcher(uri);
		matcher.find();
		return matcher.group().replaceAll("/subscriptions/", "");
	}
	
	

	
	public String getHumanReadableDuration() {
		switch (DurationUnit.valueOf(durationUnit.toUpperCase())) {
		case MINUTE:
			return duration > 1 ? duration + " Minutes" : "1 Minute";
		case DAY:
			return duration > 1 ? duration + " Days" : "1 Day";
		case WEEK:
			return duration > 1 ? duration + " Weeks" : "1 Week";
		case MONTH:
			return duration > 1 ? duration + " Months" : "1 Month";
		case YEAR:
			return duration > 1 ? duration + " Years" : "1 Year";
		case EVENT:
			return "Event";
		default:
			return "undef";
		}
	}

}