package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
    TextView tv_mentor_name, tv_mentee_name, tv_subject_val, tv_address_type, tv_address_val, tv_start_date_val, tv_end_date_val, tv_time_val;
    ListView lv_week_days;
    private static final String TAG ="FMC";
    private static String address,start_time,end_time,start_date,end_date,mentor_fname,mentor_lname,mentee_fname,mentee_lname,subject;
    JSONObject jsonObject_aboutEvent;
    JSONArray jsonArrayData;
    JSONObject event_details;
    JSONArray jsonArray_days;
    ArrayAdapter arrayAdapter;
    private ArrayList<String> days;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_event);
        initialize();
        applyActionbarProperties();
        if(Integer.parseInt(StorageHelper.getUserGroup(AboutEvent.this,"user_group"))== 3){
           tv_address_type.setText(getResources().getString(R.string.mentee_add));
        }
        if(Integer.parseInt(StorageHelper.getUserGroup(AboutEvent.this,"user_group"))== 2){
            tv_address_type.setText(getResources().getString(R.string.mentor_add));
        }
        String about_event=getIntent().getStringExtra("about_event");
        Log.d(TAG,"Event details: "+about_event);
        try {
            jsonObject_aboutEvent=new JSONObject(about_event);
            jsonArrayData=jsonObject_aboutEvent.getJSONArray("data");
            event_details=jsonArrayData.getJSONObject(0);
            jsonArray_days=event_details.getJSONArray("dates");
            Log.d(TAG,"Days json array size from AboutEvent : "+jsonArray_days.length());
            days=new ArrayList<String>();
            for(int i=0;i < jsonArray_days.length();i++){
                String day=jsonArray_days.getString(i);
                if(day.equals("M")){
                    days.add("Mon");
                }
                if(day.equals("T")){
                    days.add("Tue");
                }
                if(day.equals("W")){
                    days.add("Wed");
                }
                if(day.equals("Th")){
                    days.add("Thu");
                }
                if(day.equals("F")){
                    days.add("Fri");
                }
                if(day.equals("S")){
                    days.add("Sat");
                }
                if(day.equals("su")){
                    days.add("Sun");
                }
                Log.d(TAG,"json array for Days value "+i+" :"+jsonArray_days.getString(i));

            }

            Log.d(TAG,"days array size"+" :"+days.size());

            address=event_details.getString("location");
            start_date=event_details.getString("start_date");
            end_date=event_details.getString("stop_date");
            start_time=event_details.getString("start_time");
            end_time=event_details.getString("stop_time");
            mentor_fname=event_details.getString("first_name");
            mentor_lname=event_details.getString("last_name");
            mentee_fname=event_details.getString("student_first");
            mentee_lname=event_details.getString("student_last");
            subject=event_details.getString("sub_category_name");
            tv_start_date_val.setText(start_date);
            if(end_date != null && end_date.length() > 0){
                tv_end_date_val.setText(end_date);
            }
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(start_time.split(":",3)[0]+":"+start_time.split(":",3)[1]+" to "+end_time.split(":",3)[0]+":"+end_time.split(":",3)[1]);
            tv_time_val.setText(stringBuilder);

            if(address != null && address.length() > 0){
                tv_address_val.setText(address);
            }

            StringBuilder stringBuilder1=new StringBuilder();


            if(mentor_fname.equals("0")){
                tv_mentor_name.setText("");
            }
            else{
                stringBuilder1.append(mentor_fname);
                if(!mentor_lname.equals("0")){
                    stringBuilder1.append(" "+mentor_lname);
                }
                tv_mentor_name.setText(stringBuilder1);
            }

            StringBuilder stringBuilder2=new StringBuilder();


            if(mentee_fname.equals("0")){
                Log.d(TAG,"mentee fname found 0");
                tv_mentee_name.setText(" ");
            }
            else{
                stringBuilder2.append(mentee_fname);
                if(!mentee_lname.equals("0")){
                    stringBuilder2.append(" "+mentee_lname);
                }
                tv_mentee_name.setText(stringBuilder2);
            }


            if(!subject.equals("")){
                tv_subject_val.setText(subject);
            }else{
                tv_subject_val.setText("");
            }


            arrayAdapter=new ArrayAdapter(AboutEvent.this,android.R.layout.simple_list_item_1,days.toArray());
            lv_week_days.setAdapter(arrayAdapter);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        tv_mentor_name = (TextView) findViewById(R.id.tv_mentor_name_val);
        tv_mentee_name = (TextView) findViewById(R.id.tv_mentee_name_val);
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
        if(actionBar != null){
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
