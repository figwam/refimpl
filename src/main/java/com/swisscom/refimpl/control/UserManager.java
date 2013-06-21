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
package com.swisscom.refimpl.control;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.swisscom.refimpl.model.CheckoutRequestItem;
import com.swisscom.refimpl.model.CheckoutResponseItem;
import com.swisscom.refimpl.model.Service;
import com.swisscom.refimpl.model.Subscription;
import com.swisscom.refimpl.util.Constants;
import com.swisscom.rest.security.SignatureException;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@Named
public class UserManager {
	
	// object mapper is thread safe
	// see: http://wiki.fasterxml.com/JacksonBestPracticeThreadSafety
	protected final static ObjectMapper MAPPER = new ObjectMapper(); // can reuse, share globally
	
	static {
		MAPPER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private static final String PURCHASE_STATUS_SUCCESS = "success";

	private static final String PURCHASE_STATUS_ERROR = "error";

	private static final String PURCHASE_STATUS_CANCEL = "cancel";
	
	private static final String OUTCOME_BACK_JSF = "home";
	

	@Inject
	Logger log;

	@Inject
	ServiceControl serviceControl;

	@Inject
	@LoggedIn
	User currentUser;

	public Service loadSelectedService() {	
		Map<String, String> params = FacesContext.getCurrentInstance()
			.getExternalContext().getRequestParameterMap();

		String id = params.get("serviceId");
		if (currentUser.getService() != null && id != null
				&& currentUser.getService().getServiceId().equals(id)) {
			log.debug("selected service has not changed");
			return currentUser.getService();
		} else {
			currentUser.setService(serviceControl.retrieveService(id));
			return currentUser.getService();
		}
	}

	public Subscription loadSelectedSubscription() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();

		String id = params.get("subscriptionId");
		if (currentUser.getSubscription() != null && id != null
				&& currentUser.getSubscription().getSubscriptionId().equals(id)) {
			return currentUser.getSubscription();
		} else {
			currentUser.setSubscription(serviceControl.retrieveSubscription(id));
			return currentUser.getSubscription();
		}
	}


	public String getPurchaseStatusColor() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();

