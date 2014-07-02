package com.vas2nets.fuse.sip.call;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipProfile;
import com.csipsimple.api.SipUri;
import com.vas2nets.fuse.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentSipCallList extends Fragment implements LoaderCallbacks<Cursor>{
	
	private CallListCursorAdapter cAdapter;
	private ListView lv;
	private TextView emptyTxt;
	String selection;
	
	
	String[] projection = new String[] {
			CallLog.Calls._ID, CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL,
            CallLog.Calls.CACHED_NUMBER_TYPE, CallLog.Calls.DURATION, CallLog.Calls.DATE,
            CallLog.Calls.NEW, CallLog.Calls.NUMBER, CallLog.Calls.TYPE,
            SipManager.CALLLOG_PROFILE_ID_FIELD
	};
	
	String from;
	String to;
	
	private String phoneNumber;
	private String name;
	private String photoUrl = null;
	
	private long existingProfileId = SipProfile.INVALID_ID;

	public FragmentSipCallList() {
		// Required empty public constructor
	}
	
	@SuppressLint("CutPasteId")
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		lv = (ListView ) getActivity().findViewById(R.id.mycalllist_listview);
		emptyTxt = (TextView) getActivity().findViewById(R.id.emptyListElem);
		
		ContentResolver cr = getActivity().getContentResolver();	
		Cursor cursor = cr.query(SipManager.CALLLOG_URI, null, null, null, null);
		
		if (cursor.getCount() > 0){
			
		}else{
			emptyTxt.setVisibility(View.VISIBLE);
			emptyTxt.setText("Call History is empty");
			View empty = getActivity().findViewById(R.id.emptyListElem);  
			empty.setVisibility(View.VISIBLE);
			lv.setEmptyView(empty);
		}
		
		cAdapter = new CallListCursorAdapter(getActivity(),cursor);
		lv.setAdapter(cAdapter);
		cAdapter.notifyDataSetChanged();
		
		getLoaderManager().initLoader(0, null, this);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				openContactListDialog(arg1);
			}

		});
		
		
		
	}
	
	private class CallListCursorAdapter extends CursorAdapter {
		
		private Context mContext;
		private Cursor cr;
		private LayoutInflater inflater;
		
		private TextView callName;
		private TextView callMessage;
		private TextView callTime;
		private TextView callDuration;
		private ImageView callImage;
		private ImageView callImageStatus;


		public CallListCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
			mContext = context;
			cr = c;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context arg1, Cursor cursor) {
			// TODO Auto-generated method stub
			
			callName = (TextView) view.findViewById(R.id.calllistnametextView);
			//callMessage = (TextView) view.findViewById(R.id.calllistmessagetextView);
			callTime = (TextView) view.findViewById(R.id.calllisttimetextView);
			callDuration = (TextView) view.findViewById(R.id.durationtextView);
			callImage = (ImageView)view.findViewById(R.id.calllistimageView);
			callImageStatus = (ImageView)view.findViewById(R.id.callStatusimageView);
			
			
			phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
			String time = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
			String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
			
			if (duration.equals("0")){
				callDuration.setText("0:00");
			}else{
				callDuration.setText(duration);
			}
			
			String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
			
			int callType = Integer.parseInt(type);
			 switch (callType) {
	            case Calls.INCOMING_TYPE:
	            	callImageStatus.setImageResource(R.drawable.ic_call_incoming);
	                break;
	            case Calls.OUTGOING_TYPE:
	            	callImageStatus.setImageResource(R.drawable.ic_call_outgoing);
	                break;
	            case Calls.MISSED_TYPE:
	            	callImageStatus.setImageResource(R.drawable.ic_call_missed);
	                break;
	            default:
	                throw new IllegalArgumentException("invalid call type: " + callType);
	        }
			
		long date = Long.parseLong(time);
			
		phoneNumber = getPhoneNumberFromSipID(phoneNumber);
		
		boolean numberExist = contactExists(getActivity(), phoneNumber);
			
			if (numberExist){
				// define the columns I want the query to return
				String[] projection = new String[] {
				        ContactsContract.PhoneLookup.DISPLAY_NAME,
				        ContactsContract.PhoneLookup._ID,
				        ContactsContract.PhoneLookup.PHOTO_URI};
				// encode the phone number and build the filter URI
				Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
				// query time
				Cursor cur = getActivity().getContentResolver().query(contactUri, projection, null, null, null);
				String contactId = null;
				
				
				if (cur.moveToFirst()) {
					// Get values from contacts database:
				    contactId = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID));
				    name =      cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));  
				    photoUrl =      cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI)); 
				}
			}else{
				
				String newNumber = remove234(phoneNumber);
				boolean newNumberExist = contactExists(getActivity(), newNumber);
				if (newNumberExist){
					// define the columns I want the query to return
					String[] projection = new String[] {
					        ContactsContract.PhoneLookup.DISPLAY_NAME,
					        ContactsContract.PhoneLookup._ID,
					        ContactsContract.PhoneLookup.PHOTO_URI};
					// encode the phone number and build the filter URI
					Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
					// query time
					Cursor cur = getActivity().getContentResolver().query(contactUri, projection, null, null, null);
					String contactId = null;
					if (cur.moveToFirst()) {
						// Get values from contacts database:
					    contactId = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID));
					    name =      cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
					    photoUrl =      cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI)); 
					    
					}
					
				}
				
			}
			
			callName.setText(name);
			//callTime.setText(time);
			int flags = DateUtils.FORMAT_ABBREV_RELATIVE;
		    callTime.setText(DateUtils.getRelativeTimeSpanString(date, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, flags));
		   
		    if(photoUrl == null){
		    	callImage.setImageResource(R.drawable.noface);
		     }else{
		    	 callImage.setImageURI(Uri.parse(photoUrl));
		     }
			
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.mycalllist, arg2, false);
			return view;
		}
		
	}
	
	public boolean contactExists(Context context, String number) {
		/// number is the phone number
		Uri lookupUri = Uri.withAppendedPath(
		PhoneLookup.CONTENT_FILTER_URI, 
		Uri.encode(number));
		String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
		Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
		try {
		   if (cur.moveToFirst()) {
		      return true;
		}
		} finally {
		if (cur != null)
		   cur.close();
		}
		return false;
	}
	
	public String remove234(String number){
		String newNumber = null;
		StringBuffer buf = new StringBuffer(number);

        int start = 0;
        int end = 3;
        buf.replace(start, end, "0"); 
        newNumber = buf.toString();
		
		return newNumber;
	}
	
	public String getPhoneNumberFromSipID(String sipId){
		
		sipId = sipId.substring(sipId.indexOf(":") + 1);
		sipId = sipId.substring(0, sipId.indexOf("@"));
		return sipId;
	}
	
	public void openContactListDialog(View view){
		
		TextView nametxt = (TextView) view.findViewById(R.id.calllistnametextView);
		final String name = nametxt.getText().toString();
		final String phoneNumber = getPhoneNumber(name, getActivity());
		
		final boolean numberExist = contactExists(getActivity(), phoneNumber);
		if (numberExist){
 		   
 	   	}else{
 	   		
 	   	}
		
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.call_list);
		//dialog.setTitle("Call List");
		
		final ListView mList = (ListView)dialog.findViewById(R.id.calllistlistView);
		List<String> al = new ArrayList<String>();
        al.add("Call");
        al.add("Delete");
        
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                 getActivity(), 
                 android.R.layout.simple_list_item_1,
                 al);

         mList.setAdapter(arrayAdapter); 
         
         mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        	  @Override
        	  public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
  					long arg3) {

        		 
        	              
        	               // ListView Clicked item value
        	               String  itemValue    = (String) mList.getItemAtPosition(arg2);
        	               if (itemValue == "Call"){
        	            	   
        	            	   
        	            	  if(isOnline()){
        	            		  
        	            		   String sipAddress = "sip:" + phoneNumber + "@197.253.10.26";
            	            	   
          	            		
        	            		   try{
     				            		Intent it = new Intent(Intent.ACTION_CALL);
     				            		it.setData(SipUri.forgeSipUri("csip", sipAddress));
     				            		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     				            		it.putExtra(SipProfile.FIELD_ACC_ID, SipProfile.FIELD_ACC_ID);
     				            		startActivity(it);
     				            	}catch(Exception e){
     				            	
     				            	}
          	            	   
          	            	 
        	         			  
        	         		  }else{
        	         			 Toast.makeText(getActivity(), "No Internet Connectivity", Toast.LENGTH_LONG).show();	
        	         		  }
        	            	   
        	            	   
        	            
		        	           
        	             }else if(itemValue == "Delete"){
		        	            	
        	             }
        	               
        	               
        	               
        	                // Show Alert 
        	                //Toast.makeText(getApplicationContext(),"Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG).show();
        	    	}
        	  });
        
         dialog.show();
	}
	
	
	public String getPhoneNumber(String name, Context context) {
		String ret = null;
		String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
		Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
		        projection, selection, null, null);
		if (c.moveToFirst()) {
		    ret = c.getString(0);
		}
		c.close();
		if(ret==null)
		    ret = "Unsaved";
		return ret;
	}
	
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_fragment_sip_call_list,
				container, false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		 return new CursorLoader(getActivity(), SipManager.CALLLOG_URI, new String[] {
             CallLog.Calls._ID, CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL,
             CallLog.Calls.CACHED_NUMBER_TYPE, CallLog.Calls.DURATION, CallLog.Calls.DATE,
             CallLog.Calls.NEW, CallLog.Calls.NUMBER, CallLog.Calls.TYPE,
             SipManager.CALLLOG_PROFILE_ID_FIELD
     },
             null, null,
             Calls.DEFAULT_SORT_ORDER);
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

}
