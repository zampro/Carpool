package com.example.carpool;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class ThreadDeleteRoute implements Runnable {
	
	//CLASS FOR DELETING ROUTES
	
	private String pId;
	private HttpResponse response;
	
	//Constructor for the class
	public ThreadDeleteRoute(String id){
		
		//Encodes variables for URL usage
		try {
			this.pId = URLEncoder.encode(id, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	//Method which is run when thread is started
	@Override
	public void run() {
		
		//Tries to connect into external PHP-page
		
		 try{
	            String link = "http://users.metropolia.fi/~samposi/carpool/deleteRoute.php?ID="+pId;
	            HttpClient client = new DefaultHttpClient();
	            HttpGet request = new HttpGet();
	            request.setURI(new URI(link));
	            response = client.execute(request);
	            
	            //The PHP does all database handling
	      
	      }catch(Exception e){
	    	  
	      }
	}
}
