package com.vas2nets.sample;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;




public class Comment extends Activity {
	
	private static final int SELECT_PICTURE = 202;
	private static final int CAMERA_PIC_REQUEST = 220;
	ImageView iv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		android.app.ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setTitle("Share");
		iv = (ImageView) findViewById(R.id.displayimageView);
		
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.comment, menu);
		
		
		return true;
	}
	 
	public void sendTextComment(View v){
		
	   
	   
	   if (iv.getDrawable() == null){
		   
		    EditText et = (EditText) findViewById(R.id.editText1);
			String comment = et.getText().toString();
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, comment);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, "Share with..."));
			
	   }else{
		   
		   Intent shareIntent = new Intent();
		   shareIntent.setAction(Intent.ACTION_SEND);
		   shareIntent.putExtra(Intent.EXTRA_STREAM, R.id.displayimageView);
		   shareIntent.setType("image/jpeg");
		   startActivity(shareIntent);
		   
	   }
		
	}
	
	public void pickPicture(View v){
		Intent i = new Intent(
		Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				 
		startActivityForResult(i, SELECT_PICTURE);		
	}
	
	public void startCamera(View v){
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(i, CAMERA_PIC_REQUEST);
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
    		Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
 
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
 
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
    		iv.setImageBitmap(BitmapFactory.decodeFile(picturePath));
    	}
    	
    	if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK && null != data) {
    		Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
 
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
 
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
    		iv.setImageBitmap(BitmapFactory.decodeFile(picturePath));
    	}
    }
    
   
	

}

/**
 * Listens Response from Library
 * 
 */

