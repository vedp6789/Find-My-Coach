package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.util.StorageHelper;
import com.roomorama.caldroid.WeekdayArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ved on 12/3/15.
 */
public class AboutEvent extends Activity {
    TextView tv_mentor_name,tv_mentor_name_val,  tv_mentee_name,tv_mentee_name_val, tv_subject_val, tv_address_type, tv_address_val, tv_start_date_val, tv_end_date_val, tv_time_val;
    ListView lv_week_days;
    private static final String TAG = "FMC";
    private static String address, start_time, end_time, start_date, end_date, mentor_fname, mentor_lname, mentee_fname, mentee_lname, subject;
    JSONObject jsonObject_aboutEvent;
    JSONArray jsonArrayData;
    JSONObject event_details;
    JSONArray jsonArray_days;
    ArrayAdapter arrayAdapter;
    private ArrayList<String> days;
    JSONArray student_names;
    String mentor_first_name,mentor_last_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_event);

        initialize();     // views get initialized in this method

        applyActionbarProperties();

        populateView();


    }

    private void populateView() {

        if (Integer.parseInt(StorageHelper.getUserGroup(AboutEvent.this, "user_group")) == 3) {

            //tv_address_type.setText(getResources().getString(R.string.mentee_add));      // address is not coming from server

        }
        if (Integer.parseInt(StorageHelper.getUserGroup(AboutEvent.this, "user_group")) == 2) {
            //tv_address_type.setText(getResources().getString(R.string.mentor_add));       // address is not coming from server

        }

        String about_event = getIntent().getStringExtra("about_event");       // Json string coming from SetScheduleActivity as a intent

        Log.d(TAG, "Event details: " + about_event);

        try {
            jsonObject_aboutEvent = new JSONObject(about_event);
            jsonArrayData = jsonObject_aboutEvent.getJSONArray("data");
            event_details = jsonArrayData.getJSONObject(0);
            String [] week_Days=event_details.getString("dates").split(",");
            for( int i=0; i < week_Days.length;i++){
                Log.d(TAG,"week_Day : "+week_Days[i]);
            }

            days = new ArrayList<String>();

            /* Creating Arraylist for week-days detail for this event */
            for (int i = 0; i < week_Days.length; i++) {
                String day = week_Days[i];
                if (day.equals("M")) {
                    days.add("Mon");
                }
                if (day.equals("T")) {
                    days.add("Tue");
                }
                if (day.equals("W")) {
                    days.add("Wed");
                }
                if (day.equals("Th")) {
                    days.add("Thu");
                }
                if (day.equals("F")) {
                    days.add("Fri");
                }
                if (day.equals("S")) {
                    days.add("Sat");
                }
                if (day.equals("su")) {
                    days.add("Sun");
                }


            }

            Log.d(TAG, "days array size" + " :" + days.size());

    //        address = event_details.getString("location");                  commented as not getting from server
            start_date = event_details.getString("start_date");
            end_date = event_details.getString("stop_date");
            start_time = event_details.getString("start_time");
            end_time = event_details.getString("stop_time");
            student_names=event_details.getJSONArray("student_names");
            mentor_first_name=event_details.getString("first_name");
            mentor_last_name=event_details.getString("last_name");


            StringBuilder stringBuilder=  new StringBuilder();
            for (int student=0;student < student_names.length();student++){
                JSONObject jsonObject=student_names.getJSONObject(student);
                Log.d(TAG,"json_object_of_Student_firstAm_last : "+jsonObject.toString());
                String f_name=jsonObject.getString("first_name");
                String l_name =jsonObject.getString("last_name");
                if(student == (student_names.length()-1)){
                    if(f_name != null){
                        stringBuilder.append(f_name);
                        if(l_name != null){
                            stringBuilder.append(" "+l_name);
                        }

                    }
                }else{
                    if(f_name != null){
                        stringBuilder.append(f_name);
                        if(l_name != null){
                            stringBuilder.append(" "+l_name+", ");
                        }

                    }
                }


            }

            tv_mentee_name_val.setText(stringBuilder);







                mentor_first_name = event_details.getString("first_name");
                mentor_last_name = event_details.getString("last_name");


           /* if(Integer.parseInt(StorageHelper.getUserGroup(AboutEvent.this, "user_group")) == 2){
                mentee_fname = event_details.getString("student_first");
                mentee_lname = event_details.getString("student_last");
            }
*/

            subject = event_details.getString("sub_category_name");

            tv_start_date_val.setText(start_date.split("-")[2]+"-"+start_date.split("-")[1]+"-"+start_date.split("-")[0]);

            /* Checking whether user specified end date for this schedule or not */
            if (end_date != null && end_date.length() > 0) {
                tv_end_date_val.setText(end_date.split("-")[2]+"-"+end_date.split("-")[1]+"-"+end_date.split("-")[0]);
            }

            /*Timing of event from start time and stop time */
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(start_time.split(":", 3)[0] + ":" + start_time.split(":", 3)[1] + " to " + end_time.split(":", 3)[0] + ":" + end_time.split(":", 3)[1]);
            tv_time_val.setText(stringBuilder1);
   /*bacause not getting this

            if (address != null && address.length() > 0) {
                tv_address_val.setText(address);
            }
*/

             /* Building a single string for mentor name from fname and lname*/
            StringBuilder stringBuilder2 = new StringBuilder();
            if (mentor_first_name.equals("0")) {
                tv_mentor_name_val.setText("");
            } else {
                stringBuilder2.append(mentor_first_name);
                if (!mentor_first_name.equals("0")) {
                    stringBuilder2.append(" " + mentor_last_name);
                }
                tv_mentor_name_val.setText(stringBuilder2);
            }




            /* Building a single string for mentee name from fname and lname*//*
            StringBuilder stringBuilder3 = new StringBuilder();
            if (mentee_fname.equals("0")) {
                Log.d(TAG, "mentee fname found 0");
                tv_mentee_name.setText(" ");
            } else {
                stringBuilder3.append(mentee_fname);
                if (!mentee_lname.equals("0")) {
                    stringBuilder3.append(" " + mentee_lname);
                }
                tv_mentee_name.setText(stringBuilder3);
            }*/

            if (!subject.equals("")) {
                tv_subject_val.setText(subject);
            } else {
                tv_subject_val.setText("");
            }

            arrayAdapter = new ArrayAdapter(AboutEvent.this, android.R.layout.simple_list_item_1, days.toArray());
            lv_week_days.setAdapter(arrayAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        tv_mentor_name = (TextView) findViewById(R.id.tv_mentor_name);
        tv_mentor_name_val= (TextView) findViewById(R.id.tv_mentor_name_val);
        tv_mentee_name = (TextView) findViewById(R.id.tv_mentee_name);
        tv_mentee_name_val= (TextView) findViewById(R.id.tv_mentee_name_val);
        tv_subject_val = (TextView) findViewById(R.id.tv_subject_Val);
        tv_address_type = (TextView) findViewById(R.id.tv_address);
        tv_address_val = (TextView) findViewById(R.id.tv_address_val);
        tv_start_date_val = (TextView) findViewById(R.id.tv_event_start_date_val);
        tv_end_date_val = (TextView) findViewById(R.id.tv_event_end_date_val);
        tv_time_val = (TextView) findViewById(R.id.tv_event_time_val);
        lv_week_days = (ListView) findViewById(R.id.lv_week_days);
    }

    private void applyActionbarProperties() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.event_desc));
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
}
