package com.findmycoach.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.MentorDetailsActivity;
import com.findmycoach.app.activity.SetScheduleActivity;
import com.findmycoach.app.beans.CalendarSchedule.Day;
import com.findmycoach.app.beans.CalendarSchedule.EventBean;
import com.findmycoach.app.beans.CalendarSchedule.SlotBean;
import com.findmycoach.app.beans.CalendarSchedule.VacationBean;
import com.findmycoach.app.fragment.MyScheduleFragment;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prem on 6/2/15.
 */
public class CalendarGridAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context context;

    private List<String> list;
    private static final int DAY_OFFSET = 1;
    private String[] weekdays;
    private String[] months;
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int daysInMonth;
    private int currentDayOfMonth;
    private int currentWeekDay;
    private Button gridcell;
    private TextView num_events_per_day;
    private HashMap<String, Integer> eventsPerMonthMap;
    private static int CURRENT_MONTH_OF_CALENDAR;
    private static int CURRENT_YEAR_OF_CALENDAR;
    private static int month_in_foreground;
    private static final String TAG = "FMC:";
    private MyScheduleFragment myScheduleFragment = null;
    private MentorDetailsActivity mentorDetailsActivity = null;
    private String connection_status = null;
    private String mentor_id;
    private String availability;
    private String charges;
    private ArrayList<String> arrayList_subcategory = null;
    private String mentor_address;
    private static ArrayList<Day> prev_month_data = null;
    private static ArrayList<Day> current_month_data = null;
    private static ArrayList<Day> coming_month_data = null;
    private static int day_schedule_index = 0;
    Day day = null;
    EventBean eventBean = null;
    private boolean allow_data_population_from_server_data = true;

    private boolean allow_once; /* this is a flag which is used to give message to the user that network communication is
    successful but due to some reason there is no data to populate calendar ( it is happening in the case if wifi (i.e network )is disabled
    then general calendar is getting populate with no schedule information but after some time device get network enabled in that case application
    is requesting data from server for months like previous or next click
             but as we are getting next to next month data and previous to privious month data on next and previous button click. So in this case
             sometimes current month array is coming with no data but it is having some schedule data so for notifying this this thing to user we have
              to use this flag. This flag help in showing message single time in loop
         )*/

    private int month_for_which_calendar_get_populated;
    private int year_for_which_calendar_get_populated;


    // Days in Current Month
    /*
    * This constructor is called from MyScheduleFragment
    *
    * */
    public CalendarGridAdapter(Context context, int month, int year, MyScheduleFragment myScheduleFragment, ArrayList<Day> prev_month_data, ArrayList<Day> current_month_data, ArrayList<Day> coming_month_data) {
        super();
        this.context = context;
        weekdays = context.getResources().getStringArray(R.array.week_days);
        months = context.getResources().getStringArray(R.array.months);
        this.list = new ArrayList<String>();
        this.myScheduleFragment = myScheduleFragment;
        allow_once = true;

        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));


        CURRENT_MONTH_OF_CALENDAR = calendar.get(Calendar.MONTH);
        CURRENT_YEAR_OF_CALENDAR = calendar.get(Calendar.YEAR);
        month_in_foreground = month - 1;

        CalendarGridAdapter.prev_month_data = new ArrayList<Day>();
        CalendarGridAdapter.current_month_data = new ArrayList<Day>();
        CalendarGridAdapter.coming_month_data = new ArrayList<Day>();
        CalendarGridAdapter.prev_month_data = prev_month_data;
        CalendarGridAdapter.current_month_data = current_month_data;
        CalendarGridAdapter.coming_month_data = coming_month_data;

        month_for_which_calendar_get_populated = month;
        year_for_which_calendar_get_populated = year;
        allow_data_population_from_server_data = true;   /* This constructor get called when there is successful network communication so data from server helps in calendar population*/
        printMonth(month, year);

        eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
    }

    /*
        * This constructor is called from MyScheduleFragment
        *
        * */
    public CalendarGridAdapter(Context context, int month, int year, MentorDetailsActivity mentorDetailsActivity, ArrayList<Day> prev_month_data, ArrayList<Day> current_month_data, ArrayList<Day> coming_month_data, String mentor_id, String availability_yn, String charges, ArrayList<String> arraylist_subcategory, String connection_status) {
        super();
        this.context = context;
        weekdays = context.getResources().getStringArray(R.array.week_days);
        months = context.getResources().getStringArray(R.array.months);
        this.list = new ArrayList<String>();
        this.mentorDetailsActivity = mentorDetailsActivity;
        this.mentor_id = mentor_id;
        this.availability = availability_yn;
        this.charges = charges;
        this.arrayList_subcategory = arraylist_subcategory;
        this.connection_status = connection_status;
        allow_once = true;
        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));


        CURRENT_MONTH_OF_CALENDAR = calendar.get(Calendar.MONTH);
        CURRENT_YEAR_OF_CALENDAR = calendar.get(Calendar.YEAR);
        month_in_foreground = month - 1;

        CalendarGridAdapter.prev_month_data = new ArrayList<Day>();
        CalendarGridAdapter.current_month_data = new ArrayList<Day>();
        CalendarGridAdapter.coming_month_data = new ArrayList<Day>();
        CalendarGridAdapter.prev_month_data = prev_month_data;
        CalendarGridAdapter.current_month_data = current_month_data;
        CalendarGridAdapter.coming_month_data = coming_month_data;

        month_for_which_calendar_get_populated = month;
        year_for_which_calendar_get_populated = year;
        allow_data_population_from_server_data = true;
        printMonth(month, year);         /* This constructor get called when there is successful network communication so data from server helps in calendar population*/

        eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
    }


    public CalendarGridAdapter(Context context, int month, int year, MentorDetailsActivity mentorDetailsActivity) {
        super();
        this.context = context;
        this.mentorDetailsActivity = mentorDetailsActivity;

        this.list = new ArrayList<String>();
        weekdays = context.getResources().getStringArray(R.array.week_days);
        months = context.getResources().getStringArray(R.array.months);
        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        month_for_which_calendar_get_populated = month;
        year_for_which_calendar_get_populated = year;
        allow_data_population_from_server_data = false;
        printMonth(month, year);   /* This constructor get called when there is some problem to get data from network communication so data from server helps in calendar population*/
    }


    public CalendarGridAdapter(Context context, int month, int year, MyScheduleFragment myScheduleFragment) {
        super();
        this.context = context;
        weekdays = context.getResources().getStringArray(R.array.week_days);
        months = context.getResources().getStringArray(R.array.months);
        this.myScheduleFragment = myScheduleFragment;
        this.list = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        month_for_which_calendar_get_populated = month;
        year_for_which_calendar_get_populated = year;
        allow_data_population_from_server_data = false;  /* This constructor get called when there is some problem to get data from network communication so data from server helps in calendar population*/
        printMonth(month, year);
    }


    private String getMonthAsString(int i) {
        return months[i];
    }

    private int getNumberOfDaysOfMonth(int i) {
        return daysOfMonth[i];
    }

    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }


    private void printMonth(int mm, int yy) {

        int trailingSpaces = 0;
        int daysInPrevMonth = 0;
        int prevMonth = 0;
        int prevYear = 0;
        int nextMonth = 0;
        int nextYear = 0;

        int currentMonth = mm - 1;
        String currentMonthName = getMonthAsString(currentMonth);
        daysInMonth = getNumberOfDaysOfMonth(currentMonth);


        GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

        if (currentMonth == 11) {
            prevMonth = currentMonth - 1;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 0;
            prevYear = yy;
            nextYear = yy + 1;
        } else if (currentMonth == 0) {
            prevMonth = 11;
            prevYear = yy - 1;
            nextYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 1;
        } else {
            prevMonth = currentMonth - 1;
            nextMonth = currentMonth + 1;
            nextYear = yy;
            prevYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
        }

        int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;


        trailingSpaces = currentWeekDay;


        if (cal.isLeapYear(cal.get(Calendar.YEAR)))
            if (mm == 2)
                ++daysInMonth;
            else if (mm == 3)
                ++daysInPrevMonth;

        // Trailing Month days
        for (int i = 0; i < trailingSpaces; i++) {
            list.add(String
                    .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                            + i)
                    + "-GREY"
                    + "-"
                    + getMonthAsString(prevMonth)
                    + "-"
                    + prevYear);
        }


        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++) {

            if (i == getCurrentDayOfMonth() && currentMonth == CURRENT_MONTH_OF_CALENDAR && yy == CURRENT_YEAR_OF_CALENDAR) {
                list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
            } else {
                list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
            }
        }

        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++) {
            list.add(String.valueOf(i + 1) + "-GREY" + "-"
                    + getMonthAsString(nextMonth) + "-" + nextYear);
        }
    }


    private HashMap<String, Integer> findNumberOfEventsPerMonth(int year, int month) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        return map;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.screen_gridcell, parent, false);
        }

        // Get a reference to the Day gridcell
        gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
        gridcell.setOnClickListener(this);

        // ACCOUNT FOR SPACING

        String[] day_color = list.get(position).split("-");
        String theday = day_color[0];
        String themonth = day_color[2];
        String theyear = day_color[3];

        boolean allow_schedule_population = false;

        // Set the Day GridCell
        gridcell.setText(theday);
        gridcell.setTag(theday + "-" + themonth + "-" + theyear);


        if (day_color[1].equals("GREY")) {
            gridcell.setTextColor(context.getResources().getColor(R.color.purple_light));
        }
        if (day_color[1].equals("WHITE")) {
            gridcell.setTextColor(context.getResources().getColor(R.color.purple));
            allow_schedule_population = true;

        }
        if (day_color[1].equals("BLUE")) {
            gridcell.setTextColor(context.getResources().getColor(R.color.white));
            gridcell.setBackgroundColor(context.getResources().getColor(R.color.purple));
            allow_schedule_population = true;
        }
        day_schedule_index = Integer.parseInt(theday) - 1;
        if (allow_data_population_from_server_data) {
            if (allow_schedule_population) {


                if (current_month_data.size() <= 0) {
                    if (allow_once) {
                        // Toast.makeText(context, "Please refresh the schedule !", Toast.LENGTH_SHORT).show();
                        if (myScheduleFragment != null) {
                            String user_group = StorageHelper.getUserGroup(context, "user_group");
                            myScheduleFragment.populate_calendar_from_adapter = true;
                            MyScheduleFragment.month = month_for_which_calendar_get_populated;
                            MyScheduleFragment.year = year_for_which_calendar_get_populated;
                            if (user_group.equals("3")) {
                                myScheduleFragment.getCalendarDetailsAPICall();

                            } else {
                                if (user_group.equals("2")) {
                                   /*mentee three months data will get called from here */
                                    myScheduleFragment.getCalendarDetailsForMentee();
                                }
                            }

                        }

                        if (mentorDetailsActivity != null) {
                            mentorDetailsActivity.populate_calendar_from_adapter = true;
                            MyScheduleFragment.month = month_for_which_calendar_get_populated;
                            MyScheduleFragment.year = year_for_which_calendar_get_populated;
                            mentorDetailsActivity.getCalendarDetailsAPICall();

                        }


                    }
                    allow_once = false;

                }
                if (day_schedule_index < current_month_data.size()) {

                    day = current_month_data.get(day_schedule_index);

                /*
                *
                *
                * success when CalendarGridAdapter is used by MyScheduleFragment class
                * */
                    if (myScheduleFragment != null) {
                        if (StorageHelper.getUserDetails(context, "user_group").equals("3")) {
                            List<EventBean> eventBeans = day.getEventBeans();
                            List<SlotBean> slotBeans = day.getSlotBeans();
                            List<VacationBean> vacationBeans = day.getVacationBeans();

                            if (eventBeans.size() > 0) {
                                if (day_color[1].equals("BLUE")) {
                                    gridcell.setBackground(context.getResources().getDrawable(R.drawable.scheduled_event_arrow_today));
                                } else {
                                    gridcell.setBackground(context.getResources().getDrawable(R.drawable.scheduled_event_arrow));
                                }
                            }
                        }
                        if (StorageHelper.getUserDetails(context, "user_group").equals("2")) {
                            List<EventBean> eventBeans = day.getEventBeans();
                            List<VacationBean> vacationBeans = day.getVacationBeans();

                            if (eventBeans.size() > 0) {


                                if (day_color[1].equals("BLUE")) {
                                    gridcell.setBackground(context.getResources().getDrawable(R.drawable.scheduled_event_arrow_today));
                                } else {
                                    gridcell.setBackground(context.getResources().getDrawable(R.drawable.scheduled_event_arrow));
                                }
                            }
                        }


                    } else {
                /*
                *
                *
                *
                * success when CalendarGridAdapter is used by MentorDetailsActivity class
                * */
                        List<EventBean> eventBeans = day.getEventBeans();
                        List<SlotBean> slotBeans = day.getSlotBeans();
                        List<VacationBean> vacationBeans = day.getVacationBeans();

                    /*if (daySlots.size() > 0 && dayEvents.size() <= 0) {
                        *//*  success when this day has only slots and there is no event coming from server*//*
                        if (day_color[1].equals("BLUE")) {
                            gridcell.setBackgroundColor(new Color().CYAN);
                        } else {
                            gridcell.setBackgroundColor(new Color().YELLOW);
                        }
                        Toast.makeText(context, "free slots : " + free_slot, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "free_slots : " + free_slot);
                        gridcell.setTag(1, String.valueOf(free_slot));

                    } else {*/
                        int free_slot = 0;
                        if (slotBeans.size() <= 0) {
                            /*   success when there is no slots i.e. slots array size is zero
                            *    In this condition, grid click event should be handled like we do not open week-view and give a message that mentor is not free on this day.
                            * */
                            free_slot = -1;

                        } else {
                            /*
                            * success when this day his having slots.
                            * Now to decide any slot is free or not
                            * */

                            /*
                             * matching each slot of the day with all possible events, and on this match deciding whether this slot come as free slot or not.
                             * */
                            for (int day_slot = 0; day_slot < slotBeans.size(); day_slot++) {
                                SlotBean slotBean = slotBeans.get(day_slot);
                                String slot_start_date = slotBean.getSlot_start_date();
                                String slot_stop_date = slotBean.getSlot_stop_date();
                                String slot_start_time = slotBean.getSlot_start_time();
                                String slot_stop_time = slotBean.getSlot_stop_time();
                                String slot_type = slotBean.getSlot_type();
                                String slot_id = slotBean.getSlot_id();
                                int slot_max_users = Integer.parseInt(slotBean.getSlot_max_users());

                                int slot_stop_day = Integer.parseInt(slot_stop_date.split("-", 3)[2]);
                                int slot_stop_month = Integer.parseInt(slot_stop_date.split("-", 3)[1]);
                                int slot_stop_year = Integer.parseInt(slot_stop_date.split("-", 3)[0]);

                                int slot_start_time_in_seconds = (Integer.parseInt(slot_start_time.split(":")[0]) * 60 * 60) + (Integer.parseInt(slot_start_time.split(":")[1]) * 60);
                                int slot_stop_time_in_seconds = (Integer.parseInt(slot_stop_time.split(":")[0]) * 60 * 60) + (Integer.parseInt(slot_stop_time.split(":")[1]) * 60);

                                long current_date_in_millis = System.currentTimeMillis();

                                Calendar calendar = new GregorianCalendar();
                                calendar.set(slot_stop_year, slot_stop_month - 1, slot_stop_day);
                                long slot_stop_date_in_millis = calendar.getTimeInMillis();


                                String grid_date = (String) gridcell.getTag();
                                int grid_day = Integer.parseInt(grid_date.split("-", 3)[0]);
                                String grid_month = grid_date.split("-", 3)[1];
                                int month_index_of_grid = Arrays.asList(months).indexOf(grid_month);
                                int grid_year = Integer.parseInt(grid_date.split("-", 3)[2]);


                                Calendar calendar1 = new GregorianCalendar();
                                calendar1.set(grid_year, month_index_of_grid, grid_day);
                                long grid_day_in_millis = calendar1.getTimeInMillis();

                                if (current_date_in_millis > grid_day_in_millis)
                                    break;


                                if (current_date_in_millis > slot_stop_date_in_millis)
                                    break;

                                /*
                                *
                                * For slot which are selected as Group
                                * */
                                if (slot_type.equalsIgnoreCase(context.getResources().getString(R.string.group))) {
                                    boolean slot_match_with_event = false;
                                    for (int day_event = 0; day_event < eventBeans.size(); day_event++) {    /* dayEvents is a list of DayEvent bean*/
                                        EventBean eventBean1 = eventBeans.get(day_event);
                                        String event_start_date = eventBean1.getEvent_start_date();
                                        String event_stop_date = eventBean1.getEvent_stop_date();
                                        String event_start_time = eventBean1.getEvent_start_time();
                                        String event_stop_time = eventBean1.getEvent_stop_time();
                                        String event_regarding_slot_id = eventBean1.getSlot_id();   /* this will get slot_id regarding its matching slot */
                                        int event_total_mentees = Integer.parseInt(eventBean1.getEvent_total_mentee());
                                        /* checking whether this particular event is similar to slot or not */
                                        if (event_regarding_slot_id.equals(slot_id)) {
                                            slot_match_with_event = true;
                                            /* if found similar then check whether the event_totoal_mentees from slot_max_users*/
                                            if (event_total_mentees < slot_max_users) {


                                                if (vacationBeans.size() <= 0) {
                                                    free_slot++;
                                                } else {
                                                    boolean vacation_found_in_between = false;
                                                    for (int day_vacation = 0; day_vacation < vacationBeans.size(); day_vacation++) {
                                                        VacationBean vacationBean = vacationBeans.get(day_vacation);
                                                        String vacation_start_time = vacationBean.getStart_time();
                                                        String vacation_stop_time = vacationBean.getStop_time();

                                                        int this_vacation_start_time_seconds = (Integer.parseInt(vacation_start_time.split(":")[0]) * 60 * 60) + (Integer.parseInt(vacation_start_time.split(":")[1]) * 60);
                                                        int this_vacation_stop_time_seconds = (Integer.parseInt(vacation_stop_time.split(":")[0]) * 60 * 60) + (Integer.parseInt(vacation_stop_time.split(":")[1]) * 60);


                                                        if ((this_vacation_start_time_seconds < slot_start_time_in_seconds && this_vacation_stop_time_seconds > slot_start_time_in_seconds && this_vacation_stop_time_seconds < slot_stop_time_in_seconds) || (this_vacation_start_time_seconds > slot_start_time_in_seconds && this_vacation_start_time_seconds < slot_stop_time_in_seconds && this_vacation_stop_time_seconds > slot_stop_time_in_seconds) || (this_vacation_start_time_seconds == slot_start_time_in_seconds && this_vacation_stop_time_seconds == slot_stop_time_in_seconds) || (this_vacation_start_time_seconds < slot_start_time_in_seconds && this_vacation_stop_time_seconds > slot_stop_time_in_seconds) || (this_vacation_start_time_seconds > slot_start_time_in_seconds && this_vacation_start_time_seconds < slot_stop_time_in_seconds && this_vacation_stop_time_seconds > slot_start_time_in_seconds && this_vacation_stop_time_seconds < slot_stop_time_in_seconds)) {
                                             /* this confirms that vacation is coinciding with slot_time */
                                                            vacation_found_in_between = true;
                                                            break;
                                                        } else
                                                            continue;


                                                    }
                                                    if (!vacation_found_in_between) {
                                                        free_slot++;

                                                    }


                                                }

                                            }
                                            break;
                                        }
/*
                                        if (event_start_date.equals(slot_start_date) && event_stop_date.equals(slot_stop_date) && event_start_time.equals(slot_start_time) && event_stop_time.equals(slot_stop_time)) {
                                            slot_match_with_event = true;
                                            */
/* if found similar then check whether the event_totoal_mentees from slot_max_users*//*

                                            if (event_total_mentees < slot_max_users) {
                                                free_slot++;
                                            }
                                        }
*/
                                    }

                                    if (!slot_match_with_event) {

                                        if (vacationBeans.size() <= 0) {
                                            free_slot++;
                                        } else {
                                            boolean vacation_found_in_between = false;
                                            for (int day_vacation = 0; day_vacation < vacationBeans.size(); day_vacation++) {
                                                VacationBean vacationBean = vacationBeans.get(day_vacation);
                                                String vacation_start_time = vacationBean.getStart_time();
                                                String vacation_stop_time = vacationBean.getStop_time();

                                                int this_vacation_start_time_seconds = (Integer.parseInt(vacation_start_time.split(":")[0]) * 60 * 60) + (Integer.parseInt(vacation_start_time.split(":")[1]) * 60);
                                                int this_vacation_stop_time_seconds = (Integer.parseInt(vacation_stop_time.split(":")[0]) * 60 * 60) + (Integer.parseInt(vacation_stop_time.split(":")[1]) * 60);


                                                if ((this_vacation_start_time_seconds < slot_start_time_in_seconds && this_vacation_stop_time_seconds > slot_start_time_in_seconds && this_vacation_stop_time_seconds < slot_stop_time_in_seconds) || (this_vacation_start_time_seconds > slot_start_time_in_seconds && this_vacation_start_time_seconds < slot_stop_time_in_seconds && this_vacation_stop_time_seconds > slot_stop_time_in_seconds) || (this_vacation_start_time_seconds == slot_start_time_in_seconds && this_vacation_stop_time_seconds == slot_stop_time_in_seconds) || (this_vacation_start_time_seconds < slot_start_time_in_seconds && this_vacation_stop_time_seconds > slot_stop_time_in_seconds) || (this_vacation_start_time_seconds > slot_start_time_in_seconds && this_vacation_start_time_seconds < slot_stop_time_in_seconds && this_vacation_stop_time_seconds > slot_start_time_in_seconds && this_vacation_stop_time_seconds < slot_stop_time_in_seconds)) {
                                             /* this confirms that vacation is coinciding with slot_time */
                                                    vacation_found_in_between = true;
                                                    break;
                                                } else
                                                    continue;


                                            }
                                            if (!vacation_found_in_between) {
                                                free_slot++;

                                            }


                                        }
                                    }


                                } else {
                                    /*
                                    *
                                    * For slot which are selected as solo
                                    * */
                                    boolean slot_match_with_event = false;
                                    for (int day_event = 0; day_event < eventBeans.size(); day_event++) {
                                        EventBean eventBean1 = eventBeans.get(day_event);
                                        String event_start_date = eventBean1.getEvent_start_date();
                                        String event_stop_date = eventBean1.getEvent_stop_date();
                                        String event_start_time = eventBean1.getEvent_start_time();
                                        String event_stop_time = eventBean1.getEvent_stop_time();
                                        String event_regarding_slot_id = eventBean1.getSlot_id();/* this will get slot_id regarding its matching slot */
                                        int event_total_mentees = Integer.parseInt(eventBean1.getEvent_total_mentee());
                                        /* checking whether this particular event is similar to slot or not */
                                        if (event_regarding_slot_id.equals(slot_id)) {
                                            slot_match_with_event = true;
                                            break;
                                            /* if found similar then check whether the event_totoal_mentees from slot_max_users*/

                                        }

                                    }

                                    if (!slot_match_with_event) {
                                        if (vacationBeans.size() <= 0) {
                                            free_slot++;
                                        } else {
                                            boolean vacation_found_in_between = false;
                                            for (int day_vacation = 0; day_vacation < vacationBeans.size(); day_vacation++) {
                                                VacationBean vacationBean = vacationBeans.get(day_vacation);
                                                String vacation_start_time = vacationBean.getStart_time();
                                                String vacation_stop_time = vacationBean.getStop_time();

                                                int this_vacation_start_time_seconds = (Integer.parseInt(vacation_start_time.split(":")[0]) * 60 * 60) + (Integer.parseInt(vacation_start_time.split(":")[1]) * 60);
                                                int this_vacation_stop_time_seconds = (Integer.parseInt(vacation_stop_time.split(":")[0]) * 60 * 60) + (Integer.parseInt(vacation_stop_time.split(":")[1]) * 60);


                                                if ((this_vacation_start_time_seconds < slot_start_time_in_seconds && this_vacation_stop_time_seconds > slot_start_time_in_seconds && this_vacation_stop_time_seconds < slot_stop_time_in_seconds) || (this_vacation_start_time_seconds > slot_start_time_in_seconds && this_vacation_start_time_seconds < slot_stop_time_in_seconds && this_vacation_stop_time_seconds > slot_stop_time_in_seconds) || (this_vacation_start_time_seconds == slot_start_time_in_seconds && this_vacation_stop_time_seconds == slot_stop_time_in_seconds) || (this_vacation_start_time_seconds < slot_start_time_in_seconds && this_vacation_stop_time_seconds > slot_stop_time_in_seconds) || (this_vacation_start_time_seconds > slot_start_time_in_seconds && this_vacation_start_time_seconds < slot_stop_time_in_seconds && this_vacation_stop_time_seconds > slot_start_time_in_seconds && this_vacation_stop_time_seconds < slot_stop_time_in_seconds)) {
                                             /* this confirms that vacation is coinciding with slot_time */
                                                    vacation_found_in_between = true;
                                                    break;
                                                } else
                                                    continue;


                                            }
                                            if (!vacation_found_in_between) {
                                                free_slot++;

                                            }


                                        }
                                    }
                                }

                            }


                        }
                   /* }*/
                    /*
                            *
                            * if free_slot is having value greater than zero, it means this day has free slots and we have to populate calendar grid color
                            * */
                        if (free_slot > 0) {
                            if (day_color[1].equals("BLUE")) {
                                gridcell.setBackground(context.getResources().getDrawable(R.drawable.scheduled_event_arrow_today));
                            } else {
                                gridcell.setBackground(context.getResources().getDrawable(R.drawable.scheduled_event_arrow));
                            }
                        } else {
                            if (day_color[1].equals("BLUE")) {
                                gridcell.setBackgroundColor(context.getResources().getColor(R.color.purple));
                            }
                        }


                        gridcell.setTag(R.id.TAG_FREE_SLOT, String.valueOf(free_slot));


                    }


                }

            }
        }


        return row;
    }

    @Override
    public void onClick(View view) {

        if (allow_data_population_from_server_data) {
            Intent intent = new Intent(context, SetScheduleActivity.class);
            String s = (String) view.getTag();
            int no_of_free_slots = 0;


            int day = Integer.parseInt(s.split("-", 3)[0]);

            String month = s.split("-", 3)[1];
            String year = s.split("-", 3)[2];
            if (myScheduleFragment != null) {
                intent.putExtra("for", "ScheduleFragments");
            } else {
                intent.putExtra("for", "MentorDetailsActivity");

                String grid_cell_tag = (String) view.getTag(R.id.TAG_FREE_SLOT);
                if (grid_cell_tag != null)
                    no_of_free_slots = Integer.parseInt(grid_cell_tag);
                else
                    no_of_free_slots = 0;
                intent.putExtra("mentor_id", mentor_id);
                intent.putExtra("availability", availability);
                intent.putExtra("charges", charges);
                intent.putStringArrayListExtra("arrayList_category", arrayList_subcategory);


            }
            intent.putExtra("date", (String) view.getTag());
            intent.putExtra("day", Integer.parseInt(s.split("-", 3)[0]));
            intent.putExtra("year", Integer.parseInt(year));
            //Log.d(TAG, "three months data list size" + three_months_data.size());
            intent.putExtra("prev_month_data", prev_month_data);
            intent.putExtra("current_month_data", current_month_data);
            intent.putExtra("coming_month_data", coming_month_data);


            //intent.putExtra("day_bean", (android.os.Parcelable) three_months_data);


        /*for (Day d : day_schedule) {
            Log.i(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            Log.d(TAG, "day: " + d.getDay() + ", mont: " + d.getMonth() + ", year: " + d.getYear());
            for (DayEvent ev : d.getDayEvents())
                Log.d(TAG, "Start Time : " + ev.getEvent_start_hour() + ", Start Min : " + ev.getEvent_start_min() +
                        ", Stop Time : " + ev.getEvent_stop_hour() + ", Stop Min : " + ev.getEvent_stop_min()
                        + ", event Name : " + ev.getEvent_name());
        }
*/

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            int month_index_of_grid_clicked = Arrays.asList(months).indexOf(month);
            intent.putExtra("month", month_index_of_grid_clicked);
            Log.d(TAG, "1s1 :" + "month in foreground: " + month_in_foreground + ", Month_index_of_grid_clicked: " + month_index_of_grid_clicked);

            if (month_in_foreground < month_index_of_grid_clicked) {
                if (month_in_foreground == 0 && month_index_of_grid_clicked == 11) {
                    if (myScheduleFragment != null)
                        myScheduleFragment.showPrevMonth();
                    else
                        mentorDetailsActivity.showPrevMonth();
                } else {
                    //myScheduleFragment.showPrevMonth();
                    if (myScheduleFragment != null)
                        myScheduleFragment.showNextMonth();
                    else
                        mentorDetailsActivity.showNextMonth();
                }


            } else {
                if (month_in_foreground == month_index_of_grid_clicked) {
                    if (myScheduleFragment != null) {
                        context.startActivity(intent);
                    } else {
                        if (mentorDetailsActivity != null) {
                            Log.d(TAG, "connection status on mentor details activity calendar grid click " + connection_status);
                            if (connection_status.equalsIgnoreCase("pending") || connection_status.equalsIgnoreCase("not connected")) {
                                Toast.makeText(context, context.getResources().getString(R.string.you_are_not_connected), Toast.LENGTH_SHORT).show();
                            } else {
                                if (no_of_free_slots <= 0) {
                                    if (no_of_free_slots == 0) {
                                        Toast.makeText(context, context.getResources().getString(R.string.mentor_is_not_free), Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (no_of_free_slots == -1)
                                            Toast.makeText(context, context.getResources().getString(R.string.no_slot_from_mentor), Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    context.startActivity(intent);

                                }
                            }

                        }
                    }


                } else {
                    if (month_in_foreground == 11 && month_index_of_grid_clicked == 0) {
                        if (myScheduleFragment != null)
                            myScheduleFragment.showNextMonth();
                        else
                            mentorDetailsActivity.showNextMonth();

                    } else {
                        if (month_in_foreground > month_index_of_grid_clicked) {

                            //myScheduleFragment.showNextMonth();
                            if (myScheduleFragment != null)
                                myScheduleFragment.showPrevMonth();
                            else
                                mentorDetailsActivity.showPrevMonth();

                        }
                    }

                }
            }
        } else {
            if (!NetworkManager.isNetworkConnected(context)) {
                Toast.makeText(context, context.getResources().getString(R.string.check_network_connection), Toast.LENGTH_SHORT).show();

            } else {
                if (myScheduleFragment != null) {
                    String user_group = StorageHelper.getUserGroup(context, "user_group");
                    myScheduleFragment.populate_calendar_from_adapter = true;
                    MyScheduleFragment.month = month_for_which_calendar_get_populated;
                    MyScheduleFragment.year = year_for_which_calendar_get_populated;
                    if (user_group.equals("3")) {
                        myScheduleFragment.getCalendarDetailsAPICall();

                    } else {
                        if (user_group.equals("2")) {
                                   /*mentee three months data will get called from here */
                            myScheduleFragment.getCalendarDetailsForMentee();
                        }
                    }

                }

                if (mentorDetailsActivity != null) {
                    mentorDetailsActivity.populate_calendar_from_adapter = true;
                    MyScheduleFragment.month = month_for_which_calendar_get_populated;
                    MyScheduleFragment.year = year_for_which_calendar_get_populated;
                    mentorDetailsActivity.getCalendarDetailsAPICall();

                }
            }
        }


    }

    public int getCurrentDayOfMonth() {
        return currentDayOfMonth;
    }

    private void setCurrentDayOfMonth(int currentDayOfMonth) {
        this.currentDayOfMonth = currentDayOfMonth;
    }

    public void setCurrentWeekDay(int currentWeekDay) {
        this.currentWeekDay = currentWeekDay;
        Log.d(TAG, "Day:" + currentWeekDay + "");
    }
}