package com.vas2nets.sample;



import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class FacebookActivity extends Activity {
	
	ProgressDialog pd;
	Bitmap image ;
	ImageView iv;
	String photo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook);
		
		Intent i = getIntent();
		String name = i.getStringExtra("fname");
		String gender = i.getStringExtra("fgender");
		String email = i.getStringExtra("femail");
		String location = i.getStringExtra("flocation");
		photo = i.getStringExtra("fphoto");
	
		TextView tn = (TextView) findViewById(R.id.displaynametextView);
		tn.setText(name);
		
		TextView tg = (TextView) findViewById(R.id.gendertextView);
		tg.setText(gender);
		
		TextView te = (TextView) findViewById(R.id.emailtextView);
		te.setText(email);
		
		TextView tl = (TextView) findViewById(R.id.locationtextView);
		tl.setText(location);
		
		iv = (ImageView) findViewById(R.id.profilepiximageView);
		
		pd = new ProgressDialog(this);
		pd.setMessage("Loading from facebook..");
		new TheTask().execute();
	}
	
	class TheTask extends AsyncTask<Void,Void,Void>
	{

	    @Override
	    protected void onPreExecute() {
	        // TODO Auto-generated method stub
	        super.onPreExecute();
	        pd.show();
	    }


	    @Override
	    protected Void doInBackground(Void... params) {
	        // TODO Auto-generated method stub
	        try
	        {
	        //URL url = new URL( "http://a3.twimg.com/profile_images/670625317/aam-logo-v3-twitter.png");


	        image = downloadBitmap(photo);
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	        // TODO Auto-generated method stub
	        super.onPostExecute(result);
	        pd.dismiss();
	        if(image!=null)
	        {
	            iv.setImageBitmap(image);
	        }

	    }   
	}
	
	
	
	
	 private Bitmap downloadBitmap(String url) {
	     // initilize the default HTTP client object
	     final DefaultHttpClient client = new DefaultHttpClient();

	     //forming a HttoGet request 
	     final HttpGet getRequest = new HttpGet(url);
	     try {

	         HttpResponse response = client.execute(getRequest);

	         //check 200 OK for success
	         final int statusCode = response.getStatusLine().getStatusCode();

	         if (statusCode != HttpStatus.SC_OK) {
	             Log.w("ImageDownloader", "Error " + statusCode + 
	                     " while retrieving bitmap from " + url);
	             return null;

	         }

	         final HttpEntity entity = response.getEntity();
	         if (entity != null) {
	             InputStream inputStream = null;
	             try {
	                 // getting contents from the stream 
	                 inputStream = entity.getContent();

	                 // decoding stream data back into image Bitmap that android understands
	                 image = BitmapFactory.decodeStream(inputStream);


	             } finally {
	                 if (inputStream != null) {
	                     inputStream.close();
	                 }
	                 entity.consumeContent();
	             }
	         }
	     } catch (Exception e) {
	         // You Could provide a more explicit error message for IOException
	         getRequest.abort();
	         Log.e("ImageDownloader", "Something went wrong while" +
	                 " retrieving bitmap from " + url + e.toString());
	     } 

	     return image;
	 }
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.facebook, menu);
		return true;
	}
	
	

}
