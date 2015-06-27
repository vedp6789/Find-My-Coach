package com.findmycoach.app.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.ScheduleYourVacation;
import com.findmycoach.app.adapter.CalendarGridAdapter;
import com.findmycoach.app.beans.CalendarSchedule.Event;
import com.findmycoach.app.beans.CalendarSchedule.EventDuration;
import com.findmycoach.app.beans.CalendarSchedule.Mentee;
import com.findmycoach.app.beans.CalendarSchedule.MentorInfo;
import com.findmycoach.app.beans.CalendarSchedule.MonthYearInfo;
import com.findmycoach.app.beans.CalendarSchedule.Slot;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;
import com.findmycoach.app.fragment_mentor.LocationForSchedule;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.ScrollableGridView;
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

    private TextView currentMonth, tv_today;
    private Button add_slot, add_vacation;
    public TextView tv_location_for_calendar;
    public LinearLayout ll_location_for_calendar;
    public RadioButton cb_calendar_by_location;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private ScrollableGridView calendarView;
    private CalendarGridAdapter adapter1;
    private Calendar _calendar;
    public static int month, year;
    private static final String dateTemplate = "MMMM yyyy";
    public static MyScheduleFragment myScheduleFragment;
    private int days_in_current_month, days_in_prev_month, days_in_next_month;
    private static final String TAG = "FMC";
    ProgressDialog progressDialog;
    private int days_in_new_prev_month, days_in_new_next_month;

    public ArrayList<Slot> previousMonthArrayList = null;
    public ArrayList<Slot> currentMonthArrayList = null;
    public ArrayList<Slot> comingMonthArrayList = null;
    public ArrayList<Vacation> previousMonthNonCoincidingVacation = null;
    public ArrayList<Vacation> currentMonthNonCoincidingVacation = null;
    public ArrayList<Vacation> comingMonthNonCoincidingVacation = null;
    public ArrayList<MonthYearInfo> previousMonthYearInfo = null;
    public ArrayList<MonthYearInfo> currentMonthYearInfo = null;
    public ArrayList<MonthYearInfo> comingMonthYearInfo = null;
    public ArrayList<MentorInfo> previousMonthMentorInfos = null;
    public ArrayList<MentorInfo> currentMonthMentorInfos = null;
    public ArrayList<MentorInfo> comingMonthMentorInfos = null;

    public String calendar_by_location = null;
    public boolean cb_calendar_by_location_is_checked = false, b_three_months_data;
    private int NEW_SLT = 0, VAC_SCH = 1, RESULT_OK = 500;
    protected static int month_from_dialog, year_from_dialog; /* this is getting initialized from CustomDatePicker fragment when user wants to jump on an specific date*/
    public boolean populate_calendar_from_adapter;   /* this is getting true when currentMonthArrayList or currentMonthNonCoincidingVacation having flag network communication false, Getting initialized from CalendarGridAdatpter */
    /* populate calendar from adapter is also getting changed from SetScheduleActivity when deletion of slot or vacation occurs*/
    private String previous_month_start_date;/* this will get initialized when api is requested for three months (previous, current, coming)*/
    private String next_month_requested_date;
    private String prev_month_requested_date;
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int class_type_from_pref;  /* this is getting initialised from shared preference for class type*/


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
        populate_calendar_from_adapter = false;
    }



    /* Get Calendar current instance*/
    void startPointForCalendar() {
        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;   /* current month*/
        year = _calendar.get(Calendar.YEAR);         /* current year */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myScheduleFragment = null;
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
        tv_today = (TextView) view.findViewById(R.id.tv_today);
        tv_today.setOnClickListener(this);

        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
        currentMonth.setOnClickListener(this);

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (ScrollableGridView) view.findViewById(R.id.calendar);

        getCalendarDetailsForMentee();

    }


    public void getCalendarDetailsForMentee() {
        /* Array list of 3 months previous, current and coming , These points Day class object for day details like class schedule*/
        previousMonthArrayList = new ArrayList<Slot>();
        currentMonthArrayList = new ArrayList<Slot>();
        comingMonthArrayList = new ArrayList<Slot>();
        previousMonthNonCoincidingVacation = new ArrayList<Vacation>();
        currentMonthNonCoincidingVacation = new ArrayList<Vacation>();
        comingMonthNonCoincidingVacation = new ArrayList<Vacation>();
        previousMonthYearInfo = new ArrayList<MonthYearInfo>();
        comingMonthYearInfo = new ArrayList<MonthYearInfo>();
        currentMonthYearInfo = new ArrayList<MonthYearInfo>();
        previousMonthMentorInfos = new ArrayList<MentorInfo>();
        currentMonthMentorInfos = new ArrayList<MentorInfo>();
        comingMonthMentorInfos = new ArrayList<MentorInfo>();
        if (month_from_dialog == 0 && year_from_dialog == 0) {
            if (populate_calendar_from_adapter) {
                populate_calendar_from_adapter = false;
                currentMonth.setText(getResources().getStringArray(R.array.months)[month - 1] + " " + year);
            } else {
                startPointForCalendar();
            }
        } else {
            month = month_from_dialog;  /* CustomDatePicketFragment is assigning value to month_from_dailog and year_from_dialog*/
            year = year_from_dialog;
            month_from_dialog = 0;
            year_from_dialog = 0;
            currentMonth.setText(getResources().getStringArray(R.array.months)[month - 1] + " " + year);
        }


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        RequestParams requestParams = new RequestParams();
        requestParams.add("user_group", String.valueOf(2));
        requestParams.add("student_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        StringBuilder stringBuilder = new StringBuilder();

        /*Checking previous month possibilities for month and year as we have to get no. of days from previous month and adding this with current and coming month */
        /* Foreground month over calendar will be always similar according to this class local month and year variable . That means when we change month and year from any place this month and year variable get updated*/
        /* Days for the previous month of the calendar from foreground month over calendar. */
        if (month == 1) {
            /*
            Start date for three months data request from server get build
            */
            Calendar calendar = new GregorianCalendar(year - 1, 11, 1);
            stringBuilder.append((year - 1));
            stringBuilder.append("-" + 12);
            stringBuilder.append("-" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) - 1, 1);
            /*
            Start date for three months data request from server get build
            */
            stringBuilder.append(year);
            stringBuilder.append("-" + (month - 1));
            stringBuilder.append("-" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }


        /*Days for the month which is going to be foreground on calendar */
        days_in_current_month = new GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH);


        /* Days for the next month of current foreground month on calendar */
        if (month == 12) {
            Calendar calendar = new GregorianCalendar(year + 1, 0, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) + 1, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        requestParams.add("start_date", String.valueOf(stringBuilder));
        Log.d(TAG, "start date: " + String.valueOf(stringBuilder));


        previous_month_start_date = String.valueOf(stringBuilder);    /* this will be used to identify previous, current, coming month date (yyyy-mm-dd) */

        requestParams.add("limit", String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));
        Log.d(TAG, "limit: " + String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));

        networkCallForMentee(requestParams);
    }

    void networkCallForMentee(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 40);  /*Network operation for getting details for three months */
    }

    private void applyListeners() {


    }

    /* Initializing schedule for Mentor */
    public void initialize(final View view) {
        tv_today = (TextView) view.findViewById(R.id.tv_today);
        tv_today.setOnClickListener(this);

        tv_location_for_calendar = (TextView) view.findViewById(R.id.tv_location_for_calendar);
        tv_location_for_calendar.setOnClickListener(this);

        ll_location_for_calendar = (LinearLayout) view.findViewById(R.id.ll_location_for_calendar);
        ll_location_for_calendar.setVisibility(View.GONE);

        add_slot = (Button) view.findViewById(R.id.tv_add_new_slot);
        add_slot.setOnClickListener(this);

        add_vacation = (Button) view.findViewById(R.id.tv_add_vacation);
        add_vacation.setOnClickListener(this);

        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
        currentMonth.setOnClickListener(this);

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (ScrollableGridView) view.findViewById(R.id.calendar);
        getCalendarDetailsAPICall();    /* API call for 3 months data */

        /* Checkbox used to set flag for viewing calendar details by location */
        cb_calendar_by_location = (RadioButton) view.findViewById(R.id.cb_calendar_by_location);
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
        previousMonthArrayList = new ArrayList<Slot>();
        currentMonthArrayList = new ArrayList<Slot>();
        comingMonthArrayList = new ArrayList<Slot>();
        previousMonthNonCoincidingVacation = new ArrayList<Vacation>();
        currentMonthNonCoincidingVacation = new ArrayList<Vacation>();
        comingMonthNonCoincidingVacation = new ArrayList<Vacation>();
        previousMonthYearInfo = new ArrayList<MonthYearInfo>();
        comingMonthYearInfo = new ArrayList<MonthYearInfo>();
        currentMonthYearInfo = new ArrayList<MonthYearInfo>();
        previousMonthMentorInfos = new ArrayList<MentorInfo>();
        currentMonthMentorInfos = new ArrayList<MentorInfo>();
        comingMonthMentorInfos = new ArrayList<MentorInfo>();


        class_type_from_pref = StorageHelper.getClassTypePreference(getActivity());

        Log.d(TAG, "MyScheduleFragment: class_type preference value form shared preference: " + class_type_from_pref);

        if (month_from_dialog == 0 && year_from_dialog == 0) {
            if (populate_calendar_from_adapter) {
                populate_calendar_from_adapter = false;
                currentMonth.setText(getResources().getStringArray(R.array.months)[month - 1] + " " + year);
            } else {
                startPointForCalendar();
            }

        } else {
            month = month_from_dialog;
            year = year_from_dialog;
            month_from_dialog = 0;
            year_from_dialog =0;
            currentMonth.setText(getResources().getStringArray(R.array.months)[month - 1] + " " + year);
        }


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getResources().getString(R.string.please_wait));

        RequestParams requestParams = new RequestParams();
        requestParams.add("user_group", String.valueOf("3"));
        requestParams.add("mentor_id", StorageHelper.getUserDetails(getActivity(), "user_id"));

        if (class_type_from_pref != -1) {
            switch (class_type_from_pref) {
                case 0:/* in case of 0 user selected All types of classes so it does needed to send to server*/
                    break;
                case 1:
                    requestParams.add("slot_type", "Individual");
                    break;
                case 2:
                    requestParams.add("slot_type", "Group");
                    break;
            }
        } else {
            /* user does not selected class type preference*/
        }


        StringBuilder stringBuilder = new StringBuilder();

        /*Checking previous month possibilities for month and year as we have to get no. of days from previous month and adding this with current and coming month */
        /* Foreground month over calendar will be always similar according to this class local month and year variable . That means when we change month and year from any place this month and year variable get updated*/
        /* Days for the previous month of the calendar from foreground month over calendar. */
        if (month == 1) {
            Calendar calendar = new GregorianCalendar(year - 1, 11, 1);
            /*
            Start date for three months data request from server get build
            */
            stringBuilder.append((year - 1));
            stringBuilder.append("-" + 12);
            stringBuilder.append("-" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) - 1, 1);
            /*
            Start date for three months data request from server get build
            */
            stringBuilder.append(year);
            stringBuilder.append("-" + (month - 1));
            stringBuilder.append("-" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        /*Days for the month which is going to be foreground on calendar */
        days_in_current_month = new GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH);


        /* Days for the next month of current foreground month on calendar */

        if (month == 12) {
            Calendar calendar = new GregorianCalendar(year + 1, 0, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) + 1, 1);
            days_in_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }


        requestParams.add("start_date", String.valueOf(stringBuilder));
        requestParams.add("limit", String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));

        previous_month_start_date = String.valueOf(stringBuilder);    /* this will be used to identify previous, current, coming month date (yyyy-mm-dd) */
        Log.d(TAG, "MyScheduleFragment, mentor_id: " + "mentor_id : " + StorageHelper.getUserDetails(getActivity(), "user_id") + ", start date :" + String.valueOf(stringBuilder) + ", limit: " + String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));

        networkCall1(requestParams);  /* Now not needed to check calendar by location here*/


        /*if (cb_calendar_by_location_is_checked) {
            Log.d(TAG, "calendar_by_location is checked true");
            if (calendar_by_location != null && !calendar_by_location.trim().equals("")) {
                Log.d(TAG, "Calendar_by_location getting passed to server : " + calendar_by_location);
                requestParams.add("location", calendar_by_location);
                Log.d(TAG, " Data getting requested for three months : " + "start date : " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month) + " location : " + calendar_by_location);
                networkCall1(requestParams);
            } else {


                cb_calendar_by_location.setChecked(false);
            }
        } else {


            Log.d(TAG, " Data getting requested for three months : " + "start date : " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));
            Log.d(TAG, "start networkCall1");
            networkCall1(requestParams);
        }
*/

    }

    public void networkCall1(RequestParams requestParams) {
        progressDialog.show();
        Log.d(TAG, "MyScheudleFragment, auth_token " + StorageHelper.getUserDetails(getActivity(), "auth_token"));
        NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 37); /* Network operation for getting details for three months */
        Log.d(TAG, "FMC auth token :" + StorageHelper.getUserDetails(getActivity(), "auth_token"));
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

                DatePickerFragment datePickerDialog = new DatePickerFragment();
                datePickerDialog.textView = currentMonth;
                Bundle bundle = new Bundle();
                bundle.putString("for", "MyScheduleFragment");
                datePickerDialog.setArguments(bundle);
                datePickerDialog.show(getActivity().getFragmentManager(), "monthPicker");
                month_from_dialog = 0;
                year_from_dialog = 0;


