/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.R;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qubecell.beans.EventChargeRespBean;
import com.qubecell.beans.ResponseBaseBean;
import com.qubecell.beans.SendOTPRespBean;
import com.qubecell.constants.ApplicationActivities;
import com.qubecell.constants.ConstantStrings;
import com.qubecell.constants.IntentConstant;
import com.qubecell.constants.MobileOperators;
import com.qubecell.constants.MsisdnServerRespCode;
import com.qubecell.constants.NetworkResponse;
import com.qubecell.constants.NetworkResponseCode;
import com.qubecell.constants.PaymentResult;
import com.qubecell.constants.ServerCommand;
import com.qubecell.constants.WidgetsTagName;
import com.qubecell.elogger.ELogger;
import com.qubecell.network.AsyncClient;
import com.qubecell.network.NetworkController;
import com.qubecell.utility.CommonUtility;
import com.qubecell.xmlparser.XMLParser;

/**
 * The SelectOperatorActivity class is used to display screen for submitting MSISDN number and operator selection.
 * @author Eninov
 *
 */
public class SelectOperatorActivity extends BaseActivity
{
	private View.OnClickListener clickListener = null;
	private String operatorSelected = null;
	private Button nextButton = null;
	private Button backButton = null;
	private String operatorProdId = null;
	private String msisdn = null; 
	private final int MOBILE_LENGTH = 12; // For India with country code
	private ELogger log = null;
	private String logTag = "SelectOperatirActivity::";
	private String productIdVoda = null;
	private String productIdAirtel = null;
	private String productIdIdea = null;
	private String productIdTata = null;
	private View operatorLayoutView = null;
	private String onSavedMobNumber = "mobNumberTag";
	private String onSavedOprSelected = "oprSelectedTag";
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		operatorLayoutView = CommonUtility.getOperatorSelectionLayoutView(getApplicationContext());
		setContentView(operatorLayoutView);
		log = new ELogger();
		log.setTag(logTag);
		String savedMobNumber = null;
		String savedOperator = null;
		if(savedInstanceState != null)
		{
			savedMobNumber = savedInstanceState.getString(onSavedMobNumber);
			savedOperator = savedInstanceState.getString(onSavedOprSelected);
		}

