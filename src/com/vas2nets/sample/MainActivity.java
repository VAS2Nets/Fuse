package com.vas2nets.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthListener;





import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

public class MainActivity extends FragmentActivity implements TabListener {
	
	SocialAuthAdapter f_adapter;

	SipManager mSipManager = null;
	SipProfile mSipProfile = null;
	//List view
		
		ArrayAdapter<String> adapter;
		//Search EditText
		EditText inputSearch;
		//ArrayList for Listview
		ArrayList<HashMap<String, String>> productList;
		String userName;
	
	ViewPager viewPager;
	ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		f_adapter = new SocialAuthAdapter(new ResponseListener());
		//f_adapter = new SocialAuthAdapter(new ResponseListener());
		
      
		
		viewPager  =  (ViewPager) findViewById(R.id.pager);
		FragmentManager fm = getSupportFragmentManager();
		viewPager.setAdapter(new MyAdapter(fm));
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				actionBar.setSelectedNavigationItem(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab homeTab = actionBar.newTab();
		homeTab.setText("Home");
		homeTab.setTabListener(this);
		
		ActionBar.Tab contactTab = actionBar.newTab();
		contactTab.setText("Contact");
		contactTab.setTabListener(this);
		
		ActionBar.Tab recentTab = actionBar.newTab();
		recentTab.setText("Recent");
		recentTab.setTabListener(this);
		
		actionBar.addTab(homeTab);
		actionBar.addTab(contactTab);
		actionBar.addTab(recentTab);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	
	public void startFacebook(View v){
		f_adapter.authorize(this, Provider.FACEBOOK);
	}
	
	private final class ResponseListener implements DialogListener {

		@Override
		public void onBack() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onComplete(Bundle arg0) {
			// TODO Auto-generated method stub
		
			f_adapter.getUserProfileAsync(new SocialAuthListener<Profile>(){
			
				@Override
				public void onError(SocialAuthError arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onExecute(String arg0, Profile arg1) {
					// TODO Auto-generated method stub
					String fname = arg1.getFullName().toString();
					String fgender = arg1.getGender().toString();
					String femail = arg1.getEmail().toString();
					String fcountry = arg1.getCountry().toString();
					String flocation = arg1.getLocation().toString();
					String fphoto = arg1.getProfileImageURL().toString();
					//Map<String, String> mp = arg1.getContactInfo();
					
					
					Intent i = new Intent(MainActivity.this, FacebookActivity.class);
					i.putExtra("fname", fname);
					i.putExtra("fgender", fgender);
					i.putExtra("femail", femail);
					i.putExtra("fcountry", fcountry);
					i.putExtra("flocation", flocation);
					i.putExtra("fphoto", fphoto);
					System.out.println(fphoto);
					startActivity(i);
					
				}
				
			});
		}

		@Override
		public void onError(SocialAuthError arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	
	public void startChat(View v){
		//Intent i = new Intent(getActivity(), FacebookActivity.class);
        //startActivity(i);
		Intent i = new Intent(this, Comment.class);
		startActivity(i);
	}
	
	public void startTimeline(View v){
		//Intent i = new Intent(this, Timeline.class);
		//startActivity(i);
	}
	
	public void startEmail(View v){
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{"samson.u@vas2nets.com"});          
		email.putExtra(Intent.EXTRA_SUBJECT, "Fuse Test");
		//email.putExtra(Intent.EXTRA_TEXT, "message");
		email.setType("message/rfc822");
		startActivity(Intent.createChooser(email, "Choose an Email client :"));
	}
	public void startCalendar(View v){
		Intent calendarIntent = new Intent(Intent.ACTION_EDIT);  
		calendarIntent.setType("vnd.android.cursor.item/event");
		calendarIntent.putExtra("title", "Vas2net Xmas party");
		//calendarIntent.putExtra("beginTime", startTimeMillis);
		//calendarIntent.putExtra("endTime", endTimeMillis);
		calendarIntent.putExtra("description", "Close of year party");
		startActivity(calendarIntent);
	}
	
	
	

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(arg0.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		
		if (item.getItemId() == R.id.action_search){
			//openSearch();
            return true;
		}
		else if (item.getItemId() == R.id.comment){
			
			Intent i = new Intent(this, Comment.class);
			
            startActivity(i);
            
            return true;
		}else{
			return super.onOptionsItemSelected(item);
		}
		
	  
	}
	
	
}

class MyAdapter extends FragmentPagerAdapter{

	public MyAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		Bundle data = new Bundle();
		Fragment fragment = null;
		if (arg0 == 0){
			fragment = new Home();
		}
		if(arg0 == 1){
		    fragment = new Contact();
		}
		if(arg0 == 2){
			fragment = new Recent();
		}
		
		return fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}
	

}
