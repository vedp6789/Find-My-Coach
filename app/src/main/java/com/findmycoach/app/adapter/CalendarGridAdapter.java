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
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.MentorDetailsActivity;
import com.findmycoach.app.activity.SetScheduleActivity;
import com.findmycoach.app.beans.CalendarSchedule.Day;
import com.findmycoach.app.beans.CalendarSchedule.DayEvent;
import com.findmycoach.app.beans.CalendarSchedule.Event;
import com.findmycoach.app.beans.CalendarSchedule.MentorInfo;
import com.findmycoach.app.beans.CalendarSchedule.MonthYearInfo;
import com.findmycoach.app.beans.CalendarSchedule.Slot;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;
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
    private static ArrayList<Slot> prev_month_data = null;
    private static ArrayList<Slot> current_month_data = null;
    private static ArrayList<Slot> coming_month_data = null;
    private static ArrayList<Vacation> previousMonthNonCoincidingVacation = null;
    private static ArrayList<Vacation> currentMonthNonCoincidingVacation = null;
    private static ArrayList<Vacation> comingMonthNonCoincidingVacation = null;
    private static ArrayList<MonthYearInfo> previousMonthYearInfo = null;
    private static ArrayList<MonthYearInfo> currentMonthYearInfo = null;
    private static ArrayList<MonthYearInfo> comingMonthYearInfo = null;
    public ArrayList<MentorInfo> previousMonthMentorInfos = null;
    public ArrayList<MentorInfo> currentMonthMentorInfos = null;
    public ArrayList<MentorInfo> comingMonthMentorInfos = null;

    private static int day_schedule_index = 0;
    Day day = null;
    DayEvent dayEvent = null;
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
    public CalendarGridAdapter(Context context, int month, int year, MyScheduleFragment myScheduleFragment, ArrayList<Slot> prev_month_data, ArrayList<Slot> current_month_data, ArrayList<Slot> coming_month_data, ArrayList<Vacation> previousMonthNonCoincidingVacation, ArrayList<Vacation> currentMonthNonCoincidingVacation, ArrayList<Vacation> comingMonthNonCoincidingVacation, ArrayList<MonthYearInfo> previousMonthYearInfo, ArrayList<MonthYearInfo> currentMonthYearInfo, ArrayList<MonthYearInfo> comingMonthYearInfo, ArrayList<MentorInfo> previousMonthMentorInfos, ArrayList<MentorInfo> currentMonthMentorInfos, ArrayList<MentorInfo> comingMonthMentorInfos) {
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

        CalendarGridAdapter.prev_month_data = new ArrayList<Slot>();
        CalendarGridAdapter.current_month_data = new ArrayList<Slot>();
        CalendarGridAdapter.coming_month_data = new ArrayList<Slot>();
        CalendarGridAdapter.prev_month_data = prev_month_data;
        CalendarGridAdapter.current_month_data = current_month_data;
        CalendarGridAdapter.coming_month_data = coming_month_data;

        CalendarGridAdapter.previousMonthNonCoincidingVacation = new ArrayList<Vacation>();
        CalendarGridAdapter.currentMonthNonCoincidingVacation = new ArrayList<Vacation>();
        CalendarGridAdapter.comingMonthNonCoincidingVacation = new ArrayList<Vacation>();
        CalendarGridAdapter.previousMonthNonCoincidingVacation = previousMonthNonCoincidingVacation;
        CalendarGridAdapter.currentMonthNonCoincidingVacation = currentMonthNonCoincidingVacation;
        CalendarGridAdapter.comingMonthNonCoincidingVacation = comingMonthNonCoincidingVacation;


        CalendarGridAdapter.previousMonthYearInfo = new ArrayList<MonthYearInfo>();
        CalendarGridAdapter.currentMonthYearInfo = new ArrayList<MonthYearInfo>();
        CalendarGridAdapter.comingMonthYearInfo = new ArrayList<MonthYearInfo>();
        CalendarGridAdapter.previousMonthYearInfo = previousMonthYearInfo;
        CalendarGridAdapter.currentMonthYearInfo = currentMonthYearInfo;
        CalendarGridAdapter.comingMonthYearInfo = comingMonthYearInfo;

        month_for_which_calendar_get_populated = month;
        year_for_which_calendar_get_populated = year;
        allow_data_population_from_server_data = true;   /* This constructor get called when there is successful network communication so data from server helps in calendar population*/
        printMonth(month, year);

        eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
    }

    /*
        * This constructor is called from MentorDetailsActivity
        *
        * */
    public CalendarGridAdapter(Context context, int month, int year, MentorDetailsActivity mentorDetailsActivity, ArrayList<Slot> prev_month_data, ArrayList<Slot> current_month_data, ArrayList<Slot> coming_month_data, ArrayList<Vacation> previousMonthNonCoincidingVacation, ArrayList<Vacation> currentMonthNonCoincidingVacation, ArrayList<Vacation> comingMonthNonCoincidingVacation, ArrayList<MonthYearInfo> previousMonthYearInfo, ArrayList<MonthYearInfo> currentMonthYearInfo, ArrayList<MonthYearInfo> comingMonthYearInfo, ArrayList<MentorInfo> previousMonthMentorInfos, ArrayList<MentorInfo> currentMonthMentorInfos, ArrayList<MentorInfo> comingMonthMentorInfos, String mentor_id, String availability_yn, String charges, ArrayList<String> arraylist_subcategory, String connection_status) {
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

        CalendarGridAdapter.prev_month_data = new ArrayList<Slot>();
        CalendarGridAdapter.current_month_data = new ArrayList<Slot>();
        CalendarGridAdapter.coming_month_data = new ArrayList<Slot>();
        CalendarGridAdapter.prev_month_data = prev_month_data;
        CalendarGridAdapter.current_month_data = current_month_data;
        CalendarGridAdapter.coming_month_data = coming_month_data;

        CalendarGridAdapter.previousMonthNonCoincidingVacation = new ArrayList<Vacation>();
        CalendarGridAdapter.currentMonthNonCoincidingVacation = new ArrayList<Vacation>();
        CalendarGridAdapter.comingMonthNonCoincidingVacation = new ArrayList<Vacation>();
        CalendarGridAdapter.previousMonthNonCoincidingVacation = previousMonthNonCoincidingVacation;
        CalendarGridAdapter.currentMonthNonCoincidingVacation = currentMonthNonCoincidingVacation;
        CalendarGridAdapter.comingMonthNonCoincidingVacation = comingMonthNonCoincidingVacation;


        CalendarGridAdapter.previousMonthYearInfo = new ArrayList<MonthYearInfo>();
        CalendarGridAdapter.currentMonthYearInfo = new ArrayList<MonthYearInfo>();
        CalendarGridAdapter.comingMonthYearInfo = new ArrayList<MonthYearInfo>();
        CalendarGridAdapter.previousMonthYearInfo = previousMonthYearInfo;
        CalendarGridAdapter.currentMonthYearInfo = currentMonthYearInfo;
        CalendarGridAdapter.comingMonthYearInfo = comingMonthYearInfo;


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

        CURRENT_MONTH_OF_CALENDAR = calendar.get(Calendar.MONTH);
        CURRENT_YEAR_OF_CALENDAR = calendar.get(Calendar.YEAR);


        month_for_which_calendar_get_populated = month;
        year_for_which_calendar_get_populated = year;
        allow_data_population_from_server_data = false;
        printMonth(month, year);   /* This constructor get called when there is some problem to get data from network communication so data from server helps in calendar population*/
    }


    public CalendarGridAdapter(Context context, int month, int year, MyScheduleFragment myScheduleFragment) {
        super();
        Log.d(TAG,"CalendarGridAdapter constructor on failure");
        this.context = context;
        weekdays = context.getResources().getStringArray(R.array.week_days);
        months = context.getResources().getStringArray(R.array.months);
        this.myScheduleFragment = myScheduleFragment;
        this.list = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        CURRENT_MONTH_OF_CALENDAR = calendar.get(Calendar.MONTH);
        CURRENT_YEAR_OF_CALENDAR = calendar.get(Calendar.YEAR);


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
                    + "-TRAIL"
                    + "-"
                    + getMonthAsString(prevMonth)
                    + "-"
                    + prevYear);
        }


        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++) {

            if (i == getCurrentDayOfMonth() && currentMonth == CURRENT_MONTH_OF_CALENDAR && yy == CURRENT_YEAR_OF_CALENDAR) {
                list.add(String.valueOf(i) + "-TODAY" + "-" + getMonthAsString(currentMonth) + "-" + yy);
            } else {
                list.add(String.valueOf(i) + "-CURRENT" + "-" + getMonthAsString(currentMonth) + "-" + yy);
            }
        }

        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++) {
            list.add(String.valueOf(i + 1) + "-LEAD" + "-"
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
        int the_current_month =0;   /* current month in 0 to 11 format */

        for(int i =0; i < months.length; i++){
            if(months[i].equals(themonth)){
                the_current_month =i;
            }
        }
        String theyear = day_color[3];

        boolean allow_schedule_population = false;

        // Set the Day GridCell
        gridcell.setText(theday);
        gridcell.setTag(theday + "-" + themonth + "-" + theyear);


        if (day_color[1].equals("TRAIL") || day_color[1].equals("LEAD")) {
            gridcell.setTextColor(context.getResources().getColor(R.color.purple_light));
        }
        if (day_color[1].equals("CURRENT")) {
            gridcell.setTextColor(context.getResources().getColor(R.color.purple));
            allow_schedule_population = true;
        }
        if (day_color[1].equals("TODAY")) {
            gridcell.setTextColor(context.getResources().getColor(R.color.white));
            gridcell.setBackgroundColor(context.getResources().getColor(R.color.purple));
            allow_schedule_population = true;
        }
        day_schedule_index = Integer.parseInt(theday) - 1;
        if (allow_data_population_from_server_data) {
            if (allow_schedule_population) {  /* This conditions is true for the foreground month of the calendar so all the dates coming in this are active dates i.e. neither trailing nor leading days of the calenar  */

                /* Now current_Month_data size will be zero only when there is no slot found for this month */
                /* So assuming that when size will be zero and we do not need to populate calendar */
                if (current_month_data.size() > 0) {
                    Slot slot = current_month_data.get(0);
                    boolean slot_created_when_network_found = Boolean.parseBoolean(slot.isSlot_created_on_network_success());

                    Slot prev_month_slot;
                    boolean prev_month_slot_created_when_network_found = false;
                    if (prev_month_data.size() > 0) {
                        prev_month_slot = prev_month_data.get(0);
                        prev_month_slot_created_when_network_found = Boolean.parseBoolean(prev_month_slot.isSlot_created_on_network_success());
                    } else {
                        prev_month_slot_created_when_network_found = true; /* I am populating arrayLists for with one flag if network is not found and if there is no data coming from for the desired month then the arrayList for that month size is 0 */
                    }


                    Slot coming_month_slot;
                    boolean coming_month_slot_created_when_network_found;
                    if (coming_month_data.size() > 0) {
                        coming_month_slot = coming_month_data.get(0);
                        coming_month_slot_created_when_network_found = Boolean.parseBoolean(coming_month_slot.isSlot_created_on_network_success());
                    } else {
                        coming_month_slot_created_when_network_found = true;
                    }


                    if (!slot_created_when_network_found || !prev_month_slot_created_when_network_found || !coming_month_slot_created_when_network_found) {   /* This is assuring that when all three month arrayLists made is on successful network succes then only it is populating data on calendar */
                        /* this is happening when user either go to previous, next month and device found network but earlier network was not available and arraylist for current_month_data made at that time then in this case we have to get this month data from server again*/
                        if (allow_once) {
                            // Toast.makeText(context, "Refreshing the schedule!", Toast.LENGTH_SHORT).show();
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

                    } else {
                        /* this proves this that there is some schedule for user in this month as there is slot and with true network status */



                /*
                *
                *
                * success when CalendarGridAdapter is used by MyScheduleFragment class
                * */
                        if (myScheduleFragment != null) {
                            if (StorageHelper.getUserDetails(context, "user_group").equals("3")) {

                                AvailabilityFlags availabilityFlags = new AvailabilityFlags();


                                Calendar calendar_this_day = Calendar.getInstance();
                                calendar_this_day.set(Integer.parseInt(theyear), the_current_month, Integer.parseInt(theday));
                                long this_day = calendar_this_day.getTimeInMillis();
                                int week_day_for_this_day = calendar_this_day.get(Calendar.DAY_OF_WEEK);/* This will give week_day for this day, 1 to 7 for Sunday to Saturday */

                                finalizeWhatTypeOfOccurencesForThisDay(calendar_this_day, this_day, week_day_for_this_day, availabilityFlags);




                                /* Now to check whether any any coinciding vacation found after slot match for this day, if not found then we will check whether this day is having any non coinciding vacation or not, if found then we do not need to check */
                                if (!availabilityFlags.vacation_found) {  /* here checking current status of availabilityFlags so that if there is already some vacation found then do not need to check for non coinciding vacation*/
                                    if (currentMonthNonCoincidingVacation.size() > 0) {
                                        for (int non_coinciding_vacation = 0; non_coinciding_vacation < currentMonthNonCoincidingVacation.size(); non_coinciding_vacation++) {
                                            Vacation vacation = currentMonthNonCoincidingVacation.get(non_coinciding_vacation);
                                            String vacation_start_date = vacation.getStart_date();
                                            String vacation_stop_date = vacation.getStop_date();

                                            Calendar calendar_vacation_start_date = Calendar.getInstance();
                                            calendar_vacation_start_date.set(Integer.parseInt(vacation_start_date.split("-")[0]), Integer.parseInt(vacation_start_date.split("-")[1]) - 1, Integer.parseInt(vacation_start_date.split("-")[2]));
                                            long vacation_start_millis = calendar_vacation_start_date.getTimeInMillis();

                                            Calendar calendar_vacation_stop_date = Calendar.getInstance();
                                            calendar_vacation_stop_date.set(Integer.parseInt(vacation_stop_date.split("-")[0]), Integer.parseInt(vacation_stop_date.split("-")[1]) - 1, Integer.parseInt(vacation_stop_date.split("-")[2]));
                                            long vacation_stop_millis = calendar_vacation_stop_date.getTimeInMillis();


                                            if ((this_day == vacation_start_millis) || (this_day == vacation_stop_millis) || (this_day < vacation_stop_millis && this_day > vacation_start_millis)) {

                                            /* Now checking whether the_day is having week day similar to one of the vacation week days, if found then we have to consider this vacation for this day otherwise not*/
                                                availabilityFlags.vacation_found = true;  /* proves one of non coinciding vacation coming for this day*/
                                                break;  /* as we have to just know that there is any vacation or not for this day (grid day which is going to be populated)*/
                                            }
                                        }
                                    }
                                }


                                if (availabilityFlags.slot_found) {
                                    if (availabilityFlags.event_found) {
                                        if (availabilityFlags.vacation_found) {
                                            /* event and vacation both are there */
                                            gridcell.setBackgroundColor(Color.RED);


                                        } else {
                                            /*only event found*/
                                            gridcell.setBackgroundColor(Color.CYAN);
                                        }
                                    } else {
                                        if (availabilityFlags.vacation_found) {
                                            /* only vacation found */
                                            gridcell.setBackgroundColor(Color.BLUE);
                                        } else {
                                            /* neither event found nor vacation, only slot is there */
                                            gridcell.setBackgroundColor(Color.LTGRAY);
                                        }
                                    }
                                } else {
                                    if (availabilityFlags.vacation_found) {
                                        /* no slot found on this day there is only vacation */
                                        gridcell.setBackgroundColor(Color.YELLOW);
                                    }
                                }

                            /*List<DayEvent> dayEvents = day.getDayEvents();
                            List<DaySlot> daySlots = day.getDaySlots();
                            List<DayVacation> dayVacations = day.getDayVacations();

                            if (dayEvents.size() > 0) {
                                if (day_color[1].equals("BLUE")) {
                                    gridcell.setBackground(context.getResources().getDrawable(R.drawable.scheduled_event_arrow_today));
                                } else {
                                    gridcell.setBackground(context.getResources().getDrawable(R.drawable.scheduled_event_arrow));
                                }
                            }*/
                            }
                            if (StorageHelper.getUserDetails(context, "user_group").equals("2")) {

                                AvailabilityFlags availabilityFlags = new AvailabilityFlags();


                                Calendar calendar_this_day = Calendar.getInstance();
                                calendar_this_day.set(Integer.parseInt(theyear),the_current_month, Integer.parseInt(theday));
                                long this_day = calendar_this_day.getTimeInMillis();
                                int week_day_for_this_day = calendar_this_day.get(Calendar.DAY_OF_WEEK);/* This will give week_day for this day, 1 to 7 for Sunday to Saturday */

                                finalizeWhatTypeOfOccurencesForThisDay(calendar_this_day, this_day, week_day_for_this_day, availabilityFlags);


                                /* We will have to populate calendar grid color only when there is some slot for this */
                                if (availabilityFlags.slot_found) {
                                    if (availabilityFlags.event_found) {
                                        if (availabilityFlags.vacation_found) {
                                            /* both event and vacation found */
                                            gridcell.setBackgroundColor(Color.YELLOW);
                                        } else {
                                            /* only event found*/
                                            gridcell.setBackgroundColor(Color.LTGRAY);
                                        }
                                    } else {
                                        if (availabilityFlags.vacation_found) {
                                            /* only vacation found*/
                                            gridcell.setBackgroundColor(Color.BLUE);
                                        }
                                    }

                                }
                            }


                        } else {   /* For Mentors Detail Activity*/

                            Calendar calendar_this_day = Calendar.getInstance();
                            calendar_this_day.set(Integer.parseInt(theyear), the_current_month, Integer.parseInt(theday));
                            long this_day = calendar_this_day.getTimeInMillis();
                            int week_day_for_this_day = calendar_this_day.get(Calendar.DAY_OF_WEEK);/* This will give week_day for this day, 1 to 7 for Sunday to Saturday */

                            int free_slots = 0;

                            Calendar rightNow = Calendar.getInstance();
                            long current_date_in_millis = rightNow.getTimeInMillis();

                            if (current_date_in_millis > this_day) {
                                free_slots = -2;    /* this is know that grid for the current view is behind right now date and time, so in this case calendar will not show any available free slot on this grid tap */
                            } else {
                                free_slots = finalizeFreeSlotsForThisDay(calendar_this_day);

                            }



                            /*
                            * if free_slot is having value greater than zero, it means this day has free slots and we have to populate calendar grid color
                            * */
                            if (free_slots > 0) {
                                if (day_color[1].equals("CURRENT")) {
                                    gridcell.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.scheduled_event_arrow_today));
                                } else {
                                    gridcell.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.scheduled_event_arrow));
                                }
                            } else {
                                if (day_color[1].equals("CURRENT")) {
                                    gridcell.setBackgroundColor(context.getResources().getColor(R.color.purple));
                                }
                            }


                            gridcell.setTag(R.id.TAG_FREE_SLOT, String.valueOf(free_slots));


                        }


                    }

                } else {    /* executes when there is no slot found for the current month */

                    if (currentMonthNonCoincidingVacation.size() > 0) {
                        Calendar calendar_this_day = Calendar.getInstance();
                        calendar_this_day.set(Integer.parseInt(theyear),the_current_month, Integer.parseInt(theday));
                        long this_day = calendar_this_day.getTimeInMillis();
                        int week_day_for_this_day = calendar_this_day.get(Calendar.DAY_OF_WEEK);/* This will give week_day for this day, 1 to 7 for Sunday to Saturday */


                        Vacation vacation = currentMonthNonCoincidingVacation.get(0);
                        Boolean vacation_made_at_network_status = Boolean.parseBoolean(vacation.getVacation_made_at_network_success());

                        Vacation prev_month_vacation;
                        boolean prev_month_vacation_created_when_network_found = false;
                        if (previousMonthNonCoincidingVacation.size() > 0) {
                            prev_month_vacation = previousMonthNonCoincidingVacation.get(0);
                            prev_month_vacation_created_when_network_found = Boolean.parseBoolean(prev_month_vacation.getVacation_made_at_network_success());
                        } else {
                            prev_month_vacation_created_when_network_found = true; /* When the previousMonthNonCoincidingVacation arrayList size is 0 it means that network comm. is successful but there is no vacation found for this month*/
                        }

                        Vacation coming_month_vacation;
                        boolean coming_month_slot_created_when_network_found = false;
                        if (comingMonthNonCoincidingVacation.size() > 0) {
                            coming_month_vacation = comingMonthNonCoincidingVacation.get(0);
                            coming_month_slot_created_when_network_found = Boolean.parseBoolean(coming_month_vacation.getVacation_made_at_network_success());
                        } else {
                            coming_month_slot_created_when_network_found = true; /* When the comingMonthNonCoincidingVacation arrayList size is 0 it means that network comm. is successful but there is no vacation found for this month*/
                        }


                        if (vacation_made_at_network_status && prev_month_vacation_created_when_network_found && coming_month_slot_created_when_network_found) {/* This is assuring that when all three month arrayLists made is on successful network succes then only it is populating data on calendar */
                        /* Only non coinciding vacation found on this day, Show this day with Vacation with handling mentor schedule */
                            /* This case is needed to handle only for Mentor's Schedule as mentee has no concern for non coinciding vacations*/
                            if (myScheduleFragment != null) {
                                if (StorageHelper.getUserGroup(context, "user_group").equals("3")) {
                                        /* for mentor schedule */

                                    AvailabilityFlags availabilityFlags = new AvailabilityFlags();
                                    for (int current_month_non_coinciding_vacation_index = 0; current_month_non_coinciding_vacation_index < currentMonthNonCoincidingVacation.size(); current_month_non_coinciding_vacation_index++) {
                                        Vacation vacation1 = currentMonthNonCoincidingVacation.get(current_month_non_coinciding_vacation_index);
                                        String vacation_start_date = vacation1.getStart_date();
                                        String vacation_stop_date = vacation1.getStop_date();

                                        Calendar calendar_vacation_start_date = Calendar.getInstance();
                                        calendar_vacation_start_date.set(Integer.parseInt(vacation_start_date.split("-")[0]), Integer.parseInt(vacation_start_date.split("-")[1]) - 1, Integer.parseInt(vacation_start_date.split("-")[2]));
                                        long vacation_start_millis = calendar_vacation_start_date.getTimeInMillis();

                                        Calendar calendar_vacation_stop_date = Calendar.getInstance();
                                        calendar_vacation_stop_date.set(Integer.parseInt(vacation_stop_date.split("-")[0]), Integer.parseInt(vacation_stop_date.split("-")[1]) - 1, Integer.parseInt(vacation_stop_date.split("-")[2]));
                                        long vacation_stop_millis = calendar_vacation_stop_date.getTimeInMillis();


                                        if ((this_day == vacation_start_millis) || (this_day == vacation_stop_millis) || (this_day < vacation_stop_millis && this_day > vacation_start_millis)) {

                                            /* Now checking whether the_day is having week day similar to one of the vacation week days, if found then we have to consider this vacation for this day otherwise not*/
                                            availabilityFlags.vacation_found = true;  /* proves one of non coinciding vacation coming for this day*/
                                            break;  /* as we have to just know that there is any vacation or not for this day (grid day which is going to be populated)*/


                                        }


                                    }

                                    if (availabilityFlags.vacation_found) {
                                          /* only vacation found */
                                        gridcell.setBackgroundColor(Color.YELLOW);
                                    }


                                }
                            } else {
                                /* for MentorDetailsActivity which is coming in mentee's app for mentor availability in mentor profile view*/
                                int free_slots = -1;   /* it is for those slots where there are no slots for this day */


                            /*
                            * if free_slot is having value greater than zero, it means this day has free slots and we have to populate calendar grid color
                            * */
                                if (free_slots > 0) {
                                    if (day_color[1].equals("CURRENT")) {
                                        gridcell.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.scheduled_event_arrow_today));
                                    } else {
                                        gridcell.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.scheduled_event_arrow));
                                    }
                                } else {
                                    if (day_color[1].equals("CURRENT")) {
                                        gridcell.setBackgroundColor(context.getResources().getColor(R.color.purple));
                                    }
                                }


                                gridcell.setTag(R.id.TAG_FREE_SLOT, String.valueOf(free_slots));

                            }

                        } else {
                            /* this is happening when user either go to previous, next month and device found network but earlier network was not available and arraylist for current_month_data made at that time then in this case we have to get this month data from server again*/
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

                    } else {

                        Log.d(TAG,"expecting bug");

                        /*if(myScheduleFragment != null){

                        }*/

                        /* Calendar days don't need to show any schedule as the current_month_data have no slot */
                        /* For MentorDetailsActivity there is no slot found */
                        int free_slots = -1;   /* it is for those slots where there are no slots for this day */


                            /*
                            * if free_slot is having value greater than zero, it means this day has free slots and we have to populate calendar grid color
                            * */
                        if (free_slots > 0) {
                            if (day_color[1].equals("CURRENT")) {
                                gridcell.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.scheduled_event_arrow_today));
                            } else {
                                gridcell.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.scheduled_event_arrow));
                            }
                        } else {
                            if (day_color[1].equals("CURRENT")) {
                                Log.d(TAG,"expecting bug1");

                                gridcell.setBackgroundColor(context.getResources().getColor(R.color.purple));
                            }
                        }


                        gridcell.setTag(R.id.TAG_FREE_SLOT, String.valueOf(free_slots));

                    }

                }


            }
        }


        return row;
    }

    private int finalizeFreeSlotsForThisDay(Calendar calendar_this_day) {

        if (current_month_data.size() > 0) {
            int free_slot = 0;
            long calendar_this_day_millis = calendar_this_day.getTimeInMillis();
            int week_day_of_this_day = calendar_this_day.get(Calendar.DAY_OF_WEEK);

               /* Now to decide no. of free slots available for this day from current_month_data arraylist which have slots */
            for (int current_month_data_index = 0; current_month_data_index < current_month_data.size(); current_month_data_index++) {
                Slot new_slot = current_month_data.get(current_month_data_index);
                String slot_start_date = new_slot.getSlot_start_date();
                String slot_stop_date = new_slot.getSlot_stop_date();
                String slot_type = new_slot.getSlot_type();
                int max_users = Integer.parseInt(new_slot.getSlot_max_users());
                List<Event> events = new_slot.getEvents();  /* events can have max one event*/
                List<Vacation> vacations = new_slot.getVacations();
                String[] slot_week_day = new_slot.getSlot_week_days();

                Calendar calendar_slot_start_date = Calendar.getInstance();
                calendar_slot_start_date.set(Integer.parseInt(slot_start_date.split("-")[0]), Integer.parseInt(slot_start_date.split("-")[1]) - 1, Integer.parseInt(slot_start_date.split("-")[2]));
                long calendar_slot_start_date_in_millis = calendar_slot_start_date.getTimeInMillis();

                Calendar calendar_slot_stop_date = Calendar.getInstance();
                calendar_slot_stop_date.set(Integer.parseInt(slot_stop_date.split("-")[0]), Integer.parseInt(slot_stop_date.split("-")[1]) - 1, Integer.parseInt(slot_stop_date.split("-")[2]));
                long calendar_slot_stop_date_in_millis = calendar_slot_stop_date.getTimeInMillis();

                if ((calendar_this_day_millis == calendar_slot_start_date_in_millis) || (calendar_this_day_millis == calendar_slot_stop_date_in_millis) || (calendar_this_day_millis > calendar_slot_start_date_in_millis && calendar_this_day_millis < calendar_slot_stop_date_in_millis)) {
                    if (thisDayMatchesWithWeekDaysArray(slot_week_day, week_day_of_this_day)) {
                           /* This proves that this day is coming between this slot */
                           /* Now to check whether this slot is free or not */
                        if (slot_type.equalsIgnoreCase("group")) {   /* Group slot type */
                            if (events.size() > 0) {
                                Event event = events.get(0);
                                int active_users = Integer.parseInt(event.getEvent_total_mentee());
                                if (active_users < max_users) {
                                        /* slot is free */
                                    free_slot += checkForVacations(vacations, calendar_this_day_millis, week_day_of_this_day);


                                }
                            } else {
                                     /* No event found which means that this slot can be free */
                                free_slot += checkForVacations(vacations, calendar_this_day_millis, week_day_of_this_day);   /* Checking whether there is any vacation for this day or not */


                            }
                        } else {
                               /* Case of Individual slot type */
                            if (events.size() > 0) {
                                   /* In Individual slot, if events size is greater than 0 then that slot is not free anymore*/


                            } else {
                                     /* No event found which means that this slot can be free */
                                free_slot += checkForVacations(vacations, calendar_this_day_millis, week_day_of_this_day);   /* Checking whether there is any vacation for this day or not */


                            }
                        }

                    }
                }

            }
            return free_slot;
        } else {
            return -1;  /* No slots found for this day */
        }
    }

    private int checkForVacations(List<Vacation> vacations, long calendar_this_day_millis, int week_day_of_this_day) {
        int free_slot = 0;
        boolean vacation_found = false;
        if (vacations.size() > 0) {
            for (int vacation_number = 0; vacation_number < vacations.size(); vacation_number++) {
                Vacation vacation = vacations.get(vacation_number);
                String vacation_start_date = vacation.getStart_date();
                String vacation_stop_date = vacation.getStop_date();
                Calendar calendar_vacation_start_date = Calendar.getInstance();
                calendar_vacation_start_date.set(Integer.parseInt(vacation_start_date.split("-")[0]), Integer.parseInt(vacation_start_date.split("-")[1]), Integer.parseInt(vacation_start_date.split("-")[2]));
                long calendar_vacation_start_date_in_millis = calendar_vacation_start_date.getTimeInMillis();

                Calendar calendar_vacation_stop_date = Calendar.getInstance();
                calendar_vacation_stop_date.set(Integer.parseInt(vacation_stop_date.split("-")[0]), Integer.parseInt(vacation_stop_date.split("-")[1]), Integer.parseInt(vacation_stop_date.split("-")[2]));
                long calendar_vacation_stop_date_in_millis = calendar_vacation_stop_date.getTimeInMillis();

                if ((calendar_this_day_millis == calendar_vacation_start_date_in_millis) || (calendar_this_day_millis == calendar_vacation_stop_date_in_millis) || (calendar_this_day_millis > calendar_vacation_start_date_in_millis && calendar_this_day_millis < calendar_vacation_stop_date_in_millis)) {
                    vacation_found = true;
                    break;
                }


            }
            if (!vacation_found) {
                free_slot++;
            }

        } else {
            free_slot++;
        }
        return free_slot;
    }

    private void finalizeWhatTypeOfOccurencesForThisDay(Calendar calendar_this_day, long this_day, int week_day_for_this_day, AvailabilityFlags availabilityFlags) {
        for (int current_month_data_index = 0; current_month_data_index < current_month_data.size(); current_month_data_index++) {     /* current_month_data is having Slot*/
            Slot new_slot = current_month_data.get(current_month_data_index);

            String slot_start_date = new_slot.getSlot_start_date();
            String slot_stop_date = new_slot.getSlot_stop_date();
            String[] slot_week_days = new_slot.getSlot_week_days();

            Calendar calendar_slot_start_date = Calendar.getInstance();
            calendar_slot_start_date.set(Integer.parseInt(slot_start_date.split("-")[0]), Integer.parseInt(slot_start_date.split("-")[1]) - 1, Integer.parseInt(slot_start_date.split("-")[2]));
            long slot_start_millis = calendar_slot_start_date.getTimeInMillis();

            Calendar calendar_slot_stop_date = Calendar.getInstance();
            calendar_slot_stop_date.set(Integer.parseInt(slot_stop_date.split("-")[0]), Integer.parseInt(slot_stop_date.split("-")[1]) - 1, Integer.parseInt(slot_stop_date.split("-")[2]));
            long slot_stop_millis = calendar_slot_stop_date.getTimeInMillis();

            if ((this_day == slot_start_millis) || (this_day == slot_stop_millis) || (this_day < slot_stop_millis && this_day > slot_start_millis)) {

                                        /* Now checking whether the_day is having week day similar to one of the slot week days, if found then we have to consider this slot for this day otherwise not*/
                if (thisDayMatchesWithWeekDaysArray(slot_week_days, week_day_for_this_day)) {
                    availabilityFlags.slot_found = true;
                                            /* Now to check whether in this slot any event is coming for this day and now we do not need to check week day of this day as if event is there so evnets are of same slot and we already checked week_day for the slot*/
                    if (new_slot.anyEventFound(this_day)) {
                        availabilityFlags.event_found = true;
                    }

                                            /* Now to check whether any coinciding vacation found or not */
                    if (new_slot.anyVacationFound(calendar_this_day)) {
                        availabilityFlags.vacation_found = true;
                    }
                }


            }


        }
    }

    private boolean thisDayMatchesWithWeekDaysArray(String[] slot_week_days, int week_day) {
        boolean day_matches = false;
        String this_day_week_day = null;   /* this will have the day which is calendar current day according to grid view position*/
        switch (week_day) {
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

        for (int slot_week_day_index = 0; slot_week_day_index < slot_week_days.length; slot_week_day_index++) {
            if (this_day_week_day != null && this_day_week_day.equalsIgnoreCase(slot_week_days[slot_week_day_index])) {
                day_matches = true;
            }
        }

        return day_matches;
    }

    @Override
    public void onClick(View view) {

        if (allow_data_population_from_server_data) {
            Intent intent = new Intent(context, SetScheduleActivity.class);
            String s = (String) view.getTag();
            int no_of_free_slots = 0;   /* getting used for MentorDetailsActivity, to decide what to do on the particular grid or day click */

            int day = Integer.parseInt(s.split("-", 3)[0]);

            String month = s.split("-", 3)[1];
            String year = s.split("-", 3)[2];
            if (myScheduleFragment != null) {
                intent.putExtra("for", "ScheduleFragments");
                if (StorageHelper.getUserGroup(context, "user_group").equals("3")) {  /* sending non coinciding vacations to SetScheduleActivity only in the case of Mentor's schedule */
                    intent.putExtra("previous_month_non_coinciding_vacation", previousMonthNonCoincidingVacation);
                    intent.putExtra("current_month_non_coinciding_vacation", currentMonthNonCoincidingVacation);
                    intent.putExtra("coming_month_non_coinciding_vacation", comingMonthNonCoincidingVacation);
                }
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
            intent.putExtra("previous_month_year_info", previousMonthYearInfo);
            intent.putExtra("current_month_year_info", currentMonthYearInfo);
            intent.putExtra("coming_month_year_info", comingMonthYearInfo);
            intent.putExtra("prev_month_mentor", previousMonthMentorInfos);
            intent.putExtra("current_month_mentor", currentMonthMentorInfos);
            intent.putExtra("coming_month_mentor", comingMonthMentorInfos);


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
                                        if (no_of_free_slots == -1) {
                                            Toast.makeText(context, context.getResources().getString(R.string.no_slot_from_mentor), Toast.LENGTH_SHORT).show();

                                        } else {
                                            if (no_of_free_slots == -2) {
                                                Toast.makeText(context, context.getResources().getString(R.string.past_day), Toast.LENGTH_SHORT).show();
                                            }
                                        }
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


    public class AvailabilityFlags {
        boolean event_found = false;
        boolean vacation_found = false;
        boolean slot_found = false;
    }
}