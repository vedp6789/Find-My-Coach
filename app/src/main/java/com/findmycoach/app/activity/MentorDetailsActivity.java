package com.findmycoach.app.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.CalendarGridAdapter;
import com.findmycoach.app.adapter.ReviewAdapter;
import com.findmycoach.app.beans.CalendarSchedule.Event;
import com.findmycoach.app.beans.CalendarSchedule.EventDuration;
import com.findmycoach.app.beans.CalendarSchedule.Mentee;
import com.findmycoach.app.beans.CalendarSchedule.MentorInfo;
import com.findmycoach.app.beans.CalendarSchedule.MonthYearInfo;
import com.findmycoach.app.beans.CalendarSchedule.Slot;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;
import com.findmycoach.app.beans.Promotions.Offer;
import com.findmycoach.app.beans.Promotions.Promotions;
import com.findmycoach.app.beans.authentication.Data;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.beans.authentication.SubCategoryName;
import com.findmycoach.app.fragment.DatePickerFragment;
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.BinaryForImage;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.ScrollableGridView;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.views.ChizzleButton;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MentorDetailsActivity extends FragmentActivity implements Callback, View.OnClickListener {
    private ListView activePromotionsLV;
    private ArrayList<Offer> activeOffers = new ArrayList<Offer>();
    private CircleImageView profileImage;
    private ImageView toggleReviewIconIV, ratingIV, studentsUnderMentorIV, qualifiedIV,
            experienceIV, genderIV, teachingPlaceIV1, teachingPlaceIV2, teachingTypeIV1,
            teachingTypeIV2, arrowQualificationIV, arrowAccrediationsIV, arrowMethodologyIV, arrowAwardsIV;
    private TextView profileName, ratingTV, noOfStudentsTV, reviewTitleTV, experienceTV,
            languageTV, distanceTV, chargesTV, ageTV, promotionsTitleTV;
    private LinearLayout chatWithMentorLL, chatWithStudentsLL, reviewLL, promotionLL;
    private ListView reviewsListView;
    private RelativeLayout toggleReviewRL;
    private ScrollView scrollView;

    private TextView qualificationTV, accrediationsTV, myMethodologyTV, awardsTV;
    private LinearLayout qualificationLL, accrediationsLL, myMethodologyLL, awardsLL;

    private RelativeLayout rlArrowQualification, rlArrowAccrediations, rlArrowMethodology, rlArrowAwards;

    private Data userInfo = null;
    private String connectionStatus;

    private TextView tv_currentMonth;
    private ImageView iv_prevMonth;
    private ImageView iv_nextMonth;
    private ScrollableGridView calendarView;
    private Dialog calendarDialog;
    Layout layout;


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
    private boolean bArrowQualification, bArrowAccrediation, bArrowMethodlogy, bArrowAwards;

    private static final String TAG = "MentorDetailsActivity";
    private List<SubCategoryName> array_list_subCategory;
    private String previous_month_start_date;/* this will get initialized when api is requested for three months (previous, current, coming)*/
    private String next_month_requested_date;
    private String prev_month_requested_date;
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mentorDetailsActivity = null;
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_details);
        Log.d(TAG, "inside mentor details activity");
        initialize();
        mentorDetailsActivity = this;
        populateFields();

        month_from_dialog = 0;
        year_from_dialog = 0;
        startPointForCalendar();
        b_three_months_data = false;

        initializeCalendar();

    }

    private void initializeCalendar() {
        calendarDialog = new Dialog(this);
        calendarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        calendarDialog.setContentView(R.layout.calendar_layout);

        iv_prevMonth = (ImageView) calendarDialog.findViewById(R.id.prevTimeIB);
        iv_prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "previous month clicked");
                showPrevMonth();
            }
        });

        tv_currentMonth = (TextView) calendarDialog.findViewById(R.id.currentTimeTV);
        tv_currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
        tv_currentMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerDialog = new DatePickerFragment();
                datePickerDialog.textView = tv_currentMonth;
                Bundle bundle = new Bundle();
                bundle.putString("for", "MentorDetailsActivity");
                datePickerDialog.setArguments(bundle);
                datePickerDialog.show(getFragmentManager(), "monthPicker");
                month_from_dialog = 0;
                year_from_dialog = 0;



