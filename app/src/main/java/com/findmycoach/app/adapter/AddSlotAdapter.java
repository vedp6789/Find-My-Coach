package com.findmycoach.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.activity.ScheduleNewClass;
import com.findmycoach.app.activity.ScheduleYourVacation;

import java.util.List;

/**
 * Created by prem on 26/5/15.
 */
public class AddSlotAdapter extends BaseAdapter {

    private String[] days;
    private AddNewSlotActivity addNewSlotActivity;
    private ScheduleYourVacation scheduleYourVacation;
    private ScheduleNewClass scheduleNewClass;
    private List<Integer> selectedDays;

    public AddSlotAdapter(AddNewSlotActivity addNewSlotActivity, String[] days) {
        this.days = days;
        this.addNewSlotActivity = addNewSlotActivity;
    }

    public AddSlotAdapter(String[] days, ScheduleYourVacation scheduleYourVacation) {
        this.days = days;
        this.scheduleYourVacation = scheduleYourVacation;
    }

    public AddSlotAdapter(String[] days, List<Integer> selectedDays, ScheduleNewClass scheduleNewClass) {
        this.days = days;
        this.selectedDays = selectedDays;
        this.scheduleNewClass = scheduleNewClass;
    }


    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public Object getItem(int position) {
        return days[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = null;

        if (addNewSlotActivity != null) {

            LayoutInflater layoutInflater = (LayoutInflater) addNewSlotActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.screen_gridcell, null);
            final Button button = (Button) v.findViewById(R.id.calendar_day_gridcell);
            button.setTextSize(11f);
            button.setTypeface(null, Typeface.BOLD);
            button.setText(days[position]);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            Log.d("FMC","Mon clicked !");
                            addNewSlotActivity.boo_mon_checked = !addNewSlotActivity.boo_mon_checked;
                            updateView(button, addNewSlotActivity.boo_mon_checked);
                            break;
                        case 1:
                            Log.d("FMC","Tue clicked !");
                            addNewSlotActivity.boo_tue_checked = !addNewSlotActivity.boo_tue_checked;
                            updateView(button, addNewSlotActivity.boo_tue_checked);
                            break;
                        case 2:
                            Log.d("FMC","Wed clicked !");
                            addNewSlotActivity.boo_wed_checked = !addNewSlotActivity.boo_wed_checked;
                            updateView(button, addNewSlotActivity.boo_wed_checked);
                            break;
                        case 3:
                            Log.d("FMC","Thur clicked !");
                            addNewSlotActivity.boo_thurs_checked = !addNewSlotActivity.boo_thurs_checked;
                            updateView(button, addNewSlotActivity.boo_thurs_checked);
                            break;
                        case 4:
                            addNewSlotActivity.boo_fri_checked = !addNewSlotActivity.boo_fri_checked;
                            updateView(button, addNewSlotActivity.boo_fri_checked);
                            break;
                        case 5:
                            addNewSlotActivity.boo_sat_checked = !addNewSlotActivity.boo_sat_checked;
                            updateView(button, addNewSlotActivity.boo_sat_checked);
                            break;
                        case 6:
                            addNewSlotActivity.boo_sun_checked = !addNewSlotActivity.boo_sun_checked;
                            updateView(button, addNewSlotActivity.boo_sun_checked);
                            break;
                    }
                }
            });
        } else if (scheduleYourVacation != null) {

            LayoutInflater layoutInflater = (LayoutInflater) scheduleYourVacation.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.screen_gridcell, null);
            final Button button = (Button) v.findViewById(R.id.calendar_day_gridcell);
            button.setTextSize(11f);
            button.setTypeface(null, Typeface.BOLD);
            button.setText(days[position]);

            button.setBackgroundDrawable(scheduleYourVacation.getResources().getDrawable(R.drawable.scheduled_event_arrow));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            scheduleYourVacation.boo_mon_checked = !scheduleYourVacation.boo_mon_checked;
                            updateView(button, scheduleYourVacation.boo_mon_checked, "M");
                            break;
                        case 1:
                            scheduleYourVacation.boo_tue_checked = !scheduleYourVacation.boo_tue_checked;
                            updateView(button, scheduleYourVacation.boo_tue_checked, "T");
                            break;
                        case 2:
                            scheduleYourVacation.boo_wed_checked = !scheduleYourVacation.boo_wed_checked;
                            updateView(button, scheduleYourVacation.boo_wed_checked, "W");
                            break;
                        case 3:
                            scheduleYourVacation.boo_thurs_checked = !scheduleYourVacation.boo_thurs_checked;
                            updateView(button, scheduleYourVacation.boo_thurs_checked, "Th");
                            break;
                        case 4:
                            scheduleYourVacation.boo_fri_checked = !scheduleYourVacation.boo_fri_checked;
                            updateView(button, scheduleYourVacation.boo_fri_checked, "F");
                            break;
                        case 5:
                            scheduleYourVacation.boo_sat_checked = !scheduleYourVacation.boo_sat_checked;
                            updateView(button, scheduleYourVacation.boo_sat_checked, "S");
                            break;
                        case 6:
                            scheduleYourVacation.boo_sun_checked = !scheduleYourVacation.boo_sun_checked;
                            updateView(button, scheduleYourVacation.boo_sun_checked, "Su");
                            break;
                    }
                }
            });
        } else if (scheduleNewClass != null) {

            LayoutInflater layoutInflater = (LayoutInflater) scheduleNewClass.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.screen_gridcell, null);
            final Button button = (Button) v.findViewById(R.id.calendar_day_gridcell);
            button.setTextSize(11f);
            button.setTypeface(null, Typeface.BOLD);
            button.setText(days[position]);

            if (selectedDays.contains(position))
                button.setBackgroundDrawable(scheduleNewClass.getResources().getDrawable(R.drawable.scheduled_event_arrow));

        }
        return v;
    }

    private void updateView(Button btn, boolean isChecked) {
        if (isChecked)
            btn.setBackgroundDrawable(addNewSlotActivity.getResources().getDrawable(R.drawable.scheduled_event_arrow));
        else
            btn.setBackgroundColor(addNewSlotActivity.getResources().getColor(R.color.white));
    }

    private void updateView(Button btn, boolean isChecked, String s) {
        if (isChecked) {
            btn.setBackgroundDrawable(scheduleYourVacation.getResources().getDrawable(R.drawable.scheduled_event_arrow));
            scheduleYourVacation.days_array.add(s);
        } else {
            btn.setBackgroundColor(scheduleYourVacation.getResources().getColor(R.color.white));
            scheduleYourVacation.days_array.remove(s);
        }
    }
}
