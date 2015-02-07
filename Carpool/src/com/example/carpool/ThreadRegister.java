package com.example.carpool;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

//CLASS FOR REGISTERING USER

public class ThreadRegister implements Runnable {
	
	private String userName;
	private String firstName;
	private String lastName;
	//private String phoneNumber;
	private String eMail;
	private String phone;
	private String passWord;

	// Constructor for the class

	public ThreadRegister(String userName, String firstName, String lastName, String eMail, String phone, String passWord) {

		// Encodes variables to URL-form
		try {
			this.userName = URLEncoder.encode(userName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			this.firstName = URLEncoder.encode(firstName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			this.lastName = URLEncoder.encode(lastName, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		try {
			this.eMail = URLEncoder.encode(eMail, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		try {
			this.phone = URLEncoder.encode(phone, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		try {
			this.passWord = URLEncoder.encode(passWord, "UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
	}

	// Method which is run when the thread is startes
	public void run() {

		try {

			// Connects to a PHP-file, PHP adds variables to database

			String link = "http://users.metropolia.fi/~samposi/carpool/registerUser.php?username="+userName+"&firstname="+firstName+"&lastname="+lastName+"&email="
					+eMail+"&phone="+phone+"&password="+passWord;
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(link));
			Log.d("", "LOGGINGSTUFF" + link);
			client.execute(request);

		} catch (Exception e) {

		}

	}
}
