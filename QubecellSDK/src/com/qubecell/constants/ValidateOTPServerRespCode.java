/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.constants;

import android.content.Context;

public class ValidateOTPServerRespCode {
	
	public static final int SUCCESS = 101;
	public static final int AUTHENTICATION_FALIURE = 102;
	public static final int OPERATOR_NOT_SUPPORTED = 103;
	public static final int DUPLICATE_REQUEST_ID = 104;
	public static final int INVALID_OTP_EXPIRED = 105;
	public static final int UNDETERMINED_ERROR = 106;
	public static final int INVALID_KEY = 107;
	public static final int INVALID_MSISDN = 108;
	
	private static String validateotp_authentication_faliure = "Incorrect OTP. Please try again.";
	private static String validateotp_operator_not_found = "Sorry, your mobile operator is not supported as yet.";
	private static String validateotp_duplicate_req_id = "Incorrect OTP. Please try again.";
	private static String validateotp_invalid_otp_expired = "Incorrect OTP. Please try again.";
	private static String validateotp_undetermined_error = "Incorrect OTP. Please try again.";
	private static String validateotp_invalid_key = "Incorrect OTP. Please try again.";
	private static String validateotp_invalid_msisdn = "Incorrect OTP. Please try again.";
	
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
			errorString = validateotp_authentication_faliure;
		}
		break;
		case OPERATOR_NOT_SUPPORTED:
		{
			errorString = validateotp_operator_not_found;
		}
		break;
		case DUPLICATE_REQUEST_ID:
		{
			errorString = validateotp_duplicate_req_id;
		}
		break;
		case INVALID_OTP_EXPIRED:
		{
			errorString = validateotp_invalid_otp_expired;
		}
		break;
		case UNDETERMINED_ERROR:
		{
			errorString = validateotp_undetermined_error;
		}
		break;
		case INVALID_KEY:
		{
			errorString = validateotp_invalid_key;
		}
		break;
		case INVALID_MSISDN:
		{
			errorString = validateotp_invalid_msisdn;
		}
		break;

		default:
			break;
		}
		return errorString;
	}
}
