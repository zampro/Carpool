package com.example.carpool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class LiftsAvailableActivity extends Activity implements
		OnItemSelectedListener {

	// Initializing variables

	public ArrayList<HashMap<String, String>> listdata = new ArrayList<HashMap<String, String>>();
	private String jsondump = "";
	private String username = "";
	private TextView hello;
	private TextView routes;
	private ProgressDialog progress;
	private Spinner spinner1;
	private Button btnSubmit;
	String selected = "";
	ListView list;
	ListAdapter adapter;
	private EditText searchBar;
	String[] lista = new String[4];

	// Handler for receiving messages from threads
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
						String driver = c.getString("DriverName");
						String date = c.getString("DriveDateTime");
						String passengerMarker = c.getString("PassengerMarker");
						// Creates hashmap with the values
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("StartPlace", start);
						map.put("EndPlace", end);
						map.put("PID", pID);
						map.put("DriverName", driver);
						map.put("DriveDateTime", "Date: "+date);
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
								String pId = listdata.get(+position).get("PID");
								String passengerMarker = listdata
										.get(+position).get("PassengerMarker");
								String mista = "myRouteB";
								String drivername = listdata.get(+position).get("DriverName");
								// Passes values from listobject to click method
								listItemClicked(view, starttimestat,
										endimestat, mista, pId, passengerMarker, drivername);
							}
						});
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				progress.hide();
				// yourRoutes.setText("No Active Routes");
				e.printStackTrace();
			}

		}
	};

	// Click method for all list objects, passed data with intent

	public void listItemClicked(View view, String start, String end,
			String fromWhere, String Id, String passengerMarker, String drivername) {
		Intent intent = new Intent(this, GmapsActivity.class);
		intent.putExtra("startPoint", start);
		intent.putExtra("endPoint", end);
		intent.putExtra("fromWhere", fromWhere);
		intent.putExtra("PID", Id);
		intent.putExtra("username", username);
		intent.putExtra("passengerMarker", passengerMarker);
		intent.putExtra("drivername", drivername);
		startActivity(intent);
	}

	// Method for going into Google Map with info from list-objects

	public void pushIntent(View view, String start, String end,
			String fromWhere, String Id) {
		Intent intent = new Intent(this, GmapsActivity.class);
		intent.putExtra("startPoint", start);
		intent.putExtra("endPoint", end);
		intent.putExtra("fromWhere", fromWhere);
		intent.putExtra("PID", Id);
		intent.putExtra("username", username);
		startActivity(intent);
	}

	// Method for showing progress dialog
	public void open() {
		progress.setMessage(getResources().getString(R.string.progress_2));
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lifts_available);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		//Variables for spinner-list, work as search criteria later on
		
		lista[0] = "Destination";
		lista[1] = "Starting place";
		lista[2] = "Driver";
		lista[3] = "Date";
		
		// Assigning values from intents etc.
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, lista);
		Spinner spin;
		
		spin = (Spinner)findViewById(R.id.spinner1);
		spin.setAdapter(spinnerAdapter);
		
		
		//Variables etc for lists
		addListenerOnButton();
		addListenerOnSpinnerItemSelection();
		searchBar = (EditText) findViewById(R.id.search);
		list = (ListView) findViewById(R.id.list);
		adapter = new SimpleAdapter(LiftsAvailableActivity.this, listdata,
				R.layout.location_listview, new String[] { "StartPlace",
						"EndPlace", "PID", "DriverName", "DriveDateTime" }, new int[] { R.id.startPlace,
						R.id.endPlace, R.id.pId, R.id.driverName, R.id.driveDateTime });
		
		//Variables for XML-elements
		hello = (TextView) findViewById(R.id.hello_passenger);
		routes = (TextView) findViewById(R.id.routes_selected);
		progress = new ProgressDialog(this, R.style.progressDialog);
		
		Intent intent = getIntent();
		//Greeting text
		username = intent.getStringExtra("username");
		hello.setText(getResources().getString(R.string.hello)+ " " + username);

		//Starts thread to fetch all routes from database
		open();
		Thread t = new Thread(new ThreadGetAllRoutes(username, uiHandler));
		t.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lifts_available, menu);
		return true;
	}

	// add items into spinner dynamically

	public void addListenerOnSpinnerItemSelection() {
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner1.setOnItemSelectedListener(this);
	}

	// get the selected dropdown list value
	public void addListenerOnButton() {

		spinner1 = (Spinner) findViewById(R.id.spinner1);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);

		btnSubmit.setOnClickListener(new View.OnClickListener() {
			// Starts method for A-user

			@Override
			public void onClick(View v) {
				
				//Defining title based on search criteria

				jsondump = "";
				listdata.clear();

				selected = spinner1.getSelectedItem().toString();
				String query = searchBar.getText().toString();
				if (selected.equals("Driver")) {
					routes.setText(query + "'s Routes");
				} else if (selected.equals("Starting place")) {
					routes.setText("Routes with startplace: " + query);
				} else if (selected.equals("Destination")) {
					routes.setText("Routes with destination: " + query);
				} else if (selected.equals("Date")) {
					routes.setText("Routes with date: " + query);
				} else {
					routes.setText("Dude/Dudette, we done fucked up");
				}
				
				//Opens new thread that fetches routes from Database

				open();
				Thread t = new Thread(new ThreadGetAllRoutes(uiHandler,
						selected, query));
				t.start();
			}
		});
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// An item was selected. You can retrieve the selected item using

	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	 Intent goBack = new Intent(this, ChooseYourSide.class);
	 goBack.putExtra("username", username);
	 NavUtils.navigateUpTo(this, goBack);
      return true;
    }
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
