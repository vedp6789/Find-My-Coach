package com.findmycoach.app.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prem on 9/3/15.
 */
public class TimePickerFragment extends TimePickerDialog implements TimePicker.OnTimeChangedListener {

    private final static int TIME_PICKER_INTERVAL = 15;
    public boolean isMinTimeEnabled;
    private int minHour, minMinute;

    public TimePickerFragment(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
        isMinTimeEnabled = false;
        this.minHour = hourOfDay;
        this.minMinute = minute;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if (isMinTimeEnabled) {
            if (hourOfDay < minHour || (hourOfDay == minHour && minute < minMinute)) {
                updateTime(minHour, minMinute);
            }
        } else {
            if (hourOfDay == 23 && minute > 2) {
                updateTime(23, 2);
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            TimePicker timePicker = (TimePicker) findViewById(timePickerField
                    .getInt(null));
            Field field = classForid.getField("minute");

            NumberPicker mMinuteSpinner = (NumberPicker) timePicker
                    .findViewById(field.getInt(null));
            mMinuteSpinner.setMinValue(0);
            mMinuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            mMinuteSpinner.setDisplayedValues(displayedValues
                    .toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
