package com.findmycoach.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.activity.SetScheduleActivity;
import com.findmycoach.app.R;
import com.findmycoach.app.beans.CalendarSchedule.Day;
import com.findmycoach.app.beans.CalendarSchedule.DayEvent;
import com.findmycoach.app.fragment.MyScheduleFragment;

import java.lang.reflect.Array;
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

    private final List<String> list;
    private static final int DAY_OFFSET = 1;
    private final String[] weekdays = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int daysInMonth;
    private int currentDayOfMonth;
    private int currentWeekDay;
    private Button gridcell;
    private TextView num_events_per_day;
    private final HashMap<String, Integer> eventsPerMonthMap;
    private static int CURRENT_MONTH_OF_CALENDAR;
    private static int CURRENT_YEAR_OF_CALENDAR;
    private static int month_in_foreground;
    private static final String TAG = "FMC:";
    private MyScheduleFragment myScheduleFragment;



    //List<ArrayList> month_cal=null;
    //ArrayList<ArrayList> calendar_day=null;
    ArrayList<Day> day_schedule=null;
    //ArrayList<DayEvent> dayEvents=null;
    private static int day_schedule_index=0;
    Day day=null;
    DayEvent dayEvent=null;





    // Days in Current Month
    public CalendarGridAdapter(Context context, int month, int year, MyScheduleFragment myScheduleFragment,ArrayList<Day> day_schedule) {
        super();
        this.context = context;
        this.list = new ArrayList<String>();
        this.myScheduleFragment = myScheduleFragment;
        this.day_schedule=day_schedule;

        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));


        CURRENT_MONTH_OF_CALENDAR = calendar.get(Calendar.MONTH);
        CURRENT_YEAR_OF_CALENDAR = calendar.get(Calendar.YEAR);
        month_in_foreground = month - 1;

        Log.d(TAG, "Current Month of Calendar:" + CURRENT_MONTH_OF_CALENDAR);
        Log.d(TAG, "Current Year of Calendar:" + CURRENT_YEAR_OF_CALENDAR);
        Log.d(TAG, "Current day of month" + currentDayOfMonth);
        Log.d(TAG, "Current week day" + currentWeekDay);


        // Print Month
        printMonth(month, year);

        // Find Number of Events
        eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
    }
    /*public CalendarGridAdapter(int month, int year) {
        super();
        //this.context = context;
        this.list = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));


        CURRENT_MONTH_OF_CALENDAR = calendar.get(Calendar.MONTH);
        CURRENT_YEAR_OF_CALENDAR = calendar.get(Calendar.YEAR);
        month_in_foreground = month - 1;

        Log.d(TAG, "Current Month of Calendar:" + CURRENT_MONTH_OF_CALENDAR);
        Log.d(TAG, "Current Year of Calendar:" + CURRENT_YEAR_OF_CALENDAR);
        Log.d(TAG, "Current day of month" + currentDayOfMonth);
        Log.d(TAG, "Current week day" + currentWeekDay);


        // Print Month
        printMonth(month, year);

        // Find Number of Events
        eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
    }
*/


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
        Log.d(TAG, "current Week Day" + cal.get(Calendar.DAY_OF_WEEK));

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
//            Log.d(TAG, currentMonthName + ": " + String.valueOf(i) + " " + getMonthAsString(currentMonth) + " " + yy);
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

        Log.e(TAG,"Inside getView method for CalendarGridAdapter class"+" position: "+position);
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
        /*if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
            if (eventsPerMonthMap.containsKey(theday)) {
                num_events_per_day = (TextView) row
                        .findViewById(R.id.num_events_per_day);
                Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
                num_events_per_day.setText(numEvents.toString());
            }
        }
*/
        boolean allow_schedule_population=false;

        // Set the Day GridCell
        gridcell.setText(theday);
        gridcell.setTag(theday + "-" + themonth + "-" + theyear);
//        Log.d(TAG, "Grid cell view :" + theday + "/" + themonth + "/" + theyear);

        if (day_color[1].equals("GREY")) {
            gridcell.setTextColor(context.getResources().getColor(R.color.caldroid_darker_gray));
            gridcell.setBackgroundResource(R.drawable.abc_btn_check_material);
        }
        if (day_color[1].equals("WHITE")) {
            gridcell.setTextColor(context.getResources().getColor(R.color.caldroid_black));
            allow_schedule_population=true;
            //this.position=position;
            //Log.d(TAG,"start position of Month which is in foreground calendar"+this.position);
        }
        if (day_color[1].equals("BLUE")) {
            gridcell.setTextColor(context.getResources().getColor(R.color.caldroid_holo_blue_dark));
            allow_schedule_population=true;
            //this.position=position;
            //Log.d(TAG,"start position of Month which is in foreground calendar"+this.position);
        }

        day_schedule_index=Integer.parseInt(theday)-1;
        if(allow_schedule_population){
            if(day_schedule_index <day_schedule.size()){
                day=day_schedule.get(day_schedule_index);
                Log.d(TAG,"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                Log.d(TAG,"day: "+day.getDay()+", month: "+day.getMonth()+",year: "+day.getYear());
                List<DayEvent> dayEvents = day.getDayEvents();
                if(dayEvents.size() > 0){
                    gridcell.setBackgroundColor(new Color().CYAN);
                    for(int i=0; i<dayEvents.size();i++){
                        dayEvent=dayEvents.get(i);

                        Log.d(TAG,"start hour: " +dayEvent.getEvent_start_hour()+", start min: "+dayEvent.getEvent_start_min()+", stop hour: "+dayEvent.getEvent_stop_hour()+", stop min: "+dayEvent.getEvent_stop_min()+", event name: "+dayEvent.getEvent_name());
                    }

                }
                //day_schedule_index++;
            }

        }

        return row;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, SetScheduleActivity.class);
        String s = (String) view.getTag();
        Log.d(TAG, "Current month index:" + CURRENT_MONTH_OF_CALENDAR);
        int day=Integer.parseInt(s.split("-",3)[0]);
        Day day1=day_schedule.get(day-1);
        String month = s.split("-", 3)[1];
        String year = s.split("-",3)[2];
        intent.putExtra("date",(String)view.getTag());
        intent.putExtra("day", Integer.parseInt(s.split("-",3)[0]));
        intent.putExtra("year",Integer.parseInt(year));
        intent.putExtra("day_bean", (android.os.Parcelable) day1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int month_index_of_grid_clicked = Arrays.asList(months).indexOf(month);
        intent.putExtra("month",month_index_of_grid_clicked);
        Log.d(TAG, "grid clicked month index" + month_index_of_grid_clicked);
        Log.d(TAG, "Day of the grid clicked :" + (String) view.getTag());

        if (month_in_foreground < month_index_of_grid_clicked) {
            Log.d(TAG,""+1);
            myScheduleFragment.showPrevMonth();

        } else {
            if (month_in_foreground == month_index_of_grid_clicked) {
                context.startActivity(intent);
                Log.d(TAG,""+2);


            } else {
                if (month_in_foreground > month_index_of_grid_clicked) {
                   Log.d(TAG,""+3);
                    myScheduleFragment.showNextMonth();

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