/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.constants;


public class EventChargeServerRespCode {

	public static final int SUCCESS = 101;
	public static final int AUTHENTICATION_FALIURE = 102;
	public static final int OPERATOR_NOT_SUPPORTED = 103;
	public static final int DUPLICATE_REQUEST_ID = 104;
	public static final int CHARGE_FAILED = 105;
	public static final int UNDETERMINED_ERROR = 106;
	public static final int INVALID_KEY = 107;
	public static final int FALIURE = 108;
	public static final int INVALID_MSISDN = 109;
	public static final int INVALID_PRODUCT_ID = 110;

	
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
	public static String getResponseString(int responseCode)
	{
		String errorString = null;

		switch (responseCode) {
		case SUCCESS:
		{
			if(MerchantData.eventchargeStatusMsg != null)
			{
				errorString = MerchantData.eventchargeStatusMsg;
			}
		}
		break;
		case AUTHENTICATION_FALIURE:
		{
			errorString = eventcharge_authentication_faliure;
		}
		break;
		case OPERATOR_NOT_SUPPORTED:
		{
			if(MerchantData.eventchargeErrorMsg != null)
			{
				errorString = MerchantData.eventchargeErrorMsg;
			}
			else
			{
				errorString = eventcharge_operator_not_found;
			}
		}
		break;
		case DUPLICATE_REQUEST_ID:
		{
			errorString = eventcharge_duplicate_req_id;
		}
		break;
		case CHARGE_FAILED:
		{
			errorString = eventcharge_charge_failed;
		}
		break;
		case UNDETERMINED_ERROR:
		{
			errorString = eventcharge_undetermined_error;
		}
		break;
		case INVALID_KEY:
		{
			errorString = eventcharge_invalid_key;
		}
		break;
		case FALIURE:
		{
			errorString = eventcharge_failed;
		}
		break;
		case INVALID_MSISDN:
		{
			errorString = eventcharge_invalid_msisdn;
		}
		break;
		case INVALID_PRODUCT_ID:
		{
			errorString = eventcharge_invalid_productid;			
		}
		break;

		default:
			break;
		}	
		return errorString;
	}
}
