package com.vas2nets.fuse;

/*
 * Standard operating procedures
 * 
 * 1. pull the latest
 * 2. make your changes
 * 3. push to git hub
 * 
 * 
 * Git Basic
 * 1. cd to fuse directory on command line
 * 2. Update your changes on Git
 *    a.) git add .
<<<<<<< HEAD
 *    b) git commit -m 'gbolaga.2014.07.11.3.09'
=======
 *    b) git commit -m 'jonathan.2014.07.11.2.48'
>>>>>>> d601ad396440f62d11146307230f7091e1e856d1
 *    c) git push
 *    
 * 3. Download Latest Project Files
 * 	git pull origin
 * 4. To clean repository
 * 
 * git clean -d -fx " " //////434353636
 * 
 */

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.brickred.socialauth.Feed;
import org.brickred.socialauth.android.SocialAuthAdapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csipsimple.api.SipConfigManager;
import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipProfile;
import com.vas2nets.fuse.db.ActiveSocialDB;
import com.vas2nets.fuse.db.DBHelper;
import com.vas2nets.fuse.db.SocialDBHelper;
import com.vas2nets.fuse.home.FragmentHomeSwipe;
import com.vas2nets.fuse.image.PhotoUtility;
import com.vas2nets.fuse.image.PicturePicker;
import com.vas2nets.fuse.navigation.NavDrawerItem;
import com.vas2nets.fuse.navigation.NavDrawerListAdapter;
import com.vas2nets.fuse.profile.EditProfileFragment;

import com.vas2nets.fuse.profile.FragmentProfile;

import com.vas2nets.fuse.social.core.AddSocialActivity;

import com.vas2nets.fuse.profile.PFragment;
//import com.vas2nets.fuse.profile.FragmentProfile.SaveMyProfile;

import com.vas2nets.fuse.profile.FragmentProfile;
import com.vas2nets.fuse.social.core.AddSocialActivity;
import com.vas2nets.fuse.profile.PFragment;
//import com.vas2nets.fuse.profile.FragmentProfile.SaveMyProfile;

import com.vas2nets.fuse.social.core.MySocialService;
import com.vas2nets.fuse.social.facebook.FragmentFacebookSwipe;
import com.vas2nets.fuse.social.instagram.FragmentInstagramSwipe;
import com.vas2nets.fuse.social.linkedin.FragmentLinkedInSwipe;
import com.vas2nets.fuse.social.twitter.FragmentTwitterSwipe;
import com.vas2nets.fuse.user.UserContentProvider;

public class MainActivity extends FragmentActivity {

	private MySocialService fs;
	private ImageView profilepix;
	private Timer timer = new Timer();
	final Handler handler = new Handler();
	private static final long UPDATE_INTERNAL = 20000;
	private ProgressDialog pDialog;
	private SocialDBHelper mHelper;
	private SQLiteDatabase dataBase;
	private ContentValues content, values;
	private static String SIP_ID;
	SharedPreferences userPref;
	// private ContentValues values;
	private Bitmap bitmap;
	private String photo;
	byte[] outputPhoto;

	SocialAuthAdapter s_adapter;
	List<Feed> feedList;

	String[] projection = new String[] { SocialDBHelper.SOCIAL_KEY_ID,
			SocialDBHelper.SOCIAL_KEY_MESSAGE, SocialDBHelper.SOCIAL_KEY_DATE,
			SocialDBHelper.SOCIAL_KEY_PROVIDER, SocialDBHelper.SOCIAL_KEY_READ };

	ActiveSocialDB db;
	private List<String> allProviders;

	// sip
	private static final String THIS_FILE = "SampleCSipSimpleAppActivity";
	private static final String SAMPLE_ALREADY_SETUP = "sample_already_setup";
	private long existingProfileId = SipProfile.INVALID_ID;
	private String[] menus;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private ActionBarDrawerToggle mDrawerToggle;

	TextView commentTxtView;
	ImageView shareButton;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		db = new ActiveSocialDB(this);
		allProviders = new ArrayList<String>();
		allProviders = db.getAllProviders();
		StringBuffer responseText = new StringBuffer();
		responseText.append("Logged in social networks...\n");
		for (String provider : allProviders) {
			responseText.append("\n" + provider);
		}
		// Toast.makeText(getApplicationContext(), responseText,
		// Toast.LENGTH_LONG).show();
		// social intializer
		// socialAuthInitializer();

		// sip initialization

		// Retrieve private preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean alreadySetup = prefs.getBoolean(SAMPLE_ALREADY_SETUP, false);
		if (!alreadySetup) {
			// Activate debugging .. here can come various other options
			// One can also decide to reuse csipsimple activities to setup
			// config
			SipConfigManager.setPreferenceStringValue(this,
					SipConfigManager.LOG_LEVEL, "4");
		}

		// Bind view buttons
		// ((Button) findViewById(R.id.start_btn)).setOnClickListener(this);
		// ((Button) findViewById(R.id.save_acc_btn)).setOnClickListener(this);

