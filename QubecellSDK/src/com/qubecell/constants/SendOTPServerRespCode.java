/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.constants;

public class SendOTPServerRespCode {

	public static final int SUCCESS = 101;
	public static final int AUTHENTICATION_FALIURE = 102;
	public static final int MANDATORY_PARAM_MISSING = 103;
	public static final int OPERATOR_NOT_SUPPORTED = 104;
	public static final int DUPLICATE_REQUEST_ID = 105;
	public static final int UNDETERMINED_ERROR = 106;
	public static final int INVALID_KEY = 107;
	public static final int SEND_OTP_FAILED = 108;

	private static String sendotp_mandatory_param_missing = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String sendotp_authentication_faliure = "Sorry, this transaction cannot be processed now. Please try again later. ";
	private static String sendotp_operator_not_found = "Sorry, your mobile operator is not supported as yet.";
	private static String sendotp_duplicate_req_id = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String sendotp_undetermined_error = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String sendotp_invalid_key = "Sorry, this transaction cannot be processed now. Please try again later.";
	private static String sendotp_send_otp_failed = "Sorry, this transaction cannot be processed now. Please try again later.";
    
	
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

		case AUTHENTICATION_FALIURE:
		{
			errorString = sendotp_authentication_faliure;
		}
		break;
		case MANDATORY_PARAM_MISSING:
		{
			errorString = sendotp_mandatory_param_missing;
		}
		break;
		case OPERATOR_NOT_SUPPORTED:
		{
			if(MerchantData.sendotpErrorMsg != null)
			{
				errorString = MerchantData.sendotpErrorMsg;
			}
			else
			{
				errorString = sendotp_operator_not_found;
			}
		}
		break;
		case DUPLICATE_REQUEST_ID:
		{
			errorString = sendotp_duplicate_req_id;
		}
		break;
		case UNDETERMINED_ERROR:
		{
			errorString = sendotp_undetermined_error;
		}
		break;
		case INVALID_KEY:
		{
			errorString = sendotp_invalid_key;
		}
		break;
		case SEND_OTP_FAILED:
		{
			errorString = sendotp_send_otp_failed;
		}
		break;
		default:
			break;
		}	
		return errorString;
	}
}
