package com.vas2nets.sample;



import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class Contact extends Fragment {

	private String id;
	private String name;
	private String phoneNumber;
	private String email;
	private String photo;
	private MyPhoneContact myContact;
	String image_uri = "";
	Bitmap bitmap = null;
	String emailType;
	ListView mContactsList;
	List<MyPhoneContact> allContacts;
	String defaultPhoto;
	MyAdapter madpt;
	
	EditText inputSearch;
	
	
	public Contact() {
		// Required empty public constructor
	}
	
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		defaultPhoto = getActivity().getResources().getString(R.drawable.noface);
		allContacts = new ArrayList<MyPhoneContact>();
		readContacts();
		mContactsList = (ListView) getActivity().findViewById(R.id.contact_listview);
		madpt = new  MyAdapter(getActivity(),android.R.layout.simple_list_item_1 , R.id.customtextView, allContacts);
		mContactsList.setAdapter(madpt);
		
		mContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				  
				MyPhoneContact pc =  (MyPhoneContact) arg0.getItemAtPosition(arg2);
				Intent i = new Intent(getActivity(), ContactDetails.class);
				i.putExtra("ContactName",pc.getName());
				i.putExtra("ContactEmail", pc.getEmail());
				i.putExtra("ContactPhoneNumber", pc.getPhoneNumber());
				i.putExtra("ContactPhoto", pc.getPhoto());
				startActivity(i);
			}
			
		});
		
		inputSearch = (EditText) getActivity().findViewById(R.id.inputSearch);
		inputSearch.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
				madpt.getFilter().filter(s);
			}
			
		});
		
		
		
	}
	
	public void readContacts(){
		ContentResolver cr = getActivity().getContentResolver();
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME+ " COLLATE LOCALIZED ASC";
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, sortOrder);
		if (cur.getCount() > 0){
			while (cur.moveToNext()){
				id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
				if(Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0){
					System.out.println("name: " + name + " " + "ID " + id);
					System.out.println("photo" + image_uri);
					Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);
					while (pCur.moveToNext()){
						phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						
						System.out.println("phone" + phoneNumber);
						
					}
					pCur.close();
					
					Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[] {id} , null);
					while(emailCur.moveToNext()){
						email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
						System.out.println(email);
					}
					emailCur.close();
					myContact = new MyPhoneContact(id, name, phoneNumber, image_uri, email);
				    allContacts.add(myContact);
				}
				
				
			}
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	private class MyAdapter extends ArrayAdapter{

		@SuppressWarnings("unchecked")
		public MyAdapter(Context context, int resource, int textViewResourceId,
				List<MyPhoneContact> objects) {
			super(context, resource, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}
		
		@SuppressWarnings("unused")
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			LayoutInflater inflater =  getActivity().getLayoutInflater(); 
			View row = inflater.inflate(R.layout.custom_contact_layout, parent,false);
			List<MyPhoneContact> con = allContacts;
			
			TextView tv = (TextView)row.findViewById(R.id.customtextView);
			tv.setText(con.get(position).getName());
			
			TextView tv_contact = (TextView) row.findViewById(R.id.customcontacttextView);
			String phoneN = con.get(position).getPhoneNumber();
			if (phoneN == null){
				tv_contact.setText("No phone number");
			}else{
				tv_contact.setText(phoneN);
			}
			
			String contact_image = con.get(position).getPhoto();
			Uri uri = null;
			if (contact_image != null){
				try{
					bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(contact_image));
					
				}catch(Exception e){
					
				}
			}else{
				try{
					bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(defaultPhoto));
					
				}catch(Exception e){
					
				}
			}
			
			ImageView iv =(ImageView) row.findViewById(R.id.customimageView);
			iv.setImageBitmap(bitmap);
			
			String email = con.get(position).getEmail();
			/*
			String [] items = getResources().getStringArray(R.array.contacts);
			ImageView iv =(ImageView) row.findViewById(R.id.customimageView);
			TextView tv = (TextView)row.findViewById(R.id.customtextView);
			tv.setText(items[position]);
			if (items[position].equals("Samson")){
				iv.setImageResource(R.drawable.sam_chat);
			}else if(items[position].equals("Jonathan")){
				iv.setImageResource(R.drawable.sam_notification);
			}
			*/
			return row;
		}
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		
		return inflater.inflate(R.layout.fragment_contact, container, false);
	}

	
	
	
}
