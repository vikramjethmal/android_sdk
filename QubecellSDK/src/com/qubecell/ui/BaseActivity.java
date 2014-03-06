/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.ui;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qubecell.beans.ResponseBaseBean;
import com.qubecell.constants.ApplicationActivities;
import com.qubecell.constants.CheckstatusServerRespCode;
import com.qubecell.constants.ConstantStrings;
import com.qubecell.constants.EventChargeServerRespCode;
import com.qubecell.constants.IntentConstant;
import com.qubecell.constants.LastAPIStatusServerRespCode;
import com.qubecell.constants.MerchantData;
import com.qubecell.constants.MobileOperators;
import com.qubecell.constants.MsisdnServerRespCode;
import com.qubecell.constants.PaymentResult;
import com.qubecell.constants.QubecellResult;
import com.qubecell.constants.SendOTPServerRespCode;
import com.qubecell.constants.ServerCommand;
import com.qubecell.constants.ThemeConfigurationVariables;
import com.qubecell.constants.ValidateOTPServerRespCode;
import com.qubecell.elogger.ELogger;
import com.qubecell.network.NetworkController;
import com.qubecell.smsmgr.QubecellSMSManager;
import com.qubecell.utility.CommonUtility;
import com.qubecell.xmlparser.XMLParser;

/**
 * The BaseActivity class need to be extended by all the child activity classes.
 * @author Eninov
 *
 */

public class BaseActivity extends Activity {

	private ELogger log = null;
	private String logTag = "BaseActivity::";
	protected Context appContext = null;
	protected ProgressDialog pd = null;
	protected static Color textColor = null;
	protected static String buttonText = null;
	protected static Color buttonBackgroundColor = null;
	protected boolean isGPRSActive = false;
	protected boolean isWiFiActive = false;
	protected boolean noDataConnection = false;
	protected XMLParser xmlObj = null;
	protected QubecellSMSManager smsObj = null;
	protected NetworkController nwObj = null;
	
	/**
	 * Last used RequestId and API name
	 */
	public static long lastRequestId;
	protected String lastAPIName;
	
	/**
	 * This variable is used to check flow of request is through WiFi , GPRS or SMS based.
	 */
	protected boolean requestFlow = false;
	
	/**
	 * Variable provided by Merchant application.
	 */
	protected static String username = null;
	protected static String password = null;
	protected static String requestId = null;			
	protected static String payAmount = null;
	protected static String operator = null;
	
	protected String smsShortCode = ConstantStrings.SMS_SHOTCODE;
	protected String messageFormat = ConstantStrings.SEND_MESSAGE_FORMAT;
	protected String validateFormat = ConstantStrings.RECEIVE_MESSAGE_FORMAT;
	private int delay = 1000*40;
	protected String CurrentActivity;
	public static int receiveSmsOnPort ; //To receive sms
	public static final int SUCCESS = 1;
	public static final int FAILURE = 0;
	
	// OnSavedInstance Tag Value
	protected final String onSavedMobNumber = "mobNumberTag";
	protected final String onSavedOprSelected = "oprSelectedTag";
	protected final String isProDialogVisible = "isDialogVisible";
	protected final String onSavedUsername = "username";
	protected final String onSavedPassword = "password";
	protected final String isAlertPermDiaStr = "isAlertPermDiaStr";
	protected final String lastAPIStatusCountStr = "lastAPIStatusCount";
	protected int lastAPIStatusCount = 0;
	
	protected boolean isProDiaVisible = false;

