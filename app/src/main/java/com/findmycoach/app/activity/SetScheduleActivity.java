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
        WeekView.EventClickListener, WeekView.EventLongPressListener, Callback {

    private WeekView mWeekView;
    private String date_for_grid_selected_from_calendar;
    private Calendar cal;
    private static final String TAG = "FMC";
    private static int day;
    private static int month;
    private static int year;
    private static ArrayList<Day> prev_month = null;
    private static ArrayList<Day> current_month = null;
    private static ArrayList<Day> coming_month = null;
    private static final int slot_event_type = 0;
    private static final int event_type = 1;
    private static final int free_slot_event_type=2;
    private static String this_activity_for = null;
    private static ProgressDialog progressDialog;
    private String mentor_id = null;
    private String mentor_availablity = null;
    private String charges;
    private ArrayList<String> arrayList_subcategory=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent getIntent = getIntent();
        if (getIntent != null) {

            this_activity_for = getIntent.getStringExtra("for");    /* this_activity_for defines its uses i.e. SetScheduleActivity is getting called from two places MyScheduleFragment and MentorDetailsActivity */

            if (this_activity_for.equals("MentorDetailsActivity")) {
                mentor_id = getIntent.getStringExtra("mentor_id");
                mentor_availablity = getIntent.getStringExtra("availability");
                charges=getIntent.getStringExtra("charges");
                arrayList_subcategory=getIntent.getStringArrayListExtra("arrayList_category");
            }

            date_for_grid_selected_from_calendar = getIntent.getStringExtra("date");
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
        String date1 = day11.getDate(); /*  date of the first day of previous month*/


        Day day1 = current_month.get(0);
        String date = day1.getDate();  /*  date of the first day of current month*/


        Day day12 = coming_month.get(0);
        String date2 = day12.getDate(); /*  date of the first day of next month*/


        if (Integer.parseInt(date1.split("-", 3)[1]) == newMonth && Integer.parseInt(date1.split("-", 3)[0]) == newYear) {
            /*
            * success when this activity is going to be started from MentorDetailsActivity for previous month
            * */
            if (this_activity_for.equals("MentorDetailsActivity")) {
                populateWeekViewForPreviousMonth1(events, newYear, newMonth);
            } else {
                if (this_activity_for.equals("ScheduleFragments")) {
            /*
            * success when this activity is going to be started from MyScheduleFragment for previous month
            * */
                    populateWeekViewForPreviousMonth2(events, newYear, newMonth);
                }
            }


        }
        if (Integer.parseInt(date.split("-", 3)[1]) == newMonth && Integer.parseInt(date.split("-", 3)[0]) == newYear) {
            /*
            * success when this activity is going to be started from MentorDetailsActivity for current month
            * */
            if (this_activity_for.equals("MentorDetailsActivity")) {
                populateWeekViewForCurrentMonth1(events, newYear, newMonth);
            } else {
                if (this_activity_for.equals("ScheduleFragments")) {
            /*
            * success when this activity is going to be started from MyScheduleFragment for current month
            * */
                    populateWeekViewForCurrentMonth2(events, newYear, newMonth);
                }
            }


        }

        if (Integer.parseInt(date2.split("-", 3)[1]) == newMonth && Integer.parseInt(date2.split("-", 3)[0]) == newYear) {

            /*
            * success when this activity is going to be started from MentorDetailsActivity for next month
            * */
            if (this_activity_for.equals("MentorDetailsActivity")) {
                populateWeekViewForNextMonth1(events, newYear, newMonth);
            } else {
                if (this_activity_for.equals("ScheduleFragments")) {
            /*
            * success when this activity is going to be started from MyScheduleFragment for next month
            * */
                    populateWeekViewForNextMonth2(events, newYear, newMonth);
                }
            }


        }


        return events;
    }

    private void populateWeekViewForNextMonth2(List<WeekViewEvent> events, int newYear, int newMonth) {
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
                        String sub_category_name = dayEvent.getSub_category_name();
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
                        weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name, sub_category_name), startTime, endTime, event_type);
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
                        weekViewEvent = new WeekViewEvent(4, getSlotTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1])), startTime, endTime, slot_event_type);
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
                        String sub_category_name = dayEvent.getSub_category_name();
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
                        weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name, sub_category_name), startTime, endTime, event_type);
                        weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                        events.add(weekViewEvent);

                    }
                }
            }
        }
    }

    private void populateWeekViewForNextMonth1(List<WeekViewEvent> events, int newYear, int newMonth) {


        poplateWeekView(coming_month, events, newYear, newMonth);


    }


    /**
     *This will populate week_view for free slot representation in mentee's calendar
     */

    private void poplateWeekView(ArrayList<Day> month, List<WeekViewEvent> events, int newYear, int newMonth) {
        for (Day d : month) {
            String date_for_d = d.getDate();
            List<DaySlot> daySlots = d.getDaySlots();
            List<DayEvent> dayEvents = d.getDayEvents();

            if (daySlots.size() <= 0) {
                 /*   success when there is no slots i.e. slots array size is zero
                  *    In this condition, grid click event should be handled like we do not open week-view and give a message that mentor is not free on this day.
                  * */
            } else {
                /*
                 * success when this day his having slots.
                 * Now to decide any slot is free or not
                 * Further the variable free_slot will inform that how many free slots are availbale to get schedule on this day i.e. a particular week-view.
                 * */
                int free_slot = 0;

                /*
                 * matching each slot of the day with all possible events, and on this match deciding whether this slot come as free slot or not.
                 * */
                for (int day_slot = 0; day_slot < daySlots.size(); day_slot++) {
                    DaySlot daySlot = daySlots.get(day_slot);
                    String slot_start_date = daySlot.getSlot_start_date();
                    String slot_stop_date = daySlot.getSlot_stop_date();
                    String slot_start_time = daySlot.getSlot_start_time();
                    String slot_stop_time = daySlot.getSlot_stop_time();
                    String slot_type = daySlot.getSlot_type();
                    String slot_id = daySlot.getSlot_id();
                    String[] slot_on_week_days = daySlot.getSlot_week_days();
                    int slot_max_users = Integer.parseInt(daySlot.getSlot_max_users());


                    int slot_start_day = Integer.parseInt(slot_start_date.split("-", 3)[2]);
                    int slot_start_month = Integer.parseInt(slot_start_date.split("-", 3)[1]);
                    int slot_start_year = Integer.parseInt(slot_start_date.split("-", 3)[0]);
                    int slot_start_hour = Integer.parseInt(slot_start_time.split(":", 3)[0]);
                    int slot_start_minute = Integer.parseInt(slot_start_time.split(":", 3)[1]);


                    int slot_stop_day = Integer.parseInt(slot_stop_date.split("-", 3)[2]);
                    int slot_stop_month = Integer.parseInt(slot_stop_date.split("-", 3)[1]);
                    int slot_stop_year = Integer.parseInt(slot_stop_date.split("-", 3)[0]);
                    int slot_stop_hour = Integer.parseInt(slot_stop_time.split(":", 3)[0]);
                    int slot_stop_minute = Integer.parseInt(slot_stop_time.split(":", 3)[1]);


                    /*
                     *
                     * For the slot which are selected as Group
                     * */
                    if (slot_type.equalsIgnoreCase("Group")) {
                        boolean slot_match_with_any_event = false;
                        for (int day_event = 0; day_event < dayEvents.size(); day_event++) {    /* dayEvents is a list of DayEvent bean*/
                            DayEvent dayEvent1 = dayEvents.get(day_event);
                            String event_start_date = dayEvent1.getEvent_start_date();
                            String event_stop_date = dayEvent1.getEvent_stop_date();
                            String event_start_time = dayEvent1.getEvent_start_time();
                            String event_stop_time = dayEvent1.getEvent_stop_time();
                            int event_total_mentees = Integer.parseInt(dayEvent1.getEvent_total_mentee());
                                        /* checking whether this particular event is similar to slot or not */
                            if (event_start_date.equals(slot_start_date) && event_stop_date.equals(slot_stop_date) && event_start_time.equals(slot_start_time) && event_stop_time.equals(slot_stop_time)) {
                                slot_match_with_any_event = true;
                                            /* if found similar then check whether the event_totoal_mentees from slot_max_users*/
                                if (event_total_mentees < slot_max_users) {
                                    free_slot++;
                                    /*
                                    * Here on this slot, we will show this slot as a free slot on week-view.
                                    * */


                                    Calendar startTime;
                                    startTime = Calendar.getInstance();
                                    startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                                    startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                    startTime.set(Calendar.MINUTE, Integer.parseInt(slot_start_time.split(":", 3)[1]));
                                    startTime.set(Calendar.MONTH, newMonth - 1);
                                    startTime.set(Calendar.YEAR, newYear);
                                    Calendar endTime;// = (Calendar) startTime.clone();
                                    endTime = (Calendar) startTime.clone();
                                    endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_stop_time.split(":", 3)[0]) - Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                    endTime.set(Calendar.MINUTE, Integer.parseInt(slot_stop_time.split(":", 3)[1]));
                                    WeekViewEvent weekViewEvent;
                                    weekViewEvent = new WeekViewEvent(Integer.parseInt(slot_id), getFreeSlotTitle(slot_start_day, slot_start_month, slot_start_year, slot_stop_day, slot_stop_month, slot_stop_year, slot_start_hour, slot_start_minute, slot_stop_hour, slot_stop_minute, slot_type,slot_on_week_days), startTime, endTime, slot_start_day, slot_start_month, slot_start_year, slot_stop_day, slot_stop_month, slot_stop_year, slot_start_hour, slot_start_minute, slot_stop_hour, slot_stop_minute, slot_type,slot_on_week_days, mentor_id, mentor_availablity,free_slot_event_type,charges,arrayList_subcategory);
                                    weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                                    events.add(weekViewEvent);


                                }
                                break;
                            }
                        }

                        if (!slot_match_with_any_event) {
                            free_slot++;
                            /*
                                    * Here on this slot, we will show this slot as a free slot on week-view.
                                    * */


                            Calendar startTime;
                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_start_time.split(":", 3)[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(slot_start_time.split(":", 3)[1]));
                            startTime.set(Calendar.MONTH, newMonth - 1);
                            startTime.set(Calendar.YEAR, newYear);
                            Calendar endTime;// = (Calendar) startTime.clone();
                            endTime = (Calendar) startTime.clone();
                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_stop_time.split(":", 3)[0]) - Integer.parseInt(slot_start_time.split(":", 3)[0]));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(slot_stop_time.split(":", 3)[1]));
                            WeekViewEvent weekViewEvent;
                            weekViewEvent = new WeekViewEvent(Integer.parseInt(slot_id), getFreeSlotTitle(slot_start_day, slot_start_month, slot_start_year, slot_stop_day, slot_stop_month, slot_stop_year, slot_start_hour, slot_start_minute, slot_stop_hour, slot_stop_minute, slot_type,slot_on_week_days), startTime, endTime, slot_start_day, slot_start_month, slot_start_year, slot_stop_day, slot_stop_month, slot_stop_year, slot_start_hour, slot_start_minute, slot_stop_hour, slot_stop_minute, slot_type,slot_on_week_days, mentor_id, mentor_availablity,free_slot_event_type,charges,arrayList_subcategory);
                            weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                            events.add(weekViewEvent);
                        }


                    } else {
                                    /*
                                    *
                                    * For the slot which are selected as solo
                                    * */
                        boolean slot_match_with_event = false;
                        for (int day_event = 0; day_event < dayEvents.size(); day_event++) {
                            DayEvent dayEvent1 = dayEvents.get(day_event);
                            String event_start_date = dayEvent1.getEvent_start_date();
                            String event_stop_date = dayEvent1.getEvent_stop_date();
                            String event_start_time = dayEvent1.getEvent_start_time();
                            String event_stop_time = dayEvent1.getEvent_stop_time();
                            int event_total_mentees = Integer.parseInt(dayEvent1.getEvent_total_mentee());
                                        /* checking whether this particular event is similar to slot or not */
                            if (event_start_date.equals(slot_start_date) && event_stop_date.equals(slot_stop_date) && event_start_time.equals(slot_start_time) && event_stop_time.equals(slot_stop_time)) {
                                slot_match_with_event = true;
                                break;
                            }
                        }

                        if (!slot_match_with_event) {
                            free_slot++;
                        /*
                                    * Here on this slot, we will show this slot as a free slot on week-view.
                                    * */

                            Calendar startTime;
                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_for_d.split("-", 3)[2]));
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_start_time.split(":", 3)[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(slot_start_time.split(":", 3)[1]));
                            startTime.set(Calendar.MONTH, newMonth - 1);
                            startTime.set(Calendar.YEAR, newYear);
                            Calendar endTime;// = (Calendar) startTime.clone();
                            endTime = (Calendar) startTime.clone();
                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_stop_time.split(":", 3)[0]) - Integer.parseInt(slot_start_time.split(":", 3)[0]));
                            endTime.set(Calendar.MINUTE, Integer.parseInt(slot_stop_time.split(":", 3)[1]));
                            WeekViewEvent weekViewEvent;
                            weekViewEvent = new WeekViewEvent(Integer.parseInt(slot_id), getFreeSlotTitle(slot_start_day, slot_start_month, slot_start_year, slot_stop_day, slot_stop_month, slot_stop_year, slot_start_hour, slot_start_minute, slot_stop_hour, slot_stop_minute, slot_type, slot_on_week_days), startTime, endTime, slot_start_day, slot_start_month, slot_start_year, slot_stop_day, slot_stop_month, slot_stop_year, slot_start_hour, slot_start_minute, slot_stop_hour, slot_stop_minute, slot_type,slot_on_week_days, mentor_id, mentor_availablity,free_slot_event_type,charges,arrayList_subcategory);
                            weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                            events.add(weekViewEvent);

                        }

                    }

                }

                            /*
                            *
                            * if free_slot is having value less than or equal to zero, we can do something on this day weekview. like we can skip it or show some message
                            * */
                if ((free_slot > 0)) {

                }


            }


        }
    }

    private void populateWeekViewForCurrentMonth2(List<WeekViewEvent> events, int newYear, int newMonth) {

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
                        String sub_category_name = dayEvent.getSub_category_name();
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
                        weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name, sub_category_name), startTime, endTime, event_type);
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
                        weekViewEvent = new WeekViewEvent(4, getSlotTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1])), startTime, endTime, slot_event_type);
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
                        String sub_category_name = dayEvent.getSub_category_name();
                        startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start_time.split(":", 3)[0]));
                        startTime.set(Calendar.MINUTE, Integer.parseInt(start_time.split(":", 3)[1]));
                        startTime.set(Calendar.MONTH, newMonth - 1);
                        startTime.set(Calendar.YEAR, newYear);
                        Calendar endTime;// = (Calendar) startTime.clone();
                        endTime = (Calendar) startTime.clone();
                        endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(stop_time.split(":", 3)[0]) - Integer.parseInt(start_time.split(":", 3)[0]));
                        endTime.set(Calendar.MINUTE, Integer.parseInt(stop_time.split(":", 3)[1]));
                        WeekViewEvent weekViewEvent;
                        weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name, sub_category_name), startTime, endTime, event_type);
                        weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                        events.add(weekViewEvent);

                    }
                }
            }
        }
    }

    private void populateWeekViewForCurrentMonth1(List<WeekViewEvent> events, int newYear, int newMonth) {
        poplateWeekView(current_month, events, newYear, newMonth);

    }

    private void populateWeekViewForPreviousMonth1(List<WeekViewEvent> events, int newYear, int newMonth) {
        poplateWeekView(prev_month, events, newYear, newMonth);

    }

    private void populateWeekViewForPreviousMonth2(List<WeekViewEvent> events, int newYear, int newMonth) {
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
                        String sub_category_name = dayEvent.getSub_category_name();
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
                        weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name, sub_category_name), startTime, endTime, event_type);
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
                        weekViewEvent = new WeekViewEvent(4, getSlotTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1])), startTime, endTime, slot_event_type);
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
                        String sub_category_name = dayEvent.getSub_category_name();
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
                        weekViewEvent = new WeekViewEvent(Integer.parseInt(event_id), getEventTitle(startTime, Integer.parseInt(stop_time.split(":", 3)[0]), Integer.parseInt(stop_time.split(":", 3)[1]), f_name, l_name, sub_category_name), startTime, endTime, event_type);
                        weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                        events.add(weekViewEvent);

                    }
                }
            }
        }
    }

    private String getEventTitle(Calendar time, int stop_hour, int stop_min, String fname, String lname, String sub_category_name) {
        if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 3) {
            if (fname.equals("0")) {
                if (sub_category_name != null) {
                    return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + sub_category_name;
                } else {
                    return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min);
                }

            } else {
                if (lname.equals("0")) {
                    if (sub_category_name != null) {
                        return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + fname + "\n" + sub_category_name;
                    } else {
                        return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + fname + "\n" + "";
                    }
                }
            }
            if (sub_category_name != null) {
                return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + " " + lname + "\n" + sub_category_name;
            } else {
                return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + " " + lname + "\n" + "";
            }
            //return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + "with " + fname + " " + lname+"\n"+sub_category_name;
        }
        if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 2) {

            if (fname.equals("0")) {
                if (sub_category_name != null) {
                    return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + sub_category_name;
                } else {
                    return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min);
                }
            } else {
                if (lname.equals("0")) {
                    if (sub_category_name != null) {
                        return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + fname + "\n" + sub_category_name;
                    } else {
                        return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + fname + "\n" + "";
                    }
                }
            }
            if (sub_category_name != null) {
                return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + " " + lname + "\n" + sub_category_name;
            } else {
                return String.format("Event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "\n" + getResources().getString(R.string.with) + "" + " " + lname + "\n" + "";
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


    private String getFreeSlotTitle(int slot_start_day, int slot_start_month, int slot_start_year, int slot_stop_day, int slot_stop_month, int slot_stop_year, int slot_start_hour, int slot_start_min, int slot_stop_hour, int slot_stop_min, String slot_type,String [] slot_on_week_days) {
        StringBuilder stringBuilder=new StringBuilder();
        for (int slot_week_day=0;slot_week_day < slot_on_week_days.length;slot_week_day++){
            String week_day=slot_on_week_days[slot_week_day];

            /* Checking whether the slot_week_day is having last index of slot_on_wee_days array, Objective is to print slot week days in comma separated */
            if(slot_week_day == slot_on_week_days.length-1){
                if(week_day.equals("M")){
                    stringBuilder.append("Mon");
                }
                if(week_day.equals("T")){
                    stringBuilder.append("Tue");
                }
                if(week_day.equals("W")){
                    stringBuilder.append("Wed");
                }
                if(week_day.equals("Th")){
                    stringBuilder.append("Thu");
                }
                if(week_day.equals("F")){
                    stringBuilder.append("Fri");
                }
                if(week_day.equals("S")){
                    stringBuilder.append("Sat");
                }
                if(week_day.equals("Su")){
                    stringBuilder.append("Sun");
                }
            }else{
                if(week_day.equals("M")){
                    stringBuilder.append("Mon, ");
                }
                if(week_day.equals("T")){
                    stringBuilder.append("Tue, ");
                }
                if(week_day.equals("W")){
                    stringBuilder.append("Wed, ");
                }
                if(week_day.equals("Th")){
                    stringBuilder.append("Thu, ");
                }
                if(week_day.equals("F")){
                    stringBuilder.append("Fri, ");
                }
                if(week_day.equals("S")){
                    stringBuilder.append("Sat, ");
                }
                if(week_day.equals("Su")){
                    stringBuilder.append("Sun, ");
                }
            }

        }

        return String.format("Free slot: %02d-%02d-%d to %02d-%02d-%d \nTiming: %02d:%02d to %02d:%02d \n", slot_start_day, slot_start_month, slot_start_year, slot_stop_day, slot_stop_month, slot_stop_year,slot_start_hour,slot_start_min,slot_stop_hour,slot_stop_min) + "Slot type: " + slot_type+"\n"+"Week-days: "+stringBuilder.toString();
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        // Toast.makeText(SetScheduleActivity.this, getResources().getString(R.string.clicked) + event.getName(), Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Event Id: " + event.getId() + ", Event start time: " + event.getStartTime() + "Event stop time: " + event.getEndTime());

        int event_type = event.getEventType();
        if (event_type == 1) {
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group"));
            requestParams.add("id", String.valueOf(event.getId()));
            progressDialog.show();
            NetworkClient.getCalenderEvent(SetScheduleActivity.this, requestParams, StorageHelper.getUserDetails(SetScheduleActivity.this, "auth_token"), this, 43);
        }else{
            if(event_type == 2){    /* envent_type 2 is for free slot duration*/
                /* ScheduleNewClass activity will open now, and SetScheduleActivity will send all desired data for this free slot to ScheduleNewClass */
                Intent intent=new Intent(SetScheduleActivity.this,ScheduleNewClass.class);
                Bundle bundle=new Bundle();
                bundle.putLong("slot_id",event.getId());
                bundle.putString("mentor_id",event.getMentor_id());
                bundle.putString("mentor_availability",event.getMentor_availablity());
                bundle.putInt("slot_start_day",event.getSlot_start_day());
                bundle.putInt("slot_start_month",event.getSlot_start_month());
                bundle.putInt("slot_start_year",event.getSlot_start_year());
                bundle.putInt("slot_stop_day",event.getSlot_stop_day());
                bundle.putInt("slot_stop_month",event.getSlot_stop_month());
                bundle.putInt("slot_stop_year",event.getSlot_stop_year());
                bundle.putInt("slot_start_hour",event.getSlot_start_hour());
                bundle.putInt("slot_start_minute",event.getSlot_start_minute());
                bundle.putInt("slot_stop_hour",event.getSlot_stop_hour());
                bundle.putInt("slot_stop_minute",event.getSlot_stop_minute());
                bundle.putStringArray("slot_on_week_days",event.getSlot_on_week_days());
                bundle.putString("charges",event.getCharges());
                bundle.putStringArrayList("arrayList_sub_category",arrayList_subcategory);

                intent.putExtra("slot_bundle",bundle);
                startActivity(intent);



            }
        }



    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(SetScheduleActivity.this, getResources().getString(R.string.long_pressed_event) + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Intent intent = new Intent(SetScheduleActivity.this, AboutEvent.class);
        intent.putExtra("about_event", (String) object);
        startActivity(intent);
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(SetScheduleActivity.this, (String) object, Toast.LENGTH_SHORT).show();
    }


}
