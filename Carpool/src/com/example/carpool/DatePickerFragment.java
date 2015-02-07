package com.example.carpool;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	public AddToMapActivity act;

	public DatePickerFragment() {

	}

	public DatePickerFragment(AddToMapActivity act) {
		this.act = act;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
		act.pickDate.setText(day + "." + month + "." + year);
		act.day = "" + day++;
		act.month = "" + month++;
		act.year = "" + year;
	}
}
