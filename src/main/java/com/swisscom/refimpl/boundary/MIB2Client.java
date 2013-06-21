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
package com.swisscom.refimpl.boundary;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.swisscom.refimpl.util.Constants;
import com.swisscom.rest.security.RequestSignInformations;
import com.swisscom.rest.security.Signer;

/**
 * 
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 *
 *
 */
@Stateless
public class MIB2Client {

	public static final String ACCOUNTS_URL = Constants.CE_BASE_URL + "/accounts";

	public static final String SUBSCRIPTIONS_URL = Constants.CE_BASE_URL + "/subscriptions";

	public static final String SERVICES_URL = Constants.CE_BASE_URL + "/services";

	public static final String PAYMENTS_URL = Constants.CE_BASE_URL + "/payments";

	public static final SimpleDateFormat RFC_822_DATE_FORMAT_TZ_GMT = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
	
	// Create an HttpClient with the ThreadSafeClientConnManager.
    // This connection manager must be used if more than one thread will
    // be using the HttpClient.
    //PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
    //cm.setMaxTotal(10);
	//HttpClient httpclient = new DefaultHttpClient(cm);

    HttpClient httpClient = new DefaultHttpClient();
	
	@PostConstruct
    public void init() {
		try {
			TrustSelfSignedStrategy trustStat = new TrustSelfSignedStrategy();
			X509HostnameVerifier allHostsVerifier = new AllowAllHostnameVerifier();
	//		ProtocolSocketFactory socketFactory =
	//			    new EasySSLProtocolSocketFactory( );
	//		Protocol https = new Protocol( "https", socketFactory, 443);
	//		Protocol.registerProtocol( "https", https );
		
			SSLSocketFactory socketFactory;
				socketFactory = new SSLSocketFactory(trustStat, allHostsVerifier);
			
			Scheme sch = new Scheme("https", 443, socketFactory);
			httpClient.getConnectionManager().getSchemeRegistry().register(sch);
		
		} catch (Exception e) {
			new RuntimeException(e.getMessage());
		} 
	}


