package com.findmycoach.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.ListOfClassDuration;
import com.findmycoach.app.adapter.MenteeList;
import com.findmycoach.app.beans.CalendarSchedule.EventDuration;
import com.findmycoach.app.beans.CalendarSchedule.Mentee;
import com.findmycoach.app.beans.CalendarSchedule.MentorInfo;
import com.findmycoach.app.beans.CalendarSchedule.Slot;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;
import com.findmycoach.app.util.StorageHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

/**
 * Created by ved on 12/3/15.
 */
public class AboutWeekViewEvent extends Activity {

    TextView tv_slot_start_date, tv_slot_stop_date, tv_slot_start_time,
            tv_slot_stop_time, tv_slot_week_days, tv_slot_type, tv_max_students,
            tv_vacation_start_date, tv_vacation_stop_date, tv_vacation_start_time,
            tv_vacation_stop_time, tv_name, tv_subject,tv_slot_subject_val;
    ListView lv_list_of_mentees, lv_list_of_coinciding_vacations, lv_list_class_durations;
    Button b_delete;
    LinearLayout ll_class_slot_details, ll_list_of_mentees, ll_list_of_coincidingVacations, ll_non_coincidingLinearLayout,
            ll_mentee_class_schedule, ll_coinciding_vacations;


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
                setContentView(R.layout.activity_about_event_mentor);
                bundle = getIntent().getBundleExtra("scheduled_class_mentor_bundle");
                menteeFoundOnTheDate = bundle.getParcelableArrayList("mentees");
                slot = bundle.getParcelable("slot");

                init1();
                populateSlotInfo(slot);


                ll_list_of_coincidingVacations.setVisibility(View.GONE);
                ll_non_coincidingLinearLayout.setVisibility(View.GONE);
                b_delete.setVisibility(View.GONE);


                MenteeList menteeList = new MenteeList(AboutWeekViewEvent.this, menteeFoundOnTheDate);
                lv_list_of_mentees.setAdapter(menteeList);

                break;
            case "coinciding_vacation_mentor":
                setContentView(R.layout.activity_about_event_mentor);
                bundle = getIntent().getBundleExtra("coinciding_vacation_bundle");
                coinciding_vacations = bundle.getParcelableArrayList("coinciding_vacations");
                slot = bundle.getParcelable("slot");

                init1();
                populateSlotInfo(slot);

                ll_list_of_mentees.setVisibility(View.GONE);
                ll_non_coincidingLinearLayout.setVisibility(View.GONE);
                b_delete.setVisibility(View.GONE);


                MenteeList menteeList1 = new MenteeList(coinciding_vacations, AboutWeekViewEvent.this);
                lv_list_of_coinciding_vacations.setAdapter(menteeList1);

                break;
            case "slot_not_scheduled":
                setContentView(R.layout.activity_about_event_mentor);
                bundle = getIntent().getBundleExtra("unscheduled_slot_bundle");
                slot = bundle.getParcelable("slot");
                init1();
                populateSlotInfo(slot);
                
