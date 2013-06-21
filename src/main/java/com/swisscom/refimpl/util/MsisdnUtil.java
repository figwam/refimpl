/*
 (c) Copyright Swisscom (Schweiz) AG. All rights reserved.

 This product is the proprietary and sole property of Swisscom (Schweiz) AG
 Use, duplication or dissemination is subject to prior written consent of
 Swisscom (Schweiz) AG.

 $Id: $

 */
package com.swisscom.refimpl.util;


/**
 * @author $Author: tgdscald$
 * @version $Revision: $ / $Date: 18.05.2009$
 */
public class MsisdnUtil {

	private static final String MSISDN_PATTERN_SCM_CH = "(41|\\+41|0041|0)(\\d){9}";
	
	private static final String MSISDN_PATTERN_SCM_FL = "(423|\\+423|00423)(\\d){7}";

	// avoid instantiating util class
	private MsisdnUtil() {
	}

	/**
	 * Parses the msisdn to valid phone number format. Only Switzerland
	 * and Liechtenstein numbers are allowed. In case of valid phone
	 * number the msisdn will be formatted to the following format: </p> <code>
	 * [dial-code][provider-code][phone-number], example 41791112233
	 * </br>
	 * dial-code: 41
	 * </br>
	 * provider-code: 79
	 * </br>
	 * phone-number: 1112233
	 * </code> </p> White spaces, '-', '/' and (0)- characters, are allowed in
	 * the phone number. </p> Following number formats will be accepted (xxxxxxx
	 * represents some 7 digits): </p>
	 * <ul>
	 * <li>076xxxxxxx</li>
	 * <li>076xxxxxxx</li>
	 * <li>077xxxxxxx</li>
	 * <li>078xxxxxxx</li>
	 * <li>079xxxxxxx</li>
	 * <li>4176xxxxxxx</li>
	 * <li>4177xxxxxxx</li>
	 * <li>4178xxxxxxx</li>
	 * <li>4179xxxxxxx</li>
	 * <li>+4176xxxxxxx</li>
	 * <li>+4177xxxxxxx</li>
	 * <li>+4178xxxxxxx</li>
	 * <li>+4179xxxxxxx</li>
	 * <li>004176xxxxxxx</li>
	 * <li>004177xxxxxxx</li>
	 * <li>004178xxxxxxx</li>
	 * <li>004179xxxxxxx</li>
	 * <li>+423xxxxxxx (Liechtenstein)</li>
	 * <li>423xxxxxxx (Liechtenstein)</li>
	 * <li>00423xxxxxxx (Liechtenstein)</li>
	 * <li>004178xxxxxxx</li>
	 * <li>004178xxxxxxx</li>
	 * <li>004178xxxxxxx</li>
	 * <li>004178xxxxxxx</li>
	 * <li>004178xxxxxxx</li>
	 * <li>004178xxxxxxx</li>
	 * <li>004178xxxxxxx</li>
	 * <li>41 (0)78 xxxxxxx</li>
	 * <li>+41-78-xxx-xx-xx</li>
	 * <li>078/xxxxxxx</li>
	 * <li>41(0)78xxxxxxx</li>
	 * <li>+41/78-xxxxxxx</li>
	 * </ul>
	 * </p>
	 * 
	 * @param msisdn
	 *            the msisdn to be parsed
	 * @return the string
	 * @throws IllegalArgumentException
	 *             the illegal argument exception is thrown if the msisdn don't
	 *             match the pattern.
	 */
	public static String parseMsisdn(String msisdn)
			throws IllegalArgumentException {
		msisdn = msisdn.replaceAll("\\s*", "").replaceAll("/", "")
				.replaceAll("\\(0\\)", "").replaceAll("-", "");
		if (msisdn.matches(MSISDN_PATTERN_SCM_CH) || msisdn.matches(MSISDN_PATTERN_SCM_FL)) {
			// format the number
			if (msisdn.startsWith("+")) {
				return msisdn.substring(1);
			} else if (msisdn.startsWith("00")) {
				return msisdn.substring(2);
			} else if (msisdn.startsWith("0")) {
				return "41" + msisdn.substring(1);
			} else {
				return msisdn;
			}
		} else {
			throw new IllegalArgumentException("The given phone number: "
					+ msisdn + " has invalid format.");
		}
	}
	
}
