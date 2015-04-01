package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment.MyScheduleFragment;
import com.findmycoach.app.fragment_mentor.StartDateDialogFragment;
import com.findmycoach.app.fragment_mentor.StartTimeDialogFragment;
import com.findmycoach.app.fragment_mentor.StopTimeDialogFragment;
import com.findmycoach.app.fragment_mentor.TillDateDialogFragment;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.SetDate;
import com.findmycoach.app.util.SetTime;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TreeSet;

/**
 * Created by praka_000 on 2/12/2015.
 */
public class AddNewSlotActivity extends Activity implements SetDate, SetTime{
    CheckBox cb_mon, cb_tue,
            cb_wed, cb_thur,
            cb_fri, cb_sat,
            cb_sun;

    public static TextView tv_start_date, tv_till_date, tv_start_time, tv_stop_time;
    boolean boo_mon_checked, boo_tue_checked,
            boo_wed_checked, boo_thurs_checked,
            boo_fri_checked, boo_sat_checked,
            boo_sun_checked;
    Button b_create_slot;
    private static String time_from;
    private static String time_to;
    private static String date_from;
    private static String date_to;
    private static int from_day;// Starting day of the slot
    private static int from_month;//Starting month of the slot
    private static int from_year;//Starting year of the slot.
    private static int till_day;// completion day of the slot
    private static int till_month;//completion month of the slot
    private static int till_year;//completion year of the slot.
    private static int start_hour;
    private static int start_min;
    private static int stop_hour;
    private static int stop_min;

    private Date newDate;


    private static final String TAG = "FMC";


    private static ArrayList<String> days_array = null;

    ProgressDialog progressDialog;

