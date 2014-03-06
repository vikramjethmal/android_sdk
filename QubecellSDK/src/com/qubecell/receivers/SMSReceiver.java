/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.qubecell.constants.IntentConstant;
import com.qubecell.constants.MessageResponseCode;
import com.qubecell.constants.MsisdnServerRespCode;
import com.qubecell.constants.PaymentResult;
import com.qubecell.elogger.ELogger;
import com.qubecell.ui.BaseActivity;
import com.qubecell.ui.ResultActivity;


/**
 * The SMSReceiver class is used to receive messages came to a specified port number.
 * @author Eninov
 *
 */
public class SMSReceiver extends BroadcastReceiver
{
	private ELogger log = null;
	private String TAG = "SMSReceiver";

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if(log == null)
		{
			log = new ELogger();
			log.setTag(TAG);
		}
		log.debug("SMSReceiver : Inside onReceive() method");
		Bundle bundle = intent.getExtras();
		log.info("SMSReceiver : onreceive() : "+ bundle.toString());

		SmsMessage[] msgs = null;
		if(bundle != null)
		{
			String info = "Text SMS from ";
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			String msgBody = null;
			for (int i = 0; i < msgs.length; i++)
			{
				msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

				info += msgs[i].getOriginatingAddress();                    
				info += "\n*****TEXT MESSAGE*****\n";
				info += msgs[i].getMessageBody().toString();
				msgBody+= msgs[i].getMessageBody().toString();
			}
			log.info("SMSReceiver : Receive message on port : "+ info);			
			
			if(BaseActivity.getReceiveSmsonPort() == 0)
			{
				BaseActivity.setReceiveSmsonPort(1);
				Intent resultIntent = new Intent(context, ResultActivity.class);
				resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				resultIntent.putExtra(IntentConstant.CLOSE_PROGRESSBAR, "true");
				if(msgBody.contains("OTP"))
				{			
					resultIntent.putExtra(IntentConstant.MESSAGE, "Thank you for your transaction.!");
					resultIntent.putExtra(IntentConstant.PAYMENT_RESULT, PaymentResult.SUCCESS);
				}
				else 
				{
					resultIntent.putExtra(IntentConstant.MESSAGE, MessageResponseCode.getResponseString(context, msgBody));
					resultIntent.putExtra(IntentConstant.PAYMENT_RESULT, PaymentResult.FALIURE);
				}
				context.startActivity(resultIntent);
			}
			else
			{
				log.info("Response timeout. Result will not displayed.");
			}
		}     
	}

	public int getMsgResponceCode(String msgBody)
	{
		return 101;
	}
}
