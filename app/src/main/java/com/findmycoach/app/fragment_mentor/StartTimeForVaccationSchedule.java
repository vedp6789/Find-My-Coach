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
import android.widget.TimePicker;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.activity.ScheduleYourVacation;

import java.util.Calendar;

/**
 * Created by praka_000 on 3/3/2015.
 */
public class StartTimeForVaccationSchedule extends DialogFragment implements View.OnClickListener{
    public static TimePicker timePicker;
    Button b_ok, b_can;
    ScheduleYourVacation scheduleYourVacation;

    private int hour;
    private int minute;
    private static final String selected_date = null;

    private static final String TAG = "FMC";


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_ok:
                String hour = String.valueOf(timePicker.getCurrentHour());
                String minute = String.valueOf(timePicker.getCurrentMinute());


                dismiss();
                scheduleYourVacation.setSelectedStartTime(hour,minute);

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
        //timePicker.setCurrentHour(hour);
        //timePicker.setCurrentMinute(minute);

    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;

                    // set current time into textview
                    /*tvDisplayTime.setText(new StringBuilder().append(pad(hour))
                            .append(":").append(pad(minute)));*/

                    // set current time into timepicker
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
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
