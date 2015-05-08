package com.findmycoach.app.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
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
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.findmycoach.app.util.NetworkManager;
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
    public TextView tv_location_for_calendar;
    public LinearLayout ll_location_for_calendar;
    public CheckBox cb_calendar_by_location;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private GridView calendarView;
    private CalendarGridAdapter adapter1;
    private Calendar _calendar;
    protected static int month, year;
    private static final String dateTemplate = "MMMM yyyy";
    protected static MyScheduleFragment myScheduleFragment;
    private int days_in_current_month, days_in_prev_month, days_in_next_month;
    private static final String TAG = "FMC";
    ProgressDialog progressDialog;
    private int days_in_new_prev_month, days_in_new_next_month;
    public ArrayList<Day> previousMonthArrayList = null;
    public ArrayList<Day> currentMonthArrayList = null;
    public ArrayList<Day> comingMonthArrayList = null;
    public String calendar_by_location = null;
    public boolean cb_calendar_by_location_is_checked = false, b_three_months_data;
    private int NEW_SLT = 0, VAC_SCH = 1, RESULT_OK = 500;
    protected static int month_from_dialog, year_from_dialog;


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
        month_from_dialog = 0;
        year_from_dialog = 0;
        startPointForCalendar();
        b_three_months_data = false;
    }

    /* Get Calendar current instance*/
    void startPointForCalendar() {
        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;   /* current month*/
        year = _calendar.get(Calendar.YEAR);         /* current year */
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
        currentMonth.setOnClickListener(this);

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (GridView) view.findViewById(R.id.calendar);

        getCalendarDetailsForMentee();

    }


    public void getCalendarDetailsForMentee() {
        /* Array list of 3 months previous, current and coming , These points Day class object for day details like class schedule*/
        previousMonthArrayList = new ArrayList<Day>();
        currentMonthArrayList = new ArrayList<Day>();
        comingMonthArrayList = new ArrayList<Day>();

        if (month_from_dialog == 0 && year_from_dialog == 0) {
            startPointForCalendar();
        } else {
            month = month_from_dialog;
            year = year_from_dialog;
            currentMonth.setText(getResources().getStringArray(R.array.months)[month - 1] + " " + year);
        }


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

        requestParams.add("start_date", String.valueOf(stringBuilder));
        requestParams.add("limit", String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));
        networkCallForMentee(requestParams);
    }

    void networkCallForMentee(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.getMenteeCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 40);  /*Network operation for getting details for three months */
    }

    private void applyListeners() {


    }

    /* Initializing views for Mentor login */
    public void initialize(final View view) {

        tv_location_for_calendar = (TextView) view.findViewById(R.id.tv_location_for_calendar);
        tv_location_for_calendar.setOnClickListener(this);

        ll_location_for_calendar = (LinearLayout) view.findViewById(R.id.ll_location_for_calendar);
        ll_location_for_calendar.setVisibility(View.GONE);

        add_slot = (TextView) view.findViewById(R.id.tv_add_new_slot);
        add_slot.setOnClickListener(this);

        add_vacation = (TextView) view.findViewById(R.id.tv_add_vacation);
        add_vacation.setOnClickListener(this);

        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
        currentMonth.setOnClickListener(this);

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
                    cb_calendar_by_location_is_checked = true;
                    calendar_by_location = null;

                    getLocationFromDialog();
                } else {
                    cb_calendar_by_location_is_checked = false;

                    if (calendar_by_location != null && calendar_by_location.trim().length() > 0) {
                        Log.d(TAG, "calendar_by_location string size :" + calendar_by_location.trim().length());
                        ll_location_for_calendar.setVisibility(View.GONE);
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


        if (month_from_dialog == 0 && year_from_dialog == 0) {
            startPointForCalendar();
        } else {
            month = month_from_dialog;
            year = year_from_dialog;
            currentMonth.setText(getResources().getStringArray(R.array.months)[month - 1] + " " + year);
        }


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


        requestParams.add("start_date", String.valueOf(stringBuilder));
        requestParams.add("limit", String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));


        if (cb_calendar_by_location_is_checked) {


            Log.d(TAG, "calendar_by_location is checked true");
            if (calendar_by_location != null && !calendar_by_location.trim().equals("")) {
                Log.d(TAG, "Calendar_by_location getting passed to server : " + calendar_by_location);
                requestParams.add("location", calendar_by_location);
                Log.d(TAG, " Data getting requested for three months : " + "start date : " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month) + " location : " + calendar_by_location);
                networkCall1(requestParams);
            } else {

//                getLocationFromDialog();  /* start LocationFromDialog to get the location */
                cb_calendar_by_location.setChecked(false);
            }
        } else {


            Log.d(TAG, " Data getting requested for three months : " + "start date : " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));
            Log.d(TAG, "start networkCall1");
            networkCall1(requestParams);
        }


    }

    void networkCall1(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 37); /* Network operation for getting details for three months */
        Log.d(TAG,"FMC auth token :"+StorageHelper.getUserDetails(getActivity(), "auth_token"));
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
    public void onClick(View v) {
        /* Add New Slot option for mentor*/
        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 3) {
            if (v == add_slot) {

                Intent intent = new Intent(getActivity(), AddNewSlotActivity.class);
                startActivityForResult(intent, NEW_SLT);

            }
            if (v == add_vacation) {

                Intent intent = new Intent(getActivity(), ScheduleYourVacation.class);
                startActivityForResult(intent, VAC_SCH);


            }
            if (v == currentMonth) {
                FragmentManager fragmentManager = getFragmentManager();
                CustomDatePickerFragment customDatePickerFragment = new CustomDatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("for", "MyScheduleFragment");
                customDatePickerFragment.setArguments(bundle);
                customDatePickerFragment.show(fragmentManager, null);
                month_from_dialog = 0;
                year_from_dialog = 0;
              /*DatePickerDialog datePickerDialog=createDialogWithoutDateField();
                datePickerDialog.show();
*/
            }

            if (v == tv_location_for_calendar) {
                getLocationFromDialog();
            }

        }
        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 2) {
            if (v == currentMonth) {
                Log.d(TAG, "currentMonth clicked");
                FragmentManager fragmentManager = getFragmentManager();
                CustomDatePickerFragment customDatePickerFragment = new CustomDatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("for", "MyScheduleFragment");
                customDatePickerFragment.setArguments(bundle);

                customDatePickerFragment.show(fragmentManager, null);
                month_from_dialog = 0;
                year_from_dialog = 0;
            }
        }




        /* Operation on previous month or next month button click */
        if (v == prevMonth) {
            Log.d(TAG, "previous month clicked");
            showPrevMonth();

        } else {
            Log.d(TAG, "previous month not clicked");

            if (v == nextMonth) {
                showNextMonth();

            }


        }

    }


    private DatePickerDialog createDialogWithoutDateField() {

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), null, 2014, 1, 24);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    datePicker.setSpinnersShown(true);
                    datePicker.setCalendarViewShown(false);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }

            }
        } catch (Exception ex) {
        }
        return dpd;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_SLT && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult call ");
            getCalendarDetailsAPICall();
        }

        //super.onActivityResult(requestCode, resultCode, data);

    }

    public void showPrevMonth() {
        newPreviousMonth();
        Log.d(TAG, "after newPreviousMonth call");


    }

    public void updateMonthAndYearOnPreviousMonthClick() {
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
                    Log.d(TAG, " Request for calendar details for prev month " + "start _date: " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_new_prev_month) + " calendar by location : " + calendar_by_location);
                    networkCall2(requestParams);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.please_provide_location_to_access_details), Toast.LENGTH_SHORT).show();
                    getLocationFromDialog();  /* start LocationFromDialog to get the location */
                }
            } else {
                Log.d(TAG, " Request for calendar details for prev month " + "start _date: " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_new_prev_month));
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
    void networkCall2(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 39);
    }


    public void showNextMonth() {
        newNextMonth();


    }

    public void updateMonthAndYearOnNextMonthClick() {
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
                    Log.d(TAG, " Request for calendar details for prev month " + "start _date: " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_new_next_month) + " location : " + calendar_by_location);
                    networkCall3(requestParams);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.please_provide_location_to_access_details), Toast.LENGTH_SHORT).show();
                    getLocationFromDialog();  /* start LocationFromDialog to get the location */
                }
            } else {
                Log.d(TAG, " Request for calendar details for prev month " + "start _date: " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_new_next_month));
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
    void networkCall3(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 38);
    }


    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        switch (calledApiValue) {
            case 37:
                Log.d(TAG, " API 37 success");
                threeMonthsData(object);
                break;
            case 38:
                Log.d(TAG, " API 38 success");
                updateMonthAndYearOnNextMonthClick();
                nextMonthData(object);
                break;
            case 39:
                Log.d(TAG, " API 39 success");
                updateMonthAndYearOnPreviousMonthClick();
                previousMonthData(object);
                break;
            case 40:
                Log.d(TAG, " API 40 success");
                threeMonthsData(object);
                break;
            case 41:
                Log.d(TAG, " API 41 success");
                updateMonthAndYearOnNextMonthClick();
                nextMonthData(object);
                break;
            case 42:
                Log.d(TAG, " API 42 success");
                updateMonthAndYearOnPreviousMonthClick();
                previousMonthData(object);
                break;
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        Log.d(TAG, "API " + calledApiValue + " failure");
        switch (calledApiValue) {
            case 37:
                Log.d(TAG, " API 37 success");
                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateCalendarOnFailure();
                break;
            case 38:
                Log.d(TAG, " API 38 success");
                updateMonthAndYearOnNextMonthClick();
                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateArrayListsForNextMonth();
                updateCalendarOnFailure();
                break;
            case 39:
                Log.d(TAG, " API 39 success");
                updateMonthAndYearOnPreviousMonthClick();
                updateArrayListsForPreviousMonth();
                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateCalendarOnFailure();
                break;
            case 40:
                Log.d(TAG, " API 40 success");
                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateCalendarOnFailure();
                break;
            case 41:
                Log.d(TAG, " API 41 success");
                updateMonthAndYearOnNextMonthClick();
                updateArrayListsForNextMonth();
                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateCalendarOnFailure();
                break;
            case 42:
                Log.d(TAG, " API 42 success");
                updateMonthAndYearOnPreviousMonthClick();
                updateArrayListsForPreviousMonth();
                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateCalendarOnFailure();
                break;
        }

    }

    public void updateArrayListsForNextMonth(){
        previousMonthArrayList = currentMonthArrayList;
        currentMonthArrayList = comingMonthArrayList;
        comingMonthArrayList = null;
        comingMonthArrayList = new ArrayList<Day>();
    }

    public void updateArrayListsForPreviousMonth(){
        comingMonthArrayList = currentMonthArrayList;
        currentMonthArrayList = previousMonthArrayList;
        previousMonthArrayList = null;
        previousMonthArrayList = new ArrayList<Day>();
    }



    private void updateCalendarOnFailure() {

        adapter1 = new CalendarGridAdapter(getActivity(), month, year, this);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));

        calendarView.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
        progressDialog.dismiss();
    }


    private void threeMonthsData(Object object) {

        Log.d(TAG, "inside three months data population");


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
                            daySlot.setSlot_type(day_slot.getString("slot_type"));
                            daySlot.setSlot_start_date(day_slot.getString("start_date"));
                            daySlot.setSlot_stop_date(day_slot.getString("stop_date"));
                            daySlot.setSlot_start_time(day_slot.getString("start_time"));
                            daySlot.setSlot_stop_time(day_slot.getString("stop_time"));

                            JSONArray week_days_jsonArray = day_slot.getJSONArray("dates");
                            String[] dates = new String[week_days_jsonArray.length()];
                            for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                                dates[week_day] = week_days_jsonArray.getString(week_day);
                            }
                            daySlot.setSlot_week_days(dates);
                            daySlot.setSlot_id(day_slot.getString("id"));
                            daySlot.setSlot_max_users(day_slot.getString("max_users"));
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
                            dayEvent.setEvent_type(day_event.getString("slot_type"));
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
                            daySlot.setSlot_type(day_slot.getString("slot_type"));
                            daySlot.setSlot_start_date(day_slot.getString("start_date"));
                            daySlot.setSlot_stop_date(day_slot.getString("stop_date"));
                            daySlot.setSlot_start_time(day_slot.getString("start_time"));
                            daySlot.setSlot_stop_time(day_slot.getString("stop_time"));

                            JSONArray week_days_jsonArray = day_slot.getJSONArray("dates");
                            String[] dates = new String[week_days_jsonArray.length()];
                            for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                                dates[week_day] = week_days_jsonArray.getString(week_day);
                            }
                            daySlot.setSlot_week_days(dates);
                            daySlot.setSlot_id(day_slot.getString("id"));
                            daySlot.setSlot_max_users(day_slot.getString("max_users"));
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
                            dayEvent.setEvent_type(day_event.getString("slot_type"));
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
                            daySlot.setSlot_type(day_slot.getString("slot_type"));
                            daySlot.setSlot_start_date(day_slot.getString("start_date"));
                            daySlot.setSlot_stop_date(day_slot.getString("stop_date"));
                            daySlot.setSlot_start_time(day_slot.getString("start_time"));
                            daySlot.setSlot_stop_time(day_slot.getString("stop_time"));

                            JSONArray week_days_jsonArray = day_slot.getJSONArray("dates");
                            String[] dates = new String[week_days_jsonArray.length()];
                            for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                                dates[week_day] = week_days_jsonArray.getString(week_day);
                            }
                            daySlot.setSlot_week_days(dates);
                            daySlot.setSlot_id(day_slot.getString("id"));
                            daySlot.setSlot_max_users(day_slot.getString("max_users"));
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
                            dayEvent.setEvent_type(day_event.getString("slot_type"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    comingMonthArrayList.add(day1);
                }


                Log.d(TAG, "previousMonthArrayList size :" + previousMonthArrayList.size() + "currentMonthArrayList size :" + currentMonthArrayList.size() + ", comingMonthArrayList size :" + comingMonthArrayList.size());
                if (b_three_months_data) {
                    Log.d(TAG, "Three months data get changed");
                    adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                    calendarView.setAdapter(adapter1);
                    adapter1.notifyDataSetChanged();
                    if (month_from_dialog == 0 && year_from_dialog == 0) {
                        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
                    }

                } else {
                    adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                    calendarView.setAdapter(adapter1);
                    adapter1.notifyDataSetChanged();
                }


                b_three_months_data = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 2) {
            progressDialog.dismiss();

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
                            dayEvent.setEvent_type(day_event.getString("slot_type"));
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
                            dayEvent.setEvent_type(day_event.getString("slot_type"));
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
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvent.setEvent_type(day_event.getString("slot_type"));
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
                            daySlot.setSlot_type(day_slot.getString("slot_type"));
                            daySlot.setSlot_start_date(day_slot.getString("start_date"));
                            daySlot.setSlot_stop_date(day_slot.getString("stop_date"));
                            daySlot.setSlot_start_time(day_slot.getString("start_time"));
                            daySlot.setSlot_stop_time(day_slot.getString("stop_time"));

                            JSONArray week_days_jsonArray = day_slot.getJSONArray("dates");
                            String[] dates = new String[week_days_jsonArray.length()];
                            for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                                dates[week_day] = week_days_jsonArray.getString(week_day);
                            }
                            daySlot.setSlot_week_days(dates);
                            daySlot.setSlot_id(day_slot.getString("id"));
                            daySlot.setSlot_max_users(day_slot.getString("max_users"));
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
                            dayEvent.setEvent_type(day_event.getString("slot_type"));
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
                    //Log.d(TAG, "date from new comingMonthArrayList" + day1.getDate());
                }

                adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
                currentMonth.setText(DateFormat.format(dateTemplate,
                        _calendar.getTime()));
                adapter1.notifyDataSetChanged();
                calendarView.setAdapter(adapter1);


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
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvent.setEvent_type(day_event.getString("slot_type"));
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
                    //   Log.d(TAG, "date from new comingMonthArrayList" + day1.getDate());
                }

                adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
                currentMonth.setText(DateFormat.format(dateTemplate,
                        _calendar.getTime()));
                adapter1.notifyDataSetChanged();
                calendarView.setAdapter(adapter1);


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
                            daySlot.setSlot_type(day_slot.getString("slot_type"));
                            daySlot.setSlot_start_date(day_slot.getString("start_date"));
                            daySlot.setSlot_stop_date(day_slot.getString("stop_date"));
                            daySlot.setSlot_start_time(day_slot.getString("start_time"));
                            daySlot.setSlot_stop_time(day_slot.getString("stop_time"));

                            JSONArray week_days_jsonArray = day_slot.getJSONArray("dates");
                            String[] dates = new String[week_days_jsonArray.length()];
                            for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                                dates[week_day] = week_days_jsonArray.getString(week_day);
                            }
                            daySlot.setSlot_week_days(dates);
                            daySlot.setSlot_id(day_slot.getString("id"));
                            daySlot.setSlot_max_users(day_slot.getString("max_users"));

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
                            dayEvent.setEvent_type(day_event.getString("slot_type"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    previousMonthArrayList.add(day1);

                }
                adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);
                _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
                currentMonth.setText(DateFormat.format(dateTemplate,
                        _calendar.getTime()));
                adapter1.notifyDataSetChanged();
                calendarView.setAdapter(adapter1);


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
                            dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                            dayEvent.setEvent_type(day_event.getString("slot_type"));
                            dayEvents.add(dayEvent);
                        }
                        day1.setDayEvents(dayEvents);
                    } else {
                        day1.setDayEvents(dayEvents);
                    }
                    previousMonthArrayList.add(day1);

                }
                adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList);

                _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
                currentMonth.setText(DateFormat.format(dateTemplate,
                        _calendar.getTime()));
                adapter1.notifyDataSetChanged();
                calendarView.setAdapter(adapter1);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
