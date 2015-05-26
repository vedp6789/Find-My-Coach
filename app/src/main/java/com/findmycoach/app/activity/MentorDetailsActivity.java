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
    public ArrayList<Day> previousMonthArrayList = null;
    public ArrayList<Day> currentMonthArrayList = null;
    public ArrayList<Day> comingMonthArrayList = null;
    public boolean b_three_months_data;
    public static int month_from_dialog, year_from_dialog;
    private String charges;
    public boolean populate_calendar_from_adapter;

    private static final String TAG = "FMC";
    private ArrayList<String> array_list_subCategory = null;

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
        previousMonthArrayList = new ArrayList<Day>();
        currentMonthArrayList = new ArrayList<Day>();
        comingMonthArrayList = new ArrayList<Day>();


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
        title.setText(getResources().getString(R.string.title_activity_mentor_details));

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

    public void updateArrayListsForNextMonth() {
        previousMonthArrayList = currentMonthArrayList;
        currentMonthArrayList = comingMonthArrayList;
        comingMonthArrayList = null;
        comingMonthArrayList = new ArrayList<Day>();
    }

    public void updateArrayListsForPreviousMonth() {
        comingMonthArrayList = currentMonthArrayList;
        currentMonthArrayList = previousMonthArrayList;
        previousMonthArrayList = null;
        previousMonthArrayList = new ArrayList<Day>();
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
            JSONArray jsonArray_data = jsonObject.getJSONArray("data");
            Log.d(TAG, "json array size : " + jsonArray_data.length());
            Log.d(TAG, "Object got for three months data " + jsonObject.toString());

            for (int i = 0; i < days_in_prev_month; i++) {
                Day day1 = new Day();
                JSONObject unique_day = jsonArray_data.getJSONObject(i);
                day1.setDate(unique_day.getString("date"));
                JSONArray jsonArray_of_events = unique_day.getJSONArray("object");
                List<DayEvent> dayEvents = new ArrayList<DayEvent>();
                JSONArray jsonArray_of_slots = unique_day.getJSONArray("slots");
                List<DaySlot> daySlots = new ArrayList<DaySlot>();
                JSONArray jsonArray_of_vacation = unique_day.getJSONArray("exceptions");
                List<DayVacation> dayVacations = new ArrayList<DayVacation>();

                if (jsonArray_of_slots.length() > 0) {
                    for (int s = 0; s < jsonArray_of_slots.length(); s++) {
                        JSONObject day_slot = jsonArray_of_slots.getJSONObject(s);
                        DaySlot daySlot = new DaySlot();
                        daySlot.setSlot_start_date(day_slot.getString("start_date"));
                        daySlot.setSlot_stop_date(day_slot.getString("stop_date"));
                        daySlot.setSlot_start_time(day_slot.getString("start_time"));
                        daySlot.setSlot_stop_time(day_slot.getString("stop_time"));
                        daySlot.setSlot_type(day_slot.getString("slot_type"));
                        daySlot.setSlot_max_users(day_slot.getString("max_users"));

                        JSONArray week_days_jsonArray = day_slot.getJSONArray("dates");
                        String[] dates = new String[week_days_jsonArray.length()];
                        for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                            dates[week_day] = week_days_jsonArray.getString(week_day);
                        }
                        daySlot.setSlot_week_days(dates);
                        daySlot.setSlot_id(day_slot.getString("id"));

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
                        dayEvent.setEvent_start_date(day_event.getString("start_date"));
                        dayEvent.setEvent_stop_date(day_event.getString("stop_date"));
                        dayEvent.setEvent_start_time(day_event.getString("start_time"));
                        dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                        String slot_type = day_event.getString("slot_type");
                        dayEvent.setEvent_type(slot_type);
                        dayEvent.setEvent_total_mentee(day_event.getString("number_of_users"));
                        if (!slot_type.equalsIgnoreCase("Group")) {
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                        }

                        dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                        dayEvent.setSlot_id(day_event.getString("slot_id"));
                        dayEvents.add(dayEvent);
                    }
                    day1.setDayEvents(dayEvents);
                } else {
                    day1.setDayEvents(dayEvents);
                }

                if (jsonArray_of_vacation.length() > 0) {
                    for (int vacation = 0; vacation < jsonArray_of_vacation.length(); vacation++) {
                        JSONObject day_vacation = jsonArray_of_vacation.getJSONObject(vacation);
                        DayVacation dayVacation = new DayVacation();
                        dayVacation.setStart_date(day_vacation.getString("start_date"));
                        dayVacation.setStop_date(day_vacation.getString("stop_date"));
                        dayVacation.setStart_time(day_vacation.getString("start_time"));
                        dayVacation.setStop_time(day_vacation.getString("stop_time"));

                        JSONArray jsonArray_week_days = day_vacation.getJSONArray("dates");
                        String[] dates = new String[jsonArray_week_days.length()];
                        for (int week_day = 0; week_day < jsonArray_week_days.length(); week_day++) {
                            dates[week_day] = jsonArray_week_days.getString(week_day);
                        }
                        dayVacation.setWeek_days(dates);
                        dayVacations.add(dayVacation);


                    }
                    day1.setDayVacations(dayVacations);
                } else {
                    day1.setDayVacations(dayVacations);
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
                JSONArray jsonArray_of_vacation = unique_day.getJSONArray("exceptions");
                List<DayVacation> dayVacations = new ArrayList<DayVacation>();

                if (jsonArray_of_slots.length() > 0) {
                    for (int s = 0; s < jsonArray_of_slots.length(); s++) {
                        JSONObject day_slot = jsonArray_of_slots.getJSONObject(s);
                        DaySlot daySlot = new DaySlot();
                        daySlot.setSlot_start_date(day_slot.getString("start_date"));
                        daySlot.setSlot_stop_date(day_slot.getString("stop_date"));
                        daySlot.setSlot_start_time(day_slot.getString("start_time"));
                        daySlot.setSlot_stop_time(day_slot.getString("stop_time"));
                        daySlot.setSlot_type(day_slot.getString("slot_type"));
                        daySlot.setSlot_max_users(day_slot.getString("max_users"));

                        JSONArray week_days_jsonArray = day_slot.getJSONArray("dates");
                        String[] dates = new String[week_days_jsonArray.length()];
                        for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                            dates[week_day] = week_days_jsonArray.getString(week_day);
                        }
                        daySlot.setSlot_week_days(dates);
                        daySlot.setSlot_id(day_slot.getString("id"));

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
                        dayEvent.setEvent_start_date(day_event.getString("start_date"));
                        dayEvent.setEvent_stop_date(day_event.getString("stop_date"));
                        dayEvent.setEvent_start_time(day_event.getString("start_time"));
                        dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                        String slot_type = day_event.getString("slot_type");
                        dayEvent.setEvent_type(slot_type);
                        dayEvent.setEvent_total_mentee(day_event.getString("number_of_users"));
                        if (!slot_type.equalsIgnoreCase("Group")) {
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                        }

                        dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                        dayEvent.setSlot_id(day_event.getString("slot_id"));
                        dayEvents.add(dayEvent);
                    }
                    day1.setDayEvents(dayEvents);
                } else {
                    day1.setDayEvents(dayEvents);
                }

                if (jsonArray_of_vacation.length() > 0) {
                    for (int vacation = 0; vacation < jsonArray_of_vacation.length(); vacation++) {
                        JSONObject day_vacation = jsonArray_of_vacation.getJSONObject(vacation);
                        DayVacation dayVacation = new DayVacation();
                        dayVacation.setStart_date(day_vacation.getString("start_date"));
                        dayVacation.setStop_date(day_vacation.getString("stop_date"));
                        dayVacation.setStart_time(day_vacation.getString("start_time"));
                        dayVacation.setStop_time(day_vacation.getString("stop_time"));

                        JSONArray jsonArray_week_days = day_vacation.getJSONArray("dates");
                        String[] dates = new String[jsonArray_week_days.length()];
                        for (int week_day = 0; week_day < jsonArray_week_days.length(); week_day++) {
                            dates[week_day] = jsonArray_week_days.getString(week_day);
                        }
                        dayVacation.setWeek_days(dates);
                        dayVacations.add(dayVacation);


                    }
                    day1.setDayVacations(dayVacations);
                } else {
                    day1.setDayVacations(dayVacations);
                }

                currentMonthArrayList.add(day1);
            }