	private void addSignature(HttpRequestBase request, String methodByName,
			String path, String merchantId, String contentType, byte[] data) {
		RequestSignInformations reqSign = new RequestSignInformations();
		reqSign.setDate(RFC_822_DATE_FORMAT_TZ_GMT.format(new Date()));
		reqSign.setMethod(methodByName);
		reqSign.setPath(path);
		reqSign.setData(data);
		reqSign.setContentType(contentType);

		if (merchantId != null) {
			request.addHeader("x-merchant-id", merchantId);
		}

		String sig;
		try {
			sig = new Signer().buildSignature(reqSign, Constants.SEC_KEY);
			request.addHeader("x-scs-signature", sig);
			request.addHeader("x-scs-date", reqSign.getDate());
			if (data != null) {
				request.addHeader("content-md5",
						new String(Base64.encodeBase64(DigestUtils.md5(data))));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public HttpResponse retrieveSubscriptions(String msisdn, String merchantId,
			String status) throws HttpException,
			IOException, URISyntaxException {
		HttpGet request = new HttpGet(SUBSCRIPTIONS_URL);
		// set headers
		request.addHeader("Accept",
				"application/vnd.ch.swisscom.easypay.subscription.list+json");
		request.addHeader("X-Request-Id", "[req-id]-msisdn-" + msisdn);
		URI uri = new URIBuilder(request.getURI())
			.build();
		if (msisdn != null) {
			uri = new URIBuilder(uri)
				.addParameter("msisdn.is",msisdn)
				.build();
		}
		if (status != null) {
			uri = new URIBuilder(uri)
				.addParameter("status.is",msisdn)
				.build();
		}

		((HttpRequestBase) request).setURI(uri);
		

		addSignature(request, "GET", "/subscriptions", merchantId, null, null);
		return httpClient.execute(request);
	}

	public HttpResponse retrieveServices(String merchantId,
			Boolean doRetrieveOnlyActive)
			throws HttpException, IOException, URISyntaxException {
		HttpGet request = new HttpGet(SERVICES_URL);
		// set headers
		request.addHeader("Accept",
				"application/vnd.ch.swisscom.easypay.service.list+json");
		request.addHeader("X-Request-Id", "[req-id]-msisdn-"
				+ merchantId);

		URI uri = new URIBuilder(request.getURI())
		.build();
		if (!doRetrieveOnlyActive) {
			uri = new URIBuilder(uri)
				.addParameter("doRetrieveOnlyActive", "false")
				.build();
		}

		addSignature(request, "GET", "/services", merchantId, null, null);
		return httpClient.execute(request);
	}

	public HttpResponse retrieveServiceByUri(String merchantId, String uri) throws HttpException, IOException {
		HttpGet request = new HttpGet(uri);
		// set headers
		request.addHeader("Accept",
				"application/vnd.ch.swisscom.easypay.service+json");
		request.addHeader("X-Request-Id", "[req-id]-uri-" + uri);
		addSignature(request, "GET", "/services/" + extractServiceId(uri),
				merchantId, null, null);
		return httpClient.execute(request);
	}

	public HttpResponse commitSubscription(String merchantId,
			String subscriptionId, List<HttpPut> out) throws IOException,
			HttpException {
		return modifySubscription(merchantId, subscriptionId, out,"COMMIT");
	}

	public HttpResponse rejectSubscription(String merchantId,
			String subscriptionId, List<HttpPut> out) throws IOException,
			HttpException {
		return modifySubscription(merchantId, subscriptionId, out,"REJECT");
	}
	public HttpResponse cancelSubscription(String merchantId,String subscriptionId, List<HttpPut> outMethod) 
			throws HttpException, IOException {
		return modifySubscription(merchantId, subscriptionId, outMethod, "CANCEL");
	}
	public HttpResponse deleteSubscription(String merchantId,String subscriptionId, List<HttpDelete> outMethod) 
			throws HttpException, IOException {
		HttpDelete request = new HttpDelete(MIB2Client.SUBSCRIPTIONS_URL+"/"+subscriptionId);
		// set headers
		request.addHeader("Accept", "application/vnd.ch.swisscom.easypay.message.list+json");
		request.addHeader("X-Request-Id", "[req-id]-subscriptionId-"+subscriptionId);
		if (outMethod != null) {
			outMethod.add(request);
		}
		addSignature(request, "DELETE", "/subscriptions/"+subscriptionId, merchantId, null, null);
		return httpClient.execute(request);
	}

	public HttpResponse modifySubscription(String merchantId,
			String subscriptionId, List<HttpPut> out, String status)
			throws IOException, HttpException {
		HttpPut request = new HttpPut(MIB2Client.SUBSCRIPTIONS_URL + "/"
				+ subscriptionId);
		String entity = "{\"operation\": \"" + status + "\"}";
		request.setEntity(
				new StringEntity(entity, ContentType.create("application/vnd.ch.swisscom.easypay.subscription+json", "utf-8")));
		if (out != null) {
			out.add(request);
		}
		addSignature(request, "PUT", "/subscriptions/" + subscriptionId,
				merchantId, "application/vnd.ch.swisscom.easypay.subscription+json",
				entity.getBytes("UTF-8"));
		return httpClient.execute(request);
	}

	public HttpResponse retrieveSubscriptionByUri(String merchantId,String uri, List<HttpGet> outMethod) throws HttpException, IOException {
		HttpGet request = new HttpGet(uri);
		// set headers
		request.addHeader("Accept", "application/vnd.ch.swisscom.easypay.subscription+json");
		request.addHeader("X-Request-Id", "[req-id]-uri-"+uri);
		if (outMethod != null) {
			outMethod.add(request);
		}
		addSignature(request, "GET", "/subscriptions/"+extractSubscriptionId(uri),merchantId, null, null);
		return httpClient.execute(request);
	}

	public String extractServiceId(String uri) {
		Matcher matcher = Pattern.compile("/services/[\\w-]*").matcher(uri);
		matcher.find();
		return matcher.group().replaceAll("/services/", "");
	}
	
	public String extractSubscriptionId(String uri){
		Matcher matcher = Pattern.compile("/subscriptions/[\\w-]*").matcher(uri);
		matcher.find();
		return matcher.group().replaceAll("/subscriptions/", "");
	}
}
