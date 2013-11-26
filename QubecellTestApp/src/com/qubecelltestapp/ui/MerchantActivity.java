package com.qubecelltestapp.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.qubecell.constants.IntentConstant;
import com.qubecell.ui.BaseActivity;

public class MerchantActivity extends Activity {

	private Button startButton = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getResources().getIdentifier("activity_merchant", "layout", getPackageName()));

		startButton = ((Button)findViewById(getResources().getIdentifier("startbutton", "id", getPackageName())));	
		startButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) 
			{
				startPayment();			
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	/*	String info = null;
		if(BaseActivity.getQubecellStatus())
		{
			info = "Result success" + String.valueOf(BaseActivity.getCahrgedAmount());
		}
		else 
		{
			info = "failure"; 
		}*/
		//Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();			
	}			
	
	private  void startPayment() {
		Bitmap imageViewBit = BitmapFactory.decodeResource(getResources(), R.drawable.logo_new);
	    BaseActivity.setLogoImage(imageViewBit);
		BaseActivity.setTitleText("Qubecell...");	
		BaseActivity.setBackGroundColor(0xffff0000); //  red  (0xff0000ff); //blue
		BaseActivity.setThemeColor(0xffcccccc); // light gray //(0xff00ff00);// grean      
		BaseActivity.setBillingPartner("Powered By Qubecell.");
		Intent nextActivity = new Intent(MerchantActivity.this, com.qubecell.ui.QubecellActivity.class);		

		ArrayList<HashMap<String, String>> listMap = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> hMap = new HashMap<String, String>();

		hMap.put(IntentConstant.USERNAME, "U2FsdGVkX1++RS6lFZad68y57JzK4XaaBT5CmN0N4wU=");
		hMap.put(IntentConstant.PASSWORD, "U2FsdGVkX1/ZM1fnb/kW1Cnv+sRq+A==");
		hMap.put(IntentConstant.KEY, "98aa9d103ebb4f759f5a6143317ec98a");
		hMap.put(IntentConstant.MSISDN_USERNAME, "EninoV");
		hMap.put(IntentConstant.MSISDN_PASSWORD, "E9+ni12no@V_13");
		hMap.put(IntentConstant.MSISDN_KEY, "5d99bd2ab6d04a30bed34b26ec613247");
		hMap.put(IntentConstant.PRODUCT_ID, "QUBT");
		hMap.put(IntentConstant.VODA_PRODUCT_ID, "eninov_vodafone_t2");
		hMap.put(IntentConstant.IDEA_PRODUCT_ID, "eninov_idea_t2");
		hMap.put(IntentConstant.AIRTEL_PRODUCT_ID, "eninov_airtel_t2");
		hMap.put(IntentConstant.TATA_PRODUCT_ID, "eninov_tata_t2");
		hMap.put(IntentConstant.PAY_AMOUNT, "2");
		hMap.put(IntentConstant.MSISDN_ERROR_MSG, "I am msisdn error message.");
		hMap.put(IntentConstant.EVENTCHARGE_ERROR_MSG, "I am event charge error message.");
		hMap.put(IntentConstant.EVENTCHARGE_STATUS_MSG, "I am event status message.");
		hMap.put(IntentConstant.SENDOTP_ERROR_MSG, "I am sendotp error message.");
		listMap.add(hMap);		    
		nextActivity.putExtra(IntentConstant.ARRAY_LIST, listMap);
		startActivity(nextActivity);	
	}
}
