package com.example.carpool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DeleteFragment extends DialogFragment{
	
	public String sid;

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.dodelete)
	               .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   dialog.dismiss();
	                   }
	               })
	               .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // SULJE DIALOGI!
	                       sid = AddToMapActivity.sid;
	                       //AVAA THREADI!
	                       Thread t = new Thread(new ThreadDeleteRoute(sid));
	                       t.start();
	                       refresh();
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	   
	    public void refresh(){
	    	Intent intent = new Intent();
            intent.setClass(getActivity(), AddToMapActivity.class);
            intent.putExtra("progress_delete", "DeleteFragment");
            intent.putExtra("username", AddToMapActivity.staticusername);
            startActivity(intent);
	    }
}
