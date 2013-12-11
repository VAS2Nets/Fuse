package com.vas2nets.sample;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.TextView;

public class PhoneContact extends Activity {
	TextView textDetail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_contact);
		
		//mContactsList = (ListView) findViewById(R.layout.contact_list_view);
		//mCursorAdapter = new SimpleCursorAdapter(this, R.layout.contacts_list_item, null, FROM_COLUMNS, TO_IDS, 0);
		//mContactsList.setAdapter(mCursorAdapter);
		//textDetail = (TextView) findViewById(R.id.textView1);
		readContacts();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.phone_contact, menu);
		return true;
	}
	
	public void readContacts(){
		StringBuffer sb = new StringBuffer();
		sb.append("....Contact Details....");
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		String phone = null;
		String emailContact = null;
		String emailType = null;
		String image_uri = "";
		Bitmap bitmap = null;
		if (cur.getCount() > 0){
			while (cur.moveToNext()){
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
				if(Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0){
					System.out.println("name: " + name + " " + "ID " + id);
					sb.append("\n Contact Name: " + name);
					Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);
					while (pCur.moveToNext()){
						phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						sb.append("\n Phone number: " + phone);
						System.out.println("phone" + phone);
					}
					pCur.close();
					textDetail.setText(sb);
				}
			}
		}
		
		
		
	}

	
}
