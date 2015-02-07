package com.example.carpool;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {
	
	//Activity for registering new user
	
	private EditText fName;
	private EditText lName;
	private EditText pWord;
	private EditText uName;
	private EditText eMail;
	private EditText phone;
	private Button reg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//Variables for XML-elements and values
		
		fName = (EditText)findViewById(R.id.reg_firstname);
		lName = (EditText)findViewById(R.id.reg_lastname);
		pWord = (EditText)findViewById(R.id.password);
		uName = (EditText)findViewById(R.id.reg_username);
		reg = (Button)findViewById(R.id.btnRegister);
		eMail = (EditText)findViewById(R.id.reg_email);
		phone = (EditText)findViewById(R.id.reg_phone);
		
		reg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Clicking "register. Validates fields
				
				int fNameLength = fName.getText().toString().length();
				int lNameLength = lName.getText().toString().length();
				int pWordLength = pWord.getText().toString().length();
				int eMailLength = eMail.getText().toString().length();
				int phoneLength = phone.getText().toString().length();
				
				boolean emailCheck = checkEmail((CharSequence) eMail.getText().toString());
				
				if (fNameLength == 0 || lNameLength == 0 || pWordLength == 0 || eMailLength == 0 || phoneLength == 0 || emailCheck == false){
					Toasting("Fill every field");
				}else{
				Thread t = new Thread(new ThreadRegister(uName.getText().toString(), fName.getText().toString(),
						lName.getText().toString(), eMail.getText().toString(), phone.getText().toString(), pWord.getText().toString()));
				t.start();
				finish();
				}
			}
		});
	}
	
	
	//Validator for email
	public boolean checkEmail(CharSequence target){
		return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}
	
	
	//Method for toasting
	public void Toasting(String text){
	    Toast.makeText(this, text,Toast.LENGTH_LONG).show();	
	    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	 Intent goBack = new Intent(this, LoginActivity.class);
	 NavUtils.navigateUpTo(this, goBack);
      return true;
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

}
