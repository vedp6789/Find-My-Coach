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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

/**
 * Created by praka_000 on 3/4/2015.
 */
public class ScheduleNewClass extends Activity implements Button.OnClickListener {

    private LinearLayout ll_child_dob,ll_location;
    public static TextView tv_child_dob;
    private static TextView tv_from_date, tv_to_date, tv_class_timing, tv_subject;
    Spinner sp_subjects, sp_mentor_for;
    CheckBox cb_mon, cb_tue, cb_wed, cb_thu, cb_fri, cb_sat, cb_sun;
    EditText et_location;
    RadioButton rb_pay_now, rb_pay_personally;
    private static Button b_payment;
    private final String TAG = "FMC";
    private String selected_mentor_for;
    RequestParams requestParams;
    private ProgressDialog progressDialog;
    public static String child_DOB = null;
    Bundle bundle;
    private String selected_subject = null;

    private Long slot_id;
    private String mentor_id;
    private String mentor_availability;
    private int slot_start_day;
    private int slot_start_month;
    private int slot_start_year;
    private int slot_stop_day;
    private int slot_stop_month;
    private int slot_stop_year;
    private int slot_start_hour;
    private int slot_start_minute;
    private int slot_stop_hour;
    private int slot_stop_minute;
    private ArrayList<String> arrayList_subcategory = null;
    private String[] slot_on_week_days;
    private String charges;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_new_class);
        String fname = getIntent().getExtras().getString("fname");
        progressDialog = new ProgressDialog(ScheduleNewClass.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        applyActionbarProperties(fname);
        initialize();
        bundle = getIntent().getBundleExtra("slot_bundle");
        finalizeDateTimeAndCharges();
        populateFields();
    }

    private void finalizeDateTimeAndCharges() {
        slot_id = bundle.getLong("slot_id");
        mentor_id = bundle.getString("mentor_id");
        mentor_availability = bundle.getString("mentor_availability");
        slot_start_day = bundle.getInt("slot_start_day");
        slot_start_month = bundle.getInt("slot_start_month");
        slot_start_year = bundle.getInt("slot_start_year");
        slot_stop_day = bundle.getInt("slot_stop_day");
        slot_stop_month = bundle.getInt("slot_stop_month");
        slot_stop_year = bundle.getInt("slot_stop_year");
        slot_start_hour = bundle.getInt("slot_start_hour");
        slot_start_minute = bundle.getInt("slot_start_minute");
        slot_stop_hour = bundle.getInt("slot_stop_hour");
        slot_stop_minute = bundle.getInt("slot_stop_minute");
        slot_on_week_days = bundle.getStringArray("slot_on_week_days");
        charges = bundle.getString("charges");
        arrayList_subcategory = bundle.getStringArrayList("arrayList_sub_category");


        if (arrayList_subcategory.size() > 1) {
            sp_subjects.setVisibility(View.VISIBLE);
            ArrayAdapter arrayAdapter_sub_category = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList_subcategory);
            arrayAdapter_sub_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_subjects.setAdapter(arrayAdapter_sub_category);
            sp_subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selected_subject = (String) parent.getItemAtPosition(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(ScheduleNewClass.this, "onNothingSelected", Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            tv_subject.setVisibility(View.VISIBLE);
            selected_subject = arrayList_subcategory.get(0);
            tv_subject.setText(selected_subject);
        }

        String timing = String.format("%02d:%02d to %02d:%02d", slot_start_hour, slot_start_minute, slot_stop_hour, slot_stop_minute);
        tv_class_timing.setText(timing);


        for (int slot_week_day = 0; slot_week_day < slot_on_week_days.length; slot_week_day++) {
            String day = slot_on_week_days[slot_week_day];
            if (day.equals("M")) {
                cb_mon.setChecked(true);
            }
            if (day.equals("T")) {
                cb_tue.setChecked(true);
            }
            if (day.equals("W")) {
                cb_wed.setChecked(true);
            }
            if (day.equals("Th")) {
                cb_thu.setChecked(true);
            }
            if (day.equals("F")) {
                cb_fri.setChecked(true);
            }
            if (day.equals("S")) {
                cb_sat.setChecked(true);
            }
            if (day.equals("Su")) {
                cb_sun.setChecked(true);
            }
        }

        cb_mon.setEnabled(false);
        cb_tue.setEnabled(false);
        cb_wed.setEnabled(false);
        cb_thu.setEnabled(false);
        cb_fri.setEnabled(false);
        cb_sat.setEnabled(false);
        cb_sun.setEnabled(false);

        Log.d(TAG, "mentor availability : " + bundle.getString("mentor_availability"));


        Calendar cal = new GregorianCalendar();
        cal.set(slot_start_year, slot_start_month - 1, slot_start_day);
        long slot_start_date = cal.getTimeInMillis();


        Calendar rightNow = Calendar.getInstance();
        long rightNow_in_millis = rightNow.getTimeInMillis();



        if (rightNow_in_millis > slot_start_date) {
            String from_date = String.format("%02d-%02d-%d", rightNow.get(Calendar.DAY_OF_MONTH), (rightNow.get(Calendar.MONTH) + 1), rightNow.get(Calendar.YEAR));
            tv_from_date.setText(from_date);
        } else {
            String to_date = String.format("%02d-%02d-%d", slot_start_day, slot_start_month, slot_start_year);
            tv_from_date.setText(to_date);
        }


        String to_date=String.format("%02d-%02d-%d", slot_stop_day,slot_stop_month,slot_stop_year);
        tv_to_date.setText(to_date);


        if(mentor_availability !=null && mentor_availability.equals("1"))
            ll_location.setVisibility(View.VISIBLE);


        int current_hour=rightNow.get(Calendar.HOUR_OF_DAY);
        int current_minute=rightNow.get(Calendar.MINUTE);


        boolean include_class_start_date_for_charge=false;
        if( (current_hour<slot_stop_hour) ){
            include_class_start_date_for_charge=true;
        }else {
            if(current_hour == slot_stop_hour && current_minute < slot_stop_minute)
                include_class_start_date_for_charge=true;

        }


        String class_schedule_start_date=tv_from_date.getText().toString();   /* Date which is getting prompted to student, from where student class schedule starts */
        int class_schedule_start_day=Integer.parseInt(class_schedule_start_date.split("-",3)[0]);
        int class_schedule_start_month=Integer.parseInt(class_schedule_start_date.split("-",3)[1]);
        int class_schedule_start_year= Integer.parseInt(class_schedule_start_date.split("-",3)[2]);


        Calendar calendar_stop_date_of_schedule=Calendar.getInstance();
        calendar_stop_date_of_schedule.set(slot_stop_year,slot_stop_month-1,slot_stop_day);

        Calendar calendar_schedule_start_date=Calendar.getInstance();
        calendar_schedule_start_date.set(class_schedule_start_year,class_schedule_start_month-1,class_schedule_start_day);

        int valid_class_days=0;
        /* Calculation of charge to pay */
        if(include_class_start_date_for_charge){
            /* It means that if a class is on start day of the schedule, then it is going to be count as class */


              valid_class_days=calculateNoOfTotalClassDays(calendar_schedule_start_date,calendar_stop_date_of_schedule,slot_on_week_days);
        }else{
            /* class is not going to be count for first day of schedule as current time is greater than slot_stop_time*/
            calendar_schedule_start_date.add(Calendar.DATE,1);
            valid_class_days=calculateNoOfTotalClassDays(calendar_schedule_start_date,calendar_stop_date_of_schedule,slot_on_week_days);
        }


        Log.d(TAG,"valid days "+valid_class_days);









    }

    private int calculateNoOfTotalClassDays(Calendar calendar_schedule_start_date, Calendar calendar_stop_date_of_schedule,String [] slot_on_week_days) {

        int workDays = 0;

        //Return 0 if start and end are the same
        if (calendar_schedule_start_date.getTimeInMillis() == calendar_stop_date_of_schedule.getTimeInMillis()) {
            return 0;
        }

        List<Integer> selectedDays = new ArrayList<>();
        for(String d : slot_on_week_days){
            if(d.equalsIgnoreCase("su"))
                selectedDays.add(1);
            if(d.equalsIgnoreCase("m"))
                selectedDays.add(2);
            if(d.equalsIgnoreCase("t"))
                selectedDays.add(3);
            if(d.equalsIgnoreCase("w"))
                selectedDays.add(4);
            if(d.equalsIgnoreCase("th"))
                selectedDays.add(5);
            if(d.equalsIgnoreCase("f"))
                selectedDays.add(6);
            if(d.equalsIgnoreCase("s"))
                selectedDays.add(7);
        }

        do {
            //excluding start date

            if (selectedDays.contains(calendar_schedule_start_date.get(Calendar.DAY_OF_WEEK))) {
                ++workDays;
                Log.d(TAG,"Selected wee day : "+calendar_schedule_start_date.get(Calendar.DAY_OF_WEEK));
            }
            calendar_schedule_start_date.add(Calendar.DAY_OF_MONTH, 1);



        } while (calendar_schedule_start_date.getTimeInMillis() <= calendar_stop_date_of_schedule.getTimeInMillis()); //excluding end date

        return workDays;
    }


    private void populateFields() {


        String[] mentor_for = {getResources().getString(R.string.self), getResources().getString(R.string.child)};
        ArrayAdapter arrayAdapter1_mentor_for = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mentor_for);
        arrayAdapter1_mentor_for.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_mentor_for.setAdapter(arrayAdapter1_mentor_for);
        sp_mentor_for.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ScheduleNewClass.this, "onItemSelected", Toast.LENGTH_SHORT).show();
                child_DOB = null;
                selected_mentor_for = null;
                selected_mentor_for = (String) parent.getItemAtPosition(position);
                if (selected_mentor_for.equals(getResources().getString(R.string.child))) {
                    ll_child_dob.setVisibility(View.VISIBLE);


                } else {
                    ll_child_dob.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ScheduleNewClass.this, "onNothingSelected", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void initialize() {
        sp_subjects = (Spinner) findViewById(R.id.sp_subjects);
        tv_class_timing = (TextView) findViewById(R.id.tv_class_timing);
        tv_subject = (TextView) findViewById(R.id.tv_subject);
        tv_subject.setVisibility(View.GONE);
        sp_subjects.setVisibility(View.GONE);
        tv_from_date = (TextView) findViewById(R.id.tv_date_from_dp);
        tv_from_date.setOnClickListener(this);
        tv_to_date = (TextView) findViewById(R.id.tv_date_to_dp);
        tv_to_date.setOnClickListener(this);
        tv_child_dob = (TextView) findViewById(R.id.tv_child_dob);
        tv_child_dob.setOnClickListener(this);
        ll_child_dob = (LinearLayout) findViewById(R.id.ll_child_dob);
        ll_location= (LinearLayout) findViewById(R.id.ll_location);
        ll_location.setVisibility(View.GONE);
        sp_mentor_for = (Spinner) findViewById(R.id.sp_mentor_for);
        cb_mon = (CheckBox) findViewById(R.id.cb_m);
        cb_mon.setChecked(false);
        cb_tue = (CheckBox) findViewById(R.id.cb_t);
        cb_tue.setChecked(false);
        cb_wed = (CheckBox) findViewById(R.id.cb_w);
        cb_wed.setChecked(false);
        cb_thu = (CheckBox) findViewById(R.id.cb_th);
        cb_thu.setChecked(false);
        cb_fri = (CheckBox) findViewById(R.id.cb_f);
        cb_fri.setChecked(false);
        cb_sat = (CheckBox) findViewById(R.id.cb_s);
        cb_sat.setChecked(false);
        cb_sun = (CheckBox) findViewById(R.id.cb_su);
        cb_sun.setChecked(false);


        et_location = (EditText) findViewById(R.id.et_location);

        rb_pay_now = (RadioButton) findViewById(R.id.rb_pay_now);
        rb_pay_personally = (RadioButton) findViewById(R.id.pay_personally);
        b_payment = (Button) findViewById(R.id.b_proceed_to_payment);
        b_payment.setOnClickListener(this);


    }


    private void applyActionbarProperties(String name) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(name);
        }
    }


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

                break;
            case R.id.tv_date_from_dp:
                FragmentManager fragmentManager = getFragmentManager();
                StartDateDialogFragment startDateDialogFragment = new StartDateDialogFragment();
                startDateDialogFragment.scheduleNewClass = this;
                startDateDialogFragment.show(fragmentManager, null);
                break;
            case R.id.tv_date_to_dp:
                FragmentManager fragmentManager1 = getFragmentManager();
                TillDateDialogFragment tillDateDialogFragment = new TillDateDialogFragment();
                tillDateDialogFragment.scheduleNewClass = this;
                tillDateDialogFragment.show(fragmentManager1, null);
                break;

            case R.id.tv_child_dob:
                FragmentManager fragmentManager2 = getFragmentManager();
                ChildDOB childDOB = new ChildDOB();
                childDOB.scheduleNewClass = ScheduleNewClass.this;
                childDOB.show(fragmentManager2, null);
                break;
        }
    }

}
