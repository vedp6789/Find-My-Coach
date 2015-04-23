package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.FragmentManager;
import android.os.Handler;
import android.provider.*;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment_mentee.ChildDOB;
import com.findmycoach.app.fragment_mentor.Schedule;
import com.findmycoach.app.fragment_mentor.StartDateDialogFragment;
import com.findmycoach.app.fragment_mentor.TillDateDialogFragment;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.SetDate;
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
import java.util.Iterator;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TreeSet;

/**
 * Created by praka_000 on 3/4/2015.
 */
public class ScheduleNewClass extends Activity implements Button.OnClickListener, SetDate, Callback {
    private LinearLayout ll_child_dob;
    public static TextView tv_child_dob;
    private static TextView tv_from_date, tv_to_date;
    Spinner sp_subjects, sp_class_timing, sp_mentor_for;
    CheckBox cb_mon, cb_tue, cb_wed, cb_thu, cb_fri, cb_sat, cb_sun;
    EditText et_location;
    RadioButton rb_pay_now, rb_pay_personally;
    private boolean dates_valid = false;
    private static Button b_payment;
    private static JSONObject mentor_Details, slots_detail, mentor_data;
    private static JSONArray jsonArray_sub_category_name, mon_slots, tue_slots, wed_slots, thu_slots, fri_slots, sat_slots, sun_slots;
    private final String TAG = "FMC";
    private ArrayList<String> sub_category_name, slot_timings;
    private static TreeSet<Float> slotsTimeTreeSet;
    private String selected_time, selected_subject, selected_mentor_for;
    private JSONArray jsonArray;
    private static ArrayList<String> arrayList_days;
    private static String mentor_availability;
    RequestParams requestParams;
    private static String date_from;
    private static String date_to;
    private static int from_day;// Starting day of schedule
    private static int from_month;//Starting month of the schedule
    private static int from_year;//Starting year of the schedule.
    private static int till_day;// completion day of the schedule
    private static int till_month;//completion month of the schedule
    private static int till_year;//completion year of the schedule
    private ProgressDialog progressDialog;
    public static String child_DOB=null;
    private Date newDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_new_class);
        String fname = getIntent().getExtras().getString("fname");
        progressDialog = new ProgressDialog(ScheduleNewClass.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        applyActionbarProperties(fname);

        initialize();


    }

    /* Populate the respective layout */
    private void populateFields() {
        sub_category_name = new ArrayList<String>();

        try {
            parseJSON();

            /* creating array of subcategories to populate subcategory/subject spinner */
            for (int i = 0; i < jsonArray_sub_category_name.length(); i++) {
                sub_category_name.add(jsonArray_sub_category_name.getString(i));
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ScheduleNewClass.this, android.R.layout.simple_spinner_item, sub_category_name.toArray(new String[sub_category_name.size()]));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_subjects.setAdapter(arrayAdapter);

            sp_subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selected_subject = (String) parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            /* Mentor-for spinner data */
            String[] mentor_for = {getResources().getString(R.string.self), getResources().getString(R.string.child)};
            ArrayAdapter arrayAdapter1_mentor_for = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mentor_for);
            arrayAdapter1_mentor_for.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_mentor_for.setAdapter(arrayAdapter1_mentor_for);
            sp_mentor_for.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(ScheduleNewClass.this,"onItemSelected",Toast.LENGTH_SHORT).show();
                    child_DOB=null;
                    selected_mentor_for=null;
                    selected_mentor_for = (String) parent.getItemAtPosition(position);
                    if(selected_mentor_for.equals(getResources().getString(R.string.child))){
                        ll_child_dob.setVisibility(View.VISIBLE);


                    }else{
                        ll_child_dob.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(ScheduleNewClass.this,"onNothingSelected",Toast.LENGTH_SHORT).show();

                }
            });



            /* Creating array list of slot timing each of 1 hour by incrementing different start time from TreeSet by 1*/
            /*Iterator<Float> itr = slotsTimeTreeSet.iterator();
            while (itr.hasNext()) {
                float start_time = itr.next();
                float stop_time=(start_time+1);
                int s_hr,s_min,st_hr,st_min;
                StringBuilder sB_s_time=new StringBuilder();
                StringBuilder sB_st_time=new StringBuilder();

                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
                try {
         *//*           Log.d(TAG," start time parsed  : "+simpleDateFormat.parse(String.valueOf(start_time).replace(".",":"))+" start time format : ");*//*
                    Date s_d=simpleDateFormat.parse(String.valueOf(start_time).replace(".",":"));
                    s_hr=s_d.getHours();
                    s_min=s_d.getMinutes();

                    Date st_d=simpleDateFormat.parse(String.valueOf(stop_time).replace(".",":"));
                    st_hr=st_d.getHours();
                    st_min=st_d.getMinutes();

                    if((s_hr / 10) > 0){
                       sB_s_time.append(s_hr);
                    }else {
                        sB_s_time.append("0"+s_hr);
                    }

                    if((s_min / 10) > 0){
                        sB_s_time.append(":"+s_min);
                    }else {
                        sB_s_time.append(":"+"0"+s_min);
                    }


                    if((st_hr / 10) > 0){
                        sB_st_time.append(st_hr);
                    }else {
                        sB_st_time.append("0"+st_hr);
                    }

                    if((st_min / 10) > 0){
                        sB_st_time.append(":"+st_min);
                    }else {
                        sB_st_time.append(":"+"0"+st_min);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d(TAG , " Time formated newly : " +sB_s_time.toString()+" - "+sB_st_time.toString());

                slot_timings.add(sB_s_time.toString() + "-" + sB_st_time.toString());

            }
*/


            ArrayList<String> timings=new ArrayList<String>();
            timings.add("00:00 - 01:00");
            timings.add("01:00 - 02:00");
            timings.add("02:00 - 03:00");
            timings.add("03:00 - 04:00");
            timings.add("04:00 - 05:00");
            timings.add("05:00 - 06:00");
            timings.add("06:00 - 07:00");
            timings.add("07:00 - 08:00");
            timings.add("08:00 - 09:00");
            timings.add("09:00 - 10:00");
            timings.add("10:00 - 11:00");
            timings.add("11:00 - 12:00");
            timings.add("12:00 - 13:00");
            timings.add("13:00 - 14:00");
            timings.add("14:00 - 15:00");
            timings.add("15:00 - 16:00");
            timings.add("16:00 - 17:00");
            timings.add("17:00 - 18:00");
            timings.add("18:00 - 19:00");
            timings.add("19:00 - 20:00");
            timings.add("20:00 - 21:00");
            timings.add("21:00 - 22:00");
            timings.add("22:00 - 23:00");
            timings.add("23:00 - 00:00");

            ArrayAdapter arrayAdapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, timings);
            arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_class_timing.setAdapter(arrayAdapter1);
            sp_class_timing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selected_time=parent.getItemAtPosition(position).toString();
                    cb_mon.setChecked(true);
                    cb_tue.setChecked(true);
                    cb_wed.setChecked(true);
                    cb_thu.setChecked(true);
                    cb_fri.setChecked(true);
                    cb_sat.setChecked(true);
                    cb_sun.setChecked(true);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            /* Populating timing spinner and according to time selection, a filter over days availability is going to be applied. *//*
            ArrayAdapter arrayAdapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, slot_timings);
            arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_class_timing.setAdapter(arrayAdapter1);
            sp_class_timing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selected_time = parent.getItemAtPosition(position).toString();


                    boolean b_m = false, b_t = false, b_w = false, b_th = false, b_f = false, b_s = false, b_su = false;

                    *//* Initially making all check boxes enabled and checked. These get disabled and unchecked according to timing of the slot selected by user *//*
                    cb_mon.setEnabled(true);
                    cb_tue.setEnabled(true);
                    cb_wed.setEnabled(true);
                    cb_thu.setEnabled(true);
                    cb_fri.setEnabled(true);
                    cb_sat.setEnabled(true);
                    cb_sun.setEnabled(true);

                    cb_mon.setChecked(true);
                    cb_tue.setChecked(true);
                    cb_wed.setChecked(true);
                    cb_thu.setChecked(true);
                    cb_fri.setChecked(true);
                    cb_sat.setChecked(true);
                    cb_sun.setChecked(true);


                    String selected_start_time = selected_time.split("-", 2)[0];
                    String selected_stop_time = selected_time.split("-", 2)[1];



                    Log.d(TAG, "selected start  time : "+selected_start_time+ ", selected_stop_time : "+selected_stop_time );


                    if (mon_slots.length() > 0) {
                        Log.d(TAG,"mon slots:"+mon_slots.length());
                        for (int m = 0; m < mon_slots.length(); m++) {
                            try {
                                jsonArray = mon_slots.getJSONArray(m);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime = start_time.split(":", 3)[0] + ":" + start_time.split(":", 3)[1];
                                String stopTime = stop_time.split(":", 3)[0] + ":" + stop_time.split(":", 3)[1];

                                Log.d(TAG,"start_time : "+start_time+" stop_time : "+stop_time+ " , startTime in hr:min format : "+startTime+ " , stop_time in hr:min format : "+ stopTime);

                                if (startTime.equals(selected_start_time) && stopTime.equals(selected_stop_time)) {
                                    Log.d(TAG,"start_time equal selected time");
                                    b_m = true;
                                } else {
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        cb_mon.setChecked(false);
                    }


                    if (tue_slots.length() > 0) {
                        Log.d(TAG, "tue slots:"+tue_slots.length());
                        for (int t = 0; t < tue_slots.length(); t++) {
                            try {
                                jsonArray = tue_slots.getJSONArray(t);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime = start_time.split(":", 3)[0] + ":" + start_time.split(":", 3)[1];
                                String stopTime = stop_time.split(":", 3)[0] + ":" + stop_time.split(":", 3)[1];
                                if (startTime.equals(selected_start_time) && stopTime.equals(selected_stop_time)) {
                                    b_t = true;
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        cb_tue.setChecked(false);
                    }


                    if (wed_slots.length() > 0) {
                        for (int w = 0; w < wed_slots.length(); w++) {
                            try {
                                jsonArray = wed_slots.getJSONArray(w);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime = start_time.split(":", 3)[0] + ":" + start_time.split(":", 3)[1];
                                String stopTime = stop_time.split(":", 3)[0] + ":" + stop_time.split(":", 3)[1];
                                if (startTime.equals(selected_start_time) && stopTime.equals(selected_stop_time)) {
                                    b_w = true;
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        cb_wed.setChecked(false);
                    }

                    if (thu_slots.length() > 0) {
                        for (int th = 0; th < thu_slots.length(); th++) {
                            try {
                                jsonArray = thu_slots.getJSONArray(th);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime = start_time.split(":", 3)[0] + ":" + start_time.split(":", 3)[1];
                                String stopTime = stop_time.split(":", 3)[0] + ":" + stop_time.split(":", 3)[1];
                                if (startTime.equals(selected_start_time) && stopTime.equals(selected_stop_time)) {
                                    b_th = true;
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        cb_thu.setChecked(false);
                    }


                    if (fri_slots.length() > 0) {
                        for (int f = 0; f < fri_slots.length(); f++) {
                            try {
                                jsonArray = fri_slots.getJSONArray(f);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime = start_time.split(":", 3)[0] + ":" + start_time.split(":", 3)[1];
                                String stopTime = stop_time.split(":", 3)[0] + ":" + stop_time.split(":", 3)[1];
                                if (startTime.equals(selected_start_time) && stopTime.equals(selected_stop_time)) {
                                    b_f = true;
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        cb_fri.setChecked(false);
                    }


                    if (sat_slots.length() > 0) {
                        for (int s = 0; s < sat_slots.length(); s++) {
                            try {
                                jsonArray = sat_slots.getJSONArray(s);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime = start_time.split(":", 3)[0] + ":" + start_time.split(":", 3)[1];
                                String stopTime = stop_time.split(":", 3)[0] + ":" + stop_time.split(":", 3)[1];
                                if (startTime.equals(selected_start_time) && stopTime.equals(selected_stop_time)) {
                                    b_s = true;
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        cb_sat.setChecked(false);
                    }


                    if (sun_slots.length() > 0) {
                        for (int su = 0; su < sun_slots.length(); su++) {
                            try {
                                jsonArray = sun_slots.getJSONArray(su);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime = start_time.split(":", 3)[0] + ":" + start_time.split(":", 3)[1];
                                String stopTime = stop_time.split(":", 3)[0] + ":" + stop_time.split(":", 3)[1];
                                if (startTime.equals(selected_start_time) && stopTime.equals(selected_stop_time)) {

                                    b_su = true;
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        cb_sun.setChecked(false);
                    }



                    *//* Checking boolean variable for days which are not available for selected timing and making these disabled and unchecked*//*
                    if (!b_m) {
                        cb_mon.setEnabled(false);
                        cb_mon.setChecked(false);
                    }
                    if (!b_t) {
                        cb_tue.setEnabled(false);
                        cb_tue.setChecked(false);
                    }
                    if (!b_w) {
                        cb_wed.setEnabled(false);
                        cb_wed.setChecked(false);
                    }
                    if (!b_th) {
                        cb_thu.setEnabled(false);
                        cb_thu.setChecked(false);
                    }
                    if (!b_f) {
                        cb_fri.setEnabled(false);
                        cb_fri.setChecked(false);
                    }
                    if (!b_s) {
                        cb_sat.setEnabled(false);
                        cb_sat.setChecked(false);
                    }
                    if (!b_su) {
                        cb_sun.setEnabled(false);
                        cb_sun.setChecked(false);

                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
*/
            /*Checking mentor's availability to mentee location, if mentor is not going to be available then address is not going to be needed from mentee so disabling it*/
            mentor_availability = mentor_data.getString("availability_yn");
            if (!mentor_availability.equals("1")) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_location);
                linearLayout.setVisibility(View.GONE);
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    /* Picking different start-time of slot, from different days */
    private void createTreeSetOfSlotStartTime() {
        slotsTimeTreeSet = new TreeSet<Float>();  /* Purpose of using TreeSet is to store slot start-time and in ascending order */
        try {
            for (int m = 0; m < mon_slots.length(); m++) {
                jsonArray = mon_slots.getJSONArray(m);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                slotsTimeTreeSet.add(v);

            }
            for (int t = 0; t < tue_slots.length(); t++) {
                jsonArray = tue_slots.getJSONArray(t);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                slotsTimeTreeSet.add(v);

            }
            for (int w = 0; w < wed_slots.length(); w++) {
                jsonArray = wed_slots.getJSONArray(w);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                slotsTimeTreeSet.add(v);

            }
            for (int th = 0; th < thu_slots.length(); th++) {
                jsonArray = thu_slots.getJSONArray(th);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                slotsTimeTreeSet.add(v);

            }
            for (int f = 0; f < fri_slots.length(); f++) {
                jsonArray = fri_slots.getJSONArray(f);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                slotsTimeTreeSet.add(v);

            }
            for (int s = 0; s < sat_slots.length(); s++) {
                jsonArray = sat_slots.getJSONArray(s);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                slotsTimeTreeSet.add(v);

            }
            for (int su = 0; su < sun_slots.length(); su++) {
                jsonArray = sun_slots.getJSONArray(su);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                slotsTimeTreeSet.add(v);

            }
            slot_timings = new ArrayList<String>();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /* parsing JSON string */
    private void parseJSON() {
        try {
            mentor_Details = new JSONObject(getIntent().getStringExtra("mentor_details"));
            mentor_data = mentor_Details.getJSONObject("data");
            slots_detail = new JSONObject();
            slots_detail = mentor_Details.getJSONObject("freeSlots");
            jsonArray_sub_category_name = mentor_data.getJSONArray("sub_category_name");
            mon_slots = slots_detail.getJSONArray("M");
            tue_slots = slots_detail.getJSONArray("T");
            wed_slots = slots_detail.getJSONArray("W");
            thu_slots = slots_detail.getJSONArray("Th");
            fri_slots = slots_detail.getJSONArray("F");
            sat_slots = slots_detail.getJSONArray("S");
            sun_slots = slots_detail.getJSONArray("Su");

            createTreeSetOfSlotStartTime();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initialize() {
        sp_subjects = (Spinner) findViewById(R.id.sp_subjects);
        sp_class_timing = (Spinner) findViewById(R.id.sp_class_time);

        tv_from_date = (TextView) findViewById(R.id.tv_date_from_dp);
        tv_from_date.setOnClickListener(this);
        tv_to_date = (TextView) findViewById(R.id.tv_date_to_dp);
        tv_to_date.setOnClickListener(this);
        tv_child_dob= (TextView) findViewById(R.id.tv_child_dob);
        tv_child_dob.setOnClickListener(this);
        ll_child_dob= (LinearLayout) findViewById(R.id.ll_child_dob);
        sp_mentor_for = (Spinner) findViewById(R.id.sp_mentor_for);
        cb_mon = (CheckBox) findViewById(R.id.cb_m);
        cb_tue = (CheckBox) findViewById(R.id.cb_t);
        cb_wed = (CheckBox) findViewById(R.id.cb_w);
        cb_thu = (CheckBox) findViewById(R.id.cb_th);
        cb_fri = (CheckBox) findViewById(R.id.cb_f);
        cb_sat = (CheckBox) findViewById(R.id.cb_s);
        cb_sun = (CheckBox) findViewById(R.id.cb_su);
        et_location = (EditText) findViewById(R.id.et_location);

        rb_pay_now = (RadioButton) findViewById(R.id.rb_pay_now);
        rb_pay_personally = (RadioButton) findViewById(R.id.pay_personally);
        b_payment = (Button) findViewById(R.id.b_proceed_to_payment);
        b_payment.setOnClickListener(this);


        populateFields();
    }


    private void applyActionbarProperties(String name) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(name);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mentor_details_not_connected, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.action_connect) {
            showAlert();
            return true;
        }*/
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_proceed_to_payment:
                Log.d(TAG, "inside button action case");
                arrayList_days = null;
                scheduleValidation();


                break;
            case R.id.tv_date_from_dp:
                FragmentManager fragmentManager = getFragmentManager();
//                Bundle bundle = new Bundle();
//                bundle.putString("ComingFrom", "ScheduleNewClass");
                StartDateDialogFragment startDateDialogFragment = new StartDateDialogFragment();
//                startDateDialogFragment.setArguments(bundle);
                startDateDialogFragment.scheduleNewClass = this;
                startDateDialogFragment.show(fragmentManager, null);
                break;
            case R.id.tv_date_to_dp:
                FragmentManager fragmentManager1 = getFragmentManager();
//                Bundle bundle1 = new Bundle();
//                bundle1.putString("ComingFrom", "ScheduleNewClass");
                TillDateDialogFragment tillDateDialogFragment = new TillDateDialogFragment();
//                tillDateDialogFragment.setArguments(bundle1);
                tillDateDialogFragment.scheduleNewClass = this;
                tillDateDialogFragment.show(fragmentManager1, null);
                break;

            case R.id.tv_child_dob:
                FragmentManager fragmentManager2=getFragmentManager();
                ChildDOB childDOB=new ChildDOB();
                childDOB.scheduleNewClass=ScheduleNewClass.this;
                childDOB.show(fragmentManager2,null);
                break;
        }
    }

    @Override
    public void setSelectedStartDate(Object o1, Object o2, Object o3) {
        dates_valid = false;
        Log.d(TAG, o1.toString() + "/" + o2.toString() + "/" + o3.toString());
        int day = Integer.parseInt(o1.toString());
        ScheduleNewClass.from_day = day;
        int month = Integer.parseInt(o2.toString());
        ScheduleNewClass.from_month = month;
        int year = Integer.parseInt(o3.toString());
        ScheduleNewClass.from_year = year;
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
        ScheduleNewClass.tv_from_date.setText(stringBuilder.toString());
        ScheduleNewClass.date_from = stringBuilder.toString();


        checkDurationSelected();


    }

    boolean checkDurationSelected() {

        if (ScheduleNewClass.date_from != null && ScheduleNewClass.date_to != null) {

            int start_day = Integer.parseInt(ScheduleNewClass.date_from.split("-", 3)[0]);
            int start_month = Integer.parseInt(ScheduleNewClass.date_from.split("-", 3)[1]);
            int start_year = Integer.parseInt(ScheduleNewClass.date_from.split("-", 3)[2]);

            int stop_day = Integer.parseInt(ScheduleNewClass.date_to.split("-", 3)[0]);
            int stop_month = Integer.parseInt(ScheduleNewClass.date_to.split("-", 3)[1]);
            int stop_year = Integer.parseInt(ScheduleNewClass.date_to.split("-", 3)[2]);

            Log.d(TAG, "Start_date  and Stop details in int variable from setSelectedStartDate method : " + start_day + " " + start_month + " " + start_year + "  :  " + stop_day + " " + stop_month + " " + stop_year);
            if (stop_year < start_year) {

                Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.wrong_duration), Toast.LENGTH_LONG).show();
                showErrorMessage(tv_to_date, getResources().getString(R.string.stop_date_should_be_greater));

            } else {
                if (stop_year == start_year) {
                    if (stop_month < start_month) {
                        Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.wrong_duration), Toast.LENGTH_LONG).show();
                        showErrorMessage(tv_to_date, getResources().getString(R.string.stop_date_should_be_greater));

                    } else {
                        if (stop_month == start_month) {
                            if (stop_day < start_day) {
                                Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.wrong_duration), Toast.LENGTH_LONG).show();
                                showErrorMessage(tv_to_date, getResources().getString(R.string.stop_date_should_be_greater));
                            } else {
                                dates_valid = true;
                            }
                        } else {
                            dates_valid = true;
                        }
                    }
                } else {
                    dates_valid = true;
                }
            }

            return true;     // This true means there is error in dates selected
        } else {
            return false;    // This false means duration is not completely filled
        }
    }

    @Override
    public void setSelectedTillDate(Object o1, Object o2, Object o3, boolean b) {      /* fourth parameter is not going to be used here */
        dates_valid = false;
        Log.d(TAG, o1.toString() + "/" + o2.toString() + "/" + o3.toString());

        int day = Integer.parseInt(o1.toString());
        ScheduleNewClass.till_day = day;
        int month = Integer.parseInt(o2.toString());
        ScheduleNewClass.till_month = month;
        int year = Integer.parseInt(o3.toString());
        ScheduleNewClass.till_year = year;
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
        ScheduleNewClass.tv_to_date.setText(stringBuilder.toString());
        date_to = stringBuilder.toString();

        checkDurationSelected();


    }

    private boolean days_checked() {

        arrayList_days = new ArrayList<String>();

        if (cb_mon.isEnabled() && cb_mon.isChecked()) {
            arrayList_days.add("M");
        }
        if (cb_tue.isEnabled() && cb_tue.isChecked()) {
            arrayList_days.add("T");
        }
        if (cb_wed.isEnabled() && cb_wed.isChecked()) {
            arrayList_days.add("W");
        }
        if (cb_thu.isEnabled() && cb_thu.isChecked()) {
            arrayList_days.add("Th");

        }
        if (cb_fri.isEnabled() && cb_fri.isChecked()) {
            arrayList_days.add("F");
        }
        if (cb_sat.isEnabled() && cb_sat.isChecked()) {
            arrayList_days.add("S");
        }
        if (cb_sun.isEnabled() && cb_sun.isChecked()) {
            arrayList_days.add("Su");
        }
        Log.d(TAG, "arrayList_days size" + arrayList_days.size());
        if (arrayList_days.size() > 0) {
            return true;

        } else {

            return false;

        }


    }

    private void scheduleValidation() {
        if (dates_valid) {
            if (days_checked()) {
                if(checkDaysAvailability(tv_from_date.getText().toString(),tv_to_date.getText().toString())){
                    if (mentor_availability.equals("1")) {
                        Log.d(TAG, "mentor_availability equals 1");
                        String location = et_location.getText().toString();
                        Log.d(TAG, "address string size : " + location.trim().length());
                        if (location.equals("") || location.trim().length() == 0) {
                            showErrorMessage1(et_location, getResources().getString(R.string.error_field_required));
                            Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.your_address_please), Toast.LENGTH_SHORT).show();
                        } else {
                            checkForValidity();
                        }
                    } else {
                        checkForValidity();
                    }
                }


            } else {
                Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.atleast_a_week_day), Toast.LENGTH_SHORT).show();
            }


        } else {
            if (days_checked()) {
                if (checkDurationSelected()) {
                    showErrorMessage(tv_to_date, getResources().getString(R.string.stop_date_should_be_greater));

                } else {
                    Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.duration_please), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.atleast_a_week_day), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void checkForValidity() {
        if(selected_mentor_for.equals("Child")){

            Log.i(TAG,"child dob "+child_DOB);
            if(child_DOB != null){
                Log.d(TAG,"Child DOB :"+child_DOB);
                proceedWithValidity();
            }else{
                Toast.makeText(ScheduleNewClass.this,"Child's date of birth please! ",Toast.LENGTH_LONG).show();
                FragmentManager fragmentManager=getFragmentManager();
                ChildDOB childDOB=new ChildDOB();
                childDOB.show(fragmentManager,null);
            }
        }else{
            proceedWithValidity();
        }

}

    private void proceedWithValidity() {
        try {
            requestParams = new RequestParams();
            requestParams.add("student_id", StorageHelper.getUserDetails(ScheduleNewClass.this,"user_id"));
            Log.i(TAG,"mentor_id"+mentor_data.getString("id"));
            requestParams.add("mentor_id", mentor_data.getString("id"));
            Log.d(TAG, "Timing selected :" + String.valueOf(selected_time));
            String timing_selected = String.valueOf(selected_time);
            String start_time = timing_selected.split(" - ")[0];
            String stop_time = timing_selected.split(" - ")[1];
            /*start_time = start_time.replace(".", ":");
            stop_time = stop_time.replace(".", ":");
            */
            Log.d(TAG, "start time: hour " + start_time);
            Log.d(TAG, "start_time : " + start_time + "stop_time : " + stop_time + ", Selected start time: " + start_time.split(":")[0] + ":" + start_time.split(":")[1] + ":00");
            requestParams.add("start_time", start_time.split(":")[0] + ":" + start_time.split(":")[1] + ":00");
            requestParams.add("stop_time", stop_time.split(":")[0] + ":" + stop_time.split(":")[1] + ":00");


           /*
            requestParams.add("start_time", start_time);
            requestParams.add("stop_time", stop_time);
*/
            StringBuilder stringBuilder1 = new StringBuilder();


            ArrayList<String> a_days=getListForCheckedDays1();
            if (a_days.size() > 0) {
                stringBuilder1.append(a_days.get(0));
                for (int i = 1; i < a_days.size(); i++) {
                    stringBuilder1.append("," + a_days.get(i));
                }
            } else {
                stringBuilder1.append(a_days.get(0));
            }


            /*if (arrayList_days.size() > 0) {
                stringBuilder1.append(arrayList_days.get(0));
                for (int i = 1; i < arrayList_days.size(); i++) {
                    stringBuilder1.append("," + arrayList_days.get(i));
                }
            } else {
                stringBuilder1.append(arrayList_days.get(0));
            }*/

            String start_date=tv_from_date.getText().toString();
            String stop_date=tv_to_date.getText().toString();

            requestParams.add("dates", stringBuilder1.toString());
            requestParams.add("start_date", start_date.split("-")[2]+"-"+start_date.split("-")[1]+"-"+start_date.split("-")[0]);
            requestParams.add("stop_date", stop_date.split("-")[2]+"-"+stop_date.split("-")[1]+"-"+stop_date.split("-")[0]);
            Log.i(TAG,"Student id"+StorageHelper.getUserDetails(ScheduleNewClass.this,"user_id"));
            Log.d(TAG,"start_date ::::"+start_date.split("-")[2]+"-"+start_date.split("-")[1]+"-"+start_date.split("-")[0]+"stop_Date:::"+stop_date.split("-")[2]+"-"+stop_date.split("-")[1]+"-"+stop_date.split("-")[0]);
            requestParams.add("sub_category_name", selected_subject);
            requestParams.add("availability",mentor_availability);
            if (mentor_availability.equals("1")) {
                requestParams.add("location", et_location.getText().toString());
            }
            if(selected_mentor_for.equals("Child")){
                requestParams.add("date_of_birth_kid",child_DOB);
            }
            //requestParams.add("mentor_for", selected_mentor_for);



            if(rb_pay_now.isChecked()){
                requestParams.add("payment","1");  /* Flag is 1 if mentee  selected pay now*/

            }else{
                requestParams.add("payment","0");  /* Flag is 0 if mentee selected payment personally */
            }




            Log.d(TAG, "Data going to be validated at the time of successful date selection \n id : " + mentor_data.get("id") + ", start time : " + start_time + ", stop time :" +stop_time +" , days : " + stringBuilder1.toString() + " , start date : " + start_date.split("-")[2]+"-"+start_date.split("-")[1]+"-"+start_date.split("-")[0] + ", stop_date : " + stop_date.split("-")[2]+"-"+stop_date.split("-")[1]+"-"+stop_date.split("-")[0] + ", sub category name : " + selected_subject.toString() + "mentee address : " + et_location.getText().toString());

            progressDialog.show();
            NetworkClient.validateMenteeEvent(ScheduleNewClass.this, requestParams, this, 46);


          //  Log.d(TAG, "Data going to be validated at the time of successful date selection \n id : " + mentor_data.get("id") + ", start time : " + start_time.split(":")[0] + ":" + start_time.split(":")[1] + ":00" + ", stop time :" + stop_time.split(":")[0] + ":" + stop_time.split(":")[1] + ":00 " + " , days : " + stringBuilder1.toString() + " , start date : " + tv_from_date.getText().toString() + ", stop_date : " + tv_to_date.getText().toString() + ", sub category name : " + selected_subject.toString() + "mentee address : " + et_location.getText().toString());

            Log.d(TAG, "Can start network communication");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showErrorMessage(final TextView view, String string) {
        view.setError(string);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setError(null);
            }
        }, 3500);
    }


    private void showErrorMessage1(final EditText view, String string) {
        view.setError(string);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setError(null);
            }
        }, 3500);
    }

    /* This method generates message, when either slots coincide or there is coinciding exceptions like vacation*/
    void coincideOf(JSONArray jsonArray, int flag) {      /* flag 0 means class is coinciding with another class and flag 1 means there is some exceptions while this schedule like mentor has already scheduled some vaccations and flag 2 is to instruct mentee that if he do some changes according to suggestion he can schedule a class. */

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
                        stringBuilder.append("Sorry, there is already a class between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":") + " for " + stringBuilder1.toString() + " \n So this class cannot be scheduled!");
                        showCoincidingAlertMessage(stringBuilder.toString(), flag);
                        Log.d(TAG, "Message for coinciding class schedule : " + stringBuilder.toString());
                    }else{
                        if(flag == 1){
                            stringBuilder.append("Hi, your class is successfully scheduled but mentor will not be available between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":") + " for " + stringBuilder1.toString() + " \n !");
                            showCoincidingAlertMessage(stringBuilder.toString(), flag);
                            Log.d(TAG, "Message for coinciding exception : " + stringBuilder.toString());
                        }else{
                            stringBuilder.append("Sorry, mentor is not available as per your desired schedule.\n You can schedule your class in between " + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":") + " for " + stringBuilder1.toString());
                            showCoincidingAlertMessage(stringBuilder.toString(), flag);
                            Log.d(TAG, "Message for availability of class that can be possible to schedule : " + stringBuilder.toString());
                        }
                    }


                } else {
                    /* if condition is checking whether the flag is 0 or 1 in case of multiple coincidiing slots with no week-days, or in case of multiple coinciding exceptions  in Json string .*/
                    if (flag == 0) {
                        stringBuilder.append("Sorry, there is already a class between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":") +" \n So this class cannot be scheduled!");
                        showCoincidingAlertMessage(stringBuilder.toString(), flag);
                        Log.d(TAG, "Message for coinciding class schedule : " + stringBuilder.toString());
                    }else{
                        if(flag == 1){
                            stringBuilder.append("Hi, your class is successfully scheduled but mentor will not be available between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":") +" !");
                            showCoincidingAlertMessage(stringBuilder.toString(), flag);
                            Log.d(TAG, "Message for coinciding exception : " + stringBuilder.toString());
                        }else{
                            stringBuilder.append("Sorry, mentor is not available as per your desired schedule.\n You can schedule your class in between " + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":") +"!");
                            showCoincidingAlertMessage(stringBuilder.toString(), flag);
                            Log.d(TAG, "Message for availability of class that can be possible to schedule : " + stringBuilder.toString());
                        }
                    }
                }

            }


        } else {
            if (jsonArray.length() > 0) {
                try {
                    String s_date, st_date, s_time, st_time;
                    ArrayList<String> days = new ArrayList<String>();

                    JSONObject jO_coinciding_detail = jsonArray.getJSONObject(0);

                    /*String week_days=jO_coinciding_detail.getString("week_days");
                    String [] week_days_array=week_days.split(",");
                    if(week_days_array.length > 0){
                        for(int i=0 ; i < week_days_array.length ; i++){
                            days.add(week_days_array[i]);
                        }
                    }*/


                    /*  Commented because week_days are coming as string not an array */

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
                            stringBuilder.append("Sorry, there is already a class between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0, 5) + " to " + st_time.substring(0, 5) + " for " + stringBuilder1.toString() +  " \n So this class cannot be scheduled!");
                            showCoincidingAlertMessage(stringBuilder.toString(), flag);
                            Log.d(TAG, "Message for coinciding class schedule : " + stringBuilder.toString());
                        }else{
                            if(flag == 1){
                                stringBuilder.append("Hi, your class is successfully scheduled but mentor will not be available between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0,5) + " to " + s_time.substring(0,5) + " for " + stringBuilder1.toString() + " \n !");
                                showCoincidingAlertMessage(stringBuilder.toString(), flag);
                                Log.d(TAG, "Message for coinciding exception : " + stringBuilder.toString());
                            }else{
                                stringBuilder.append("Sorry, mentor is not available as per your desired schedule.\n You can schedule your class in between " + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0,5) + " to " + st_time.substring(0,5)+ " for " + stringBuilder1.toString());
                                showCoincidingAlertMessage(stringBuilder.toString(), flag);
                                Log.d(TAG, "Message for availability of class that can be possible to schedule : " + stringBuilder.toString());
                            }
                        }




                    } else {

                    /* if condition is checking whether the flag is 0 or 1 in case of single coinciding slot,or in case of single coinciding exception in Json string.*/
                        if (flag == 0) {
                            stringBuilder.append("Sorry, there is already a class between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0, 5) + " to " + st_time.substring(0, 5) +" \n So this class cannot be scheduled!");
                            showCoincidingAlertMessage(stringBuilder.toString(), flag);
                            Log.d(TAG, "Message for coinciding class schedule : " + stringBuilder.toString());
                        }else{
                            if(flag == 1){
                                stringBuilder.append("Hi, your class is successfully scheduled but mentor will not be available between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0,5) + " to " + s_time.substring(0,5) + " \n !");
                                showCoincidingAlertMessage(stringBuilder.toString(), flag);
                                Log.d(TAG, "Message for coinciding exception : " + stringBuilder.toString());
                            }else{
                                stringBuilder.append("Sorry, mentor is not available as per your desired schedule.\n You can schedule your class in between " + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0,5) + " to " + st_time.substring(0,5)+ " ! ");
                                showCoincidingAlertMessage(stringBuilder.toString(), flag);
                                Log.d(TAG, "Message for availability of class that can be possible to schedule : " + stringBuilder.toString());
                            }
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
                    .setTitle("Coinciding class")
                    .setMessage(message)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            if(flag == 1){
                new AlertDialog.Builder(this)
                        .setTitle("Mentor unavailability")
                        .setMessage(message)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }else{
                new AlertDialog.Builder(this)
                        .setTitle("Schedule availabilty")
                        .setMessage(message)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

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
                    Toast.makeText(ScheduleNewClass.this, stringBuilder.toString() +" "+ getResources().getString(R.string.out_of_duration), Toast.LENGTH_LONG).show();
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
        if (cb_thu.isChecked())
            days_checked.add("5");
        if (cb_fri.isChecked())
            days_checked.add("6");
        if (cb_sat.isChecked())
            days_checked.add("7");
        if (cb_sun.isChecked())
            days_checked.add("1");

        return days_checked;

    }

    private ArrayList<String> getListForCheckedDays1() {
        ArrayList<String> days_checked = new ArrayList<String>();
        if (cb_mon.isChecked())
            days_checked.add("M");
        if (cb_tue.isChecked())
            days_checked.add("T");
        if (cb_wed.isChecked())
            days_checked.add("W");
        if (cb_thu.isChecked())
            days_checked.add("Th");
        if (cb_fri.isChecked())
            days_checked.add("F");
        if (cb_sat.isChecked())
            days_checked.add("S");
        if (cb_sun.isChecked())
            days_checked.add("Su");

        return days_checked;

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

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();

        if(statusCode == 200){
            Log.d(TAG,"success response : "+String.valueOf(object));
            try {
                JSONObject jO_response=new JSONObject(String.valueOf(object));
                String message=jO_response.getString("message");
                if(message.equalsIgnoreCase("success")) {
                    Toast.makeText(ScheduleNewClass.this,"Your schedule request get submitted.",Toast.LENGTH_SHORT).show();
                    /*Log.d(TAG,"success");
                    JSONArray jA_exceptions=jO_response.getJSONArray("coincidingExceptions");
                    if(jA_exceptions.length() > 0){
                        coincideOf(jA_exceptions,1);
                    }else{
                        Toast.makeText(ScheduleNewClass.this,getResources().getString(R.string.class_is_available), Toast.LENGTH_SHORT).show();
                    }
*/

                }else {
                    Toast.makeText(ScheduleNewClass.this,"Mentor is not available for your requested schedule.",Toast.LENGTH_SHORT).show();
                   /* Log.d(TAG,"failure");
                    JSONArray jA_availability=jO_response.getJSONArray("availability");
                    if(jA_availability.length() > 0){
                        coincideOf(jA_availability,2);
                    }
*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Toast.makeText(ScheduleNewClass.this,(String)object,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Log.d(TAG,"failure response : "+String.valueOf(object));
        if(statusCode == 403){
            try {
                JSONObject jO_resp=new JSONObject(String.valueOf(object));
                String message=jO_resp.getString("message");
                if(message.equals("Success")){
                    Toast.makeText(ScheduleNewClass.this,"Your schedule request get submitted.",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ScheduleNewClass.this,"Mentor is not available for your requested schedule.",Toast.LENGTH_SHORT).show();
                }
                //JSONArray jA_Coinciding_Class=jO_resoponse.getJSONArray("coincidingEvents");

                /*if(jA_Coinciding_Class.length() > 0){
                    coincideOf(jA_Coinciding_Class,0);
                }
*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(ScheduleNewClass.this,getResources().getString(R.string.problem_in_connection_server),Toast.LENGTH_SHORT).show();
        }


      //  Toast.makeText(ScheduleNewClass.this,(String)object,Toast.LENGTH_SHORT).show();
    }
}
