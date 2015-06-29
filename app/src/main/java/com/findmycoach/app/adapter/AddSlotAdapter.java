package com.findmycoach.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.activity.ScheduleNewClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prem on 26/5/15.
 */
public class AddSlotAdapter extends BaseAdapter {

    private String[] days;
    private AddNewSlotActivity addNewSlotActivity;
    private ScheduleNewClass scheduleNewClass;
    private List<Integer> selectedDays;
    private boolean isNotAllDayAvailableForSelection;
    private ArrayList<Integer> arrayList_weekDay;
    private int color;

    public AddSlotAdapter(AddNewSlotActivity addNewSlotActivity, String[] days) {
        this.days = days;
        this.addNewSlotActivity = addNewSlotActivity;
        color = addNewSlotActivity.getResources().getColor(R.color.purple_light);
        isNotAllDayAvailableForSelection = false;
    }

    public AddSlotAdapter(String[] days, List<Integer> selectedDays, ScheduleNewClass scheduleNewClass) {
        this.days = days;
        this.selectedDays = selectedDays;
        this.scheduleNewClass = scheduleNewClass;
    }

    public void setNotAllDayAvailableForSelection(boolean isAllDayAvailableForSelection) {
        this.isNotAllDayAvailableForSelection = isAllDayAvailableForSelection;
    }

    public void setArrayList_weekDay(ArrayList<Integer> arrayList_weekDay) {
        this.arrayList_weekDay = arrayList_weekDay;
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
            enableAllDaysForSelection(button, position);

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

    private void enableAllDaysForSelection(final Button button, int position) {
        switch (position) {
            case 0:
                if (!isNotAllDayAvailableForSelection || arrayList_weekDay.contains(2)) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("FMC", "Mon clicked !");
                            addNewSlotActivity.boo_mon_checked = !addNewSlotActivity.boo_mon_checked;
                            updateView(button, addNewSlotActivity.boo_mon_checked);
                        }
                    });
                    updateView(button, addNewSlotActivity.boo_mon_checked);
                } else if (!arrayList_weekDay.contains(2)) {
                    button.setTextColor(color);
                }
                break;
            case 1:
                if (!isNotAllDayAvailableForSelection || arrayList_weekDay.contains(3)) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("FMC", "Tue clicked !");
                            addNewSlotActivity.boo_tue_checked = !addNewSlotActivity.boo_tue_checked;
                            updateView(button, addNewSlotActivity.boo_tue_checked);
                        }
                    });
                    updateView(button, addNewSlotActivity.boo_tue_checked);
                } else if (!arrayList_weekDay.contains(3)) {
                    button.setTextColor(color);
                }
                break;
            case 2:
                if (!isNotAllDayAvailableForSelection || arrayList_weekDay.contains(4)) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("FMC", "Wed clicked !");
                            addNewSlotActivity.boo_wed_checked = !addNewSlotActivity.boo_wed_checked;
                            updateView(button, addNewSlotActivity.boo_wed_checked);
                        }
                    });
                    updateView(button, addNewSlotActivity.boo_wed_checked);
                } else if (!arrayList_weekDay.contains(4)) {
                    button.setTextColor(color);
                }
                break;
            case 3:
                if (!isNotAllDayAvailableForSelection || arrayList_weekDay.contains(5)) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("FMC", "Thur clicked !");
                            addNewSlotActivity.boo_thurs_checked = !addNewSlotActivity.boo_thurs_checked;
                            updateView(button, addNewSlotActivity.boo_thurs_checked);
                        }
                    });
                    updateView(button, addNewSlotActivity.boo_thurs_checked);
                } else if (!arrayList_weekDay.contains(5)) {
                    button.setTextColor(color);
                }
                break;
            case 4:
                if (!isNotAllDayAvailableForSelection || arrayList_weekDay.contains(6)) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("FMC", "Fri clicked !");
                            addNewSlotActivity.boo_fri_checked = !addNewSlotActivity.boo_fri_checked;
                            updateView(button, addNewSlotActivity.boo_fri_checked);
                        }
                    });
                    updateView(button, addNewSlotActivity.boo_fri_checked);
                } else if (!arrayList_weekDay.contains(6)) {
                    button.setTextColor(color);
                }
                break;
            case 5:
                if (!isNotAllDayAvailableForSelection || arrayList_weekDay.contains(7)) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("FMC", "Sat clicked !");
                            addNewSlotActivity.boo_sat_checked = !addNewSlotActivity.boo_sat_checked;
                            updateView(button, addNewSlotActivity.boo_sat_checked);
                        }
                    });
                    updateView(button, addNewSlotActivity.boo_sat_checked);
                } else if (!arrayList_weekDay.contains(7)) {
                    button.setTextColor(color);
                }
                break;
            case 6:
                if (!isNotAllDayAvailableForSelection || arrayList_weekDay.contains(1)) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("FMC", "Sun clicked !");
                            addNewSlotActivity.boo_sun_checked = !addNewSlotActivity.boo_sun_checked;
                            updateView(button, addNewSlotActivity.boo_sun_checked);
                        }
                    });
                    updateView(button, addNewSlotActivity.boo_sun_checked);
                } else if (!arrayList_weekDay.contains(1)) {
                    button.setTextColor(color);
                }
                break;
        }
    }

    private void updateView(Button btn, boolean isChecked) {
        if (isChecked)
            btn.setBackgroundDrawable(addNewSlotActivity.getResources().getDrawable(R.drawable.scheduled_event_arrow));
        else
            btn.setBackgroundColor(addNewSlotActivity.getResources().getColor(R.color.white));
    }

}
