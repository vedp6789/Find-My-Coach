package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.findmycoach.app.R;

/**
 * Created by praka_000 on 3/4/2015.
 */
public class ScheduleNewClass extends Activity implements Button.OnClickListener{
    Spinner sp_subjects,sp_class_start_time,sp_class_stop_time,sp_start_date,sp_end_date,sp_mentor_for;
    CheckBox cb_mon,cb_tue,cb_wed,cb_thu,cb_fri,cb_sat;
    EditText et_location;
    RadioButton rb_pay_now,rb_pay_personally;
    Button payment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_new_class);

        String fname=getIntent().getExtras().getString("fname");
        initialize();
        applyActionbarProperties(fname);
        populateFields();

    }

    private void populateFields() {

    }

    private void initialize() {
        sp_subjects= (Spinner) findViewById(R.id.sp_subjects);
        sp_class_start_time= (Spinner) findViewById(R.id.sp_class_start_time);
        sp_class_stop_time= (Spinner) findViewById(R.id.sp_class_stop_time);
        sp_start_date= (Spinner) findViewById(R.id.sp_start_date);
        sp_end_date= (Spinner) findViewById(R.id.sp_end_date);
        sp_mentor_for= (Spinner) findViewById(R.id.sp_mentor_for);
        cb_mon= (CheckBox) findViewById(R.id.cb_m);
        cb_tue= (CheckBox) findViewById(R.id.cb_t);
        cb_wed=(CheckBox) findViewById(R.id.cb_w);
        cb_thu=(CheckBox) findViewById(R.id.cb_th);
        cb_fri=(CheckBox) findViewById(R.id.cb_f);
        cb_sat=(CheckBox) findViewById(R.id.cb_s);
        et_location= (EditText) findViewById(R.id.et_location);
        rb_pay_now= (RadioButton) findViewById(R.id.rb_pay_now);
        rb_pay_personally= (RadioButton) findViewById(R.id.pay_personally);
        payment= (Button) findViewById(R.id.b_proceed_to_payment);
        payment.setOnClickListener(this);



    }


    private void applyActionbarProperties(String name) {
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(name);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mentor_details, menu);
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
        if(v == payment){

        }
    }
}
