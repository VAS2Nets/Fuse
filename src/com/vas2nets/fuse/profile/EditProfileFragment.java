package com.vas2nets.fuse.profile;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.layout;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link EditProfileFragment.OnFragmentInteractionListener} interface to handle
 * interaction events.
 * 
 */
public class EditProfileFragment extends Fragment {

	//private OnFragmentInteractionListener mListener;

	public EditProfileFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_edit_profile, container,
				false);
	}
	
public void updateUser(View v){
	
}



}
