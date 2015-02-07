package com.example.carpool;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

//CLASS FOR ADDING ROUTES TO DATABASE

public class ThreadAddRoute implements Runnable {

	private String end;
	private String start;
	private String driver;
	private String date;

	// Constructor for the class

	public ThreadAddRoute(String start, String end, String driver, String date) {

		// Encodes variables to URL-form
		try {
			this.start = URLEncoder.encode(start, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			this.end = URLEncoder.encode(end, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			this.date = URLEncoder.encode(date, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		try {
			this.driver = URLEncoder.encode(driver, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

	}

	// Method which is run when the thread is startes
	public void run() {

		try {

			// Connects to a PHP-file, PHP adds variables to database

			String link = "http://users.metropolia.fi/~samposi/carpool/addRoute2.php?StartPlace="
					+ start
					+ "&EndPlace="
					+ end
					+ "&DriverName="
					+ driver
					+ "&DriveDateTime=" + date;
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(link));
			Log.d("", "LOGGINGSTUFF" + link);
			client.execute(request);

		} catch (Exception e) {

		}

	}
}
