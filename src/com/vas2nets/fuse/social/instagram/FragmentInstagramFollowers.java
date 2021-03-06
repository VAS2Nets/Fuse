package com.vas2nets.fuse.social.instagram;

import java.util.List;

import org.brickred.socialauth.Contact;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.image.ImageLoader;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentInstagramFollowers extends Fragment {
	
	SocialAuthAdapter adapter;
	ListView list;

	public FragmentInstagramFollowers() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_fragment_instagram_followers, container, false);
		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.INSTAGRAM, R.drawable.instagram);
		adapter.authorize(getActivity(), Provider.INSTAGRAM);
		
		list = (ListView) view.findViewById(R.id.instagramcontactList);
		
		return view;
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
			adapter.getContactListAsync(new ContactDataListener());
			
		}

		@Override
		public void onError(SocialAuthError arg0) {
			// TODO Auto-generated method stub
			
		}

	}
	
	// To receive the contacts response after authentication
				private final class ContactDataListener implements SocialAuthListener<List<Contact>> {

				@Override
				public void onError(SocialAuthError arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onExecute(String arg0, List<Contact> arg1) {
					// TODO Auto-generated method stub
					List<Contact> contactsList = arg1;
					list.setAdapter(new ContactAdapter(getActivity(), R.layout.instagran_contact_list, contactsList));
				}
				
				}
				
				// adapter for contact list
				public class ContactAdapter extends ArrayAdapter<Contact> {
					private final LayoutInflater mInflater;
					List<Contact> contacts;
					ImageLoader imageLoader;

					public ContactAdapter(Context context, int textViewResourceId, List<Contact> contacts) {
						super(context, textViewResourceId);
						mInflater = LayoutInflater.from(context);
						this.contacts = contacts;
						imageLoader = new ImageLoader(context);
					}

					@Override
					public int getCount() {
						return contacts.size();
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {

						final Contact bean = contacts.get(position);
						View row = mInflater.inflate(R.layout.instagran_contact_list, parent, false);

						TextView label = (TextView) row.findViewById(R.id.instagramcName);
						TextView email = (TextView) row.findViewById(R.id.instagramcEmail);
						ImageView cImage = (ImageView) row.findViewById(R.id.instagramcImage);

						Log.d("Custom-UI", "Display Name = " + bean.getDisplayName());
						Log.d("Custom-UI", "First Name = " + bean.getFirstName());
						Log.d("Custom-UI", "Last Name = " + bean.getLastName());
						Log.d("Custom-UI", "Contact ID = " + bean.getId());
						Log.d("Custom-UI", "Profile URL = " + bean.getProfileUrl());
						Log.d("Custom-UI", "Profile Image URL = " + bean.getProfileImageURL());
						Log.d("Custom-UI", "Email = " + bean.getEmail());

						imageLoader.DisplayImage(bean.getProfileImageURL(), cImage);
						label.setText(bean.getFirstName() + "  " + bean.getLastName());
						if (bean.getEmail() != null){
							email.setVisibility(View.VISIBLE);
							email.setText(bean.getEmail());
						}
					
						return row;
					}
				}

}
