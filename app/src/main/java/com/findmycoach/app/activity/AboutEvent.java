package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.util.StorageHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ved on 12/3/15.
 */
public class AboutEvent extends Activity {
    TextView tv_mentor_name, tv_mentee_name, tv_subject_val, tv_address_type, tv_address_val, tv_start_date_val, tv_end_date_val, tv_time_val;
    ListView lv_week_days;
    private static final String TAG ="FMC";
    private static String address,start_time,end_time,start_date,end_date;
    JSONObject jsonObject_aboutEvent;
    JSONArray jsonArrayData;
    JSONObject event_details;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_event);
        initialize();
        applyActionbarProperties();
        if(Integer.parseInt(StorageHelper.getUserGroup(AboutEvent.this,"user_group"))== 3){
           tv_address_type.setText("Mentee's address:");
        }
        if(Integer.parseInt(StorageHelper.getUserGroup(AboutEvent.this,"user_group"))== 2){
            tv_address_type.setText("Mentor's address:");
        }
        String about_event=getIntent().getStringExtra("about_event");
        Log.d(TAG,"Event details: "+about_event);
        try {
            jsonObject_aboutEvent=new JSONObject(about_event);
            jsonArrayData=jsonObject_aboutEvent.getJSONArray("data");
            event_details=jsonArrayData.getJSONObject(0);
            address=event_details.getString("location");
            start_date=event_details.getString("start_date");
            end_date=event_details.getString("stop_date");
            start_time=event_details.getString("start_time");
            end_time=event_details.getString("stop_time");
            tv_start_date_val.setText(start_date);
            if(end_date != null && end_date.length() > 0){
                tv_end_date_val.setText(end_date);
            }
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(start_time.split(":",3)[0]+":"+start_time.split(":",3)[1]+" to "+end_date.split(":",3)[0]+":"+end_time.split(":",3)[1]);
            tv_time_val.setText(stringBuilder);



            if(address != null && address.length() > 0){
                tv_address_val.setText(address);
            }

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
