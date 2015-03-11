package com.findmycoach.app.fragment;

import android.annotation.TargetApi;
import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.ScheduleYourVacation;
import com.findmycoach.app.adapter.CalendarGridAdapter;
import com.findmycoach.app.R;
import com.findmycoach.app.beans.CalendarSchedule.Day;
import com.findmycoach.app.beans.CalendarSchedule.DayEvent;
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
    private ImageView prevMonth;
    private ImageView nextMonth;
    private GridView calendarView;
    private CalendarGridAdapter adapter;
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

        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);


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

    private void initializeMenteeView(View view) {
        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (GridView) view.findViewById(R.id.calendar);

        // Initialised
        /*arrayList_of_3_months = new ArrayList<ArrayList>();
        arrayList_of_3_months.add(previousMonthArrayList);
        arrayList_of_3_months.add(currentMonthArrayList);
        arrayList_of_3_months.add(comingMonthArrayList);
*/

        previousMonthArrayList = new ArrayList<Day>();
        currentMonthArrayList = new ArrayList<Day>();
        comingMonthArrayList = new ArrayList<Day>();


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");

        RequestParams requestParams = new RequestParams();
        requestParams.add("user_group",String.valueOf(2));
        requestParams.add("mentee_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        StringBuilder stringBuilder = new StringBuilder();


        if (month == 1) {
            Calendar calendar = new GregorianCalendar(year - 1, 11, 1);
            stringBuilder.append((year - 1));
            stringBuilder.append("/" + 12);
            stringBuilder.append("/" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            //prev_json = getDemoCalendarDetails(days_in_prev_month, 12, year - 1);
            //calendarEventScheduler(prev_json);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) - 1, 1);
            stringBuilder.append(year);
            stringBuilder.append("/" + (month - 1));
            stringBuilder.append("/" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            //prev_json = getDemoCalendarDetails(days_in_prev_month, month - 1, year - 1);
            //calendarEventScheduler(prev_json);
        }


        if (month == 12) {
            Calendar calendar = new GregorianCalendar(year + 1, 0, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            //next_json = getDemoCalendarDetails(days_in_next_month, 1, year + 1);
            //calendarEventScheduler(next_json);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) + 1, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            //next_json = getDemoCalendarDetails(days_in_next_month, month + 1, year);
            //calendarEventScheduler(next_json);
        }

        days_in_current_month = new GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
        //current_json = getDemoCalendarDetails(days_in_current_month, month, year);
        //calendarEventScheduler(current_json);

        Toast.makeText(getActivity(), "Start date" + String.valueOf(stringBuilder), Toast.LENGTH_SHORT).show();
        requestParams.add("start_date", String.valueOf(stringBuilder));
        requestParams.add("limit", String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));
        progressDialog.show();
        NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 37);
    }

    private void applyListeners() {


    }

    private void initialize(View view) {


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

        // Initialised
        /*arrayList_of_3_months = new ArrayList<ArrayList>();
        arrayList_of_3_months.add(previousMonthArrayList);
        arrayList_of_3_months.add(currentMonthArrayList);
        arrayList_of_3_months.add(comingMonthArrayList);
*/

        previousMonthArrayList = new ArrayList<Day>();
        currentMonthArrayList = new ArrayList<Day>();
        comingMonthArrayList = new ArrayList<Day>();


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");

        RequestParams requestParams = new RequestParams();
        requestParams.add("user_group",String.valueOf("3"));
        requestParams.add("mentor_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        StringBuilder stringBuilder = new StringBuilder();


        if (month == 1) {
            Calendar calendar = new GregorianCalendar(year - 1, 11, 1);
            stringBuilder.append((year - 1));
            stringBuilder.append("/" + 12);
            stringBuilder.append("/" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            //prev_json = getDemoCalendarDetails(days_in_prev_month, 12, year - 1);
            //calendarEventScheduler(prev_json);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) - 1, 1);
            stringBuilder.append(year);
            stringBuilder.append("/" + (month - 1));
            stringBuilder.append("/" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            //prev_json = getDemoCalendarDetails(days_in_prev_month, month - 1, year - 1);
            //calendarEventScheduler(prev_json);
        }


        if (month == 12) {
            Calendar calendar = new GregorianCalendar(year + 1, 0, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            //next_json = getDemoCalendarDetails(days_in_next_month, 1, year + 1);
            //calendarEventScheduler(next_json);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) + 1, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            //next_json = getDemoCalendarDetails(days_in_next_month, month + 1, year);
            //calendarEventScheduler(next_json);
        }

        days_in_current_month = new GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
        //current_json = getDemoCalendarDetails(days_in_current_month, month, year);
        //calendarEventScheduler(current_json);

        Toast.makeText(getActivity(), "Start date" + String.valueOf(stringBuilder), Toast.LENGTH_SHORT).show();
        requestParams.add("start_date", String.valueOf(stringBuilder));
        requestParams.add("limit", String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));
        progressDialog.show();
        NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 37);
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

        if(Integer.parseInt(StorageHelper.getUserGroup(getActivity(),"user_group")) == 3){
            if (v == add_slot) {

                Intent intent = new Intent(getActivity(), AddNewSlotActivity.class);
                startActivity(intent);

            }
            if (v == add_vacation) {

                Intent intent = new Intent(getActivity(), ScheduleYourVacation.class);
                startActivity(intent);

            }
        }



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


        //    setGridCellAdapterToDate(0, month, year);
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

        if(Integer.parseInt(StorageHelper.getUserGroup(getActivity(),"user_group")) == 3){
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group",String.valueOf("3"));
            requestParams.add("mentor_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            requestParams.add("limit", String.valueOf(days_in_new_prev_month));
            progressDialog.show();
            NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 39);
        }
        if(Integer.parseInt(StorageHelper.getUserGroup(getActivity(),"user_group")) == 2){
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group",String.valueOf("2"));
            requestParams.add("mentee_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            requestParams.add("limit", String.valueOf(days_in_new_prev_month));
            progressDialog.show();
            NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 39);
        }


    }


    public void showNextMonth() {
        newNextMonth();

        Log.d(TAG, "initial month: " + month + ", year: " + year);

        if (month > 11) {
            month = 1;
            year++;
        } else {
            month++;
        }


        //   setGridCellAdapterToDate(1, month, year);
    }

    private void newNextMonth() {
        StringBuilder stringBuilder = new StringBuilder();

        int year_for_this = year;
        int month_for_this = month;
        if (month == 12) {
            Log.d(TAG, "inside month equals 1");
            year_for_this++;
            month_for_this = 2;
        } else {
            if (month == 11) {
                Log.d(TAG, "inside month equals 2");
                year_for_this++;
                month_for_this = 1;

            } else {
                Log.d(TAG, "inside month not equals 1 and 2");
                year_for_this = year;
                month_for_this = month + 2;
            }
        }

        stringBuilder.append(year_for_this);
        stringBuilder.append("/" + month_for_this);
        stringBuilder.append("/" + 1);


        Calendar calendar = new GregorianCalendar(year_for_this, (month_for_this - 1), 1);
        days_in_new_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if(Integer.parseInt(StorageHelper.getUserGroup(getActivity(),"user_group")) == 3) {
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", String.valueOf("3"));
            requestParams.add("mentor_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            requestParams.add("limit", String.valueOf(days_in_new_next_month));
            progressDialog.show();
            NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 38);
        }
        if(Integer.parseInt(StorageHelper.getUserGroup(getActivity(),"user_group")) == 2) {
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", String.valueOf("2"));
            requestParams.add("mentee_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            requestParams.add("limit", String.valueOf(days_in_new_next_month));
            progressDialog.show();
            NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 38);
        }


    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        switch (calledApiValue){
            case 37:
                threeMonthsData(object);
                break;
            case 38:
                nextMonthData(object);
                break;
            case 39:
                previousMonthData(object);
                break;
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        Toast.makeText(getActivity(),(String) object,Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }


    private void threeMonthsData(Object object){
        progressDialog.dismiss();

            if(Integer.parseInt(StorageHelper.getUserGroup(getActivity(),"user_group")) == 3){
                try {

                    JSONObject jsonObject = new JSONObject((String) object);
                JSONArray jsonArray_data = jsonObject.getJSONArray("data");

                for (int i = 0; i < days_in_prev_month; i++) {
                    Day day1 = new Day();
                    JSONObject unique_day = jsonArray_data.getJSONObject(i);
                    day1.setDate(unique_day.getString("date"));
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("object");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();
                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();
                            dayEvent.setEvent_start_date(day_event.getString("start_date"));
                            dayEvent.setEvent_stop_date(day_event.getString("stop_date"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
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
                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();
                            dayEvent.setEvent_start_date(day_event.getString("start_date"));
                            dayEvent.setEvent_stop_date(day_event.getString("stop_date"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
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
                    JSONArray jsonArray_of_events = unique_day.getJSONArray("object");
                    List<DayEvent> dayEvents = new ArrayList<DayEvent>();
                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();
                            dayEvent.setEvent_start_date(day_event.getString("start_date"));
                            dayEvent.setEvent_stop_date(day_event.getString("stop_date"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    comingMonthArrayList.add(day1);
                }


                Log.d(TAG, "previousMonthArrayList size :" + previousMonthArrayList.size() + "currentMonthArrayList size :" + currentMonthArrayList.size() + ", comingMonthArrayList size :" + comingMonthArrayList.size());


                adapter = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                adapter.notifyDataSetChanged();
                calendarView.setAdapter(adapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
            if(Integer.parseInt(StorageHelper.getUserGroup(getActivity(),"user_group")) == 2){

            }


    }

    private void nextMonthData(Object object){

        if(Integer.parseInt(StorageHelper.getUserGroup(getActivity(),"user_group")) == 3){
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject((String) object);
                JSONArray jsonArray_data = jsonObject.getJSONArray("data");
/*

                    comingMonthArrayList=currentMonthArrayList;
                    currentMonthArrayList=previousMonthArrayList;
                    previousMonthArrayList=null;
                    previousMonthArrayList = new ArrayList<Day>();
*/


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
                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();
                            dayEvent.setEvent_start_date(day_event.getString("start_date"));
                            dayEvent.setEvent_stop_date(day_event.getString("stop_date"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
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

                adapter = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
                currentMonth.setText(DateFormat.format(dateTemplate,
                        _calendar.getTime()));
                adapter.notifyDataSetChanged();
                calendarView.setAdapter(adapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        if(Integer.parseInt(StorageHelper.getUserGroup(getActivity(),"user_group")) == 2){
            progressDialog.dismiss();
        }

    }

    private void previousMonthData(Object object){
        progressDialog.dismiss();
        if(Integer.parseInt(StorageHelper.getUserGroup(getActivity(),"user_group")) == 3){
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
                    if (jsonArray_of_events.length() > 0) {
                        for (int e = 0; e < jsonArray_of_events.length(); e++) {

                            JSONObject day_event = jsonArray_of_events.getJSONObject(e);
                            DayEvent dayEvent = new DayEvent();
                            dayEvent.setEvent_start_date(day_event.getString("start_date"));
                            dayEvent.setEvent_stop_date(day_event.getString("stop_date"));
                            dayEvent.setEvent_start_time(day_event.getString("start_time"));
                            dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    previousMonthArrayList.add(day1);

                }
                adapter = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
                currentMonth.setText(DateFormat.format(dateTemplate,
                        _calendar.getTime()));
                adapter.notifyDataSetChanged();
                calendarView.setAdapter(adapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(Integer.parseInt(StorageHelper.getUserGroup(getActivity(),"user_group")) == 2){

        }
    }

}
