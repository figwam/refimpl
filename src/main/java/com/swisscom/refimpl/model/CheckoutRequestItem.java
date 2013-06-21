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

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class CheckoutRequestItem {

	private static Log log = LogFactory.getLog(CheckoutRequestItem.class);

	public enum TransactionType {
		Recurrent, NonRecurrent, Event, Trial
	}

	private TransactionType transactionType;
	private String contentPartnerServiceId;
	private String billingText;
	private boolean adultContent;
	private boolean roaming;
	private String contentPartnerId;
	private String duration;
	private String durationUnit;
	private String requestId;
	private String serviceUrl;
	private String cancelUrl;
	private String serviceErrorUrl;
	private BigDecimal amount;
	private BigDecimal trialAmount;
	private String currency = "CHF";
	private String secretKey = System.getProperty(
			"mib.checkout.request.item.secret.key", "");

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public String getContentPartnerServiceId() {
		return contentPartnerServiceId;
	}

	public void setContentPartnerServiceId(String contentPartnerServiceId) {
		this.contentPartnerServiceId = contentPartnerServiceId;
	}

	public String getBillingText() {
		return billingText;
	}

	public void setBillingText(String billingText) {
		this.billingText = billingText;
	}

	public boolean isAdultContent() {
		return adultContent;
	}

	public void setAdultContent(boolean adultContent) {
		this.adultContent = adultContent;
	}

	public boolean isRoaming() {
		return roaming;
	}

	public void setRoaming(boolean roaming) {
		this.roaming = roaming;
	}

	public String getContentPartnerId() {
		return contentPartnerId;
	}

	public void setContentPartnerId(String contentPartnerId) {
		this.contentPartnerId = contentPartnerId;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

	public String getServiceErrorUrl() {
		return serviceErrorUrl;
	}

	public void setServiceErrorUrl(String serviceErrorUrl) {
		this.serviceErrorUrl = serviceErrorUrl;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getTrialAmount() {
		return trialAmount;
	}

	public void setTrialAmount(BigDecimal trialAmount) {
		this.trialAmount = trialAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String toJson() {
		String json = "{" + "	   \"transactionType\":\"" + this.transactionType
				+ "\"," + "	   \"contentPartnerServiceId\": \""
				+ this.contentPartnerServiceId + "\","
				+ "	   \"billingText\": \"" + this.billingText + "\", "
				+ "	   \"adultContent\": \"" + this.adultContent + "\","
				+ "	   \"roaming\": \"" + this.roaming + "\","
				+ "	   \"contentPartnerId\": \"" + this.contentPartnerId
				+ "\",";
		if (!this.transactionType.equals(TransactionType.Event)) {
			json += "	   \"duration\":\"" + this.duration + "\","
					+ "	   \"durationUnit\":\"" + this.durationUnit + "\",";
		}
		json += "	   \"requestId\":\"" + this.requestId + "\","
				+ "	   \"serviceUrl\": \"" + this.serviceUrl + "\","
				+ "	   \"cancelUrl\": \"" + this.cancelUrl + "\","
				+ "	   \"serviceErrorUrl\": \"" + this.serviceErrorUrl + "\","
				+ "	   \"amount\": \"" + this.amount + "\","
				+ "	   \"trialAmount\": \"" + this.trialAmount + "\","
				+ "	   \"currency\": \"" + this.currency + "\"" + "	}";
		
		// remove null values
		json = json.replaceAll("\\s*", "").replaceAll("\"\\w*\":\"null\",?", "");
		
		if (log.isDebugEnabled()) {
			log.debug("producing json: " + json);
		}
		return json;
	}
	
	public static void main(String[] args) {
		CheckoutRequestItem ri = new CheckoutRequestItem();
		ri.setAdultContent(false);
		ri.setAmount(new BigDecimal("2.75"));
		ri.setBillingText("super game");
		ri.setCancelUrl("http://www.mysupergamesshop.ch?action=cancel");
		ri.setServiceUrl("http://www.mysupergamesshop.ch?action=success");
		ri.setServiceErrorUrl("http://www.mysupergamesshop.ch?action=error");
		ri.setContentPartnerId("gamesshop");
		ri.setContentPartnerServiceId("supergame001");
		ri.setDuration("WEEK");
		ri.setDurationUnit("1");
		ri.setRequestId("64gh6-gh3-45bv-hh4");
		ri.setRoaming(false);
		ri.setTransactionType(TransactionType.Recurrent);
		ri.setTrialAmount(new BigDecimal("0.5"));
		System.out.println(ri.toJson());
	}


}
