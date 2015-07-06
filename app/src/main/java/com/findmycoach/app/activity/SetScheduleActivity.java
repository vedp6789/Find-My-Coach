package com.findmycoach.app.activity;

/**
 * Created by prem on 5/2/15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.CalendarSchedule.Event;
import com.findmycoach.app.beans.CalendarSchedule.EventDuration;
import com.findmycoach.app.beans.CalendarSchedule.Mentee;
import com.findmycoach.app.beans.CalendarSchedule.MentorInfo;
import com.findmycoach.app.beans.CalendarSchedule.MonthYearInfo;
import com.findmycoach.app.beans.CalendarSchedule.Slot;
import com.findmycoach.app.beans.CalendarSchedule.SlotDurationDetailBean;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;
import com.findmycoach.app.fragment.MyScheduleFragment;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.util.WeekViewEvent;
import com.findmycoach.app.views.WeekView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class SetScheduleActivity extends Activity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener, Callback {

    private WeekView mWeekView;
    private String date_of_grid_selected_from_calendar;
    private Calendar cal;
    private static final String TAG = "FMC";
    private static int day;
    private static int month;
    private static int year;
    private static ArrayList<Slot> prev_month = null;
    private static ArrayList<Slot> current_month = null;
    private static ArrayList<Slot> coming_month = null;
    private static ArrayList<Vacation> prev_month_non_coinciding_vacations = null;
    private static ArrayList<Vacation> current_month_non_coinciding_vacations = null;
    private static ArrayList<Vacation> coming_month_non_coinciding_vacations = null;
    private static ArrayList<MonthYearInfo> previous_month_year_info = null;
    private static ArrayList<MonthYearInfo> current_month_year_info = null;
    private static ArrayList<MonthYearInfo> coming_month_year_info = null;
    private static ArrayList<MentorInfo> previousMonthMentorInfo = null;
    private static ArrayList<MentorInfo> currentMonthMentorInfo = null;
    private static ArrayList<MentorInfo> comingMonthMentorInfo = null;

    private static String this_activity_for = null;
    private static ProgressDialog progressDialog;
    private String mentor_id = null;
    private String mentor_availablity = null;
    private String charges;
    private ArrayList<String> arrayList_subcategory = null;
    private boolean show_free_Slot = true;
    private boolean vacation_found_in_between_slot = false;
    private int weekView_previous_month, weekView_current_month, weekView_coming_month;  /*These are in relation to month and year of the calendar which get tapped to view the details for particular day*/
    private int weekView_previous_month_year, weekView_current_month_year, weekView_coming_month_year;
    private int weekView_previous_month_days, weekView_current_month_days, weekView_coming_month_days;
    private int slot_request_code = 0;
    private int slot_result_code = 1;
    private int vacation_req_code = 3;
    private int vacation_res_code = 6;
    private TextView titleTextView;
    public static SetScheduleActivity setScheduleActivity;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setScheduleActivity=null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setScheduleActivity=this;

            Intent getIntent = getIntent();
            if (getIntent != null) {
                Log.d(TAG, "inside SetScheduleActivity onCreate");
                this_activity_for = "";
                this_activity_for = getIntent.getStringExtra("for");    /* this_activity_for defines its uses i.e. SetScheduleActivity is getting called from two places MyScheduleFragment and MentorDetailsActivity */
                Log.d(TAG, "activity_for: " + this_activity_for);
                if (this_activity_for.equals("MentorDetailsActivity")) {
                    mentor_id = getIntent.getStringExtra("mentor_id");
                    mentor_availablity = getIntent.getStringExtra("availability");
                    charges = getIntent.getStringExtra("charges");
                    arrayList_subcategory = getIntent.getStringArrayListExtra("arrayList_category");
                } else {
                    if (this_activity_for.equals("ScheduleFragments") && StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group").equals("3")) {
                        prev_month_non_coinciding_vacations = new ArrayList<Vacation>();
                        current_month_non_coinciding_vacations = new ArrayList<Vacation>();
                        coming_month_non_coinciding_vacations = new ArrayList<Vacation>();
                        prev_month_non_coinciding_vacations = (ArrayList<Vacation>) getIntent.getSerializableExtra("previous_month_non_coinciding_vacation");
                        current_month_non_coinciding_vacations = (ArrayList<Vacation>) getIntent.getSerializableExtra("current_month_non_coinciding_vacation");
                        coming_month_non_coinciding_vacations = (ArrayList<Vacation>) getIntent.getSerializableExtra("coming_month_non_coinciding_vacation");
                    }
                }

                date_of_grid_selected_from_calendar = getIntent.getStringExtra("date");
                day = getIntent.getExtras().getInt("day");
                month = getIntent.getExtras().getInt("month");   /* According to calendar month array i.e. it is one less according to current month, if month is december its value is 11*/
                year = getIntent.getExtras().getInt("year");
                prev_month = new ArrayList<Slot>();
                current_month = new ArrayList<Slot>();
                coming_month = new ArrayList<Slot>();
                prev_month = (ArrayList<Slot>) getIntent.getSerializableExtra("prev_month_data");
                current_month = (ArrayList<Slot>) getIntent.getSerializableExtra("current_month_data");
                coming_month = (ArrayList<Slot>) getIntent.getSerializableExtra("coming_month_data");
                previous_month_year_info = new ArrayList<MonthYearInfo>();
                current_month_year_info = new ArrayList<MonthYearInfo>();
                coming_month_year_info = new ArrayList<MonthYearInfo>();
                previous_month_year_info = (ArrayList<MonthYearInfo>) getIntent.getSerializableExtra("previous_month_year_info");
                current_month_year_info = (ArrayList<MonthYearInfo>) getIntent.getSerializableExtra("current_month_year_info");
                coming_month_year_info = (ArrayList<MonthYearInfo>) getIntent.getSerializableExtra("coming_month_year_info");
                previousMonthMentorInfo = new ArrayList<MentorInfo>();
                currentMonthMentorInfo = new ArrayList<MentorInfo>();
                comingMonthMentorInfo = new ArrayList<MentorInfo>();
                previousMonthMentorInfo = (ArrayList<MentorInfo>) getIntent.getSerializableExtra("prev_month_mentor");
                currentMonthMentorInfo = (ArrayList<MentorInfo>) getIntent.getSerializableExtra("current_month_mentor");
                comingMonthMentorInfo = (ArrayList<MentorInfo>) getIntent.getSerializableExtra("coming_month_mentor");


                Log.d(TAG, "prev_month_mentor_info size: " + previousMonthMentorInfo.size() + "current :" + currentMonthMentorInfo.size() + "coming :" + comingMonthMentorInfo.size());

                progressDialog = new ProgressDialog(SetScheduleActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.please_wait));

                Log.d(TAG, "prev_month: " + prev_month.size() + ", current_month: " + current_month.size() + ", coming_month: " + coming_month.size());
                //day_schedule = (ArrayList<Day>) getIntent.getSerializableExtra("day_bean");
                /*Toast.makeText(this, "" + day + "/" + (month + 1) + "/" + year, Toast.LENGTH_LONG).show();*/

            }
            StorageHelper.storePreference(SetScheduleActivity.this, "day", String.valueOf(day));
            StorageHelper.storePreference(SetScheduleActivity.this, "month", String.valueOf(month));
            StorageHelper.storePreference(SetScheduleActivity.this, "year", String.valueOf(year));


            setContentView(R.layout.activity_set_schedule);

            findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            titleTextView = (TextView) findViewById(R.id.title);

            mWeekView = (WeekView) findViewById(R.id.weekView);

            /*When SetScheduleActivity opens, it always get three months(previous, current(calendar tap day), coming) data to populate */

            weekView_previous_month = previous_month_year_info.get(0).getMonth();
            weekView_previous_month_year = previous_month_year_info.get(0).getYear();
            weekView_previous_month_days = previous_month_year_info.get(0).getDays();

            weekView_current_month = current_month_year_info.get(0).getMonth();
            weekView_current_month_year = current_month_year_info.get(0).getYear();
            weekView_current_month_days = current_month_year_info.get(0).getDays();

            weekView_coming_month = coming_month_year_info.get(0).getMonth();
            weekView_coming_month_year = coming_month_year_info.get(0).getYear();
            weekView_coming_month_days = coming_month_year_info.get(0).getDays();

            applyProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void applyProperties() {
//        if (getActionBar() != null)
//            getActionBar().setDisplayHomeAsUpEnabled(true);
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

        if (weekView_previous_month == newMonth && weekView_previous_month_year == newYear) {
            /*
            * success when this activity is going to be started from MentorDetailsActivity for previous month
            * */
            if (this_activity_for.equals("MentorDetailsActivity")) {
                populateWeekViewForPreviousMonth1(events, newYear, newMonth, weekView_previous_month_days);      /* call for the method to populate weekVeiw for previous month in MentorDetailsActivity i.e. mentee is trying to schedule a class from Calendar   */
            } else {
                if (this_activity_for.equals("ScheduleFragments")) {
            /*
            * success when this activity is going to be started from MyScheduleFragment for previous month
            * */
                    Log.d(TAG, "populate week_view for previous month");

                    populateWeekViewForPreviousMonth2(events, newYear, newMonth, weekView_previous_month_days);    /* call for the method to populate weekVeiw for previous month in Mentor Scedule Calendar   */
                }
            }


        }
        if (weekView_current_month == newMonth && weekView_current_month_year == newYear) {
            /*
            * success when this activity is going to be started from MentorDetailsActivity for current month
            * */
            if (this_activity_for.equals("MentorDetailsActivity")) {
                populateWeekViewForCurrentMonth1(events, newYear, newMonth, weekView_current_month_days);     /* call for the method to populate weekVeiw for current month in MentorDetailsActivity i.e. mentee is trying to schedule a class from Calendar   */
            } else {
                if (this_activity_for.equals("ScheduleFragments")) {
            /*
            * success when this activity is going to be started from MyScheduleFragment for current month
            * */

                    Log.d(TAG, "populate week_view for current month");
                    populateWeekViewForCurrentMonth2(events, newYear, newMonth, weekView_current_month_days);    /* call for the method to populate weekVeiw for current month in Mentor Scedule Calendar   */
                }
            }


        }

        if (weekView_coming_month == newMonth && weekView_coming_month_year == newYear) {

            /*
            * success when this activity is going to be started from MentorDetailsActivity for next month
            * */
            if (this_activity_for.equals("MentorDetailsActivity")) {
                populateWeekViewForNextMonth1(events, newYear, newMonth, weekView_coming_month_days);       /* call for the method to populate weekVeiw for next month in MentorDetailsActivity i.e. mentee is trying to schedule a class from Calendar   */
            } else {
                if (this_activity_for.equals("ScheduleFragments")) {
            /*
            * success when this activity is going to be started from MyScheduleFragment for next month
            * */
                    Log.d(TAG, "populate week_view for coming month");

                    populateWeekViewForNextMonth2(events, newYear, newMonth, weekView_coming_month_days);   /* call for the method to populate weekVeiw for next month in Mentor Scedule Calendar   */
                }
            }


        }


        return events;
    }

    public class AvailabilityFlags {
        boolean event_found = false;
        boolean vacation_found = false;
        boolean slot_found = false;
    }


    /* method to know whether one week day is among one of week days array like ["M","W","F","Su"] or not*/
    private boolean thisDayMatchesWithArrayOfWeekDays(String[] week_days, int week_day_of_the_day) {
        boolean day_matches = false;
        String this_day_week_day = null;   /* this will have the day which is calendar current day according to grid view position*/
        switch (week_day_of_the_day) {
            case 1:
                this_day_week_day = "Su";
                break;
            case 2:
                this_day_week_day = "M";
                break;
            case 3:
                this_day_week_day = "T";
                break;
            case 4:
                this_day_week_day = "W";
                break;
            case 5:
                this_day_week_day = "Th";
                break;
            case 6:
                this_day_week_day = "F";
                break;
            case 7:
                this_day_week_day = "S";
                break;
        }

        for (int week_day_index = 0; week_day_index < week_days.length; week_day_index++) {
            Log.d(TAG, "week day: " + week_days[week_day_index] + " this day: " + this_day_week_day);
            if (this_day_week_day != null && this_day_week_day.equalsIgnoreCase(week_days[week_day_index])) {
                day_matches = true;
            }
        }

        return day_matches;
    }


    private void populateWeekViewForNextMonth2(List<WeekViewEvent> events, int newYear, int newMonth, int number_of_days_in_this_month) {
        // Log.d(TAG,"Going to create view for next month.");

        if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 2) {

            populateWeekViewForMenteeSchedule(events, coming_month, comingMonthMentorInfo, number_of_days_in_this_month, newMonth, newYear);


        }

        if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 3) {

            populateWeekViewForMentorSchedule(coming_month, coming_month_non_coinciding_vacations, number_of_days_in_this_month, events, newMonth, newYear);

        }
    }

    private void populateWeekViewForMenteeSchedule(List<WeekViewEvent> events, ArrayList<Slot> coming_month, ArrayList<MentorInfo> comingMonthMentorInfo, int number_of_days_in_this_month, int newMonth, int newYear) {

        if (coming_month.size() > 0) {
            Slot slot = coming_month.get(0);
            if (Boolean.parseBoolean(slot.isSlot_created_on_network_success())) {  /* Checking whether the slots came in this month are either on network success as when there is network failure i am adding one slot with flag for network communication as false */
                Log.d(TAG, "network success data for mentee schedule");
                for (int day_of_this_month = 1; day_of_this_month <= number_of_days_in_this_month; day_of_this_month++) {


                    for (int slot_no = 0; slot_no < coming_month.size(); slot_no++) {

                        AvailabilityFlags availabilityFlags = new AvailabilityFlags();

                        Slot new_slot = coming_month.get(slot_no);
                        String slot_start_date = new_slot.getSlot_start_date();
                        String slot_stop_date = new_slot.getSlot_stop_date();
                        String slot_start_time = new_slot.getSlot_start_time();
                        String slot_stop_time = new_slot.getSlot_stop_time();
                        String slot_week_days[] = new_slot.getSlot_week_days();
                        String sub_category_name = null;  /* it will get decided from mentee */
                        String event_id = null;   /* will get initialized with event_id if event found for this slot on this day*/
                        String slot_id = new_slot.getSlot_id();
                        String mentor_id = new_slot.getMentor_id();
                        List<Event> eventList = new_slot.getEvents();
                        List<Vacation> vacationList = new_slot.getVacations();

                        Calendar calendar_slot_start_date = Calendar.getInstance();
                        calendar_slot_start_date.set(Integer.parseInt(slot_start_date.split("-")[0]), Integer.parseInt(slot_start_date.split("-")[1]) - 1, Integer.parseInt(slot_start_date.split("-")[2]));
                        long new_slot_start_date_millis = calendar_slot_start_date.getTimeInMillis();


                        Calendar calendar_for_day_of_this_month = Calendar.getInstance();   /* each day of this month will get initialized as loop executes. For each day, matching the possible events or vacation coming for the mentee */
                        calendar_for_day_of_this_month.set(newYear, newMonth - 1, day_of_this_month);
                        long this_day_in_millis = calendar_for_day_of_this_month.getTimeInMillis();
                        int this_day_week_day = calendar_for_day_of_this_month.get(Calendar.DAY_OF_WEEK);


                        Calendar calendar_slot_stop_date = Calendar.getInstance();
                        calendar_slot_stop_date.set(Integer.parseInt(slot_stop_date.split("-")[0]), Integer.parseInt(slot_stop_date.split("-")[1]) - 1, Integer.parseInt(slot_stop_date.split("-")[2]));
                        long new_slot_stop_date_millis = calendar_slot_stop_date.getTimeInMillis();

                        if ((this_day_in_millis == new_slot_start_date_millis) || (this_day_in_millis == new_slot_stop_date_millis) || (this_day_in_millis > new_slot_start_date_millis && this_day_in_millis < new_slot_stop_date_millis)) {
                            if (thisDayMatchesWithArrayOfWeekDays(slot_week_days, this_day_week_day)) {
                                    /* slot found on this day */

                                MentorInfo mentorInfo = new MentorInfo();
                                for (int mentor_no = 0; mentor_no < comingMonthMentorInfo.size(); mentor_no++) {
                                    MentorInfo mentorInfo1 = comingMonthMentorInfo.get(mentor_no);
                                    if (mentorInfo1.getMentor_id().equals(mentor_id)) {
                                        mentorInfo = mentorInfo1;
                                    }
                                }


                                List<Mentee> menteeFoundOnThisDate = new ArrayList<Mentee>();  /* In this list of mentee, there can be only one mentee as in mentee schedule assuming to show only his info */


                                if (eventList.size() > 0) {
                                    Event event = eventList.get(0);
                                    event_id = event.getEvent_id();
                                    sub_category_name = event.getSub_category_name();

                                    List<Mentee> mentees = event.getMentees();

                                    Mentee mentee = mentees.get(0);   /* Assuming only one mentee in the case of mentee schedule as it is not feasible to show other mentee info to one */
                                    List<EventDuration> eventDurations = mentee.getEventDurations();
                                    level_event_duration:
                                    for (int event_duration_part = 0; event_duration_part < eventDurations.size(); event_duration_part++) {  /* One mentee can have event in one or many classes in subsection. Here subsection means, while placing a schedule with mentor if any vacation found during slot then there that event get discontinuity and leads to a subsection*/
                                        EventDuration eventDuration = eventDurations.get(event_duration_part);

                                        String eventDuration_start_date = eventDuration.getStart_date();
                                        Calendar calendar_event_start = Calendar.getInstance();
                                        calendar_event_start.set(Integer.parseInt(eventDuration_start_date.split("-")[0]), Integer.parseInt(eventDuration_start_date.split("-")[1]) - 1, Integer.parseInt(eventDuration_start_date.split("-")[2]));

                                        String eventDuration_stop_date = eventDuration.getStop_date();
                                        Calendar calendar_event_stop = Calendar.getInstance();
                                        calendar_event_stop.set(Integer.parseInt(eventDuration_stop_date.split("-")[0]), Integer.parseInt(eventDuration_stop_date.split("-")[1]) - 1, Integer.parseInt(eventDuration_stop_date.split("-")[2]));

                                        ArrayList<SlotDurationDetailBean> slotDurationDetailBeans = calculateNoOfTotalClassDays(calendar_event_start, calendar_event_stop, slot_week_days);  /* active class dates found between two dates by matching slot weekdays*/
                                        for (int day_no = 0; day_no < slotDurationDetailBeans.size(); day_no++) {
                                            SlotDurationDetailBean slotDurationDetailBean = slotDurationDetailBeans.get(day_no);
                                            String date = slotDurationDetailBean.getDate();
                                            Calendar calendar_for_this_date = Calendar.getInstance();
                                            calendar_for_this_date.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) - 1, Integer.parseInt(date.split("-")[2]));
                                            long this_date_in_millis = calendar_for_this_date.getTimeInMillis();

                                            if ((Integer.parseInt(date.split("-")[0]) == newYear) || (Integer.parseInt(date.split("-")[1]) == newMonth) || (Integer.parseInt(date.split("-")[2]) == day_of_this_month)) {
                                                menteeFoundOnThisDate.add(mentee);
                                                break level_event_duration;
                                            }
                                            /*
                                            if (this_date_in_millis == this_day_in_millis) {
                                                               *//*this mentee is having class on this day *//*
                                                menteeFoundOnThisDate.add(mentee);
                                                break level_event_duration;
                                            }*/
                                        }
                                    }


                                    if (menteeFoundOnThisDate.size() > 0) { /* This proves that there is one event on this day */
                                        availabilityFlags.event_found = true;
                                    }


                                }
                                List<Vacation> coinciding_vacation_of_this_day_for_this_slot = new ArrayList<Vacation>();
                                if (!availabilityFlags.event_found) {
                                    if (vacationList.size() > 0) {
                                        for (int coinciding_vacation = 0; coinciding_vacation < vacationList.size(); coinciding_vacation++) {
                                            Vacation vacation = vacationList.get(coinciding_vacation);

                                            String coin_vac_start_date = vacation.getStart_date();
                                            Calendar cal_coin_vac_start_date = Calendar.getInstance();
                                            cal_coin_vac_start_date.set(Integer.parseInt(coin_vac_start_date.split("-")[0]), Integer.parseInt(coin_vac_start_date.split("-")[1]) - 1, Integer.parseInt(coin_vac_start_date.split("-")[2]));
                                            long cal_coin_vac_start_millis = cal_coin_vac_start_date.getTimeInMillis();

                                            Calendar calendar_for_day_of_this_month2 = Calendar.getInstance();   /* each day of this month will get initialized as loop executes. For each day, matching the possible events or vacation coming for the mentee */
                                            calendar_for_day_of_this_month2.set(newYear, newMonth - 1, day_of_this_month);
                                            long this_day_in_millis2 = calendar_for_day_of_this_month2.getTimeInMillis();
                                            int this_day_week_day2 = calendar_for_day_of_this_month2.get(Calendar.DAY_OF_WEEK);


                                            String coin_vac_stop_date = vacation.getStop_date();
                                            Calendar cal_coin_vac_stop_date = Calendar.getInstance();
                                            cal_coin_vac_stop_date.set(Integer.parseInt(coin_vac_stop_date.split("-")[0]), Integer.parseInt(coin_vac_stop_date.split("-")[1]) - 1, Integer.parseInt(coin_vac_stop_date.split("-")[2]));
                                            long cal_coin_vac_stop_millis = cal_coin_vac_stop_date.getTimeInMillis();

                                            if ((this_day_in_millis2 == cal_coin_vac_start_millis) || (this_day_in_millis2 == cal_coin_vac_stop_millis) || (this_day_in_millis2 > cal_coin_vac_start_millis && this_day_in_millis2 < cal_coin_vac_stop_millis)) {
                                                        /* Vacation found for this slot of the day*/
                                                coinciding_vacation_of_this_day_for_this_slot.add(vacation);
                                                availabilityFlags.vacation_found = true;

                                            }
                                        }

                                    }
                                }


                                if (coinciding_vacation_of_this_day_for_this_slot.size() > 0) {
                                    availabilityFlags.vacation_found = true;
                                }


                                if (availabilityFlags.event_found) {
                                        /* event found*/
                                    Calendar startTime;
                                    startTime = Calendar.getInstance();
                                    startTime.set(Calendar.DAY_OF_MONTH, day_of_this_month);
                                    startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                    startTime.set(Calendar.MINUTE, Integer.parseInt(slot_start_time.split(":", 3)[1]));
                                    startTime.set(Calendar.MONTH, newMonth - 1);
                                    startTime.set(Calendar.YEAR, newYear);
                                    Calendar endTime;// = (Calendar) startTime.clone();
                                    endTime = (Calendar) startTime.clone();
                                    endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_stop_time.split(":", 3)[0]) - Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                    endTime.set(Calendar.MINUTE, Integer.parseInt(slot_stop_time.split(":", 3)[1]));
                                    WeekViewEvent weekViewEvent;
                                    weekViewEvent = new WeekViewEvent(Long.parseLong(event_id), getMenteeEventTitle(startTime, Integer.parseInt(slot_stop_time.split(":", 3)[0]), Integer.parseInt(slot_stop_time.split(":", 3)[1]), sub_category_name), startTime, endTime, new_slot, mentorInfo, menteeFoundOnThisDate, 103);
                                    weekViewEvent.setColor(getResources().getColor(R.color.category_selected));
                                    events.add(weekViewEvent);
                                } else {
                                    if (availabilityFlags.vacation_found) {
                                            /* vacation found*/
                                        Calendar startTime;
                                        startTime = Calendar.getInstance();
                                        startTime.set(Calendar.DAY_OF_MONTH, day_of_this_month);
                                        startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                        startTime.set(Calendar.MINUTE, Integer.parseInt(slot_start_time.split(":", 3)[1]));
                                        startTime.set(Calendar.MONTH, newMonth - 1);
                                        startTime.set(Calendar.YEAR, newYear);
                                        Calendar endTime;// = (Calendar) startTime.clone();
                                        endTime = (Calendar) startTime.clone();
                                        endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_stop_time.split(":", 3)[0]) - Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                        endTime.set(Calendar.MINUTE, Integer.parseInt(slot_stop_time.split(":", 3)[1]));
                                        WeekViewEvent weekViewEvent;
                                        weekViewEvent = new WeekViewEvent(Long.parseLong(slot_id), getMenteeVacationTitle(startTime, Integer.parseInt(slot_stop_time.split(":", 3)[0]), Integer.parseInt(slot_stop_time.split(":", 3)[1])), startTime, endTime, new_slot, mentorInfo, 105, coinciding_vacation_of_this_day_for_this_slot);
                                        weekViewEvent.setColor(getResources().getColor(R.color.purple_light));
                                        events.add(weekViewEvent);

                                    }
                                }


                            }
                        }

                    }


                }
            } else {
                Log.e(TAG, "Network status false found while populating week_view for " + "month: " + newMonth + " year: " + newYear + "this is found while populating weekView for mentee and in next month");
            }
        } else {
                /* There is no slot for this month,so mentee does not have any event on this day*/
        }
    }

    private void populateWeekViewForMentorSchedule(ArrayList<Slot> coming_month, ArrayList<Vacation> coming_month_non_coinciding_vacations, int number_of_days_in_this_month, List<WeekViewEvent> events, int newMonth, int newYear) {
        try {
            if (coming_month.size() > 0) {     /* here coming_month can have slots for previous, current or coming in three different cases and coming_month_non_coinciding_vacations can also of three types i.e. previous, current or coming */
                Slot slot = coming_month.get(0);
                if (Boolean.parseBoolean(slot.isSlot_created_on_network_success())) {  /* Checking whether the slots came in this month are either on network success as when there is network failure i am adding one slot with flag for network communication as false */


                    for (int day_of_this_month = 1; day_of_this_month <= number_of_days_in_this_month; day_of_this_month++) { /* This for loop will iterate through first to last day of next month. For each day what possible class, event or vacation can be possible will bet populated*/
                        ArrayList<String> coinciding_vacation_vacation_id_for_this_day = new ArrayList<String>();

                        Log.d(TAG, "coming month size: " + coming_month.size());
                        for (int slot_number = 0; slot_number < coming_month.size(); slot_number++) {   /*Will match possible slot, event or coinciding vacation for the matching slot of this day. */


                            AvailabilityFlags availabilityFlags = new AvailabilityFlags();
                            Slot new_slot = coming_month.get(slot_number);

                            String[] slot_week_days = new_slot.getSlot_week_days();
                            String slot_id = new_slot.getSlot_id();
                            String start_date = new_slot.getSlot_start_date();
                            String slot_start_time = new_slot.getSlot_start_time();
                            String slot_stop_time = new_slot.getSlot_stop_time();
                            String sub_category_name = null;  /* slot scheduled for which type of subject, it get decided from event */
                            String event_id = null;
                            String slot_type = new_slot.getSlot_type();
                            String slot_max_users = new_slot.getSlot_max_users();

                            Calendar calendar_slot_start_date = Calendar.getInstance();
                            calendar_slot_start_date.set(Integer.parseInt(start_date.split("-")[0]), Integer.parseInt(start_date.split("-")[1]) - 1, Integer.parseInt(start_date.split("-")[2]));
                            long slot_start_date_in_millis = calendar_slot_start_date.getTimeInMillis();


                            Calendar calendar_for_day_of_this_month = Calendar.getInstance();   /* each day of this month will get initialized as loop executes. For each day, matching the possible events or vacation coming for the mentee */
                            calendar_for_day_of_this_month.set(newYear, newMonth - 1, day_of_this_month);  /*creating each day instance, as loop iterates  */
                            long this_day_in_millis = calendar_for_day_of_this_month.getTimeInMillis();
                            int this_day_week_day = calendar_for_day_of_this_month.get(Calendar.DAY_OF_WEEK);
                            Log.d(TAG, "day_of_this_month: " + day_of_this_month + ", month :" + newMonth + ", year: " + newYear + ", week_Day: " + this_day_week_day);


                            String stop_date = new_slot.getSlot_stop_date();
                            Calendar calendar_slot_stop_date = Calendar.getInstance();
                            calendar_slot_stop_date.set(Integer.parseInt(stop_date.split("-")[0]), Integer.parseInt(stop_date.split("-")[1]) - 1, Integer.parseInt(stop_date.split("-")[2]));
                            long slot_stop_date_in_millis = calendar_slot_stop_date.getTimeInMillis();


                            Log.d(TAG, "slot start_date: " + start_date + "slot stop date: " + new_slot.getSlot_stop_date() + ", slot_start_time: " + slot_start_time + ", slot_stop_time: " + slot_stop_time);
                            Log.d(TAG, "slot_start_date_in_millis: " + slot_start_date_in_millis + ", slot_stop_date_in_millis: " + slot_stop_date_in_millis + ", this day in millis" + this_day_in_millis);
                            if ((this_day_in_millis == slot_start_date_in_millis) || (this_day_in_millis == slot_stop_date_in_millis) || (this_day_in_millis > slot_start_date_in_millis && this_day_in_millis < slot_stop_date_in_millis)) {
                                /*this day is coming in between slot duration, now to check whether the week_day of this day is one of the slot week_days, which will prove this day is slot or not  */

                                Log.d(TAG, "this day matches ");
                                if (thisDayMatchesWithArrayOfWeekDays(slot_week_days, this_day_week_day)) {
                                    Log.d(TAG, "slot day matches with this day");
                                    availabilityFlags.slot_found = true;  /* making slot_found flag true for this day */
                                    List<Event> eventList = new_slot.getEvents();
                                    List<Vacation> coincidingVacationList = new_slot.getVacations();
                                    String[] slot_on_week_days = new_slot.getSlot_week_days();
                                    List<Mentee> menteeFoundOnThisDate = new ArrayList<Mentee>();  /* a list of mentee's who have class for this day of week_view */

                                    if (eventList.size() > 0) {
                                        /* Event found, now to check whether this day is coming between one of the event duration for any of mentee or not */
                                        Event event = eventList.get(0);  /* Only one event is expected on a slot. There can be more than one mentee on this event*/
                                        event_id = event.getEvent_id();
                                        sub_category_name = event.getSub_category_name();

                                        List<Mentee> mentees = event.getMentees();

                                        for (int mentee_no = 0; mentee_no < mentees.size(); mentee_no++) {
                                            Mentee mentee = mentees.get(mentee_no);
                                            List<EventDuration> eventDurations = mentee.getEventDurations();

                                            level_event_duration:
                                            for (int event_duration_part = 0; event_duration_part < eventDurations.size(); event_duration_part++) {  /* One mentee can have event in one or many classes in subsection. Here subsection means, while placing a schedule with mentor if any vacation found during slot then there that event get discontinuity and leads to a subsection*/
                                                EventDuration eventDuration = eventDurations.get(event_duration_part);

                                                String eventDuration_start_date = eventDuration.getStart_date();
                                                Calendar calendar_event_start = Calendar.getInstance();
                                                calendar_event_start.set(Integer.parseInt(eventDuration_start_date.split("-")[0]), Integer.parseInt(eventDuration_start_date.split("-")[1]) - 1, Integer.parseInt(eventDuration_start_date.split("-")[2]));

                                                String eventDuration_stop_date = eventDuration.getStop_date();
                                                Calendar calendar_event_stop = Calendar.getInstance();
                                                calendar_event_stop.set(Integer.parseInt(eventDuration_stop_date.split("-")[0]), Integer.parseInt(eventDuration_stop_date.split("-")[1]) - 1, Integer.parseInt(eventDuration_stop_date.split("-")[2]));

                                                ArrayList<SlotDurationDetailBean> slotDurationDetailBeans = calculateNoOfTotalClassDays(calendar_event_start, calendar_event_stop, slot_on_week_days);  /* active class dates found between two dates by matching slot weekdays*/
                                                for (int day_no = 0; day_no < slotDurationDetailBeans.size(); day_no++) {
                                                    SlotDurationDetailBean slotDurationDetailBean = slotDurationDetailBeans.get(day_no);
                                                    String date = slotDurationDetailBean.getDate();

                                                    Calendar calendar_for_this_date = Calendar.getInstance();
                                                    calendar_for_this_date.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) - 1, Integer.parseInt(date.split("-")[2]));
                                                    long this_date_in_millis = calendar_for_this_date.getTimeInMillis();

                                                    if ((Integer.parseInt(date.split("-")[0]) == newYear) && ((Integer.parseInt(date.split("-")[1])) == (newMonth - 1)) && (Integer.parseInt(date.split("-")[2]) == day_of_this_month)) {
                                                        menteeFoundOnThisDate.add(mentee);
                                                        break level_event_duration;
                                                    }

                                                    /*
                                                    if (this_date_in_millis == this_day_in_millis) {
                                                               *//*this mentee is having class on this day *//*
                                                        menteeFoundOnThisDate.add(mentee);
                                                        break level_event_duration;
                                                    }*/
                                                }
                                            }
                                        }

                                        if (menteeFoundOnThisDate.size() > 0) { /* This proves that there is some event on this day also*/
                                            availabilityFlags.event_found = true;
                                        }


                                    } else {
                                        /* No event scheduled till now for this slot*/
                                    }

                                    List<Vacation> coinciding_vacation_of_this_day_for_this_slot = new ArrayList<Vacation>();


                                    if (!availabilityFlags.event_found) {
                                        /* will check for any coinciding vacation if there is no mentee found on this day */
                                        if (coincidingVacationList.size() > 0) {
                                            /* Now to decide whether the vacation is coming on this day or not */
                                            for (int coinciding_vacation = 0; coinciding_vacation < coincidingVacationList.size(); coinciding_vacation++) {
                                                Vacation vacation = coincidingVacationList.get(coinciding_vacation);

                                                String coin_vac_start_date = vacation.getStart_date();
                                                Calendar cal_coin_vac_start_date = Calendar.getInstance();
                                                cal_coin_vac_start_date.set(Integer.parseInt(coin_vac_start_date.split("-")[0]), Integer.parseInt(coin_vac_start_date.split("-")[1]) - 1, Integer.parseInt(coin_vac_start_date.split("-")[2]));
                                                long cal_coin_vac_start_millis = cal_coin_vac_start_date.getTimeInMillis();

                                                Calendar calendar_for_day_of_this_month2 = Calendar.getInstance();   /* each day of this month will get initialized as loop executes. For each day, matching the possible events or vacation coming for the mentee */
                                                calendar_for_day_of_this_month2.set(newYear, newMonth - 1, day_of_this_month);  /*creating each day instance, as loop iterates  */
                                                long this_day_in_millis2 = calendar_for_day_of_this_month2.getTimeInMillis();
                                                int this_day_week_day2 = calendar_for_day_of_this_month2.get(Calendar.DAY_OF_WEEK);
                                                Log.d(TAG, "day_of_this_month: " + day_of_this_month + ", month :" + newMonth + ", year: " + newYear + ", week_Day: " + this_day_week_day);


                                                String coin_vac_stop_date = vacation.getStop_date();
                                                Calendar cal_coin_vac_stop_date = Calendar.getInstance();
                                                cal_coin_vac_stop_date.set(Integer.parseInt(coin_vac_stop_date.split("-")[0]), Integer.parseInt(coin_vac_stop_date.split("-")[1]) - 1, Integer.parseInt(coin_vac_stop_date.split("-")[2]));
                                                long cal_coin_vac_stop_millis = cal_coin_vac_stop_date.getTimeInMillis();

                                                if ((this_day_in_millis2 == cal_coin_vac_start_millis) || (this_day_in_millis2 == cal_coin_vac_stop_millis) || (this_day_in_millis2 > cal_coin_vac_start_millis && this_day_in_millis2 < cal_coin_vac_stop_millis)) {
                                                        /* Vacation found for this slot of the day*/
                                                    coinciding_vacation_of_this_day_for_this_slot.add(vacation);
                                                    coinciding_vacation_vacation_id_for_this_day.add(vacation.getVacation_id());
                                                    availabilityFlags.vacation_found = true;

                                                }
                                            }
                                        }
                                    }

                                    /* Now to check which type of event we have to show for this slot*/

                                    if (availabilityFlags.slot_found) {
                                        if (availabilityFlags.event_found) {
                                            /* Class found for this slot, making class information on week view */
                                            Calendar startTime;
                                            startTime = Calendar.getInstance();
                                            startTime.set(Calendar.DAY_OF_MONTH, day_of_this_month);
                                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                            startTime.set(Calendar.MINUTE, Integer.parseInt(slot_start_time.split(":", 3)[1]));
                                            startTime.set(Calendar.MONTH, newMonth - 1);
                                            startTime.set(Calendar.YEAR, newYear);
                                            Calendar endTime;// = (Calendar) startTime.clone();
                                            endTime = (Calendar) startTime.clone();
                                            endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_stop_time.split(":", 3)[0]) - Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                            endTime.set(Calendar.MINUTE, Integer.parseInt(slot_stop_time.split(":", 3)[1]));
                                            WeekViewEvent weekViewEvent;
                                            weekViewEvent = new WeekViewEvent(Long.parseLong(event_id), getEventTitle(startTime, Integer.parseInt(slot_stop_time.split(":", 3)[0]), Integer.parseInt(slot_stop_time.split(":", 3)[1])), startTime, endTime, menteeFoundOnThisDate, slot_type, new_slot, 12345);  /* For making a scheduled class information on week view */
                                            weekViewEvent.setColor(getResources().getColor(R.color.category_selected));
                                            events.add(weekViewEvent);

                                        } else {
                                            if (availabilityFlags.vacation_found) {
                                                /* vacation found on this slot */
                                                Calendar startTime;
                                                startTime = Calendar.getInstance();
                                                startTime.set(Calendar.DAY_OF_MONTH, day_of_this_month);
                                                startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                                startTime.set(Calendar.MINUTE, Integer.parseInt(slot_start_time.split(":", 3)[1]));
                                                startTime.set(Calendar.MONTH, newMonth - 1);
                                                startTime.set(Calendar.YEAR, newYear);
                                                Calendar endTime;// = (Calendar) startTime.clone();
                                                endTime = (Calendar) startTime.clone();
                                                endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_stop_time.split(":", 3)[0]) - Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                                endTime.set(Calendar.MINUTE, Integer.parseInt(slot_stop_time.split(":", 3)[1]));
                                                WeekViewEvent weekViewEvent;
                                                weekViewEvent = new WeekViewEvent(Long.parseLong(slot_id), getVacationTitleForCoincidingVacation(startTime, Integer.parseInt(slot_stop_time.split(":", 3)[0]), Integer.parseInt(slot_stop_time.split(":", 3)[1])), startTime, endTime, coinciding_vacation_of_this_day_for_this_slot, new_slot, 123);  /* For making a coinciding vacation information on week view. I am showing vacation for slot_duration time i.e. if any vacation coming for just some fraction of time of slot time, in this case also i am showing vacation for full slot time  */
                                                weekViewEvent.setColor(getResources().getColor(R.color.purple_light));
                                                events.add(weekViewEvent);
                                            } else {
                                                /* neither any vacation nor any class scheduled till now */
                                                Calendar startTime;
                                                startTime = Calendar.getInstance();
                                                startTime.set(Calendar.DAY_OF_MONTH, day_of_this_month);
                                                startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                                startTime.set(Calendar.MINUTE, Integer.parseInt(slot_start_time.split(":", 3)[1]));
                                                startTime.set(Calendar.MONTH, newMonth - 1);
                                                startTime.set(Calendar.YEAR, newYear);
                                                Calendar endTime;// = (Calendar) startTime.clone();
                                                endTime = (Calendar) startTime.clone();
                                                endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_stop_time.split(":", 3)[0]) - Integer.parseInt(slot_start_time.split(":", 3)[0]));
                                                endTime.set(Calendar.MINUTE, Integer.parseInt(slot_stop_time.split(":", 3)[1]));
                                                WeekViewEvent weekViewEvent;
                                                weekViewEvent = new WeekViewEvent(Long.parseLong(slot_id), getSlotTitle(start_date, stop_date, slot_start_time, slot_stop_time, slot_type, slot_week_days, slot_max_users), startTime, endTime, new_slot, 432);  /* For making a free slot information on week view */
                                                weekViewEvent.setColor(getResources().getColor(R.color.grey_light));
                                                events.add(weekViewEvent);
                                            }
                                        }
                                    }


                                }
                            } else {
                                /* this day is not coming between the duration for this slot */
                            }
                        }

                        if (coming_month_non_coinciding_vacations.size() > 0) {  /* There can be some non coinciding vacation  */
                            list_of_vacation_iterator:
                            for (int vacation_no = 0; vacation_no < coming_month_non_coinciding_vacations.size(); vacation_no++) {


                                Vacation vacation = coming_month_non_coinciding_vacations.get(vacation_no);
                                String vacation_start_date = vacation.getStart_date();
                                String vacation_stop_date = vacation.getStop_date();

                                String vacation_start_time = vacation.getStart_time();
                                String vacation_stop_time = vacation.getStop_time();
                                String vacation_id = vacation.getVacation_id();  /* Only using this in non coinciding vacation*/


                                for (int count = 0; count < coinciding_vacation_vacation_id_for_this_day.size(); count++) {
                                /* Checking for the coinciding vacation id among this list of vacation.
                                If any coinciding vacation id matches among any of the non coinciding vacation id then we do not need to populate again */
                                    if (coinciding_vacation_vacation_id_for_this_day.get(count).equals(vacation_id)) {
                                        continue list_of_vacation_iterator;
                                    }
                                }


                                Calendar calendar_vacation_start_date = Calendar.getInstance();
                                calendar_vacation_start_date.set(Integer.parseInt(vacation_start_date.split("-")[0]), Integer.parseInt(vacation_start_date.split("-")[1]) - 1, Integer.parseInt(vacation_start_date.split("-")[2]));
                                long vac_start_date_millis = calendar_vacation_start_date.getTimeInMillis();


                                Calendar calendar_for_day_of_this_month = Calendar.getInstance();   /* each day of this month will get initialized as loop executes. For each day, matching the possible events or vacation coming for the mentee */
                                calendar_for_day_of_this_month.set(newYear, newMonth - 1, day_of_this_month);  /*creating each day instance, as loop iterates  */
                                long this_day_in_millis = calendar_for_day_of_this_month.getTimeInMillis();
                                int this_day_week_day = calendar_for_day_of_this_month.get(Calendar.DAY_OF_WEEK);
                                Log.d(TAG, "day_of_this_month: " + day_of_this_month + ", month :" + newMonth + ", year: " + newYear + ", week_Day: " + this_day_week_day);


                                Calendar calendar_vacation_stop_date = Calendar.getInstance();
                                calendar_vacation_stop_date.set(Integer.parseInt(vacation_stop_date.split("-")[0]), Integer.parseInt(vacation_stop_date.split("-")[1]) - 1, Integer.parseInt(vacation_stop_date.split("-")[2]));
                                long vac_stop_date_millis = calendar_vacation_stop_date.getTimeInMillis();


                                if ((this_day_in_millis == vac_start_date_millis) || (this_day_in_millis == vac_stop_date_millis) || (this_day_in_millis > vac_start_date_millis && this_day_in_millis < vac_stop_date_millis)) {
                                         /* this prove non coinciding vacation on this day */
                                    Calendar startTime;
                                    startTime = Calendar.getInstance();
                                    startTime.set(Calendar.DAY_OF_MONTH, day_of_this_month);
                                    startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(vacation_start_time.split(":", 3)[0]));
                                    startTime.set(Calendar.MINUTE, Integer.parseInt(vacation_start_time.split(":", 3)[1]));
                                    startTime.set(Calendar.MONTH, newMonth - 1);
                                    startTime.set(Calendar.YEAR, newYear);
                                    Calendar endTime;// = (Calendar) startTime.clone();
                                    endTime = (Calendar) startTime.clone();
                                    endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(vacation_stop_time.split(":", 3)[0]) - Integer.parseInt(vacation_start_time.split(":", 3)[0]));
                                    endTime.set(Calendar.MINUTE, Integer.parseInt(vacation_stop_time.split(":", 3)[1]));
                                    WeekViewEvent weekViewEvent;
                                    weekViewEvent = new WeekViewEvent(Long.parseLong(vacation_id), getVacationTitle(startTime, Integer.parseInt(vacation_stop_time.split(":", 3)[0]), Integer.parseInt(vacation_stop_time.split(":", 3)[1])), startTime, endTime, vacation, 21);  /* For making a non coinciding vacation over weekview */
                                    weekViewEvent.setColor(getResources().getColor(R.color.purple));
                                    events.add(weekViewEvent);
                                }


                            }
                        }


                    }
                } else {
                    Log.e(TAG, "Network status false found while populating week_view for " + "month: " + newMonth + " year: " + newYear + "This is found while populating weekView for mentor='s next month");
                }
            } else {
                /* There is no slot for this month,so mentee does not have any event on this day*/

                if (coming_month_non_coinciding_vacations.size() > 0) {

                    Log.d(TAG, "inside SetScheueduleActivity and populate non_coinciding vac, size of array" + coming_month_non_coinciding_vacations.size() + "days in month" + number_of_days_in_this_month);

                    Vacation vacation = coming_month_non_coinciding_vacations.get(0);
                    Log.d(TAG, "network value: " + vacation.getVacation_made_at_network_success());
                    if (Boolean.parseBoolean(vacation.getVacation_made_at_network_success())) {
                        Log.d(TAG, "vacation found on network success");
                        for (int day_of_this_month = 1; day_of_this_month <= number_of_days_in_this_month; day_of_this_month++) {
                         /* This for loop will iterate through first to last day of next month. For each day what possible class, event or vacation can be possible will bet populated*/


                            for (int non_coinciding_vacation = 0; non_coinciding_vacation < coming_month_non_coinciding_vacations.size(); non_coinciding_vacation++) {
                                Vacation vacation1 = coming_month_non_coinciding_vacations.get(non_coinciding_vacation);
                                String vacation_start_date = vacation1.getStart_date();
                                String vacation_stop_date = vacation1.getStop_date();
                                String vacation_start_time = vacation1.getStart_time();
                                String vacation_stop_time = vacation1.getStop_time();
                                String vacation_id = vacation1.getVacation_id();  /* Only using this in non coinciding vacation*/


                                Calendar calendar_vacation_start_date = Calendar.getInstance();
                                calendar_vacation_start_date.set(Integer.parseInt(vacation_start_date.split("-")[0]), Integer.parseInt(vacation_start_date.split("-")[1]) - 1, Integer.parseInt(vacation_start_date.split("-")[2]));
                                long vac_start_date_millis = calendar_vacation_start_date.getTimeInMillis();


                                Calendar calendar_for_day_of_this_month = Calendar.getInstance();   /* each day of this month will get initialized as loop executes. For each day, matching the possible events or vacation coming for the mentee */
                                calendar_for_day_of_this_month.set(newYear, newMonth - 1, day_of_this_month);  /*creating each day instance, as loop iterates  */
                                long this_day_in_millis = calendar_for_day_of_this_month.getTimeInMillis();
                                int this_day_week_day = calendar_for_day_of_this_month.get(Calendar.DAY_OF_WEEK);


                                Calendar calendar_vacation_stop_date = Calendar.getInstance();
                                calendar_vacation_stop_date.set(Integer.parseInt(vacation_stop_date.split("-")[0]), Integer.parseInt(vacation_stop_date.split("-")[1]) - 1, Integer.parseInt(vacation_stop_date.split("-")[2]));
                                long vac_stop_date_millis = calendar_vacation_stop_date.getTimeInMillis();


                                if ((this_day_in_millis == vac_start_date_millis) || (this_day_in_millis == vac_stop_date_millis) || (this_day_in_millis > vac_start_date_millis && this_day_in_millis < vac_stop_date_millis)) {
                                         /* this prove non coinciding vacation on this day */
                                    Calendar startTime;
                                    startTime = Calendar.getInstance();
                                    startTime.set(Calendar.DAY_OF_MONTH, day_of_this_month);
                                    startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(vacation_start_time.split(":", 3)[0]));
                                    startTime.set(Calendar.MINUTE, Integer.parseInt(vacation_start_time.split(":", 3)[1]));
                                    startTime.set(Calendar.MONTH, newMonth - 1);
                                    startTime.set(Calendar.YEAR, newYear);
                                    Calendar endTime;// = (Calendar) startTime.clone();
                                    endTime = (Calendar) startTime.clone();
                                    endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(vacation_stop_time.split(":", 3)[0]) - Integer.parseInt(vacation_start_time.split(":", 3)[0]));
                                    endTime.set(Calendar.MINUTE, Integer.parseInt(vacation_stop_time.split(":", 3)[1]));
                                    WeekViewEvent weekViewEvent;
                                    weekViewEvent = new WeekViewEvent(Long.parseLong(vacation_id), getVacationTitle(startTime, Integer.parseInt(vacation_stop_time.split(":", 3)[0]), Integer.parseInt(vacation_stop_time.split(":", 3)[1])), startTime, endTime, vacation, 21);  /* For making a non coinciding vacation over weekview */
                                    weekViewEvent.setColor(getResources().getColor(R.color.purple));
                                    events.add(weekViewEvent);
                                }

                            }

                        }

                    }
                }
            }
        } catch (Exception e) {

        }

    }

    private void populateWeekViewForNextMonth1(List<WeekViewEvent> events, int newYear, int newMonth, int coming_month_days) {


        poplateWeekView(coming_month, coming_month_days, comingMonthMentorInfo, events, newYear, newMonth);

    }


    /**
     * This will populate week_view for free slot representation in mentee's Mentor's availability calendar
     */

    private void poplateWeekView(ArrayList<Slot> month, int days_in_month, ArrayList<MentorInfo> month_mentor_info, List<WeekViewEvent> events, int newYear, int newMonth) {
        if (month.size() > 0) {  /* Firstly checking whether there is any slot available in this month or not*/
            Slot slot = month.get(0);
            if (Boolean.parseBoolean(slot.isSlot_created_on_network_success())) {  /* If any slot is there then to check whether it is created on network success or not */
                for (int day_of_month = 1; day_of_month <= days_in_month; day_of_month++) {


                    for (int slot_no = 0; slot_no < month.size(); slot_no++) {
                        Slot new_Slot = month.get(slot_no);
                        String slot_start_date = new_Slot.getSlot_start_date();
                        String slot_stop_date = new_Slot.getSlot_stop_date();
                        String slot_week_days[] = new_Slot.getSlot_week_days();
                        String slot_subject = new_Slot.getSlot_subject();

                        Calendar cal_slot_start_date = Calendar.getInstance();
                        cal_slot_start_date.set(Integer.parseInt(slot_start_date.split("-")[0]), Integer.parseInt(slot_start_date.split("-")[1]) - 1, Integer.parseInt(slot_start_date.split("-")[2]));
                        long cal_slot_start_date_millis = cal_slot_start_date.getTimeInMillis();


                        Calendar calendar_new_day_of_week_view = Calendar.getInstance();
                        calendar_new_day_of_week_view.set(newYear, newMonth - 1, day_of_month);
                        long this_day_in_millis = calendar_new_day_of_week_view.getTimeInMillis();
                        int this_day_week_day = calendar_new_day_of_week_view.get(Calendar.DAY_OF_WEEK);


                        Calendar cal_slot_stop_date = Calendar.getInstance();
                        cal_slot_stop_date.set(Integer.parseInt(slot_stop_date.split("-")[0]), Integer.parseInt(slot_stop_date.split("-")[1]) - 1, Integer.parseInt(slot_stop_date.split("-")[2]));
                        long cal_slot_stop_date_millis = cal_slot_stop_date.getTimeInMillis();


                        if ((this_day_in_millis == cal_slot_start_date_millis) || (this_day_in_millis == cal_slot_stop_date_millis) || (this_day_in_millis > cal_slot_start_date_millis && this_day_in_millis < cal_slot_stop_date_millis)) {
                            if (thisDayMatchesWithArrayOfWeekDays(slot_week_days, this_day_week_day)) {
                                /* this day is matching with this slot */
                                String slot_type = new_Slot.getSlot_type();
                                String slot_mentor_id = new_Slot.getMentor_id();
                                MentorInfo mentorInfo = new MentorInfo();   /* Mentor information related to this slot */
                                for (int mentor_no = 0; mentor_no < month_mentor_info.size(); mentor_no++) {
                                    MentorInfo mentorInfo1 = month_mentor_info.get(mentor_no);
                                    if (mentorInfo1.getMentor_id().equals(slot_mentor_id)) {
                                        mentorInfo = mentorInfo1;
                                    }
                                }


                                if (slot_type.equalsIgnoreCase("group")) {
                                    int max_users = Integer.parseInt(new_Slot.getSlot_max_users());
                                    List<Event> class_already_scheduled = new_Slot.getEvents();
                                    if (class_already_scheduled.size() > 0) {
                                        Event event = class_already_scheduled.get(0);
                                        int no_of_active_mentee = Integer.parseInt(event.getEvent_total_mentee());
                                        if (no_of_active_mentee < max_users) {
                                         /* Slot is free */

                                            List<Vacation> vacations = new_Slot.getVacations();

                                            if (isAnyVacationCoincide(vacations, newYear, newMonth, day_of_month)) {
                                                /* Due to vacation, this slot cannot be treated as free for this day .*/
                                            } else {
                                                /* Slot is free */
                                                allowSlotOnWeekView(new_Slot, mentorInfo, events, day_of_month, newMonth, newYear);

                                            }
                                        }


                                    } else {
                                        /* No class scheduled hence slot is free*/
                                        List<Vacation> vacations = new_Slot.getVacations();

                                        if (isAnyVacationCoincide(vacations, newYear, newMonth, day_of_month)) {
                                                /* Due to vacation, this slot cannot be treated as free for this day .*/
                                        } else {
                                                /* Slot is free */
                                            allowSlotOnWeekView(new_Slot, mentorInfo, events, day_of_month, newMonth, newYear);

                                        }
                                    }


                                } else {
                                    /* individual group */
                                    List<Event> class_already_scheduled = new_Slot.getEvents();
                                    if (class_already_scheduled.size() > 0) {
                                        Event event = class_already_scheduled.get(0);
                                        int no_of_active_mentee = Integer.parseInt(event.getEvent_total_mentee());
                                        if (no_of_active_mentee > 0) {
                                            /* As slot is individual so slot is not free in this case*/
                                        } else {
                                            /* No active users so slot is free */
                                            List<Vacation> vacations = new_Slot.getVacations();

                                            if (isAnyVacationCoincide(vacations, newYear, newMonth, day_of_month)) {
                                                /* Due to vacation, this slot cannot be treated as free for this day .*/
                                            } else {
                                                /* Slot is free */
                                                allowSlotOnWeekView(new_Slot, mentorInfo, events, day_of_month, newMonth, newYear);

                                            }
                                        }

                                    } else {
                                        /* No event found hence slot is free */
                                        List<Vacation> vacations = new_Slot.getVacations();

                                        if (isAnyVacationCoincide(vacations, newYear, newMonth, day_of_month)) {
                                                /* Due to vacation,this slot cannot be treated as free for this day .*/
                                        } else {
                                                /* Slot is free */
                                            allowSlotOnWeekView(new_Slot, mentorInfo, events, day_of_month, newMonth, newYear);

                                        }
                                    }
                                }


                            }
                        }

                    }


                }
            }
        }


    }

    private void allowSlotOnWeekView(Slot new_slot, MentorInfo mentorInfo, List<WeekViewEvent> events, int day_of_month, int newMonth, int newYear) {
        String slot_start_time = new_slot.getSlot_start_time();
        String slot_stop_time = new_slot.getSlot_stop_time();
        String slot_id = new_slot.getSlot_id();

        Calendar startTime;
        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, day_of_month);
        startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_start_time.split(":", 3)[0]));
        startTime.set(Calendar.MINUTE, Integer.parseInt(slot_start_time.split(":", 3)[1]));
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);

        Calendar endTime;// = (Calendar) startTime.clone();
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, Integer.parseInt(slot_stop_time.split(":", 3)[0]) - Integer.parseInt(slot_start_time.split(":", 3)[0]));
        endTime.set(Calendar.MINUTE, Integer.parseInt(slot_stop_time.split(":", 3)[1]));

        WeekViewEvent weekViewEvent;
        weekViewEvent = new WeekViewEvent(Integer.parseInt(slot_id),
                getFreeSlotTitle(new_slot.getSlot_subject(),
                        Integer.parseInt(new_slot.getSlot_start_date().split("-")[2]),
                        Integer.parseInt(new_slot.getSlot_start_date().split("-")[1]),
                        Integer.parseInt(new_slot.getSlot_start_date().split("-")[0]),
                        Integer.parseInt(new_slot.getSlot_stop_date().split("-")[2]),
                        Integer.parseInt(new_slot.getSlot_stop_date().split("-")[1]),
                        Integer.parseInt(new_slot.getSlot_stop_date().split("-")[0]),
                        Integer.parseInt(new_slot.getSlot_start_time().split(":")[0]),
                        Integer.parseInt(new_slot.getSlot_start_time().split(":")[1]),
                        Integer.parseInt(new_slot.getSlot_stop_time().split(":")[0]),
                        Integer.parseInt(new_slot.getSlot_stop_time().split(":")[1]),
                        new_slot.getSlot_type(), new_slot.getSlot_week_days()),
                startTime, endTime, new_slot, mentorInfo, mentor_id, mentor_availablity,
                202, charges, arrayList_subcategory);
        weekViewEvent.setColor(getResources().getColor(R.color.grey_light));
        events.add(weekViewEvent);
    }

    private boolean isAnyVacationCoincide(List<Vacation> vacations, int new_year, int new_month, int new_day) {
        boolean vacation_found = false;

        for (int vacation_no = 0; vacation_no < vacations.size(); vacation_no++) {
            Vacation vacation = vacations.get(vacation_no);
            String vac_start_date = vacation.getStart_date();
            String vac_stop_date = vacation.getStop_date();

            Calendar cal_vac_start_date = Calendar.getInstance();
            cal_vac_start_date.set(Integer.parseInt(vac_start_date.split("-")[0]), Integer.parseInt(vac_start_date.split("-")[1]) - 1, Integer.parseInt(vac_start_date.split("-")[2]));
            long cal_vac_start_date_millis = cal_vac_start_date.getTimeInMillis();

            Calendar calendar_new_day_of_week_view = Calendar.getInstance();
            calendar_new_day_of_week_view.set(new_year, new_month - 1, new_day);
            long this_day_in_millis1 = calendar_new_day_of_week_view.getTimeInMillis();

            Calendar cal_vac_stop_date = Calendar.getInstance();
            cal_vac_stop_date.set(Integer.parseInt(vac_stop_date.split("-")[0]), Integer.parseInt(vac_stop_date.split("-")[1]) - 1, Integer.parseInt(vac_stop_date.split("-")[2]));
            long cal_vac_stop_date_millis = cal_vac_stop_date.getTimeInMillis();


            if ((this_day_in_millis1 == cal_vac_start_date_millis) ||
                    (this_day_in_millis1 == cal_vac_stop_date_millis) ||
                    (this_day_in_millis1 > cal_vac_start_date_millis && this_day_in_millis1 < cal_vac_stop_date_millis)) {
                vacation_found = true;
                break;
            }
        }
        return vacation_found;
    }


    private ArrayList<SlotDurationDetailBean> calculateNoOfTotalClassDays(Calendar calendar_schedule_start_date, Calendar calendar_stop_date_of_schedule, String[] slot_on_week_days) {

        int workDays = 0;
        ArrayList<SlotDurationDetailBean> slotDurationDetailBeans = new ArrayList<SlotDurationDetailBean>();


        List<Integer> selectedDays = new ArrayList<Integer>();
        for (String d : slot_on_week_days) {
            if (d.equalsIgnoreCase("su"))
                selectedDays.add(1);
            if (d.equalsIgnoreCase("m"))
                selectedDays.add(2);
            if (d.equalsIgnoreCase("t"))
                selectedDays.add(3);
            if (d.equalsIgnoreCase("w"))
                selectedDays.add(4);
            if (d.equalsIgnoreCase("th"))
                selectedDays.add(5);
            if (d.equalsIgnoreCase("f"))
                selectedDays.add(6);
            if (d.equalsIgnoreCase("s"))
                selectedDays.add(7);
        }

        do {
            //excluding start date

            if (selectedDays.contains(calendar_schedule_start_date.get(Calendar.DAY_OF_WEEK))) {
                ++workDays;
                SlotDurationDetailBean slotDurationDetailBean = new SlotDurationDetailBean();
                Log.d(TAG, "year: " + calendar_schedule_start_date.get(Calendar.YEAR) + "month: " + calendar_schedule_start_date.get(Calendar.MONTH) + "day_of_month: " + calendar_schedule_start_date.get(Calendar.DAY_OF_MONTH) + "date: " + calendar_schedule_start_date.get(Calendar.DATE));
                slotDurationDetailBean.setDate(calendar_schedule_start_date.get(Calendar.YEAR) + "-" + calendar_schedule_start_date.get(Calendar.MONTH) + "-" + calendar_schedule_start_date.get(Calendar.DAY_OF_MONTH));
                slotDurationDetailBean.setWeek_day(String.valueOf(calendar_schedule_start_date.get(Calendar.DAY_OF_WEEK)));
                slotDurationDetailBeans.add(slotDurationDetailBean);
            }
            calendar_schedule_start_date.add(Calendar.DAY_OF_MONTH, 1);


        }
        while (calendar_schedule_start_date.getTimeInMillis() <= calendar_stop_date_of_schedule.getTimeInMillis()); //excluding end date

        return slotDurationDetailBeans;
    }


    private void populateWeekViewForCurrentMonth2(List<WeekViewEvent> events, int newYear, int newMonth, int number_of_days_in_this_month) {

        if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 2) {
            populateWeekViewForMenteeSchedule(events, current_month, currentMonthMentorInfo, number_of_days_in_this_month, newMonth, newYear);
        }
        if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 3) {
            populateWeekViewForMentorSchedule(current_month, current_month_non_coinciding_vacations, number_of_days_in_this_month, events, newMonth, newYear);
        }
    }

    private void populateWeekViewForCurrentMonth1(List<WeekViewEvent> events, int newYear, int newMonth, int current_month_days) {
        poplateWeekView(current_month, current_month_days, currentMonthMentorInfo, events, newYear, newMonth);

    }

    private void populateWeekViewForPreviousMonth1(List<WeekViewEvent> events, int newYear, int newMonth, int previous_month_days) {
        poplateWeekView(prev_month, previous_month_days, previousMonthMentorInfo, events, newYear, newMonth);

    }

    private void populateWeekViewForPreviousMonth2(List<WeekViewEvent> events, int newYear, int newMonth, int number_of_days_in_this_month) {
        // Log.d(TAG,"Going to create view for previous month.");
        if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 2) {
            populateWeekViewForMenteeSchedule(events, prev_month, previousMonthMentorInfo, number_of_days_in_this_month, newMonth, newYear);
        }
        if (Integer.parseInt(StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group")) == 3) {
            populateWeekViewForMentorSchedule(prev_month, prev_month_non_coinciding_vacations, number_of_days_in_this_month, events, newMonth, newYear);
        }
    }


    private String getEventTitle(Calendar time, int stop_hour, int stop_min) {

        return String.format("Scheduled event of %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min);


    }


    private String getMenteeEventTitle(Calendar time, int stop_hour, int stop_min, String sub_category_name) {
        if (sub_category_name != null) {
            return String.format("Class time: %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min) + "Subject: " + sub_category_name;
        } else {
            return String.format("Class time: %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min);
        }

    }

    /* Non coinciding vacation */
    private String getVacationTitle(Calendar time, int stop_hour, int stop_min) {

        return String.format("Vacation time: %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min);

    }

    /* Non coinciding vacation */
    private String getVacationTitleForCoincidingVacation(Calendar time, int stop_hour, int stop_min) {

        return String.format("Vacation on slot time: %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min);

    }


    private String getMenteeVacationTitle(Calendar time, int stop_hour, int stop_min) {
        return String.format("Vacation time: %02d:%02d to %02d:%02d \n", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), stop_hour, stop_min);

    }

    private String getSlotTitle(String slot_start_date, String slot_stop_date, String slot_start_time, String slot_stop_time, String slot_type, String[] slot_on_week_days, String slot_max_users) {

        StringBuilder stringBuilder = new StringBuilder();
        for (int slot_week_day = 0; slot_week_day < slot_on_week_days.length; slot_week_day++) {
            String week_day = slot_on_week_days[slot_week_day];

            /* Checking whether the slot_week_day is having last index of slot_on_wee_days array, Objective is to print slot week days in comma separated */
            if (slot_week_day == slot_on_week_days.length - 1) {
                if (week_day.equals("M")) {
                    stringBuilder.append("Mon");
                }
                if (week_day.equals("T")) {
                    stringBuilder.append("Tue");
                }
                if (week_day.equals("W")) {
                    stringBuilder.append("Wed");
                }
                if (week_day.equals("Th")) {
                    stringBuilder.append("Thu");
                }
                if (week_day.equals("F")) {
                    stringBuilder.append("Fri");
                }
                if (week_day.equals("S")) {
                    stringBuilder.append("Sat");
                }
                if (week_day.equals("Su")) {
                    stringBuilder.append("Sun");
                }
            } else {
                if (week_day.equals("M")) {
                    stringBuilder.append("Mon, ");
                }
                if (week_day.equals("T")) {
                    stringBuilder.append("Tue, ");
                }
                if (week_day.equals("W")) {
                    stringBuilder.append("Wed, ");
                }
                if (week_day.equals("Th")) {
                    stringBuilder.append("Thu, ");
                }
                if (week_day.equals("F")) {
                    stringBuilder.append("Fri, ");
                }
                if (week_day.equals("S")) {
                    stringBuilder.append("Sat, ");
                }
                if (week_day.equals("Su")) {
                    stringBuilder.append("Sun, ");
                }
            }
        }


        return String.format("Active Slot: %02d-%02d-%d to %02d-%02d-%d ",
                Integer.parseInt(slot_start_date.split("-", 3)[2]), Integer.parseInt(slot_start_date.split("-", 3)[1]), Integer.parseInt(slot_start_date.split("-", 3)[0]),
                Integer.parseInt(slot_stop_date.split("-", 3)[2]), Integer.parseInt(slot_stop_date.split("-", 3)[1]), Integer.parseInt(slot_stop_date.split("-", 3)[0]));


        /*return String.format("Active Slot: %02d-%02d-%d to %02d-%02d-%d \nTiming: %02d:%02d to %02d:%02d \n",
                Integer.parseInt(slot_start_date.split("-", 3)[2]), Integer.parseInt(slot_start_date.split("-", 3)[1]), Integer.parseInt(slot_start_date.split("-", 3)[0]),
                Integer.parseInt(slot_stop_date.split("-", 3)[2]), Integer.parseInt(slot_stop_date.split("-", 3)[1]), Integer.parseInt(slot_stop_date.split("-", 3)[0]),
                Integer.parseInt(slot_start_time.split(":", 3)[0]), Integer.parseInt(slot_start_time.split(":", 3)[1]),
                Integer.parseInt(slot_stop_time.split(":", 3)[0]), Integer.parseInt(slot_stop_time.split(":", 3)[1])) +
                "Slot type: " + slot_type + " (" + slot_max_users + ")" + "\n" + "Week-days: " + stringBuilder.toString();
*/
    }


    private String getFreeSlotTitle(String slot_subject, int slot_start_day, int slot_start_month, int slot_start_year, int slot_stop_day, int slot_stop_month, int slot_stop_year, int slot_start_hour, int slot_start_min, int slot_stop_hour, int slot_stop_min, String slot_type, String[] slot_on_week_days) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int slot_week_day = 0; slot_week_day < slot_on_week_days.length; slot_week_day++) {
            String week_day = slot_on_week_days[slot_week_day];

            /* Checking whether the slot_week_day is having last index of slot_on_wee_days array, Objective is to print slot week days in comma separated */
            if (slot_week_day == slot_on_week_days.length - 1) {
                if (week_day.equals("M")) {
                    stringBuilder.append("Mon");
                }
                if (week_day.equals("T")) {
                    stringBuilder.append("Tue");
                }
                if (week_day.equals("W")) {
                    stringBuilder.append("Wed");
                }
                if (week_day.equals("Th")) {
                    stringBuilder.append("Thu");
                }
                if (week_day.equals("F")) {
                    stringBuilder.append("Fri");
                }
                if (week_day.equals("S")) {
                    stringBuilder.append("Sat");
                }
                if (week_day.equals("Su")) {
                    stringBuilder.append("Sun");
                }
            } else {
                if (week_day.equals("M")) {
                    stringBuilder.append("Mon, ");
                }
                if (week_day.equals("T")) {
                    stringBuilder.append("Tue, ");
                }
                if (week_day.equals("W")) {
                    stringBuilder.append("Wed, ");
                }
                if (week_day.equals("Th")) {
                    stringBuilder.append("Thu, ");
                }
                if (week_day.equals("F")) {
                    stringBuilder.append("Fri, ");
                }
                if (week_day.equals("S")) {
                    stringBuilder.append("Sat, ");
                }
                if (week_day.equals("Su")) {
                    stringBuilder.append("Sun, ");
                }
            }

        }
        return String.format("Free slot: %02d-%02d-%d to %02d-%02d-%d", slot_start_day, slot_start_month, slot_start_year, slot_stop_day, slot_stop_month, slot_stop_year);


/* commented as this much data is not coming sometimes clearly on week view event .
        return String.format("Free slot: %02d-%02d-%d to %02d-%02d-%d \nTiming: %02d:%02d to %02d:%02d \n", slot_start_day, slot_start_month, slot_start_year, slot_stop_day, slot_stop_month, slot_stop_year, slot_start_hour, slot_start_min, slot_stop_hour, slot_stop_min) + "Slot type: " + slot_type + ", Subject: " + slot_subject + "\n" + "Week-days: " + stringBuilder.toString();
*/
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        int event_type = event.getEvent_type();
        if (this_activity_for.equals("MentorDetailsActivity")) {
            switch (event_type) {
                case 202:

                    /* Checking whether this slot is possible to get scheduled after evaluating slot start time and current time of the day.*/

                    Slot slot = event.getSlot();

                    Calendar cal = Calendar.getInstance();
                    cal.set(Integer.parseInt(slot.getSlot_start_date().split("-")[0]), Integer.parseInt(slot.getSlot_start_date().split("-")[1]) - 1, Integer.parseInt(slot.getSlot_start_date().split("-")[2]));
                    long slot_start_date = cal.getTimeInMillis();


                    Calendar rightNow = Calendar.getInstance();
                    long rightNow_in_millis = rightNow.getTimeInMillis();
                    int current_hour = rightNow.get(Calendar.HOUR_OF_DAY);
                    int current_minute = rightNow.get(Calendar.MINUTE);

                    Log.d(TAG, "right now in millis: " + rightNow_in_millis + " slot_start_date in millis: " + slot_start_date);
                    String from_date;
                    if (rightNow_in_millis >= slot_start_date) {

                    /* Mentee is looking to schedule when class slot is already behind the current date i.e. he is looking to join class in mid of class schedule  */


                        if (current_hour > Integer.parseInt(slot.getSlot_start_time().split(":")[0])) {
                    /* increasing schedule start date by one day i.e. slot_start_date is before current date and current time is also greater than slot_start_time*/
                            from_date = String.format("%02d-%02d-%d", rightNow.get(Calendar.DAY_OF_MONTH) + 1, (rightNow.get(Calendar.MONTH) + 1), rightNow.get(Calendar.YEAR));
                        } else {

                    /* if current hour is behing slot start hour or it is equal to it , then start day of class schedule will be from this current date */
                            from_date = String.format("%02d-%02d-%d", rightNow.get(Calendar.DAY_OF_MONTH), (rightNow.get(Calendar.MONTH) + 1), rightNow.get(Calendar.YEAR));

                        }
                    } else {
                        from_date = String.format("%02d-%02d-%d", Integer.parseInt(slot.getSlot_start_date().split("-")[2]), Integer.parseInt(slot.getSlot_start_date().split("-")[1]), Integer.parseInt(slot.getSlot_start_date().split("-")[0]));

                    }

                    /* Now to check whether a class is possible from start date (from_date variable) and slot stop date after time evaluation and possible start date for this slot */
                    Calendar cal_from_date = Calendar.getInstance();
                    cal_from_date.set(Integer.parseInt(from_date.split("-")[2]),Integer.parseInt(from_date.split("-")[1])-1,Integer.parseInt(from_date.split("-")[0]));
                    long cal_from_date_millis= cal_from_date.getTimeInMillis();
                    Log.d(TAG,"from date on click : "+from_date.toString()+" cal date "+Integer.parseInt(from_date.split("-")[2])+"-"+(Integer.parseInt(from_date.split("-")[1])-1)+"-"+Integer.parseInt(from_date.split("-")[0])+" cal in millies"+cal_from_date_millis);


                    Calendar cal_to_date = Calendar.getInstance();
                    cal_to_date.set(Integer.parseInt(slot.getSlot_stop_date().split("-")[0]), Integer.parseInt(slot.getSlot_stop_date().split("-")[1])-1, Integer.parseInt(slot.getSlot_stop_date().split("-")[2]));
                    long cal_to_date_millis= cal_to_date.getTimeInMillis();
                    Log.d(TAG,"to date on click : "+" cal date "+Integer.parseInt(slot.getSlot_stop_date().split("-")[0])+"-"+(Integer.parseInt(slot.getSlot_stop_date().split("-")[1])-1)+"-"+Integer.parseInt(slot.getSlot_stop_date().split("-")[2])+" cal to in millies"+cal_to_date_millis);

                    if((cal_to_date_millis > cal_from_date_millis) || (cal_to_date_millis == cal_from_date_millis)){
                        Intent intent = new Intent(SetScheduleActivity.this, ScheduleNewClass.class);
                        Bundle bundle = new Bundle();     /// startTime, endTime, new_slot,mentorInfo, mentor_id, mentor_availablity, 202, charges, arrayList_subcategory
                        bundle.putParcelable("slot", event.getSlot());
                        bundle.putParcelable("mentor_info", event.getMentorInfo());
                        Log.i(TAG, "mentor_info_mentor_id_going to be sent to Schedule new class: " + event.getMentorInfo().getMentor_id() + " mentor_id came from list of mentors when mentee searched: " + event.getMentor_id());
                        bundle.putString("mentor_id", event.getMentor_id());
                        bundle.putString("mentor_availability", event.getMentor_availablity()); // Mentor availability can be "0" or "1" , 0 means not available
                        bundle.putString("charges", event.getCharges());
                        bundle.putStringArrayList("arrayList_sub_category", event.getArrayList_sub_category());
                        intent.putExtra("slot_bundle", bundle);
                        startActivity(intent);
                    }else{
                        /* class is not possible as time crossed */
                        Toast.makeText(SetScheduleActivity.this,getResources().getString(R.string.class_not_possible),Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } else {
            if (this_activity_for.equals("ScheduleFragments")) {
                Intent intent = new Intent(SetScheduleActivity.this, AboutWeekViewEvent.class);
                Bundle bundle = new Bundle();

                if (StorageHelper.getUserGroup(SetScheduleActivity.this, "user_group").equals("3")) {

                    switch (event_type) {
                        case 12345:
                            intent.putExtra("for", "scheduled_class_mentor");
                            bundle.putParcelableArrayList("mentees", (ArrayList<? extends android.os.Parcelable>) event.getMenteeFoundOnThisDate());
                            bundle.putParcelable("slot", event.getSlot());
                            intent.putExtra("scheduled_class_mentor_bundle", bundle);
                            startActivity(intent);
                            break;
                        case 123:
                            intent.putExtra("for", "coinciding_vacation_mentor");
                            bundle.putParcelableArrayList("coinciding_vacations", (ArrayList<? extends android.os.Parcelable>) event.getCoincidingVacations());
                            bundle.putParcelable("slot", event.getSlot());
                            intent.putExtra("coinciding_vacation_bundle", bundle);
                            startActivity(intent);
                            break;
                        case 432:
                            intent.putExtra("for", "slot_not_scheduled");
                            bundle.putParcelable("slot", event.getSlot());
                            intent.putExtra("unscheduled_slot_bundle", bundle);
                            startActivityForResult(intent, slot_request_code);
                            break;
                        case 21:
                            intent.putExtra("for", "non_coinciding_vacation");
                            bundle.putParcelable("vacation", event.getVacation());
                            intent.putExtra("vacation_bundle", bundle);
                            startActivityForResult(intent, vacation_req_code);
                            break;
                    }
                } else {
                    switch (event_type) {
                        case 103:
                            intent.putExtra("for", "scheduled_class_mentee");
                            bundle.putParcelableArrayList("mentees", (ArrayList<? extends android.os.Parcelable>) event.getMenteeFoundOnThisDate());
                            bundle.putParcelable("slot", event.getSlot());
                            bundle.putParcelable("mentor_info", event.getMentorInfo());
                            intent.putExtra("scheduled_class_mentee_bundle", bundle);
                            startActivity(intent);
                            break;
                        case 105:
                            intent.putExtra("for", "coinciding_vacation_mentee");
                            bundle.putParcelableArrayList("coinciding_vacations", (ArrayList<? extends android.os.Parcelable>) event.getCoincidingVacations());
                            bundle.putParcelable("slot", event.getSlot());
                            bundle.putParcelable("mentor_info", event.getMentorInfo());
                            intent.putExtra("coinciding_vacation_mentee_bundle", bundle);
                            startActivity(intent);
                            break;

                    }
                }
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1) {
                /* for free slot delete from AboutWeekViewEvent delete*/
            Log.d(TAG, "inside onActivityResult");
            finish();
            if (MyScheduleFragment.myScheduleFragment != null) {
                MyScheduleFragment myScheduleFragment = MyScheduleFragment.myScheduleFragment;
                myScheduleFragment.populate_calendar_from_adapter = true;
                myScheduleFragment.month = weekView_current_month;
                myScheduleFragment.year = weekView_current_month_year;
                myScheduleFragment.getCalendarDetailsAPICall();
            }


        } else {
            if (requestCode == 3 && resultCode == 6) {
                /* for vacation delete from AboutWeekViewEvent delete option*/
                Log.d(TAG, "inside onActivityResult");
                finish();
                if (MyScheduleFragment.myScheduleFragment != null) {
                    MyScheduleFragment myScheduleFragment = MyScheduleFragment.myScheduleFragment;
                    myScheduleFragment.populate_calendar_from_adapter = true;
                    myScheduleFragment.month = weekView_current_month;
                    myScheduleFragment.year = weekView_current_month_year;
                    myScheduleFragment.getCalendarDetailsAPICall();
                }

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
        Intent intent = new Intent(SetScheduleActivity.this, AboutWeekViewEvent.class);
        intent.putExtra("about_event", (String) object);
        Log.d(TAG, "event id data for event : " + (String) object);
        startActivity(intent);
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(SetScheduleActivity.this, (String) object, Toast.LENGTH_SHORT).show();
    }


}
