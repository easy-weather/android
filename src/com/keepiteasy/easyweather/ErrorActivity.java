package com.keepiteasy.easyweather;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ErrorActivity extends Activity {
	private static TextView message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);
		
		message  = (TextView) findViewById(R.id.error_message);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    String error = extras.getString("error");
		    message.setText(error);
		}
	}
}
