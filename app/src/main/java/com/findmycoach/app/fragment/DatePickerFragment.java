package com.findmycoach.app.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by prem on 9/3/15.
 */
public class DatePickerFragment  extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    Context context;
    EditText inputDate;

    public DatePickerFragment(Context context, EditText inputDate){
        this.context = context;
        this.inputDate = inputDate;
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
        String mon = null;
        switch (month){
            case 0: mon = "Jan"; break;
            case 1: mon = "Feb"; break;
            case 2: mon = "Mar"; break;
            case 3: mon = "Apr"; break;
            case 4: mon = "May"; break;
            case 5: mon = "Jun"; break;
            case 6: mon = "Jul"; break;
            case 7: mon = "Aug"; break;
            case 8: mon = "Sep"; break;
            case 9: mon = "Oct"; break;
            case 10: mon = "Nov"; break;
            case 11: mon = "Dec"; break;
        }
        if(mon != null)
            inputDate.setText(mon + " - " + year);
        else
            inputDate.setText((month + 1) + " - " + year);
    }
}
