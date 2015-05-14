package com.findmycoach.app.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.findmycoach.app.activity.PaymentDetailsActivity;

import java.util.Calendar;

/**
 * Created by prem on 9/3/15.
 */
public class DatePickerFragment  extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    public DatePickerFragment(){
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
        String mon = getMonth(month);

        if(mon != null)
            PaymentDetailsActivity.inputCardExpiry.setText(mon + " - " + year);
        else
            PaymentDetailsActivity.inputCardExpiry.setText((month + 1) + " - " + year);
    }

    public static String getMonth(int month){
        switch (month){
            case 0: return "Jan";
            case 1: return "Feb";
            case 2: return "Mar";
            case 3: return "Apr";
            case 4: return "May";
            case 5: return "Jun";
            case 6: return "Jul";
            case 7: return "Aug";
            case 8: return "Sep";
            case 9: return "Oct";
            case 10: return "Nov";
            case 11: return "Dec";
        }

        return null;
    }
}
