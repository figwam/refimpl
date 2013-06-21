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


/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class CheckoutResponseItem {
    
    public enum ExceptionType {
	POLICY_EXCEPTION("PolicyException"), 
	SERVICE_EXCEPTION("ServiceException"),
	REMOTE_EXCEPTION("RemoteException");
	String name;
	private ExceptionType(String name) {
	    this.name = name;
	}
	public String getName() {
	    return name;
	}
	public void setName(String name) {
	    this.name = name;
	}
    }

    /**
     * Identification of the request. Can be used to correlate the response to
     * the request.
     * */
    private String requestId;

    /**
     * Identifier of the successful reservation. The reservationIdentifer has to
     * be used for the commit or rollback method. Is present in successful
     * reservation
     * */
    private String reservationIdentifier;

    /** The user identification. */
    private String userIdentification;

    /**
     * Is present in not successful reservation
     * */
    private String exception;

    /**
     * Is present in not successful reservation
     */
    private String reason;
    
    private String exceptionType;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getReservationIdentifier() {
        return reservationIdentifier;
    }

    public void setReservationIdentifier(String reservationIdentifier) {
        this.reservationIdentifier = reservationIdentifier;
    }

    public String getUserIdentification() {
        return userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getExceptionType() {
		return exceptionType;
	}

}