	protected Handler handler = new Handler();
	Runnable progressDiaplay = new Runnable()
	{
		@Override
		public void run() 
		{
			dismissProgressDialogue();
			if(getCurrentActivity().equalsIgnoreCase(ApplicationActivities.CLOSE_ACTIVITY) && (getReceiveSmsonPort() == 0))
			{
				setReceiveSmsonPort(1); //To not receive sms
				Intent intent = new Intent(BaseActivity.this, ResultActivity.class);
				log.info("Runnable progressDiaplay progressbar dismiss on timeout.");
				intent.putExtra(IntentConstant.MESSAGE, ConstantStrings.TRANSACTION_INPROGRESS);
				intent.putExtra(IntentConstant.PAYMENT_RESULT, PaymentResult.FALIURE);
				startActivity(intent);
				finish();
				
			}	
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLog();
		appContext = getApplicationContext();
		checkNetworkStatus();
	}

	/**
	 * This method is used to initialize the ELogger object.
	 */
	private void initLog() 
	{
		if(log == null)
		{
			log = new ELogger();
			log.setTag(logTag);
			ELogger.init(ConstantStrings.LOG_FILE_NAME);
			ELogger.setLogLevel(ELogger.INFO);
		}
	}

	/**
	 * This method is used to check with which type of network the device is connected.
	 */
	private void checkNetworkStatus() 
	{
		isGPRSActive = CommonUtility.isGPRSConnected(appContext);
		log.info("checkNetworkStatus() : GPRS is : "+ isGPRSActive);
		if(isGPRSActive)
		{
			log.info("checkNetworkStatus() : GPRS network is active");
			noDataConnection = false;
			isWiFiActive = false;
		}
		else
		{
			isWiFiActive = CommonUtility.isWiFiConnected(appContext);
			if(isWiFiActive)
			{
				log.info("checkNetworkStatus() : Wifi network is active");
				isGPRSActive = false;
				noDataConnection = false;
			}
			else
			{
				log.info("checkNetworkStatus() : No data network connection is active");
				isGPRSActive = false;
				isWiFiActive = false;
				noDataConnection = true;
			}
		}
	}

	/**
	 * This method is used to start the handler with a delayed time. After which progress bar will be 
	 * closed automatically.
	 */
	protected void setProgressDisplayTime()
	{
		if(handler != null)
			handler.postDelayed(progressDiaplay, delay);
	}

	/**
	 * This method is used to manually dismiss the progress bar after receiving sms from server.  
	 */
	public void dismissProgressDialogOnEvent()
	{
		try 
		{
			BaseActivity.this.runOnUiThread(new Runnable() {

		        @Override
		        public void run() {
		        	dismissProgressDialogue();
					if(handler != null)
						handler.removeCallbacks(progressDiaplay);
		        }
		    });
		}
		catch(Exception e)
		{
			log.info("Cautch exception while dismissing progressbar on event.");
		}
	}

	/**
	 * These are setter and getter of merchant app credentials.
	 */
	protected void setProductId(String val) 
	{
		if(val != null)
			MerchantData.merchantProdId = val;
	}

	protected String getProductId() 
	{
		return MerchantData.merchantProdId;
	}

	protected void setVodaProductId(String val)//pass
	{
		if(val != null)
			MerchantData.productIdVoda = val;
	}

	protected String getVodaProductId()//pass
	{
		return MerchantData.productIdVoda;
	}

	protected void setIdeaProductId(String val)//pass
	{
		if(val != null)
			MerchantData.productIdIdea = val;
	}

	protected String getIdeaProductId()//pass
	{
		return MerchantData.productIdIdea;
	}

	protected void setAirtelProductId(String val)
	{
		if(val != null)
			MerchantData.productIdAirtel = val;
	}

	protected String getAirtelProductId()
	{
		return MerchantData.productIdAirtel;
	}

	protected void setTataProductId(String val)
	{
		if(val != null)
			MerchantData.productIdTata = val;
	}

	protected String getTataProductId()
	{
		return MerchantData.productIdTata;
	}

	protected void setChargeKey(String val)
	{
		if(val != null)
			MerchantData.chargeKey = val;
	}

	protected String getchargeKey()
	{
		return MerchantData.chargeKey;
	} 

	protected void setUsername(String val)
	{
		if(val != null)
			MerchantData.username = val;
	}

	protected String getUsername()
	{
		return MerchantData.username;
	}

	protected void setPassword(String val)
	{
		if(val != null)
			MerchantData.password = val;
	}

	protected String getPassword()
	{
		return MerchantData.password;
	}

	protected void setMsisdnUsername(String val)
	{
		if(val != null)
			MerchantData.msisdnUsername = val;
	}

	protected String getMsisdnUsername()
	{
		return MerchantData.msisdnUsername;
	}

	protected void setMsisdnPassword(String val)
	{
		if(val != null)
			MerchantData.msisdnPassword = val;
	}

	protected String getMsisdnPassword()
	{
		return MerchantData.msisdnPassword;
	}

	protected void setMsisdnChargeKey(String val)
	{
		if(val != null)
			MerchantData.msisdnChargeKey = val;
	}

	protected String getMsisdnChargeKey()
	{
		return MerchantData.msisdnChargeKey;
	}

	protected void setPayamount(String val)
	{
		if(val != null)
			MerchantData.payamount = val;
	}

	protected String getPayamount()
	{
		return MerchantData.payamount;
	}

	protected void setMsisdnErrorMsg(String val)
	{
		if(val != null)
			MerchantData.msisdnErrorMsg = val;
	}

	protected String getMsisdnErrorMsg()
	{
		return MerchantData.msisdnErrorMsg;
	}

	protected void setEventchargeStatusMsg(String val)
	{
		if(val != null)
			MerchantData.eventchargeStatusMsg = val;
	}

	protected String getEventchargeStatusMsg()
	{
		return MerchantData.eventchargeStatusMsg;
	}

	protected void setEventchargeErrorMsg(String val)
	{
		if(val != null)
			MerchantData.eventchargeErrorMsg = val;
	}

	protected String getEventchargeErrorMsg()
	{
		return MerchantData.eventchargeErrorMsg;
	}

	protected void setSendotpErrorMsg(String val)
	{
		if(val != null)
			MerchantData.sendotpErrorMsg = val;
	}

	protected String getSendotpErrorMsg()
	{
		return MerchantData.sendotpErrorMsg;
	}

	/**
	 * This method is used to set the data passed by merchant application in BaseActivity's variables.
	 * @param intent
	 */
	protected int setIntentData(Intent intent) 
	{
		if(intent == null)
		{
			log.error("setIntentData() : Intent not found.");
			return -1;
		}
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(IntentConstant.ARRAY_LIST);
		HashMap<String, String> hMap = list.get(0);
		String locUsername = hMap.get(IntentConstant.USERNAME); 
		String locPpassword =hMap.get(IntentConstant.PASSWORD); 
		requestId = String.valueOf(CommonUtility.getRandomNumberBetween()); 
		operator = hMap.get(IntentConstant.OPERATOR_INFO);
		String merchantProdId = hMap.get(IntentConstant.PRODUCT_ID);
		String productIdVoda = hMap.get(IntentConstant.VODA_PRODUCT_ID); 
		String productIdIdea = hMap.get(IntentConstant.IDEA_PRODUCT_ID); 
		String productIdAirtel = hMap.get(IntentConstant.AIRTEL_PRODUCT_ID); 
		String productIdTata = hMap.get(IntentConstant.TATA_PRODUCT_ID); 
		String key = hMap.get(IntentConstant.KEY);
		String msisdnUsername = hMap.get(IntentConstant.MSISDN_USERNAME); 
		String msisdnPassword = hMap.get(IntentConstant.MSISDN_PASSWORD); 
		String msisdnKey = hMap.get(IntentConstant.MSISDN_KEY); 
		String msisdnErrorMsg = hMap.get(IntentConstant.MSISDN_ERROR_MSG);
		String eventchargeErrorMsg = hMap.get(IntentConstant.EVENTCHARGE_ERROR_MSG);
		String eventchargeStatusMsg = hMap.get(IntentConstant.EVENTCHARGE_STATUS_MSG);
		String sendotpErrorMsg = hMap.get(IntentConstant.SENDOTP_ERROR_MSG);

		payAmount = hMap.get(IntentConstant.PAY_AMOUNT);
		if(locUsername != null)
		{
			setUsername(locUsername);
		}
		else
		{
			log.error("setIntentData:: username not found");
			return -1;
		}
		if(locPpassword != null)
		{
			setPassword(locPpassword);
		}
		else
		{
			log.error("setIntentData:: password not found");
			return -1;
		}
		if(merchantProdId != null)
		{
			setProductId(merchantProdId);
		}
		if(productIdVoda != null)
		{
			setVodaProductId(productIdVoda);
		}
		else
		{
			log.error("setIntentData:: vodafone operator product id  not found");
			return -1;
		}
		if(productIdIdea != null)
		{
			setIdeaProductId(productIdIdea);
		}
		else
		{
			log.error("setIntentData:: idea operator product id not found");
			return -1;
		}
		if(productIdAirtel != null)
		{
			setAirtelProductId(productIdAirtel);
		}
		else
		{
			log.error("setIntentData:: airtel operator product id  not found");
			return -1;
		}
		if(productIdTata != null)
		{
			setTataProductId(productIdTata);
		}
		else
		{
			log.error("setIntentData:: tata operator product id  not found");
			return -1;
		}
		if(key != null)
		{
			setChargeKey(key);
		}
		else
		{
			log.error("setIntentData:: key not found.");
			return -1;
		}
		if(msisdnUsername != null)
		{
			setMsisdnUsername(msisdnUsername);
		}
		else
		{
			log.error("setIntentData:: MSISDN username not found.");
			return -1;
		}
		if(msisdnPassword != null)
		{
			setMsisdnPassword(msisdnPassword);
		}
		else
		{
			log.error("setIntentData:: MSISDN password not found.");
			return -1;
		}
		
		if(msisdnKey != null)
			setMsisdnChargeKey(msisdnKey);
		else
		{
			log.error("setIntentData:: MSISDN key not found.");
			return -1;
		}
		if(payAmount != null)
			setPayamount(payAmount);
		else
		{
			log.error("setIntentData:: payment amount not found.");
			return -1;
		}
		
		if(msisdnErrorMsg != null)
		{
			setMsisdnErrorMsg(msisdnErrorMsg);
		}
		if(eventchargeErrorMsg != null)
		{
			setEventchargeErrorMsg(eventchargeErrorMsg);
		}
		if(eventchargeStatusMsg != null)
		{
			setEventchargeStatusMsg(eventchargeStatusMsg);
		}
		if(sendotpErrorMsg != null)
		{
			setSendotpErrorMsg(sendotpErrorMsg);
		}

		payAmount = intent.getStringExtra(IntentConstant.PAY_AMOUNT);
		log.debug("*****************************");
		log.debug("username" +getUsername()+" password "+getPassword()+ " msisdnuser " +getMsisdnUsername()+ " msisdnpwd "+getMsisdnPassword()+" key "+getchargeKey()+" msisdnkey "+getMsisdnChargeKey()+" requestid "+ requestId);
		log.debug(" tata " + getTataProductId()+" voda "+getVodaProductId()+" idea "+getIdeaProductId()+" airtel "+getAirtelProductId()+" sms "+getProductId());
		log.debug("*****************************");
		return 1;
	}


	/**
	 * This method is used to display the toast messages.
	 * @param message
	 */
	public void displayToastMessage(String message)
	{
		Toast.makeText(getApplicationContext(), message	, Toast.LENGTH_LONG).show();
	}

	/**
	 * This function will show progress dialogue with given message 
	 * @param msg String (message to show on progress dialogue if message is null then progress dialogue is shown without message)
	 */
	public void showProgressDialogue(String msg)
	{
		if(msg == null)
		{
			log.error("No message content found to display on progressbar. ");
			return;
		}
		if(pd == null)
		{
			pd = new ProgressDialog(BaseActivity.this);
		}
		pd.setMessage(msg);
		// This will cancel the progress bar on back button otherwise set it false
		pd.setCancelable(false);
		if(!pd.isShowing())
		{ 
			pd.show();
		}
	}

	/**
	 * This function will dismiss progress dialogue with given message 
	 * @param msg String 
	 */
	public void dismissProgressDialogue()
	{
		try
		{
			if(pd != null)
			{
				if(pd.isShowing())
					pd.dismiss();
				pd = null ;
			}
		}
		catch (Exception e)
		{
			log.info("Cautch exception while dismissing progress bar");
		}
	}//End of dismissProgressDialogue()

	/**
	 * This method is used to get the product id for the given operator.
	 * @param operatorStr
	 * @return
	 */
	protected String getProductId(String operatorStr)
	{
		String productId = null;
		if((operatorStr == null) || (TextUtils.isEmpty(operatorStr)))
		{
			log.info("getProductId() : Operator String not found.");
			return null;
		}
		if(operatorStr.equalsIgnoreCase(MobileOperators.NODATA))
		{
			productId = getProductId();
		}
		else if(operatorStr.equalsIgnoreCase(MobileOperators.IDEA))
		{
			productId = getIdeaProductId();
		}
		else if(operatorStr.equalsIgnoreCase(MobileOperators.VODAFONE))
		{
			productId = getVodaProductId();
		}
		else if(operatorStr.equalsIgnoreCase(MobileOperators.AIRTEL))
		{
			productId = getAirtelProductId();
		}
		else if(operatorStr.equalsIgnoreCase(MobileOperators.TATA))
		{
			productId = getTataProductId();
		}
		return productId;
	}

	/**
	 * This method is used to set configuration details provided by merchant. 
	 */
	protected void merchantColorConfigurationBG(LinearLayout innerLL)
	{
		if(innerLL == null || ThemeConfigurationVariables.themeColor == 0)
			return;
		innerLL.setBackgroundColor(ThemeConfigurationVariables.themeColor);
	}

	/**
	 * This method is used to convert the given string into MD5 format.
	 * @param input
	 * @return
	 */
	public String getMD5(String input) 
	{
		String hashText = null;
		try 
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while (hashtext.length() < 32) 
			{
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}
		catch (NoSuchAlgorithmException e) 
		{
			log.error("getMD5() : NoSuchAlgorithmException : "+ e);
		}
		catch(Exception e)
		{
			log.error("getMD5() : Unable to perform MD5 string conversion : "+ e);
		}
		return hashText;
	}

	/**
	 * This method is used to return error message received from server.
	 * @param requestType
	 * @param bean
	 * @return
	 */
	protected String getCommandErrorMessage(int requestType, ResponseBaseBean bean)
	{
		String message = null;
		if(bean == null)
			return message;
		switch (requestType) {
		case ServerCommand.MSISDN_CMD:
		{
			message = MsisdnServerRespCode.getResponseString(bean.getResponsecode());
		}	
		break;
		case ServerCommand.SENDOTP_CMD:
		{
			message = SendOTPServerRespCode.getResponseString(bean.getResponsecode());
		}	
		break;
		case ServerCommand.VALIDATEOTP_CMD:
		{
			message = ValidateOTPServerRespCode.getResponseString(bean.getResponsecode());
		}	
		break;
		case ServerCommand.EVENTCHARGE_CMD:
		{
			message = EventChargeServerRespCode.getResponseString(bean.getResponsecode()); 
		}	
		break;
		case ServerCommand.CHECK_STATUS_CMD:
		{
			message = CheckstatusServerRespCode.getResponseString(bean.getResponsecode()); 
		}	
		break;
		case ServerCommand.GETLASTSTATUS_CDM:
		{
			message = LastAPIStatusServerRespCode.getResponseString(bean.getResponsecode());
		}
		break;
		default:
			break;
		}
		return message;
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}

	public Bitmap getLogoImage() 
	{
		return ThemeConfigurationVariables.logoImage;
	}

	public static void setLogoImage(Bitmap _logoImage) 
	{
		if(_logoImage != null)
			ThemeConfigurationVariables.logoImage = _logoImage;
	}

	public Color getTextColor() 
	{
		return textColor;
	}

	public void setTextColor(Color _textColor) 
	{
		if(_textColor != null)
			textColor = _textColor;
	}

	public int getBackGroundColor() 
	{
		return ThemeConfigurationVariables.backGroundColor;
	}

	public static void setBackGroundColor(int _backGroundColor) 
	{
		ThemeConfigurationVariables.backGroundColor = _backGroundColor;
	}

	public int getThemeColor() 
	{
		return ThemeConfigurationVariables.themeColor;
	}

	public static void setThemeColor(int _themeColor) 
	{
		ThemeConfigurationVariables.themeColor = _themeColor;
	}

	public String getButtonText() 
	{
		return buttonText;
	}

	public static void setButtonText(String _buttonText) 
	{
		if(_buttonText != null)
			buttonText = _buttonText;
	}

	public Color getButtonBackgroundColor() {
		return buttonBackgroundColor;
	}

	public static void setButtonBackgroundColor(Color _buttonBackgroundColor) 
	{
		if(_buttonBackgroundColor != null)
			buttonBackgroundColor = _buttonBackgroundColor;
	}

	public String getTitleText() 
	{
		return ThemeConfigurationVariables.titleText;
	}

	public static void setTitleText(String _titleText) 
	{
		if(_titleText != null)
			ThemeConfigurationVariables.titleText = _titleText;
	}

	public String getCurrentActivity() 
	{
		return CurrentActivity;
	}
	public void setCurrentActivity (String message) //pass
	{
		if(message != null)
			this.CurrentActivity = message;
	}

	public String getBillingPartner() 
	{
		return ThemeConfigurationVariables.billingPartner;
	}

	public static void setBillingPartner (String message)
	{
		if(message != null)
			ThemeConfigurationVariables.billingPartner = message;
	}

	public static  void setReceiveSmsonPort(int val)
	{
		receiveSmsOnPort = val;
	}

	public static int getReceiveSmsonPort()
	{
		return receiveSmsOnPort;
	}

	public static boolean getQubecellStatus() 
	{
		if(QubecellResult.status == PaymentResult.PAYMENT_SUCCESS)
			return true;
		else
			return false;					
	}

	public void setQubecell(int val)
	{
		if(val == PaymentResult.PAYMENT_SUCCESS)
			QubecellResult.status = PaymentResult.PAYMENT_SUCCESS;
		else
			QubecellResult.status = PaymentResult.PAYMENT_FALIURE;
	}

	public static String getCahrgedAmount() {
		return QubecellResult.chargedAmount;			

	}
	public void setChargedAmount (String val) {
		if(val != null)
			QubecellResult.chargedAmount = val;
	}
	
	/**
	 * This method is used to call last requested API Status and take decisions based on that.
	 */
	public List<NameValuePair> getLastAPIStatus() {
		
		List<NameValuePair> requestParam = new ArrayList<NameValuePair>();
		String chargeKey = getchargeKey();
		String md5Str = null;
		String apirequestid = requestId;
		requestId = String.valueOf(CommonUtility.getRandomNumberBetween());
		if (chargeKey != null) {
			md5Str = getMD5(chargeKey + requestId);
		} else {
			log.error("getLastAPIStatus() : Authentication key not found..");
		}
		
		String uName = username;
		String pwd = password;
		requestParam.add(new BasicNameValuePair(ConstantStrings.USERNAME, uName));
		requestParam.add(new BasicNameValuePair(ConstantStrings.PASSWORD, pwd));
		requestParam.add(new BasicNameValuePair(ConstantStrings.REQUESTID, requestId));
		requestParam.add(new BasicNameValuePair(ConstantStrings.KEY,md5Str));
		requestParam.add(new BasicNameValuePair(ConstantStrings.API_REQUESTID, apirequestid));
		requestParam.add(new BasicNameValuePair(ConstantStrings.OPERATION,"apistatus"));
		requestParam.add(new BasicNameValuePair(ConstantStrings.OPERATOR, operator));
		
		return requestParam;
	}
}

