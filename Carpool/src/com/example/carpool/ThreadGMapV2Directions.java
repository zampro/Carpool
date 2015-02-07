package com.example.carpool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//CLASS FOR ACCESSING GOOGLE DIRECTIONS API!

public class ThreadGMapV2Directions implements Runnable {
	public final static String MODE_DRIVING = "driving";
	public final static String MODE_WALKING = "walking";
	private String end;
	private String start;
	private String apikey = "AIzaSyB2Dd32l-rcVUEc4lhelcecSoT5KUMO0Bo";
	public Handler ui;
	private String pMarkerS = "";

	// Constructor for routes without Passenger-marker
	public ThreadGMapV2Directions(String startPlace, String endPlace, Handler UI) {
		this.start = startPlace.replaceAll("\\s", "+");
		this.end = endPlace.replaceAll("\\s", "+");
		this.ui = UI;
	}

	// Construction for routes with Passenger-marker
	public ThreadGMapV2Directions(String startPlace, String endPlace, Handler UI,
			String pMarker) {
		this.start = startPlace.replaceAll("\\s", "+");
		this.end = endPlace.replaceAll("\\s", "+");
		this.ui = UI;
		this.pMarkerS = pMarker;

	}

	// Method for getting JSON-response from API
	public String getJSON(String address) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(address);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {

			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	public void run() {

		// Method which is run when thread is started

		// Defining which URL to use

		String url;
		if (pMarkerS.equals("")) {

			// One with passenger-marker
			url = "https://maps.googleapis.com/maps/api/directions/json?"
					+ "origin=" + start + "&destination=" + end + "&key="
					+ apikey;
			Log.d("LOGGINGSTUFF", "LOGGINGSTUFF Mentiin normihandlaamiseen");
		} else {
			// One for ordinary routes without passenger-marker
			url = "https://maps.googleapis.com/maps/api/directions/json?"
					+ "origin=" + start + "&destination=" + end
					+ "&waypoints=via:" + pMarkerS + "&key=" + apikey;
		}

		// Calls getJSON with the url to get the json-dump

		String readJSON = getJSON(url);
		JSONObject json;
		try {
			json = new JSONObject(readJSON);
			String status = json.getString("status");
			if (status.equals("OK")) {
				try {

					//Passes the received JSON-dump to a message, which is send to main thread
					Message msg;
					msg = ui.obtainMessage();
					Bundle b = new Bundle();
					b.putString("json", readJSON);
					msg.setData(b);
					ui.sendMessage(msg);

				} catch (Exception e) {
					Log.d("LOGGINGSTUFF",
							"LOGGINGSTUFF GMapsV2Directions mokas vitust, tsori: "
									+ e);
				}
			} else {
				try {
					//Different message if JSON-failed.
					Message msg;
					msg = ui.obtainMessage();
					Bundle b = new Bundle();
					b.putString("json", "Failed");
					msg.setData(b);
					ui.sendMessage(msg);

				} catch (Exception e) {
					Log.d("LOGGINGSTUFF",
							"LOGGINGSTUFF GMapsV2Directions mokas vitust, tsori: "
									+ e);
				}
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