		// Get current account if any
		Cursor c = getContentResolver().query(
				SipProfile.ACCOUNT_URI,
				new String[] { SipProfile.FIELD_ID, SipProfile.FIELD_ACC_ID,
						SipProfile.FIELD_REG_URI }, null, null,
				SipProfile.FIELD_PRIORITY + " ASC");
		if (c != null) {
			try {
				if (c.moveToFirst()) {
					SipProfile foundProfile = new SipProfile(c);
					existingProfileId = foundProfile.id;
					// t1.setText(foundProfile.getSipUserName() + "@" +
					// foundProfile.getSipDomain());
				}
			} catch (Exception e) {
				Log.e(THIS_FILE, "Some problem occured while accessing cursor",
						e);
			} finally {
				c.close();
			}

		}

		// end of initialization

		// setting sip profile

		SharedPreferences pref = getApplicationContext().getSharedPreferences(
				"FusePreferences", 0);
		String sipID = pref.getString("FuseSipID", null);
		// String sipID = pref.getString("FusePhoneNumber", null);

		String pwd = "vas";
		// String pwd = "password";
		// String pwd = "";
		// String fullUser = "gbolaga@sip.linphone.org";
		String fullUser = sipID + "@197.253.10.26";
		// String fullUser = "29@197.253.10.26";

		String[] splitUser = fullUser.split("@");

		String error = getValidAccountFieldsError();
		if (TextUtils.isEmpty(error)) {

			// We do some VERY basic thing here (minimal), a real app should
			// probably manage input differently
			SipProfile builtProfile = new SipProfile();
			builtProfile.display_name = "Fuse";
			builtProfile.id = existingProfileId;
			builtProfile.acc_id = "<sip:" + fullUser + ">";
			builtProfile.reg_uri = "sip:" + splitUser[1];
			builtProfile.realm = "*";
			builtProfile.username = splitUser[0];
			builtProfile.data = pwd;
			builtProfile.proxies = new String[] { "sip:" + splitUser[1] };

			ContentValues builtValues = builtProfile.getDbContentValues();

			if (existingProfileId != SipProfile.INVALID_ID) {
				getContentResolver().update(
						ContentUris.withAppendedId(
								SipProfile.ACCOUNT_ID_URI_BASE,
								existingProfileId), builtValues, null, null);
			} else {
				Uri savedUri = getContentResolver().insert(
						SipProfile.ACCOUNT_URI, builtValues);
				if (savedUri != null) {
					existingProfileId = ContentUris.parseId(savedUri);
				}
			}
		}

		Intent it = new Intent(SipManager.INTENT_SIP_SERVICE);
		startService(it);

		SipConfigManager.setPreferenceBooleanValue(MainActivity.this,
				SipConfigManager.USE_3G_IN, true);
		SipConfigManager.setPreferenceBooleanValue(MainActivity.this,
				SipConfigManager.USE_3G_OUT, true);
		SipConfigManager.setPreferenceBooleanValue(MainActivity.this,
				SipConfigManager.USE_EDGE_IN, true);
		SipConfigManager.setPreferenceBooleanValue(MainActivity.this,
				SipConfigManager.USE_EDGE_OUT, true);
		SipConfigManager.setPreferenceBooleanValue(MainActivity.this,
				SipConfigManager.USE_GPRS_IN, true);
		SipConfigManager.setPreferenceBooleanValue(MainActivity.this,
				SipConfigManager.USE_GPRS_OUT, true);
		SipConfigManager.setPreferenceBooleanValue(MainActivity.this,
				SipConfigManager.USE_OTHER_IN, true);
		SipConfigManager.setPreferenceBooleanValue(MainActivity.this,
				SipConfigManager.USE_OTHER_OUT, true);
		SipConfigManager.setPreferenceBooleanValue(MainActivity.this,
				SipConfigManager.USE_WIFI_IN, true);
		SipConfigManager.setPreferenceBooleanValue(MainActivity.this,
				SipConfigManager.USE_WIFI_OUT, true);

		// end sip profile

