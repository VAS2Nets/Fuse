package com.vas2nets.fuse.profile;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.db.DBHelper;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class PFragment extends Fragment {
	private TextView fView;
	private TextView lView;
	private TextView eView;
	private ImageView profilepix;
	public PFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences(
				"FusePreferences", 0);
		String sipId = userPref.getString("FuseSipID", null);
		// get user cursor
		Cursor c = new DBHelper(getActivity()).getUser(sipId);
		String fname = null;
		String lname = null;;
		String email = null;
		byte bytes[] = null;
		while(c.moveToNext()){
			fname = c.getString(0);
			lname= c.getString(1);
			email = c.getString(2);
			bytes = c.getBlob(3);
		}
		c.close();
		Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		View view = inflater.inflate(R.layout.fragment_, container, false);
		fView = (TextView) view.findViewById(R.id.firstName);
		lView = (TextView) view.findViewById(R.id.lastName);
		eView = (TextView) view.findViewById(R.id.email);
		profilepix = (ImageView) view.findViewById(R.id.myprofileImage);
		fView.setText(fname);
		lView.setText(lname);
		eView.setText(email);
		profilepix.setImageBitmap(bMap);
		
		//return inflater.inflate(R.layout.fragment_, container, false);
		return view;
	}
	
	

}
