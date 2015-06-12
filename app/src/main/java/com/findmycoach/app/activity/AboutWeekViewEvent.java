package com.findmycoach.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.CalendarSchedule.Mentee;
import com.findmycoach.app.beans.CalendarSchedule.MentorInfo;
import com.findmycoach.app.beans.CalendarSchedule.Slot;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;
import com.findmycoach.app.util.StorageHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ved on 12/3/15.
 */
public class AboutWeekViewEvent extends Activity {

    TextView tv_slot_start_date,tv_slot_stop_date, tv_slot_start_time,
            tv_slot_stop_time,tv_slot_week_days,tv_slot_type,tv_max_students,
            tv_vacation_start_date,tv_vacation_stop_date,tv_vacation_start_time,
            tv_vacation_stop_time,tv_name,tv_subject;
    ListView lv_list_of_mentees,lv_coinciding_vacations,lv_class_durations;
    Button b_delete;
    LinearLayout ll_class_slot_details,ll_list_of_mentees,ll_list_of_coincidingVacations,ll_non_coincidingLinearLayout,
            ll_mentor_class_details,ll_mentee_class_schedule,ll_coinciding_vacations;



    ArrayList<Mentee> menteeFoundOnTheDate;
    Slot slot;
    ArrayList<Vacation> coinciding_vacations;
    Vacation non_coinciding_vacation;
    MentorInfo mentorInfo;
    Bundle bundle;
    private String for_which_event = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for_which_event = getIntent().getStringExtra("for");

        switch (for_which_event) {
            case "scheduled_class_mentor":
                setContentView(R.layout.act);
                bundle = getIntent().getBundleExtra("scheduled_class_mentor_bundle");
                menteeFoundOnTheDate = bundle.getParcelableArrayList("mentees");
                slot = bundle.getParcelable("slot");

                break;
            case "coinciding_vacation_mentor":
                bundle = getIntent().getBundleExtra("coinciding_vacation_bundle");
                coinciding_vacations = bundle.getParcelableArrayList("coinciding_vacations");
                slot = bundle.getParcelable("slot");
                break;
            case "slot_not_scheduled":
                bundle = getIntent().getBundleExtra("unscheduled_slot_bundle");
                slot = bundle.getParcelable("slot");
                break;
            case "non_coinciding_vacation":
                bundle = getIntent().getBundleExtra("vacation_bundle");
                non_coinciding_vacation = bundle.getParcelable("vacation");
                break;
            case "scheduled_class_mentee":
                bundle = getIntent().getBundleExtra("scheduled_class_mentee_bundle");
                menteeFoundOnTheDate = bundle.getParcelableArrayList("mentees");
                slot = bundle.getParcelable("slot");
                mentorInfo = bundle.getParcelable("mentor_info");
                break;
            case "coinciding_vacation_mentee":
                bundle = getIntent().getBundleExtra("coinciding_vacation_mentee_bundle");
                coinciding_vacations = bundle.getParcelableArrayList("coinciding_vacations");
                slot = bundle.getParcelable("slot");
                mentorInfo = bundle.getParcelable("mentor_info");
                break;
        }
    }


    private void applyActionbarProperties() {

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
