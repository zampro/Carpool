package com.example.carpool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//CLASS FOR RECEIVING JSON-DUMP OF ALL ROUTES IN DATABASE

public class ThreadGetAllRoutes implements Runnable {

	public Handler ui;
	public String username = "";
	public String passenger = "";
	public String criteria = "";
	public String query = "";

	// Constructor for ordinary usage (=getting all routes)
	public ThreadGetAllRoutes(Handler UI) {
		this.ui = UI;

	}

	// Constructor for spesific usage (=getting all routes of single driver)
	public ThreadGetAllRoutes(Handler UI, String username) {
		this.ui = UI;
		
		try {
			this.username = URLEncoder.encode(username, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	// Constructor for spesific usage (=getting all routes of single passenger)
		public ThreadGetAllRoutes(String passenger, Handler UI) {
			this.ui = UI;
			try {
				this.passenger = URLEncoder.encode(passenger, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		// Constructor for B-users searches
		public ThreadGetAllRoutes(Handler UI, String criteria, String query) {
			this.ui = UI;
			try {
				this.criteria = URLEncoder.encode(criteria, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				this.query = URLEncoder.encode(query, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	// Method for accessing external PHP page with JSON-dump
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

	// Method which is run when thread is started

	public void run() {

		// Defines which URL use
		String url;
		
		if(!username.equals("")){
			// Returns all routes of named driver
			url = "http://users.metropolia.fi/~samposi/carpool/showRoutes.php?username="
					+ username;
		}else if(!passenger.equals("")){
			// Returns all routes of named passenger
			url = "http://users.metropolia.fi/~samposi/carpool/showRoutes.php?passenger="
					+ passenger;
		}else if(!criteria.equals("")){
			// Returns routes by search criteria
			url = "http://users.metropolia.fi/~samposi/carpool/showRoutes.php?"+criteria+"="+query;
		}else{
			url = "http://users.metropolia.fi/~samposi/carpool/showRoutes.php";
		}
		

		// Calls method for receiving JSON
		String readJSON = getJSON(url);

		try {

			//Attempts to send message to main thread
			Message msg;

			msg = ui.obtainMessage();
			Bundle b = new Bundle();
			//Puts the JSON-dump into the message as extra
			b.putString("json", readJSON);
			msg.setData(b);
			ui.sendMessage(msg);

		} catch (Exception e) {
			Log.d("LOGGINGSTUFF",
					"LOGGINGSTUFF getAllRoutes mokas, tsori: " + e);
		}
	}

}
