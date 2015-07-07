package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.ListOfClassDuration;
import com.findmycoach.app.adapter.MenteeList;
import com.findmycoach.app.beans.CalendarSchedule.Mentee;
import com.findmycoach.app.beans.CalendarSchedule.MentorInfo;
import com.findmycoach.app.beans.CalendarSchedule.Slot;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.ListViewInsideScrollViewHelper;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ved on 12/3/15.
 */
public class AboutWeekViewEvent extends Activity implements Callback {

    TextView tv_slot_start_date, tv_slot_stop_date, tv_slot_start_time,
            tv_slot_stop_time, tv_slot_week_days, tv_slot_type, tv_max_students,
            tv_vacation_start_date, tv_vacation_stop_date, tv_vacation_start_time,
            tv_vacation_stop_time, tv_name, tv_subject, tv_slot_subject_val,
            tv_class_location;
    ListView lv_list_of_mentees, lv_list_of_coinciding_vacations, lv_list_class_durations;
    Button b_delete;
    LinearLayout ll_list_of_mentees, ll_list_of_coincidingVacations,
            ll_non_coincidingLinearLayout,
            ll_mentee_class_schedule, ll_coinciding_vacations,ll_slot_details;

    private ScrollView ll_class_slot_details;

    ArrayList<Mentee> menteeFoundOnTheDate;
    Slot slot;
    ArrayList<Vacation> coinciding_vacations;
    Vacation non_coinciding_vacation;
    MentorInfo mentorInfo;
    int mentee_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for_which_event = getIntent().getStringExtra("for");
        progressDialog = new ProgressDialog(AboutWeekViewEvent.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        switch (for_which_event) {
            case "scheduled_class_mentor":
                setContentView(R.layout.activity_about_event_mentor);
                bundle = getIntent().getBundleExtra("scheduled_class_mentor_bundle");
                menteeFoundOnTheDate = bundle.getParcelableArrayList("mentees");
                slot = bundle.getParcelable("slot");
                mentee_found = menteeFoundOnTheDate.size();
                init1();
                populateSlotInfo(slot);

                ll_list_of_coincidingVacations.setVisibility(View.GONE);
                ll_non_coincidingLinearLayout.setVisibility(View.GONE);
                b_delete.setVisibility(View.GONE);


                MenteeList menteeList = new MenteeList(AboutWeekViewEvent.this, menteeFoundOnTheDate);
                lv_list_of_mentees.setAdapter(menteeList);
                ListViewInsideScrollViewHelper.getListViewSize(lv_list_of_mentees);


                break;
            case "coinciding_vacation_mentor":
                setContentView(R.layout.activity_about_event_mentor);
                bundle = getIntent().getBundleExtra("coinciding_vacation_bundle");
                coinciding_vacations = bundle.getParcelableArrayList("coinciding_vacations");
                slot = bundle.getParcelable("slot");
                mentee_found = -5;// in case of coinciding vacation for slot day
                init1();
                populateSlotInfo(slot);

                ll_list_of_mentees.setVisibility(View.GONE);
                ll_non_coincidingLinearLayout.setVisibility(View.GONE);
                b_delete.setVisibility(View.GONE);


                MenteeList menteeList1 = new MenteeList(coinciding_vacations,
                        AboutWeekViewEvent.this);
                lv_list_of_coinciding_vacations.setAdapter(menteeList1);
                ListViewInsideScrollViewHelper.getListViewSize(lv_list_of_coinciding_vacations);

                break;
            case "slot_not_scheduled":
                setContentView(R.layout.activity_about_event_mentor);
                bundle = getIntent().getBundleExtra("unscheduled_slot_bundle");
                slot = bundle.getParcelable("slot");
                init1();
                populateSlotInfo(slot);
                b_delete.setText(getResources().getString(R.string.delete));
                mentee_found = 0;
                ll_list_of_mentees.setVisibility(View.GONE);
                ll_non_coincidingLinearLayout.setVisibility(View.GONE);
                ll_list_of_coincidingVacations.setVisibility(View.GONE);
                b_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams requestParams = new RequestParams();
                        Log.d("FMC", slot.getSlot_id());
                        requestParams.add("slot_id", slot.getSlot_id());
                        showAlertOnDelete(0, requestParams);

                    }
                });
                break;
            case "non_coinciding_vacation":
                setContentView(R.layout.activity_about_event_mentor);
                bundle = getIntent().getBundleExtra("vacation_bundle");
                non_coinciding_vacation = bundle.getParcelable("vacation");

                init1();

                ll_class_slot_details.setVisibility(View.VISIBLE);
                ll_slot_details.setVisibility(View.GONE);
                ll_list_of_coincidingVacations.setVisibility(View.GONE);
                ll_list_of_mentees.setVisibility(View.GONE);

                tv_vacation_start_date.setText(String.format("%02d-%02d-%d",
                        Integer.parseInt(non_coinciding_vacation.getStart_date().split("-")[2]),
                        Integer.parseInt(non_coinciding_vacation.getStart_date().split("-")[1]),
                        Integer.parseInt(non_coinciding_vacation.getStart_date().split("-")[0])));
                tv_vacation_stop_date.setText(String.format("%02d-%02d-%d",
                        Integer.parseInt(non_coinciding_vacation.getStop_date().split("-")[2]),
                        Integer.parseInt(non_coinciding_vacation.getStop_date().split("-")[1]),
                        Integer.parseInt(non_coinciding_vacation.getStop_date().split("-")[0])));

                tv_vacation_start_time.setText(getTime(non_coinciding_vacation.getStart_time()));
                tv_vacation_stop_time.setText(getTime(non_coinciding_vacation.getStop_time()));
                b_delete.setText(getResources().getString(R.string.delete));

               /* String slot_type = slot.getSlot_type();
                if (slot_type.equalsIgnoreCase("group")) {
                    tv_slot_type.setText(getResources().getString(R.string.group));
                } else {
                    if (slot_type.equalsIgnoreCase("individual")) {
                        tv_slot_type.setText(getResources().getString(R.string.individual));
                    }
                }
                tv_max_students.setText(slot.getSlot_max_users());
*/


                b_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams requestParams = new RequestParams();
                        requestParams.add("vacation_id", non_coinciding_vacation.getVacation_id());
                        showAlertOnDelete(1, requestParams);

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
                ll_coinciding_vacations.setVisibility(View.GONE);
                tv_name.setText(mentorInfo.getFirst_name() + "\t" + mentorInfo.getLast_name().
                        trim());
                populateSlotInfo(slot);
                //tv_subject.setText(""); /* not required as mentee class is similar to slot subject*/
                // /*  Not setting because this value is not available correctly*/

                ListOfClassDuration listOfClassDuration =
                        new ListOfClassDuration(AboutWeekViewEvent.this, menteeFoundOnTheDate);
                lv_list_class_durations.setAdapter(listOfClassDuration);
                ListViewInsideScrollViewHelper.getListViewSize(lv_list_class_durations);
                break;
            case "coinciding_vacation_mentee":
                setContentView(R.layout.activity_about_event_mentee);
                bundle = getIntent().getBundleExtra("coinciding_vacation_mentee_bundle");
                coinciding_vacations = bundle.getParcelableArrayList("coinciding_vacations");
                slot = bundle.getParcelable("slot");
                mentorInfo = bundle.getParcelable("mentor_info");
                init2();
                ll_mentee_class_schedule.setVisibility(View.GONE);
                tv_name.setText(mentorInfo.getFirst_name() + "\t" +
                        mentorInfo.getLast_name().trim());
                populateSlotInfo(slot);
                ListOfClassDuration listOfClassDuration1 =
                        new ListOfClassDuration(coinciding_vacations, AboutWeekViewEvent.this);
                lv_list_of_coinciding_vacations.setAdapter(listOfClassDuration1);
                ListViewInsideScrollViewHelper.getListViewSize(lv_list_of_coinciding_vacations);
                break;
        }

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.class_info));
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    Bundle bundle;
    private String for_which_event = null;


    ProgressDialog progressDialog;

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

    /**
     * This is used to populate class details on view
     * */
    private void populateSlotInfo(Slot slot) {
        tv_slot_start_date.setText(String.format("%02d-%02d-%d",
                Integer.parseInt(slot.getSlot_start_date().split("-")[2]),
                Integer.parseInt(slot.getSlot_start_date().split("-")[1]),
                Integer.parseInt(slot.getSlot_start_date().split("-")[0])));
        tv_slot_stop_date.setText(String.format("%02d-%02d-%d",
                Integer.parseInt(slot.getSlot_stop_date().split("-")[2]),
                Integer.parseInt(slot.getSlot_stop_date().split("-")[1]),
                Integer.parseInt(slot.getSlot_stop_date().split("-")[0])));

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

        if (StorageHelper.getUserGroup(AboutWeekViewEvent.this, "user_group").equals("2")) {
            tv_max_students.setText(slot.getSlot_max_users());
        } else {
            int max_students_poss = Integer.parseInt(slot.getSlot_max_users());
            if (mentee_found == -5) {
                tv_max_students.setText(getResources().getString(R.string.class_not_possible_as_vacation_found));
            } else {
                if (mentee_found == 0) {
                    tv_max_students.setText("" + max_students_poss);
                } else {
                    if (mentee_found > 0) {
                        int x = max_students_poss - mentee_found;
                        tv_max_students.setText(x + " " + getResources().getString(R.string.out_of) + " " + max_students_poss);
                    }
                }
            }

        }

        tv_class_location.setText(slot.getTutorial_location().toString().trim());
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
        tv_class_location = (TextView) findViewById(R.id.tv_class_location_val);
        lv_list_of_mentees = (ListView) findViewById(R.id.lv_list_of_mentees);
        lv_list_of_coinciding_vacations = (ListView) findViewById(R.id.lv_list_of_coinciding_vacations);
        tv_vacation_start_date = (TextView) findViewById(R.id.tv_vacation_start_date_val);
        tv_vacation_stop_date = (TextView) findViewById(R.id.tv_vacation_end_date_val);
        tv_vacation_start_time = (TextView) findViewById(R.id.tv_vacation_start_time_val);
        tv_vacation_stop_time = (TextView) findViewById(R.id.tv_vacation_end_time_val);
        b_delete = (Button) findViewById(R.id.b_delete_slot);

        ll_class_slot_details = (ScrollView) findViewById(R.id.ll_class_slot_details);
        ll_list_of_mentees = (LinearLayout) findViewById(R.id.ll_list_of_mentees);
        ll_list_of_coincidingVacations = (LinearLayout) findViewById(R.id.ll_list_of_coincidingVacations);
        ll_non_coincidingLinearLayout = (LinearLayout) findViewById(R.id.ll_non_coinciding_vacation);
        ll_slot_details = (LinearLayout) findViewById(R.id.ll_slot_details);

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

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        if (calledApiValue == 50) {
    /* vacation deletion*/
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject((String) object);
                int status = Integer.parseInt(jsonObject.getString("status"));
                String message = jsonObject.getString("message");
                if (status == 1) {
                    /*success*/
                    Toast.makeText(AboutWeekViewEvent.this, message, Toast.LENGTH_SHORT).show();

                    setResult(6);
                    finish();
                } else {
                    if (status == 2) {
                        /* failure*/
                        Toast.makeText(AboutWeekViewEvent.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        if (status == 3)
                            Toast.makeText(AboutWeekViewEvent.this, message, Toast.LENGTH_SHORT).show();
                                                    /* wrong vacation id*/
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
    /* slot deletion*/
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject((String) object);
                int status = Integer.parseInt(jsonObject.getString("status"));
                String message = jsonObject.getString("message");
                if (status == 1) {
                    /*success*/
                    Toast.makeText(AboutWeekViewEvent.this, message, Toast.LENGTH_SHORT).show();
                    setResult(1);
                    finish();
                } else {
                    if (status == 2) {
                        /* failure*/
                        Toast.makeText(AboutWeekViewEvent.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        if (status == 3)
                            Toast.makeText(AboutWeekViewEvent.this, message, Toast.LENGTH_SHORT).show();
                                    /* wrong slot id*/
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        Toast.makeText(AboutWeekViewEvent.this, (String) object, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

    private void showAlertOnDelete(final int flag, final RequestParams requestParams) {  /* flag is 0 when this method is called to delete
                                                      slot and 1 when called to delete vacation*/
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.confirm_delete));
        ScrollView scrollView = new ScrollView(this);
        final TextView contentView = new TextView(this);
        if (flag == 0) {
            contentView.setText(getResources().getString(R.string.slot_delete_warning_message));
        } else {
            contentView.setText(getResources().getString(R.string.vacations_delete_warning_message));
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(8, 8, 8, 8);
        scrollView.addView(contentView);
        scrollView.setLayoutParams(params);
        alertDialog.setView(scrollView);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (flag == 0) {
                            progressDialog.show();
                            NetworkClient.deleteClassSlot(AboutWeekViewEvent.this, requestParams,
                                    StorageHelper.getUserDetails(AboutWeekViewEvent.this,
                                            "auth_token")
                                    , AboutWeekViewEvent.this, 51);
                        } else {
                            progressDialog.show();
                            NetworkClient.deleteVacation(AboutWeekViewEvent.this, requestParams,
                                    StorageHelper.getUserDetails(AboutWeekViewEvent.this,
                                            "auth_token")
                                    , AboutWeekViewEvent.this, 50);
                        }


                    }
                }
        );
        alertDialog.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                }
        );
        alertDialog.show();
    }


}



