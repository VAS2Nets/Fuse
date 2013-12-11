package com.vas2nets.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactDetails extends Activity {
     String name;
     Bitmap bitmap;
     SipManager mSipManager = null;
		SipProfile mSipProfile = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_details);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		Intent i = getIntent();
		name = i.getStringExtra("ContactName");
		String email = i.getStringExtra("ContactEmail");
		String phoneNumber = i.getStringExtra("ContactPhoneNumber");
		String photo = i.getStringExtra("ContactPhoto");
		
		this.getActionBar().setTitle(name);
		
		TextView tv_name = (TextView)findViewById(R.id.d_contactname);
		tv_name.setText(name);
		
		TextView tv_email = (TextView)findViewById(R.id.d_contactemail);
		tv_email.setText(email);
		
		TextView tv_phonenumber = (TextView)findViewById(R.id.d_contactnumber);
		tv_phonenumber.setText(phoneNumber);
		
		ImageView iv = (ImageView)findViewById(R.id.contactImageView);
		Uri uri = null;
		if (photo != null){
			try{
				uri = Uri.parse(photo);
			}catch(Exception e){
				
			}
		}
		
		iv.setImageURI(uri);
		/*
		if (photo != null){
			try{
				bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photo));
				
			}catch(Exception e){
				
			}
		}
		iv.setImageBitmap(bitmap);
		*/
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_details, menu);
		return true;
	}
	
	public void startFreeCall(View v){
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
		
		SipAudioCall.Listener listener = new SipAudioCall.Listener() {
			  
			   @Override
			   public void onCallEstablished(SipAudioCall call) {
			      call.startAudio();
			      call.setSpeakerMode(true);
			      call.toggleMute();
			      
			   }
			   
			   @Override
			   public void onCallEnded(SipAudioCall call) {
			      // Do something.
			   }
			};
			 mSipManager.makeAudioCall(mSipProfile.getUriString(), "omotayo@iptel.org", listener, 30); 
			
		
		}catch(Exception e){
			
		}
		
		
	}
	
	
	public void startCall(View v){
		TextView tv = (TextView) findViewById(R.id.d_contactnumber);
		String phoneNumber = "tel:" + tv.getText().toString();
		Intent i = new Intent(Intent.ACTION_CALL);
		i.setData(Uri.parse(phoneNumber));
		startActivity(i);
	}
	public void startSMS(View v){
		TextView tv = (TextView) findViewById(R.id.d_contactnumber);
		String phoneNumber = "sms:" + tv.getText().toString();
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);         
		sendIntent.setData(Uri.parse(phoneNumber));
		startActivity(sendIntent);
	}
	
	public void startChat(View v){
		Intent i = new Intent(this, FuseChat.class);
	    startActivity(i);
	}

}
