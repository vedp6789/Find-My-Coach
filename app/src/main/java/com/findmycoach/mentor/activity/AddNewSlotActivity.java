package com.findmycoach.mentor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.findmycoach.mentor.fragment.StartDateDialogFragment;
import com.findmycoach.mentor.fragment.TillDateDialogFragment;
import com.findmycoach.mentor.util.SetDate;
import com.fmc.mentor.findmycoach.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by praka_000 on 2/12/2015.
 */
public class AddNewSlotActivity extends Activity implements SetDate {
    CheckBox cb_mon, cb_tue,
            cb_wed, cb_thur,
            cb_fri, cb_sat,
            cb_sun;
    Spinner sp_time_from, sp_time_half1,
            sp_time_to, sp_time_half2;

    public static EditText et_start_date,et_till_date;
boolean b_mon_checked,b__checked
        ,b_mon_checked,b_mon_checked,b_mon_checked
    b_mon_checked,b_mon_checked
    Button b_create_slot;
    private static String time_from;
    private static String time_to;
    private static String date_from;
    private static String date_to;
    private static int from_day;// Starting day of the slot
    private static int from_month;//Starting month of the slot
    private static int from_year;//Starting year of the slot.

    private static final String TAG="FMC";
    private static String FOREVER;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_avail_slot);
        time_from=getResources().getString(R.string.select);
        time_to=getResources().getString(R.string.select);
        date_to=getResources().getString(R.string.forever);
        FOREVER=getResources().getString(R.string.forever);
        from_day=0;
        from_month=0;
        from_year=0;

        initialize();
        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Current date ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyy");
        String current_date=simpleDateFormat.format(new Date());
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/


        date_from=current_date.substring(0,2)+"/"+current_date.substring(2,4)+"/"+current_date.substring(4,8);
        date_to=getResources().getString(R.string.forever);

        et_start_date.setInputType(InputType.TYPE_NULL);
        et_till_date.setInputType(InputType.TYPE_NULL);

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


        et_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_start_date.getText().length() > 0){
                    FragmentManager fragmentManager=getFragmentManager();
                    StartDateDialogFragment dateDialogFragment=new StartDateDialogFragment();
                    dateDialogFragment.show(fragmentManager,null);

                }else {
                    FragmentManager fragmentManager=getFragmentManager();
                    StartDateDialogFragment dateDialogFragment=new StartDateDialogFragment();
                    dateDialogFragment.show(fragmentManager,null);
                }

            }
        });

        et_till_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_start_date.getText().length() > 0){
                    FragmentManager fragmentManager=getFragmentManager();
                    TillDateDialogFragment dateDialogFragment=new TillDateDialogFragment();
                    dateDialogFragment.show(fragmentManager,null);
                }else{
                    Toast.makeText(AddNewSlotActivity.this,getResources().getString(R.string.from_date_first),Toast.LENGTH_SHORT).show();
                }

            }
        });

        cb_mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_mon.isChecked()) {

                } else {

                }
            }
        });

        cb_tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_tue.isChecked()) {

                } else {

                }
            }
        });

        cb_wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_wed.isChecked()) {

                } else {

                }
            }
        });

        cb_thur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_thur.isChecked()) {

                } else {

                }
            }
        });

        cb_fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_fri.isChecked()) {

                } else {

                }
            }
        });

        cb_sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_sat.isChecked()) {

                } else {

                }
            }
        });

        cb_sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_sun.isChecked()) {

                } else {

                }
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
        et_start_date= (EditText) findViewById(R.id.et_slot_start_date);
        et_till_date= (EditText) findViewById(R.id.et_slot_till_date);

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


    @Override
    public void setSelectedStartDate(Object o1,Object o2, Object o3) {
        Log.d(TAG, o1.toString()+"/"+o2.toString()+"/"+o3.toString());
        int day=Integer.parseInt(o1.toString());
        AddNewSlotActivity.from_day=day;
        int month=Integer.parseInt(o2.toString());
        AddNewSlotActivity.from_month=month;
        int year=Integer.parseInt(o3.toString());
        AddNewSlotActivity.from_year=year;
        Log.d(TAG,"From date:"+from_day+"/"+from_month+"/"+from_year);
        StringBuilder stringBuilder=new StringBuilder();
        if((day/10) > 0){
            stringBuilder.append(day);
        }else{
            stringBuilder.append(""+0+day);
        }
        if((month/10) > 0){
            stringBuilder.append("/"+month);
        }else{
            stringBuilder.append("/"+0+month);
        }
        stringBuilder.append("/"+year);
        Log.d(TAG, "start date:" + stringBuilder.toString());
        AddNewSlotActivity.et_start_date.setText(stringBuilder.toString());
        AddNewSlotActivity.date_from=String.valueOf(stringBuilder);

        AddNewSlotActivity.et_till_date.setText(date_to);

       // et_start_date.setText(stringBuilder.toString());
    }

    @Override
    public void setSelectedTillDate(Object o1,Object o2, Object o3,boolean b) {
        Log.d(TAG, o1.toString()+"/"+o2.toString()+"/"+o3.toString());
        if (b){
            AddNewSlotActivity.et_till_date.setText(FOREVER);

        }else{
            int day=Integer.parseInt(o1.toString());
            int month=Integer.parseInt(o2.toString());
            int year=Integer.parseInt(o3.toString());
            StringBuilder stringBuilder=new StringBuilder();
            if((day/10) > 0){
                stringBuilder.append(day);
            }else{
                stringBuilder.append(""+0+day);
            }
            if((month/10) > 0){
                stringBuilder.append("/"+month);
            }else{
                stringBuilder.append("/"+0+month);
            }
            stringBuilder.append("/"+year);
            Log.d(TAG,"till date:"+stringBuilder.toString());
            AddNewSlotActivity.et_till_date.setText(stringBuilder.toString());
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
        int [] from_date={from_day,from_month-1,from_year};
        return from_date;

    }

    @Override
    public void setTillUpperLimit(Object o1, Object o2, Object o3) {

    }


}
