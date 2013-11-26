/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.smsmgr;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import com.qubecell.elogger.ELogger;

/**
 * The QubecellSMSManager class is used to provide the functionality for sending and receiving messages
 * to and from the android device.
 * @author Eninov
 *
 */
public class QubecellSMSManager {
	
	private ELogger log = null;
	private String logTag = "QubecellSMSManager";
	private Context appContext = null;
	private static QubecellSMSManager smsObj = null;
	private static final int MAX_SMS_MESSAGE_LENGTH = 160;
    private static final int SMS_PORT = 5555;
    private static final String SMS_DELIVERED = "SMS_DELIVERED";
    private static final String SMS_SENT = "SMS_SENT";

	/**
	 * This method is used to initialize the QubecellSMSManager class.
	 * @param context 
	 * @return
	 */
	public boolean init(Context context)
	{
		if(context == null)
			return false;
		initLog();
		smsObj = getInstance();
		appContext = context;
		appContext.registerReceiver(sendreceiver, new IntentFilter(SMS_SENT));
		appContext.registerReceiver(deliveredreceiver, new IntentFilter(SMS_DELIVERED));
		return true;
	}

	/**
	 * This method is used to unregister the receivers.
	 * @param context
	 * @return
	 */
	public boolean unRegisterReceivers(Context context)
	{
		if(context == null)
			return false;
		context.unregisterReceiver(sendreceiver);
		context.unregisterReceiver(deliveredreceiver);
		return true;
	}
	
	/**
	 * This method is used to get the QubecellSMSManager instance.
	 * @return
	 */
	public static QubecellSMSManager getInstance() //pass
	{
		if(smsObj == null)
		{
			smsObj = new QubecellSMSManager();
		}
		return smsObj;
	}

	/**
	 * This method is used to initialize the ELogger object.
	 */
	private boolean initLog() 
	{
		if(log == null)
			log = new ELogger();
		log.setTag(logTag);
		return true;
	}

	/**
	 * This method is used to send message to the given address.
	 * @param appContext
	 * @param message
	 * @param address
	 * @return
	 */
	public boolean sendMessage(Context appContext, String message, String address, boolean isBinary)
	{
		SmsManager manager = SmsManager.getDefault();
        PendingIntent piSend = PendingIntent.getBroadcast(appContext, 0, new Intent(SMS_SENT), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(appContext, 0, new Intent(SMS_DELIVERED), 0);
        if((message == null) || (address == null))
        {
        	return false;
        }
        log.info("detectMsisdnForOperator() : Address : "+address +", Message : "+message);
        if(isBinary)
        {
                byte[] data = new byte[message.length()];
               
                for(int index=0; index<message.length() && index < MAX_SMS_MESSAGE_LENGTH; ++index)
                {
                        data[index] = (byte)message.charAt(index);
                }
                log.info("detectMsisdnForOperator() : Sending binary message"); 
                manager.sendDataMessage(address, null, (short) SMS_PORT, data,piSend, piDelivered);
        }
        else
        {
        	int length = message.length();
        	try
        	{   
        		if(length > MAX_SMS_MESSAGE_LENGTH)
        		{
        			ArrayList<String> messagelist = manager.divideMessage(message);
        			log.info("detectMsisdnForOperator() : Sending text message :" + message+"  Address is : "+ address);
        			manager.sendMultipartTextMessage(address, null, messagelist, null, null);
        		}
        		else
        		{
        			log.info("detectMsisdnForOperator() : Sending text message :" + message+"  Address is : "+ address);
        			manager.sendTextMessage(address, null, message, piSend, piDelivered);
        		}
        	}
        	catch(IllegalArgumentException ex)
        	{
        		log.error("sendMessage() : IllegalArgumentException : "+ ex);
        	}
        }

		return true;
	}
	
	/**
	 * This receiver is used to handle the send information status of the message.
	 */
	private BroadcastReceiver sendreceiver = new BroadcastReceiver()
    {
            @Override
            public void onReceive(Context context, Intent intent)
            {
            	log.debug("Message received.");
            }
    };

    /**
	 * This receiver is used to handle the delivered information status of the message.
	 */
	private BroadcastReceiver deliveredreceiver = new BroadcastReceiver()
    {
            @Override
            public void onReceive(Context context, Intent intent)
            {
            	log.debug("Message delivered.");
            }
    };
}
