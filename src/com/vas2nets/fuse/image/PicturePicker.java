package com.vas2nets.fuse.image;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class PicturePicker {
	
	public static final int CAMERA_REQUEST = 100;
	public static final int GALLERY_PICTURE = 101;
	private Intent pictureActionIntent = null;
	private Activity activity;
	
	public PicturePicker(Activity a){
		activity = a;
	}
	
	public void startDialog() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(activity);
		myAlertDialog.setTitle("Pictures Option");
		myAlertDialog
				.setMessage("How do you want to set your profile picture?");
		myAlertDialog.setPositiveButton("Gallery",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						pictureActionIntent = new Intent(
								Intent.ACTION_GET_CONTENT, null);
						pictureActionIntent.setType("image/*");
						pictureActionIntent.putExtra("return-data", true);
						activity.startActivityForResult(pictureActionIntent,
								GALLERY_PICTURE);
					}
				});

		myAlertDialog.setNegativeButton("Camera",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						pictureActionIntent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						activity.startActivityForResult(pictureActionIntent,
								CAMERA_REQUEST);

					}
				});

		myAlertDialog.show();

	}

}
