/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.utility;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qubecell.constants.ConstantStrings;
import com.qubecell.constants.ServerCommand;
import com.qubecell.constants.WidgetsTagName;
import com.qubecell.ui.BaseActivity;

/**
 * The CommonUtility class has all the common functions which is required by all the modules of the application.
 * @author Eninov
 *
 */
public class CommonUtility {

	private static Drawable appLogo = null;
	private static Drawable dialogIcon = null;

	/**
	 * This method is used to check whether GPRS is connected or not.
	 * @param appContext
	 * @return
	 */
	public static boolean isGPRSConnected(Context appContext)
	{
		boolean isConnected = false;
		if(isMobileDataEnables(appContext))
		{
			ConnectivityManager connMgr = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if (mobile != null && mobile.isAvailable() && mobile.isConnected()) 
			{

				isConnected = true;
			} 
			return isConnected;
		}
		else
		{
			return isConnected;
		}
	}

	/**
	 * This method is used to get the current orientation of the device.
	 * @param context
	 * @return
	 */
	public static String getRotation(Context context)
	{
		final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
		switch (rotation) {
		case Surface.ROTATION_0:
			return "portrait";
		case Surface.ROTATION_90:
			return "landscape";
		case Surface.ROTATION_180:
			return "portrait";
			//return "reverse portrait";
		default:
		{
			return "landscape";
			//return "reverse landscape";
		}
		}
	}

