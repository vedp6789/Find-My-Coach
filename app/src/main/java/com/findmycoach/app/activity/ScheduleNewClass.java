package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.FragmentManager;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by praka_000 on 3/4/2015.
 */
public class ScheduleNewClass extends Activity implements Button.OnClickListener, SetDate, Callback {
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
                       FragmentManager fragmentManager=getFragmentManager();
                        ChildDOB childDOB=new ChildDOB();
                        childDOB.scheduleNewClass=ScheduleNewClass.this;
                        childDOB.show(fragmentManager,null);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(ScheduleNewClass.this,"onNothingSelected",Toast.LENGTH_SHORT).show();

                }
            });



            /* Creating array list of slot timing each of 1 hour by incrementing different start time from TreeSet by 1*/
            Iterator<Float> itr = slotsTimeTreeSet.iterator();
            while (itr.hasNext()) {
                float start_time = itr.next();
                slot_timings.add(String.valueOf(start_time) + "-" + String.valueOf(++start_time));

            }



            /* Populating timing spinner and according to time selection, a filter over days availability is going to be applied. */
            ArrayAdapter arrayAdapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, slot_timings);
            arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_class_timing.setAdapter(arrayAdapter1);
            sp_class_timing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selected_time = parent.getItemAtPosition(position).toString();


                    boolean b_m = false, b_t = false, b_w = false, b_th = false, b_f = false, b_s = false, b_su = false;

                    /* Initially making all check boxes enabled and checked. These get disabled and unchecked according to timing of the slot selected by user */
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


                    if (mon_slots.length() > 0) {
                        for (int m = 0; m < mon_slots.length(); m++) {
                            try {
                                jsonArray = mon_slots.getJSONArray(m);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
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
                        for (int t = 0; t < tue_slots.length(); t++) {
                            try {
                                jsonArray = tue_slots.getJSONArray(t);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
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
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
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
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
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
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
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
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
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
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
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



                    /* Checking boolean variable for days which are not available for selected timing and making these disabled and unchecked*/
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
            stringBuilder.append("/" + month);
        } else {
            stringBuilder.append("/" + 0 + month);
        }
        stringBuilder.append("/" + year);
        Log.d(TAG, "start date:" + stringBuilder.toString());
        ScheduleNewClass.tv_from_date.setText(stringBuilder.toString());
        ScheduleNewClass.date_from = stringBuilder.toString();


        checkDurationSelected();


    }

    boolean checkDurationSelected() {

        if (ScheduleNewClass.date_from != null && ScheduleNewClass.date_to != null) {

            int start_day = Integer.parseInt(ScheduleNewClass.date_from.split("/", 3)[0]);
            int start_month = Integer.parseInt(ScheduleNewClass.date_from.split("/", 3)[1]);
            int start_year = Integer.parseInt(ScheduleNewClass.date_from.split("/", 3)[2]);

            int stop_day = Integer.parseInt(ScheduleNewClass.date_to.split("/", 3)[0]);
            int stop_month = Integer.parseInt(ScheduleNewClass.date_to.split("/", 3)[1]);
            int stop_year = Integer.parseInt(ScheduleNewClass.date_to.split("/", 3)[2]);

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
            stringBuilder.append("/" + month);
        } else {
            stringBuilder.append("/" + 0 + month);
        }
        stringBuilder.append("/" + year);
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
            String start_time = timing_selected.split("-")[0];
            String stop_time = timing_selected.split("-")[1];
            start_time = start_time.replace(".", ":");
            stop_time = stop_time.replace(".", ":");
            Log.d(TAG, "start time: hour " + start_time);
            Log.d(TAG, "start_time : " + start_time + "stop_time : " + stop_time + ", Selected start time: " + start_time.split(":")[0] + ":" + start_time.split(":")[1] + ":00");
            requestParams.add("start_time", start_time.split(":")[0] + ":" + start_time.split(":")[1] + ":00");
            requestParams.add("stop_time", stop_time.split(":")[0] + ":" + stop_time.split(":")[1] + ":00");


            StringBuilder stringBuilder1 = new StringBuilder();
            if (arrayList_days.size() > 0) {
                stringBuilder1.append(arrayList_days.get(0));
                for (int i = 1; i < arrayList_days.size(); i++) {
                    stringBuilder1.append("," + arrayList_days.get(i));
                }
            } else {
                stringBuilder1.append(arrayList_days.get(0));
            }

            String start_date=tv_from_date.getText().toString();
            String stop_date=tv_to_date.getText().toString();

            requestParams.add("dates", stringBuilder1.toString());
            requestParams.add("start_date", start_date.split("/")[2]+"-"+start_date.split("/")[1]+"-"+start_date.split("/")[0]);
            requestParams.add("stop_date", stop_date.split("/")[2]+"-"+stop_date.split("/")[1]+"-"+stop_date.split("/")[0]);
            Log.i(TAG,"Student id"+StorageHelper.getUserDetails(ScheduleNewClass.this,"user_id"));
            Log.d(TAG,"start_date ::::"+start_date.split("/")[2]+"-"+start_date.split("/")[1]+"-"+start_date.split("/")[0]+"stop_Date:::"+stop_date.split("/")[2]+"-"+stop_date.split("/")[1]+"-"+stop_date.split("/")[0]);
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





            progressDialog.show();
            NetworkClient.validateMenteeEvent(ScheduleNewClass.this, requestParams, this, 46);


            Log.d(TAG, "Data going to be validated at the time of successful date selection \n id : " + mentor_data.get("id") + ", start time : " + start_time.split(":")[0] + ":" + start_time.split(":")[1] + ":00" + ", stop time :" + stop_time.split(":")[0] + ":" + stop_time.split(":")[1] + ":00 " + " , days : " + stringBuilder1.toString() + " , start date : " + tv_from_date.getText().toString() + ", stop_date : " + tv_to_date.getText().toString() + ", sub category name : " + selected_subject.toString() + "mentee address : " + et_location.getText().toString());

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
        Toast.makeText(ScheduleNewClass.this,(String)object,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(ScheduleNewClass.this,(String)object,Toast.LENGTH_SHORT).show();
    }
}
