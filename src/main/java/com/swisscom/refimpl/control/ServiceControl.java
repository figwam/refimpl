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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.swisscom.refimpl.boundary.MIB2Client;
import com.swisscom.refimpl.model.Service;
import com.swisscom.refimpl.model.Services;
import com.swisscom.refimpl.model.Subscription;
import com.swisscom.refimpl.model.Subscriptions;
import com.swisscom.refimpl.util.Constants;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class ServiceControl {

	
	// object mapper is thread safe
	// see: http://wiki.fasterxml.com/JacksonBestPracticeThreadSafety
	protected final static ObjectMapper MAPPER = new ObjectMapper(); // can reuse, share globally
	
	static {
		MAPPER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	private static final int HTTP_NO_CONTENT = 204;

	@Inject
	Logger log;

	@Inject
	MIB2Client requestor;

	public List<Service> retrieveServices() {
		try {
			HttpResponse response = requestor.retrieveServices(Constants.MERCHANT_ID, true);
			Services srvs = MAPPER.readValue(EntityUtils.toByteArray(response.getEntity()),
					Services.class);
			Collections.sort(srvs.getServices(),
					Service.COMPARATOR_BY_SERVICE_ID);
			return srvs.getServices();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void commitSubscription(String sId) {
		try {
			List<HttpPut> out = new ArrayList<HttpPut>();
			requestor.commitSubscription(Constants.MERCHANT_ID, sId, out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Subscription> retrieveSubscriptionsForMsisdn(String msisdn) {
		try {
			HttpResponse response = requestor.retrieveSubscriptions(msisdn, Constants.MERCHANT_ID, null);
			int rCode = response.getStatusLine().getStatusCode();
			log.info("Got response code ["+rCode+"] ");
			if (rCode == HTTP_NO_CONTENT) {
				return new ArrayList<Subscription>();
			} else {
				String r = new String(EntityUtils.toByteArray(response.getEntity()));
				log.info("Got response: "+r);
				Subscriptions srvs = MAPPER.readValue(r,Subscriptions.class);
				return srvs.getSubscriptions();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public Service retrieveService(String id) {
		try {
			log.info("Get info for service: " + id);
			HttpResponse response = requestor.retrieveServiceByUri(Constants.MERCHANT_ID, MIB2Client.SERVICES_URL
					+ "/" + id);
			ObjectMapper mapper = new ObjectMapper();
			return mapper
					.readValue(EntityUtils.toByteArray(response.getEntity()), Service.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Service> filterPurchasedServices(
			List<Service> services,
			List<Subscription> subs) {
		List<String> l = new ArrayList<String>();
		List<Service> ret = new ArrayList<Service>();
		for (Subscription s : subs) {
			l.add(s.getServiceId());
		}
		for (Service s : services) {
			if (!l.contains(s.getServiceId())){
				ret.add(s);
			}
		}
		return ret;
	}

	public void cancelSubscription(String sId) {
		try {
			List<HttpPut> out = new ArrayList<HttpPut>();
			requestor.cancelSubscription(Constants.MERCHANT_ID, sId, out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Subscription retrieveSubscription(String id) {
		try {
			log.info("Get info for subscription: " + id);
			List<HttpGet> out = new ArrayList<HttpGet>();
			HttpResponse response = requestor.retrieveSubscriptionByUri(Constants.MERCHANT_ID, MIB2Client.SUBSCRIPTIONS_URL
					+ "/" + id, out);
			String r = new String(EntityUtils.toByteArray(response.getEntity()));
			log.info("Got response: "+r);
			return MAPPER.readValue(r, Subscription.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteSubscription(String sId) {
		try {
			List<HttpDelete> out = new ArrayList<HttpDelete>();
			requestor.deleteSubscription(Constants.MERCHANT_ID, sId, out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
