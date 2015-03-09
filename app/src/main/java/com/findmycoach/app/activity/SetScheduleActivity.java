package com.findmycoach.app.activity;

/**
 * Created by prem on 5/2/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

//import com.alamkanak.weekview.WeekView;
//import com.alamkanak.weekview.WeekViewEvent;
import com.findmycoach.app.beans.CalendarSchedule.Day;
import com.findmycoach.app.beans.CalendarSchedule.DayEvent;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.views.WeekView;
import com.findmycoach.app.util.WeekViewEvent;
import com.findmycoach.app.R;
import com.findmycoach.app.beans.connections.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class SetScheduleActivity extends Activity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {

    private WeekView mWeekView;
    private String date;
    private Calendar cal;

    private static final String TAG="FMC";

    public int day;
    public int month;
    public int year;

    private static ArrayList<Day> day_schedule=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent getIntent = getIntent();
        if(getIntent != null) {
            date = getIntent.getStringExtra("date");
            day = getIntent.getExtras().getInt("day");
            month = getIntent.getExtras().getInt("month");
            year = getIntent.getExtras().getInt("year");
            day_schedule = (ArrayList<Day>) getIntent.getSerializableExtra("day_bean");


        }

            Day day1=day_schedule.get(day-1);


            StorageHelper.storePreference(SetScheduleActivity.this, "day", String.valueOf(day));
            StorageHelper.storePreference(SetScheduleActivity.this,"month",String.valueOf(month));
            StorageHelper.storePreference(SetScheduleActivity.this,"year", String.valueOf(year));



            setContentView(R.layout.activity_set_schedule);

            mWeekView = (WeekView) findViewById(R.id.weekView);

            applyProperties();

            Toast.makeText(this,""+day+"/"+(month+1)+"/"+year,Toast.LENGTH_LONG).show();




    }

    private void applyProperties() {
        if(getActionBar() != null)
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


        Day day1=day_schedule.get(day-1);
        DayEvent dayEvent;
        List<DayEvent> list=day1.getDayEvents();

        int day_schedule_arrayList_of_month=day1.getMonth();
        int day_schedule_arrayList_of_year=day1.getYear();

        int days_in_day_schedule_arrayList;
        if(day_schedule_arrayList_of_month == newMonth && day_schedule_arrayList_of_year == newYear){
            Calendar calendar=new GregorianCalendar(day_schedule_arrayList_of_year,day_schedule_arrayList_of_month-1,1);
            days_in_day_schedule_arrayList=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for(int i=0; i<days_in_day_schedule_arrayList;i++){
                Day day2=day_schedule.get(i);
                DayEvent dayEvent2;
                List<DayEvent> list2=day1.getDayEvents();

                if(list2.size() > 0){

                    for(int j=0; j<list2.size();j++){
                        dayEvent2=list.get(j);
//                        Log.d(TAG,"creating event");
                        Calendar startTime;
                        startTime = Calendar.getInstance();
                        startTime.set(Calendar.DAY_OF_MONTH, day2.getDay());
                        int start_hour=dayEvent2.getEvent_start_hour();
                        int start_min=dayEvent2.getEvent_start_min();
                        int stop_hour=dayEvent2.getEvent_stop_hour();
                        int stop_min=dayEvent2.getEvent_stop_min();
                        startTime.set(Calendar.HOUR_OF_DAY, start_hour);
                        startTime.set(Calendar.MINUTE, start_min);
                        startTime.set(Calendar.MONTH, newMonth-1);
                        startTime.set(Calendar.YEAR, newYear);
                        Calendar endTime;// = (Calendar) startTime.clone();
                        endTime = (Calendar) startTime.clone();
                        endTime.add(Calendar.HOUR_OF_DAY, stop_hour-start_hour);
                        endTime.set(Calendar.MINUTE, stop_min);
                        WeekViewEvent event;
                        event = new WeekViewEvent(4, getEventTitle(startTime), startTime, endTime);
                        event.setColor(getResources().getColor(R.color.event_color_04));
                        events.add(event);

                    }

                }


            }


        }
    return events;
    }

    private String getEventTitle(Calendar time) {

        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(SetScheduleActivity.this, getResources().getString(R.string.clicked) + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(SetScheduleActivity.this, getResources().getString(R.string.long_pressed_event) + event.getName(), Toast.LENGTH_SHORT).show();
    }
}
