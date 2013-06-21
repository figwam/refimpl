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
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class Service implements Serializable {
	
	public static Comparator<Service> COMPARATOR_BY_SERVICE_ID = new Comparator<Service>() {
		@Override
		public int compare(Service o1, Service o2) {
			return o1.getServiceId().compareTo(o2.getServiceId());
		}
	};
	
	public enum DurationUnit {MINUTE, DAY, WEEK, MONTH, YEAR, EVENT}
	
	private static final long serialVersionUID = 1L;	

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String uri;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String amount;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Boolean hasTrial;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Boolean isRecurrent;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private Integer duration;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String durationUnit;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String trialAmount;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String createdOn;	

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String serviceName;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String serviceDesc;	
		

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
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
	
	public String getServiceId(){
		Matcher matcher = Pattern.compile("/services/[\\w-]*").matcher(uri);
		matcher.find();
		return matcher.group().replaceAll("/services/", "");
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