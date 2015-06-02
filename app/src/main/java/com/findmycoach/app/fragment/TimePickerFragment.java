package com.findmycoach.app.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by prem on 9/3/15.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public TextView textView;

    public TimePickerFragment(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, min, false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int hour = hourOfDay % 12;
        if(textView != null) {
            textView.setText(hour + ":" + minute + (hourOfDay > 11 ? " pm" : " am"));
            textView.setTag(textView.getId(), hourOfDay + ":" + minute);
        }
    }
}
