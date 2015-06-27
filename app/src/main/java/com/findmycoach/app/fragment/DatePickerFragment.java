package com.findmycoach.app.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.MentorDetailsActivity;
import com.findmycoach.app.activity.PaymentDetailsActivity;
import com.findmycoach.app.util.StorageHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by prem on 9/3/15.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    public DatePickerFragment() {
    }

    public TextView textView;
    private String for_which_activity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        for_which_activity = getArguments().getString("for");

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (textView != null) {
            String date = textView.getText().toString();
            year = Integer.parseInt(date.split(" ")[1]);
            month = getMonth(date.split(" ")[0].trim());
        }


        DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
        dpd.getDatePicker().setMinDate(c.getTimeInMillis());
        ((ViewGroup) dpd.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);

        // Create a new instance of DatePickerDialog and return it
        return dpd;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        String mon = getMonth(month);

        try {
            if (textView == null) {
                if (mon != null)
                    PaymentDetailsActivity.inputCardExpiry.setText(mon + " - " + year);
                else
                    PaymentDetailsActivity.inputCardExpiry.setText((month + 1) + " - " + year);
            } else {
                if(for_which_activity.equals("MyScheduleFragment")){
                    MyScheduleFragment.month_from_dialog = month + 1;
                    MyScheduleFragment.year_from_dialog = year;
                    if (mon != null)
                        textView.setText(mon + " " + year);
                    else
                        textView.setText((month + 1) + " " + year);
                    if(StorageHelper.getUserGroup(getActivity(),"user_group").equals("2")) {
                        MyScheduleFragment.myScheduleFragment.getCalendarDetailsForMentee();
                    }
                    else {
                        MyScheduleFragment.myScheduleFragment.getCalendarDetailsAPICall();
                    }
                }else{
                    if(for_which_activity.equals("MentorDetailsActivity")){
                        MentorDetailsActivity.month_from_dialog = month + 1;
                        MentorDetailsActivity.year_from_dialog = year;
                        if (mon != null)
                            textView.setText(mon + " " + year);
                        else
                            textView.setText((month + 1) + " " + year);
                    }
                    if(MentorDetailsActivity.mentorDetailsActivity != null){
                        MentorDetailsActivity.mentorDetailsActivity.getCalendarDetailsAPICall();

                    }
                }

            }
        } catch (Exception ignored) {
        }

    }

    public static String getMonth(int month) {
        switch (month) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
        }

        return null;
    }

    public int getMonth(String month) {
        ArrayList<String> months = new ArrayList<>();
        Collections.addAll(months, getActivity().getResources().getStringArray(R.array.months));
        return months.indexOf(month);
    }
}