//                FragmentManager fragmentManager = getFragmentManager();
//                CustomDatePickerFragment customDatePickerFragment = new CustomDatePickerFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("for", "MyScheduleFragment");
//                customDatePickerFragment.setArguments(bundle);
//                customDatePickerFragment.show(fragmentManager, null);
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

                DatePickerFragment datePickerDialog = new DatePickerFragment();
                datePickerDialog.textView = currentMonth;
                Bundle bundle = new Bundle();
                bundle.putString("for", "MyScheduleFragment");
                datePickerDialog.setArguments(bundle);
                datePickerDialog.show(getActivity().getFragmentManager(), "monthPicker");
                month_from_dialog = 0;
                year_from_dialog = 0;

//                Log.d(TAG, "currentMonth clicked");
//                FragmentManager fragmentManager = getFragmentManager();
//                CustomDatePickerFragment customDatePickerFragment = new CustomDatePickerFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("for", "MyScheduleFragment");
//                customDatePickerFragment.setArguments(bundle);
//
//                customDatePickerFragment.show(fragmentManager, null);
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

        if (v == tv_today) {
            Calendar calendar_right_now = Calendar.getInstance();
            int right_now_month = calendar_right_now.get(Calendar.MONTH) + 1;
            int right_now_year = calendar_right_now.get(Calendar.YEAR);

            Log.d(TAG, "right_now_month" + right_now_month + " month: " + month);
            Log.d(TAG, "right_now_year" + right_now_year + " year " + year);

            if ((right_now_month) == month && right_now_year == year) {
                Toast.makeText(getActivity(), getResources().getString(R.string.current_month_view_message), Toast.LENGTH_SHORT).show();
            } else {
                populate_calendar_from_adapter = true;
                month = right_now_month;
                year = right_now_year;
                String user_group = StorageHelper.getUserGroup(getActivity(), "user_group");

                if (user_group.equals("3")) {
                    getCalendarDetailsAPICall();

                } else {
                    if (user_group.equals("2")) {
                                   /*mentee three months data will get called from here */
                        getCalendarDetailsForMentee();
                    }
                }


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


        Log.d(TAG, "request_Code: " + requestCode + ", result code: " + resultCode);
        if (requestCode == NEW_SLT && resultCode == 500) {
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
        stringBuilder.append("-" + month_for_this);
        stringBuilder.append("-" + 1);


        Calendar calendar = new GregorianCalendar(year_for_this, (month_for_this - 1), 1);
        days_in_new_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (DashboardActivity.dashboardActivity.user_group == 3) {
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", String.valueOf("3"));
            requestParams.add("mentor_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            prev_month_requested_date = String.valueOf(stringBuilder);
            requestParams.add("limit", String.valueOf(days_in_new_prev_month));

            if (class_type_from_pref != -1) {
                switch (class_type_from_pref) {
                    case 0:/* in case of 0 user selected All types of classes so it does needed to send to server*/
                        break;
                    case 1:
                        requestParams.add("slot_type", "Individual");
                        break;
                    case 2:
                        requestParams.add("slot_type", "Group");
                        break;
                }
            } else {
            /* user does not selected class type preference*/
            }
            networkCall2(requestParams);

            /*if (cb_calendar_by_location_is_checked) {
                if (calendar_by_location != null) {
                    Log.d(TAG, "Calendar_by_location getting passed to server : " + calendar_by_location);
                    requestParams.add("location", calendar_by_location);
                    Log.d(TAG, " Request for calendar details for prev month " + "start _date: " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_new_prev_month) + " calendar by location : " + calendar_by_location);
                    networkCall2(requestParams);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.please_provide_location_to_access_details), Toast.LENGTH_SHORT).show();
                    getLocationFromDialog();  *//* start LocationFromDialog to get the location *//*
                }
            } else {
                Log.d(TAG, " Request for calendar details for prev month " + "start _date: " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_new_prev_month));
                networkCall2(requestParams);
            }
*/
        }
        if (DashboardActivity.dashboardActivity.user_group == 2) {
            Log.d(TAG, "calling getMenteeCalendarDetails api for prev month");
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", String.valueOf("2"));
            requestParams.add("student_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            prev_month_requested_date = String.valueOf(stringBuilder);
            requestParams.add("limit", String.valueOf(days_in_new_prev_month));
            progressDialog.show();
            NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 42);
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
        stringBuilder.append("-" + month_for_this);
        stringBuilder.append("-" + 1);


        Calendar calendar = new GregorianCalendar(year_for_this, (month_for_this - 1), 1);
        days_in_new_next_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 3) {
            Log.d(TAG, "calling getMenteeCalendarDetails api for next month user_Group 3");
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", String.valueOf("3"));
            requestParams.add("mentor_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            next_month_requested_date = String.valueOf(stringBuilder);

            requestParams.add("limit", String.valueOf(days_in_new_next_month));

            if (class_type_from_pref != -1) {
                switch (class_type_from_pref) {
                    case 0:/* in case of 0 user selected All types of classes so it does needed to send to server*/
                        break;
                    case 1:
                        requestParams.add("slot_type", "Individual");
                        break;
                    case 2:
                        requestParams.add("slot_type", "Group");
                        break;
                }
            } else {
            /* user does not selected class type preference*/
            }

            Log.i(TAG, "user_group : " + String.valueOf("3") + " mentor id : " + StorageHelper.getUserDetails(getActivity(), "user_id") + " start date : " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_new_next_month));
            networkCall3(requestParams);

            /*if (cb_calendar_by_location_is_checked) {
                if (calendar_by_location != null) {
                    Log.d(TAG, "Calendar_by_location getting passed to server : " + calendar_by_location);
                    requestParams.add("location", calendar_by_location);
                    Log.d(TAG, " Request for calendar details for prev month " + "start _date: " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_new_next_month) + " location : " + calendar_by_location);
                    networkCall3(requestParams);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.please_provide_location_to_access_details), Toast.LENGTH_SHORT).show();
                    getLocationFromDialog();  *//* start LocationFromDialog to get the location *//*
                }
            } else {
                Log.d(TAG, " Request for calendar details for prev month " + "start _date: " + String.valueOf(stringBuilder) + " limit : " + String.valueOf(days_in_new_next_month));
                networkCall3(requestParams);
            }*/


        }
        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 2) {
            Log.d(TAG, "calling getMenteeCalendarDetails api for next month user_Group 2");
            RequestParams requestParams = new RequestParams();
            requestParams.add("user_group", String.valueOf("2"));
            requestParams.add("student_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
            requestParams.add("start_date", String.valueOf(stringBuilder));
            next_month_requested_date = String.valueOf(stringBuilder);

            requestParams.add("limit", String.valueOf(days_in_new_next_month));
            progressDialog.show();
            NetworkClient.getCalendarDetails(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 41);
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
        progressDialog.dismiss();
        switch (calledApiValue) {
            case 37:
                Log.d(TAG, "inside 37 failure");
                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateArrayListForThreeMonths();
                updateCalendarOnFailure();
                break;
            case 38:
                Log.d(TAG, "inside 38 failure");
                updateMonthAndYearOnNextMonthClick();
                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateArrayListsForNextMonth();

                updateCalendarOnFailure();
                break;
            case 39:

                updateMonthAndYearOnPreviousMonthClick();
                updateArrayListsForPreviousMonth();
                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateCalendarOnFailure();
                break;
            case 40:

                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateArrayListForThreeMonths();
                updateCalendarOnFailure();
                break;
            case 41:

                updateMonthAndYearOnNextMonthClick();
                updateArrayListsForNextMonth();
                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateCalendarOnFailure();
                break;
            case 42:

                updateMonthAndYearOnPreviousMonthClick();
                updateArrayListsForPreviousMonth();
                Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                updateCalendarOnFailure();
                break;
        }

    }

    private void updateArrayListForThreeMonths() {
        try {
            Slot slot = new Slot();
            slot.setSlot_created_on_network_success("false");
            previousMonthArrayList.add(slot);
            currentMonthArrayList.add(slot);
            comingMonthArrayList.add(slot);

        /* Making non-coinciding Arraylists updated with "false" network communication */
            Vacation vacation = new Vacation();
            vacation.setVacation_made_at_network_success("false");
            previousMonthNonCoincidingVacation.add(vacation);
            currentMonthNonCoincidingVacation.add(vacation);
            comingMonthNonCoincidingVacation.add(vacation);


            int previous_month = Integer.parseInt(previous_month_start_date.split("-")[1]);
            int previous_month_year = Integer.parseInt(previous_month_start_date.split("-")[0]);

            int current_month, current_year, coming_month, coming_year;
            if (previous_month == 11) {
                current_month = 12;
                current_year = previous_month_year;
                coming_month = 1;
                coming_year = previous_month_year;
                ++coming_year;
            } else {
                if (previous_month == 12) {
                    current_month = 1;
                    current_year = previous_month_year;
                    ++current_year;
                    coming_month = 2;
                    coming_year = current_year;
                } else {
                    current_month = previous_month;
                    ++current_month;
                    current_year = previous_month_year;
                    coming_month = current_month;
                    ++coming_month;
                    coming_year = previous_month_year;
                }
            }

            previousMonthYearInfo = getMonthYearForThis(previous_month, previous_month_year, finalizeDaysInMonth(previous_month, previous_month_year));
            currentMonthYearInfo = getMonthYearForThis(current_month, current_year, finalizeDaysInMonth(current_month, current_year));
            comingMonthYearInfo = getMonthYearForThis(coming_month, coming_year, finalizeDaysInMonth(coming_month, coming_year));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateArrayListsForNextMonth() {
        try {
            previousMonthArrayList = currentMonthArrayList;
            currentMonthArrayList = comingMonthArrayList;
            comingMonthArrayList = null;
            comingMonthArrayList = new ArrayList<Slot>();
            Slot slot = new Slot();
            slot.setSlot_created_on_network_success("false");
            comingMonthArrayList.add(slot);


            previousMonthNonCoincidingVacation = currentMonthNonCoincidingVacation;
            currentMonthNonCoincidingVacation = comingMonthNonCoincidingVacation;
            currentMonthNonCoincidingVacation = null;
            comingMonthNonCoincidingVacation = new ArrayList<Vacation>();
            Vacation vacation = new Vacation();
            vacation.setVacation_made_at_network_success("false");
            comingMonthNonCoincidingVacation.add(vacation);


            previousMonthYearInfo = currentMonthYearInfo;
            currentMonthYearInfo = comingMonthYearInfo;
            comingMonthYearInfo = null;
            comingMonthYearInfo = new ArrayList<MonthYearInfo>();
            comingMonthYearInfo = getMonthYearForThis(Integer.parseInt(next_month_requested_date.split("-")[1]), Integer.parseInt(next_month_requested_date.split("-")[0]), finalizeDaysInMonth(Integer.parseInt(next_month_requested_date.split("-")[1]), Integer.parseInt(next_month_requested_date.split("-")[0])));


            previousMonthMentorInfos = currentMonthMentorInfos;
            currentMonthMentorInfos = comingMonthMentorInfos;
            comingMonthMentorInfos = null;
            comingMonthMentorInfos = new ArrayList<MentorInfo>();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void updateArrayListsForPreviousMonth() {
        try {
            comingMonthArrayList = currentMonthArrayList;
            currentMonthArrayList = previousMonthArrayList;
            previousMonthArrayList = null;
            previousMonthArrayList = new ArrayList<Slot>();
            Slot slot = new Slot();
            slot.setSlot_created_on_network_success("false");
            previousMonthArrayList.add(slot);

            comingMonthNonCoincidingVacation = currentMonthNonCoincidingVacation;
            currentMonthNonCoincidingVacation = previousMonthNonCoincidingVacation;
            previousMonthNonCoincidingVacation = null;
            previousMonthNonCoincidingVacation = new ArrayList<Vacation>();
            Vacation vacation = new Vacation();
            vacation.setVacation_made_at_network_success("false");
            previousMonthNonCoincidingVacation.add(vacation);

            comingMonthYearInfo = currentMonthYearInfo;
            currentMonthYearInfo = previousMonthYearInfo;
            previousMonthYearInfo = null;
            previousMonthYearInfo = new ArrayList<MonthYearInfo>();
            previousMonthYearInfo = getMonthYearForThis(Integer.parseInt(prev_month_requested_date.split("-")[1]), Integer.parseInt(prev_month_requested_date.split("-")[0]), finalizeDaysInMonth(Integer.parseInt(prev_month_requested_date.split("-")[1]), Integer.parseInt(prev_month_requested_date.split("-")[0])));


            comingMonthMentorInfos = currentMonthMentorInfos;
            currentMonthMentorInfos = previousMonthMentorInfos;
            previousMonthMentorInfos = null;
            previousMonthMentorInfos = new ArrayList<MentorInfo>();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void updateCalendarOnFailure() {
        Log.d(TAG, "updateCalendarOnFailure call ");

        adapter1 = new CalendarGridAdapter(getActivity(), month, year, this);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));

        calendarView.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
        progressDialog.dismiss();
    }


    private int finalizeDaysInMonth(int month, int year) {
        int days;
        if (isLeapYear(year)) {
            if (month == 2) {
                days = 29;
            } else {
                days = daysOfMonth[month - 1];
            }
        } else {
            days = daysOfMonth[month - 1];


        }
        return days;
    }

    private void threeMonthsData(Object object) {

        Log.d(TAG, "inside three months data population");


        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 3) {


            Log.d(TAG, "Inside threeMonthData method for user_group 3");
            progressDialog.dismiss();
            try {

                JSONObject jsonObject = new JSONObject((String) object);
                JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                JSONArray jsonArray_mentor = jsonObject_data.getJSONArray("mentor");
                JSONArray jsonArray_data = jsonObject_data.getJSONArray("slots");
                JSONArray jsonArray_vacation_non_coinciding = jsonObject_data.getJSONArray("vacations");

                List<Slot> slots = new ArrayList<Slot>();
                List<Vacation> vacations = new ArrayList<Vacation>();  /* list of non coinciding vacations*/

                parseSlots(slots, jsonArray_data, 3);
                parseVacation(vacations, jsonArray_vacation_non_coinciding);

                int previous_month = Integer.parseInt(previous_month_start_date.split("-")[1]);
                int previous_month_year = Integer.parseInt(previous_month_start_date.split("-")[0]);

                int current_month, current_year, coming_month, coming_year;
                if (previous_month == 11) {
                    current_month = 12;
                    current_year = previous_month_year;
                    coming_month = 1;
                    coming_year = previous_month_year;
                    ++coming_year;
                } else {
                    if (previous_month == 12) {
                        current_month = 1;
                        current_year = previous_month_year;
                        ++current_year;
                        coming_month = 2;
                        coming_year = current_year;
                    } else {
                        current_month = previous_month;
                        ++current_month;
                        current_year = previous_month_year;
                        coming_month = current_month;
                        ++coming_month;
                        coming_year = previous_month_year;
                    }
                }


                previousMonthArrayList = getSlotsForThis(slots, previous_month, previous_month_year, finalizeDaysInMonth(previous_month, previous_month_year));
                currentMonthArrayList = getSlotsForThis(slots, current_month, current_year, finalizeDaysInMonth(current_month, current_year));
                comingMonthArrayList = getSlotsForThis(slots, coming_month, coming_year, finalizeDaysInMonth(coming_month, coming_year));

                Log.d(TAG, "pr_slots_size" + previousMonthArrayList.size());
                Log.d(TAG, "cr_slots_size" + currentMonthArrayList.size());
                Log.d(TAG, "co_slots_size" + comingMonthArrayList.size());

                previousMonthNonCoincidingVacation = getVacationsForThis(vacations, previous_month, previous_month_year, finalizeDaysInMonth(previous_month, previous_month_year));
                currentMonthNonCoincidingVacation = getVacationsForThis(vacations, current_month, current_year, finalizeDaysInMonth(current_month, current_year));
                comingMonthNonCoincidingVacation = getVacationsForThis(vacations, coming_month, coming_year, finalizeDaysInMonth(coming_month, coming_year));
                previousMonthYearInfo = getMonthYearForThis(previous_month, previous_month_year, finalizeDaysInMonth(previous_month, previous_month_year));
                currentMonthYearInfo = getMonthYearForThis(current_month, current_year, finalizeDaysInMonth(current_month, current_year));
                comingMonthYearInfo = getMonthYearForThis(coming_month, coming_year, finalizeDaysInMonth(coming_month, coming_year));
                previousMonthMentorInfos = getMentorInfo(jsonArray_mentor);
                currentMonthMentorInfos = getMentorInfo(jsonArray_mentor);
                comingMonthMentorInfos = getMentorInfo(jsonArray_mentor);
                Log.d(TAG, "For mentor, previousMonthArrayList size :" + previousMonthArrayList.size() + "currentMonthArrayList size :" + currentMonthArrayList.size() + ", comingMonthArrayList size :" + comingMonthArrayList.size());
                if (b_three_months_data) {
                    Log.d(TAG, "Three months data get changed");
                    adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos);
                    calendarView.setAdapter(adapter1);
                    adapter1.notifyDataSetChanged();
                    if (month_from_dialog == 0 && year_from_dialog == 0) {
                        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
                    }

                } else {
                    adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos);
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
                JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                JSONArray jsonArray_mentor = jsonObject_data.getJSONArray("mentor");
                JSONArray jsonArray_data = jsonObject_data.getJSONArray("slots");
                /*JSONArray jsonArray_vacation_non_coinciding = jsonObject_data.getJSONArray("vacations");*//* We are not using non coinciding vacations for mentee*/

                List<Slot> slots = new ArrayList<Slot>();
                List<Vacation> vacations = new ArrayList<Vacation>();  /* list of non coinciding vacations*/

                parseSlots(slots, jsonArray_data, 2);

                int previous_month = Integer.parseInt(previous_month_start_date.split("-")[1]);
                int previous_month_year = Integer.parseInt(previous_month_start_date.split("-")[0]);

                int current_month, current_year, coming_month, coming_year;
                if (previous_month == 11) {
                    current_month = 12;
                    current_year = previous_month_year;
                    coming_month = 1;
                    coming_year = previous_month_year;
                    ++coming_year;
                } else {
                    if (previous_month == 12) {
                        current_month = 1;
                        current_year = previous_month_year;
                        ++current_year;
                        coming_month = 2;
                        coming_year = current_year;
                    } else {
                        current_month = previous_month;
                        ++current_month;
                        current_year = previous_month_year;
                        coming_month = current_month;
                        ++coming_month;
                        coming_year = previous_month_year;
                    }
                }


                previousMonthArrayList = getSlotsForThis(slots, previous_month, previous_month_year, finalizeDaysInMonth(previous_month, previous_month_year));
                currentMonthArrayList = getSlotsForThis(slots, current_month, current_year, finalizeDaysInMonth(current_month, current_year));
                comingMonthArrayList = getSlotsForThis(slots, coming_month, coming_year, finalizeDaysInMonth(coming_month, coming_year));
                previousMonthNonCoincidingVacation = getVacationsForThis(vacations, previous_month, previous_month_year, finalizeDaysInMonth(previous_month, previous_month_year));
                currentMonthNonCoincidingVacation = getVacationsForThis(vacations, current_month, current_year, finalizeDaysInMonth(current_month, current_year));
                comingMonthNonCoincidingVacation = getVacationsForThis(vacations, coming_month, coming_year, finalizeDaysInMonth(coming_month, coming_year));
                previousMonthYearInfo = getMonthYearForThis(previous_month, previous_month_year, finalizeDaysInMonth(previous_month, previous_month_year));
                currentMonthYearInfo = getMonthYearForThis(current_month, current_year, finalizeDaysInMonth(current_month, current_year));
                comingMonthYearInfo = getMonthYearForThis(coming_month, coming_year, finalizeDaysInMonth(coming_month, coming_year));
                previousMonthMentorInfos = getMentorInfo(jsonArray_mentor);
                currentMonthMentorInfos = getMentorInfo(jsonArray_mentor);
                comingMonthMentorInfos = getMentorInfo(jsonArray_mentor);

                Log.d(TAG, " For mentee: previousMonthArrayList size :" + previousMonthArrayList.size() + "currentMonthArrayList size :" + currentMonthArrayList.size() + ", comingMonthArrayList size :" + comingMonthArrayList.size());

                adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos);
                calendarView.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private ArrayList<MentorInfo> getMentorInfo(JSONArray jsonArray_mentor) {
        ArrayList<MentorInfo> mentorInfos = new ArrayList<MentorInfo>();
        for (int array_index = 0; array_index < jsonArray_mentor.length(); array_index++) {
            try {
                JSONObject jsonObject = jsonArray_mentor.getJSONObject(array_index);
                MentorInfo mentorInfo = new MentorInfo();
                mentorInfo.setMentor_id(jsonObject.getString("mentor_id"));
                mentorInfo.setFirst_name(jsonObject.getString("first_name"));
                mentorInfo.setLast_name(jsonObject.getString("last_name"));
                mentorInfos.add(mentorInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mentorInfos;
    }

    private ArrayList<MonthYearInfo> getMonthYearForThis(int month, int year, int days) {
        ArrayList<MonthYearInfo> monthYearInfos = new ArrayList<MonthYearInfo>();
        MonthYearInfo monthYearInfo = new MonthYearInfo();
        monthYearInfo.setMonth(month);
        monthYearInfo.setYear(year);
        monthYearInfo.setDays(days);
        monthYearInfos.add(monthYearInfo);
        return monthYearInfos;


    }


    private void parseVacation(List<Vacation> vacations, JSONArray jsonArray_vacation_non_coinciding) {
        for (int vacation_index = 0; vacation_index < jsonArray_vacation_non_coinciding.length(); vacation_index++) {
            try {
                Vacation vacation = new Vacation();
                JSONObject vacation_jsonObject = jsonArray_vacation_non_coinciding.getJSONObject(vacation_index);
                vacation.setVacation_id(vacation_jsonObject.getString("vacation_id"));
                vacation.setStart_date(vacation_jsonObject.getString("start_date"));
                vacation.setStop_date(vacation_jsonObject.getString("stop_date"));
                vacation.setCause_of_the_vacation(vacation_jsonObject.getString("cause_of_the_vacation"));
                /*JSONArray vacation_weekdays = vacation_jsonObject.getJSONArray("weekdays");
                String vacation_weekdays_array[] = new String[vacation_weekdays.length()];
                for (int week_day = 0; week_day < vacation_weekdays.length(); week_day++) {
                    vacation_weekdays_array[week_day] = vacation_weekdays.getString(week_day);   *//* week_day is used to pass index *//*
                }*/
                vacation.setStart_time(vacation_jsonObject.getString("start_time"));
                vacation.setStop_time(vacation_jsonObject.getString("stop_time"));
                vacation.setVacation_made_at_network_success("true");
                vacations.add(vacation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parseSlots(List<Slot> slots, JSONArray jsonArray_data, int user_group) {
        for (int json_Array_data_index = 0; json_Array_data_index < jsonArray_data.length(); json_Array_data_index++) {
            try {
                Slot slot = new Slot();
                List<Event> events = new ArrayList<Event>();
                List<Vacation> vacations = new ArrayList<Vacation>();

                JSONObject slot_jsonObject = jsonArray_data.getJSONObject(json_Array_data_index);
                slot.setSlot_id(slot_jsonObject.getString("slot_id"));
                slot.setMentor_id(slot_jsonObject.getString("mentor_id"));
                slot.setSlot_start_time(slot_jsonObject.getString("start_time"));
                slot.setSlot_stop_time(slot_jsonObject.getString("stop_time"));
                slot.setSlot_start_date(slot_jsonObject.getString("start_date"));
                slot.setSlot_stop_date(slot_jsonObject.getString("stop_date"));
                slot.setSlot_type(slot_jsonObject.getString("slot_type"));
                slot.setSlot_max_users(slot_jsonObject.getString("max_users"));
                JSONArray jsonArray_week_days = slot_jsonObject.getJSONArray("weekdays");
                String week_days[] = new String[jsonArray_week_days.length()];
                for (int week_day = 0; week_day < jsonArray_week_days.length(); week_day++) {
                    week_days[week_day] = jsonArray_week_days.getString(week_day);
                }
                slot.setSlot_week_days(week_days);

                JSONArray event_jsonArray = slot_jsonObject.getJSONArray("events");
                JSONArray vacation_jsonArray = slot_jsonObject.getJSONArray("vacations");

                for (int event_jsonArray_index = 0; event_jsonArray_index < event_jsonArray.length(); event_jsonArray_index++) {
                    Event event = new Event();
                    JSONObject event_jsonObject = event_jsonArray.getJSONObject(event_jsonArray_index);
                    JSONArray event_mentees = event_jsonObject.getJSONArray("mentee");
                    List<Mentee> mentees = new ArrayList<Mentee>();
                    for (int mentee_index = 0; mentee_index < event_mentees.length(); mentee_index++) {
                        Mentee mentee = new Mentee();
                        JSONObject mentee_jsonObject = event_mentees.getJSONObject(mentee_index);
                        JSONArray event_durations = mentee_jsonObject.getJSONArray("durations");
                        List<EventDuration> eventDurations = new ArrayList<EventDuration>();
                        for (int event_duration_no = 0; event_duration_no < event_durations.length(); event_duration_no++) {
                            JSONObject jsonObject_event_duration = event_durations.getJSONObject(event_duration_no);
                            EventDuration eventDuration = new EventDuration();
                            eventDuration.setStart_date(jsonObject_event_duration.getString("start_date"));
                            eventDuration.setStop_date(jsonObject_event_duration.getString("stop_date"));
                            eventDurations.add(eventDuration);
                        }

                        mentee.setEventDurations(eventDurations);

                        if (StorageHelper.getUserGroup(getActivity(), "user_group").equals("2")) {
                            /* first name and last name is not coming , We have full in shared preference from PaymentDetailsActivity */
                            mentee.setFirst_name("");
                            mentee.setLast_name("");
                        } else {
                            mentee.setFirst_name(mentee_jsonObject.getString("first_name"));
                            mentee.setLast_name(mentee_jsonObject.getString("last_name"));
                        }
                        mentees.add(mentee);
                    }
                    event.setEvent_id(event_jsonObject.getString("event_id"));
                    event.setSub_category_name(event_jsonObject.getString("sub_category"));
                    event.setEvent_total_mentee(event_jsonObject.getString("active_members"));
                    event.setMentees(mentees);
                    events.add(event);


                }
                slot.setEvents(events);

                for (int vacation_jsonArray_index = 0; vacation_jsonArray_index < vacation_jsonArray.length(); vacation_jsonArray_index++) {
                    Vacation vacation = new Vacation();
                    JSONObject vacation_jsonObject = vacation_jsonArray.getJSONObject(vacation_jsonArray_index);
                    vacation.setVacation_id(vacation_jsonObject.getString("vacation_id"));
                    vacation.setStart_date(vacation_jsonObject.getString("start_date"));
                    vacation.setStop_date(vacation_jsonObject.getString("stop_date"));
                    vacation.setCause_of_the_vacation(vacation_jsonObject.getString("cause_of_the_vacation"));
                    /*JSONArray vacation_weekdays = vacation_jsonObject.getJSONArray("weekdays");
                    String vacation_weekdays_array[] = new String[vacation_weekdays.length()];
                    for (int week_day = 0; week_day < vacation_weekdays.length(); week_day++) {
                        vacation_weekdays_array[week_day] = vacation_weekdays.getString(week_day);   *//* week_day is used to pass index *//*
                    vacation.setStart_time(vacation_jsonObject.getString("start_time"));
                    vacation.setStop_time(vacation_jsonObject.getString("stop_time"));
                    }*/

                    vacation.setVacation_made_at_network_success("true");
                    vacations.add(vacation);
                }
                slot.setVacations(vacations);
                slot.setSlot_created_on_network_success("true");
                slot.setSlot_subject(slot_jsonObject.getString("subject"));
                slots.add(slot);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private ArrayList<Slot> getSlotsForThis(List<Slot> slots, int month, int year, int days) {

        Log.d(TAG, "slots_size:" + slots.size() + ", month: " + month + ", year: " + year + ", days: " + days);
        ArrayList<Slot> slotArrayList = new ArrayList<Slot>();

        for (int slot_no = 0; slot_no < slots.size(); slot_no++) {
            Slot slot = slots.get(slot_no);
            String start_date = slot.getSlot_start_date();
            String stop_date = slot.getSlot_stop_date();

            Calendar calendar_start_of_month = Calendar.getInstance();
            calendar_start_of_month.set(year, month - 1, 1);
            long month_start_date_in_millis = calendar_start_of_month.getTimeInMillis();
            Log.d(TAG, "start_of_month_millis: " + month_start_date_in_millis);


            Calendar calendar_slot_start_date = Calendar.getInstance();
            calendar_slot_start_date.set(Integer.parseInt(start_date.split("-")[0]), Integer.parseInt(start_date.split("-")[1]) - 1, Integer.parseInt(start_date.split("-")[2]));
            long slot_start_date_in_millis = calendar_slot_start_date.getTimeInMillis();
            Log.d(TAG, "slot_start_of_month_millis: " + slot_start_date_in_millis);


            Calendar calendar_slot_end_date = Calendar.getInstance();
            calendar_slot_end_date.set(Integer.parseInt(stop_date.split("-")[0]), Integer.parseInt(stop_date.split("-")[1]) - 1, Integer.parseInt(stop_date.split("-")[2]));
            long slot_stop_date_in_millis = calendar_slot_end_date.getTimeInMillis();
            Log.d(TAG, "slot_stop_of_month_millis: " + slot_stop_date_in_millis);


            Calendar calendar_end_of_month = Calendar.getInstance();
            calendar_end_of_month.set(year, month - 1, days);
            long month_end_date_in_millis = calendar_end_of_month.getTimeInMillis();
            Log.d(TAG, "stop_of_month_millis: " + month_end_date_in_millis);


            /*if (((slot_start_date_in_millis < month_start_date_in_millis) && slot_stop_date_in_millis > month_end_date_in_millis) ||
                    (slot_start_date_in_millis < month_start_date_in_millis && slot_stop_date_in_millis > month_start_date_in_millis && slot_stop_date_in_millis < month_end_date_in_millis) ||
                    (slot_start_date_in_millis > month_start_date_in_millis && slot_start_date_in_millis < month_end_date_in_millis && (slot_stop_date_in_millis > month_end_date_in_millis || slot_stop_date_in_millis == month_end_date_in_millis)) ||
                    (slot_start_date_in_millis > month_start_date_in_millis && slot_start_date_in_millis < month_end_date_in_millis && slot_stop_date_in_millis > month_start_date_in_millis && slot_stop_date_in_millis < month_end_date_in_millis) ||
                    (slot_start_date_in_millis == month_start_date_in_millis && slot_stop_date_in_millis == month_end_date_in_millis)) {

                slotArrayList.add(slot);

            } else {
                Log.d(TAG, "not matched");
            }*/


            if (((slot_start_date_in_millis < month_start_date_in_millis || slot_start_date_in_millis == month_start_date_in_millis) && (slot_stop_date_in_millis > month_end_date_in_millis || slot_stop_date_in_millis == month_end_date_in_millis)) ||
                    ((slot_start_date_in_millis < month_start_date_in_millis || slot_start_date_in_millis == month_start_date_in_millis) && (slot_stop_date_in_millis > month_start_date_in_millis || slot_stop_date_in_millis == month_start_date_in_millis) && (slot_stop_date_in_millis < month_end_date_in_millis || slot_stop_date_in_millis == month_end_date_in_millis)) ||
                    ((slot_start_date_in_millis > month_start_date_in_millis || slot_start_date_in_millis == month_start_date_in_millis) && (slot_start_date_in_millis < month_end_date_in_millis || slot_start_date_in_millis == month_end_date_in_millis) && (slot_stop_date_in_millis > month_end_date_in_millis || slot_stop_date_in_millis == month_end_date_in_millis)) ||
                    ((slot_start_date_in_millis > month_start_date_in_millis || slot_start_date_in_millis == month_start_date_in_millis) && (slot_start_date_in_millis < month_end_date_in_millis || slot_start_date_in_millis == month_end_date_in_millis) && (slot_stop_date_in_millis > month_start_date_in_millis || slot_stop_date_in_millis == month_start_date_in_millis) && (slot_stop_date_in_millis < month_end_date_in_millis || slot_stop_date_in_millis == month_end_date_in_millis)) ||
                    (slot_start_date_in_millis == month_start_date_in_millis && slot_stop_date_in_millis == month_end_date_in_millis)) {

                slotArrayList.add(slot);

            } else {
                Log.d(TAG, "not matched");
            }

        }


        Log.d(TAG, "slots arrayList for the month size:" + slotArrayList.size());

        return slotArrayList;


    }

    private ArrayList<Vacation> getVacationsForThis(List<Vacation> vacations, int month, int year, int days) {
        ArrayList<Vacation> vacationArrayList = new ArrayList<Vacation>();


        for (int vacation_no = 0; vacation_no < vacations.size(); vacation_no++) {
            Vacation vacation = vacations.get(vacation_no);
            String start_date = vacation.getStart_date();
            String stop_date = vacation.getStop_date();

            Calendar calendar_start_of_month = Calendar.getInstance();
            calendar_start_of_month.set(year, month - 1, 1);
            long month_start_date_in_millis = calendar_start_of_month.getTimeInMillis();
            Log.d(TAG, "month start millies: " + month_start_date_in_millis);


            Calendar calendar_vacation_start_date = Calendar.getInstance();
            calendar_vacation_start_date.set(Integer.parseInt(start_date.split("-")[0]), Integer.parseInt(start_date.split("-")[1]) - 1, Integer.parseInt(start_date.split("-")[2]));
            long vacation_start_date_in_millis = calendar_vacation_start_date.getTimeInMillis();
            Log.d(TAG, "vacation month start millies: " + vacation_start_date_in_millis);


            Calendar calendar_vacation_end_date = Calendar.getInstance();
            calendar_vacation_end_date.set(Integer.parseInt(stop_date.split("-")[0]), Integer.parseInt(stop_date.split("-")[1]) - 1, Integer.parseInt(stop_date.split("-")[2]));
            long vacation_stop_date_in_millis = calendar_vacation_end_date.getTimeInMillis();
            Log.d(TAG, "vacation month stop millies: " + vacation_stop_date_in_millis);


            Calendar calendar_end_of_month = Calendar.getInstance();
            calendar_end_of_month.set(year, month - 1, days);
            long month_end_date_in_millis = calendar_end_of_month.getTimeInMillis();
            Log.d(TAG, "month stop millies: " + month_end_date_in_millis);

           /* if ((vacation_start_date_in_millis < month_start_date_in_millis && vacation_stop_date_in_millis > month_end_date_in_millis) ||
                    (vacation_start_date_in_millis < month_start_date_in_millis && vacation_stop_date_in_millis > month_start_date_in_millis && vacation_stop_date_in_millis < month_end_date_in_millis) ||
                    (vacation_start_date_in_millis > month_start_date_in_millis && vacation_start_date_in_millis < month_end_date_in_millis && (vacation_stop_date_in_millis > month_end_date_in_millis || vacation_stop_date_in_millis == month_end_date_in_millis) ) ||
                    (vacation_start_date_in_millis > month_start_date_in_millis && vacation_start_date_in_millis < month_end_date_in_millis && vacation_stop_date_in_millis > month_start_date_in_millis && vacation_stop_date_in_millis < month_end_date_in_millis) ||
                    (vacation_start_date_in_millis == month_start_date_in_millis && vacation_stop_date_in_millis == month_end_date_in_millis)) {

                vacationArrayList.add(vacation);

            }*/


            if (((vacation_start_date_in_millis < month_start_date_in_millis || vacation_start_date_in_millis == month_start_date_in_millis) && (vacation_stop_date_in_millis > month_end_date_in_millis || vacation_stop_date_in_millis == month_end_date_in_millis)) ||
                    ((vacation_start_date_in_millis < month_start_date_in_millis || vacation_start_date_in_millis == month_start_date_in_millis) && (vacation_stop_date_in_millis > month_start_date_in_millis || vacation_stop_date_in_millis == month_start_date_in_millis) && (vacation_stop_date_in_millis < month_end_date_in_millis || vacation_stop_date_in_millis == month_end_date_in_millis)) ||
                    ((vacation_start_date_in_millis > month_start_date_in_millis || vacation_start_date_in_millis == month_start_date_in_millis) && (vacation_start_date_in_millis < month_end_date_in_millis || vacation_start_date_in_millis == month_end_date_in_millis) && (vacation_stop_date_in_millis > month_end_date_in_millis || vacation_stop_date_in_millis == month_end_date_in_millis)) ||
                    ((vacation_start_date_in_millis > month_start_date_in_millis || vacation_start_date_in_millis == month_start_date_in_millis) && (vacation_start_date_in_millis < month_end_date_in_millis || vacation_start_date_in_millis == month_end_date_in_millis) && (vacation_stop_date_in_millis > month_start_date_in_millis || vacation_stop_date_in_millis == month_start_date_in_millis) && (vacation_stop_date_in_millis < month_end_date_in_millis || vacation_stop_date_in_millis == month_end_date_in_millis)) ||
                    (vacation_start_date_in_millis == month_start_date_in_millis && vacation_stop_date_in_millis == month_end_date_in_millis)) {

                vacationArrayList.add(vacation);

            } else {
                Log.d(TAG, "vacation match unsuccessful");
            }

        }

        return vacationArrayList;

    }

    private void nextMonthData(Object object) {

        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 3) {

            progressDialog.dismiss();
            fetchNextMonthDataAndPopulateIt(object, 3);

        }


        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 2) {
            progressDialog.dismiss();
            fetchNextMonthDataAndPopulateIt(object, 2);

        }

    }

    private void fetchNextMonthDataAndPopulateIt(Object object, int user_group) {
        try {
            JSONObject jsonObject = new JSONObject((String) object);
            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
            JSONArray jsonArray_mentor = jsonObject_data.getJSONArray("mentor");
            JSONArray jsonArray_data = jsonObject_data.getJSONArray("slots");


            List<Slot> slots = new ArrayList<Slot>();
            List<Vacation> vacations = new ArrayList<Vacation>();  /* list of non coinciding vacations*/


            parseSlots(slots, jsonArray_data, user_group);
            if (user_group == 3) {
                JSONArray jsonArray_vacation_non_coinciding = jsonObject_data.getJSONArray("vacations");
                parseVacation(vacations, jsonArray_vacation_non_coinciding);

            }

            previousMonthArrayList = currentMonthArrayList;
            currentMonthArrayList = comingMonthArrayList;
            comingMonthArrayList = null;
            comingMonthArrayList = new ArrayList<Slot>();
            comingMonthArrayList = (ArrayList<Slot>) slots;

            previousMonthNonCoincidingVacation = currentMonthNonCoincidingVacation;
            currentMonthNonCoincidingVacation = comingMonthNonCoincidingVacation;
            comingMonthNonCoincidingVacation = null;
            comingMonthNonCoincidingVacation = new ArrayList<Vacation>();
            comingMonthNonCoincidingVacation = (ArrayList<Vacation>) vacations;


            previousMonthYearInfo = currentMonthYearInfo;
            currentMonthYearInfo = comingMonthYearInfo;
            comingMonthYearInfo = null;
            comingMonthYearInfo = new ArrayList<MonthYearInfo>();
            comingMonthYearInfo = getMonthYearForThis(Integer.parseInt(next_month_requested_date.split("-")[1]), Integer.parseInt(next_month_requested_date.split("-")[0]), finalizeDaysInMonth(Integer.parseInt(next_month_requested_date.split("-")[1]), Integer.parseInt(next_month_requested_date.split("-")[0])));


            previousMonthMentorInfos = currentMonthMentorInfos;
            currentMonthMentorInfos = comingMonthMentorInfos;
            comingMonthMentorInfos = null;
            comingMonthMentorInfos = new ArrayList<MentorInfo>();
            comingMonthMentorInfos = getMentorInfo(jsonArray_mentor);
            Log.d(TAG, "comingMonthArrayList size" + comingMonthArrayList.size());


            Log.d(TAG, "prev month arrayList size" + previousMonthArrayList.size() + ", current month arrayList size: " + currentMonthArrayList.size() + ", coming month arrayList size " + comingMonthArrayList.size());

            adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos);
            _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
            currentMonth.setText(DateFormat.format(dateTemplate,
                    _calendar.getTime()));
            calendarView.setAdapter(adapter1);
            adapter1.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void previousMonthData(Object object) {

        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 3) {
            progressDialog.dismiss();
            fetchPreviousMonthDataAndPopulateIt(object, 3);

        }

        if (Integer.parseInt(StorageHelper.getUserGroup(getActivity(), "user_group")) == 2) {
            progressDialog.dismiss();
            fetchPreviousMonthDataAndPopulateIt(object, 2);

        }
    }

    private void fetchPreviousMonthDataAndPopulateIt(Object object, int user_group) {
        try {
            JSONObject jsonObject = new JSONObject((String) object);
            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
            JSONArray jsonArray_mentor = jsonObject_data.getJSONArray("mentor");
            JSONArray jsonArray_data = jsonObject_data.getJSONArray("slots");


            List<Slot> slots = new ArrayList<Slot>();
            List<Vacation> vacations = new ArrayList<Vacation>();  /* list of non coinciding vacations*/


            parseSlots(slots, jsonArray_data, user_group);

            if (user_group == 3) {
                JSONArray jsonArray_vacation_non_coinciding = jsonObject_data.getJSONArray("vacations");
                parseVacation(vacations, jsonArray_vacation_non_coinciding);

            }


            comingMonthArrayList = currentMonthArrayList;
            currentMonthArrayList = previousMonthArrayList;
            previousMonthArrayList = null;
            previousMonthArrayList = new ArrayList<Slot>();
            previousMonthArrayList = (ArrayList<Slot>) slots;

            comingMonthNonCoincidingVacation = currentMonthNonCoincidingVacation;
            currentMonthNonCoincidingVacation = previousMonthNonCoincidingVacation;
            previousMonthNonCoincidingVacation = null;
            previousMonthNonCoincidingVacation = new ArrayList<Vacation>();
            previousMonthNonCoincidingVacation = (ArrayList<Vacation>) vacations;

            comingMonthYearInfo = currentMonthYearInfo;
            currentMonthYearInfo = previousMonthYearInfo;
            previousMonthYearInfo = null;
            previousMonthYearInfo = new ArrayList<MonthYearInfo>();
            previousMonthYearInfo = getMonthYearForThis(Integer.parseInt(prev_month_requested_date.split("-")[1]), Integer.parseInt(prev_month_requested_date.split("-")[0]), finalizeDaysInMonth(Integer.parseInt(prev_month_requested_date.split("-")[1]), Integer.parseInt(prev_month_requested_date.split("-")[0])));

            comingMonthMentorInfos = currentMonthMentorInfos;
            currentMonthMentorInfos = previousMonthMentorInfos;
            previousMonthMentorInfos = null;
            previousMonthMentorInfos = new ArrayList<MentorInfo>();
            previousMonthMentorInfos = getMentorInfo(jsonArray_mentor);


            adapter1 = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos);
            _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
            currentMonth.setText(DateFormat.format(dateTemplate,
                    _calendar.getTime()));
            calendarView.setAdapter(adapter1);
            adapter1.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }


}
