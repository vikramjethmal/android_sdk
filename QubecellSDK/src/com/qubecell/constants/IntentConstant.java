/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.constants;

/**
 * The IntentConstant class is used to store constants values which is used
 * in passing extra information via Intent.
 * @author Eninov
 *
 */
public class IntentConstant 
{
	/*
	 * Mandatory parameters : Authenticated user for event charge request.
	 */
	public static final String USERNAME = "Username";

	/*
	 * Mandatory parameters : Authenticated password of USERNAME for event charge request.
	 */
	public static final String PASSWORD = "Password";

	/*
	 * Mandatory parameters : Authenticated user to send msisdn request
	 */
	public static final String MSISDN_USERNAME = "msisdnUsername";

	/*
	 * Mandatory parameters : Authenticated password of MSISDN_USERNAME for msisdn request
	 */
	public static final String MSISDN_PASSWORD = "msisdnPassword";

	/*
	 *  Product id  used in case of no data connection, 
	 *  If product id is set then charging will allow even if offline mode
	 *  else not.
	 */
	public static final String PRODUCT_ID = "productId";

	/*
	 * Mandatory parameters : Product if of Vodafone operator.
	 */
	public static final String VODA_PRODUCT_ID = "vodaproductId";

	/*
	 * Mandatory parameters : Product if of Airtel operator.
	 */
	public static final String AIRTEL_PRODUCT_ID = "airtelproductId";

	/*
	 * Mandatory parameters : Product if of Idea operator.
	 */
	public static final String IDEA_PRODUCT_ID = "ideaproductId";

	/*
	 * Mandatory parameters : Product if of Tata operator.
	 */
	public static final String TATA_PRODUCT_ID = "tataproductId";

	/*
	 * Mandatory parameters : Event charge authentication unique key.
	 */
	public static final String KEY = "key";

	/*
	 * Mandatory parameters : Msisdn authentication unique key.
	 */
	public static final String MSISDN_KEY = "msisdnKey";
	public static final String OPERATOR_INFO = "operatorInfo";
	public static final String TRANSACTION_ID = "transactionId";
	public static final String MSISDN = "msisdn";
	public static final String MESSAGE = "message";

	/*
	 * Mandatory parameters : Event charge detection amount in Indian Rupee.
	 */
	public static final String PAY_AMOUNT = "payAmount";

	/*
	 * Application tittle.
	 */
	//public static final String TITTLE_TEXT = "tittleText";

	/*
	 * Application tittle.
	 */
	//public static final String APPLICATION_TITTLE = "appTittle";


	/*
	 * Set color of theme in integer
	 */
	public static final String THEME_COLOR = "themeColor";
	public static final String PAYMENT_RESULT = "paymentResult";
	public static final String CLOSE_PROGRESSBAR = "closeProgressbar";	
	public static final String ARRAY_LIST = "arrayList";
	public static final String OPERATION = "operation";
	public static final String OPERATION_TYPE = "operationType"; 

	/*
	 * Set error message be displayed when msisdn response failed
	 */
	public static final String MSISDN_ERROR_MSG = "msisdnErrorMsg";


	/*
	 * Set message be displayed when event charge success.
	 */
	public static final String EVENTCHARGE_STATUS_MSG = "eventchargeStatusMsg";

	/*
	 * Set message be displayed when event charge failed.
	 */
	public static final String EVENTCHARGE_ERROR_MSG = "eventchargeErrorMsg";

	/*
	 * Set error message be displayed when sending OTP failed.
	 */
	public static final String SENDOTP_ERROR_MSG = "sendotpErrorMsg";	
}