		String status = params.get("purchase");
		if (status == null) {
			status = "";
		}
		if (status.equals(PURCHASE_STATUS_ERROR)){
			return "red";
		} else if (status.equals(PURCHASE_STATUS_SUCCESS)){
			return "green";
		} else if (status.equals(PURCHASE_STATUS_CANCEL)){
			return "gray";
		} else {
			return "black"; //returns blank
		}
	}


	public String finalizePurchase() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();

		String status = params.get("purchase");
		String error = params.get("error");
		if (status == null) {
			status = "";
		}
		if (status.equals(PURCHASE_STATUS_ERROR)){
			String signature = params.get("signature");
			String checkoutResponseItem = params.get("checkoutResponseItem");
			CheckoutResponseItem cr = null;
			if (signature != null && checkoutResponseItem != null) {
				try {
					cr = buildCheckoutResponseItem(checkoutResponseItem, signature);
				} catch (Exception e) {
					log.error("Could not extract response-item: "+e.getMessage());
				} 
			}
			if (cr == null || cr.getReason() == null) {
				return "Service purchase failed cause: "+error;
			} else {
				return cr.getReason();
			}
		} else if (status.equals(PURCHASE_STATUS_SUCCESS)){
			return "Service purchase was successfull.";
		} else if (status.equals(PURCHASE_STATUS_CANCEL)){
			return "Service purchase was canceled by customer.";
		} else {
			return "";
		}
	}


	public User getCurrentUser() {
		return currentUser;
	}

	public String doCheckoutOnePhase() {
		return doCheckout(false);
	}

	public String doCheckoutTwoPhase() {
		return doCheckout(true);
	}

	public String doCheckout(boolean isTwoPhase) {
		try {
			CheckoutRequestItem ri = serviceToCheckoutItem(currentUser.getService());
			ri.setServiceUrl(ri.getServiceUrl()+"&isTwoPhase="+isTwoPhase);
			String coItem = ri.toJson();
			byte[] coItemb64 = Base64.encodeBase64(coItem.getBytes("UTF-8"));
			byte[] signb64 = Base64.encodeBase64(sign(coItem.getBytes("UTF-8"),
					Constants.SEC_KEY.getBytes("UTF-8")));
			
			
			
			String coUrl = Constants.MIB2_CO_URL+"?signature="
					+ urlEncode(new String(signb64),false)
					+ "&checkoutRequestItem="
					+ urlEncode(new String(coItemb64),false);
			log.info(coUrl);
			
			// do avoid auto commit on easypay side
			if (isTwoPhase) {
				coUrl+="&capture=false";
			}
			return coUrl;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String doCancel(String sId) {
		try {
			serviceControl.cancelSubscription(sId);
			currentUser.setSubscription(null);
			return "mySubscriptions";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String doDelete(String sId) {
		try {
			serviceControl.deleteSubscription(sId);
			refreshServices();
			return "mySubscriptions";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String doCommit() {
		try {
			Map<String, String> params = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap();

			String purchaseStatus = params.get("purchase");
			boolean isTwoPhase = Boolean.valueOf(params.get("isTwoPhase") == null?"false":params.get("isTwoPhase"));
			if (purchaseStatus == null) {
				purchaseStatus ="";
			}
			String reqId = params.get("requestId");
			String reservationId = params.get("reservationId");
			String signature = params.get("signature");
			String checkoutResponseItem = params.get("checkoutResponseItem");
			
			if (purchaseStatus.equals(PURCHASE_STATUS_SUCCESS)) {
				
				if (checkoutResponseItem != null) {
					String coItem = new String(Base64.decodeBase64(checkoutResponseItem.getBytes("UTF-8")));
					log.info("Checkout response: "+coItem);
					byte[] signb64 = Base64.encodeBase64(sign(coItem.getBytes("UTF-8"),
							Constants.SEC_KEY.getBytes("UTF-8")));
					log.info("Checkout signature expected: "+new String(signb64));
					log.info("Checkout signature received: "+signature);
				}
				log.info("Request id received: "+reqId);
				log.info("Resevation id received: "+reservationId);
				
				if (isTwoPhase) {
					log.info("Do manual commit");
					serviceControl.commitSubscription(reservationId);
				}

				refreshServices();
				
			}
			
			return purchaseStatus;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String img(int idx, int hash) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"/img/tn" + Math.abs(hash % 11) + ".jpg";
	}

	/**
	 * 
	 */
	private void refreshServices() {
		List<Service> services = serviceControl.retrieveServices();
		List<Subscription> subscriptions = serviceControl.retrieveSubscriptionsForMsisdn(currentUser.getMsisdn());
		currentUser.setServices(serviceControl.filterPurchasedServices(services,subscriptions));
		currentUser.setSubscriptions(serviceControl.retrieveSubscriptionsForMsisdn(currentUser.getMsisdn()));
	}

	private CheckoutRequestItem serviceToCheckoutItem(Service service) {
		CheckoutRequestItem ri = new CheckoutRequestItem();
		if (service.getServiceId().toLowerCase().contains("adult")) {
			ri.setAdultContent(true);
		} else {
			ri.setAdultContent(false);
		}
		if (service.getServiceId().toLowerCase().contains("roaming")) {
			ri.setRoaming(true);
		} else {
			ri.setRoaming(false);
		}
		ri.setAmount(new BigDecimal(service.getAmount()));
		ri.setBillingText(service.getServiceId());
		ri.setContentPartnerId("testPartner7");
		ri.setContentPartnerServiceId(service.getServiceId());
		ri.setCurrency("CHF");
		if (service.getDuration() != null) {
			ri.setDuration(service.getDuration().toString());
		}
		ri.setDurationUnit(service.getDurationUnit());
		ri.setRequestId(UUID.randomUUID().toString());
		ri.setCancelUrl(Constants.REFIMPL_BACK_URL+"/"+OUTCOME_BACK_JSF+".jsf?purchase="+PURCHASE_STATUS_CANCEL);
		ri.setServiceErrorUrl(Constants.REFIMPL_BACK_URL+"/"+OUTCOME_BACK_JSF+".jsf?purchase="+PURCHASE_STATUS_ERROR);
		ri.setServiceUrl(Constants.REFIMPL_BACK_URL+"/"+OUTCOME_BACK_JSF+".jsf?purchase="+PURCHASE_STATUS_SUCCESS);
		if (service.getHasTrial()) {
			ri.setTransactionType(CheckoutRequestItem.TransactionType.Trial);
			ri.setTrialAmount(new BigDecimal(service.getTrialAmount()));
		} else if (service.getIsRecurrent()) {
			ri.setTransactionType(CheckoutRequestItem.TransactionType.Recurrent);
		} else if (service.getDuration() != null
				&& service.getDurationUnit() != null
				&& !service.getIsRecurrent()) {
			ri.setTransactionType(CheckoutRequestItem.TransactionType.NonRecurrent);
		} else {
			ri.setTransactionType(CheckoutRequestItem.TransactionType.Event);
		}
		return ri;
	}


	private CheckoutResponseItem buildCheckoutResponseItem(String checkoutResponseItem, String signature) 
			throws JsonParseException, JsonMappingException, IOException {
		CheckoutResponseItem cr = null;
		byte[] signb64;
		try {
			// check signature
			signb64 = Base64.encodeBase64(sign(Base64.decodeBase64(checkoutResponseItem.getBytes("UTF-8")),
					Constants.SEC_KEY.getBytes("UTF-8")));
			String signExpected = new String(signb64,"UTF-8");
			if (!signExpected.equals(signature)) {
				log.error("!!! Signature validation failed !!!, Received: "+signature+" Expected: "+signExpected);
			}
			
			// extract the response
			String decodedResponse = new String (Base64.decodeBase64(checkoutResponseItem.getBytes("UTF-8")), "UTF-8");
			cr = MAPPER.readValue(decodedResponse, CheckoutResponseItem.class);
		} catch (SignatureException e) {
			log.error("!!! Signature validation failed !!!", e.getMessage());
		}
		return cr;
	}

	private byte[] sign(byte[] data, byte[] key) throws SignatureException {
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(key, "HmacSHA1"));
			return mac.doFinal(data);
		} catch (Exception e) {
			throw new SignatureException(
					"Unable to calculate a request signature: "
							+ e.getMessage(), e);
		}
	}
	
	private String urlEncode(String value, boolean path) {
		if (value == null)
			return "";

		try {
			String encoded = URLEncoder.encode(value, "UTF-8")
					.replace("+", "%20")
					.replace("*", "%2A")
					.replace("%7E", "~");
			if (path) {
				encoded = encoded.replace("%2F", "/");
			}

			return encoded;
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}
}