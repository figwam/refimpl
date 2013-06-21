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
package com.swisscom.refimpl.util;


/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class Constants {
	
	
    /* **************************************************************** 
	 * This sample App is for demonstration purposes only.
	 * 
     * It is not secure to embed your credentials into source code!
     * 
     * Please keep your secure key always internal. Do never commit/push
     * your key on public repositories!
     ******************************************************************/
	public static final String SEC_KEY = System.getProperty("sec.key"); //Your SEC_KEY here	
	public static final String MERCHANT_ID = System.getProperty("merchant.id");//Your MERCHANT_ID here
	
	
	

    /* **************************************************************** 
	 * Set the back urls for your app here
     ******************************************************************/
	public static final String REFIMPL_BACK_URL = System.getProperty("refimpl.back.url","easypay-webshop-demo/mib2");
	
    /* **************************************************************** 
	 * Set the destination charging engine 
     ******************************************************************/
	public static final String CE_BASE_URL = System.getProperty("ce.base.url","https://easypay-test.swisscom.ch/ce-rest-service");
	public static final String MIB2_CO_URL = System.getProperty("mib2.co.url","https://easypay-test.swisscom.ch/checkout/CheckoutPage.seam");
	
	

}
