package com.findmycoach.app.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.ScheduleNewClass;
import com.findmycoach.app.activity.ScheduleYourVacation;
import com.findmycoach.app.adapter.CalendarGridAdapter;
import com.findmycoach.app.beans.CalendarSchedule.Day;
import com.findmycoach.app.beans.CalendarSchedule.DayEvent;
import com.findmycoach.app.beans.CalendarSchedule.DaySlot;
import com.findmycoach.app.fragment_mentor.LocationForSchedule;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class MyScheduleFragment extends Fragment implements View.OnClickListener, Callback {

    private TextView currentMonth, add_slot, add_vacation;
    public   CheckBox cb_calendar_by_location;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private GridView calendarView;
    private CalendarGridAdapter adapter1,adapter2,adapter3;
    private Calendar _calendar;
    private static int month, year;
    private static final String dateTemplate = "MMMM yyyy";
    private MyScheduleFragment myScheduleFragment;
    private int days_in_current_month, days_in_prev_month, days_in_next_month;
    private static final String TAG = "FMC";
    ProgressDialog progressDialog;
    private int days_in_new_prev_month, days_in_new_next_month;
    public static ArrayList<Day> previousMonthArrayList = null;
    public static ArrayList<Day> currentMonthArrayList = null;
    public static ArrayList<Day> comingMonthArrayList = null;
    public JSONArray prev_json, current_json, next_json;
    public String calendar_by_location = null;
    public boolean cb_calendar_by_location_is_checked=false,b_three_months_data;


    public MyScheduleFragment() {
        // Required empty public constructor
    }

    public static MyScheduleFragment newInstance(String param1, String param2) {
        MyScheduleFragment fragment = new MyScheduleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myScheduleFragment = this;

        startPointForCalendar();
        b_three_months_data=false;
    }
    /* Get Calendar current instance*/
    void startPointForCalendar(){
        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;   /* current month*/
        year = _calendar.get(Calendar.YEAR); /* current year */

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        // Checking logged in user and return respective view
        if (DashboardActivity.dashboardActivity.user_group == 2) {
            view = inflater.inflate(R.layout.fragment_schedule_mentee, container, false);
            initializeMenteeView(view);
        } else if (DashboardActivity.dashboardActivity.user_group == 3) {
            view = inflater.inflate(R.layout.my_calendar_view, container, false);
            initialize(view);
            applyListeners();
        }
        return view;
    }

    /* Initializing views for Mentee login */
    private void initializeMenteeView(View view) {

        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (GridView) view.findViewById(R.id.calendar);


        /* Array list of 3 months previous, current and coming , These points Day class object for day details like class schedule*/
        previousMonthArrayList = new ArrayList<Day>();
        currentMonthArrayList = new ArrayList<Day>();
        comingMonthArrayList = new ArrayList<Day>();


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        RequestParams requestParams = new RequestParams();
        requestParams.add("user_group", String.valueOf(2));
        requestParams.add("student_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        StringBuilder stringBuilder = new StringBuilder();

        /*Checking previous month possibilities for month and year as we have to get no. of days from previous month and adding this with current and coming month */
        if (month == 1) {
            Calendar calendar = new GregorianCalendar(year - 1, 11, 1);
            stringBuilder.append((year - 1));
            stringBuilder.append("/" + 12);
            stringBuilder.append("/" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) - 1, 1);
            stringBuilder.append(year);
            stringBuilder.append("/" + (month - 1));
            stringBuilder.append("/" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }


        if (month == 12) {
            Calendar calendar = new GregorianCalendar(year + 1, 0, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) + 1, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        days_in_current_month = new GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
        Toast.makeText(getActivity(), getResources().getString(R.string.start_date1) + String.valueOf(stringBuilder), Toast.LENGTH_SHORT).show();
        requestParams.add("start_date", String.valueOf(stringBuilder));
        requestParams.add("limit", String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));
        progressDialog.show();
        NetworkClient.getMenteeCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 40);  /*Network operation for getting details for three months */
    }

    private void applyListeners() {


    }

    /* Initializing views for Mentor login */
    public void initialize(final View view) {

        add_slot = (TextView) view.findViewById(R.id.tv_add_new_slot);
        add_slot.setOnClickListener(this);

        add_vacation = (TextView) view.findViewById(R.id.tv_add_vacation);
        add_vacation.setOnClickListener(this);

        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (GridView) view.findViewById(R.id.calendar);
        getCalendarDetailsAPICall();    /* API call for 3 months data */

        /* Checkbox used to set flag for viewing calendar details by location */
        cb_calendar_by_location = (CheckBox) view.findViewById(R.id.cb_calendar_by_location);
        cb_calendar_by_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {                                                                 /* Starting a fragment to get the location detail by autotype method */
                    cb_calendar_by_location_is_checked=true;
                    getLocationFromDialog();
                } else {
                    cb_calendar_by_location_is_checked=false;

                    if(calendar_by_location != null && calendar_by_location.trim().length() > 0){
                        Log.d(TAG,"calendar_by_location strig size :"+calendar_by_location.trim().length());
                        getCalendarDetailsAPICall();
                    }

                }
            }
        });

    }

    void getLocationFromDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        LocationForSchedule locationForSchedule = new LocationForSchedule();
        locationForSchedule.myScheduleFragment = MyScheduleFragment.this;
        locationForSchedule.show(fragmentManager, null);

    }

    public void getCalendarDetailsAPICall() {

        previousMonthArrayList = new ArrayList<Day>();
        currentMonthArrayList = new ArrayList<Day>();
        comingMonthArrayList = new ArrayList<Day>();

        startPointForCalendar();


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getResources().getString(R.string.please_wait));

        RequestParams requestParams = new RequestParams();
        requestParams.add("user_group", String.valueOf("3"));
        requestParams.add("mentor_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        StringBuilder stringBuilder = new StringBuilder();

/*Checking previous month possibilities for month and year as we have to get no. of days from previous month and adding this with current and coming month */
        if (month == 1) {
            Calendar calendar = new GregorianCalendar(year - 1, 11, 1);
            stringBuilder.append((year - 1));
            stringBuilder.append("/" + 12);
            stringBuilder.append("/" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) - 1, 1);
            stringBuilder.append(year);
            stringBuilder.append("/" + (month - 1));
            stringBuilder.append("/" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }


        if (month == 12) {
            Calendar calendar = new GregorianCalendar(year + 1, 0, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) + 1, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        days_in_current_month = new GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH);

        Toast.makeText(getActivity(), getResources().getString(R.string.start_date1) + String.valueOf(stringBuilder), Toast.LENGTH_SHORT).show();
        requestParams.add("start_date", String.valueOf(stringBuilder));
        requestParams.add("limit", String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));

        if (cb_calendar_by_location_is_checked) {
            Log.d(TAG,"calendar_by_location is checked true");
            if (calendar_by_location != null && !calendar_by_location.trim().equals("")) {
                Log.d(TAG, "Calendar_by_location getting passed to server : " + calendar_by_location);
                requestParams.add("location", calendar_by_location);
                networkCall1(requestParams);
            } else {

//                getLocationFromDialog();  /* start LocationFromDialog to get the location */
                cb_calendar_by_location.setChecked(false);
            }
        } else {

            Log.d(TAG,"start networkCall1");
            networkCall1(requestParams);
        }


    }

    void networkCall1(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 37); /* Network operation for getting details for three months */

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        /* Add New Slot option for mentor*/
        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 3) {
            if (v == add_slot) {

                Intent intent = new Intent(getActivity(), AddNewSlotActivity.class);
                startActivity(intent);

            }
            if (v == add_vacation) {

                Intent intent = new Intent(getActivity(), ScheduleYourVacation.class);
                startActivity(intent);

            }
        }


        /* Operation on previous month or next month button click */
        if (v == prevMonth) {
            showPrevMonth();

        } else {
            if (v == nextMonth) {
                showNextMonth();

            }
        }

    }

    public void showPrevMonth() {
        newPreviousMonth();

        if (month <= 1) {
            month = 12;
            year--;

        } else {
            month--;
        }

    }

    private void newPreviousMonth() {
        StringBuilder stringBuilder = new StringBuilder();

        int year_for_this = year;
        int month_for_this = month;
        if (month == 1) {

            year_for_this--;
            month_for_this = 11;
        } else {
            if (month == 2) {

                year_for_this--;
                month_for_this = 12;

            } else {

                year_for_this = year;
                month_for_this = month - 2;
            }
        }

        stringBuilder.append(year_for_this);
        stringBuilder.append("/" + month_for_this);
        stringBuilder.append("/" + 1);


        Calendar calendar = new GregorianCalendar(year_for_this, (month_for_this - 1), 1);
        days_in_new_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (DashboardActivity.dashboardActivity.user_group == 3) {
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", String.valueOf("3"));
            requestParams.add("mentor_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            requestParams.add("limit", String.valueOf(days_in_new_prev_month));
            if (cb_calendar_by_location_is_checked) {
                if (calendar_by_location != null) {
                    Log.d(TAG, "Calendar_by_location getting passed to server : " + calendar_by_location);
                    requestParams.add("location", calendar_by_location);
                    networkCall2(requestParams);
                } else {
                    Toast.makeText(getActivity(), "Please provide location to access calendar details", Toast.LENGTH_SHORT).show();
                    getLocationFromDialog();  /* start LocationFromDialog to get the location */
                }
            } else {

                networkCall2(requestParams);
            }

        }
        if (DashboardActivity.dashboardActivity.user_group == 2) {
            Log.d(TAG, "calling getMenteeCalendarDetails api for prev month");
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", String.valueOf("2"));
            requestParams.add("student_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            requestParams.add("limit", String.valueOf(days_in_new_prev_month));
            progressDialog.show();
            NetworkClient.getMenteeCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 42);
        }


    }

    /* Network call for getting previous to previous month data in case of mentor login*/
    void networkCall2(RequestParams requestParams){
        progressDialog.show();
        NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 39);
    }


    public void showNextMonth() {
        newNextMonth();
        if (month > 11) {
            month = 1;
            year++;
        } else {
            month++;
        }

    }

    private void newNextMonth() {
        StringBuilder stringBuilder = new StringBuilder();

        int year_for_this = year;
        int month_for_this = month;
        if (month == 12) {
            year_for_this++;
            month_for_this = 2;
        } else {
            if (month == 11) {
                year_for_this++;
                month_for_this = 1;

            } else {
                year_for_this = year;
                month_for_this = month + 2;
            }
        }

        stringBuilder.append(year_for_this);
        stringBuilder.append("/" + month_for_this);
        stringBuilder.append("/" + 1);


        Calendar calendar = new GregorianCalendar(year_for_this, (month_for_this - 1), 1);
        days_in_new_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 3) {
            Log.d(TAG, "calling getMenteeCalendarDetails api for next month user_Group 3");
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", String.valueOf("3"));
            requestParams.add("mentor_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            requestParams.add("limit", String.valueOf(days_in_new_next_month));
            Log.i(TAG, "user_group : " + String.valueOf("3") + " mentor id : " + StorageHelper.getUserDetails(getActivity(), "user_id") + " start date : " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_new_next_month));
            if (cb_calendar_by_location_is_checked) {
                if (calendar_by_location != null) {
                    Log.d(TAG, "Calendar_by_location getting passed to server : " + calendar_by_location);
                    requestParams.add("location", calendar_by_location);
                    networkCall3(requestParams);
                } else {
                    Toast.makeText(getActivity(), "Please provide location to access calendar details", Toast.LENGTH_SHORT).show();
                    getLocationFromDialog();  /* start LocationFromDialog to get the location */
                }
            } else {

                networkCall3(requestParams);
            }


        }
        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 2) {
            Log.d(TAG, "calling getMenteeCalendarDetails api for next month user_Group 2");
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", String.valueOf("2"));
            requestParams.add("student_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            requestParams.add("limit", String.valueOf(days_in_new_next_month));
            progressDialog.show();
            NetworkClient.getMenteeCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 41);
        }


    }

    /* Network call for getting next to next month data in case of mentor login*/
    void networkCall3(RequestParams requestParams){
        progressDialog.show();
        NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 38);
    }



    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        switch (calledApiValue) {
            case 37:
                threeMonthsData(object);
                break;
            case 38:
                nextMonthData(object);
                break;
            case 39:
                previousMonthData(object);
                break;
            case 40:
                threeMonthsData(object);
                break;
            case 41:
                nextMonthData(object);
                break;
            case 42:
                previousMonthData(object);
                break;
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }


    private void threeMonthsData(Object object) {

        Log.d(TAG,"inside three months data population");


        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 3) {




            Log.d(TAG, "INside threeMonthData method for user_group 3");
            progressDialog.dismiss();
            try {

                JSONObject jsonObject = new JSONObject((String) object);
                JSONArray jsonArray_data = jsonObject.getJSONArray("data");

                for (int i = 0; i < days_in_prev_month; i++) {
                    Day day1 = new Day();
                    JSONObject unique_day = jsonArray_data.getJSONObject(i);
                    day1.setDate(unique_day.getString("date"));
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("object");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();
                    JSONArray jsonArray_of_slots = unique_day.getJSONArray("slots");

                    List<DaySlot> daySlots = new ArrayList<DaySlot>();
                    if (jsonArray_of_slots.length() > 0) {
                        for (int s = 0; s < jsonArray_of_slots.length(); s++) {
                            JSONObject day_slot = jsonArray_of_slots.getJSONObject(s);
                            DaySlot daySlot = new DaySlot();
                            daySlot.setSlot_start_time(day_slot.getString("start_time"));
                            daySlot.setSlot_stop_time(day_slot.getString("stop_time"));
                            daySlots.add(daySlot);
                        }
                        day1.setDaySlots(daySlots);
                    } else {
                        day1.setDaySlots(daySlots);
                    }

                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();
                            dayEvent.setEvent_id(day_event.getString("id"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    previousMonthArrayList.add(day1);
                }


                for (int i = days_in_prev_month; i < days_in_prev_month + days_in_current_month; i++) {
                    Day day1 = new Day();
                    JSONObject unique_day = jsonArray_data.getJSONObject(i);
                    day1.setDate(unique_day.getString("date"));
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("object");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();
                    JSONArray jsonArray_of_slots = unique_day.getJSONArray("slots");

                    List<DaySlot> daySlots = new ArrayList<DaySlot>();
                    if (jsonArray_of_slots.length() > 0) {
                        for (int s = 0; s < jsonArray_of_slots.length(); s++) {
                            JSONObject day_slot = jsonArray_of_slots.getJSONObject(s);
                            DaySlot daySlot = new DaySlot();
                            daySlot.setSlot_start_time(day_slot.getString("start_time"));
                            daySlot.setSlot_stop_time(day_slot.getString("stop_time"));
                            daySlots.add(daySlot);
                        }
                        day1.setDaySlots(daySlots);
                    } else {
                        day1.setDaySlots(daySlots);
                    }

                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();

                            dayEvent.setEvent_id(day_event.getString("id"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    currentMonthArrayList.add(day1);
                }


                for (Day d : currentMonthArrayList) {

                    for (DayEvent de : d.dayEvents) {
                        Log.v(TAG, de.getEvent_id() + " : s_time : " + de.getEvent_start_time() + " : L_time : " + de.getEvent_stop_time());
                    }
                }

                for (int i = days_in_prev_month + days_in_current_month; i < days_in_prev_month + days_in_current_month + days_in_next_month; i++) {
                    Day day1 = new Day();
                    JSONObject unique_day = jsonArray_data.getJSONObject(i);
                    day1.setDate(unique_day.getString("date"));
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("object");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();

                    JSONArray jsonArray_of_slots = unique_day.getJSONArray("slots");
                    List<DaySlot> daySlots = new ArrayList<DaySlot>();
                    if (jsonArray_of_slots.length() > 0) {
                        for (int s = 0; s < jsonArray_of_slots.length(); s++) {
                            JSONObject day_slot = jsonArray_of_slots.getJSONObject(s);
                            DaySlot daySlot = new DaySlot();
                            daySlot.setSlot_start_time(day_slot.getString("start_time"));
                            daySlot.setSlot_stop_time(day_slot.getString("stop_time"));
                            daySlots.add(daySlot);
                        }
                        day1.setDaySlots(daySlots);
                    } else {
                        day1.setDaySlots(daySlots);
                    }

                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();

                            dayEvent.setEvent_id(day_event.getString("id"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    comingMonthArrayList.add(day1);
                }


                Log.d(TAG, "previousMonthArrayList size :" + previousMonthArrayList.size() + "currentMonthArrayList size :" + currentMonthArrayList.size() + ", comingMonthArrayList size :" + comingMonthArrayList.size());
                if(b_three_months_data){
                    Log.d(TAG,"Three months data get changed");
                    adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                    calendarView.setAdapter(adapter1);
                    adapter1.notifyDataSetChanged();
                    currentMonth.setText(DateFormat.format(dateTemplate,_calendar.getTime()));
                }else{
                    adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                    calendarView.setAdapter(adapter1);
                    adapter1.notifyDataSetChanged();
                }




                b_three_months_data=true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 2) {
            progressDialog.dismiss();
            Log.d(TAG, "INside threeMonthData method for user_group 2");
            try {

                JSONObject jsonObject = new JSONObject((String) object);
                JSONArray jsonArray_data = jsonObject.getJSONArray("data");

                for (int i = 0; i < days_in_prev_month; i++) {
                    Day day1 = new Day();
                    JSONObject unique_day = jsonArray_data.getJSONObject(i);
                    day1.setDate(unique_day.getString("date"));
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("event");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();

                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();

                            dayEvent.setEvent_id(day_event.getString("id"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    previousMonthArrayList.add(day1);

                }


                for (int i = days_in_prev_month; i < days_in_prev_month + days_in_current_month; i++) {
                    Day day1 = new Day();
                    JSONObject unique_day = jsonArray_data.getJSONObject(i);
                    day1.setDate(unique_day.getString("date"));
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("event");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();


                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();

                            dayEvent.setEvent_id(day_event.getString("id"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    currentMonthArrayList.add(day1);
                }

                for (int i = days_in_prev_month + days_in_current_month; i < days_in_prev_month + days_in_current_month + days_in_next_month; i++) {
                    Day day1 = new Day();
                    JSONObject unique_day = jsonArray_data.getJSONObject(i);
                    day1.setDate(unique_day.getString("date"));
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("event");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();


                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();

                            dayEvent.setEvent_id(day_event.getString("id"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    comingMonthArrayList.add(day1);
                }


                Log.d(TAG, "previousMonthArrayList size :" + previousMonthArrayList.size() + "currentMonthArrayList size :" + currentMonthArrayList.size() + ", comingMonthArrayList size :" + comingMonthArrayList.size());


                adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                adapter1.notifyDataSetChanged();
                calendarView.setAdapter(adapter1);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void nextMonthData(Object object) {

        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 3) {

            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject((String) object);
                JSONArray jsonArray_data = jsonObject.getJSONArray("data");


                previousMonthArrayList = currentMonthArrayList;
                currentMonthArrayList = comingMonthArrayList;
                comingMonthArrayList = null;
                comingMonthArrayList = new ArrayList<Day>();


                for (int i = 0; i < days_in_new_next_month; i++) {
                    Day day1 = new Day();
                    JSONObject unique_day = jsonArray_data.getJSONObject(i);
                    day1.setDate(unique_day.getString("date"));
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("object");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();

                    JSONArray jsonArray_of_slots = unique_day.getJSONArray("slots");
                    List<DaySlot> daySlots = new ArrayList<DaySlot>();
                    if (jsonArray_of_slots.length() > 0) {
                        for (int s = 0; s < jsonArray_of_slots.length(); s++) {
                            JSONObject day_slot = jsonArray_of_slots.getJSONObject(s);
                            DaySlot daySlot = new DaySlot();
                            daySlot.setSlot_start_time(day_slot.getString("start_time"));
                            daySlot.setSlot_stop_time(day_slot.getString("stop_time"));
                            daySlots.add(daySlot);
                        }
                        day1.setDaySlots(daySlots);
                    } else {
                        day1.setDaySlots(daySlots);
                    }

                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();

                            dayEvent.setEvent_id(day_event.getString("id"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    comingMonthArrayList.add(day1);

                }


                Log.d(TAG, "comingMonthArrayList size" + comingMonthArrayList.size());
                for (Day day1 : comingMonthArrayList) {
                    Log.d(TAG, "date from new comingMonthArrayList" + day1.getDate());
                }

                adapter2 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
                currentMonth.setText(DateFormat.format(dateTemplate,
                        _calendar.getTime()));
                adapter2.notifyDataSetChanged();
                calendarView.setAdapter(adapter2);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 2) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject((String) object);
                JSONArray jsonArray_data = jsonObject.getJSONArray("data");


                previousMonthArrayList = currentMonthArrayList;
                currentMonthArrayList = comingMonthArrayList;
                comingMonthArrayList = null;
                comingMonthArrayList = new ArrayList<Day>();


                for (int i = 0; i < days_in_new_next_month; i++) {
                    Day day1 = new Day();
                    JSONObject unique_day = jsonArray_data.getJSONObject(i);
                    day1.setDate(unique_day.getString("date"));
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("event");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();


                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();

                            dayEvent.setEvent_id(day_event.getString("id"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    comingMonthArrayList.add(day1);

                }


                Log.d(TAG, "comingMonthArrayList size" + comingMonthArrayList.size());
                for (Day day1 : comingMonthArrayList) {
                    Log.d(TAG, "date from new comingMonthArrayList" + day1.getDate());
                }

                adapter2 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
                currentMonth.setText(DateFormat.format(dateTemplate,
                        _calendar.getTime()));
                adapter2.notifyDataSetChanged();
                calendarView.setAdapter(adapter2);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void previousMonthData(Object object) {

        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 3) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject((String) object);
                JSONArray jsonArray_data = jsonObject.getJSONArray("data");

                comingMonthArrayList = currentMonthArrayList;
                currentMonthArrayList = previousMonthArrayList;
                previousMonthArrayList = null;
                previousMonthArrayList = new ArrayList<Day>();


                for (int i = 0; i < days_in_new_prev_month; i++) {
                    Day day1 = new Day();
                    JSONObject unique_day = jsonArray_data.getJSONObject(i);
                    day1.setDate(unique_day.getString("date"));
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("object");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();

                    JSONArray jsonArray_of_slots = unique_day.getJSONArray("slots");
                    List<DaySlot> daySlots = new ArrayList<DaySlot>();
                    if (jsonArray_of_slots.length() > 0) {
                        for (int s = 0; s < jsonArray_of_slots.length(); s++) {
                            JSONObject day_slot = jsonArray_of_slots.getJSONObject(s);
                            DaySlot daySlot = new DaySlot();
                            daySlot.setSlot_start_time(day_slot.getString("start_time"));
                            daySlot.setSlot_stop_time(day_slot.getString("stop_time"));
                            daySlots.add(daySlot);
                        }
                        day1.setDaySlots(daySlots);
                    } else {
                        day1.setDaySlots(daySlots);
                    }

                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();

                            dayEvent.setEvent_id(day_event.getString("id"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    previousMonthArrayList.add(day1);

                }
                adapter3= new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
                currentMonth.setText(DateFormat.format(dateTemplate,
                        _calendar.getTime()));
                adapter3.notifyDataSetChanged();
                calendarView.setAdapter(adapter3);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 2) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject((String) object);
                JSONArray jsonArray_data = jsonObject.getJSONArray("data");

                comingMonthArrayList = currentMonthArrayList;
                currentMonthArrayList = previousMonthArrayList;
                previousMonthArrayList = null;
                previousMonthArrayList = new ArrayList<Day>();


                for (int i = 0; i < days_in_new_prev_month; i++) {
                    Day day1 = new Day();
                    JSONObject unique_day = jsonArray_data.getJSONObject(i);
                    day1.setDate(unique_day.getString("date"));
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("event");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();


                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();

                            dayEvent.setEvent_id(day_event.getString("id"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    previousMonthArrayList.add(day1);

                }
                adapter3= new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);

                _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
                currentMonth.setText(DateFormat.format(dateTemplate,
                        _calendar.getTime()));
                adapter3.notifyDataSetChanged();
                calendarView.setAdapter(adapter3);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