	/**
	 * This method is used to check whether mobile data connection is enabled or disabled.
	 * @param appContext
	 * @return
	 */
	public static boolean isMobileDataEnables(Context appContext)
	{
		boolean mobileDataEnabled = false; // Assume disabled
		ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			Class cmClass = Class.forName(cm.getClass().getName());
			Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
			method.setAccessible(true); // Make the method call able
			// get the setting for "mobile data"
			mobileDataEnabled = (Boolean)method.invoke(cm);
		}
		catch (Exception e) 
		{
			System.err.println("isMobileDataEnables() : "+ e);
		}
		return mobileDataEnabled;
	}

	/**
	 * This method is used to check whether Wi-Fi is connected or not.
	 * @param appContext
	 * @return
	 */
	public static boolean isWiFiConnected(Context appContext)
	{
		boolean isConnected = false;
		ConnectivityManager connMgr = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (wifi != null && wifi.isAvailable()) 
		{
			isConnected = true;
		}
		return isConnected;
	}

	/**
	 * This method is used to generate random number between given range.
	 */
	public static long getRandomNumberBetween(int apiCommand) 
	{
		/*Calendar rightNow = Calendar.getInstance();
		long offset = rightNow.get(Calendar.ZONE_OFFSET) +
				rightNow.get(Calendar.DST_OFFSET);
		long sinceMidnight = (rightNow.getTimeInMillis() + offset) %
				(24 * 60 * 60 * 1000);*/
		long sinceMidnight = new Date().getTime();
		if(apiCommand != ServerCommand.GETLASTSTATUS_CDM)
			BaseActivity.lastRequestId = sinceMidnight;
		return sinceMidnight ;
	}

	/**
	 * This method is used to create Result Layout View Dynamically.
	 * @param appContext
	 * @return
	 */
	public static View getResultLayoutView(Context appContext) 
	{
		int height = getScreenHeightDimen(appContext);
		int width = getScreenWidthDimen(appContext);
		if (appLogo == null)
		{
			appLogo = ImageBase64.getLogoDrawable(appContext);
		}

		LinearLayout parentView = new LinearLayout(appContext);
		parentView.setTag(WidgetsTagName.RESULT_LAYOUT);
		parentView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		parentView.setOrientation(LinearLayout.VERTICAL);
		parentView.setBackgroundColor(Color.DKGRAY);

		LinearLayout firstSectionView = new LinearLayout(appContext);
		firstSectionView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,(int)(0.09*height)));
		firstSectionView.setOrientation(LinearLayout.VERTICAL);

		ImageView iv = new ImageView(appContext);
		iv.setTag(WidgetsTagName.RESULT_TOPHEADER_IMAGEVIEW);
		iv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		iv.setBackgroundColor(Color.RED);
		iv.setBackgroundDrawable(appLogo);
		firstSectionView.addView(iv);

		LinearLayout secondSectionView = new LinearLayout(appContext);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int)(0.08*height));
		lp.setMargins((int)(0.3*width), 0, (int)(0.3*width), 0);
		secondSectionView.setLayoutParams(lp);
		secondSectionView.setOrientation(LinearLayout.VERTICAL);

		LinearLayout thirdSectionView = new LinearLayout(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int)(0.70*height));
		lp.setMargins((int)(0.03*width), 0, (int)(0.03*width), 0);
		thirdSectionView.setLayoutParams(lp);
		thirdSectionView.setTag(WidgetsTagName.RESULT_MAIN_FRAME);
		thirdSectionView.setBackgroundColor(Color.LTGRAY);
		thirdSectionView.setOrientation(LinearLayout.VERTICAL);

		TextView resultTV = new TextView(appContext);
		resultTV.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		resultTV.setTag(WidgetsTagName.RESULT_RESULT_TEXTVIEW);
		resultTV.setText(ConstantStrings.SUCCESSFULL);
		resultTV.setTextSize(14);
		resultTV.setTextColor(Color.BLACK);
		resultTV.setGravity(Gravity.CENTER_HORIZONTAL);
		resultTV.setPadding(0, (int)(0.02*height), 0, 0);
		resultTV.setTypeface(Typeface.DEFAULT_BOLD);
		thirdSectionView.addView(resultTV);

		LinearLayout buttonViewLL = new LinearLayout(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		buttonViewLL.setLayoutParams(lp);
		buttonViewLL.setGravity(Gravity.CENTER);

		Button button = new Button(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		button.setLayoutParams(lp);
		lp.setMargins((int)(0.2*width), (int)(0.03*height), (int)(0.2*width), (int)(0.03*height));
		button.setText(ConstantStrings.OK);
		button.setTextColor(Color.WHITE);
		button.setTypeface(Typeface.DEFAULT_BOLD);
		button.setGravity(Gravity.CENTER_HORIZONTAL);
		button.setTag(WidgetsTagName.RESULT_BUTTON_VIEW);
		button.setBackgroundColor(Color.BLACK);

		buttonViewLL.addView(button);
		thirdSectionView.addView(buttonViewLL);

		LinearLayout fourthSectionView = new LinearLayout(appContext);
		fourthSectionView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,(int)(0.1*height)));
		fourthSectionView.setOrientation(LinearLayout.VERTICAL);

		TextView bottomTV = new TextView(appContext);
		bottomTV.setTag(WidgetsTagName.RESULT_BOTTOM_TEXTVIEW);
		bottomTV.setText(ConstantStrings.POWEREDBY_QUBECELL);
		bottomTV.setTextSize(15);
		bottomTV.setTextColor(Color.WHITE);
		bottomTV.setPadding(0, 0, (int)(0.03*width), (int)(0.01*height));
		bottomTV.setGravity(Gravity.RIGHT);
		fourthSectionView.addView(bottomTV);

		parentView.addView(firstSectionView);
		parentView.addView(secondSectionView);
		parentView.addView(thirdSectionView);
		parentView.addView(fourthSectionView);

		return parentView;
	}


	/**
	 * This method is used to create Validate OTP layout View dynamically at run time.
	 * @param appContext
	 * @return
	 */
	public static View getValidateOTPlayoutView(Context appContext)
	{

		int height = getScreenHeightDimen(appContext);
		int width = getScreenWidthDimen(appContext);

		if (appLogo == null)
		{
			appLogo = ImageBase64.getLogoDrawable(appContext);
		}

		LinearLayout parentView = new LinearLayout(appContext);
		parentView.setTag(WidgetsTagName.VALIDATE_LAYOUT);
		parentView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		parentView.setOrientation(LinearLayout.VERTICAL);
		parentView.setBackgroundColor(Color.DKGRAY);

		LinearLayout firstSectionView = new LinearLayout(appContext);
		firstSectionView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,(int)(0.09*height)));
		firstSectionView.setOrientation(LinearLayout.VERTICAL);

		ImageView iv = new ImageView(appContext);
		iv.setTag(WidgetsTagName.VALIDATE_TOPHEADER_IMAGEVIEW);
		iv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		iv.setBackgroundColor(Color.RED);
		iv.setBackgroundDrawable(appLogo);
		firstSectionView.addView(iv);

		LinearLayout secondSectionView = new LinearLayout(appContext);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int)(0.08*height));
		lp.setMargins(20, 0, 20, 0);
		secondSectionView.setLayoutParams(lp);
		secondSectionView.setOrientation(LinearLayout.VERTICAL);

		LinearLayout thirdSectionView = new LinearLayout(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int)(0.70*height));
		lp.setMargins((int)(0.03*width), 0, (int)(0.03*width), 0);
		thirdSectionView.setLayoutParams(lp);
		thirdSectionView.setTag(WidgetsTagName.VALIDATE_MAIN_FRAME);
		thirdSectionView.setBackgroundColor(Color.LTGRAY);
		thirdSectionView.setOrientation(LinearLayout.VERTICAL);

		TextView resultTV = new TextView(appContext);
		resultTV.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		resultTV.setTag(WidgetsTagName.VALIDATE_TEXTVIEW);
		resultTV.setText(ConstantStrings.PLEASE_ENTER_OTP);
		resultTV.setTextColor(Color.BLACK);
		resultTV.setGravity(Gravity.LEFT);
		resultTV.setPadding((int)(0.03*width), 2, 0, 0);
		thirdSectionView.addView(resultTV);


		EditText editText = new EditText(appContext);
		editText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		editText.setTag(WidgetsTagName.VALIDATE_EDITTEXT);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		editText.setSingleLine(true);
		editText.setHint(ConstantStrings.ENTER_SMS_CODE);
		editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		editText.setTextSize(15);
		editText.setTextColor(Color.BLACK);
		editText.setPadding((int)(0.03*width), 0, 0, 0);
		editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		thirdSectionView.addView(editText);


		TextView operatorTV = new TextView(appContext);
		operatorTV.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		operatorTV.setTag(WidgetsTagName.VALIDATE_TEXTVIEW);
		operatorTV.setText(ConstantStrings.ONETIME_PASSWORD_SENDTO_MOBILE);
		operatorTV.setTextColor(Color.BLACK);
		operatorTV.setGravity(Gravity.LEFT);
		operatorTV.setPadding((int)(0.03*width), 0, 0, 0);
		thirdSectionView.addView(operatorTV);

		LinearLayout scrollViewLL = new LinearLayout(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		scrollViewLL.setLayoutParams(lp);
		scrollViewLL.setGravity(Gravity.BOTTOM);
		scrollViewLL.setOrientation(LinearLayout.VERTICAL);

		TextView resendOTPTV = new TextView(appContext);
		resendOTPTV.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		resendOTPTV.setTag(WidgetsTagName.VALIDATE_SENDPTP_TEXTVIEW);
		SpannableString content = new SpannableString(ConstantStrings.RESEND_OTP);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		resendOTPTV.setText(content);
		resendOTPTV.setTextColor(Color.BLACK);
		resendOTPTV.setTypeface(Typeface.DEFAULT_BOLD);
		resendOTPTV.setGravity(Gravity.RIGHT);
		resendOTPTV.setPadding(0, 0,(int)(0.03*width), (int)(0.02*height));
		scrollViewLL.addView(resendOTPTV);

		LinearLayout buttonViewLL = new LinearLayout(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 0, 0, (int)(0.05*height));
		buttonViewLL.setLayoutParams(lp);
		buttonViewLL.setGravity(Gravity.CENTER);
		buttonViewLL.setOrientation(LinearLayout.HORIZONTAL);
		buttonViewLL.setWeightSum(1f);

		Button buttonNext = new Button(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int)(0.09*height),0.5f);
		lp.setMargins((int)(0.02*width), 0, (int)(0.02*width), (int)(0.01*height));
		buttonNext.setPadding((int)(0.04*width), (int)(0.01*height),(int)(0.04*width), (int)(0.01*height));
		buttonNext.setLayoutParams(lp);
		buttonNext.setText(ConstantStrings.NEXT);
		buttonNext.setTextColor(Color.WHITE);
		buttonNext.setTypeface(Typeface.DEFAULT_BOLD);
		buttonNext.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		buttonNext.setTag(WidgetsTagName.VALIDATE_NEXT_BUTTONVIEW);
		buttonNext.setBackgroundColor(Color.YELLOW);

		buttonViewLL.addView(buttonNext);

		Button buttonBack = new Button(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int)(0.09*height),0.5f);
		lp.setMargins((int)(0.02*width), 0, (int)(0.02*width), (int)(0.01*height));
		buttonBack.setPadding((int)(0.04*width), (int)(0.01*height),(int)(0.04*width), (int)(0.01*height));
		buttonBack.setLayoutParams(lp);
		buttonBack.setText(ConstantStrings.BACK);
		buttonBack.setTextColor(Color.WHITE);
		buttonBack.setTypeface(Typeface.DEFAULT_BOLD);
		buttonBack.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		buttonBack.setTag(WidgetsTagName.VALIDATE_BACK_BUTTONVIEW);
		buttonBack.setBackgroundColor(Color.BLACK);
		buttonViewLL.addView(buttonBack);

		scrollViewLL.addView(buttonViewLL);
		thirdSectionView.addView(scrollViewLL);

		LinearLayout fourthSectionView = new LinearLayout(appContext);
		fourthSectionView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,(int)(0.1*height)));
		fourthSectionView.setOrientation(LinearLayout.VERTICAL);

		TextView bottomTV = new TextView(appContext);
		bottomTV.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		bottomTV.setTag(WidgetsTagName.VALIDATE_BOTTOM_TEXTVIEW);
		bottomTV.setText(ConstantStrings.POWEREDBY_QUBECELL);
		bottomTV.setTextSize(15);
		bottomTV.setTextColor(Color.WHITE);
		bottomTV.setPadding(0, 0, (int)(0.03*width), (int)(0.01*height));
		bottomTV.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		fourthSectionView.addView(bottomTV);

		parentView.addView(firstSectionView);
		parentView.addView(secondSectionView);
		parentView.addView(thirdSectionView);
		parentView.addView(fourthSectionView);
		return parentView;
	}

	/**
	 * This method is used to create Operator layout View dynamically at run time.
	 * @param appContext
	 * @return
	 */
	public static View getOperatorSelectionLayoutView(Context appContext)
	{
		int height = getScreenHeightDimen(appContext);
		int width = getScreenWidthDimen(appContext);
		if (appLogo == null)
		{
			appLogo = ImageBase64.getLogoDrawable(appContext);
		}

		LinearLayout parentView = new LinearLayout(appContext);
		parentView.setTag(WidgetsTagName.OPERATOR_LAYOUT);
		parentView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		parentView.setOrientation(LinearLayout.VERTICAL);
		parentView.setBackgroundColor(Color.DKGRAY);

		LinearLayout firstSectionView = new LinearLayout(appContext);
		firstSectionView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,(int)(0.09*height)));
		firstSectionView.setOrientation(LinearLayout.VERTICAL);

		ImageView iv = new ImageView(appContext);
		iv.setTag(WidgetsTagName.OPERATOR_TOPHEADER_IMAGEVIEW);
		iv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		iv.setBackgroundColor(Color.RED);
		iv.setBackgroundDrawable(appLogo);
		firstSectionView.addView(iv);

		LinearLayout secondSectionView = new LinearLayout(appContext);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int)(0.08*height));
		lp.setMargins((int)(0.05*width), 0, (int)(0.05*width), 0);
		secondSectionView.setLayoutParams(lp);
		secondSectionView.setOrientation(LinearLayout.VERTICAL);


		LinearLayout thirdSectionView = new LinearLayout(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int)(0.70*height));
		lp.setMargins((int)(0.03*width), 0, (int)(0.03*width), 0);
		thirdSectionView.setLayoutParams(lp);
		thirdSectionView.setTag(WidgetsTagName.OPERATOR_MAIN_FRAME);
		thirdSectionView.setBackgroundColor(Color.LTGRAY);
		thirdSectionView.setOrientation(LinearLayout.VERTICAL);


		TextView resultTV = new TextView(appContext);
		resultTV.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		resultTV.setTag(WidgetsTagName.OPERATOR_TEXTVIEW);
		resultTV.setText(ConstantStrings.ENTER_MOBILE_NUMBER);
		resultTV.setTextColor(Color.BLACK);
		resultTV.setGravity(Gravity.LEFT);
		resultTV.setPadding((int)(0.03*width), (int)(0.02*height), 0, 0);
		thirdSectionView.addView(resultTV);

		EditText editText = new EditText(appContext);
		editText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		editText.setTag(WidgetsTagName.OPERATOR_EDITTEXT);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		editText.setSingleLine(true);
		editText.setHint("91");
		editText.setTextColor(Color.BLACK);
		editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		editText.setTextSize(15);
		resultTV.setPadding((int)(0.04*width), (int)(0.03*height), (int)(0.04*width), 0);
		editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		thirdSectionView.addView(editText);


		TextView operatorTV = new TextView(appContext);
		operatorTV.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		operatorTV.setTag(WidgetsTagName.OPERATOR_TEXTVIEW_MOBILE);
		operatorTV.setText(ConstantStrings.SELECT_OPERATOR);
		operatorTV.setTextColor(Color.BLACK);
		operatorTV.setGravity(Gravity.LEFT);
		operatorTV.setPadding((int)(0.03*width), (int)(0.04*height), 0, 0);
		thirdSectionView.addView(operatorTV);

		ScrollView scrollView = new ScrollView(appContext);
		scrollView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		LinearLayout scrollViewLL = new LinearLayout(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		scrollViewLL.setLayoutParams(lp);
		scrollViewLL.setGravity(Gravity.LEFT);
		scrollViewLL.setOrientation(LinearLayout.VERTICAL);

		LinearLayout operatorsListViewLL = new LinearLayout(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		lp.setMargins((int)(0.03*width), (int)(0.05*height), 0, 0);
		operatorsListViewLL.setTag(WidgetsTagName.OPERATOR_OPERATOTLIST);
		operatorsListViewLL.setLayoutParams(lp);
		operatorsListViewLL.setGravity(Gravity.LEFT);
		operatorsListViewLL.setOrientation(LinearLayout.VERTICAL);
		scrollViewLL.addView(operatorsListViewLL);

		LinearLayout buttonViewLL = new LinearLayout(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		lp.setMargins(0, 0, 0, (int)(0.05*height));
		buttonViewLL.setLayoutParams(lp);
		buttonViewLL.setGravity(Gravity.BOTTOM);
		buttonViewLL.setOrientation(LinearLayout.HORIZONTAL);
		buttonViewLL.setWeightSum(1f);

		Button buttonNext = new Button(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int)(0.09*height),0.5f);
		lp.setMargins((int)(0.02*width), 0, (int)(0.02*width), (int)(0.01*height));
		buttonNext.setPadding((int)(0.04*width), (int)(0.01*height),(int)(0.04*width), (int)(0.01*height));
		buttonNext.setLayoutParams(lp);
		buttonNext.setText(ConstantStrings.NEXT);
		buttonNext.setTextColor(Color.WHITE);
		buttonNext.setTypeface(Typeface.DEFAULT_BOLD);
		buttonNext.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		buttonNext.setTag(WidgetsTagName.OPERATOR_NEXT_BUTTONVIEW);
		buttonNext.setBackgroundColor(Color.YELLOW);

		buttonViewLL.addView(buttonNext);

		Button buttonBack = new Button(appContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int)(0.09*height),0.5f);
		lp.setMargins((int)(0.02*width), 0, (int)(0.02*width), (int)(0.01*height));
		buttonBack.setPadding((int)(0.04*width), (int)(0.01*height),(int)(0.04*width), (int)(0.01*height));
		buttonBack.setLayoutParams(lp);
		buttonBack.setText(ConstantStrings.BACK);

		buttonBack.setTextColor(Color.WHITE);
		buttonBack.setTypeface(Typeface.DEFAULT_BOLD);
		buttonBack.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		buttonBack.setTag(WidgetsTagName.OPERATOR_BACK_BUTTONVIEW);
		buttonBack.setBackgroundColor(Color.BLACK);

		buttonViewLL.addView(buttonBack);
		scrollViewLL.addView(buttonViewLL);
		scrollView.addView(scrollViewLL);
		thirdSectionView.addView(scrollView);

		LinearLayout fourthSectionView = new LinearLayout(appContext);
		fourthSectionView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,(int)(0.1*height)));
		fourthSectionView.setOrientation(LinearLayout.VERTICAL);

		TextView bottomTV = new TextView(appContext);
		bottomTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		bottomTV.setTag(WidgetsTagName.OPERATOR_BOTTOM_TEXTVIEW);
		bottomTV.setText(ConstantStrings.POWEREDBY_QUBECELL);
		bottomTV.setTextSize(15);
		bottomTV.setTextColor(Color.WHITE);
		bottomTV.setPadding(0, 0, (int)(0.03*width), 0);
		bottomTV.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		fourthSectionView.addView(bottomTV);

		parentView.addView(firstSectionView);
		parentView.addView(secondSectionView);
		parentView.addView(thirdSectionView);
		parentView.addView(fourthSectionView);
		return parentView;
	}

	/**
	 * This method is used to get the height of the screen.
	 * 
	 * @param appContext
	 * @return
	 */
	public static int getScreenHeightDimen(Context appContext) {
		DisplayMetrics display = appContext.getResources().getDisplayMetrics();
		int height = display.heightPixels;
		return height;
	}

	/**
	 * This method is used to get the width of the screen.
	 * 
	 * @param appContext
	 * @return
	 */
	public static int getScreenWidthDimen(Context appContext) {
		DisplayMetrics display = appContext.getResources().getDisplayMetrics();
		int width = display.widthPixels;
		return width;
	}

	/**
	 * This method is used to create permission dialog View.
	 * @param appContext
	 * @return
	 */
	public static View getDialogPermissionview(Context appContext)
	{
		int height = getScreenHeightDimen(appContext);
		int width = getScreenWidthDimen(appContext);
		String orientation = getRotation(appContext);
		if (dialogIcon == null)
		{
			dialogIcon = ImageBase64.getDialogDrawable(appContext);
		}

		LinearLayout parentView = new LinearLayout(appContext);
		parentView.setTag(WidgetsTagName.DIALOG_LAYOUT);
		parentView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		parentView.setOrientation(LinearLayout.VERTICAL);
		parentView.setBackgroundColor(Color.DKGRAY);

		LinearLayout tittleSectionInnerView = new LinearLayout(appContext);
		tittleSectionInnerView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		tittleSectionInnerView.setOrientation(LinearLayout.HORIZONTAL);
		tittleSectionInnerView.setBackgroundColor(Color.BLACK);

		TextView tittleTV = new TextView(appContext);
		tittleTV.setTag(WidgetsTagName.DIALOG_TITTLE);
		tittleTV.setText(ConstantStrings.QUBECELL);
		tittleTV.setTextColor(Color.WHITE);
		tittleTV.setTextSize(20);
		tittleTV.setBackgroundColor(Color.BLACK);
		tittleTV.setPadding((int)(0.01*width), (int)(0.02 * height), 0, (int)(0.02*height));
		tittleSectionInnerView.addView(tittleTV);

		LinearLayout firstSectionView = new LinearLayout(appContext);
		LinearLayout.LayoutParams layoutparam1 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		firstSectionView.setOrientation(LinearLayout.HORIZONTAL);
		firstSectionView.setWeightSum(1f);

		layoutparam1 = new LinearLayout.LayoutParams(0,LayoutParams.FILL_PARENT,0.2f);
		layoutparam1.setMargins(5, 5, 0, 2);
		LinearLayout firstSectionLeftView = new LinearLayout(appContext);
		firstSectionLeftView.setLayoutParams(layoutparam1);
		firstSectionLeftView.setOrientation(LinearLayout.VERTICAL);

		ImageView iv = new ImageView(appContext);
		iv.setTag(WidgetsTagName.DIALOG_LOGO);
		iv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		iv.setBackgroundColor(Color.DKGRAY);
		iv.setBackgroundDrawable(dialogIcon);
		firstSectionLeftView.addView(iv); 

		layoutparam1 = new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT,0.8f);
		LinearLayout firstSectionRightView = new LinearLayout(appContext);
		firstSectionRightView.setLayoutParams(layoutparam1);
		firstSectionRightView.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
		firstSectionRightView.setOrientation(LinearLayout.VERTICAL);

		TextView dialogHeaderTV = new TextView(appContext);
		dialogHeaderTV.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		dialogHeaderTV.setTag(WidgetsTagName.DIALOG_TOPHEADER_TEXTVIEW);
		dialogHeaderTV.setText(ConstantStrings.PAY_RUPEES);
		dialogHeaderTV.setTextSize(20);
		dialogHeaderTV.setTypeface(Typeface.DEFAULT_BOLD);
		dialogHeaderTV.setTextColor(Color.WHITE);
		firstSectionRightView.addView(dialogHeaderTV);

		TextView dialogDetailsTV = new TextView(appContext);
		dialogDetailsTV.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		dialogDetailsTV.setTag(WidgetsTagName.DIALOG_TOPHEADER_DETAIL_TEXTVIEW);
		dialogDetailsTV.setText(ConstantStrings.THISWILL_CHARGE_YOU_ACCOUNT);
		dialogDetailsTV.setTextSize(12);
		dialogDetailsTV.setTypeface(Typeface.DEFAULT_BOLD);
		dialogDetailsTV.setTextColor(Color.WHITE);
		firstSectionRightView.addView(dialogDetailsTV); 

		firstSectionView.addView(firstSectionLeftView);
		firstSectionView.addView(firstSectionRightView);

		LinearLayout buttonViewLL = new LinearLayout(appContext);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		//lp.setMargins(0, 10, 0, 0);
		buttonViewLL.setLayoutParams(lp);
		buttonViewLL.setGravity(Gravity.CENTER);
		buttonViewLL.setOrientation(LinearLayout.HORIZONTAL);
		buttonViewLL.setWeightSum(1f);

		Button buttonNext = new Button(appContext);
		if(orientation.equalsIgnoreCase("landscape"))
			lp = new LinearLayout.LayoutParams(0,(int)(0.3*height),0.5f);
		else
			lp = new LinearLayout.LayoutParams(0,(int)(0.1*height),0.5f);
		buttonNext.setLayoutParams(lp);
		buttonNext.setText(ConstantStrings.ACCEPT);
		buttonNext.setTextColor(Color.WHITE);
		buttonNext.setTypeface(Typeface.DEFAULT_BOLD);
		buttonNext.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		buttonNext.setTag(WidgetsTagName.DIALOG_ACCEPT_BUTTONVIEW);
		buttonNext.setBackgroundColor(Color.RED);

		buttonViewLL.addView(buttonNext);

		Button buttonBack = new Button(appContext);
		if(orientation.equalsIgnoreCase("landscape"))
			lp = new LinearLayout.LayoutParams(0,(int)(0.3*height),0.5f);
		else
			lp = new LinearLayout.LayoutParams(0,(int)(0.1*height),0.5f);
		buttonBack.setLayoutParams(lp);
		buttonBack.setText(ConstantStrings.CANCEL);
		buttonBack.setTextColor(Color.WHITE);
		buttonBack.setTypeface(Typeface.DEFAULT_BOLD);
		buttonBack.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		buttonBack.setTag(WidgetsTagName.DIALOG_CANCLE_BUTTONVIEW);
		buttonBack.setBackgroundColor(Color.GREEN);

		buttonViewLL.addView(buttonBack);

		parentView.addView(tittleSectionInnerView);
		parentView.addView(firstSectionView);
		parentView.addView(buttonViewLL);
		return parentView;
	}
}
