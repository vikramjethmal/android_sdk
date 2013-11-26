/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.ui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qubecell.constants.ApplicationActivities;
import com.qubecell.constants.IntentConstant;
import com.qubecell.constants.PaymentResult;
import com.qubecell.constants.WidgetsTagName;
import com.qubecell.elogger.ELogger;
import com.qubecell.utility.CommonUtility;

/**
 * The ResultActivity class is used to display the success or failure message received from server. 
 * @author Eninov
 *
 */
public class ResultActivity extends BaseActivity 
{
	private Button backButton = null;
	private ELogger log = null;
	private String message = null;
	private String logTag = "ResultDisplayActivity::";
	private View resultLayoutView = null;
	private String result = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		resultLayoutView = CommonUtility.getResultLayoutView(appContext);
		this.setContentView(resultLayoutView);	
		initLogger();
		log.debug("Inside ResultActivity");
		getIntentData(getIntent());
		initializeWidget();
		handleItemClickListener();
		((TextView)resultLayoutView.findViewWithTag(WidgetsTagName.RESULT_RESULT_TEXTVIEW)).setText(message);
		setCurrentActivity(ApplicationActivities.RESULT_ACTIVITY);
	}

	private void handleItemClickListener() 
	{
		// Back Button Click listener
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) 
			{
				if(result == null)
				{
					setQubecell(PaymentResult.PAYMENT_FALIURE);
				}
				else
				{
					if(result.equalsIgnoreCase(PaymentResult.SUCCESS))
					{
						setQubecell(PaymentResult.PAYMENT_SUCCESS);
					}
					else
					{
						setQubecell(PaymentResult.PAYMENT_FALIURE);
					}
				}
				setCurrentActivity(ApplicationActivities.CLOSE_QUBECELL);
				finish();
			}
		});
	}

	/**
	 * This method is used to initialize the widgets
	 */
	private void initializeWidget() 
	{

		backButton = ((Button)resultLayoutView.findViewWithTag(WidgetsTagName.RESULT_BUTTON_VIEW));
		backButton.setBackgroundColor(getBackGroundColor());
		if(getBillingPartner() != null)
		{
			TextView poweredBy = ((TextView)resultLayoutView.findViewWithTag(WidgetsTagName.RESULT_BOTTOM_TEXTVIEW));
			poweredBy.setText(getBillingPartner());
		}
		if(getLogoImage() != null)
		{
			ImageView logoImageView = ((ImageView)resultLayoutView.findViewWithTag(WidgetsTagName.RESULT_TOPHEADER_IMAGEVIEW));
			Drawable logoDrawable = new BitmapDrawable(getLogoImage());
			logoImageView.setBackgroundDrawable(logoDrawable);
		}
		LinearLayout mainFrame = ((LinearLayout)resultLayoutView.findViewWithTag(WidgetsTagName.RESULT_MAIN_FRAME));
		mainFrame.setBackgroundColor(getThemeColor());
	}

	/**
	 * This method is used to get intent data and set it in class variable.
	 */
	private void getIntentData(Intent intent) 
	{
		log.debug("Inside getIntentData.");
		if(intent == null)
		{
			log.error("getIntentData() : Intent is found null");
			return;
		}
		message = intent.getStringExtra(IntentConstant.MESSAGE); 
		result = intent.getStringExtra(IntentConstant.PAYMENT_RESULT);
		String progressBarClose = intent.getStringExtra(IntentConstant.CLOSE_PROGRESSBAR);
		if(progressBarClose != null &&  progressBarClose.equalsIgnoreCase("true"))
		{
			log.debug("Close progressbar");
			dismissProgressDialogOnEvent();
		}
		else
		{
			log.debug("Dont close progressbar");
		}
	}

	/**
	 * This method is used to initialize the Elogger. 
	 */
	private void initLogger() 
	{
		if(log == null)
			log = new ELogger();
		log.setTag(logTag);
	}
}
