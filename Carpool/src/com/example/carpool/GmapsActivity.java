package com.example.carpool;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.carpool.ThreadAddRoute;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

@SuppressLint("HandlerLeak")
public class GmapsActivity extends Activity implements OnMapClickListener,
		OnMapLongClickListener, OnMarkerDragListener {

	// CLASS FOR HANDLING GOOGLE MAPS -ACTIONS!

	// Initializing variables & xml-elements

	String action = "";
	String fromWhere = "";
	Button send;
	String startPoint = "";
	String endPoint = "";
	String date = "";
	private String jsondump = "";
	Marker marker;
	LatLng passengerMarker;
	String passengerName;
	String pId = "";
	GoogleMap map;
	String username;
	String pMarker = "";
	private ProgressDialog progress;
	String user1 = null;
	String drivername = "";
	String passengerStuff = "";

	// Handler for receiving messages from networking threads (JSON & map-stuff)

	public Handler uiHandler = new Handler() {

		public void handleMessage(Message msg) {

			// Receives the message
			Bundle bundle = msg.getData();
			jsondump = bundle.getString("json");
			user1 = bundle.getString("user1");
			
			Log.d("","LOGGINGSTUFF user1: "+user1);
			
			// Checks if response was valid JSON

			if (jsondump.equals("Failed")) {
				pushIntent("Invalid response status, check adresses",
						"AddToMapActivity");
				// Returns to previous activity

			} else {

				try {

					// Handling the JSON-data.
					JSONObject json = new JSONObject(jsondump);
					JSONArray routeArray = json.getJSONArray("routes");
					JSONObject routes = routeArray.getJSONObject(0);
					JSONObject overviewPolylines = routes
							.getJSONObject("overview_polyline");
					String encodedString = overviewPolylines
							.getString("points");

					// Getting the route legs

					JSONArray legs = routes.getJSONArray("legs");
					JSONObject legsobject = legs.getJSONObject(0);
					JSONObject startLocation = legsobject
							.getJSONObject("start_location");

					// Start and end locations latitudes and longitudes

					double encodedLat = startLocation.getDouble("lat");
					double encodedLng = startLocation.getDouble("lng");

					JSONObject endLocation = legsobject
							.getJSONObject("end_location");

					double encodedEndLat = endLocation.getDouble("lat");
					double encodedEndLng = endLocation.getDouble("lng");

					// Creating LatLng objects from lats and lngs

					LatLng startMarker = new LatLng(encodedLat, encodedLng);
					LatLng endMarker = new LatLng(encodedEndLat, encodedEndLng);

					// Decoding the polyline from Google Directions API -
					// Contains all waypoints

					List<LatLng> list = decodePoly(encodedString);

					progress.hide();
					// Starting drawRoute function for drawing the map
					drawRoute(list, startMarker, endMarker);

					// Checks if route contains passengermarker. If so, inserts
					// marker for passenger pick-up

					if (pMarker.equals("")) {
					} else {
						createPassengermarker(pMarker);
					}

				} catch (Exception e) {

				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gmaps_act);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Assigning values to variables

		progress = new ProgressDialog(this, R.style.progressDialog);
		Intent intent = getIntent();
		send = (Button) findViewById(R.id.send);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setOnMapLongClickListener(this);
		map.setOnMarkerDragListener(this);

		open(getResources().getString(R.string.progress_2));
		// Activity uses same button for multiple use cases
		// Checks where intent came from, assigns different functionality based
		// on that

		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// Intent came from A user
				// Starts new database-thread, adds coordinates to database
				if (fromWhere.equals("AddToMapActivity")) {
					Thread t = new Thread(new ThreadAddRoute(startPoint,
							endPoint, username, date));
					t.start();
					pushIntent("Route added", "ChooseYourSide");
					// Sends info message with the intent

					// Sending message to passenger from the driver
				} else if (fromWhere.equals("myRouteA")) {
					pushIntent("Passenger informed", "ChooseYourSide2");
				} else {
					passengerMarker = marker.getPosition();
					passengerName = username;
					Thread modifyroute = new Thread(new ThreadModifyRoute(
							passengerMarker, passengerName, pId));
					modifyroute.start();
					action = "passenger";
					
					pushIntent("Driver informed", "ChooseYourSide2");
					//sendEmail("miikka.repo@metropolia.fi", "miikka.repo@metropolia.fi");
				}
			}
		});

		// Gets values from intent.

		startPoint = intent.getStringExtra("startPoint");
		drivername = intent.getStringExtra("drivername");
		endPoint = intent.getStringExtra("endPoint");
		date = intent.getStringExtra("date");
		pId = intent.getStringExtra("PID");
		username = intent.getStringExtra("username");
		pMarker = intent.getStringExtra("passengerMarker");
		fromWhere = intent.getStringExtra("fromWhere");
		passengerStuff = intent.getStringExtra("passengerStuff");

		// Checks the "fromWhere" value of intent. Hides or shows send-button,
		// changes text

		if (fromWhere.equals("AddToMapActivity")) {

			// If intent came from A-user

			send.setVisibility(View.VISIBLE);
			Thread t2 = new Thread(new ThreadGMapV2Directions(startPoint,
					endPoint, uiHandler));
			t2.start();
		} else if (fromWhere.equals("myRouteA")) {

			// If intent came from A-users own routes
			if(passengerStuff.equals("No passenger")){
			send.setVisibility(View.GONE);
			}else{
				send.setVisibility(View.VISIBLE);
			}
			send.setText(getResources().getString(R.string.accept));
			Log.d("LOGGINGSTUFF", "LOGGINGSTUFF Aloitetaan threadi 2");
			Thread t2 = new Thread(new ThreadGMapV2Directions(startPoint,
					endPoint, uiHandler, pMarker));
			t2.start();
		} else if (fromWhere.equals("myRouteB")) {

			// If intent came from A-users own routes

			passengerStuff = username;
			send.setVisibility(View.GONE);
			Thread t2 = new Thread(new ThreadGMapV2Directions(startPoint,
					endPoint, uiHandler, pMarker));
			t2.start();
		} else {

			// If intent came from B-user

			send.setVisibility(View.GONE);
			Thread t2 = new Thread(new ThreadGMapV2Directions(startPoint,
					endPoint, uiHandler));
			t2.start();

		}
	}

	

	// Method for showing progress dialog
	public void open(String msg) {
		progress.setMessage(msg);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Method for pushing intents to A & B user activities.

	private void pushIntent(String s, String s2) {
		
		if (s2.equals("AddToMapActivity")) {
			Intent intent3 = new Intent(this, AddToMapActivity.class);
			intent3.putExtra("message", s);
			intent3.putExtra("username", username);
			startActivity(intent3);
		} else if(s2.equals("ChooseYourSide2")) {
			Intent intent3 = new Intent(this, ChooseYourSide.class);
			intent3.putExtra("message", s);
			if(fromWhere.equals("myRouteA")){
				Log.d("LOGGINGSTUFF","LOGGINGSTUFF (if fromwhere = myRouteA)");
				intent3.putExtra("username", username);
				intent3.putExtra("driverstuff", passengerStuff);
				intent3.putExtra("action", "shitfuckasstit");
			}else{
				Log.d("LOGGINGSTUFF","LOGGINGSTUFF (if fromWhere = else)");
				intent3.putExtra("username", username);
				intent3.putExtra("driverstuff", drivername);
				intent3.putExtra("action", "passenger");
			}
			intent3.putExtra("startpoint", startPoint);
			intent3.putExtra("endpoint", endPoint);
			startActivity(intent3);
		}else{
			Intent intent3 = new Intent(this, ChooseYourSide.class);
			intent3.putExtra("message", s);
			intent3.putExtra("username", username);
			startActivity(intent3);
		}
	}

	
	
	
	
	// Main method for drawing the Google Map and routes

	private void drawRoute(List<LatLng> list, LatLng start, LatLng end) {

		// Zooms map

		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 13));

		// Adds markers to start & end locations of the route

		map.addMarker(new MarkerOptions().title(startPoint)
				.snippet(getResources().getString(R.string.start)).position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));

		map.addMarker(new MarkerOptions().title(endPoint)
				.snippet(getResources().getString(R.string.end)).position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));

		for (int i = 0; i < list.size() - 1; i++) {
			LatLng src = list.get(i);
			LatLng dest = list.get(i + 1);

			// Draws the directions route from polyline

			map.addPolyline(new PolylineOptions()
					.add(new LatLng(src.latitude, src.longitude),
							new LatLng(dest.latitude, dest.longitude))
					.width(10).color(Color.RED));
		}
	}

	// Method for creating Passenger-marker (if one exists)
	private void createPassengermarker(String marker) {

		// Reforms the string into LatLng-object for the map

		String[] latlngit = marker.split(",");
		LatLng markkeri = new LatLng(Double.parseDouble(latlngit[0]),
				Double.parseDouble(latlngit[1]));

		map.addMarker(new MarkerOptions().title(getResources().getString(R.string.passengerMarker))
				.snippet(getResources().getString(R.string.passengerMarker)+" "+passengerStuff).position(markkeri).icon(BitmapDescriptorFactory.fromResource(R.drawable.pass)));

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(markkeri, 13));

	}

	// Method for decoding polylines. It's some weird google stuff.

	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0;
		int length = encoded.length();
		int latitude = 0;
		int longitude = 0;

		while (index < length) {
			int b;
			int shift = 0;
			int result = 0;

			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);

			int destLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			latitude += destLat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);

			int destLong = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			longitude += destLong;

			poly.add(new LatLng((latitude / 1E5), (longitude / 1E5)));
		}
		return poly;
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	// Function for B-user. Adding you own position to the map

	@Override
	public void onMapLongClick(LatLng point) {

		// Allows only one marker. Removes original if new one is added
		if (fromWhere.equals("LiftsAvailable") || fromWhere.equals("myRouteB")) {
			if (marker != null) {
				marker.remove();
			}
			marker = map.addMarker(new MarkerOptions().draggable(true)
					.position(point).title("Your location").icon(BitmapDescriptorFactory.fromResource(R.drawable.pass)));

			// Makes send button visible. You can only send point if there is
			// one
			send.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		marker.setTitle("" + arg0.getPosition());
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(fromWhere.equals("LiftsAvailable") || fromWhere.equals("myRouteB")){
			Intent goBack = new Intent(this, LiftsAvailableActivity.class);
			goBack.putExtra("username", username);
			NavUtils.navigateUpTo(this, goBack);
		    return true;
		}else{
			Intent goBack = new Intent(this, AddToMapActivity.class);
			goBack.putExtra("username", username);
			NavUtils.navigateUpTo(this, goBack);
		    return true;
		}
    }
}
