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
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class Subscriptions implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private List<Subscription> subscriptions;

	/**
	 * Instantiates a new response list SubscriptionResponseList.
	 */
	public Subscriptions() {
		super();
	}

	/**
	 * Gets the subscriptions.
	 * 
	 * @return the subscriptions
	 */
	public List<Subscription> getSubscriptions() {
		if (subscriptions == null)
			subscriptions = new ArrayList<Subscription>();
		return subscriptions;
	}

	/**
	 * Sets the subscriptions.
	 * 
	 * @param subscriptions
	 *            the new subscriptions
	 */
	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}
	
}