/*                for (Day d : currentMonthArrayList) {

                    for (DayEvent de : d.dayEvents) {
                        Log.v(TAG, de.getEvent_id() + " : s_time : " + de.getEvent_start_time() + " : L_time : " + de.getEvent_stop_time());
                    }
                }*/

            for (int i = days_in_prev_month + days_in_current_month; i < days_in_prev_month + days_in_current_month + days_in_next_month; i++) {
                Day day1 = new Day();
                JSONObject unique_day = jsonArray_data.getJSONObject(i);
                day1.setDate(unique_day.getString("date"));

                JSONArray jsonArray_of_events = unique_day.getJSONArray("object");
                List<DayEvent> dayEvents = new ArrayList<DayEvent>();
                JSONArray jsonArray_of_slots = unique_day.getJSONArray("slots");
                Log.d(TAG, "for third month date :" + unique_day.getString("date") + " for third month event size : " + jsonArray_of_events.length() + " for third month slot size : " + jsonArray_of_slots.length());
                List<DaySlot> daySlots = new ArrayList<DaySlot>();
                JSONArray jsonArray_of_vacation = unique_day.getJSONArray("exceptions");
                List<DayVacation> dayVacations = new ArrayList<DayVacation>();

                if (jsonArray_of_slots.length() > 0) {
                    for (int s = 0; s < jsonArray_of_slots.length(); s++) {
                        JSONObject day_slot = jsonArray_of_slots.getJSONObject(s);
                        DaySlot daySlot = new DaySlot();
                        daySlot.setSlot_start_date(day_slot.getString("start_date"));
                        daySlot.setSlot_stop_date(day_slot.getString("stop_date"));
                        daySlot.setSlot_start_time(day_slot.getString("start_time"));
                        daySlot.setSlot_stop_time(day_slot.getString("stop_time"));
                        daySlot.setSlot_type(day_slot.getString("slot_type"));
                        daySlot.setSlot_max_users(day_slot.getString("max_users"));

                        JSONArray week_days_jsonArray = day_slot.getJSONArray("dates");
                        String[] dates = new String[week_days_jsonArray.length()];
                        for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                            dates[week_day] = week_days_jsonArray.getString(week_day);
                        }
                        daySlot.setSlot_week_days(dates);
                        daySlot.setSlot_id(day_slot.getString("id"));
                        Log.d(TAG, "Slot info " + " for date : " + unique_day.getString("date") + " start_date : " + day_slot.getString("start_date") + " stop_date : " + day_slot.getString("stop_date") + " start_time : " + day_slot.getString("start_time") + " max users : " + day_slot.getString("max_users"));

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
                        dayEvent.setEvent_start_date(day_event.getString("start_date"));
                        dayEvent.setEvent_stop_date(day_event.getString("stop_date"));
                        dayEvent.setEvent_start_time(day_event.getString("start_time"));
                        dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                        String slot_type = day_event.getString("slot_type");
                        dayEvent.setEvent_type(slot_type);
                        dayEvent.setEvent_total_mentee(day_event.getString("number_of_users"));
                        if (!slot_type.equalsIgnoreCase("Group")) {
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                        }

                        dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                        dayEvent.setSlot_id(day_event.getString("slot_id"));

                        Log.d(TAG, "Event info " + " for date : " + unique_day.getString("date") + " start_date : " + day_event.getString("start_date") + " stop_date : " + day_event.getString("stop_date") + " start_time : " + day_event.getString("start_time") + " no of users : " + day_event.getString("number_of_users"));
                        dayEvents.add(dayEvent);
                    }
                    day1.setDayEvents(dayEvents);
                } else {
                    day1.setDayEvents(dayEvents);
                }

                if (jsonArray_of_vacation.length() > 0) {
                    for (int vacation = 0; vacation < jsonArray_of_vacation.length(); vacation++) {
                        JSONObject day_vacation = jsonArray_of_vacation.getJSONObject(vacation);
                        DayVacation dayVacation = new DayVacation();
                        dayVacation.setStart_date(day_vacation.getString("start_date"));
                        dayVacation.setStop_date(day_vacation.getString("stop_date"));
                        dayVacation.setStart_time(day_vacation.getString("start_time"));
                        dayVacation.setStop_time(day_vacation.getString("stop_time"));

                        JSONArray jsonArray_week_days = day_vacation.getJSONArray("dates");
                        String[] dates = new String[jsonArray_week_days.length()];
                        for (int week_day = 0; week_day < jsonArray_week_days.length(); week_day++) {
                            dates[week_day] = jsonArray_week_days.getString(week_day);
                        }
                        dayVacation.setWeek_days(dates);
                        dayVacations.add(dayVacation);


                    }
                    day1.setDayVacations(dayVacations);
                } else {
                    day1.setDayVacations(dayVacations);
                }


                comingMonthArrayList.add(day1);
            }


            Log.d(TAG, "previousMonthArrayList size :" + previousMonthArrayList.size() + "currentMonthArrayList size :" + currentMonthArrayList.size() + ", comingMonthArrayList size :" + comingMonthArrayList.size());
            if (b_three_months_data) {   /*  program will come in this scope when user selects date from dialog i.e. user randomly selects a year and month */
                Log.d(TAG, "Three months data get changed");
                adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, userInfo.getId(), userInfo.getAvailabilityYn(), charges, array_list_subCategory, connectionStatus);
                calendarView.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
                if (month_from_dialog == 0 && year_from_dialog == 0) {
                    tv_currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
                }

            } else {
                Log.d(TAG, "three months data population");
                adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, userInfo.getId(), userInfo.getAvailabilityYn(), charges, array_list_subCategory, connectionStatus);
                calendarView.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
            }


            b_three_months_data = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void nextMonthData(Object object) {


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
                Log.d(TAG, "for third month date 2:" + unique_day.getString("date") + " for third month event size 2: " + jsonArray_of_events.length() + " for third month slot size : " + jsonArray_of_slots.length());
                List<DaySlot> daySlots = new ArrayList<DaySlot>();
                JSONArray jsonArray_of_vacation = unique_day.getJSONArray("exceptions");
                List<DayVacation> dayVacations = new ArrayList<DayVacation>();
                if (jsonArray_of_slots.length() > 0) {
                    for (int s = 0; s < jsonArray_of_slots.length(); s++) {
                        JSONObject day_slot = jsonArray_of_slots.getJSONObject(s);
                        DaySlot daySlot = new DaySlot();
                        daySlot.setSlot_start_date(day_slot.getString("start_date"));
                        daySlot.setSlot_stop_date(day_slot.getString("stop_date"));
                        daySlot.setSlot_start_time(day_slot.getString("start_time"));
                        daySlot.setSlot_stop_time(day_slot.getString("stop_time"));
                        daySlot.setSlot_type(day_slot.getString("slot_type"));
                        daySlot.setSlot_max_users(day_slot.getString("max_users"));

                        JSONArray week_days_jsonArray = day_slot.getJSONArray("dates");
                        String[] dates = new String[week_days_jsonArray.length()];
                        for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                            dates[week_day] = week_days_jsonArray.getString(week_day);
                        }
                        daySlot.setSlot_week_days(dates);
                        daySlot.setSlot_id(day_slot.getString("id"));


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
                        dayEvent.setEvent_start_date(day_event.getString("start_date"));
                        dayEvent.setEvent_stop_date(day_event.getString("stop_date"));
                        dayEvent.setEvent_start_time(day_event.getString("start_time"));
                        dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                        String slot_type = day_event.getString("slot_type");
                        dayEvent.setEvent_type(slot_type);
                        dayEvent.setEvent_total_mentee(day_event.getString("number_of_users"));
                        if (!slot_type.equalsIgnoreCase("Group")) {
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                        }

                        dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                        dayEvent.setSlot_id(day_event.getString("slot_id"));
                        dayEvents.add(dayEvent);
                    }
                    day1.setDayEvents(dayEvents);
                } else {
                    day1.setDayEvents(dayEvents);
                }


                if (jsonArray_of_vacation.length() > 0) {
                    for (int vacation = 0; vacation < jsonArray_of_vacation.length(); vacation++) {
                        JSONObject day_vacation = jsonArray_of_vacation.getJSONObject(vacation);
                        DayVacation dayVacation = new DayVacation();
                        dayVacation.setStart_date(day_vacation.getString("start_date"));
                        dayVacation.setStop_date(day_vacation.getString("stop_date"));
                        dayVacation.setStart_time(day_vacation.getString("start_time"));
                        dayVacation.setStop_time(day_vacation.getString("stop_time"));

                        JSONArray jsonArray_week_days = day_vacation.getJSONArray("dates");
                        String[] dates = new String[jsonArray_week_days.length()];
                        for (int week_day = 0; week_day < jsonArray_week_days.length(); week_day++) {
                            dates[week_day] = jsonArray_week_days.getString(week_day);
                        }
                        dayVacation.setWeek_days(dates);
                        dayVacations.add(dayVacation);


                    }
                    day1.setDayVacations(dayVacations);
                } else {
                    day1.setDayVacations(dayVacations);
                }

                comingMonthArrayList.add(day1);

            }


            Log.d(TAG, "comingMonthArrayList size" + comingMonthArrayList.size());
            for (Day day1 : comingMonthArrayList) {
                Log.d(TAG, "date from new comingMonthArrayList" + day1.getDate());
            }

            adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, userInfo.getId(), userInfo.getAvailabilityYn(), charges, array_list_subCategory, connectionStatus);
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
                Log.d(TAG, "for third month date 3:" + unique_day.getString("date") + " for third month event size 3: " + jsonArray_of_events.length() + " for third month slot size : " + jsonArray_of_slots.length());
                List<DaySlot> daySlots = new ArrayList<DaySlot>();
                JSONArray jsonArray_of_vacation = unique_day.getJSONArray("exceptions");
                List<DayVacation> dayVacations = new ArrayList<DayVacation>();
                if (jsonArray_of_slots.length() > 0) {
                    for (int s = 0; s < jsonArray_of_slots.length(); s++) {
                        JSONObject day_slot = jsonArray_of_slots.getJSONObject(s);
                        DaySlot daySlot = new DaySlot();
                        daySlot.setSlot_start_date(day_slot.getString("start_date"));
                        daySlot.setSlot_stop_date(day_slot.getString("stop_date"));
                        daySlot.setSlot_start_time(day_slot.getString("start_time"));
                        daySlot.setSlot_stop_time(day_slot.getString("stop_time"));
                        daySlot.setSlot_type(day_slot.getString("slot_type"));
                        daySlot.setSlot_max_users(day_slot.getString("max_users"));

                        JSONArray week_days_jsonArray = day_slot.getJSONArray("dates");
                        String[] dates = new String[week_days_jsonArray.length()];
                        for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                            dates[week_day] = week_days_jsonArray.getString(week_day);
                        }
                        daySlot.setSlot_week_days(dates);
                        daySlot.setSlot_id(day_slot.getString("id"));

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
                        dayEvent.setEvent_start_date(day_event.getString("start_date"));
                        dayEvent.setEvent_stop_date(day_event.getString("stop_date"));
                        dayEvent.setEvent_start_time(day_event.getString("start_time"));
                        dayEvent.setEvent_stop_time(day_event.getString("stop_time"));
                        String slot_type = day_event.getString("slot_type");
                        dayEvent.setEvent_type(slot_type);
                        dayEvent.setEvent_total_mentee(day_event.getString("number_of_users"));
                        if (!slot_type.equalsIgnoreCase("Group")) {
                            dayEvent.setFname(day_event.getString("first_name"));
                            dayEvent.setLname(day_event.getString("last_name"));
                        }

                        dayEvent.setSub_category_name(day_event.getString("sub_category_name"));
                        dayEvent.setSlot_id(day_event.getString("slot_id"));
                        dayEvents.add(dayEvent);
                    }
                    day1.setDayEvents(dayEvents);
                } else {
                    day1.setDayEvents(dayEvents);
                }


                if (jsonArray_of_vacation.length() > 0) {
                    for (int vacation = 0; vacation < jsonArray_of_vacation.length(); vacation++) {
                        JSONObject day_vacation = jsonArray_of_vacation.getJSONObject(vacation);
                        DayVacation dayVacation = new DayVacation();
                        dayVacation.setStart_date(day_vacation.getString("start_date"));
                        dayVacation.setStop_date(day_vacation.getString("stop_date"));
                        dayVacation.setStart_time(day_vacation.getString("start_time"));
                        dayVacation.setStop_time(day_vacation.getString("stop_time"));

                        JSONArray jsonArray_week_days = day_vacation.getJSONArray("dates");
                        String[] dates = new String[jsonArray_week_days.length()];
                        for (int week_day = 0; week_day < jsonArray_week_days.length(); week_day++) {
                            dates[week_day] = jsonArray_week_days.getString(week_day);
                        }
                        dayVacation.setWeek_days(dates);
                        dayVacations.add(dayVacation);


                    }
                    day1.setDayVacations(dayVacations);
                } else {
                    day1.setDayVacations(dayVacations);
                }
                previousMonthArrayList.add(day1);

            }
            adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, userInfo.getId(), userInfo.getAvailabilityYn(), charges, array_list_subCategory, connectionStatus);
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
