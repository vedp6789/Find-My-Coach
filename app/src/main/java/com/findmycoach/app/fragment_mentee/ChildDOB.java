package com.findmycoach.app.fragment_mentee;

import android.app.DatePickerDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.ScheduleNewClass;
import com.findmycoach.app.fragment.MyScheduleFragment;
import com.findmycoach.app.util.Callback;

import java.util.Calendar;

/**
 * Created by ved on 24/3/15.
 */
public class ChildDOB extends DialogFragment {
    DatePicker dp_child_dob;
    Button b_ok,b_cancel;
    public ScheduleNewClass scheduleNewClass;
    Calendar calendar;
    long time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        int current_year=calendar.YEAR;
        int current_month= Calendar.MONTH;
        calendar.set((current_year-100),current_month,1,0,0);

        time = calendar.getTimeInMillis();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.child_date_of_birth_dialog,container,false);
        dp_child_dob= (DatePicker) view.findViewById(R.id.dp_child_dob);
        dp_child_dob.setMinDate(time);
        b_cancel= (Button) view.findViewById(R.id.b_cancel);
        b_ok= (Button) view.findViewById(R.id.b_ok);
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day = String.valueOf(dp_child_dob.getDayOfMonth());
                String month = String.valueOf(dp_child_dob.getMonth() + 1);
                String year = String.valueOf(dp_child_dob.getYear());
                StringBuilder stringBuilder=new StringBuilder();
                stringBuilder.append(day);stringBuilder.append("/"+month);stringBuilder.append("/"+year);

                scheduleNewClass.child_DOB=stringBuilder.toString();

                dismiss();
            }
        });


        b_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.prompt_date_of_birth));
        dialog.setCanceledOnTouchOutside(true);

        return view;

    }


}
