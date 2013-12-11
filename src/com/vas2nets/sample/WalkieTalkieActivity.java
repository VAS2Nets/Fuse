package com.vas2nets.sample;

import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.Menu;

public class WalkieTalkieActivity extends Activity {

	 SipManager mSipManager = null;
		SipProfile mSipProfile = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_walkie_talkie);
		
		
		try{
			
			
			if(mSipManager == null) {
			    mSipManager = SipManager.newInstance(this);
			}
			
			SipProfile.Builder builder = new SipProfile.Builder("psalmsin", "sip2sip.info");
			builder.setPassword("samson");
			mSipProfile = builder.build();
			
			Intent intent = new Intent();
			intent.setAction("android.SipDemo.INCOMING_CALL");
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, Intent.FILL_IN_DATA);
			mSipManager.open(mSipProfile, pendingIntent, null);
			
			mSipManager.setRegistrationListener(mSipProfile.getUriString(),new SipRegistrationListener(){

				@SuppressLint("ShowToast")
				@Override
				public void onRegistering(String arg0) {
					// TODO Auto-generated method stub
					
					//Toast.makeText(getApplicationContext(), "Registering with SIP Server...",Toast.LENGTH_LONG );
				}

				@SuppressLint("ShowToast")
				@Override
				public void onRegistrationDone(String arg0, long arg1) {
					// TODO Auto-generated method stub
					
					//Toast.makeText(getApplicationContext(), "Ready",Toast.LENGTH_LONG );
				}

				@SuppressLint("ShowToast")
				@Override
				public void onRegistrationFailed(String arg0, int arg1, String arg2) {
					// TODO Auto-generated method stub
					
					//Toast.makeText(getApplicationContext(), "Registration failed.  Please check settings.",Toast.LENGTH_LONG );
				}
				
				
			});
			
		}catch(Exception e){
			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.walkie_talkie, menu);
		return true;
	}

}
