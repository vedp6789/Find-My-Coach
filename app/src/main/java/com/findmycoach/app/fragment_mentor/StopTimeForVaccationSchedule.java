package com.findmycoach.app.fragment_mentor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.ScheduleYourVacation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by praka_000 on 3/3/2015.
 */
public class StopTimeForVaccationSchedule extends DialogFragment implements View.OnClickListener {
    public static TimePicker timePicker;
    Button b_ok, b_can;
    ScheduleYourVacation scheduleYourVacation;
    private int hour;
    private int minute;
    private static final String selected_date = null;

    private static final String TAG = "FMC";
    private final static int TIME_PICKER_INTERVAL = 15;



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_ok:
                String hour = String.valueOf(timePicker.getCurrentHour());

                int minute = timePicker.getCurrentMinute();
                String min = "";
                if (minute == 0)
                    min = "00";
                else if (minute == 1)
                    min = "15";
                else if (minute == 2)
                    min = "30";
                else if (minute == 3)
                    min = "45";

                dismiss();
                scheduleYourVacation.setSelectedTillTime(hour,min);

                break;
            case R.id.b_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleYourVacation = new ScheduleYourVacation();
        /*calendar = Calendar.getInstance();
        Log.d(TAG, "Current year:" + calendar.get(Calendar.YEAR));
        Log.d(TAG, "Current month:" + calendar.get(Calendar.MONTH));
        Log.d(TAG, "Current day:" + calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        time = calendar.getTimeInMillis();*/

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        /*// set current time into textview
        tvDisplayTime.setText(
                new StringBuilder().append(pad(hour))
                        .append(":").append(pad(minute)));*/

        // set current time into timepicker
        /*timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
*/
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    // hour = selectedHour;
                    // minute = selectedMinute;

                    // set current time into textview
                    /*tvDisplayTime.setText(new StringBuilder().append(pad(hour))
                            .append(":").append(pad(minute)));*/

                    // set current time into timepicker


                    final Calendar c = Calendar.getInstance();
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    minute = c.get(Calendar.MINUTE);

                    timePicker.setCurrentHour(hour);
                    timePicker.setCurrentMinute(minute);

                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_time_fragment, container, false);
        timePicker = (TimePicker) view.findViewById(R.id.slottimePicker);
        b_ok = (Button) view.findViewById(R.id.b_ok);
        b_can = (Button) view.findViewById(R.id.b_cancel);
        b_ok.setOnClickListener(this);
        b_can.setOnClickListener(this);
        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.set_time));
        dialog.setCanceledOnTouchOutside(false);

        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
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

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
