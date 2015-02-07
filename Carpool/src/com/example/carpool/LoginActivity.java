package com.example.carpool;

import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	// initializing xml-elements

	private EditText username;
	private EditText passu;
	private Button loginBtn;
	ActionBar actionBar;
	private Button registerBtn;
	String jsondump = "";
	private ProgressDialog progress;

	public Handler uiHandler = new Handler() {

		public void handleMessage(Message msg) {

			// Receives the message
			Bundle bundle = msg.getData();
			jsondump = bundle.getString("json");
			Log.d("", "LOGGINGSTUFF JSONDUMP" + jsondump);

			// Checks if response was valid JSON

			if (jsondump.length() > 8) {

				progress.hide();
				sendIntent();

			} else {
				progress.hide();
				Log.d("", "LOGGINGSTUFF TULI FAILED");

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Values for XML-elements
		

		
		setContentView(R.layout.activity_login);
		progress = new ProgressDialog(this, R.style.progressDialog);
		actionBar = getActionBar();
		
		username = (EditText) findViewById(R.id.username);
		passu = (EditText) findViewById(R.id.password);
		loginBtn = (Button) findViewById(R.id.sign_in_button);
		registerBtn = (Button) findViewById(R.id.register);
		
		// Go to register activity

		registerBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goToRegAct();
			}
		});
		// Send button click

		loginBtn.setOnClickListener(new View.OnClickListener() {

			// Input validation by length
			@Override
			public void onClick(View v) {

				Thread t = new Thread(new ThreadCheckLogin(uiHandler, username
						.getText().toString(), passu.getText().toString()));
				t.start();
				open();

			}
		});
	}

	// Method for navigating to register Act

	public void open() {
		progress.setMessage(getResources().getString(R.string.auth));
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.show();
	}

	public void goToRegAct() {

		Intent i = new Intent(this, Register.class);

		startActivity(i);
	}

	// Method for navigating to next Activity

	public void sendIntent() {

		// Creates new intent, passes string variables as Extras

		Intent intent = new Intent(this, ChooseYourSide.class);
		String logMessage1 = username.getText().toString();
		String logMessage2 = passu.getText().toString();
		intent.putExtra("username", logMessage1);
		intent.putExtra("passu", logMessage2);

		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Method for toasting messages, accepts String variable

	public void Toasting(String text) {
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
