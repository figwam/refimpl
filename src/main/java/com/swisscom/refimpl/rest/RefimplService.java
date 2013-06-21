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
package com.swisscom.refimpl.rest;

import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.swisscom.refimpl.control.Login;
import com.swisscom.refimpl.control.UserManager;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the members table.
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@Path("/refimpl")
public class RefimplService {
	
    @Inject
    private Login login;
    
    @Inject
    Logger log;
    
    @Inject
    UserManager um;
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doOperation(InputStream data) {
		// do check signature
		ObjectMapper mapper = new ObjectMapper(); 
		Operation op = null;
		byte[] dataB = null;
		try {
			dataB = IOUtils.toByteArray(data);
	        log.info("---------------->"+new String(dataB));
			op = mapper.readValue(dataB, Operation.class);
			if (op.getOperation().equals("deleteSubscription")) {
				um.doDelete(op.getId());
			} else if (op.getOperation().equals("cancelSubscription")) {
				um.doCancel(op.getId());
			} else if (op.getOperation().equals("logout")) {
				login.logout();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        return Response.ok().build();
    }
    
    public static void main(String[] args) throws Exception{
		ObjectMapper mapper = new ObjectMapper(); 
		Operation 
			op = mapper.readValue("{\"operation\":\"deleteSubscription\",\"id\":\"a\"}".getBytes(), Operation.class);
		System.out.println(op.getOperation());
	}
}
