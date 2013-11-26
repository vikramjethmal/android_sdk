/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.constants;

import android.content.Context;

public class CheckstatusServerRespCode {
	public static final int SUCCESS = 101;
	public static final int AUTHENTICATION_FALIURE = 102;
	public static final int OPERATOR_NOT_SUPPORTED = 103;
	public static final int DUPLICATE_REQUEST_ID = 104;
	public static final int UNSUBSCRIBED = 105;
	public static final int UNDETERMINED_ERROR = 106;
	public static final int CUSTOMER_NOT_MAPPED_WITH_PRODUCTID = 107;
	public static final int RENEWED = 108;
	public static final int PARKING = 109;
	public static final int INVALID_KEY = 110;
	public static final int INVALID_MSISDN = 111;
	
	
	private static String checkstatus_authentication_faliure = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String checkstatus_operator_not_found = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String checkstatus_duplicate_req_id = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String checkstatus_unsubscribed = "";
	private static String checkstatus_undetermined_error = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String checkstatus_customer_notmapped_with_productid = " Sorry, this transaction cannot be processed now. Please try again later.";
	private static String checkstatus_renewed = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String checkstatus_parking = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String checkstatus_invalid_key = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String checkstatus_invalid_msisdn = "Sorry, this transaction cannot be processed now. Please try again later.";
    
	
	/**
	 * This method is used to return the server error strings as per given error code.
	 * @param appContext
	 * @param responseCode
	 * @return
	 */
	public static String getResponseString(Context appContext,int responseCode)
	{
		String errorString = null;

		switch (responseCode) {
		case AUTHENTICATION_FALIURE:
		{
			errorString = checkstatus_authentication_faliure;
		}
		break;
		case OPERATOR_NOT_SUPPORTED:
		{
			errorString = checkstatus_operator_not_found;
		}
		break;
		case DUPLICATE_REQUEST_ID:
		{
			errorString = checkstatus_duplicate_req_id;
		}
		break;
		case UNSUBSCRIBED:
		{
			errorString = checkstatus_unsubscribed;
		}
		break;
		case UNDETERMINED_ERROR:
		{
			errorString = checkstatus_undetermined_error;
		}
		break;
		case CUSTOMER_NOT_MAPPED_WITH_PRODUCTID:
		{
			errorString = checkstatus_customer_notmapped_with_productid;
		}
		break;
		case RENEWED:
		{
			errorString = checkstatus_renewed;
		}
		break;
		case PARKING:
		{
			errorString = checkstatus_parking;
		}
		break;

		case INVALID_KEY:
		{
			errorString = checkstatus_invalid_key;
		}
		break;

		case INVALID_MSISDN:
		{
			errorString = checkstatus_invalid_msisdn;
		}
		break;
		default:
			break;
		}
		return errorString;	
	}
}
