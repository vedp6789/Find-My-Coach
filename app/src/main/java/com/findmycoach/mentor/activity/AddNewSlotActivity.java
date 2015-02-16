package com.findmycoach.mentor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.fmc.mentor.findmycoach.R;

/**
 * Created by praka_000 on 2/12/2015.
 */
public class AddNewSlotActivity extends Activity{
    CheckBox cb_mon, cb_tue,
            cb_wed, cb_thur,
            cb_fri, cb_sat,
            cb_sun;
    Spinner sp_time_from, sp_time_half1,
            sp_time_to, sp_time_half2;



    Button b_create_slot;
    static String time_from="Select";
    static String time_to="Select";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_avail_slot);
        initialize();

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter(AddNewSlotActivity.this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.time1));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_time_from.setAdapter(arrayAdapter);
        sp_time_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                     time_from=parent.getItemAtPosition(position).toString();
                if (time_from.equals("Select")){
                    //Toast.makeText(AddNewSlotActivity.this,"Please select your slot start time.",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> arrayAdapter1=new ArrayAdapter(AddNewSlotActivity.this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.time1));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_time_to.setAdapter(arrayAdapter1);
        sp_time_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    time_to=parent.getItemAtPosition(position).toString();
                    //Toast.makeText(AddNewSlotActivity.this,".."+time_to,Toast.LENGTH_SHORT).show();
                    if (time_to.equals("Select")){
                        //Toast.makeText(AddNewSlotActivity.this,"Please select your slot completion time.",Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void initialize() {
        cb_mon = (CheckBox) findViewById(R.id.cb_mon);
        cb_tue = (CheckBox) findViewById(R.id.cb_tue);
        cb_wed = (CheckBox) findViewById(R.id.cb_wed);
        cb_thur = (CheckBox) findViewById(R.id.cb_thur);
        cb_fri = (CheckBox) findViewById(R.id.cb_fri);
        cb_sat = (CheckBox) findViewById(R.id.cb_sat);
        cb_sun = (CheckBox) findViewById(R.id.cb_sun);
        sp_time_from= (Spinner) findViewById(R.id.sp_time_from);
        sp_time_to=(Spinner) findViewById(R.id.sp_time_to);

        b_create_slot= (Button) findViewById(R.id.b_create_slot);

    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }



}
