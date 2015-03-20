package com.findmycoach.app.fragment_mentor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.activity.ScheduleYourVacation;

import java.util.Calendar;

/**
 * Created by praka_000 on 3/3/2015.
 */
public class StartDateForVaccationSchedule extends DialogFragment implements View.OnClickListener {
    public static DatePicker datePicker;
    Button b_ok, b_can;

    ScheduleYourVacation scheduleYourVacation;
    Calendar calendar;
    long time;

    private static final String selected_date = null;

    private static final String TAG = "FMC";


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.b_ok:
                String day = String.valueOf(datePicker.getDayOfMonth());
                String month = String.valueOf(datePicker.getMonth() + 1);
                String year = String.valueOf(datePicker.getYear());
                dismiss();

                scheduleYourVacation.setSelectedStartDate(day, month, year);
                break;
            case R.id.b_cancel:
                dismiss();
                break;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleYourVacation =new ScheduleYourVacation();
        calendar = Calendar.getInstance();
        Log.d(TAG, "Current year:" + calendar.get(Calendar.YEAR));
        Log.d(TAG, "Current month:" + calendar.get(Calendar.MONTH));
        Log.d(TAG, "Current day:" + calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        time = calendar.getTimeInMillis();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_date_fragment, container, false);
        datePicker = (DatePicker) view.findViewById(R.id.slotdatePicker);
        b_ok = (Button) view.findViewById(R.id.b_ok);
        b_can = (Button) view.findViewById(R.id.b_cancel);
        datePicker.setMinDate(time);

        b_ok.setOnClickListener(this);
        b_can.setOnClickListener(this);
        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.date_picker_from));
        dialog.setCanceledOnTouchOutside(false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
