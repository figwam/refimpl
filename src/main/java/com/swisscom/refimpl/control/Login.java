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

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.swisscom.refimpl.model.Service;
import com.swisscom.refimpl.model.Subscription;
import com.swisscom.refimpl.util.MsisdnUtil;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@SessionScoped
@Named
public class Login implements Serializable {

	@Inject
	transient Logger log;

    @Inject
    transient ServiceControl serviceControl;
	
	String msisdn;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private User currentUser;
	
//	@PostConstruct
//	public void init() {
//		msisdn = "079xxxxxxx";
//		login();
//	}
	
	
	public String login() {
		log.info("Login received with msisdn: "+msisdn);
		
		currentUser = new User();
		currentUser.setMsisdn(MsisdnUtil.parseMsisdn(msisdn));
		
		// MIB 2 Subscriptions
		List<Service> services = serviceControl.retrieveServices();
		List<Subscription> subscriptions = serviceControl.retrieveSubscriptionsForMsisdn(currentUser.getMsisdn());
		currentUser.setServices(serviceControl.filterPurchasedServices(services,subscriptions));
		currentUser.setSubscriptions(subscriptions);
		
		log.info("Login with msisdn: "+currentUser.getMsisdn()+" was successful.");
		
		/*
		 * http://www.mkyong.com/jsf2/implicit-navigation-in-jsf-2-0/
		 * 
		 * By default, JSF 2 is perform a forward while navigating to another page, it caused the page URL is always one 
		 * behind :). For example, when you move from “page1.xhtml” to “page2.xhtml”, the browser URL address bar will 
		 * still showing the same “page1.xhtml” URL.
		 * 
		 * To avoid this, you can tell JSF to use the redirection by append the “faces-redirect=true” to the end 
		 * of the “outcome” string.
		 * 
		 * */
		return "home?faces-redirect=true";
	}

	public void logout() {
		String user = currentUser.getMsisdn();
		currentUser = null;
		log.info("Logout with msisdn: "+user+" was successful.");
	}

	public boolean isLoggedIn() {
		return currentUser != null;
	}

	@Produces
	@LoggedIn
	public User getCurrentUser() {
		return currentUser;
	}
	
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
}