/*                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                CustomDatePickerFragment customDatePickerFragment = new CustomDatePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("for", "MentorDetailsActivity");
                customDatePickerFragment.setArguments(bundle);

                customDatePickerFragment.show(fragmentManager, null);
                month_from_dialog = 0;
                year_from_dialog = 0;*/
            }
        });

        iv_nextMonth = (ImageView) calendarDialog.findViewById(R.id.nextTimeIB);
        iv_nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextMonth();
            }
        });

        calendarView = (ScrollableGridView) calendarDialog.findViewById(R.id.calendar_mentor_availability);
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


        RequestParams requestParams = new RequestParams();
        requestParams.add("user_group", String.valueOf("3"));
        requestParams.add("mentor_id", userInfo.getId());
        Log.d(TAG, "mentor_id : " + userInfo.getId());

        StringBuilder stringBuilder = new StringBuilder();

        /*Checking previous month possibilities for month and year as we have to get no. of days from previous month and adding this with current and coming month */
        if (month == 1) {
            Calendar calendar = new GregorianCalendar(year - 1, 11, 1);
            stringBuilder.append((year - 1));
            stringBuilder.append("-" + 12);
            stringBuilder.append("-" + 1);

            days_in_prev_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            Calendar calendar = new GregorianCalendar(year, (month - 1) - 1, 1);
            stringBuilder.append(year);
            stringBuilder.append("-" + (month - 1));
            stringBuilder.append("-" + 1);

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
        stringBuilder.append("-" + month_for_this);
        stringBuilder.append("-" + 1);


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
        stringBuilder.append("-" + month_for_this);
        stringBuilder.append("-" + 1);


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

    private void initialize() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        String jsonData = getIntent().getStringExtra("mentorDetails");
        Response mentorDetails = new Gson().fromJson(jsonData, Response.class);
        userInfo = mentorDetails.getData();
        connectionStatus = userInfo.getConnectionStatus();
        if (connectionStatus == null || connectionStatus.trim().equals("null"))
            connectionStatus = "not connected";
        if (connectionStatus.equals("broken"))
            connectionStatus = "not connected";
        Log.d(TAG, "connection status : 2 " + connectionStatus);
        Log.d(TAG, "json data :" + jsonData);

        array_list_subCategory = userInfo.getSubCategoryName();


        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        profileName = (TextView) findViewById(R.id.title);
        ratingTV = (TextView) findViewById(R.id.rating);
        ratingIV = (ImageView) findViewById(R.id.imageView2);
        qualifiedIV = (ImageView) findViewById(R.id.qualifiedIV);
        studentsUnderMentorIV = (ImageView) findViewById(R.id.imageView);
        noOfStudentsTV = (TextView) findViewById(R.id.numberOfStudents);
        distanceTV = (TextView) findViewById(R.id.distanceTV);
        chargesTV = (TextView) findViewById(R.id.charges);
        ageTV = (TextView) findViewById(R.id.age);
        promotionsTitleTV = (TextView) findViewById(R.id.promotionsTitleTV);
        chatWithMentorLL = (LinearLayout) findViewById(R.id.chat_with_mentor);
        chatWithStudentsLL = (LinearLayout) findViewById(R.id.chat_with_students);
        reviewLL = (LinearLayout) findViewById(R.id.review_LL);
        reviewTitleTV = (TextView) findViewById(R.id.review_title_TV);
        toggleReviewIconIV = (ImageView) findViewById(R.id.toggle_review_icon_IV);
        reviewsListView = (ListView) findViewById(R.id.reviews_list_view);
        toggleReviewRL = (RelativeLayout) findViewById(R.id.toggle_review_RL);
        scrollView = (ScrollView) findViewById(R.id.sv_profile);
        // professionTV = (TextView) findViewById(R.id.professionTV);
        // areaOfCoachingTV = (TextView) findViewById(R.id.areaOfCoachingTV);
        experienceTV = (TextView) findViewById(R.id.experience);
        experienceIV = (ImageView) findViewById(R.id.imageView4);
        genderIV = (ImageView) findViewById(R.id.genderIV);
        teachingPlaceIV1 = (ImageView) findViewById(R.id.teachingPlaceIV1);
        teachingPlaceIV2 = (ImageView) findViewById(R.id.teachingPlaceIV2);
        teachingTypeIV1 = (ImageView) findViewById(R.id.teachingTypeIV1);
        teachingTypeIV2 = (ImageView) findViewById(R.id.teachingTypeIV2);
        languageTV = (TextView) findViewById(R.id.languageTV);
        qualificationTV = (TextView) findViewById(R.id.qualificationTV);
        accrediationsTV = (TextView) findViewById(R.id.accrediationsTV);
        myMethodologyTV = (TextView) findViewById(R.id.myMethodologyTV);
        awardsTV = (TextView) findViewById(R.id.awardsTV);
        // professionLL = (LinearLayout) findViewById(R.id.professionLL);
        // areaOfCoachingLL = (LinearLayout) findViewById(R.id.areaOfCoachingLL);
        //experienceLL = (LinearLayout) findViewById(R.id.experienceLL);
//        coachingLanguageLL = (LinearLayout) findViewById(R.id.coachingLanguageLL);
        qualificationLL = (LinearLayout) findViewById(R.id.qualificationLL);
        accrediationsLL = (LinearLayout) findViewById(R.id.accrediationsLL);
        myMethodologyLL = (LinearLayout) findViewById(R.id.myMethodologyLL);
        awardsLL = (LinearLayout) findViewById(R.id.awardsLL);
//        genderLL = (LinearLayout) findViewById(R.id.genderLL);
//        teachingPlaceLL = (LinearLayout) findViewById(R.id.teachingPlaceLL);
//        teachingTypeLL = (LinearLayout) findViewById(R.id.teachingTypeLL);
        promotionLL = (LinearLayout) findViewById(R.id.promotionLL);
        promotionLL.setVisibility(View.GONE);

        tv_currentMonth = (TextView) findViewById(R.id.currentTimeTV);
        iv_nextMonth = (ImageView) findViewById(R.id.nextTimeIB);
        iv_prevMonth = (ImageView) findViewById(R.id.prevTimeIB);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rlArrowQualification = (RelativeLayout) findViewById(R.id.rlArrowQualification);
        rlArrowAccrediations = (RelativeLayout) findViewById(R.id.rlArrowAccrediations);
        rlArrowMethodology = (RelativeLayout) findViewById(R.id.rlArrowMethodology);
        rlArrowAwards = (RelativeLayout) findViewById(R.id.rlArrowAwards);

        rlArrowQualification.setOnClickListener(this);
        rlArrowAccrediations.setOnClickListener(this);
        rlArrowMethodology.setOnClickListener(this);
        rlArrowAwards.setOnClickListener(this);

        arrowQualificationIV = (ImageView) findViewById(R.id.arrowQualificationIV);
        arrowAccrediationsIV = (ImageView) findViewById(R.id.arrowAccrediationsIV);
        arrowMethodologyIV = (ImageView) findViewById(R.id.arrowMethodologyIV);
        arrowAwardsIV = (ImageView) findViewById(R.id.arrowAwardsIV);

        arrowQualificationIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        arrowAccrediationsIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        arrowMethodologyIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        arrowAwardsIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));

        qualificationTV.setSingleLine(true);
        accrediationsTV.setSingleLine(true);
        myMethodologyTV.setSingleLine(true);
        awardsTV.setSingleLine(true);

        bArrowQualification = false;
        bArrowAccrediation = false;
        bArrowMethodlogy = false;
        bArrowAwards = false;

        arrowQualificationIV.setOnClickListener(this);
        arrowAccrediationsIV.setOnClickListener(this);
        arrowMethodologyIV.setOnClickListener(this);
        arrowAwardsIV.setOnClickListener(this);

        ImageView connectionButton = (ImageView) findViewById(R.id.menuItem);

        if (connectionStatus.equals("not connected")) {
            connectionButton.setImageDrawable(getResources().getDrawable(R.drawable.connect_white));
            connectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlert();
                }
            });
        } else if (connectionStatus.equals("accepted")) {
            connectionButton.setImageDrawable(getResources().getDrawable(R.drawable.disconnect_white));
            connectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDisconnectDialog(userInfo.getConnectionId(), userInfo.getId());
                }
            });
        } else if (connectionStatus.equals("pending")) {
            connectionButton.setImageDrawable(getResources().getDrawable(R.drawable.pending_white));
            connectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDisconnectDialog(userInfo.getConnectionId(), userInfo.getId());
                }
            });
        }

        activePromotionsLV = (ListView) findViewById(R.id.promotionsLV);

        if (userInfo.getAuthToken() != null && !userInfo.getAuthToken().trim().isEmpty()) {
            RequestParams requestParams = new RequestParams();
            requestParams.add("is_active", "1");
            progressDialog.show();
            Log.e(TAG, "token of mento_____:" + userInfo.getAuthToken());
            NetworkClient.getAllPromotions(this, requestParams, userInfo.getAuthToken(), this, 58);
        } else {
            Log.e(TAG, "token of mento_____:" + userInfo.getAuthToken());

        }


    }

    private void startChat(String receiverId, String receiverName, Bitmap receiverBitmap) {
        Intent chatWidgetIntent = new Intent(this, ChatWidgetActivity.class);
        chatWidgetIntent.putExtra("receiver_id", receiverId);
        chatWidgetIntent.putExtra("receiver_name", receiverName);
        chatWidgetIntent.putExtra("receiver_image", BinaryForImage.getBinaryStringFromBitmap(receiverBitmap));
        startActivity(chatWidgetIntent);
    }

    private void showDisconnectDialog(final String connectionId, final String id) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.disconnect_confirmation_dialog);

        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect(connectionId, id);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void populateFields() {
        try {
            profileName.setText(userInfo.getFirstName().trim().split(" ")[0]);
        } catch (Exception e) {
            profileName.setText(userInfo.getFirstName());
        }

        ChizzleButton bookClass = (ChizzleButton) findViewById(R.id.bookClass);
        bookClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarDialog.show();
            }
        });

        try {
            int trialClassCount = userInfo.getNumber_of_trial_classes();
            if (trialClassCount >= 0) {
                findViewById(R.id.bookTrialClass).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendarDialog.show();
                    }
                });
            } else {
                findViewById(R.id.bookTrialClass).setVisibility(View.GONE);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 2f);
                bookClass.setLayoutParams(param);
            }
        } catch (Exception e) {
            findViewById(R.id.bookTrialClass).setVisibility(View.GONE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 2f);
            bookClass.setLayoutParams(param);
        }


        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
            ImageLoader imgLoader = new ImageLoader(profileImage);
            imgLoader.execute((String) userInfo.getPhotograph());
        }

        try {
            distanceTV.setText(getIntent().getStringExtra("distance"));
        } catch (Exception e) {
            distanceTV.setText("");
        }

        try {
            chargesTV.setText(Html.fromHtml(getIntent().getStringExtra("charges")));
        } catch (Exception e) {
            chargesTV.setText("");
        }

        try {
            if (getIntent().getBooleanExtra("qualified", false))
                qualifiedIV.setImageDrawable(getResources().getDrawable(R.drawable.qualified));
            else
                qualifiedIV.setImageDrawable(getResources().getDrawable(R.drawable.qualified_not));
        } catch (Exception e) {
            qualifiedIV.setImageDrawable(getResources().getDrawable(R.drawable.qualified_not));
        }

        try {
            int age = getAgeInyearsFromDOB((String) userInfo.getDob());
            if (age > 1)
                ageTV.setText(age + " " + getResources().getString(R.string.yrs));
            else if (age == 0)
                ageTV.setText(age + " " + getResources().getString(R.string.yr));
        } catch (Exception e) {
            ageTV.setText("");
        }

        try {
            if (userInfo.getGender() != null && !userInfo.getGender().isEmpty()) {
                if (userInfo.getGender().equals("M")) {
                    genderIV.setImageDrawable(getResources().getDrawable(R.drawable.male));
                } else if (userInfo.getGender().equals("F")) {
                    genderIV.setImageDrawable(getResources().getDrawable(R.drawable.female));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if (userInfo.getAvailabilityYn() != null && !userInfo.getAvailabilityYn().isEmpty()) {
                if (userInfo.getAvailabilityYn().equals("0")) {
                    teachingPlaceIV1.setImageDrawable(getResources().getDrawable(R.drawable.institute));  // Mentor's home is acting as institute as
                    teachingPlaceIV2.setImageDrawable(getResources().getDrawable(R.drawable.home_2));
                } else if (userInfo.getAvailabilityYn().equals("1")) {
                    teachingPlaceIV2.setImageDrawable(getResources().getDrawable(R.drawable.home));    // Mentee's home is shown as teaching place
                    teachingPlaceIV1.setImageDrawable(getResources().getDrawable(R.drawable.institute_2));
                } else if (userInfo.getAvailabilityYn().equals("2")) {
                    teachingPlaceIV2.setImageDrawable(getResources().getDrawable(R.drawable.home));    // Both address can be opted for teaching
                    teachingPlaceIV1.setImageDrawable(getResources().getDrawable(R.drawable.institute));
                }
            }
//            else {
//                teachingPlaceLL.setVisibility(View.GONE);
//            }
        } catch (Exception e) {
            e.printStackTrace();
//            teachingPlaceLL.setVisibility(View.GONE);
        }


        try {
            if (userInfo.getSlotType() != null && !userInfo.getSlotType().isEmpty()) {
                if (userInfo.getSlotType().equals("0")) {
                    teachingTypeIV1.setImageDrawable(getResources().getDrawable(R.drawable.individual));  // individual calss
                    teachingTypeIV2.setImageDrawable(getResources().getDrawable(R.drawable.group_class_2));
                } else if (userInfo.getSlotType().equals("1")) {
                    teachingTypeIV2.setImageDrawable(getResources().getDrawable(R.drawable.group_class));    // group class
                    teachingTypeIV1.setImageDrawable(getResources().getDrawable(R.drawable.individual_2));
                } else if (userInfo.getSlotType().equals("2")) {
                    teachingTypeIV2.setImageDrawable(getResources().getDrawable(R.drawable.group_class));    // Both class is active
                    teachingTypeIV1.setImageDrawable(getResources().getDrawable(R.drawable.individual));
                }
            }
//            else {
//                teachingTypeLL.setVisibility(View.GONE);
//            }
        } catch (Exception e) {
            e.printStackTrace();
//            teachingTypeLL.setVisibility(View.GONE);
        }


        try {
            int rating = Integer.parseInt(userInfo.getRating());
            if (rating > 0)
                ratingTV.setText(String.valueOf(rating));
            else {
                ratingIV.setImageDrawable(getResources().getDrawable(R.drawable.unrated_icon_1));
                ratingTV.setText("");
            }
        } catch (Exception e) {
            ratingTV.setText("");
            ratingIV.setImageDrawable(getResources().getDrawable(R.drawable.unrated_icon_1));
        }


        try {
            int noOfStud = Integer.parseInt(userInfo.getNumberOfStudents());
            if (noOfStud > 0) {
                noOfStudentsTV.setText(String.valueOf(noOfStud));
            } else {
                noOfStudentsTV.setVisibility(View.GONE);
                studentsUnderMentorIV.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            noOfStudentsTV.setText("0");

        }

        /*if (userInfo.getProfession() != null && !userInfo.getProfession().trim().isEmpty())
            professionTV.setText(userInfo.getProfession().trim());
        else
            professionLL.setVisibility(View.GONE);

        if (userInfo.getSubCategoryName() != null && userInfo.getSubCategoryName().size() > 0) {
            String subCategory = "";
            for (SubCategoryName subCategoryName : userInfo.getSubCategoryName())
                subCategory = subCategory + ", " + subCategoryName.getSub_category_name();

            subCategory = subCategory.replaceFirst(", ", "");
            areaOfCoachingTV.setText(subCategory);
        } else
            areaOfCoachingLL.setVisibility(View.GONE);
*/
        try {
            int experience = Integer.parseInt(userInfo.getExperience());
            if (experience > 0) {
                experienceTV.setText(experience + (experience > 1 ? " " + getResources().getString(R.string.yrs) : " " + getResources().getString(R.string.yr)));
                experienceIV.setImageDrawable(getResources().getDrawable(R.drawable.experience_1));

            } else {
                experienceTV.setText(experience + (experience > 1 ? " " + getResources().getString(R.string.yrs) : " " + getResources().getString(R.string.yr)));
                experienceIV.setImageDrawable(getResources().getDrawable(R.drawable.not_experienced_1));

            }


        } catch (Exception e) {
            experienceTV.setText("0" + getResources().getString(R.string.yr));
            experienceIV.setImageDrawable(getResources().getDrawable(R.drawable.not_experienced_1));
        }

        if (userInfo.getMediumOfEducation() != null && !userInfo.getMediumOfEducation().trim().isEmpty())
            languageTV.setText(userInfo.getMediumOfEducation().trim());
        else
            languageTV.setVisibility(View.GONE);


        if (userInfo.getMyQualification() != null && !userInfo.getMyQualification().trim().isEmpty()) {
            qualificationTV.setText(userInfo.getMyQualification().trim());
            Log.e(TAG, "qulification text line: " + qualificationTV.getText().toString() + qualificationTV.getLineCount());
        } else
            qualificationLL.setVisibility(View.GONE);


        if (userInfo.getMyAccredition() != null && !userInfo.getMyAccredition().trim().isEmpty())
            accrediationsTV.setText(userInfo.getMyAccredition().trim());
        else
            accrediationsLL.setVisibility(View.GONE);


        if (userInfo.getMyTeachingMethodology() != null && !userInfo.getMyTeachingMethodology().trim().isEmpty())
            myMethodologyTV.setText(userInfo.getMyTeachingMethodology().trim());
        else
            myMethodologyLL.setVisibility(View.GONE);


        if (userInfo.getMyAwards() != null && !userInfo.getMyAwards().trim().isEmpty())
            awardsTV.setText(userInfo.getMyAwards().trim());
        else
            awardsLL.setVisibility(View.GONE);

        /**Uncomment for dummy reviews*/
//        List<Review> reviewList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Review review = new Review();
//            review.setCommentedBy("Mentee " + (i + 1));
//            review.setComment("Mentor teaches good " + (i + 1));
//            reviewList.add(review);
//        }
//        userInfo.setReviews(reviewList);

        if (userInfo.getReviews() != null && userInfo.getReviews().size() > 0) {
            reviewTitleTV.setText(getResources().getString(R.string.reviews) + " (" + userInfo.getReviews().size() + ")");
            reviewsListView.setAdapter(new ReviewAdapter(userInfo.getReviews(), this));
            EditProfileActivityMentee.setHeight(reviewsListView);

            final Drawable upIcon = getResources().getDrawable(R.drawable.up_arrow);
            final Drawable downIcon = getResources().getDrawable(R.drawable.down_arrow);
            //0 for listVIew closed, 1 for listVIew expanded
            toggleReviewIconIV.setTag(toggleReviewIconIV.getId(), 0);

            toggleReviewRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((int) toggleReviewIconIV.getTag(toggleReviewIconIV.getId())) == 0) {
                        toggleReviewIconIV.setTag(toggleReviewIconIV.getId(), 1);
                        reviewsListView.setVisibility(View.VISIBLE);
                        toggleReviewIconIV.setImageDrawable(upIcon);
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    } else {
                        toggleReviewIconIV.setTag(toggleReviewIconIV.getId(), 0);
                        reviewsListView.setVisibility(View.GONE);
                        toggleReviewIconIV.setImageDrawable(downIcon);
                    }
                }
            });
        } else
            reviewLL.setVisibility(View.GONE);


        chatWithMentorLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
                startChat(userInfo.getId(), profileName.getText().toString().trim(), drawable.getBitmap());
            }
        });


    }


   /* @Override
    protected void onPostResume() {
        super.onPostResume();
        layout = qualificationTV.getLayout();

        qualificationTV.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.e(TAG, "qual tv:" + qualificationTV.getText().toString() + " " + qualificationTV.getLineCount() + "layout" + layout);
                if (!checkForEllipsized(qualificationTV.getLineCount())) {
                    rlArrowQualification.setVisibility(View.GONE);
                }

            }
        }, 2000);


        if (!checkForEllipsized(accrediationsTV)) {
            rlArrowAccrediations.setVisibility(View.GONE);
        }

        if (!checkForEllipsized(myMethodologyTV)) {
            rlArrowMethodology.setVisibility(View.GONE);
        }


        if (!checkForEllipsized(awardsTV)) {
            rlArrowAwards.setVisibility(View.GONE);
        }

    }*/

   /* private boolean checkForEllipsized(int line_count) {
        if (line_count > -1) {

            if (line_count > 0) {
                int ellipsisCount = layout.getEllipsisCount(0);
                Log.e(TAG, "ellipsis count: " + ellipsisCount);

                if (ellipsisCount > 0) {
                    Log.d(TAG, "Text is ellipsized");
                    return true;
                }
            }
        }

        return false;
    }
*/
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
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.send_connection_request_dialog);
        final EditText editText = (EditText) dialog.findViewById(R.id.editText);
        final Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        Button okButton = (Button) dialog.findViewById(R.id.okButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                if (message.trim().length() < 1)
                    message = getResources().getString(R.string.connection_request_msg);
                sendConnectionRequest(message);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
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

        if (calledApiValue == 58) {
            progressDialog.dismiss();
            try {
                Promotions promotions = (Promotions) object;

                activeOffers.clear();
                activeOffers = (ArrayList<Offer>) promotions.getPromotions();

                List<String> offersList = new ArrayList<>();
                String lesson = getResources().getString(R.string.lesson);
                String lessons = getResources().getString(R.string.lessons);
                if (activeOffers.size() > 0) {
                    for (Offer offer : activeOffers) {
                        if (offer.getPromotion_type().equals("discount"))
                            offersList.add(offer.getDiscount_percentage() + getResources().getString(R.string.percentage)
                                    + " " + getResources().getString(R.string.discount_on) + " " + offer.getDiscount_over_classes()
                                    + " " + (Integer.parseInt(offer.getDiscount_over_classes()) > 1 ? lessons : lesson));
                        else
                            offersList.add(offer.getFree_classes() + " " + getResources().getString(R.string.free)
                                    + " " + (Integer.parseInt(offer.getFree_classes()) > 1 ? lessons : lesson)
                                    + " " + getResources().getString(R.string.on) + " " + offer.getFree_min_classes()
                                    + " " + (Integer.parseInt(offer.getFree_min_classes()) > 1 ? lessons : lesson));

                    }
                    promotionLL.setVisibility(View.VISIBLE);
                    if (activeOffers.size() > 1)
                        promotionsTitleTV.setText(getResources().getString(R.string.promotions));
                    activePromotionsLV.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, offersList));
                    EditProfileActivityMentee.setHeight(activePromotionsLV);
                    Log.e(TAG, "expandable lv height: " + activePromotionsLV.getHeight());
                    Log.e(TAG, "expandable lv width: " + activePromotionsLV.getWidth());
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        ArrayList<String> tempAreaOfCategory = new ArrayList<>();
        for (SubCategoryName subCategoryName : array_list_subCategory)
            tempAreaOfCategory.add(subCategoryName.getSub_category_name());

        switch (calledApiValue) {
            case 37:
                Log.d(TAG, " API 37 success");
                Log.d(TAG, "call for api 37");
                threeMonthsData(object, tempAreaOfCategory);
                break;
            case 38:
                Log.d(TAG, " API 38 success");
                updateMonthAndYearOnNextMonthClick();
                nextMonthData(object, tempAreaOfCategory);
                break;
            case 39:
                Log.d(TAG, " API 39 success");
                updateMonthAndYearOnPreviousMonthClick();
                previousMonthData(object, tempAreaOfCategory);
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
            case 58:
                progressDialog.dismiss();
                Log.e(TAG, (String) object);
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


    private void threeMonthsData(Object object, ArrayList<String> tempAreaOfCategory) {


        Log.d(TAG, "inside three months data population");
        Log.d(TAG, "INside threeMonthData method ");
        progressDialog.dismiss();
        try {

            JSONObject jsonObject = new JSONObject((String) object);
            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
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
            previousMonthMentorInfos = getMentorInfo(jsonArray_mentor);
            currentMonthMentorInfos = getMentorInfo(jsonArray_mentor);
            comingMonthMentorInfos = getMentorInfo(jsonArray_mentor);

            Log.d(TAG, "previous month mentor info size: " + previousMonthMentorInfos.size() + ", current : " + currentMonthMentorInfos.size() + ", coming: " + comingMonthMentorInfos.size());

            Log.d(TAG, "Mentors slots info for mentee,  previousMonthArrayList size :" + previousMonthArrayList.size() + "currentMonthArrayList size :" + currentMonthArrayList.size() + ", comingMonthArrayList size :" + comingMonthArrayList.size());
            if (b_three_months_data) {   /*  program will come in this scope when user selects date from dialog i.e. user randomly selects a year and month */
                Log.d(TAG, "Three months data get changed");
                adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos, userInfo.getId(), userInfo.getAvailabilityYn(), charges, tempAreaOfCategory, connectionStatus);
                calendarView.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
                if (month_from_dialog == 0 && year_from_dialog == 0) {
                    tv_currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
                }

            } else {
                Log.d(TAG, "three months data population");
                adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos, userInfo.getId(), userInfo.getAvailabilityYn(), charges, tempAreaOfCategory, connectionStatus);
                calendarView.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
            }


            b_three_months_data = true;
        } catch (JSONException e) {
            e.printStackTrace();
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

    private ArrayList<Vacation> getVacationsForThis(List<Vacation> vacations, int month, int year, int days) {
        ArrayList<Vacation> vacationArrayList = new ArrayList<Vacation>();


        for (int vacation_no = 0; vacation_no < vacations.size(); vacation_no++) {
            Vacation vacation = vacations.get(vacation_no);
            String start_date = vacation.getStart_date();
            String stop_date = vacation.getStop_date();

            Calendar calendar_start_of_month = Calendar.getInstance();
            calendar_start_of_month.set(year, month - 1, 1);
            long month_start_date_in_millis = calendar_start_of_month.getTimeInMillis();

            Calendar calendar_vacation_start_date = Calendar.getInstance();
            calendar_vacation_start_date.set(Integer.parseInt(start_date.split("-")[0]), Integer.parseInt(start_date.split("-")[1]) - 1, Integer.parseInt(start_date.split("-")[2]));
            long vacation_start_date_in_millis = calendar_vacation_start_date.getTimeInMillis();


            Calendar calendar_vacation_end_date = Calendar.getInstance();
            calendar_vacation_end_date.set(Integer.parseInt(stop_date.split("-")[0]), Integer.parseInt(stop_date.split("-")[1]) - 1, Integer.parseInt(stop_date.split("-")[2]));
            long vacation_stop_date_in_millis = calendar_vacation_end_date.getTimeInMillis();

            Calendar calendar_end_of_month = Calendar.getInstance();
            calendar_end_of_month.set(year, month - 1, days);
            long month_end_date_in_millis = calendar_end_of_month.getTimeInMillis();

            if (((vacation_start_date_in_millis < month_start_date_in_millis || vacation_start_date_in_millis == month_start_date_in_millis) && (vacation_stop_date_in_millis > month_end_date_in_millis || vacation_stop_date_in_millis == month_end_date_in_millis)) ||
                    ((vacation_start_date_in_millis < month_start_date_in_millis || vacation_start_date_in_millis == month_start_date_in_millis) && (vacation_stop_date_in_millis > month_start_date_in_millis || vacation_stop_date_in_millis == month_start_date_in_millis) && (vacation_stop_date_in_millis < month_end_date_in_millis || vacation_stop_date_in_millis == month_end_date_in_millis)) ||
                    ((vacation_start_date_in_millis > month_start_date_in_millis || vacation_start_date_in_millis == month_start_date_in_millis) && (vacation_start_date_in_millis < month_end_date_in_millis || vacation_start_date_in_millis == month_end_date_in_millis) && (vacation_stop_date_in_millis > month_end_date_in_millis || vacation_stop_date_in_millis == month_end_date_in_millis)) ||
                    ((vacation_start_date_in_millis > month_start_date_in_millis || vacation_start_date_in_millis == month_start_date_in_millis) && (vacation_start_date_in_millis < month_end_date_in_millis || vacation_start_date_in_millis == month_end_date_in_millis) && (vacation_stop_date_in_millis > month_start_date_in_millis || vacation_stop_date_in_millis == month_start_date_in_millis) && (vacation_stop_date_in_millis < month_end_date_in_millis || vacation_stop_date_in_millis == month_end_date_in_millis)) ||
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


        for (int slot_no = 0; slot_no < slots.size(); slot_no++) {
            Slot slot = slots.get(slot_no);
            String start_date = slot.getSlot_start_date();
            String stop_date = slot.getSlot_stop_date();

            Calendar calendar_start_of_month = Calendar.getInstance();
            calendar_start_of_month.set(year, month - 1, 1);
            long month_start_date_in_millis = calendar_start_of_month.getTimeInMillis();


            Calendar calendar_slot_start_date = Calendar.getInstance();
            calendar_slot_start_date.set(Integer.parseInt(start_date.split("-")[0]), Integer.parseInt(start_date.split("-")[1]) - 1, Integer.parseInt(start_date.split("-")[2]));
            long slot_start_date_in_millis = calendar_slot_start_date.getTimeInMillis();


            Calendar calendar_slot_end_date = Calendar.getInstance();
            calendar_slot_end_date.set(Integer.parseInt(stop_date.split("-")[0]), Integer.parseInt(stop_date.split("-")[1]) - 1, Integer.parseInt(stop_date.split("-")[2]));
            long slot_stop_date_in_millis = calendar_slot_end_date.getTimeInMillis();


            Calendar calendar_end_of_month = Calendar.getInstance();
            calendar_end_of_month.set(year, month - 1, days);
            long month_end_date_in_millis = calendar_end_of_month.getTimeInMillis();


            /*if ((slot_start_date_in_millis < month_start_date_in_millis && slot_stop_date_in_millis > month_end_date_in_millis) ||
                    (slot_start_date_in_millis < month_start_date_in_millis && slot_stop_date_in_millis > month_start_date_in_millis && slot_stop_date_in_millis < month_end_date_in_millis) ||
                    (slot_start_date_in_millis > month_start_date_in_millis && slot_start_date_in_millis < month_end_date_in_millis && (slot_stop_date_in_millis > month_end_date_in_millis || slot_stop_date_in_millis == month_end_date_in_millis)) ||
                    (slot_start_date_in_millis > month_start_date_in_millis && slot_start_date_in_millis < month_end_date_in_millis && slot_stop_date_in_millis > month_start_date_in_millis && slot_stop_date_in_millis < month_end_date_in_millis) ||
                    (slot_start_date_in_millis == month_start_date_in_millis && slot_stop_date_in_millis == month_end_date_in_millis)) {

                slotArrayList.add(slot);

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
                        if (StorageHelper.getUserGroup(MentorDetailsActivity.this, "user_group").equals("2")) {
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
                   /* JSONArray vacation_weekdays = vacation_jsonObject.getJSONArray("weekdays");
                    String vacation_weekdays_array[] = new String[vacation_weekdays.length()];
                    for (int week_day = 0; week_day < vacation_weekdays.length(); week_day++) {
                        vacation_weekdays_array[week_day] = vacation_weekdays.getString(week_day);   *//* week_day is used to pass index *//*
                        vacation.setStart_time(vacation_jsonObject.getString("start_time"));
                    vacation.setStop_time(vacation_jsonObject.getString("stop_time"));
                    }*/

                    vacations.add(vacation);
                }
                slot.setVacations(vacations);
                slot.setSlot_created_on_network_success("true");
                slot.setSlot_subject(slot_jsonObject.getString("subject"));
                slot.setTutorial_location(slot_jsonObject.getString("tutorial_location"));

                slots.add(slot);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private void nextMonthData(Object object, ArrayList<String> tempAreaOfCategory) {


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


            adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos, userInfo.getId(), userInfo.getAvailabilityYn(), charges, tempAreaOfCategory, connectionStatus);
            _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
            tv_currentMonth.setText(DateFormat.format(dateTemplate,
                    _calendar.getTime()));
            adapter1.notifyDataSetChanged();
            calendarView.setAdapter(adapter1);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void previousMonthData(Object object, ArrayList<String> tempAreaOfCategory) {


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

            adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos, userInfo.getId(), userInfo.getAvailabilityYn(), charges, tempAreaOfCategory, connectionStatus);
            //adapter1 = new CalendarGridAdapter(getApplicationContext(), month, year, mentorDetailsActivity, previousMonthArrayList, currentMonthArrayList, comingMonthArrayList, previousMonthNonCoincidingVacation, currentMonthNonCoincidingVacation, comingMonthNonCoincidingVacation, previousMonthYearInfo, currentMonthYearInfo, comingMonthYearInfo, previousMonthMentorInfos, currentMonthMentorInfos, comingMonthMentorInfos, userInfo.getId(), userInfo.getAvailabilityYn(), charges, array_list_subCategory, connectionStatus);
            _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
            tv_currentMonth.setText(DateFormat.format(dateTemplate,
                    _calendar.getTime()));
            adapter1.notifyDataSetChanged();
            calendarView.setAdapter(adapter1);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrowQualificationIV:
            case R.id.rlArrowQualification:
                if (!bArrowQualification) {
                    qualificationTV.setSingleLine(false);
                    arrowQualificationIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                    bArrowQualification = true;
                } else if (bArrowQualification) {
                    bArrowQualification = false;
                    qualificationTV.setSingleLine(true);
                    arrowQualificationIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                }
                break;
            case R.id.arrowAccrediationsIV:
            case R.id.rlArrowAccrediations:
                if (!bArrowAccrediation) {
                    accrediationsTV.setSingleLine(false);
                    arrowAccrediationsIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                    bArrowAccrediation = true;
                } else if (bArrowAccrediation) {
                    bArrowAccrediation = false;
                    accrediationsTV.setSingleLine(true);
                    arrowAccrediationsIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                }
                break;
            case R.id.arrowMethodologyIV:
            case R.id.rlArrowMethodology:
                if (!bArrowMethodlogy) {
                    myMethodologyTV.setSingleLine(false);
                    arrowMethodologyIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                    bArrowMethodlogy = true;
                } else if (bArrowMethodlogy) {
                    bArrowMethodlogy = false;
                    myMethodologyTV.setSingleLine(true);
                    arrowMethodologyIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                }
                break;
            case R.id.arrowAwardsIV:
            case R.id.rlArrowAwards:
                if (!bArrowAwards) {
                    awardsTV.setSingleLine(false);
                    arrowAwardsIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                    bArrowAwards = true;
                } else if (bArrowAwards) {
                    bArrowAwards = false;
                    awardsTV.setSingleLine(true);
                    arrowAwardsIV.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                }
                break;


        }
    }

    private int getAgeInyearsFromDOB(String dobString) {
        java.text.DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(dobString);
            Calendar dob = Calendar.getInstance();
            dob.setTime(date);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
                age--;
            } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
