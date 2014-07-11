package com.vas2nets.fuse.profile;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vas2nets.fuse.MainActivity;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.db.DBHelper;
import com.vas2nets.fuse.image.PhotoUtility;
import com.vas2nets.fuse.profile.UpdateProfileActivity.UpdateMyProfile;

//import com.vas2nets.fuse.social.core.AddSocialNetworksActivity;

import com.vas2nets.fuse.social.core.AddSocialActivity;

import com.vas2nets.fuse.user.UserContentProvider;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentProfile extends Fragment implements LoaderCallbacks<Cursor>{
	private ProgressDialog pDialog;
	private UserCursorAdapter cAdapter;
	private TextView ftv;
	private TextView ltv;
	private TextView etv;
	private ImageView profilepix;
	private ContentValues values;
	private Bitmap bitmap;
	private String photo;
	byte[] outputPhoto;
	
	String[] projection = new String[] {
			DBHelper.USER_KEY_ID,
			DBHelper.USER_KEY_FIRSTNAME,
			DBHelper.USER_KEY_LASTNAME,
			DBHelper.USER_KEY_PHONENUMBER,
			DBHelper.USER_KEY_PHOTO
    };
	

	public FragmentProfile() {
		// Required empty public constructor
	}
	
	
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		ContentResolver cr = getActivity().getContentResolver();
		 Cursor cursor = cr.query(UserContentProvider.CONTENT_URI, projection, null, null, null);
		 
		 cAdapter = new UserCursorAdapter(getActivity(),cursor);
		 
		 cAdapter.notifyDataSetChanged();
		 getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ftv = (TextView) getActivity().findViewById(R.id.firstName);
		ltv = (TextView) getActivity().findViewById(R.id.lastName);
		etv = (TextView) getActivity().findViewById(R.id.email);
		profilepix = (ImageView) getActivity().findViewById(R.id.myprofileImage);
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.edit_profile, container,
				false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		Uri uri = UserContentProvider.CONTENT_URI;
		// return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
		return new CursorLoader(getActivity(), uri, projection, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		cAdapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		cAdapter.swapCursor(null);
	}
	
	public class SaveMyProfile extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				//Activity activity = getActivity();
				pDialog = new ProgressDialog(getActivity());
				pDialog.setMessage("Applying Changes...");
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
				// update profile in server using json post
				return null;
			} catch (Exception e) {

			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			try {
				Intent i = new Intent(getActivity(),
						MainActivity.class);
				getActivity().finish();
				startActivity(i);

			} catch (Exception e) {
                System.out.println(e.getMessage());
			}
		}

	}
	
	public void updateUser(View v) {
		String firstName = ftv.getText().toString();
		String lastName = ltv.getText().toString();
		String email = etv.getText().toString();
		// store bitmap in sqlite

		Bitmap photobitmap = ((BitmapDrawable) profilepix.getDrawable())
				.getBitmap();
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
      
		values = new ContentValues();
		SharedPreferences pref3 = getActivity().getApplicationContext().getSharedPreferences(
				"FusePreferences", 0);
		String sipId = pref3.getString("FuseSipID", null);
		
		values.put(DBHelper.USER_KEY_FIRSTNAME, firstName);
		values.put(DBHelper.USER_KEY_LASTNAME, lastName);
		values.put(DBHelper.USER_KEY_EMAIL, email);
		values.put(DBHelper.USER_KEY_PHOTO, PhotoUtility.getBytes(photobitmap));
		
		//values.put(DBHelper.USER_KEY_PHONENUMBER, phoneNumber);
		//values.put(DBHelper.USER_KEY_AUTHKEY, authKey);
		//values.put(DBHelper.USER_KEY_SIPID, sipId);
		//values.put(DBHelper.USER_KEY_DEVICEID, deviceId);
		this.getActivity().getContentResolver().update(UserContentProvider.CONTENT_URI, values, 
				DBHelper.USER_KEY_SIPID+"="+sipId, null);
		//getContentResolver().insert(UserContentProvider.CONTENT_URI, values);
        
		photobitmap.compress(Bitmap.CompressFormat.PNG, 90, bao);
		outputPhoto = bao.toByteArray();
		photo = Base64.encodeToString(outputPhoto, Base64.DEFAULT);
		new SaveMyProfile().execute();

	}
	
	private class UserCursorAdapter extends CursorAdapter {
		
		private Context mContext;
		private Cursor cr;
		private LayoutInflater inflater;

		public UserCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
			mContext = context;
			cr = c;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			 ImageView imageView = (ImageView) view.findViewById(R.id.profilepiximageView);
			 byte[] img = cursor.getBlob(cursor.getColumnIndex(DBHelper.USER_KEY_PHOTO));
			 imageView.setImageBitmap(PhotoUtility.getPhoto(img));
			 imageView.setBackgroundResource(R.drawable.imageborder);
			 
			 
			 String firstName = cursor.getString(cursor.getColumnIndex(DBHelper.USER_KEY_FIRSTNAME));
			 String lastName = cursor.getString(cursor.getColumnIndex(DBHelper.USER_KEY_LASTNAME));
			 //String phoneN = cursor.getString(cursor.getColumnIndex(DBHelper.USER_KEY_PHONENUMBER));
			 
			 TextView fName = (TextView)view.findViewById(R.id.firstName);
			 //fullName.setText(firstName + " " + lastName);
			 fName.setText(firstName);
			 TextView lName = (TextView)view.findViewById(R.id.lastName);
			 //fullName.setText(firstName + " " + lastName);
			 lName.setText(lastName);
			 //byte[] img=null;
			
			 /*if (img == null){
				 imageView.setImageResource(R.drawable.noface);
			 }else{
				 
				 imageView.setImageBitmap(PhotoUtility.getPhoto(img));
			 }*/
			 
			 /*
			 String photo = cursor.getString(cursor.getColumnIndex(DBHelper.USER_KEY_PHOTO));
			 if(photo == null){
		    	 imageView.setImageResource(R.drawable.noface);
		     }else{
			     imageView.setImageURI(Uri.parse(photo));
		     }*/
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup container) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.edit_profile, container, false);
			return view;
		}
		
	}

}


