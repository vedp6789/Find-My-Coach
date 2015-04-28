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
import java.util.Locale;
import java.util.TreeSet;

/**
 * Created by praka_000 on 3/4/2015.
 */
public class ScheduleNewClass extends Activity implements Button.OnClickListener {

    private LinearLayout ll_child_dob;
    public static TextView tv_child_dob;
    private static TextView tv_from_date, tv_to_date,tv_class_timing,tv_subject;
    Spinner sp_subjects,  sp_mentor_for;
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
    Calendar calendar_current_date;

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
    private ArrayList<String> arrayList_subcategory=null;
    private String [] slot_on_week_days;
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
        slot_id=bundle.getLong("slot_id");
        mentor_id=bundle.getString("mentor_id");
        mentor_availability=bundle.getString("mentor_availability");
        slot_start_day=bundle.getInt("slot_start_day");
        slot_start_month=bundle.getInt("slot_start_month");
        slot_start_year=bundle.getInt("slot_start_year");
        slot_stop_day=bundle.getInt("slot_stop_day");
        slot_stop_month=bundle.getInt("slot_stop_month");
        slot_stop_year=bundle.getInt("slot_stop_year");
        slot_start_hour=bundle.getInt("slot_start_hour");
        slot_start_minute=bundle.getInt("slot_start_minute");
        slot_stop_hour=bundle.getInt("slot_stop_hour");
        slot_stop_minute=bundle.getInt("slot_stop_minute");
        slot_on_week_days=bundle.getStringArray("slot_on_week_days");
        charges=bundle.getString("charges");
        arrayList_subcategory=bundle.getStringArrayList("arrayList_sub_category");

        Calendar cal = new GregorianCalendar();
        cal.set(2012, 11, 26);
        Long slot_start_date=cal.getTimeInMillis();


        Calendar rightNow = Calendar.getInstance();
        Long current_date=rightNow.getTimeInMillis();

        if(current_date > slot_start_date){

        }else{

        }


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
        tv_class_timing= (TextView) findViewById(R.id.tv_class_timing);
        tv_subject= (TextView) findViewById(R.id.tv_subject);
        tv_subject.setVisibility(View.GONE);
        sp_subjects.setVisibility(View.GONE);
        tv_from_date = (TextView) findViewById(R.id.tv_date_from_dp);
        tv_from_date.setOnClickListener(this);
        tv_to_date = (TextView) findViewById(R.id.tv_date_to_dp);
        tv_to_date.setOnClickListener(this);
        tv_child_dob = (TextView) findViewById(R.id.tv_child_dob);
        tv_child_dob.setOnClickListener(this);
        ll_child_dob = (LinearLayout) findViewById(R.id.ll_child_dob);
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