                ll_list_of_mentees.setVisibility(View.GONE);
                ll_non_coincidingLinearLayout.setVisibility(View.GONE);
                ll_list_of_coincidingVacations.setVisibility(View.GONE);
                b_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                    }
                });                
                break;
            case "non_coinciding_vacation":
                setContentView(R.layout.activity_about_event_mentor);
                bundle = getIntent().getBundleExtra("vacation_bundle");
                non_coinciding_vacation = bundle.getParcelable("vacation");
                
                init1();
                
                ll_class_slot_details.setVisibility(View.GONE);
                ll_list_of_coincidingVacations.setVisibility(View.GONE);
                ll_list_of_mentees.setVisibility(View.GONE);

                tv_vacation_start_date.setText(String.format("%02d-%02d-%d", Integer.parseInt(non_coinciding_vacation.getStart_date().split("-")[2]), Integer.parseInt(non_coinciding_vacation.getStop_date().split("-")[1]), Integer.parseInt(non_coinciding_vacation.getStop_date().split("-")[0])));
                tv_vacation_stop_date.setText(String.format("%02d-%02d-%d", Integer.parseInt(non_coinciding_vacation.getStop_date().split("-")[2]), Integer.parseInt(non_coinciding_vacation.getStop_date().split("-")[1]), Integer.parseInt(non_coinciding_vacation.getStop_date().split("-")[0])));

                tv_vacation_start_time.setText(getTime(non_coinciding_vacation.getStart_time()));
                tv_vacation_stop_time.setText(getTime(non_coinciding_vacation.getStop_time()));

                String slot_type = slot.getSlot_type();
                if (slot_type.equalsIgnoreCase("group")) {
                    tv_slot_type.setText(getResources().getString(R.string.group));
                } else {
                    if (slot_type.equalsIgnoreCase("individual")) {
                        tv_slot_type.setText(getResources().getString(R.string.individual));
                    }
                }

                tv_max_students.setText(slot.getSlot_max_users());
                
                
                
                
                b_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                    }
                });
               
                
                break;
            case "scheduled_class_mentee":
                setContentView(R.layout.activity_about_event_mentee);
                bundle = getIntent().getBundleExtra("scheduled_class_mentee_bundle");
                menteeFoundOnTheDate = bundle.getParcelableArrayList("mentees");
                slot = bundle.getParcelable("slot");
                mentorInfo = bundle.getParcelable("mentor_info");
                
                init2();


                tv_name.setText(mentorInfo.getFirst_name()+"\t"+mentorInfo.getLast_name().trim());
                populateSlotInfo(slot);
                //tv_subject.setText(""); /* not required as mentee class is similar to slot subject*/
                // /*  Not setting because this value is not available correctly*/

                ListOfClassDuration listOfClassDuration=new ListOfClassDuration(AboutWeekViewEvent.this,menteeFoundOnTheDate);
                lv_list_class_durations.setAdapter(listOfClassDuration);
                ll_coinciding_vacations.setVisibility(View.GONE);


                
                break;
            case "coinciding_vacation_mentee":
                setContentView(R.layout.activity_about_event_mentee);
                bundle = getIntent().getBundleExtra("coinciding_vacation_mentee_bundle");
                coinciding_vacations = bundle.getParcelableArrayList("coinciding_vacations");
                slot = bundle.getParcelable("slot");
                mentorInfo = bundle.getParcelable("mentor_info");

                init2();
                tv_name.setText(mentorInfo.getFirst_name() + "\t" + mentorInfo.getLast_name().trim());
                populateSlotInfo(slot);

                ll_mentee_class_schedule.setVisibility(View.GONE);

                ListOfClassDuration listOfClassDuration1 = new ListOfClassDuration(coinciding_vacations,AboutWeekViewEvent.this);
                lv_list_of_coinciding_vacations.setAdapter(listOfClassDuration1);

                break;
        }
    }

    private void init2() {
        tv_name = (TextView) findViewById(R.id.tv_name_val);
        tv_slot_start_date = (TextView) findViewById(R.id.tv_start_date_val);
        tv_slot_stop_date = (TextView) findViewById(R.id.tv_end_date_val);
        tv_slot_start_time = (TextView) findViewById(R.id.tv_start_time_val);
        tv_slot_stop_time = (TextView) findViewById(R.id.tv_end_time_val);
        tv_slot_subject_val = (TextView) findViewById(R.id.tv_slot_subject_val);
        tv_slot_week_days = (TextView) findViewById(R.id.tv_week_days_val);
        tv_slot_type = (TextView) findViewById(R.id.tv_slot_type_val);
        tv_max_students = (TextView) findViewById(R.id.tv_max_users_val);
        tv_subject = (TextView) findViewById(R.id.tv_subject_val);
        lv_list_class_durations = (ListView) findViewById(R.id.lv_class_durations);
        lv_list_of_coinciding_vacations = (ListView) findViewById(R.id.lv_coinciding_vacations);

        ll_mentee_class_schedule = (LinearLayout) findViewById(R.id.ll_mentee_class_schedule);
        ll_coinciding_vacations = (LinearLayout) findViewById(R.id.ll_coinciding_vacations);


    }

    private void populateSlotInfo(Slot slot) {
        tv_slot_start_date.setText(String.format("%02d-%02d-%d", Integer.parseInt(slot.getSlot_start_date().split("-")[2]), Integer.parseInt(slot.getSlot_start_date().split("-")[1]), Integer.parseInt(slot.getSlot_start_date().split("-")[0])));
        tv_slot_stop_date.setText(String.format("%02d-%02d-%d", Integer.parseInt(slot.getSlot_stop_date().split("-")[2]), Integer.parseInt(slot.getSlot_stop_date().split("-")[1]), Integer.parseInt(slot.getSlot_stop_date().split("-")[0])));

        tv_slot_start_time.setText(getTime(slot.getSlot_start_time()));
        tv_slot_stop_time.setText(getTime(slot.getSlot_stop_time()));

        tv_slot_subject_val.setText(slot.getSlot_subject());
        tv_slot_week_days.setText(getWeekDays(slot.getSlot_week_days()));



        String slot_type = slot.getSlot_type();
        if (slot_type.equalsIgnoreCase("group")) {
            tv_slot_type.setText(getResources().getString(R.string.group));
        } else {
            if (slot_type.equalsIgnoreCase("individual")) {
                tv_slot_type.setText(getResources().getString(R.string.individual));
            }
        }

        tv_max_students.setText(slot.getSlot_max_users());

    }

    private void init1() {
        tv_slot_start_date = (TextView) findViewById(R.id.tv_start_date_val);
        tv_slot_stop_date = (TextView) findViewById(R.id.tv_end_date_val);
        tv_slot_start_time = (TextView) findViewById(R.id.tv_start_time_val);
        tv_slot_stop_time = (TextView) findViewById(R.id.tv_end_time_val);
        tv_slot_subject_val = (TextView) findViewById(R.id.tv_slot_subject_val);
        tv_slot_week_days = (TextView) findViewById(R.id.tv_week_days_val);
        tv_slot_type = (TextView) findViewById(R.id.tv_slot_type_val);
        tv_max_students = (TextView) findViewById(R.id.tv_max_users_val);
        lv_list_of_mentees = (ListView) findViewById(R.id.lv_list_of_mentees);
        lv_list_of_coinciding_vacations = (ListView) findViewById(R.id.lv_list_of_coinciding_vacations);
        tv_vacation_start_date = (TextView) findViewById(R.id.tv_vacation_start_date_val);
        tv_vacation_stop_date = (TextView) findViewById(R.id.tv_vacation_end_date_val);
        tv_vacation_start_time = (TextView) findViewById(R.id.tv_vacation_start_time_val);
        tv_vacation_stop_time = (TextView) findViewById(R.id.tv_vacation_end_time_val);

        ll_class_slot_details = (LinearLayout) findViewById(R.id.ll_class_slot_details);
        ll_list_of_mentees = (LinearLayout) findViewById(R.id.ll_list_of_mentees);
        ll_list_of_coincidingVacations = (LinearLayout) findViewById(R.id.ll_list_of_coincidingVacations);
        ll_non_coincidingLinearLayout = (LinearLayout) findViewById(R.id.ll_non_coinciding_vacation);

    }

    private String getWeekDays(String[] slot_week_days) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int day_no = 0; day_no < slot_week_days.length; day_no++) {
            String d = slot_week_days[day_no];
            if (day_no == (slot_week_days.length - 1)) {
                switch (d) {
                    case "M":
                        stringBuilder.append("Monday");
                        break;
                    case "T":
                        stringBuilder.append("Tuesday");
                        break;
                    case "W":
                        stringBuilder.append("Wednesday");
                        break;
                    case "Th":
                        stringBuilder.append("Thursday");
                        break;
                    case "F":
                        stringBuilder.append("Friday");
                        break;
                    case "S":
                        stringBuilder.append("Saturday");
                        break;
                    case "Su":
                        stringBuilder.append("Sunday");
                        break;
                }
            } else {
                switch (d) {
                    case "M":
                        stringBuilder.append("Monday, ");
                        break;
                    case "T":
                        stringBuilder.append("Tuesday, ");
                        break;
                    case "W":
                        stringBuilder.append("Wednesday, ");
                        break;
                    case "Th":
                        stringBuilder.append("Thursday, ");
                        break;
                    case "F":
                        stringBuilder.append("Friday, ");
                        break;
                    case "S":
                        stringBuilder.append("Saturday, ");
                        break;
                    case "Su":
                        stringBuilder.append("Sunday, ");
                        break;
                }
            }
        }
        return stringBuilder.toString();
    }

    private String getTime(String hr_24_format_time) {
        String time = null;
        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(hr_24_format_time);
            time = _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;

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