    private void applyActionbarProperties() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.add_new_slot));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_avail_slot);

        time_from = getResources().getString(R.string.select);
        time_to = getResources().getString(R.string.select);
        date_to = getResources().getString(R.string.forever);


        from_day = 0;
        from_month = 0;
        from_year = 0;

        boo_mon_checked = false;
        boo_tue_checked = false;
        boo_wed_checked = false;
        boo_thurs_checked = false;
        boo_fri_checked = false;
        boo_sat_checked = false;
        boo_sun_checked = false;

        initialize();
        applyActionbarProperties();
        progressDialog = new ProgressDialog(AddNewSlotActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        /* Current date */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        String current_date = simpleDateFormat.format(new Date());

        date_from = current_date.substring(0, 2) + "-" + current_date.substring(2, 4) + "-" + current_date.substring(4, 8);
        from_day = Integer.parseInt(current_date.substring(0, 2));
        from_month = Integer.parseInt(current_date.substring(2, 4));
        from_year = Integer.parseInt(current_date.substring(4, 8));
        tv_start_date.setText(date_from);
        tv_till_date.setText(getResources().getString(R.string.forever));
        date_to = getResources().getString(R.string.forever);

        allListeners();

    }

    private void allListeners() {
        tv_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                StartTimeDialogFragment timeDialogFragment = new StartTimeDialogFragment();
                timeDialogFragment.show(fragmentManager, null);
            }
        });

        tv_stop_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_start_time.getText().length() > 0) {
                    FragmentManager fragmentManager = getFragmentManager();
                    StopTimeDialogFragment timeDialogFragment = new StopTimeDialogFragment();
                    timeDialogFragment.show(fragmentManager, null);
                } else {
                    Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.start_time_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "On click");
                FragmentManager fragmentManager = getFragmentManager();
                StartDateDialogFragment dateDialogFragment = new StartDateDialogFragment();
                dateDialogFragment.addNewSlotActivity = AddNewSlotActivity.this;
                dateDialogFragment.show(fragmentManager, null);

            }
        });

        tv_till_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_start_date.getText().length() > 0) {
                    FragmentManager fragmentManager = getFragmentManager();
                    TillDateDialogFragment dateDialogFragment = new TillDateDialogFragment();
                    dateDialogFragment.addNewSlotActivity = AddNewSlotActivity.this;
                    dateDialogFragment.show(fragmentManager, null);
                } else {
                    Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.from_date_first), Toast.LENGTH_SHORT).show();
                }

            }
        });

        cb_mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_mon.isChecked()) {
                    boo_mon_checked = true;
                } else {
                    boo_mon_checked = false;
                }
            }
        });

        cb_tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_tue.isChecked()) {
                    boo_tue_checked = true;
                } else {
                    boo_tue_checked = false;
                }
            }
        });

        cb_wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_wed.isChecked()) {
                    boo_wed_checked = true;
                } else {
                    boo_wed_checked = false;
                }
            }
        });

        cb_thur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_thur.isChecked()) {
                    boo_thurs_checked = true;
                } else {
                    boo_thurs_checked = false;
                }
            }
        });

        cb_fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_fri.isChecked()) {
                    boo_fri_checked = true;
                } else {
                    boo_fri_checked = false;
                }
            }
        });

        cb_sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_sat.isChecked()) {
                    boo_sat_checked = true;
                } else {
                    boo_sat_checked = false;
                }
            }
        });

        cb_sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_sun.isChecked()) {
                    boo_sun_checked = true;
                } else {
                    boo_sun_checked = false;
                }
            }
        });

        b_create_slot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {

                    days_array = new ArrayList<String>();

                    if (boo_mon_checked) {

                        days_array.add("M");
                    }
                    if (boo_tue_checked) {
                        days_array.add("T");
                    }
                    if (boo_wed_checked) {
                        days_array.add("W");
                    }
                    if (boo_thurs_checked) {
                        days_array.add("Th");
                    }
                    if (boo_fri_checked) {
                        days_array.add("F");
                    }
                    if (boo_sat_checked) {
                        days_array.add("S");
                    }
                    if (boo_sun_checked) {
                        days_array.add("Su");
                    }

                    if (StorageHelper.getUserGroup(AddNewSlotActivity.this, "user_group").equals("3")) {
                        Log.d(TAG, "Going to create a new slot for you.");
                        RequestParams requestParams = new RequestParams();
                        requestParams.add("mentor_id", StorageHelper.getUserDetails(AddNewSlotActivity.this, "user_id"));
                        Log.d(TAG, "From date" + tv_start_date.getText().toString());
                        Log.d(TAG, "Till date" + tv_till_date.getText().toString());

                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(String.valueOf(from_year));
                        if ((from_month / 10) > 0) {
                            stringBuilder.append("-" + from_month);
                        } else {
                            stringBuilder.append("-" + 0 + from_month);
                        }
                        if ((from_day / 10) > 0) {
                            stringBuilder.append("-" + from_day);
                        } else {
                            stringBuilder.append("-" + 0 + from_day);
                        }
                        Log.d(TAG, "start date:" + stringBuilder.toString());

                        requestParams.add("start_date", stringBuilder.toString());

                        if (tv_till_date.getText().toString().equals(getResources().getString(R.string.forever))) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(String.valueOf(from_year + 10));
                            if ((from_month / 10) > 0) {
                                stringBuilder2.append("-" + from_month);
                            } else {
                                stringBuilder2.append("-" + 0 + from_month);
                            }
                            if ((from_day / 10) > 0) {
                                stringBuilder2.append("-" + from_day);
                            } else {
                                stringBuilder2.append("-" + 0 + from_day);
                            }
                            Log.d(TAG, "till date1:" + stringBuilder2.toString());

                            requestParams.add("stop_date", stringBuilder2.toString());
                        } else {
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(String.valueOf(till_year));
                            if ((till_month / 10) > 0) {
                                stringBuilder3.append("-" + till_month);
                            } else {
                                stringBuilder3.append("-" + 0 + till_month);
                            }
                            if ((till_day / 10) > 0) {
                                stringBuilder3.append("-" + till_day);
                            } else {
                                stringBuilder3.append("-" + 0 + till_day);
                            }
                            Log.d(TAG, "till date2:" + stringBuilder3.toString());

                            requestParams.add("stop_date", stringBuilder3.toString());
                        }

                        requestParams.add("start_time", start_hour + ":" + start_min + ":" + "00");
                        requestParams.add("stop_time", stop_hour + ":" + stop_min + ":" + "00");

                        StringBuilder stringBuilder1 = new StringBuilder();
                        stringBuilder1.append(days_array.get(0));
                        if (days_array.size() > 1) {
                            for (int i = 1; i < days_array.size(); i++) {
                                stringBuilder1.append("," + days_array.get(i));
                            }
                        }


                        requestParams.add("name", StorageHelper.getUserDetails(AddNewSlotActivity.this, "user_id") + "_Slot");
                        requestParams.add("dates", stringBuilder1.toString());

                        for (int i = 0; i < days_array.size(); i++) {
                            Log.d(TAG, "Day" + days_array.get(i));
                        }

                        int start_time = ((start_hour * 60) + start_min) * 60;
                        int stop_time = ((stop_hour * 60) + stop_min) * 60;
                        int intermediate_time = (24 * 60) * 60;
                        int slot_time_value;
                        if (start_hour > stop_hour) {
                            int first_half_time = intermediate_time - start_time;
                            slot_time_value = first_half_time + stop_time;
                        } else {
                            slot_time_value = stop_time - start_time;
                        }
                        Log.d(TAG, "Slot time value from AddNewSlotActivity" + slot_time_value / (60 * 60));
                        requestParams.add("slot_time_value", String.valueOf(slot_time_value));
                        String auth_token = StorageHelper.getUserDetails(AddNewSlotActivity.this, "auth_token");
                        progressDialog.show();
                        NetworkClient.createNewSlot(AddNewSlotActivity.this, requestParams, auth_token, new Callback() {

                            @Override
                            public void successOperation(Object object, int statusCode, int calledApiValue) {
                                Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.created_new_slot_successfully), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                String success_response=(String) object;
                                Log.d(TAG, "success response for add new slot activity : " + success_response);
                                JSONObject jO_success_response =null;
                                JSONArray jA_coinciding_Exceptions=null;

                                if (success_response.equals(getResources().getString(R.string.problem_in_connection_server))) {
                                    Toast.makeText(AddNewSlotActivity.this, (String) object, Toast.LENGTH_SHORT).show();
                                    // progressDialog.dismiss();
                                }else{
                                    try {
                                        jO_success_response=new JSONObject(success_response);
                                        jA_coinciding_Exceptions=jO_success_response.getJSONArray("coincidingExceptions");
                                        String message=jO_success_response.getString("message");
                                        if(message.equals("Success")){
                                            if(jA_coinciding_Exceptions.length() > 0){
                                                coincideOf(jA_coinciding_Exceptions,1);
                                            }else{

                                                setResult(500);
                                                finish();


                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void failureOperation(Object object, int statusCode, int calledApiValue) {
                                progressDialog.dismiss();
                                String failure_response = (String) object;
                                Log.d(TAG, "failure  response for add new slot activity : " + failure_response);

                                JSONObject jO_failure_resp = null;
                                JSONArray jA_coinciding_slots = null, jA_coinciding_Exceptions = null;
                                if (failure_response.equals(getResources().getString(R.string.problem_in_connection_server))) {
                                    Toast.makeText(AddNewSlotActivity.this, (String) object, Toast.LENGTH_SHORT).show();
                                   // progressDialog.dismiss();
                                } else {
                                    try {
                                        jO_failure_resp = new JSONObject(failure_response);
                                        jA_coinciding_slots = jO_failure_resp.getJSONArray("coincidingSlots");
                                        jA_coinciding_Exceptions = jO_failure_resp.getJSONArray("coincidingExceptions");

                                        if (jA_coinciding_slots != null) {
                                            coincideOf(jA_coinciding_slots, 0);
                                        }
                                        if (jA_coinciding_Exceptions != null) {
                                            coincideOf(jA_coinciding_Exceptions, 1);
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }


                            }
                        }, 35);
                    }
                }
            }
        });
    }

    /* This method generates message when either slots coincide or there is coinciding exceptions like vaccation*/
    void coincideOf(JSONArray jsonArray, int flag) {      /* flag 0 means slot is coinciding and 1 means there is some exceptions while this schedule like mentor has already scheduled some vaccations*/

        if (jsonArray.length() > 1) {
            String s_date, st_date, s_time, st_time;

            Date start_date = null, stop_date = null;
            TreeSet<String> tset_days = new TreeSet<String>();
            TreeSet<Float> tset_s_time = new TreeSet<Float>();
            TreeSet<Float> tset_st_time = new TreeSet<Float>();
            JSONObject jO_coinciding_detail = null;
            try {
                jO_coinciding_detail = jsonArray.getJSONObject(0);
                JSONArray jA_Week_days = jO_coinciding_detail.getJSONArray("week_days");
                if (jA_Week_days.length() > 0) {

                    for (int jA_Week_day = 0; jA_Week_day < jA_Week_days.length(); jA_Week_day++) {
                        tset_days.add(jA_Week_days.getString(jA_Week_day));
                    }

                }
                s_date = jO_coinciding_detail.getString("start_date");
                st_date = jO_coinciding_detail.getString("stop_date");
                s_time = jO_coinciding_detail.getString("start_time");
                st_time = jO_coinciding_detail.getString("stop_time");

                float f_s_time = Float.parseFloat(s_time.split(":")[0] + "." + s_time.split(":")[1]);
                float f_st_time = Float.parseFloat(st_time.split(":")[0] + "." + st_time.split(":")[1]);

                tset_s_time.add(f_s_time);
                tset_st_time.add(f_st_time);


                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    start_date = dateFormat.parse(s_date);
                    stop_date = dateFormat.parse(st_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 1; i < jsonArray.length(); i++) {
                Date new_start_date, new_stop_date;
                JSONObject jO_coinciding_detail1 = null;
                try {
                    jO_coinciding_detail1 = jsonArray.getJSONObject(i);
                    JSONArray jA_Week_days = jO_coinciding_detail.getJSONArray("week_days");
                    if (jA_Week_days.length() > 0) {
                        for (int jA_Week_day = 0; jA_Week_day < jA_Week_days.length(); jA_Week_day++) {
                            tset_days.add(jA_Week_days.getString(jA_Week_day));
                        }
                    }
                    s_date = jO_coinciding_detail.getString("start_date");
                    st_date = jO_coinciding_detail.getString("stop_date");
                    s_time = jO_coinciding_detail.getString("start_time");
                    st_time = jO_coinciding_detail.getString("stop_time");

                    float f_s_time = Float.parseFloat(s_time.split(":")[0] + "." + s_time.split(":")[1]);
                    float f_st_time = Float.parseFloat(st_time.split(":")[0] + "." + st_time.split(":")[1]);

                    tset_s_time.add(f_s_time);
                    tset_st_time.add(f_st_time);


                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        new_start_date = dateFormat.parse(s_date);
                        new_stop_date = dateFormat.parse(st_date);

                        if (new_start_date.before(start_date)) {
                            start_date = new_start_date;
                        }

                        if (new_stop_date.after(stop_date)) {
                            stop_date = new_stop_date;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            StringBuilder stringBuilder = new StringBuilder();

            if (start_date != null && stop_date != null && tset_s_time.size() > 0 && tset_st_time.size() > 0) {
                float start_time = tset_s_time.first();
                float stop_time = tset_st_time.last();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());


                if (tset_days.size() > 0) {
                    ArrayList<String> days_selected = new ArrayList<String>(tset_days.size());
                    days_selected.addAll(tset_days);

                    String day = days_selected.get(0);

                    StringBuilder stringBuilder1 = new StringBuilder();


                    for (int i = 0; i < days_selected.size(); i++) {
                        String day1 = days_selected.get(i);
                        if (day1.equals("M"))
                            stringBuilder1.append("Mon");
                        if (day1.equals("T"))
                            stringBuilder1.append("Tue");
                        if (day1.equals("W"))
                            stringBuilder1.append("Wed");
                        if (day1.equals("Th"))
                            stringBuilder1.append("Thu");
                        if (day1.equals("F"))
                            stringBuilder1.append("Fri");
                        if (day1.equals("S"))
                            stringBuilder1.append("Sat");
                        if (day1.equals("Su"))
                            stringBuilder1.append("Sun");
                    }

                    /* if condition is checking whether the flag is 0 or 1 in case of multiple coincidiing slots with Week-days mentioned, or in case of multiple coinciding exceptions with Week-days in Json string .*/
                    if (flag == 0) {
                        stringBuilder.append("Sorry, there is already a slot between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":") + " for " + stringBuilder1.toString() + " \n So this slot cannot be placed!");
                        showCoincidingAlertMessage(stringBuilder.toString(), flag);
                        Log.d(TAG, "Message for coinciding slot schedule : " + stringBuilder.toString());
                    } else {
                        stringBuilder.append("Hi, you have placed a new slot with a vacation between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":") + " for " + stringBuilder1.toString());
                        showCoincidingAlertMessage(stringBuilder.toString(), flag);
                        Log.d(TAG, "Message for coinciding exception while add new slot : " + stringBuilder.toString());
                    }

                } else {
                    /* if condition is checking whether the flag is 0 or 1 in case of multiple coincidiing slots , or in case of multiple coinciding exceptions  in Json string .*/
                    if (flag == 0) {
                        stringBuilder.append("Sorry, there is already a slot between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":") + "\n So this slot cannot be placed!");
                        showCoincidingAlertMessage(stringBuilder.toString(), flag);
                        Log.d(TAG, "Message for coinciding slot schedule : " + stringBuilder.toString());
                    } else {
                        stringBuilder.append("Hi, you have placed a new slot with a vacation between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":"));
                        showCoincidingAlertMessage(stringBuilder.toString(), flag);
                        Log.d(TAG, "Message for coinciding exception while add new slot : " + stringBuilder.toString());
                    }
                }

            }


        } else {
            if (jsonArray.length() > 0) {
                try {
                    String s_date, st_date, s_time, st_time;
                    ArrayList<String> days = new ArrayList<String>();

                    JSONObject jO_coinciding_detail = jsonArray.getJSONObject(0);
                    JSONArray jA_Week_days = jO_coinciding_detail.getJSONArray("week_days");
                    if (jA_Week_days.length() > 0) {
                        for (int jA_index = 0; jA_index < jA_Week_days.length(); jA_index++) {
                            days.add(jA_Week_days.getString(jA_index));
                        }
                    }
                    s_date = jO_coinciding_detail.getString("start_date");
                    st_date = jO_coinciding_detail.getString("stop_date");
                    s_time = jO_coinciding_detail.getString("start_time");
                    st_time = jO_coinciding_detail.getString("stop_time");
                    StringBuilder stringBuilder = new StringBuilder();
                    Date start_date = null, stop_date = null;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        start_date = dateFormat.parse(s_date);
                        stop_date = dateFormat.parse(st_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                    Log.d(TAG, "days : " + days);

                    if (days != null) {

                        StringBuilder stringBuilder1 = new StringBuilder();
                        String day1 = days.get(0);
                        if (day1.equals("M"))
                            stringBuilder1.append("Mon");
                        if (day1.equals("T"))
                            stringBuilder1.append("Tue");
                        if (day1.equals("W"))
                            stringBuilder1.append("Wed");
                        if (day1.equals("Th"))
                            stringBuilder1.append("Thu");
                        if (day1.equals("F"))
                            stringBuilder1.append("Fri");
                        if (day1.equals("S"))
                            stringBuilder1.append("Sat");
                        if (day1.equals("Su"))
                            stringBuilder1.append("Sun");

                        for (int i = 1; i < days.size(); i++) {
                            String day = days.get(i);
                            if (day.equals("M"))
                                stringBuilder1.append(", Mon");
                            if (day.equals("T"))
                                stringBuilder1.append(", Tue");
                            if (day.equals("W"))
                                stringBuilder1.append(", Wed");
                            if (day.equals("Th"))
                                stringBuilder1.append(", Thu");
                            if (day.equals("F"))
                                stringBuilder1.append(", Fri");
                            if (day.equals("S"))
                                stringBuilder1.append(", Sat");
                            if (day.equals("Su"))
                                stringBuilder1.append(", Sun");

                        }



                    /* if condition is checking whether the flag is 0 or 1 in case of single coinciding slot with Week-days mentioned, or in case of single coinciding exception with Week-days in Json string .*/
                        if (flag == 0) {
                            stringBuilder.append("Sorry, there is already a slot between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0, 5) + " to " + st_time.substring(0, 5) + " for " + stringBuilder1.toString() + " \n So this slot cannot be placed!");

                            showCoincidingAlertMessage(stringBuilder.toString(), flag);
                            Log.d(TAG, "Message for coinciding slot schedule : " + stringBuilder.toString());
                        } else {
                            stringBuilder.append("Hi, you have placed a new slot with a vacation between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0, 5) + " to " + st_time.substring(0, 5) + " for " + stringBuilder1.toString());
                            showCoincidingAlertMessage(stringBuilder.toString(), flag);
                            Log.d(TAG, "Message for coinciding exception while add new slot : " + stringBuilder.toString());
                        }


                    } else {

                    /* if condition is checking whether the flag is 0 or 1 in case of single coinciding slot,or in case of single coinciding exception in Json string.*/
                        if (flag == 0) {
                            stringBuilder.append("Sorry, there is already a slot between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0, 5) + " to " + st_time.substring(0, 5) + "\n So this slot cannot be placed!");
                            showCoincidingAlertMessage(stringBuilder.toString(), flag);
                            Log.d(TAG, "Message for coinciding slot schedule : " + stringBuilder.toString());
                        } else {
                            stringBuilder.append("Hi, you have placed a new slot with a vacation between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0, 5) + " to " + st_time.substring(0, 5));
                            showCoincidingAlertMessage(stringBuilder.toString(), flag);
                            Log.d(TAG, "Message for coinciding exception while add new slot : " + stringBuilder.toString());
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    void showCoincidingAlertMessage(String message, int flag) {
        if (flag == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Coinciding slot")
                    .setMessage(message)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Vaccation schedule found")
                    .setMessage(message)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            setResult(500);
                            finish();
                        }
                    })

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    private boolean validate() {
        if (boo_mon_checked || boo_tue_checked || boo_wed_checked || boo_thurs_checked || boo_fri_checked || boo_sat_checked || boo_sun_checked) {
            if (time_from.equals(getResources().getString(R.string.select)) || time_to.equals(getResources().getString(R.string.select))) {
                if (time_from.equals(getResources().getString(R.string.select)) && time_to.equals(getResources().getString(R.string.select))) {
                    Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_start_and_end_time), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    if (time_from.equals(getResources().getString(R.string.select))) {
                        Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_start_time), Toast.LENGTH_SHORT).show();
                        return false;

                    } else {
                        if (time_to.equals(getResources().getString(R.string.select))) {
                            Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_end_time), Toast.LENGTH_SHORT).show();
                            return false;
                        }


                    }

                }
                return false;
            } else {
                int start_time = ((start_hour * 60) + start_min) * 60;
                int stop_time = ((stop_hour * 60) + stop_min) * 60;

                int slot_time_value=0;
                if (start_time > stop_time) {
                    Toast.makeText(AddNewSlotActivity.this,getResources().getString(R.string.stop_time_should_be_grater), Toast.LENGTH_LONG).show();
                    return false;

                } else {
                    slot_time_value = stop_time - start_time;
                    int minimum_difference = (Integer.parseInt(getResources().getString(R.string.slot_time_difference_in_hour)) * 60) * 60;

                    if (slot_time_value < minimum_difference) {
                        Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.slot_time_difference), Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        if ((slot_time_value % 3600) > 0) {
                            Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_slot_in_multiple_of_hour), Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            if (tv_start_date.getText().length() > 0) {
                                String till_date_val = tv_till_date.getText().toString();
                                String s = getResources().getString(R.string.forever);
                                Log.d(TAG, " till date value : " + till_date_val + " forever string from resource : " + s);
                                if (till_date_val.equals(s)) {
                                    return true;
                                } else {
                                    return checkDaysAvailability(tv_start_date.getText().toString(), tv_till_date.getText().toString());
                                }
                            } else {
                                Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_start_date_of_slot), Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }


                    }
                }


            }

        } else {
            Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_at_least_one_day), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    boolean checkDaysAvailability(String start_date, String till_date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date start_convertedDate = null, till_CovertedDate = null/*,todayWithZeroTime=null*/;
        try {
            start_convertedDate = dateFormat.parse(start_date);
            till_CovertedDate = dateFormat.parse(till_date);

            /*Date today = new Date();

            todayWithZeroTime =dateFormat.parse(dateFormat.format(today));*/
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int s_year = 0, s_month = 0, s_day = 0;

        Calendar s_cal = Calendar.getInstance();
        s_cal.setTime(start_convertedDate);

        s_year = s_cal.get(Calendar.YEAR);
        s_month = s_cal.get(Calendar.MONTH);
        s_day = s_cal.get(Calendar.DAY_OF_MONTH);


        Calendar t_cal = Calendar.getInstance();
        t_cal.setTime(till_CovertedDate);

        int t_year = t_cal.get(Calendar.YEAR);
        int t_month = t_cal.get(Calendar.MONTH);
        int t_day = t_cal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(s_year, s_month, s_day);
        date2.clear();
        date2.set(t_year, t_month, t_day);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);
        int day_count = (int) dayCount + 1;

        Log.d(TAG, "Duration difference : " + dayCount + ", in Days : " + day_count);

        int no_of_odd_days = (day_count % 7);

        if (no_of_odd_days > 0 && day_count < 7) {
            int weeks_having_no_odd_days = (day_count / 7);   /* Week number from start date and stop date having odd days*/
            int move_by = weeks_having_no_odd_days * 7;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start_convertedDate);
            calendar.add(Calendar.DAY_OF_YEAR, move_by);

            newDate = calendar.getTime();
            Log.d(TAG, " start of odd dates from this date by adding one date " + dateFormat.format(newDate) + " no of odd days : " + no_of_odd_days);

            ArrayList<String> checkedWeekDays = getListForCheckedDays();
            Log.d(TAG, "Checked day arraylist values");
            for (int i = 0; i < checkedWeekDays.size(); i++) {
                Log.d(TAG, " Checked days :" + checkedWeekDays.get(i));
            }

            Log.d(TAG, "ArrayList of selected week days : " + checkedWeekDays.size());
            if (checkedWeekDays.size() > 0) {

                String weeK_day = dayOfDate(newDate);
                Log.d(TAG, "First week day from odd days : " + weeK_day);
                for (int i = 0; i < checkedWeekDays.size(); i++) {
                    String day = checkedWeekDays.get(i);
                    if (weeK_day.equals(day)) {
                        Log.d(TAG, "Initial day get removed !");
                        checkedWeekDays.remove(i);
                    }
                }


                Log.d(TAG, "Checked day arraylist values after first deletion ");
                for (int i = 0; i < checkedWeekDays.size(); i++) {
                    Log.d(TAG, " Checked days :" + checkedWeekDays.get(i));
                }

                int loop = 1;

                while (loop < no_of_odd_days) {
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(newDate);
                    calendar1.add(Calendar.DAY_OF_YEAR, 1);
                    newDate = calendar1.getTime();
                    Log.d(TAG, " new odd date from " + loop + " : " + dateFormat.format(newDate));
                    Log.d(TAG, "ArrayList of selected week days from odd day loop " + loop + " is :" + checkedWeekDays.size());
                    if (checkedWeekDays.size() > 0) {
                        String weeK_day1 = dayOfDate(newDate);
                        for (int i = 0; i < checkedWeekDays.size(); i++) {
                            String day = checkedWeekDays.get(i);
                            if (weeK_day1.equals(day)) {
                                checkedWeekDays.remove(i);
                            }
                        }
                    }
                    ++loop;
                }


                Log.d(TAG, "Selected week day size after all operation " + checkedWeekDays.size());

                Log.d(TAG, "Checked day arraylist values after all deletions");
                for (int i = 0; i < checkedWeekDays.size(); i++) {
                    Log.d(TAG, " Checked days :" + checkedWeekDays.get(i));
                }

                if (checkedWeekDays.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String day = checkedWeekDays.get(0);
                    if (day.equals("1"))

                        stringBuilder.append("Sun");
                    if (day.equals("2"))
                        stringBuilder.append("Mon");
                    if (day.equals("3"))
                        stringBuilder.append("Tue");
                    if (day.equals("4"))
                        stringBuilder.append("Wed");
                    if (day.equals("5"))
                        stringBuilder.append("Thu");
                    if (day.equals("6"))
                        stringBuilder.append("Fri");
                    if (day.equals("7"))
                        stringBuilder.append("Sat");


                    for (int s = 1; s < checkedWeekDays.size(); s++) {
                        String day1 = checkedWeekDays.get(s);

                        if (day1.equals("1"))
                            stringBuilder.append(", Sun");
                        if (day1.equals("2"))
                            stringBuilder.append(", Mon");
                        if (day1.equals("3"))
                            stringBuilder.append(", Tue");
                        if (day1.equals("4"))
                            stringBuilder.append(", Wed");
                        if (day1.equals("5"))
                            stringBuilder.append(", Thu");
                        if (day1.equals("6"))
                            stringBuilder.append(", Fri");
                        if (day1.equals("7"))
                            stringBuilder.append(", Sat");


                    }
                    Toast.makeText(AddNewSlotActivity.this, stringBuilder.toString() + " " + getResources().getString(R.string.out_of_duration), Toast.LENGTH_LONG).show();
                    return false;
                } else
                    return true;


            } else {
                return true;
            }
        } else {
            return true;
        }


    }

    private String dayOfDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date); // yourdate is a object of type Date

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return String.valueOf(dayOfWeek);
    }

    private ArrayList<String> getListForCheckedDays() {
        ArrayList<String> days_checked = new ArrayList<String>();
        if (cb_mon.isChecked())
            days_checked.add("2");
        if (cb_tue.isChecked())
            days_checked.add("3");
        if (cb_wed.isChecked())
            days_checked.add("4");
        if (cb_thur.isChecked())
            days_checked.add("5");
        if (cb_fri.isChecked())
            days_checked.add("6");
        if (cb_sat.isChecked())
            days_checked.add("7");
        if (cb_sun.isChecked())
            days_checked.add("1");

        return days_checked;

    }


    void initialize() {
        cb_mon = (CheckBox) findViewById(R.id.cb_mon);
        cb_tue = (CheckBox) findViewById(R.id.cb_tue);
        cb_wed = (CheckBox) findViewById(R.id.cb_wed);
        cb_thur = (CheckBox) findViewById(R.id.cb_thur);
        cb_fri = (CheckBox) findViewById(R.id.cb_fri);
        cb_sat = (CheckBox) findViewById(R.id.cb_sat);
        cb_sun = (CheckBox) findViewById(R.id.cb_sun);
        tv_start_date = (TextView) findViewById(R.id.tv_slot_start_date);
        tv_till_date = (TextView) findViewById(R.id.tv_slot_till_date);
        tv_start_time = (TextView) findViewById(R.id.tv_slot_start_time);
        tv_stop_time = (TextView) findViewById(R.id.tv_slot_stop_time);

        b_create_slot = (Button) findViewById(R.id.b_create_slot);

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /* Initializes start date from StartDateDialogFragment*/
    @Override
    public void setSelectedStartDate(Object o1, Object o2, Object o3) {
        Log.d(TAG, o1.toString() + "/" + o2.toString() + "/" + o3.toString());
        int day = Integer.parseInt(o1.toString());
        AddNewSlotActivity.from_day = day;
        int month = Integer.parseInt(o2.toString());
        AddNewSlotActivity.from_month = month;
        int year = Integer.parseInt(o3.toString());
        AddNewSlotActivity.from_year = year;
        Log.d(TAG, "From date:" + from_day + "/" + from_month + "/" + from_year);
        StringBuilder stringBuilder = new StringBuilder();
        if ((day / 10) > 0) {
            stringBuilder.append(day);
        } else {
            stringBuilder.append("" + 0 + day);
        }
        if ((month / 10) > 0) {
            stringBuilder.append("-" + month);
        } else {
            stringBuilder.append("-" + 0 + month);
        }
        stringBuilder.append("-" + year);
        Log.d(TAG, "start date:" + stringBuilder.toString());
        AddNewSlotActivity.tv_start_date.setText(stringBuilder.toString());
        AddNewSlotActivity.date_from = String.valueOf(stringBuilder);

        AddNewSlotActivity.tv_till_date.setText(date_to);
    }


    /* Initializes Completion date from TillDateDialogFragment */
    @Override
    public void setSelectedTillDate(Object o1, Object o2, Object o3, boolean b) {
        Log.d(TAG, o1.toString() + "/" + o2.toString() + "/" + o3.toString());
        if (b) {
            AddNewSlotActivity.tv_till_date.setText(getResources().getString(R.string.forever));

        } else {
            int day = Integer.parseInt(o1.toString());
            AddNewSlotActivity.till_day = day;
            int month = Integer.parseInt(o2.toString());
            AddNewSlotActivity.till_month = month;
            int year = Integer.parseInt(o3.toString());
            AddNewSlotActivity.till_year = year;
            StringBuilder stringBuilder = new StringBuilder();
            if ((day / 10) > 0) {
                stringBuilder.append(day);
            } else {
                stringBuilder.append("" + 0 + day);
            }
            if ((month / 10) > 0) {
                stringBuilder.append("-" + month);
            } else {
                stringBuilder.append("-" + 0 + month);
            }
            stringBuilder.append("-" + year);
            Log.d(TAG, "till date:" + stringBuilder.toString());
            AddNewSlotActivity.tv_till_date.setText(stringBuilder.toString());
        }

    }

    @Override
    public void setStartInitialLimit(Object o1, Object o2, Object o3) {

    }

    @Override
    public void setStartUpperLimit(Object o1, Object o2, Object o3) {

    }

    @Override
    public int[] getTillInitialLimit() {
        int[] from_date = {from_day, from_month - 1, from_year};
        return from_date;

    }

    @Override
    public void setTillUpperLimit(Object o1, Object o2, Object o3) {

    }

    /* Initializes start date from StartTimeDialogFragment*/
    @Override
    public void setSelectedStartTime(Object o1, Object o2) {
        String hour = (String) o1;
        AddNewSlotActivity.start_hour = Integer.parseInt(hour);
        String minute = (String) o2;
        AddNewSlotActivity.start_min = Integer.parseInt(minute);
        AddNewSlotActivity.time_from = hour + ":" + minute;
        tv_start_time.setText(hour + ":" + minute);

    }

    /* Initializes start date from StopTimeDialogFragment*/
    @Override
    public void setSelectedTillTime(Object o1, Object o2) {
        String hour = (String) o1;
        AddNewSlotActivity.stop_hour = Integer.parseInt(hour);
        String minute = (String) o2;
        AddNewSlotActivity.stop_min = Integer.parseInt(minute);
        AddNewSlotActivity.time_to = hour + ":" + minute;
        tv_stop_time.setText(hour + ":" + minute);
    }
}
