package com.example.carpool;

import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ChooseYourSide extends Activity {

	// Initializing xml-elements

	Button a_user;
	Button b_user;
	String toastMessage = "nope";
	String username = "";
	String user1 = null;
	String startplace;
	String endplace;
	String drivername;
	String action;
	String user1name = "";

	
	//Handler for receiving data from threads
	//In this case userdata for confirming ride
	
	public Handler uiHandler = new Handler() {

		public void handleMessage(Message msg) {

			// Receives the message
			Bundle bundle = msg.getData();
			user1 = bundle.getString("user1");

			
			//Makes sure we have received right userdata
			if (user1 != null) {
				JSONArray user1Array;
				try {
					user1Array = new JSONArray(user1);
					Log.d("", "LOGGINGSTUFF user1A: " + user1Array);

					JSONObject c = user1Array.getJSONObject(0);

					Log.d("", "LOGGINGSTUFF user objekti: " + c);

					user1name = c.getString("firstname") + " "
							+ c.getString("lastname");
					String user1email = c.getString("email");
					String user1phone = c.getString("phone");

					Log.d("", "LOGGINGSTUFF user1name: " + user1name);
					
					//Starts methods for sending email and SMS messages

					sendEmail(username, user1email);
					sendSMS(username, user1phone);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d("", "LOGGINGSTUFF eksepsioni " + e);
				}

			}
		}
	};
	
	//Method for sending email

	public void sendEmail(String user1stuff, String user1email) {

		String route = startplace + " - " + endplace;

		
		//Passes information to thread that connects to PHP-mailer on the server 
		Thread mailthread = new Thread(new ThreadEmail(route, user1email,
				user1stuff, action, user1name));
		mailthread.start();
	}

	
	//Method for sending SMS-messages
	
	public void sendSMS(String username, String userphone) {
		
		//Defines what message to send
		//Either "new passenger" or "you have been accepted" message
		try {
			String message = "";
			if (action.equals("passenger")) {
				message = getResources().getString(R.string.textMsg_1) + " "
						+ startplace + "-" + endplace + " "
						+ getResources().getString(R.string.textMsg_2)+" "
						+ username;
			} else {
				message = getResources().getString(R.string.textMsg_4) + " "
						+ startplace + " - " + endplace;
			}
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(userphone, null, message, null, null);
		} catch (Exception e) {
			Log.d("LOGGING", "LOGGINGSTUFF SMS error" + e);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_your_side);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Assigning values to variables

		a_user = (Button) findViewById(R.id.a_user);
		b_user = (Button) findViewById(R.id.b_user);

		// Handling the intent, gets username
		Intent intent = getIntent();
		username = intent.getStringExtra("username");

		// Checks if intent came from map, toasts the result

		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (extras.containsKey("message")) {
				toastMessage = intent.getStringExtra("message");
				Toasting(toastMessage);
			}
			if (extras.containsKey("action")) {
				action = intent.getStringExtra("action");
				startplace = intent.getStringExtra("startpoint");
				endplace = intent.getStringExtra("endpoint");
				drivername = intent.getStringExtra("driverstuff");

				Thread t = new Thread(new ThreadGetUserInfo(uiHandler,
						drivername));
				t.start();
			}
		}

		// Button listeners

		a_user.setOnClickListener(new View.OnClickListener() {
			// Starts method for A-user

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showEnterInputs(v);
				// toastMessage = "nope";
			}
		});

		b_user.setOnClickListener(new View.OnClickListener() {
			// Starts method for B-user
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showLiftsAvailable(v);
				toastMessage = "nope";
			}
		});
	}

	// Method for toasting
	public void Toasting(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	// Method for going to A-users activities
	public void showEnterInputs(View view) {
		Intent intent = new Intent(this, AddToMapActivity.class);
		intent.putExtra("username", username);
		// Always passes the username with intent
		startActivity(intent);
	}

	// Method for going to B-users activities
	public void showLiftsAvailable(View view) {
		Intent intent = new Intent(this, LiftsAvailableActivity.class);
		intent.putExtra("username", username);
		// Always passes the username with intent
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent goBack = new Intent(this, LoginActivity.class);
		NavUtils.navigateUpTo(this, goBack);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_your_side, menu);
		return true;
	}
}
