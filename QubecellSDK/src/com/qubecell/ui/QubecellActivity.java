/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.ui;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.qubecell.beans.EventChargeRespBean;
import com.qubecell.beans.MsisdnRespBean;
import com.qubecell.beans.OperatorDetails;
import com.qubecell.beans.OperatorsRespBean;
import com.qubecell.beans.ResponseBaseBean;
import com.qubecell.constants.ApplicationActivities;
import com.qubecell.constants.ConstantStrings;
import com.qubecell.constants.IntentConstant;
import com.qubecell.constants.MerchantData;
import com.qubecell.constants.MobileOperators;
import com.qubecell.constants.MsisdnServerRespCode;
import com.qubecell.constants.NetworkResponse;
import com.qubecell.constants.NetworkResponseCode;
import com.qubecell.constants.PaymentResult;
import com.qubecell.constants.QubecellResult;
import com.qubecell.constants.ServerCommand;
import com.qubecell.constants.WidgetsTagName;
import com.qubecell.elogger.ELogger;
import com.qubecell.network.AsyncClient;
import com.qubecell.network.NetworkController;
import com.qubecell.smsmgr.QubecellSMSManager;
import com.qubecell.utility.CommonUtility;
import com.qubecell.xmlparser.XMLParser;

/**
 * The QubecellActivity class is used to show the dialog permission for starting
 * the billing process.
 * 
 * @author Eninov
 * 
 */
public class QubecellActivity extends BaseActivity {
	private ELogger log = null;
	private String logTag = "QubecellActivity::";
	private Dialog permissionDialog = null;
	private TextView txtVw = null;
	private Button acceptButton = null;
	private Button cancelButton = null;
	protected ArrayList<OperatorDetails> operatorList = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log = new ELogger();
		log.setTag(logTag);
		log.info("onCreate() : Elogger initialized");
		if (xmlObj == null) {
			xmlObj = XMLParser.getInstance();
			xmlObj.init();
			log.info("onCreate() : XMLParser initialized");
		}

		if (nwObj == null) {
			nwObj = NetworkController.getInstance();
			nwObj.init();
			log.info("onCreate() : NetworkController initialized");
		}

		if (smsObj == null) {
			smsObj = QubecellSMSManager.getInstance();
			smsObj.init(getApplicationContext());
			log.info("onCreate() : SMSManager initialized");
		}

