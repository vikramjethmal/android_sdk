/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.constants;

public class MsisdnServerRespCode {

	public static final int SUCCESS = 101;
	public static final int MANDATORY_PARAM_MISSING = 102;
	public static final int AUTHENTICATION_FALIURE = 103;
	public static final int OPERATOR_NOT_SUPPORTED = 104;
	public static final int DUPLICATE_REQUEST_ID = 105;
	public static final int UNDETERMINED_ERROR = 106;
	public static final int INVALID_KEY = 107;
	public static final int INVALID_RETURN_URL = 109;

	private static String msisdn_mandatory_param_missing = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String msisdn_authentication_faliure = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String msisdn_operator_not_found = "Sorry, your mobile operator is not supported as yet.";
	private static String msisdn_duplicate_req_id = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String msisdn_undetermined_error = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String msisdn_invalid_key = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String msisdn_invalid_return_url = "Sorry, this transaction cannot be processed now. Please try again later.";
	
	/**
	 * This method is used to return the server error strings as per given error code.
	 * @param appContext
	 * @param responseCode
	 * @return
	 */
	public static String getResponseString(int responseCode)
	{
		String errorString = null;

		switch (responseCode) {
		case MANDATORY_PARAM_MISSING:
		{
			errorString = msisdn_mandatory_param_missing;
		}
		break;
		case AUTHENTICATION_FALIURE:
		{
			errorString = msisdn_authentication_faliure;
		}
		break;
		case OPERATOR_NOT_SUPPORTED:
		{
			//If configurable message is provided.
			if(MerchantData.msisdnErrorMsg != null)
			{
				errorString = MerchantData.msisdnErrorMsg;
			}
			else
			{
				errorString = msisdn_operator_not_found;
			}
		}
		break;
		case DUPLICATE_REQUEST_ID:
		{
			errorString = msisdn_duplicate_req_id;
		}
		break;
		case UNDETERMINED_ERROR:
		{
			errorString = msisdn_undetermined_error;
		}
		break;
		case INVALID_KEY:
		{
			errorString = msisdn_invalid_key;
		}
		break;
		case INVALID_RETURN_URL:
		{
			errorString = msisdn_invalid_return_url;
		}
		break;

		default:
			break;
		}
		return errorString;
	}
}
