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

import javax.inject.Named;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@Named
public class CheckConfig {
	
	public boolean isWellConfigured(){
		return System.getProperty("sec.key") != null 
				&& System.getProperty("merchant.id") != null
				&& System.getProperty("refimpl.back.url") != null
				&& System.getProperty("ce.base.url") != null
				&& System.getProperty("mib2.co.url") != null;
	}

}