		//initializeManager();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mTitle = "Fuse";
		menus = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		// Facebook
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1), true, "17"));
		// Twitter
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1), true, "22"));
		// LinkedIn
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
				.getResourceId(3, 1), true, "4"));
		// instagram
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
				.getResourceId(4, -1), true, "10"));
		// profile
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons
				.getResourceId(5, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// mDrawerList.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.drawer_list_item, menus));

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);//

			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mTitle);
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			selectItem(0);
		}

		// bindService(new Intent(MainActivity.this, MySocialService.class),
		// conn, Context.BIND_AUTO_CREATE);
		userPref = getApplicationContext().getSharedPreferences(
				"FusePreferences", 0);
		SIP_ID = userPref.getString("FuseSipID", null);
		
	}

	/**
	 * Swaps fragments in the main content view
	 */
	private void selectItem(int position) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		switch (position) {
		case 0:
			ft.replace(R.id.content_frame, new FragmentHomeSwipe());
			break;
		case 1:
			ft.replace(R.id.content_frame, new FragmentFacebookSwipe());

			break;
		case 2:
			ft.replace(R.id.content_frame, new FragmentTwitterSwipe());
			break;
		case 3:
			ft.replace(R.id.content_frame, new FragmentLinkedInSwipe());
			break;
		case 4:
			ft.replace(R.id.content_frame, new FragmentInstagramSwipe());
			break;
		case 5:
			ft.replace(R.id.content_frame, new PFragment());
		}
		ft.commit();

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(menus[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (item.getItemId() == R.id.action_edit) {
			/*
			 * Intent i = new Intent(this, ShareActivity.class);
			 * startActivity(i);
			 */
			return true;
		}

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PicturePicker.GALLERY_PICTURE && resultCode == RESULT_OK
				&& null != data) {
			BitmapDrawable bmpDrawable = null;
			// try to retrieve the image using the data from the intent
			Cursor cursor = getContentResolver().query(data.getData(), null,
					null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();

				int idx = cursor.getColumnIndex(ImageColumns.DATA);
				String fileSrc = cursor.getString(idx);
				bitmap = BitmapFactory.decodeFile(fileSrc);
				bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
				profilepix.setImageBitmap(bitmap);
			} else {

				bmpDrawable = new BitmapDrawable(getResources(), data.getData()
						.getPath());
				profilepix.setImageDrawable(bmpDrawable);
			}
		}

		if (requestCode == PicturePicker.CAMERA_REQUEST && resultCode == RESULT_OK
				&& null != data) {
			bitmap = (Bitmap) data.getExtras().get("data");

			bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
			// update the image view with the bitmap
			profilepix.setImageBitmap(bitmap);
		}

		if (resultCode == RESULT_CANCELED) {
			Toast.makeText(getApplicationContext(), "Cancelled",
					Toast.LENGTH_SHORT).show();
		}
	}


	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@SuppressWarnings("rawtypes")
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// When we get back from the preference setting Activity, assume
		// settings have changed, and re-login with new auth info.
		// initializeManager();
	}

	@Override
	public void onResume() {
		super.onResume();
		// initializeManager();
	}
	public void pickPicture(View v) {
		new PicturePicker(this).startDialog();
	}

	private String getValidAccountFieldsError() {
		String pwd = "password";
		// String pwd = "";
		String fullUser = "psalmsin@sip.linphone.org";
		// String fullUser = "20000@197.253.10.26";
		String[] splitUser = fullUser.split("@");

		if (TextUtils.isEmpty(fullUser)) {
			return "Empty user";
		}
		if (TextUtils.isEmpty(pwd)) {
			return "Empty password";
		}
		if (splitUser.length != 2) {
			return "Invaid user, should be user@domain";
		}
		return "";
	}

	class SaveMyProfile extends AsyncTask<String, String, String> {
		private TextView fView;
		private TextView lView;
		private TextView eView;
		//private ImageView profilepix;
		public SaveMyProfile(){
			
		}
        public SaveMyProfile(View v){
        	//  get the view objects
        	View root = v.getRootView();
    		fView = (TextView) root.findViewById(R.id.firstName);
    		lView = (TextView) root.findViewById(R.id.lastName);
    		eView = (TextView) root.findViewById(R.id.email);
    		profilepix = (ImageView) root.findViewById(R.id.myprofileImage);
    		
    		
        }
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				// Activity activity = getActivity();
				pDialog = new ProgressDialog(MainActivity.this);
				pDialog.setMessage("Saving Changes..");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
			} catch (Exception e) {

			}

		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				// do something with the image , covert to bitmap
	    		Bitmap photobitmap = ((BitmapDrawable) profilepix.getDrawable())
	    				.getBitmap();
	    		ByteArrayOutputStream bao = new ByteArrayOutputStream();
	            // set up the values for db persistence
	    		values = new ContentValues();	
	    		values.put(DBHelper.USER_KEY_FIRSTNAME, fView.getText().toString());
	    		values.put(DBHelper.USER_KEY_LASTNAME, lView.getText().toString());
	    		values.put(DBHelper.USER_KEY_EMAIL, eView.getText().toString());
	    		values.put(DBHelper.USER_KEY_PHOTO, PhotoUtility.getBytes(photobitmap));
	    		getContentResolver().update(UserContentProvider.CONTENT_URI, values, 
	    				DBHelper.USER_KEY_SIPID+"="+SIP_ID, null);
	    		
	    		photobitmap.compress(Bitmap.CompressFormat.PNG, 90, bao);
	    		outputPhoto = bao.toByteArray();
	    		photo = Base64.encodeToString(outputPhoto, Base64.DEFAULT);
				// update profile in server using json post
				return null;
			} catch (Exception e) {

			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			try {

				Intent i = new Intent(MainActivity.this, MainActivity.class);
				MainActivity.this.finish();
				startActivity(i);

				System.out.println("In SaveMyProfile.Post execute");

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

	}

	public void updateProfile(View view) {
			new SaveMyProfile(view).execute();
	}
	
	public SharedPreferences getSharedPref(){
       return userPref;
	}

	class InsertIntoDB extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			// new InsertIntoDB().execute();
			return null;
		}


    }
    
    public void addSocials(View v){	
		 Intent i = new Intent(this, AddSocialActivity.class);
		 startActivity(i);
}
}
