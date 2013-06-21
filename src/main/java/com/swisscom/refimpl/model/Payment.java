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

import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public abstract class Payment implements Serializable {

	protected static final long serialVersionUID = 1L;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	protected String uri;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	protected String paymentInfo;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	protected Boolean isAdultContent;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	protected Boolean isRoaming;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	protected String merchantId;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	protected String amount;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	protected String msisdn;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	protected String createdOn;

	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the paymentInfo
	 */
	public String getPaymentInfo() {
		return paymentInfo;
	}

	/**
	 * @param paymentInfo the paymentInfo to set
	 */
	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	/**
	 * @return the isAdultContent
	 */
	public Boolean getIsAdultContent() {
		return isAdultContent;
	}

	/**
	 * @param isAdultContent the isAdultContent to set
	 */
	public void setIsAdultContent(Boolean isAdultContent) {
		this.isAdultContent = isAdultContent;
	}

	/**
	 * @return the isRoaming
	 */
	public Boolean getIsRoaming() {
		return isRoaming;
	}

	/**
	 * @param isRoaming the isRoaming to set
	 */
	public void setIsRoaming(Boolean isRoaming) {
		this.isRoaming = isRoaming;
	}

	/**
	 * @return the merchantId
	 */
	public String getMerchantId() {
		return merchantId;
	}

	/**
	 * @param merchantId the merchantId to set
	 */
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	/**
	 * @return the msisdn
	 */
	public String getMsisdn() {
		return msisdn;
	}

	/**
	 * @param msisdn the msisdn to set
	 */
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	@Override
	public String toString() {
		return msisdn;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	
}