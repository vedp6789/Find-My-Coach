package com.findmycoach.app.activity;

/**
 * Created by prem on 5/2/15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

//import com.alamkanak.weekview.WeekView;
//import com.alamkanak.weekview.WeekViewEvent;
import com.findmycoach.app.beans.CalendarSchedule.Day;
import com.findmycoach.app.beans.CalendarSchedule.DayEvent;
import com.findmycoach.app.beans.CalendarSchedule.DaySlot;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.SetDate;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.views.WeekView;
import com.findmycoach.app.util.WeekViewEvent;
import com.findmycoach.app.R;
import com.findmycoach.app.beans.connections.Data;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;


public class SetScheduleActivity extends Activity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener, Callback{

    private WeekView mWeekView;
    private String date;
    private Calendar cal;
private static final String TAG = "FMC";
    private static int day;
    private static int month;
    private static int year;
    private static ArrayList<Day> prev_month = null;
    private static ArrayList<Day> current_month = null;
    private static ArrayList<Day> coming_month = null;
    private static final int slot_event_type=0;
    private static final int event_type=1;

    private static ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent getIntent = getIntent();
        if (getIntent != null) {
            date = getIntent.getStringExtra("date");
            day = getIntent.getExtras().getInt("day");
            month = getIntent.getExtras().getInt("month");
            year = getIntent.getExtras().getInt("year");
            prev_month = new ArrayList<Day>();
            current_month = new ArrayList<Day>();
            coming_month = new ArrayList<Day>();
            prev_month = (ArrayList<Day>) getIntent.getSerializableExtra("prev_month_data");
            current_month = (ArrayList<Day>) getIntent.getSerializableExtra("current_month_data");
            coming_month = (ArrayList<Day>) getIntent.getSerializableExtra("coming_month_data");

            progressDialog = new ProgressDialog(SetScheduleActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));


            Log.d(TAG, "prev_month: " + prev_month.size() + ", current_month: " + current_month.size() + ", coming_month: " + coming_month.size());
            //day_schedule = (ArrayList<Day>) getIntent.getSerializableExtra("day_bean");
            Toast.makeText(this, "" + day + "/" + (month + 1) + "/" + year, Toast.LENGTH_LONG).show();

        }

        //  Day day1=day_schedule.get(day-1);


        StorageHelper.storePreference(SetScheduleActivity.this, "day", String.valueOf(day));
        StorageHelper.storePreference(SetScheduleActivity.this, "month", String.valueOf(month));
        StorageHelper.storePreference(SetScheduleActivity.this, "year", String.valueOf(year));


        setContentView(R.layout.activity_set_schedule);

        mWeekView = (WeekView) findViewById(R.id.weekView);

        applyProperties();


    }

    private void applyProperties() {
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        Day day11 = prev_month.get(0);
        String date1 = day11.getDate();


        Day day1 = current_month.get(0);
        String date = day1.getDate();


        Day day12 = coming_month.get(0);
        String date2 = day12.getDate();


        if (Integer.parseInt(date1.split("-", 3)[1]) == newMonth && Integer.parseInt(date1.split("-", 3)[0]) == newYear) {
            // Log.d(TAG,"Going to create view for previous month.");

            if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 2) {
                for (Day d : prev_month) {
                    String date_for_d = d.getDate();
                    List<DayEvent> dayEvents = d.getDayEvents();


                    DayEvent dayEvent;
                    if (dayEvents.size() > 0) {


                        for (int event = 0; event < dayEvents.size(); event++) {
                            dayEvent = dayEvents.get(event);

                            Calendar startTime;
                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                            String start_time = dayEvent.getEvent_start_time();
                            String stop_time = dayEvent.getEvent_stop_time();
                            String f_name = dayEvent.getFname();
                            String l_name = dayEvent.getLname();
                            String event_id = dayEvent.getEvent_id();
                            String sub_category_name=dayEvent.getSub_category_name();
                            //String event_name=dayEvent.getEvent_name();
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_time.split(":", 3)[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(start_time.split(":", 3)[1]));
                            startTime.set(Calendar.MONTH, newMonth - 1);
                            startTime.set(Calendar.YEAR, newYear);
                            Calendar endTime;// = (Calendar) startTime.clone();
                            endTime = (Calendar) startTime.clone();
                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(stop_time.split(":", 3)[0]) - Integer.parseInt(start_time.split(":", 3)[0]));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(stop_time.split(":", 3)[1]));
                            WeekViewEvent weekViewEvent;
                            weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name,sub_category_name), startTime, endTime,event_type);
                            weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                            events.add(weekViewEvent);

                        }
                    }
                }
            }
            if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 3) {
                for (Day d : prev_month) {
                    String date_for_d = d.getDate();
                    List<DayEvent> dayEvents = d.getDayEvents();

                    List<DaySlot> daySlots = d.getDaySlots();
                    DaySlot daySlot;
                    if (daySlots.size() > 0) {
                        Log.d(TAG, "Populating slot data for: " + date_for_d);
                        for (int slot = 0; slot < daySlots.size(); slot++) {
                            daySlot = daySlots.get(slot);
                            Calendar startTime;
                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                            String start_time = daySlot.getSlot_start_time();
                            String stop_time = daySlot.getSlot_stop_time();

                            //String event_name=dayEvent.getEvent_name();
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_time.split(":", 3)[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(start_time.split(":", 3)[1]));
                            startTime.set(Calendar.MONTH, newMonth - 1);
                            startTime.set(Calendar.YEAR, newYear);
                            Calendar endTime;// = (Calendar) startTime.clone();
                            endTime = (Calendar) startTime.clone();
                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(stop_time.split(":", 3)[0]) - Integer.parseInt(start_time.split(":", 3)[0]));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(stop_time.split(":", 3)[1]));
                            WeekViewEvent weekViewEvent;
                            weekViewEvent = new WeekViewEvent(4, getSlotTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1])), startTime, endTime,slot_event_type);
                            weekViewEvent.setColor(getResources().getColor(R.color.event_color_02));
                            events.add(weekViewEvent);

                        }
                    }

                    DayEvent dayEvent;
                    if (dayEvents.size() > 0) {


                        for (int event = 0; event < dayEvents.size(); event++) {
                            dayEvent = dayEvents.get(event);
                            Calendar startTime;
                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                            String start_time = dayEvent.getEvent_start_time();
                            String stop_time = dayEvent.getEvent_stop_time();
                            String f_name = dayEvent.getFname();
                            String l_name = dayEvent.getLname();
                            String event_id = dayEvent.getEvent_id();
                            String sub_category_name=dayEvent.getSub_category_name();
                            //String event_name=dayEvent.getEvent_name();
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_time.split(":", 3)[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(start_time.split(":", 3)[1]));
                            startTime.set(Calendar.MONTH, newMonth - 1);
                            startTime.set(Calendar.YEAR, newYear);
                            Calendar endTime;// = (Calendar) startTime.clone();
                            endTime = (Calendar) startTime.clone();
                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(stop_time.split(":", 3)[0]) - Integer.parseInt(start_time.split(":", 3)[0]));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(stop_time.split(":", 3)[1]));
                            WeekViewEvent weekViewEvent;
                            weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name,sub_category_name), startTime, endTime,event_type);
                            weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                            events.add(weekViewEvent);

                        }
                    }
                }
            }

        }
        if (Integer.parseInt(date.split("-", 3)[1]) == newMonth && Integer.parseInt(date.split("-", 3)[0]) == newYear) {
            // Log.d(TAG,"Going to create view for current month.");

            if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 2) {
                for (Day d : current_month) {
                    String date_for_d = d.getDate();
                    List<DayEvent> dayEvents = d.getDayEvents();


                    DayEvent dayEvent;
                    if (dayEvents.size() > 0) {
                        for (int event = 0; event < dayEvents.size(); event++) {
                            dayEvent = dayEvents.get(event);

                            Calendar startTime;
                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                            String start_time = dayEvent.getEvent_start_time();
                            String stop_time = dayEvent.getEvent_stop_time();
                            String f_name = dayEvent.getFname();
                            String l_name = dayEvent.getLname();
                            String event_id = dayEvent.getEvent_id();
                            String sub_category_name=dayEvent.getSub_category_name();
                            //String event_name=dayEvent.getEvent_name();
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_time.split(":", 3)[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(start_time.split(":", 3)[1]));
                            startTime.set(Calendar.MONTH, newMonth - 1);
                            startTime.set(Calendar.YEAR, newYear);
                            Calendar endTime;// = (Calendar) startTime.clone();
                            endTime = (Calendar) startTime.clone();
                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(stop_time.split(":", 3)[0]) - Integer.parseInt(start_time.split(":", 3)[0]));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(stop_time.split(":", 3)[1]));
                            WeekViewEvent weekViewEvent;
                            weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name,sub_category_name), startTime, endTime,event_type);
                            weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                            events.add(weekViewEvent);

                        }
                    }
                }
            }
            if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 3) {
                for (Day d : current_month) {
                    String date_for_d = d.getDate();
                    List<DayEvent> dayEvents = d.getDayEvents();

                    List<DaySlot> daySlots = d.getDaySlots();
                    DaySlot daySlot;
                    if (daySlots.size() > 0) {
                        for (int slot = 0; slot < daySlots.size(); slot++) {
                            Log.d(TAG, "Populating slot data for: " + date_for_d);
                            daySlot = daySlots.get(slot);
                            Calendar startTime;
                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                            String start_time = daySlot.getSlot_start_time();
                            String stop_time = daySlot.getSlot_stop_time();
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_time.split(":", 3)[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(start_time.split(":", 3)[1]));
                            startTime.set(Calendar.MONTH, newMonth - 1);
                            startTime.set(Calendar.YEAR, newYear);
                            Calendar endTime;// = (Calendar) startTime.clone();
                            endTime = (Calendar) startTime.clone();
                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(stop_time.split(":", 3)[0]) - Integer.parseInt(start_time.split(":", 3)[0]));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(stop_time.split(":", 3)[1]));
                            WeekViewEvent weekViewEvent;
                            weekViewEvent = new WeekViewEvent(4, getSlotTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1])), startTime, endTime,slot_event_type);
                            weekViewEvent.setColor(getResources().getColor(R.color.event_color_02));
                            events.add(weekViewEvent);

                        }
                    }

                    DayEvent dayEvent;
                    if (dayEvents.size() > 0) {
                        for (int event = 0; event < dayEvents.size(); event++) {
                            dayEvent = dayEvents.get(event);

                            Calendar startTime;
                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                            String start_time = dayEvent.getEvent_start_time();
                            String stop_time = dayEvent.getEvent_stop_time();
                            String f_name = dayEvent.getFname();
                            String l_name = dayEvent.getLname();
                            String event_id = dayEvent.getEvent_id();
                            String sub_category_name=dayEvent.getSub_category_name();
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_time.split(":", 3)[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(start_time.split(":", 3)[1]));
                            startTime.set(Calendar.MONTH, newMonth - 1);
                            startTime.set(Calendar.YEAR, newYear);
                            Calendar endTime;// = (Calendar) startTime.clone();
                            endTime = (Calendar) startTime.clone();
                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(stop_time.split(":", 3)[0]) - Integer.parseInt(start_time.split(":", 3)[0]));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(stop_time.split(":", 3)[1]));
                            WeekViewEvent weekViewEvent;
                            weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name,sub_category_name), startTime, endTime,event_type);
                            weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                            events.add(weekViewEvent);

                        }
                    }
                }
            }

        }

        if (Integer.parseInt(date2.split("-", 3)[1]) == newMonth && Integer.parseInt(date2.split("-", 3)[0]) == newYear) {
            // Log.d(TAG,"Going to create view for next month.");

            if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 2) {
                for (Day d : coming_month) {
                    String date_for_d = d.getDate();
                    List<DayEvent> dayEvents = d.getDayEvents();


                    DayEvent dayEvent;
                    if (dayEvents.size() > 0) {
                        for (int event = 0; event < dayEvents.size(); event++) {
                            dayEvent = dayEvents.get(event);

                            Calendar startTime;
                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                            String start_time = dayEvent.getEvent_start_time();
                            String stop_time = dayEvent.getEvent_stop_time();
                            String f_name = dayEvent.getFname();
                            String l_name = dayEvent.getLname();
                            String event_id = dayEvent.getEvent_id();
                            String sub_category_name=dayEvent.getSub_category_name();
                            //String event_name=dayEvent.getEvent_name();
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_time.split(":", 3)[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(start_time.split(":", 3)[1]));
                            startTime.set(Calendar.MONTH, newMonth - 1);
                            startTime.set(Calendar.YEAR, newYear);
                            Calendar endTime;// = (Calendar) startTime.clone();
                            endTime = (Calendar) startTime.clone();
                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(stop_time.split(":", 3)[0]) - Integer.parseInt(start_time.split(":", 3)[0]));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(stop_time.split(":", 3)[1]));
                            WeekViewEvent weekViewEvent;
                            weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name,sub_category_name), startTime, endTime,event_type);
                            weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                            events.add(weekViewEvent);

                        }
                    }
                }
            }
            if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 3) {
                for (Day d : coming_month) {
                    String date_for_d = d.getDate();
                    List<DayEvent> dayEvents = d.getDayEvents();

                    List<DaySlot> daySlots = d.getDaySlots();
                    DaySlot daySlot;
                    if (daySlots.size() > 0) {
                        for (int slot = 0; slot < daySlots.size(); slot++) {
                            Log.d(TAG, "Populating slot data for: " + date_for_d);
                            daySlot = daySlots.get(slot);
                            Calendar startTime;
                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                            String start_time = daySlot.getSlot_start_time();
                            String stop_time = daySlot.getSlot_stop_time();
                            //String event_name=dayEvent.getEvent_name();
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_time.split(":", 3)[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(start_time.split(":", 3)[1]));
                            startTime.set(Calendar.MONTH, newMonth - 1);
                            startTime.set(Calendar.YEAR, newYear);
                            Calendar endTime;// = (Calendar) startTime.clone();
                            endTime = (Calendar) startTime.clone();
                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(stop_time.split(":", 3)[0]) - Integer.parseInt(start_time.split(":", 3)[0]));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(stop_time.split(":", 3)[1]));
                            WeekViewEvent weekViewEvent;
                            weekViewEvent = new WeekViewEvent(4, getSlotTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1])), startTime, endTime,slot_event_type);
                            weekViewEvent.setColor(getResources().getColor(R.color.event_color_02));
                            events.add(weekViewEvent);

                        }
                    }

                    DayEvent dayEvent;
                    if (dayEvents.size() > 0) {
                        for (int event = 0; event < dayEvents.size(); event++) {
                            dayEvent = dayEvents.get(event);

                            Calendar startTime;
                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                            String start_time = dayEvent.getEvent_start_time();
                            String stop_time = dayEvent.getEvent_stop_time();
                            String f_name = dayEvent.getFname();
                            String l_name = dayEvent.getLname();
                            String event_id = dayEvent.getEvent_id();
                            String sub_category_name=dayEvent.getSub_category_name();
                            //String event_name=dayEvent.getEvent_name();
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_time.split(":", 3)[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(start_time.split(":", 3)[1]));
                            startTime.set(Calendar.MONTH, newMonth - 1);
                            startTime.set(Calendar.YEAR, newYear);
                            Calendar endTime;// = (Calendar) startTime.clone();
                            endTime = (Calendar) startTime.clone();
                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(stop_time.split(":", 3)[0]) - Integer.parseInt(start_time.split(":", 3)[0]));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(stop_time.split(":", 3)[1]));
                            WeekViewEvent weekViewEvent;
                            weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name,sub_category_name), startTime, endTime,event_type);
                            weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                            events.add(weekViewEvent);

                        }
                    }
                }
            }

        }


        return events;
    }

    private String getEventTitle(Calendar time, int stop_hour, int stop_min, String fname, String lname,String sub_category_name) {
        if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 3) {
            if(fname.equals("0")){
                if(sub_category_name != null){
                    return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min)+"\n"+sub_category_name;
                }else {
                    return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min);
                }

            }else{
                if(lname.equals("0")){
                    if(sub_category_name != null){
                        return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + fname +"\n"+sub_category_name;
                    }else {
                        return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + fname+"\n"+"";
                    }
                }
            }
            if(sub_category_name != null){
                return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + " " + lname+"\n"+sub_category_name;
            }else {
                return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + " " + lname+"\n"+"";
            }
            //return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + "with " + fname + " " + lname+"\n"+sub_category_name;
        }
        if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 2) {

            if(fname.equals("0")){
                if(sub_category_name != null){
                    return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) +"\n"+sub_category_name;
                }else {
                    return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) ;
                }
            }else{
                if(lname.equals("0")){
                    if(sub_category_name != null){
                        return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + fname+"\n"+sub_category_name;
                    }else {
                        return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with)+fname+"\n"+"";
                    }
                }
            }
            if(sub_category_name != null){
                return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + " " + lname+"\n"+sub_category_name;
            }else {
                return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + " " + lname+"\n"+"";
            }
        }
        return null;
        //return String.format("Event of %02d:%02d to %02d:%02d %s/%d ", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE),stop_hour,stop_min, time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
        //return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE),stop_hour,stop_min)+"\n"+event_name;

    }

    private String getSlotTitle(Calendar time, int stop_hour, int stop_min) {


        //return String.format("Active slot of %02d:%02d to %02d:%02d %s/%d ", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE),stop_hour,stop_min, time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
        return String.format("Active slot: %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min);

    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
       // Toast.makeText(SetScheduleActivity.this, getResources().getString(R.string.clicked) + event.getName(), Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Event Id: " + event.getId() + ", Event start time: " + event.getStartTime() + "Event stop time: " + event.getEndTime());

        int event_type=event.getEventType();
        if(event_type == 1){
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group"));
            requestParams.add("id", String.valueOf(event.getId()));
            progressDialog.show();
            NetworkClient.getCalenderEvent(SetScheduleActivity.this, requestParams, StorageHelper.getUserDetails(SetScheduleActivity.this, "auth_token"), this, 43);
        }


    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(SetScheduleActivity.this, getResources().getString(R.string.long_pressed_event) + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Intent intent=new Intent(SetScheduleActivity.this,AboutEvent.class);
        intent.putExtra("about_event",(String)object);
        startActivity(intent);
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(SetScheduleActivity.this,(String)object,Toast.LENGTH_SHORT).show();
    }


}
