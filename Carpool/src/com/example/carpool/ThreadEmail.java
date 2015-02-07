package com.example.carpool;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

//CLASS FOR SENDING EMAIL TO USERS

public class ThreadEmail implements Runnable {

	private String route;
	private String email;
	private String username;
	private String action;
	private String realname;

	// Constructor for the class

	public ThreadEmail(String route, String email, String username, String action, String realname) {

		// Encodes variables to URL-form
		try {
			this.route = URLEncoder.encode(route, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			this.email = URLEncoder.encode(email, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			this.username = URLEncoder.encode(username, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		try {
			this.action = URLEncoder.encode(action, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		
		try {
			this.realname = URLEncoder.encode(realname, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		
		

	}

	// Method which is run when the thread is startes
	public void run() {

		try {

			// Connects to a PHP-file

			String link = "http://users.metropolia.fi/~samposi/carpool/email.php?action="+action+"&route="+route+"&username="+username+"&email="+email+"&realname="+realname;
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(link));
			Log.d("", "LOGGINGSTUFF" + link);
			client.execute(request);

		} catch (Exception e) {

		}

	}
}