		setCurrentActivity(ApplicationActivities.QUBECELL_ACTIVITY);
		int result = setIntentData(getIntent());
		if (result == -1) {
			return;
		}
		username = getUsername();
		password = getPassword();
		if (initializeDialog() == -1) {
			log.error("Qubecell mendatory argument missing:: Please provide pay amount.");
			return;
		}
		detectMsisdnForOperator();
		QubecellResult.status = PaymentResult.PAYMENT_FALIURE;
	}
	
	/**
	 * This method is used to initialize the permission dialog views.
	 */
	private int initializeDialog() {
		if(permissionDialog == null)
		{
			permissionDialog = new Dialog(this);
			permissionDialog.setCanceledOnTouchOutside(false);
			permissionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
	

		View dialogView = CommonUtility
				.getDialogPermissionview(getApplicationContext());
		int width = CommonUtility.getScreenWidthDimen(appContext);
		int padding = (width*10)/100;
		int widthWithoutPadding = width - padding; 
		LayoutParams layoutParams = new LayoutParams(
				widthWithoutPadding,
				LayoutParams.WRAP_CONTENT);
		permissionDialog.setContentView(dialogView, layoutParams);

		txtVw = ((TextView) dialogView
				.findViewWithTag(WidgetsTagName.DIALOG_TOPHEADER_TEXTVIEW));
		String chargedAmount = getPayamount();
		if (chargedAmount == null) {
			return -1;
		}
		txtVw.setText("Pay Rs. " + chargedAmount);
		if (getTitleText() != null) {
			txtVw = ((TextView) dialogView
					.findViewWithTag(WidgetsTagName.DIALOG_TITTLE));
			txtVw.setText(getTitleText());
		}
		acceptButton = ((Button) dialogView
				.findViewWithTag("DialogLayout_AcceptButtonView"));
		cancelButton = ((Button) dialogView
				.findViewWithTag("DialogLayout_CancelButtonView"));
		acceptButton.setBackgroundColor(getBackGroundColor());

		permissionDialog.setOnKeyListener(new Dialog.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface arg0, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					finish();
					permissionDialog.dismiss();
				}
				return true;
			}
		});

		return 0;
	}

	/**
	 * This method is used to detect MSISDN for operator Vodafone, Airtel and
	 * Tata is GPRS connection is available.
	 */
	private void detectMsisdnForOperator() {
		if (isGPRSActive) {
			requestFlow = false;
			log.info("detectMsisdnForOperator() : GPRS is Active");
			List<NameValuePair> requestParam = new ArrayList<NameValuePair>();
			String msisdnUsername = getMsisdnUsername();
			String msisdnPassword = getMsisdnPassword();
			String msisdnKey = getMsisdnChargeKey();
			if ((msisdnUsername == null) || (msisdnPassword == null)
					|| (msisdnKey == null)) {
				log.error("MSISDN authentication credentials not found.");
			} else {
				requestParam.add(new BasicNameValuePair(
						ConstantStrings.USERNAME, msisdnUsername));
				requestParam.add(new BasicNameValuePair(
						ConstantStrings.PASSWORD, msisdnPassword));
				requestParam.add(new BasicNameValuePair(
						ConstantStrings.REQUESTID, requestId));
				String md5Str = getMD5(msisdnKey + requestId);
				requestParam.add(new BasicNameValuePair(ConstantStrings.KEY,
						md5Str));
				requestParam.add(new BasicNameValuePair(
						ConstantStrings.RETURNURL, ""));
				makeNetworkRequest(requestParam, ServerCommand.MSISDN_CMD);
			}
		} else if (isWiFiActive) {
			if (MerchantData.flow.equalsIgnoreCase(MerchantData.event_charge)) {
				requestFlow = false;
				startNextActivity();
			}
		} else if (noDataConnection) {
			if ((MerchantData.flow.equalsIgnoreCase(MerchantData.event_charge))
					&& (getProductId() != null)) {
				requestFlow = true;
				String msgBody = messageFormat
						+ getProductId(MobileOperators.NODATA);
				log.info("detectMsisdnForOperator() : message body is : ");
				log.info(msgBody);
				smsObj.sendMessage(appContext, msgBody, smsShortCode, false);
				startNextActivity();
			} else if (getProductId() == null) {
				log.info("Event charge service is not available on offline mode.");
				Intent intent = new Intent(QubecellActivity.this,
						ResultActivity.class);
				intent.putExtra(IntentConstant.MESSAGE,
						ConstantStrings.SERVICE_NOT_SUPPORTED_OFFLINE);
				intent.putExtra(IntentConstant.PAYMENT_RESULT,
						PaymentResult.FALIURE);
				startActivity(intent);
				finish();
			} else {
				log.info("Service is not available on offline mode.");
				Intent intent = new Intent(QubecellActivity.this,
						ResultActivity.class);
				intent.putExtra(IntentConstant.MESSAGE,
						ConstantStrings.SERVICE_NOT_SUPPORT_YOUR_NETWORK);
				intent.putExtra(IntentConstant.PAYMENT_RESULT,
						PaymentResult.FALIURE);
				startActivity(intent);
				finish();
			}
		} else {
			log.error("detectMsisdnForOperator() : invalid case handle.");
			finish();
		}
	}

	/**
	 * This method is used to make request to the network
	 * 
	 * @param requestParam
	 * @param requestParam
	 */
	private void makeNetworkRequest(List<NameValuePair> requestParam,
			final int requestType) {
		if (requestParam == null) {
			log.error("makeNetworkRequest() : Request Param not found.");
			return;
		}

		Object[] reqParam = new Object[2];
		reqParam[0] = requestParam;
		reqParam[1] = requestType;

		new AsyncClient<Object[], Object, NetworkResponse>() {
			@Override
			protected void onPreExecute() {
				showProgressDialogue("In Progress. . .");
			};

			@Override
			protected NetworkResponse doInBackground(Object[]... arg0) {
				Object[] requestParam = arg0[0];
				List<NameValuePair> requestP = (List<NameValuePair>) requestParam[0];
				int reqType = (Integer) requestParam[1];
				NetworkController nwObj = new NetworkController();
				NetworkResponse netresp = nwObj.httpPost(requestP, reqType);
				return netresp;
			}

			@Override
			protected void onPostExecute(NetworkResponse result) {
				dismissProgressDialogue();
				handleServerResponse(result, requestType);
			};
		}.execute(reqParam);
	}

	/**
	 * This method is used to handle the server response code.
	 * 
	 * @param result
	 * @param requestType
	 */
	protected void handleServerResponse(NetworkResponse result, int requestType) {
		if (result == null) {
			log.error("handleServerResponse() : Networkresponse response not found for "
					+ String.valueOf(requestType));
			return;
		}
		int respCode = result.netRespCode;
		log.info("handleServerResponse() : network responce code is : "
				+ respCode);

		if (result.netRespCode == NetworkResponseCode.NET_RESP_SUCCESS) {
			if (result.respStr == null || TextUtils.isEmpty(result.respStr)) {
				log.error("handleServerResponse() : Response String is null");
				Intent intent = new Intent(QubecellActivity.this,
						ResultActivity.class);
				intent.putExtra(IntentConstant.PAYMENT_RESULT,
						PaymentResult.FALIURE);
				intent.putExtra(IntentConstant.MESSAGE,
						ConstantStrings.TRANSACTION_CANNOT_PROCESS);
				startActivity(intent);
				finish();
				return;
			}
			String responseStr = result.respStr;
			XMLParser xmlObj = XMLParser.getInstance();
			log.info(" handleServerResponse() : Server response is : "
					+ responseStr);
			ResponseBaseBean msisdnRespBean = xmlObj.getResponseBean(
					responseStr, requestType);
			if (msisdnRespBean == null) {
				log.error("onPostExecute() :  Msisdn bean is found null");
				Intent intent = new Intent(QubecellActivity.this,
						ResultActivity.class);
				intent.putExtra(IntentConstant.PAYMENT_RESULT,
						PaymentResult.FALIURE);
				intent.putExtra(IntentConstant.MESSAGE,
						ConstantStrings.TRANSACTION_CANNOT_PROCESS);
				startActivity(intent);
				finish();
				return;
			}
			if (msisdnRespBean.getResponsecode() == MsisdnServerRespCode.SUCCESS) {
				switch (requestType) {

				case ServerCommand.FETCH_OPR_CMD: {
					OperatorsRespBean oprBean = (OperatorsRespBean) msisdnRespBean;
					operatorList = oprBean.getOperators();
				}
				break;

				case ServerCommand.MSISDN_CMD: {
					MsisdnRespBean msisdnBean = (MsisdnRespBean) msisdnRespBean;
					List<NameValuePair> requestParam = new ArrayList<NameValuePair>();
					requestId = String.valueOf(CommonUtility
							.getRandomNumberBetween());
					String chargeKey = getchargeKey();
					String md5Str = null;
					if (chargeKey != null) {
						md5Str = getMD5(chargeKey + requestId);
					} else {
						log.error("Authentication key not found..");
					}
					String msisdn = msisdnBean.getMsisdn();

					if (MerchantData.flow
							.equalsIgnoreCase(MerchantData.event_charge)) {
						String operatorProdId = null;
						if (!TextUtils.isEmpty(msisdnBean.getOperator())) {
							operator = msisdnBean.getOperator();
							if (operator != null)
								operatorProdId = getProductId(operator);
							else {
								log.error("MSISDN fails to get operator.");
								return;
							}
						} else {
							log.error("handleServerResponse() MSISDN fail to get operator information.");

						}
						if (operatorProdId == null) {
							log.error("handleServerResponse MSISDN operator's product id not found.");
							return;
						}
						setChargedAmount("0");
						requestParam.add(new BasicNameValuePair(
								ConstantStrings.USERNAME, username));
						requestParam.add(new BasicNameValuePair(
								ConstantStrings.PASSWORD, password));
						requestParam.add(new BasicNameValuePair(
								ConstantStrings.REQUESTID, requestId));
						requestParam.add(new BasicNameValuePair(
								ConstantStrings.OPERATION, "eventcharge"));
						requestParam.add(new BasicNameValuePair(
								ConstantStrings.PRODUCTID, operatorProdId));
						requestParam
						.add(new BasicNameValuePair(
								ConstantStrings.MESSAGE,
								"Event charge request"));
						requestParam.add(new BasicNameValuePair(
								ConstantStrings.KEY, md5Str));
						requestParam.add(new BasicNameValuePair(
								ConstantStrings.MSISDN, msisdn));
						requestParam.add(new BasicNameValuePair(
								ConstantStrings.RETURNURL, ""));
						requestParam.add(new BasicNameValuePair(
								ConstantStrings.LOG_PATH, ""));
						requestParam.add(new BasicNameValuePair(
								ConstantStrings.OPERATOR, operator));
						showEventChargeDialogPermission(requestParam,
								ServerCommand.EVENTCHARGE_CMD);
					} else {
						log.error("handleServerResponse() msisdn. Invalid case handle.");
					}
				}
				break;
				case ServerCommand.EVENTCHARGE_CMD: {
					EventChargeRespBean eveChargeRespBean = (EventChargeRespBean) msisdnRespBean;
					String message = getCommandErrorMessage(requestType,
							eveChargeRespBean);
					if (message == null) {
						message = ConstantStrings.THANKS_FOR_TRANSACTION;
					}
					if (eveChargeRespBean.getAmount() != null) {
						setChargedAmount(eveChargeRespBean.getAmount());
					} else {
						log.error("Event charge response not found payed amount.");
						// NEED TO REVIEW SHOULD RETURN;
					}
					if (!TextUtils.isEmpty(operator)
							&& operator.equalsIgnoreCase(MobileOperators.IDEA)) {
						Intent intent = new Intent(QubecellActivity.this,
								ValidateOTPActivity.class);
						String txnId = eveChargeRespBean.getTxnid();
						String msisdn = eveChargeRespBean.getMsisdn();
						intent.putExtra(IntentConstant.OPERATOR_INFO, operator);
						String chargeKey = getchargeKey();
						if (chargeKey != null) {
							intent.putExtra(IntentConstant.KEY, chargeKey);
						} else {
							log.error("Authentication key not found...");
							return;
						}
						intent.putExtra(IntentConstant.TRANSACTION_ID, txnId);
						intent.putExtra(IntentConstant.MSISDN, msisdn);
						startActivity(intent);
						finish();
					} else {
						Intent intent = new Intent(QubecellActivity.this,
								ResultActivity.class);
						intent.putExtra(IntentConstant.PAYMENT_RESULT,
								PaymentResult.SUCCESS);
						intent.putExtra(IntentConstant.MESSAGE, message);
						startActivity(intent);
						finish();
					}
				}
				break;

				case ServerCommand.CHECK_STATUS_CMD: {
					log.info("Product is already subscribed.");
				}
				break;
				default: {
					log.error("handleServerResponse() : Invalid case");
				}
				break;
				}
			} else {
				if (requestType == ServerCommand.CHECK_STATUS_CMD) {
					String message = getCommandErrorMessage(requestType,
							msisdnRespBean);
					Intent intent = new Intent(QubecellActivity.this,
							ResultActivity.class);
					intent.putExtra(IntentConstant.MESSAGE, message);
					intent.putExtra(IntentConstant.PAYMENT_RESULT,
							PaymentResult.FALIURE);
					startActivity(intent);
					finish();
				} else {
					log.error("handleServerResponse get fail MSISDN server responce");
					String message = getCommandErrorMessage(requestType,
							msisdnRespBean);
					if (MerchantData.flow
							.equalsIgnoreCase(MerchantData.event_charge)) {
						log.error(message);
						Intent intent = new Intent(QubecellActivity.this,
								SelectOperatorActivity.class);
						intent.putExtra(IntentConstant.OPERATOR_INFO, operator);
						String chargeKey = getchargeKey();
						if (chargeKey != null) {
							intent.putExtra(IntentConstant.KEY, chargeKey);
						} else {
							log.error("Authentication key not found...");
							return;
						}
						startActivity(intent);
						finish();
					} else {
						log.info("unsubscribe.");
					}
				}
			}
		} else {
			log.error("handleServerResponse Fail to get MSISDN server responce");
			if (MerchantData.flow.equalsIgnoreCase(MerchantData.event_charge)) {
				if (result.netRespCode == NetworkResponseCode.NET_REQ_TIMEOUT) {
					log.info("handleServerResponse() : Starting SelectOperator Screen because of Connection Time Out or Socket Time Out");
					startNextActivity();
				} else {
					Intent intent = new Intent(QubecellActivity.this,
							ResultActivity.class);
					intent.putExtra(IntentConstant.PAYMENT_RESULT,
							PaymentResult.FALIURE);
					intent.putExtra(IntentConstant.MESSAGE,
							ConstantStrings.TRANSACTION_CANNOT_PROCESS);
					startActivity(intent);
					finish();
				}
			} else {
				log.info("unsubscribe.");
			}
		}
	}

	/**
	 * This method is used to display the permission dialog to user for
	 * accepting and initiating the billing process or else cancel and quit.
	 * 
	 * @param eventchargeCmd
	 * @param apiUrl
	 * @param requestParam
	 */
	private void showEventChargeDialogPermission(
			final List<NameValuePair> requestParam, final int eventchargeCmd) {
		if (permissionDialog == null)
			return;
		boolean flag = permissionDialog.isShowing();
		log.info("showEventChargeDialogPermission() : " + flag);
		if (!flag) {
			permissionDialog.show();
		}

		// Adding Click listener for accept button
		acceptButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				permissionDialog.dismiss();
				makeNetworkRequest(requestParam, eventchargeCmd);
			}
		});

		// Adding Click listener for cancel button
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				permissionDialog.dismiss();
				finish();
			}
		});
	}

	/**
	 * This method is used to start the next based on the availability of GPRS,
	 * Wi-Fi, or No-data connection.
	 */
	protected void startNextActivity() {
		if (isGPRSActive) {
			Intent intent = new Intent(QubecellActivity.this,
					SelectOperatorActivity.class);
			intent.putExtra(IntentConstant.OPERATOR_INFO, operator);
			String chargeKey = getchargeKey();
			if (chargeKey != null) {
				intent.putExtra(IntentConstant.KEY, chargeKey);
			} else {
				log.error("Authentication key not found...");
			}

			startActivity(intent);
			finish();
		} else if (isWiFiActive) {
			Intent intent = new Intent(QubecellActivity.this,
					SelectOperatorActivity.class);
			intent.putExtra(IntentConstant.OPERATOR_INFO, operator);
			String chargeKey = getchargeKey();
			if (chargeKey != null) {
				intent.putExtra(IntentConstant.KEY, chargeKey);
			} else {
				log.error("Authentication key not found...");
			}

			startActivity(intent);
			finish();
		} else if (noDataConnection) {
			Intent intent = new Intent(QubecellActivity.this,
					ValidateOTPActivity.class);
			intent.putExtra(IntentConstant.OPERATOR_INFO, operator);
			String chargeKey = getchargeKey();
			if (chargeKey != null) {
				intent.putExtra(IntentConstant.KEY, chargeKey);
			} else {
				log.error("Authentication key not found...");
				return;
			}
			startActivity(intent);
			finish();
		} else {
			Intent intent = new Intent(QubecellActivity.this,
					SelectOperatorActivity.class);
			intent.putExtra(IntentConstant.OPERATOR_INFO, operator);
			String chargeKey = getchargeKey();
			if (chargeKey != null) {
				intent.putExtra(IntentConstant.KEY, chargeKey);
			} else {
				log.error("Authentication key not found.....");
				return;
			}
			startActivity(intent);
			finish();
		}
	}
}
