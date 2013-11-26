/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.constants;

import android.content.Context;

public class MessageResponseCode {
	
	public static final String SUCCESS = "101";
	public static final String AUTHENTICATION_FALIURE = "102";
	public static final String OPERATOR_NOT_SUPPORTED = "103";
	public static final String DUPLICATE_REQUEST_ID = "104";
	public static final String CHARGE_FAILED = "105";
	public static final String UNDETERMINED_ERROR = "106";
	public static final String INVALID_KEY = "107";
	public static final String FALIURE = "108";
	public static final String INVALID_MSISDN = "109";
	public static final String INVALID_PRODUCT_ID = "110";
	
	private static String eventcharge_authentication_faliure = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String eventcharge_operator_not_found = "Sorry, your mobile operator is not supported as yet.";
	private static String eventcharge_duplicate_req_id = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String eventcharge_charge_failed ="Sorry, this transaction cannot be processed now. Please try again later.";
	private static String eventcharge_undetermined_error = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String eventcharge_invalid_key = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String eventcharge_failed = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String eventcharge_invalid_msisdn = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String eventcharge_invalid_productid = "Sorry, this transaction cannot be processed now. Please try again later.";

	/**
	 * This method is used to return the server error strings as per given error code.
	 * @param appContext
	 * @param responseCode
	 * @return
	 */
	public static String getResponseString(Context appContext, String responseCode)
	{
		String errorString = null;

		if(responseCode.contains(AUTHENTICATION_FALIURE))
		{
			errorString = eventcharge_authentication_faliure;
		}
		else if(responseCode.contains(OPERATOR_NOT_SUPPORTED))
		{
			errorString = eventcharge_operator_not_found;
		}
		else if(responseCode.contains(DUPLICATE_REQUEST_ID))
		{
			errorString = eventcharge_duplicate_req_id;
		}
		else if(responseCode.contains(CHARGE_FAILED))
		{
			errorString = eventcharge_charge_failed;
		}
		else if(responseCode.contains(UNDETERMINED_ERROR))
		{
			errorString = eventcharge_undetermined_error;
		}
		else if(responseCode.contains(INVALID_KEY))
		{
			errorString = eventcharge_invalid_key;
		}
		else if(responseCode.contains(FALIURE))
		{
			errorString = eventcharge_failed;
		}
		else if(responseCode.contains(INVALID_MSISDN))
		{
			errorString = eventcharge_invalid_msisdn;
		}
		else if(responseCode.contains(INVALID_PRODUCT_ID))
		{
			errorString = eventcharge_invalid_productid;
		}
		return errorString;
	}
}
