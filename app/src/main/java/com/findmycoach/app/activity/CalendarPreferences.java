package com.findmycoach.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment.MyScheduleFragment;

/**
 * Created by ved on 14/6/15.
 */
public class CalendarPreferences extends Activity {
Spinner sp_class_type;
    AutoCompleteTextView auto_tv_location;
    Button b_save;
    public String TAG="FMC";
    private String location=null;
    private String last_location_selected=null;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mentor_calendar_preferences);
        initialize();
    }

    private void initialize() {
        sp_class_type = (Spinner) findViewById(R.id.sp_class_type);
        auto_tv_location = (AutoCompleteTextView) findViewById(R.id.auto_tv_location);
        b_save = (Button) findViewById(R.id.b_save);


        auto_tv_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                location = arrayAdapter.getItem(position).toString();
                last_location_selected=location;

            }
        });


        listeners();

    }

    private void listeners() {


    }
}
