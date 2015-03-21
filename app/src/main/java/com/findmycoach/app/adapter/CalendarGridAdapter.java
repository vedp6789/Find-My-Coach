package com.findmycoach.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.SetScheduleActivity;
import com.findmycoach.app.beans.CalendarSchedule.Day;
import com.findmycoach.app.beans.CalendarSchedule.DayEvent;
import com.findmycoach.app.fragment.MyScheduleFragment;

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
    private String[] weekdays;
    private String[] months;
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



    private static ArrayList<Day> prev_month_data=null;
    private static ArrayList<Day> current_month_data=null;
    private static ArrayList<Day> coming_month_data=null;
    private static int day_schedule_index = 0;
    Day day = null;
    DayEvent dayEvent = null;


    // Days in Current Month
    public CalendarGridAdapter(Context context, int month, int year, MyScheduleFragment myScheduleFragment, ArrayList<Day> prev_month_data, ArrayList<Day> current_month_data,ArrayList<Day> coming_month_data) {
        super();
        this.context = context;
        weekdays = context.getResources().getStringArray(R.array.week_days);
        months = context.getResources().getStringArray(R.array.months);
        this.list = new ArrayList<String>();
        this.myScheduleFragment = myScheduleFragment;

        Log.d(TAG,"Inside CalendarGridAdapter");

        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));


        CURRENT_MONTH_OF_CALENDAR = calendar.get(Calendar.MONTH);
        CURRENT_YEAR_OF_CALENDAR = calendar.get(Calendar.YEAR);
        month_in_foreground = month - 1;

        Log.d(TAG, "Current Month of Calendar : " + CURRENT_MONTH_OF_CALENDAR);
        Log.d(TAG, "Current Year of Calendar : " + CURRENT_YEAR_OF_CALENDAR);
        Log.d(TAG, "Current day of month : " + currentDayOfMonth);
        Log.d(TAG, "Current week day : "  + currentWeekDay);


        CalendarGridAdapter.prev_month_data=new ArrayList<Day>();
        CalendarGridAdapter.current_month_data=new ArrayList<Day>();
        CalendarGridAdapter.coming_month_data=new ArrayList<Day>();
        CalendarGridAdapter.prev_month_data=prev_month_data;
        CalendarGridAdapter.current_month_data=current_month_data;
        CalendarGridAdapter.coming_month_data=coming_month_data;
        // Print Month
        printMonth(month, year);

        // Find Number of Events
        eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
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
            gridcell.setTextColor(context.getResources().getColor(R.color.caldroid_darker_gray));
            gridcell.setBackgroundResource(R.drawable.abc_btn_check_material);
        }
        if (day_color[1].equals("WHITE")) {
            gridcell.setTextColor(context.getResources().getColor(R.color.caldroid_black));
            allow_schedule_population = true;

        }
        if (day_color[1].equals("BLUE")) {
            gridcell.setTextColor(context.getResources().getColor(R.color.caldroid_holo_blue_dark));
            allow_schedule_population = true;

        }

        day_schedule_index = Integer.parseInt(theday) - 1;
        if (allow_schedule_population) {
            if (day_schedule_index < current_month_data.size()) {
                day = current_month_data.get(day_schedule_index);
                List<DayEvent> dayEvents = day.getDayEvents();
                if (dayEvents.size() > 0) {
                    gridcell.setBackgroundColor(new Color().CYAN);
                    /*for (int i = 0; i < dayEvents.size(); i++) {
                        dayEvent = dayEvents.get(i);

                    }*/

                }

            }

        }

        return row;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, SetScheduleActivity.class);
        String s = (String) view.getTag();

        int day = Integer.parseInt(s.split("-", 3)[0]);

        String month = s.split("-", 3)[1];
        String year = s.split("-", 3)[2];
        intent.putExtra("date", (String) view.getTag());
        intent.putExtra("day", Integer.parseInt(s.split("-", 3)[0]));
        intent.putExtra("year", Integer.parseInt(year));
        //Log.d(TAG, "three months data list size" + three_months_data.size());
        intent.putExtra("prev_month_data", prev_month_data);
        intent.putExtra("current_month_data", current_month_data);
        intent.putExtra("coming_month_data",coming_month_data);

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
                myScheduleFragment.showPrevMonth();
            } else {
                //myScheduleFragment.showPrevMonth();
                myScheduleFragment.showNextMonth();
            }



        } else {
            if (month_in_foreground == month_index_of_grid_clicked) {
                context.startActivity(intent);


            } else {
                if (month_in_foreground == 11 && month_index_of_grid_clicked == 0) {
                    myScheduleFragment.showNextMonth();

                } else {
                    if (month_in_foreground > month_index_of_grid_clicked) {

                        //myScheduleFragment.showNextMonth();
                        myScheduleFragment.showPrevMonth();


                    }
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