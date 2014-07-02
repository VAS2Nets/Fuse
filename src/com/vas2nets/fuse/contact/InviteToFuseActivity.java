package com.vas2nets.fuse.contact;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.vas2nets.fuse.R;

public class InviteToFuseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_to_fuse);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.invite_to_fuse, menu);
		return true;
	}

}
