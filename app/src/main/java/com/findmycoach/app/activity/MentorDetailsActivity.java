package com.findmycoach.app.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.CalendarGridAdapter;
import com.findmycoach.app.beans.CalendarSchedule.Day;
import com.findmycoach.app.beans.CalendarSchedule.DayEvent;
import com.findmycoach.app.beans.CalendarSchedule.DaySlot;
import com.findmycoach.app.beans.CalendarSchedule.DayVacation;
import com.findmycoach.app.beans.CalendarSchedule.Event;
import com.findmycoach.app.beans.CalendarSchedule.EventDuration;
import com.findmycoach.app.beans.CalendarSchedule.Mentee;
import com.findmycoach.app.beans.CalendarSchedule.MentorInfo;
import com.findmycoach.app.beans.CalendarSchedule.MonthYearInfo;
import com.findmycoach.app.beans.CalendarSchedule.Slot;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;
import com.findmycoach.app.beans.mentor.Data;
import com.findmycoach.app.beans.mentor.Response;
import com.findmycoach.app.fragment.CustomDatePickerFragment;
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.ScrollableGridView;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MentorDetailsActivity extends FragmentActivity implements Callback {

    private ImageView profileImage;
    private TextView profileName;
    private TextView profileAddress;
    private RatingBar profileRatting;
    private TextView profileCharges;
    private TextView profileEmail;
    private TextView profileExperience;
    private TextView profileQualification;
    private TextView profileTravelAvailable;
    private TextView profilePhone;
    private TextView profileDob;
    private LinearLayout areaOfCoaching;
    private Data userInfo = null;
    private String connectionStatus;


    private TextView tv_currentMonth;
    private ImageView iv_prevMonth;
    private ImageView iv_nextMonth;
    private ScrollableGridView calendarView;
    private CalendarGridAdapter adapter1;
    private Calendar _calendar;
    public static int month, year;
    private static final String dateTemplate = "MMMM yyyy";
    public static MentorDetailsActivity mentorDetailsActivity;
    private int days_in_current_month, days_in_prev_month, days_in_next_month;
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
    public boolean b_three_months_data;
    public static int month_from_dialog, year_from_dialog;
    private String charges;
    public boolean populate_calendar_from_adapter;

    private static final String TAG = "FMC";
    private ArrayList<String> array_list_subCategory = null;
    private String previous_month_start_date;/* this will get initialized when api is requested for three months (previous, current, coming)*/
    private String next_month_requested_date;
    private String prev_month_requested_date;
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_details);
        Log.d(TAG, "inside mentor details acitivity");
        array_list_subCategory = new ArrayList<String>();
        initialize();
        mentorDetailsActivity = this;
        Log.d(TAG, "connection status : " + connectionStatus);
        applyActionbarProperties();
        populateFields();

        month_from_dialog = 0;
        year_from_dialog = 0;
        startPointForCalendar();
        b_three_months_data = false;

        initializeCalendar();

    }

    private void initializeCalendar() {
        iv_prevMonth = (ImageView) findViewById(R.id.iv_prevMonth);
        iv_prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "previous month clicked");
                showPrevMonth();
            }
        });

        tv_currentMonth = (TextView) findViewById(R.id.tv_currentMonth);
        tv_currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
        tv_currentMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                CustomDatePickerFragment customDatePickerFragment = new CustomDatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("for", "MentorDetailsActivity");
                customDatePickerFragment.setArguments(bundle);

                customDatePickerFragment.show(fragmentManager, null);
                month_from_dialog = 0;
                year_from_dialog = 0;
            }
        });

        iv_nextMonth = (ImageView) findViewById(R.id.iv_nextMonth);
        iv_nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextMonth();
            }
        });

        calendarView = (ScrollableGridView) findViewById(R.id.calendar_mentor_availability);
        getCalendarDetailsAPICall();    /* API call for 3 months data */


    }

    public void getCalendarDetailsAPICall() {
        Log.d(TAG, "state 1");
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

            } else {
                startPointForCalendar();
            }
        } else {
            month = month_from_dialog;
            year = year_from_dialog;
            tv_currentMonth.setText(getResources().getStringArray(R.array.months)[month - 1] + " " + year);
        }


        progressDialog = new ProgressDialog(MentorDetailsActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        RequestParams requestParams = new RequestParams();
        requestParams.add("user_group", String.valueOf("3"));
        requestParams.add("mentor_id", userInfo.getId());
        Log.d(TAG, "mentor_id : " + userInfo.getId());

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

        //Toast.makeText(getActivity(), getResources().getString(R.string.start_date1) + String.valueOf(stringBuilder), Toast.LENGTH_SHORT).show();
        requestParams.add("start_date", String.valueOf(stringBuilder));
        previous_month_start_date = String.valueOf(stringBuilder);
        requestParams.add("limit", String.valueOf(days_in_prev_month + days_in_current_month + days_in_next_month));
        Log.d(TAG, "state 2");

        networkCall1(requestParams);


    }

    void networkCall1(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.getCalendarDetails(MentorDetailsActivity.this, requestParams, StorageHelper.getUserDetails(MentorDetailsActivity.this, "auth_token"), this, 37); /* Network operation for getting details for three months */
        Log.d(TAG, "FMC auth token :" + StorageHelper.getUserDetails(MentorDetailsActivity.this, "auth_token"));

    }


    public void showPrevMonth() {
        newPreviousMonth();
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


        RequestParams requestParams = new RequestParams();
        requestParams.add("user_group", String.valueOf("3"));
        requestParams.add("mentor_id", userInfo.getId());
        requestParams.add("start_date", String.valueOf(stringBuilder));
        prev_month_requested_date = String.valueOf(stringBuilder);
        requestParams.add("limit", String.valueOf(days_in_new_prev_month));
        networkCall2(requestParams);


    }

    /* Network call for getting previous to previous month data in case of mentor login*/
    void networkCall2(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.getCalendarDetails(this, requestParams, StorageHelper.getUserDetails(this, "auth_token"), this, 39);
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


        RequestParams requestParams = new RequestParams();
        requestParams.add("user_group", String.valueOf("3"));
        requestParams.add("mentor_id", userInfo.getId());
        requestParams.add("start_date", String.valueOf(stringBuilder));
        next_month_requested_date = String.valueOf(stringBuilder);
        requestParams.add("limit", String.valueOf(days_in_new_next_month));
        networkCall3(requestParams);


    }

    /* Network call for getting next to next month data in case of mentor login*/
    void networkCall3(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.getCalendarDetails(this, requestParams, StorageHelper.getUserDetails(this, "auth_token"), this, 38);
    }


    /* Get Calendar current instance*/
    void startPointForCalendar() {
        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;   /* current month*/
        year = _calendar.get(Calendar.YEAR);         /* current year */

    }


    private void applyActionbarProperties() {
//        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle(userInfo.getFirstName());
//        }
    }

    private void initialize() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        String jsonData = getIntent().getStringExtra("mentorDetails");
        connectionStatus = getIntent().getStringExtra("connection_status");
        Log.d(TAG, "connection status : 2 " + connectionStatus);
        if (connectionStatus == null)
            connectionStatus = "not connected";
        if (connectionStatus.equals("broken"))
            connectionStatus = "not connected";
        Log.d(TAG, "json data :" + jsonData);
        Response mentorDetails = new Gson().fromJson(jsonData, Response.class);
        userInfo = mentorDetails.getData();

        String searchedKeyWord = getIntent().getStringExtra("searched_keyword");
        if (searchedKeyWord != null && !searchedKeyWord.equals("-1")) {
            searchedKeyWord = DataBase.singleton(this).getSubCategory(searchedKeyWord);
            List<String> newSubCategory = new ArrayList<String>();
            newSubCategory.add(searchedKeyWord);
            userInfo.setSubCategoryName(newSubCategory);
        }

        array_list_subCategory = (ArrayList<String>) userInfo.getSubCategoryName();

        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileName = (TextView) findViewById(R.id.profile_name);
        profileAddress = (TextView) findViewById(R.id.profile_address);
        profileExperience = (TextView) findViewById(R.id.profile_experience);
        profileRatting = (RatingBar) findViewById(R.id.profile_rating);
        profileQualification = (TextView) findViewById(R.id.profile_accomplishment);
        profileCharges = (TextView) findViewById(R.id.profile_charges);
        profileTravelAvailable = (TextView) findViewById(R.id.profile_travel_available);
        areaOfCoaching = (LinearLayout) findViewById(R.id.areas_of_coaching);
        profilePhone = (TextView) findViewById(R.id.profile_phone);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileDob = (TextView) findViewById(R.id.profile_dob);

        tv_currentMonth = (TextView) findViewById(R.id.tv_currentMonth);
        iv_nextMonth = (ImageView) findViewById(R.id.iv_nextMonth);
        iv_prevMonth = (ImageView) findViewById(R.id.iv_prevMonth);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.title_mentor_details));

    }


    private void populateFields() {
        profileName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
        try {
            profileEmail.setText(userInfo.getEmail());
        } catch (Exception e) {
        }
        String address = "";
        if (userInfo.getAddress() != null) {
            address = address + userInfo.getAddress() + ", ";
        }
        if (userInfo.getCity() != null) {
            address = address + userInfo.getCity() + ", ";
        }
        if (userInfo.getState() != null) {
            address = address + userInfo.getState() + ", ";
        }
        if (userInfo.getZip() != null) {
            address = address + userInfo.getZip();
        }
        try {
            profileDob.setText((String) userInfo.getDob());
        } catch (Exception e) {
        }
        if (userInfo.getExperience() != null) {
            profileExperience.setText(userInfo.getExperience() + " year(s)");
        }
        if (userInfo.getAccomplishments() != null) {
            profileQualification.setText(userInfo.getAccomplishments());
        }
        profileAddress.setText(address);
        if (userInfo.getCharges() != null) {
            charges = (userInfo.getCharges().equals("0") ? userInfo.getCharges() + " per hour" : userInfo.getCharges() + " per hour");

            Log.d(TAG, "Charges amount : " + charges.split("per", 2)[0] + "charges unit : " + charges.split("per", 2)[1]);
            profileCharges.setText("\u20B9 " + charges);
        }
        try {
            profileRatting.setRating(Float.parseFloat(userInfo.getRating()));
        } catch (Exception e) {
            profileRatting.setRating(0f);
        }
        LayerDrawable stars = (LayerDrawable) profileRatting.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.purple_light), PorterDuff.Mode.SRC_ATOP);
        if (userInfo.getAvailabilityYn() != null && userInfo.getAvailabilityYn().equals("1")) {
            profileTravelAvailable.setText(getResources().getString(R.string.yes));
        } else {
            profileTravelAvailable.setText(getResources().getString(R.string.no));
        }
        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
            ImageLoader imgLoader = new ImageLoader(profileImage);
            imgLoader.execute((String) userInfo.getPhotograph());
        }

        List<String> areaOfInterests = userInfo.getSubCategoryName();
        if (areaOfInterests.size() > 0 && areaOfInterests.get(0) != null && !areaOfInterests.get(0).trim().equals("")) {
            List<Button> buttons = new ArrayList<>();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (String areaOfInterest : areaOfInterests) {
                Button button = (Button) inflater.inflate(R.layout.button, null);
                button.setText(areaOfInterest);
                buttons.add(button);
            }
            populateViews(areaOfCoaching, buttons, this);
        }
        profilePhone.setText(userInfo.getPhonenumber());
    }

    private void populateViews(LinearLayout linearLayout, List<Button> views, Context context) {

        Display display = getWindowManager().getDefaultDisplay();
        linearLayout.removeAllViews();
        int maxWidth = display.getWidth() - 40;

        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(context);
        newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        newLL.setOrientation(LinearLayout.HORIZONTAL);
        newLL.setGravity(Gravity.CENTER_HORIZONTAL);

        int widthSoFar = 0;

        for (Button view : views) {
            LinearLayout LL = new LinearLayout(context);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            LL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(0, 0);
            params = new LinearLayout.LayoutParams(view.getMeasuredWidth(), profileEmail.getHeight());
            params.setMargins(2, 2, 2, 2);

            LL.addView(view, params);
            LL.measure(0, 0);
            widthSoFar += view.getMeasuredWidth();
            if (widthSoFar >= maxWidth) {
                linearLayout.addView(newLL);

                newLL = new LinearLayout(context);
                newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, profileEmail.getHeight()));
                newLL.setOrientation(LinearLayout.HORIZONTAL);
                newLL.setGravity(Gravity.CENTER_HORIZONTAL);
                params = new LinearLayout.LayoutParams(LL.getMeasuredWidth(), LL.getMeasuredHeight());
                newLL.addView(LL, params);
                widthSoFar = LL.getMeasuredWidth();
            } else {
                newLL.addView(LL);
            }
        }
        linearLayout.addView(newLL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (connectionStatus.equals("not connected")) {
            getMenuInflater().inflate(R.menu.menu_mentor_details_not_connected, menu);
        } else if (connectionStatus.equals("accepted")) {
            getMenuInflater().inflate(R.menu.menu_connected, menu);
            menu.add(0, Menu.FIRST, Menu.NONE, R.string.rate);
        }
        if (connectionStatus.equals("pending")) {
            getMenuInflater().inflate(R.menu.menu_mentor_details_pending, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, userInfo.getConnectionStatus() + " : " + userInfo.getConnectionId());
        if (id == R.id.action_connect) {
            showAlert();
        } else if (id == R.id.action_disconnect) {
            disconnect(userInfo.getConnectionId(), userInfo.getId());
        }
        if (id == android.R.id.home) {
            finish();
        }
        if (id == Menu.FIRST) {
            showRatingDialog();
        }
        return true;
    }

    private void disconnect(String connectionId, String oppositeUSerId) {
        progressDialog.show();
        Log.d(TAG, "id : " + connectionId + ", user_id : " + oppositeUSerId +
                ", user_group : " + DashboardActivity.dashboardActivity.user_group);
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", connectionId);
        requestParams.add("user_id", oppositeUSerId);
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.breakConnection(this, requestParams, this, 21);
    }

    /**
     * Dialog to rate mentor
     */
    private void showRatingDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(getResources().getString(R.string.rate) + " " + userInfo.getFirstName());
        dialog.setContentView(R.layout.dialog_rate_mentor);

        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);

        dialog.findViewById(R.id.submitRating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MentorDetailsActivity.this, getResources().getString(R.string.rating_for) + userInfo.getFirstName() + getResources().getString(R.string.is) + ratingBar.getRating() + getResources().getString(R.string.will_be_submitted), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showAlert() {
        final String defaultMessage = getResources().getString(R.string.connection_request_msg);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.connection_request));
        alertDialog.setMessage(getResources().getString(R.string.enter_msg));
        final EditText input = new EditText(this);
        input.setHint(defaultMessage);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(8, 8, 8, 8);
        input.setLayoutParams(params);
        alertDialog.setView(input);
        input.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittext));
        alertDialog.setPositiveButton(getResources().getString(R.string.send),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String message = input.getText().toString();
                        if (message.trim().length() < 1)
                            message = defaultMessage;
                        sendConnectionRequest(message);
                    }
                }
        );

        alertDialog.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void sendConnectionRequest(String message) {
        progressDialog.show();
        String studentId = StorageHelper.getUserDetails(this, getResources().getString(R.string.user_id));
        Log.d(TAG, "\n" + message + "\nMentor id : " + userInfo.getId() + "\nStudent id : " + studentId);
        RequestParams requestParams = new RequestParams();
        requestParams.add("owner", studentId);
        requestParams.add("invitee", userInfo.getId());
        requestParams.add("message", message);
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.sendConnectionRequest(this, requestParams, this, 17);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        if (calledApiValue == 21 || calledApiValue == 17) {
            Intent intent = new Intent();
            intent.putExtra("status", "close_activity");
            String status = object + "";
            try {
                intent.putExtra("connectionId", Integer.parseInt(status) + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            intent.putExtra("connectionStatus", calledApiValue == 17 ? "pending" : "broken");
            setResult(RESULT_OK, intent);
            finish();
            Toast.makeText(getApplicationContext(), (String) object, Toast.LENGTH_LONG).show();
        }

        switch (calledApiValue) {
            case 37:
                Log.d(TAG, " API 37 success");
                Log.d(TAG, "call for api 37");
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
        }

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        switch (calledApiValue) {
            case 37:
                Log.d(TAG, " API 37 success");
                Toast.makeText(MentorDetailsActivity.this, (String) object, Toast.LENGTH_SHORT).show();
                updateArrayListForThreeMonths();
                updateCalendarOnFailure();
                break;
            case 38:
                Log.d(TAG, " API 38 success");
                updateMonthAndYearOnNextMonthClick();
                updateArrayListsForNextMonth();
                Toast.makeText(MentorDetailsActivity.this, (String) object, Toast.LENGTH_SHORT).show();
                updateCalendarOnFailure();
                break;
            case 39:
                Log.d(TAG, " API 39 success");
                updateMonthAndYearOnPreviousMonthClick();
                updateArrayListsForPreviousMonth();
                Toast.makeText(MentorDetailsActivity.this, (String) object, Toast.LENGTH_SHORT).show();
                updateCalendarOnFailure();
                break;
        }

        //Toast.makeText(getApplicationContext(), (String) object, Toast.LENGTH_LONG).show();
    }

    private void updateArrayListForThreeMonths() {
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


    }

    public void updateArrayListsForNextMonth() {
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


    }

    public void updateArrayListsForPreviousMonth() {
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


    }


    private void updateCalendarOnFailure() {

        adapter1 = new CalendarGridAdapter(MentorDetailsActivity.this, month, year, this);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        tv_currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));

        calendarView.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
        progressDialog.dismiss();
    }


    private void threeMonthsData(Object object) {


        Log.d(TAG, "inside three months data population");
        Log.d(TAG, "INside threeMonthData method ");
        progressDialog.dismiss();
        try {

            JSONObject jsonObject = new JSONObject((String) object);
            JSONObject jsonObject_data =jsonObject.getJSONObject("data");
            JSONArray jsonArray_mentor = jsonObject_data.getJSONArray("mentor");
            JSONArray jsonArray_data = jsonObject_data.getJSONArray("slots");
            JSONArray jsonArray_vacation_non_coinciding = jsonObject_data.getJSONArray("vacations");



            //  Log.d(TAG, "json array size : " + jsonArray_data.length());
            // Log.d(TAG, "Object got for three months data " + jsonObject.toString());

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
            previousMonthNonCoincidingVacation = getVacationsForThis(vacations, previous_month, previous_month_year, finalizeDaysInMonth(previous_month, previous_month_year));
            currentMonthNonCoincidingVacation = getVacationsForThis(vacations, current_month, current_year, finalizeDaysInMonth(current_month, current_year));
            comingMonthNonCoincidingVacation = getVacationsForThis(vacations, coming_month, coming_year, finalizeDaysInMonth(coming_month, coming_year));
            previousMonthYearInfo = getMonthYearForThis(previous_month, previous_month_year, finalizeDaysInMonth(previous_month, previous_month_year));
            currentMonthYearInfo = getMonthYearForThis(current_month, current_year, finalizeDaysInMonth(current_month, current_year));
            comingMonthYearInfo = getMonthYearForThis(coming_month, coming_year, finalizeDaysInMonth(coming_month, coming_year));
            previousMonthMentorInfos =getMentorInfo(jsonArray_mentor);
            currentMonthMentorInfos =getMentorInfo(jsonArray_mentor);
            comingMonthMentorInfos =getMentorInfo(jsonArray_mentor);
            Log.d(TAG, "Mentors slots info for mentee,  previousMonthArrayList size :" + previousMonthArrayList.size() + "currentMonthArrayList size :" + currentMonthArrayList.size() + ", comingMonthArrayList size :" + comingMonthArrayList.size());
            if (b_three_months_data) {   /*  program will come in this scope when user selects date from dialog i.e. user randomly selects a year and month */
                Log.d(TAG, "Three months data get changed");
                adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos, userInfo.getId(), userInfo.getAvailabilityYn(), charges, array_list_subCategory, connectionStatus);
                calendarView.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
                if (month_from_dialog == 0 && year_from_dialog == 0) {
                    tv_currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
                }

            } else {
                Log.d(TAG, "three months data population");
                adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos,currentMonthMentorInfos,comingMonthMentorInfos,userInfo.getId(), userInfo.getAvailabilityYn(), charges, array_list_subCategory, connectionStatus);
                calendarView.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
            }


            b_three_months_data = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private ArrayList<MentorInfo> getMentorInfo(JSONArray jsonArray_mentor) {
        ArrayList<MentorInfo>  mentorInfos = new ArrayList<MentorInfo>();
        for(int array_index =0; array_index < jsonArray_mentor.length() ; array_index++){
            try {
                JSONObject jsonObject = jsonArray_mentor.getJSONObject(array_index);
                MentorInfo mentorInfo =new MentorInfo();
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

    private void parseVacation(List<Vacation> vacations, JSONArray jsonArray_vacation_non_coinciding) {
        for (int vacation_index = 0; vacation_index < jsonArray_vacation_non_coinciding.length(); vacation_index++) {
            try {
                Vacation vacation = new Vacation();
                JSONObject vacation_jsonObject = jsonArray_vacation_non_coinciding.getJSONObject(vacation_index);
                vacation.setVacation_id(vacation_jsonObject.getString("vacation_id"));
                vacation.setStart_date(vacation_jsonObject.getString("start_date"));
                vacation.setStop_date(vacation_jsonObject.getString("stop_date"));
                vacation.setCause_of_the_vacation(vacation_jsonObject.getString("cause_of_the_vacation"));
                JSONArray vacation_weekdays = vacation_jsonObject.getJSONArray("weekdays");
                String vacation_weekdays_array[] = new String[vacation_weekdays.length()];
                for (int week_day = 0; week_day < vacation_weekdays.length(); week_day++) {
                    vacation_weekdays_array[week_day] = vacation_weekdays.getString(week_day);   /* week_day is used to pass index */
                }
                vacation.setWeek_days(vacation_weekdays_array);
                vacation.setStart_time(vacation_jsonObject.getString("start_time"));
                vacation.setStop_time(vacation_jsonObject.getString("stop_time"));
                vacation.setVacation_made_at_network_success("true");
                vacations.add(vacation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<Vacation> getVacationsForThis(List<Vacation> vacations, int month, int year, int days) {
        ArrayList<Vacation> vacationArrayList = new ArrayList<Vacation>();

        Calendar calendar_start_of_month = Calendar.getInstance();
        calendar_start_of_month.set(year, month - 1, 1);
        long month_start_date_in_millis = calendar_start_of_month.getTimeInMillis();

        Calendar calendar_end_of_month = Calendar.getInstance();
        calendar_end_of_month.set(year, month - 1, days);
        long month_end_date_in_millis = calendar_end_of_month.getTimeInMillis();


        for (int vacation_no = 0; vacation_no < vacations.size(); vacation_no++) {
            Vacation vacation = vacations.get(vacation_no);
            String start_date = vacation.getStart_date();
            String stop_date = vacation.getStop_date();

            Calendar calendar_vacation_start_date = Calendar.getInstance();
            calendar_vacation_start_date.set(Integer.parseInt(start_date.split("-")[0]), Integer.parseInt(start_date.split("-")[1]) - 1, Integer.parseInt(start_date.split("-")[2]));
            long vacation_start_date_in_millis = calendar_vacation_start_date.getTimeInMillis();
            Calendar calendar_vacation_end_date = Calendar.getInstance();
            calendar_vacation_end_date.set(Integer.parseInt(stop_date.split("-")[0]), Integer.parseInt(stop_date.split("-")[1]) - 1, Integer.parseInt(stop_date.split("-")[2]));
            long vacation_stop_date_in_millis = calendar_vacation_end_date.getTimeInMillis();


            if ((vacation_start_date_in_millis < month_start_date_in_millis && vacation_stop_date_in_millis > month_end_date_in_millis) ||
                    (vacation_start_date_in_millis < month_start_date_in_millis && vacation_stop_date_in_millis > month_start_date_in_millis && vacation_stop_date_in_millis < month_end_date_in_millis) ||
                    (vacation_start_date_in_millis > month_start_date_in_millis && vacation_start_date_in_millis < month_end_date_in_millis && vacation_stop_date_in_millis > month_end_date_in_millis) ||
                    (vacation_start_date_in_millis > month_start_date_in_millis && vacation_start_date_in_millis < month_end_date_in_millis && vacation_stop_date_in_millis > month_start_date_in_millis && vacation_stop_date_in_millis < month_end_date_in_millis) ||
                    (vacation_start_date_in_millis == month_start_date_in_millis && vacation_stop_date_in_millis == month_end_date_in_millis)) {

                vacationArrayList.add(vacation);

            }
        }

        return vacationArrayList;

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

    private ArrayList<Slot> getSlotsForThis(List<Slot> slots, int month, int year, int days) {
        ArrayList<Slot> slotArrayList = new ArrayList<Slot>();

        Calendar calendar_start_of_month = Calendar.getInstance();
        calendar_start_of_month.set(year, month - 1, 1);
        long month_start_date_in_millis = calendar_start_of_month.getTimeInMillis();

        Calendar calendar_end_of_month = Calendar.getInstance();
        calendar_end_of_month.set(year, month - 1, days);
        long month_end_date_in_millis = calendar_end_of_month.getTimeInMillis();


        for (int slot_no = 0; slot_no < slots.size(); slot_no++) {
            Slot slot = slots.get(slot_no);
            String start_date = slot.getSlot_start_date();
            String stop_date = slot.getSlot_stop_date();

            Calendar calendar_slot_start_date = Calendar.getInstance();
            calendar_slot_start_date.set(Integer.parseInt(start_date.split("-")[0]), Integer.parseInt(start_date.split("-")[1]) - 1, Integer.parseInt(start_date.split("-")[2]));
            long slot_start_date_in_millis = calendar_slot_start_date.getTimeInMillis();
            Calendar calendar_slot_end_date = Calendar.getInstance();
            calendar_slot_end_date.set(Integer.parseInt(stop_date.split("-")[0]), Integer.parseInt(stop_date.split("-")[1]) - 1, Integer.parseInt(stop_date.split("-")[2]));
            long slot_stop_date_in_millis = calendar_slot_end_date.getTimeInMillis();


            if ((slot_start_date_in_millis < month_start_date_in_millis && slot_stop_date_in_millis > month_end_date_in_millis) ||
                    (slot_start_date_in_millis < month_start_date_in_millis && slot_stop_date_in_millis > month_start_date_in_millis && slot_stop_date_in_millis < month_end_date_in_millis) ||
                    (slot_start_date_in_millis > month_start_date_in_millis && slot_start_date_in_millis < month_end_date_in_millis && slot_stop_date_in_millis > month_end_date_in_millis) ||
                    (slot_start_date_in_millis > month_start_date_in_millis && slot_start_date_in_millis < month_end_date_in_millis && slot_stop_date_in_millis > month_start_date_in_millis && slot_stop_date_in_millis < month_end_date_in_millis) ||
                    (slot_start_date_in_millis == month_start_date_in_millis && slot_stop_date_in_millis == month_end_date_in_millis)) {

                slotArrayList.add(slot);

            }
        }

        return slotArrayList;
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

    private boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }

    private void parseSlots(List<Slot> slots, JSONArray jsonArray_data, int user_group) {
        for (int json_Array_data_index = 0; json_Array_data_index < jsonArray_data.length(); json_Array_data_index++) {
            try {
                Slot slot = new Slot();
                List<Event> events = new ArrayList<Event>();
                List<Vacation> vacations = new ArrayList<Vacation>();

                JSONObject slot_jsonObject = jsonArray_data.getJSONObject(json_Array_data_index);
                slot.setSlot_id(slot_jsonObject.getString("slot_id"));
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
                    JSONArray event_mentees=event_jsonObject.getJSONArray("mentee");
                    List<Mentee> mentees=new ArrayList<Mentee>();
                    for(int mentee_index=0; mentee_index < event_mentees.length(); mentee_index++ ){
                        Mentee mentee=new Mentee();
                        JSONObject mentee_jsonObject=event_mentees.getJSONObject(mentee_index);
                        JSONArray event_durations = mentee_jsonObject.getJSONArray("durations");
                        List<EventDuration> eventDurations =new ArrayList<EventDuration>();
                        for(int event_duration_no = 0 ; event_duration_no < event_durations.length(); event_duration_no++){
                            JSONObject jsonObject_event_duration = event_durations.getJSONObject(event_duration_no);
                            EventDuration eventDuration = new EventDuration();
                            eventDuration.setStart_date(jsonObject_event_duration.getString("start_date"));
                            eventDuration.setStop_date(jsonObject_event_duration.getString("stop_date"));
                            eventDurations.add(eventDuration);
                        }

                        mentee.setEventDurations(eventDurations);
                        mentee.setFirst_name(mentee_jsonObject.getString("first_name"));
                        mentee.setLast_name(mentee_jsonObject.getString("last_name"));
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
                    JSONArray vacation_weekdays = vacation_jsonObject.getJSONArray("weekdays");
                    String vacation_weekdays_array[] = new String[vacation_weekdays.length()];
                    for (int week_day = 0; week_day < vacation_weekdays.length(); week_day++) {
                        vacation_weekdays_array[week_day] = vacation_weekdays.getString(week_day);   /* week_day is used to pass index */
                    }
                    vacation.setWeek_days(vacation_weekdays_array);
                    vacation.setStart_time(vacation_jsonObject.getString("start_time"));
                    vacation.setStop_time(vacation_jsonObject.getString("stop_time"));
                    vacations.add(vacation);
                }
                slot.setVacations(vacations);
                slot.setSlot_created_on_network_success("true");

                slots.add(slot);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private void nextMonthData(Object object) {


        progressDialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject((String) object);
            JSONObject jsonObject_data =jsonObject.getJSONObject("data");
            JSONArray jsonArray_mentor = jsonObject_data.getJSONArray("mentor");
            JSONArray jsonArray_data = jsonObject_data.getJSONArray("slots");
            JSONArray jsonArray_vacation_non_coinciding = jsonObject_data.getJSONArray("vacations");

            List<Slot> slots = new ArrayList<Slot>();
            List<Vacation> vacations = new ArrayList<Vacation>();  /* list of non coinciding vacations*/

            parseSlots(slots, jsonArray_data, 3);
            parseVacation(vacations, jsonArray_vacation_non_coinciding);

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


            adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos, userInfo.getId(), userInfo.getAvailabilityYn(), charges, array_list_subCategory, connectionStatus);
            _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
            tv_currentMonth.setText(DateFormat.format(dateTemplate,
                    _calendar.getTime()));
            adapter1.notifyDataSetChanged();
            calendarView.setAdapter(adapter1);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void previousMonthData(Object object) {


        progressDialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject((String) object);
            JSONObject jsonObject_data =jsonObject.getJSONObject("data");
            JSONArray jsonArray_mentor = jsonObject_data.getJSONArray("mentor");
            JSONArray jsonArray_data = jsonObject_data.getJSONArray("slots");
            JSONArray jsonArray_vacation_non_coinciding = jsonObject_data.getJSONArray("vacations");


            List<Slot> slots = new ArrayList<Slot>();
            List<Vacation> vacations = new ArrayList<Vacation>();  /* list of non coinciding vacations*/

            parseSlots(slots, jsonArray_data, 3);
            parseVacation(vacations, jsonArray_vacation_non_coinciding);

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

            adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo,previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos, userInfo.getId(), userInfo.getAvailabilityYn(), charges, array_list_subCategory, connectionStatus);
            _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
            tv_currentMonth.setText(DateFormat.format(dateTemplate,
                    _calendar.getTime()));
            adapter1.notifyDataSetChanged();
            calendarView.setAdapter(adapter1);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
