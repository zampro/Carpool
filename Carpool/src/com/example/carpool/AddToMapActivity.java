package com.example.carpool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class AddToMapActivity extends Activity {

	// Initializing xml-elements & other variables

	AutoCompleteTextView start;
	AutoCompleteTextView end;
	String username = "";
	TextView date;
	Button submit;
	String toastMessage = "";
	TextView hello;
	public ArrayList<HashMap<String, String>> listdata = new ArrayList<HashMap<String, String>>();
	private String jsondump = "";
	ListView list;
	ListAdapter adapter;
	private ProgressDialog progress;
	TextView yourRoutes;
	Button pickTime;
	Button pickDate;
	public String minute = "";
	public String hour = "";
	public String day = "";
	public String month = "";
	public String year = "";
	static String sid;
	android.app.FragmentManager fm = getFragmentManager();
	static String staticusername;

	// Handler for receiving messages from networking threads (eg. databases and
	// routes)

	public Handler uiHandler = new Handler() {

		public void handleMessage(Message msg) {

			// Tries to receive the data
			Bundle bundle = msg.getData();
			jsondump = bundle.getString("json");

			try {
				// Attempts to create JSONArray object from the data
				JSONArray jArray = new JSONArray(jsondump);

				if (jArray != null) {

					// Handling the JSONarray, gets necessary values
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject c = jArray.getJSONObject(i);
						String start = c.getString("StartPlace");
						String end = c.getString("EndPlace");
						String pID = c.getString("PID");
						String dates = "Date: "+c.getString("DriveDateTime");
						String passengername = c.getString("PassengerName");
						if(passengername.equals("false")){
							passengername = "No passenger";
						}
						String passengerMarker = c.getString("PassengerMarker");
						// Creates hashmap with the values
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("StartPlace", start);
						map.put("EndPlace", end);
						map.put("DriveDateTime", dates);
						map.put("PassengerName", passengername);
						map.put("PID", pID);
						map.put("PassengerMarker", passengerMarker);
						listdata.add(map);

						// Hide progress dialog
						progress.hide();

						// Creates new list from the JSONData

						list.setAdapter(adapter);
						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							// Makes the listobject clickable
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								String starttimestat = listdata.get(+position)
										.get("StartPlace");
								String endimestat = listdata.get(+position)
										.get("EndPlace");
								String passengerStuff = listdata.get(+position)
										.get("PassengerName");
								String pId = listdata.get(+position).get("PID");
								String passengerMarker = listdata
										.get(+position).get("PassengerMarker");
								String mista = "myRouteA";
								// Passes values from listobject to click method
								listItemClicked(view, starttimestat,
										endimestat, mista, pId, passengerMarker, passengerStuff);
							}
						});
						
						
						//Listener for deleting route
						//Creates new dialog -> essentially "yes" or "no"
						list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
							public boolean onItemLongClick(AdapterView<?> parent,
									View view, int position, long id) {
								
								sid = listdata.get(+position).get("PID");
								Log.d("","LOGGINGSTUFF LISTA LONGKLIKKI!");
								DeleteFragment frag = new DeleteFragment();
								// Show DialogFragment
								frag.show(fm, "Dialog Fragment");
		                        
								return true;
								
								
							}
						});
						
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				progress.hide();
				yourRoutes.setText("No Active Routes");
				e.printStackTrace();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_to_map);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Assigning values to variables
		

		Calendar c = Calendar.getInstance();
		yourRoutes = (TextView) findViewById(R.id.your_routes);
		progress = new ProgressDialog(this, R.style.progressDialog);
		start = (AutoCompleteTextView) findViewById(R.id.start);
		end = (AutoCompleteTextView) findViewById(R.id.end);
		hello = (TextView) findViewById(R.id.hello_driver);
		start.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
		end.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
		list = (ListView) findViewById(R.id.your_list);
		adapter = new SimpleAdapter(AddToMapActivity.this, listdata,
				R.layout.location_listview, new String[] { "StartPlace",
						"EndPlace", "PID", "PassengerName", "DriveDateTime" }, new int[] { R.id.startPlace,
						R.id.endPlace, R.id.pId, R.id.driverName, R.id.driveDateTime });

		// Receives the intent with the username

		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		staticusername = username;
		// Prints out the username
		hello.setText(getResources().getString(R.string.hello)+ " " + username);

		// Call the progress dialog
		open();
		// Starts new thread to receive the last routes of the user

		Thread t = new Thread(new ThreadGetAllRoutes(uiHandler, username));
		t.start();

		// Check if the intent came from the map activity, toast the result

		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (extras.containsKey("message")) {
				toastMessage = intent.getStringExtra("message");
				Toasting(toastMessage);
			}
		}

		// Click method for showing Time Dialog
		hour = ""+c.get(Calendar.HOUR_OF_DAY);
		minute = ""+c.get(Calendar.MINUTE);
		pickTime = (Button) findViewById(R.id.pick_time);
		pickTime.setText(hour + "." + minute);
		pickTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showTimePickerDialog(v);
			}
		});

		// Click method for showing Date Dialog

		year = ""+c.get(Calendar.YEAR);
		month = ""+c.get(Calendar.MONTH);
		day = ""+c.get(Calendar.DAY_OF_MONTH);
		pickDate = (Button) findViewById(R.id.pick_date);
		pickDate.setText(day + "." + month + "." + year);
		pickDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDatePickerDialog(v);
			}
		});

		// Click method for submitting & input validation

		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (start.length() < 3 || end.length() < 3) {
					Toasting("Insert valid addresses");
				} else {
					sendMessage(v);
				}
			}
		});
	}

	// Method for showing progress dialog

	public void open() {
		progress.setMessage(getResources().getString(R.string.progress_2));
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.show();
	}

	// Method for showing TimePicker
	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment(this);
		newFragment.show(getFragmentManager(), "timePicker");
	}

	// Method for showing DatePicker
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment(this);
		newFragment.show(getFragmentManager(), "datePicker");
	}

	// Method for passing intents.

	public void sendMessage(View view) {
		Intent intent = new Intent(this, GmapsActivity.class);

		// Puts necessary variables as extras into intent

		hour = checkLength(hour);
		minute = checkLength(minute);
		//month = checkLength(month);
		//day = checkLength(day);

		String date = day + "." + month + "." + year + " - " + hour + "."
				+ minute;
		Log.d("", "LOGGINGSTUFF date: " + date);
		String message1 = start.getText().toString();
		String message2 = end.getText().toString();
		String message3 = "AddToMapActivity";
		String message4 = username;
		intent.putExtra("startPoint", message1);
		intent.putExtra("endPoint", message2);
		intent.putExtra("fromWhere", message3);
		intent.putExtra("username", message4);
		intent.putExtra("date", date);

		// Passes intent to GMapsActivity (The Map)

		startActivity(intent);
	}

	// Add "0"
	public String checkLength(String j) {
		if (j.length() < 2) {
			j = "0" + j;
		}
		return j;
	}

	// Click method for all list objects, passed data with intent

	public void listItemClicked(View view, String start, String end,
			String fromWhere, String Id, String passengerMarker, String passengerStuff) {
		Intent intent = new Intent(this, GmapsActivity.class);
		intent.putExtra("startPoint", start);
		intent.putExtra("endPoint", end);
		intent.putExtra("fromWhere", fromWhere);
		intent.putExtra("PID", Id);
		intent.putExtra("username", username);
		intent.putExtra("passengerMarker", passengerMarker);
		intent.putExtra("passengerStuff", passengerStuff);
		startActivity(intent);
	}

	// Toasting method

	public void Toasting(String text) {
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_route, menu);
		return true;
	}
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
		 Intent goBack = new Intent(this, ChooseYourSide.class);
		 goBack.putExtra("username", username);
		 NavUtils.navigateUpTo(this, goBack);
	      return true;
	    }
}
