package com.example.carpool;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.gms.maps.model.LatLng;

//CLASS FOR MODIFYING ROUTE IN DATABASE: ADDING PASSENGER-MARKER
public class ThreadModifyRoute implements Runnable {

	private String lat;
	private String lng;
	private String name;
	private String pId;
	private HttpResponse response;
	
	
	//Constructor for the class
	public ThreadModifyRoute(LatLng position, String name, String id){
		
		//Encodes variables for URL usage
		try {
			this.lat = URLEncoder.encode(""+position.latitude, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			this.lng = URLEncoder.encode(""+position.longitude, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			this.name = URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
	            String link = "http://users.metropolia.fi/~samposi/carpool/modifyRoute.php?PassengerMarker="+lat+","+lng+"&PassengerName="+name+"&ID="+pId;
	            HttpClient client = new DefaultHttpClient();
	            HttpGet request = new HttpGet();
	            request.setURI(new URI(link));
	            response = client.execute(request);
	            
	            //The PHP does all database handling
	            
	      
	      }catch(Exception e){
	      }
		
	}

}