		initializeWidget(savedMobNumber);
		addOperatorsList(savedOperator);
		handleItemClickListener();
		getTransIntentData(getIntent());
		productIdAirtel = getAirtelProductId();
		productIdIdea = getIdeaProductId();
		productIdTata = getTataProductId();
		productIdVoda = getVodaProductId();
		setCurrentActivity(ApplicationActivities.SELECT_OPERATOR_ACTIVITY);
	}

	/**
	 * This method is used to save data on orientation change.
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle saveData) {
		super.onSaveInstanceState(saveData);
		log.debug("Inside onSaveInstance.");
		String mobMumber = ((EditText)operatorLayoutView.findViewWithTag(WidgetsTagName.OPERATOR_EDITTEXT)).getText().toString();
		if(mobMumber != null)
		{
			saveData.putString(onSavedMobNumber, mobMumber);
		}
		if(operatorSelected != null)
		{
			saveData.putString(onSavedOprSelected, operatorSelected);
		}
	}


	private void onNextButtonClick()
	{
		if(!TextUtils.isEmpty(operatorSelected))
		{
			operatorProdId = getProductId(operatorSelected);
			if(operatorProdId == null)
			{
				log.error("SelectOperator onClick product id not found.");
				return;
			}
			log.info("nextButtonClick() : Operator selected is : "+ operatorSelected);
			List<NameValuePair> requestParam = new ArrayList<NameValuePair>();
			requestId = String.valueOf(CommonUtility.getRandomNumberBetween());
			String chargeKey = getchargeKey();
			String md5Str = null;
			if(chargeKey != null)
			{
				md5Str = getMD5(chargeKey+requestId);
			}
			else
			{
				log.error("Authentication key not found.");
			}
			EditText mobileEditText = ((EditText)operatorLayoutView.findViewWithTag(WidgetsTagName.OPERATOR_EDITTEXT));
			msisdn = mobileEditText.getText().toString();
			log.info("msisdn value is :" + msisdn);
			if(!TextUtils.isEmpty(msisdn) && msisdn.length() == MOBILE_LENGTH)
			{
				String key = getchargeKey();
				if(key == null)
				{
					log.error("Authentication key not found...");
				}

				if(operatorSelected.equalsIgnoreCase(MobileOperators.IDEA))
				{
					setChargedAmount("0");
					requestParam.add(new BasicNameValuePair(ConstantStrings.USERNAME, username));
					requestParam.add(new BasicNameValuePair(ConstantStrings.PASSWORD, password));
					requestParam.add(new BasicNameValuePair(ConstantStrings.REQUESTID, requestId));
					requestParam.add(new BasicNameValuePair(ConstantStrings.OPERATION, "eventcharge"));
					requestParam.add(new BasicNameValuePair(ConstantStrings.PRODUCTID, operatorProdId));
					requestParam.add(new BasicNameValuePair(ConstantStrings.MESSAGE, "Event charge request"));
					requestParam.add(new BasicNameValuePair(ConstantStrings.KEY, md5Str));
					requestParam.add(new BasicNameValuePair(ConstantStrings.MSISDN, msisdn));
					requestParam.add(new BasicNameValuePair(ConstantStrings.RETURNURL, ""));
					requestParam.add(new BasicNameValuePair(ConstantStrings.LOG_PATH, ""));
					requestParam.add(new BasicNameValuePair(ConstantStrings.OPERATOR, operatorSelected));
					makeNetworkRequest(requestParam, ServerCommand.EVENTCHARGE_CMD);
				}
				else
				{
					requestParam.add(new BasicNameValuePair(ConstantStrings.USERNAME, username));
					requestParam.add(new BasicNameValuePair(ConstantStrings.PASSWORD, password));
					requestParam.add(new BasicNameValuePair(ConstantStrings.OPERATION, "sendotp"));
					requestParam.add(new BasicNameValuePair(ConstantStrings.REQUESTID, requestId));
					requestParam.add(new BasicNameValuePair(ConstantStrings.OPERATOR, operatorSelected));
					requestParam.add(new BasicNameValuePair(ConstantStrings.MESSAGE, ""));
					requestParam.add(new BasicNameValuePair(ConstantStrings.KEY, md5Str));
					requestParam.add(new BasicNameValuePair(ConstantStrings.MSISDN, msisdn));
					makeNetworkRequest(requestParam, ServerCommand.SENDOTP_CMD);
				}
			}
			else if (msisdn.length() == (MOBILE_LENGTH-2))
			{
				displayToastMessage(ConstantStrings.ENTER_COUNTRY_CODE);
			}
			else
			{
				displayToastMessage(ConstantStrings.INVALID_MOBILE_NUMBER);
			}
		}
		else
		{
			displayToastMessage(ConstantStrings.SELECT_OPERATOR);
		}
	}

	/**
	 * This method is used to get intent data and set it in class variable.
	 */
	private void getTransIntentData(Intent intent) 
	{
		if(intent == null)
		{
			log.error("setIntentData() : Intent is found null");
			return;
		}
		msisdn = intent.getStringExtra(IntentConstant.MSISDN);
		operator = intent.getStringExtra(IntentConstant.OPERATOR_INFO);
		username = getUsername(); 
		password = getPassword();
	}
	/**
	 * This method is used to handle the click listeners of button.
	 */
	private void handleItemClickListener() 
	{
		// Back Button Click listener
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{
				finish();
			}
		});

		// Next button click listener
		nextButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{
				onNextButtonClick();
			}
		});
	}


	/**
	 * This method is used to make request to the network
	 * @param requestParam 
	 * @param requestParam
	 */
	private void makeNetworkRequest(final List<NameValuePair> requestParam, final int requestType) 
	{
		if(requestParam == null)
		{
			log.error("makeNetworkRequest() : Request Param is found null");
			return ;
		}

		new AsyncClient<Object[], Object, NetworkResponse>() 
		{
			@Override
			protected void onPreExecute() 
			{
				showProgressDialogue("In Progress. . .");
			};

			@Override
			protected NetworkResponse doInBackground(Object[]... arg0) 
			{
				NetworkController nwObj = new NetworkController();
				NetworkResponse netresp = nwObj.httpPost(requestParam, requestType);
				return netresp;
			}

			@Override
			protected void onPostExecute(NetworkResponse result) 
			{
				dismissProgressDialogue();
				handleServerResponse(result, requestType);
			};

		}.execute();
	}


	/**
	 * This method is used to handle the server response from server.
	 * @param result
	 */
	protected void handleServerResponse(NetworkResponse result, int requestType) 
	{
		if(result == null)
		{
			log.error("handleServerResponse() : Networkresponse is null");
			return;
		}

		if(result.netRespCode == NetworkResponseCode.NET_RESP_SUCCESS)
		{
			if(TextUtils.isEmpty(result.respStr))
			{
				log.error("handleServerResponse() : Response String is null");
				Intent intent = new Intent(SelectOperatorActivity.this, ResultActivity.class);
				intent.putExtra(IntentConstant.PAYMENT_RESULT, PaymentResult.FALIURE);
				intent.putExtra(IntentConstant.MESSAGE,ConstantStrings.TRANSACTION_CANNOT_PROCESS);
				startActivity(intent);
				finish();
				return;
			}

			String responseStr = result.respStr;
			XMLParser xmlObj = XMLParser.getInstance();
			ResponseBaseBean respBean = xmlObj.getResponseBean(responseStr, requestType);
			if(respBean == null)
			{
				log.error("onPostExecute() :  Msisdn bean is found null");
				finish();
				return;
			}

			if(respBean.getResponsecode() == MsisdnServerRespCode.SUCCESS)
			{
				switch (requestType) {
				case ServerCommand.EVENTCHARGE_CMD:
				{
					EventChargeRespBean eveChargeRespBean = (EventChargeRespBean)respBean; 
					if(eveChargeRespBean.getAmount() != null)
					{
						setChargedAmount(eveChargeRespBean.getAmount());
					}
					else
					{
						log.error("Event charge payed ammount not found.");
					}
					Intent intent = new Intent(SelectOperatorActivity.this, ValidateOTPActivity.class);
					String txnId = eveChargeRespBean.getTxnid();
					intent.putExtra(IntentConstant.OPERATOR_INFO, operatorSelected);
					String chargeKey = getchargeKey();
					if(chargeKey != null)
					{
						intent.putExtra(IntentConstant.KEY, chargeKey);
					}
					else
					{
						log.error("Authentication key not found...");
					}
					intent.putExtra(IntentConstant.KEY, chargeKey);
					intent.putExtra(IntentConstant.TRANSACTION_ID, txnId);
					intent.putExtra(IntentConstant.MSISDN, msisdn);
					startActivity(intent);
					finish();
				}
				break;
				case ServerCommand.SENDOTP_CMD:
				{
					SendOTPRespBean sendOTPRespBean = (SendOTPRespBean)respBean; 
					Intent intent = new Intent(SelectOperatorActivity.this, ValidateOTPActivity.class);
					intent.putExtra(IntentConstant.OPERATOR_INFO, operatorSelected);
					String chargeKey = getchargeKey();
					if(chargeKey != null)
					{
						intent.putExtra(IntentConstant.KEY, chargeKey);
					}
					else
					{
						log.error("Authentication key not found...");
					}
					intent.putExtra(IntentConstant.TRANSACTION_ID, sendOTPRespBean.getTxnid());
					intent.putExtra(IntentConstant.MSISDN, msisdn);
					startActivity(intent);
					finish();
				}
				break;
				case ServerCommand.FETCH_OPR_CMD:
				{

				}
				break;
				default:
					break;
				}
			}
			else
			{
				log.info("response code is failed for request type : " + String.valueOf(requestType));
				log.info("and response code is " + String.valueOf(respBean.getResponsecode()));
				String message = getCommandErrorMessage(requestType,respBean);
				Intent intent = new Intent(SelectOperatorActivity.this, ResultActivity.class);
				intent.putExtra(IntentConstant.PAYMENT_RESULT, PaymentResult.FALIURE);
				intent.putExtra(IntentConstant.MESSAGE, message);
				startActivity(intent);
				finish();
			}
		}
		else
		{
			Intent intent = new Intent(SelectOperatorActivity.this, ResultActivity.class);
			intent.putExtra(IntentConstant.MESSAGE, ConstantStrings.TRANSACTION_CANNOT_PROCESS);
			intent.putExtra(IntentConstant.PAYMENT_RESULT, PaymentResult.FALIURE);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * This method is used to initialize all the widgets of select operator layout.
	 */
	private void initializeWidget(String savedMobNum) 
	{
		nextButton = ((Button)operatorLayoutView.findViewWithTag(WidgetsTagName.OPERATOR_NEXT_BUTTONVIEW));
		nextButton.setOnClickListener(clickListener);
		nextButton.setBackgroundColor(getBackGroundColor());
		backButton = ((Button)operatorLayoutView.findViewWithTag(WidgetsTagName.OPERATOR_BACK_BUTTONVIEW));
		backButton.setOnClickListener(clickListener);
		if(getBillingPartner() != null)
		{
			TextView poweredBy = ((TextView)operatorLayoutView.findViewWithTag(WidgetsTagName.OPERATOR_BOTTOM_TEXTVIEW));
			poweredBy.setText(getBillingPartner());
		}
		EditText mobileEditText = ((EditText)operatorLayoutView.findViewWithTag(WidgetsTagName.OPERATOR_EDITTEXT));
		if(savedMobNum != null)
		{
			mobileEditText.setText(savedMobNum);
		}
		else
		{		
			mobileEditText.setText("91");
		}
		if(getLogoImage() != null)
		{ 
			ImageView logoImageView = ((ImageView)operatorLayoutView.findViewWithTag(WidgetsTagName.OPERATOR_TOPHEADER_IMAGEVIEW));
			Drawable logoDrawable = new BitmapDrawable(getLogoImage());
			logoImageView.setBackgroundDrawable(logoDrawable);
		}
		LinearLayout mainFrame = ((LinearLayout)operatorLayoutView.findViewWithTag(WidgetsTagName.OPERATOR_MAIN_FRAME));
		mainFrame.setBackgroundColor(getThemeColor());
	}


	/**
	 * This method is used to add list of operators for selection.
	 */
	private void addOperatorsList(String savedOpr) 
	{
		ArrayList<String> operatorList = new ArrayList<String>();
		operatorList.add(MobileOperators.AIRTEL);
		operatorList.add(MobileOperators.VODAFONE);
		operatorList.add(MobileOperators.IDEA);
		operatorList.add(MobileOperators.TATA);

		LinearLayout operatorLL = (LinearLayout)operatorLayoutView.findViewWithTag(WidgetsTagName.OPERATOR_OPERATOTLIST);
		RadioGroup rs = new RadioGroup(getApplicationContext());
		rs.getCheckedRadioButtonId();
		rs.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < operatorList.size(); i++) 
		{
			Drawable drawObj = getResources().getDrawable(R.drawable.btn_radio);
			RadioButton radioBtn = new RadioButton(getApplicationContext());
			radioBtn.setId(i);
			radioBtn.setButtonDrawable(drawObj);
			radioBtn.setDrawingCacheEnabled(true);
			radioBtn.setTextColor(Color.BLACK);
			if(!TextUtils.isEmpty(savedOpr))
			{
				if(savedOpr.equalsIgnoreCase(operatorList.get(i)))
				{
					radioBtn.setChecked(true);
					operatorSelected = savedOpr;
					operator = operatorSelected;
				}
			}
			radioBtn.setText(operatorList.get(i));
			radioBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View radioButton) // Need to review
				{
					RadioButton cmdBtn = (RadioButton)radioButton;
					if(cmdBtn != null)
					{
						operatorSelected = cmdBtn.getText().toString();
						operator = operatorSelected;

						if(operatorSelected.equalsIgnoreCase(MobileOperators.VODAFONE))
						{
							operatorProdId = productIdVoda;
						}
						else if(operatorSelected.equalsIgnoreCase(MobileOperators.IDEA))
						{
							operatorProdId = productIdIdea;
						}
						else if(operatorSelected.equalsIgnoreCase(MobileOperators.AIRTEL))
						{
							operatorProdId = productIdAirtel;
						}
						else if(operatorSelected.equalsIgnoreCase(MobileOperators.TATA))
						{
							operatorProdId = productIdTata;
						}
						else
						{
							log.error("None of the above operator selected");
						}
					}
				}
			});

			rs.addView(radioBtn);
		}
		operatorLL.addView(rs);
	}
}
