package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.AddSlotAdapter;
import com.findmycoach.app.beans.CalendarSchedule.SlotDurationDetailBean;
import com.findmycoach.app.beans.CalendarSchedule.VacationCoincidingSlot;
import com.findmycoach.app.fragment_mentee.ChildDOB;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.ScrollableGridView;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by praka_000 on 3/4/2015.
 */
public class ScheduleNewClass extends Activity implements Button.OnClickListener, Callback {

    private LinearLayout ll_child_dob, ll_location, ll_vacation;
    public static TextView tv_child_dob;
    private static TextView tv_from_date, tv_to_date, tv_class_timing, tv_subject, tv_total_charges;
    Spinner sp_subjects, sp_mentor_for;
    EditText et_location;
    RadioButton rb_pay_now, rb_pay_personally;
    private static Button b_payment;
    private final String TAG = "FMC";
    private String selected_mentor_for;
    RequestParams requestParams;
    private ProgressDialog progressDialog;
    public static String child_DOB = null;
    Bundle bundle;
    private String selected_subject = null;

    private Long slot_id;
    private String mentor_id;
    private String mentor_availability;
    private int slot_start_day;
    private int slot_start_month;
    private int slot_start_year;
    private int slot_stop_day;
    private int slot_stop_month;
    private int slot_stop_year;
    private int slot_start_hour;
    private int slot_start_minute;
    private int slot_stop_hour;
    private int slot_stop_minute;
    private ArrayList<String> arrayList_subcategory = null;
    private String slot_type;
    private String[] slot_on_week_days;
    private String charges;
    private ArrayList<SlotDurationDetailBean> slotDurationDetailBeans;
    private ArrayList<VacationCoincidingSlot> vacationCoincidingSlots;
    private ScrollableGridView gridView;
    private TextView title;
    private TextView tv_number_of_classes, tv_vacation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_new_class);
        String fname = getIntent().getExtras().getString("fname");
        progressDialog = new ProgressDialog(ScheduleNewClass.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        applyActionbarProperties(fname);
        initialize();
        bundle = getIntent().getBundleExtra("slot_bundle");
        finalizeDateTimeAndCharges();
        populateFields();
    }

    private void finalizeDateTimeAndCharges() {
        slot_id = bundle.getLong("slot_id");
        mentor_id = bundle.getString("mentor_id");
        mentor_availability = bundle.getString("mentor_availability");
        slot_start_day = bundle.getInt("slot_start_day");
        slot_start_month = bundle.getInt("slot_start_month");
        slot_start_year = bundle.getInt("slot_start_year");
        slot_stop_day = bundle.getInt("slot_stop_day");
        slot_stop_month = bundle.getInt("slot_stop_month");
        slot_stop_year = bundle.getInt("slot_stop_year");
        slot_start_hour = bundle.getInt("slot_start_hour");
        slot_start_minute = bundle.getInt("slot_start_minute");
        slot_stop_hour = bundle.getInt("slot_stop_hour");
        slot_stop_minute = bundle.getInt("slot_stop_minute");
        slot_on_week_days = bundle.getStringArray("slot_on_week_days");
        charges = bundle.getString("charges");
        arrayList_subcategory = bundle.getStringArrayList("arrayList_sub_category");
        slot_type = bundle.getString("slot_type");
        slotDurationDetailBeans = bundle.getParcelableArrayList("slot_duration_detail");
        vacationCoincidingSlots = bundle.getParcelableArrayList("slot_coinciding_vacation");


        /*
        * IN CASE OF GROUP SLOT_TYPE THERE IS NO NEED TO TAKE ADDRESS FROM USERS AS IT IS ASSUMED THAT GROUP CLASS WILL BE SCHEDULED AT MENTOR'S ADDRESS
        *
        *
        *
        * */

        Log.d(TAG, "slot_type : " + slot_type);

        if (slot_type.equalsIgnoreCase(getResources().getString(R.string.group))) {
            ll_location.setVisibility(View.GONE);
        }

        if (vacationCoincidingSlots.size() <= 0)
            ll_vacation.setVisibility(View.GONE);
        else {
            StringBuilder stringBuilder_comma_separated_vacation_duration = new StringBuilder();
            for (int vacation_coinciding_slot = 0; vacation_coinciding_slot < vacationCoincidingSlots.size(); vacation_coinciding_slot++) {
                VacationCoincidingSlot va = vacationCoincidingSlots.get(vacation_coinciding_slot);
                String vacation_start_date = va.getVacation_start_date();
                String vacation_stop_date = va.getVacation_stop_date();
                if (vacation_coinciding_slot == (vacationCoincidingSlots.size() - 1))
                    stringBuilder_comma_separated_vacation_duration.append(String.format("%02d-%02d-%d to %02d-%02d-%d", Integer.parseInt(vacation_start_date.split("-")[2]), Integer.parseInt(vacation_start_date.split("-")[1]), Integer.parseInt(vacation_start_date.split("-")[0]), Integer.parseInt(vacation_stop_date.split("-")[2]), Integer.parseInt(vacation_stop_date.split("-")[1]), Integer.parseInt(vacation_stop_date.split("-")[0])));
                else
                    stringBuilder_comma_separated_vacation_duration.append(String.format("%02d-%02d-%d to %02d-%02d-%d, ", Integer.parseInt(vacation_start_date.split("-")[2]), Integer.parseInt(vacation_start_date.split("-")[1]), Integer.parseInt(vacation_start_date.split("-")[0]), Integer.parseInt(vacation_stop_date.split("-")[2]), Integer.parseInt(vacation_stop_date.split("-")[1]), Integer.parseInt(vacation_stop_date.split("-")[0])));

            }
            tv_vacation.setText(stringBuilder_comma_separated_vacation_duration.toString());
        }


        tv_number_of_classes.setText(String.valueOf(slotDurationDetailBeans.size()));


        if (arrayList_subcategory.size() > 1) {
            sp_subjects.setVisibility(View.VISIBLE);
            ArrayAdapter arrayAdapter_sub_category = new ArrayAdapter(this, R.layout.textview, arrayList_subcategory);
            arrayAdapter_sub_category.setDropDownViewResource(R.layout.textview);
            sp_subjects.setAdapter(arrayAdapter_sub_category);
            sp_subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selected_subject = (String) parent.getItemAtPosition(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } else {
            tv_subject.setVisibility(View.VISIBLE);
            selected_subject = arrayList_subcategory.get(0);
            tv_subject.setText(selected_subject);
        }
        String timing = String.format("%02d:%02d to %02d:%02d", slot_start_hour, slot_start_minute, slot_stop_hour, slot_stop_minute);
        tv_class_timing.setText(timing);


        List<Integer> selectedDays = new ArrayList<>();
        for (int slot_week_day = 0; slot_week_day < slot_on_week_days.length; slot_week_day++) {
            String day = slot_on_week_days[slot_week_day];
            if (day.equals("M")) {
                selectedDays.add(0);
            }
            if (day.equals("T")) {
                selectedDays.add(1);
            }
            if (day.equals("W")) {
                selectedDays.add(2);
            }
            if (day.equals("Th")) {
                selectedDays.add(3);
            }
            if (day.equals("F")) {
                selectedDays.add(4);
            }
            if (day.equals("S")) {
                selectedDays.add(0);
            }
            if (day.equals("Su")) {
                selectedDays.add(6);
            }
        }

        gridView.setAdapter(new AddSlotAdapter(getResources().getStringArray(R.array.week_days_mon), selectedDays, this));


        Log.d(TAG, "mentor availability : " + bundle.getString("mentor_availability"));

/* Here i'm checking whether the current date is ahead of class start time or not , If ahead then this mentee's class schedule will start from the current date */
        Calendar cal = new GregorianCalendar();
        cal.set(slot_start_year, slot_start_month - 1, slot_start_day);
        long slot_start_date = cal.getTimeInMillis();


        Calendar rightNow = Calendar.getInstance();
        long rightNow_in_millis = rightNow.getTimeInMillis();
        int current_hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int current_minute = rightNow.get(Calendar.MINUTE);

        Log.d(TAG, "right now in millis: " + rightNow_in_millis + " slot_start_date in millis: " + slot_start_date);
        if (rightNow_in_millis >= slot_start_date) {

            /* Mentee is looking to schedule when class slot is already behind the current date i.e. he is looking to join class in mid of class schedule  */

            String from_date;
            if (current_hour > slot_start_hour) {
                /* increasing schedule start date by one day i.e. slot_start_date is before current date and current time is also greater than slot_start_time*/
                from_date = String.format("%02d-%02d-%d", rightNow.get(Calendar.DAY_OF_MONTH) + 1, (rightNow.get(Calendar.MONTH) + 1), rightNow.get(Calendar.YEAR));
            } else {

                    /* if current hour is behing slot start hour or it is equal to it , then start day of class schedule will be from this current date */
                from_date = String.format("%02d-%02d-%d", rightNow.get(Calendar.DAY_OF_MONTH), (rightNow.get(Calendar.MONTH) + 1), rightNow.get(Calendar.YEAR));

            }


            tv_from_date.setText(from_date);
        } else {
            String to_date = String.format("%02d-%02d-%d", slot_start_day, slot_start_month, slot_start_year);
            tv_from_date.setText(to_date);
        }


        String to_date = String.format("%02d-%02d-%d", slot_stop_day, slot_stop_month, slot_stop_year);
        tv_to_date.setText(to_date);


        if (mentor_availability != null && mentor_availability.equals("1")) {
            if (slot_type.equalsIgnoreCase(getResources().getString(R.string.group))) {
                ll_location.setVisibility(View.GONE);
            } else {
                ll_location.setVisibility(View.VISIBLE);
            }
        }


        String class_schedule_start_date = tv_from_date.getText().toString();   /* Date which is getting prompted to student, from where student class schedule starts */
        int class_schedule_start_day = Integer.parseInt(class_schedule_start_date.split("-", 3)[0]);
        int class_schedule_start_month = Integer.parseInt(class_schedule_start_date.split("-", 3)[1]);
        int class_schedule_start_year = Integer.parseInt(class_schedule_start_date.split("-", 3)[2]);


        Calendar calendar_stop_date_of_schedule = Calendar.getInstance();
        calendar_stop_date_of_schedule.set(slot_stop_year, slot_stop_month - 1, slot_stop_day);

        Calendar calendar_schedule_start_date = Calendar.getInstance();
        calendar_schedule_start_date.set(class_schedule_start_year, class_schedule_start_month - 1, class_schedule_start_day);

        /*int valid_class_days = 0;

        valid_class_days = calculateNoOfTotalClassDays(calendar_schedule_start_date, calendar_stop_date_of_schedule, slot_on_week_days);



        Log.d(TAG, "valid days " + valid_class_days);
        int total_amount = 0;
        int cost = Integer.parseInt(charges.split(" per ", 2)[0]);
        String cost_basis=charges.split(" per ",2)[1];
        if(cost_basis.equalsIgnoreCase("hour")){
            if(valid_class_days == 0)
                valid_class_days=1;
            int no_of_hours_in_a_day=slot_stop_hour-slot_start_hour;
            int no_of_total_hours= valid_class_days * no_of_hours_in_a_day;
            total_amount=no_of_total_hours*cost;
        }else{
            *//*  this will not allowed now as there is only cost_basis that is per hour*//*
        }

        */

        int total_class_days;
        int total_amount = 0;
        int cost = Integer.parseInt(charges.split(" per ", 2)[0]);
        String cost_basis = charges.split(" per ", 2)[0];
        if (cost_basis.equalsIgnoreCase("hour")) {
            total_class_days = slotDurationDetailBeans.size();
            int no_of_hours_in_a_day = slot_stop_hour - slot_start_hour;
            int no_of_total_hours = total_class_days * no_of_hours_in_a_day;
            total_amount = no_of_total_hours * cost;

        } else {
             /*this will not allowed now as there is only cost_basis that is per hour*/
        }


        Log.d(TAG, "Total amount :" + total_amount);
        try {
            tv_total_charges.setText("\u20B9 " + String.valueOf(total_amount));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int calculateNoOfTotalClassDays(Calendar calendar_schedule_start_date, Calendar calendar_stop_date_of_schedule, String[] slot_on_week_days) {

        int workDays = 0;

        //Return 0 if start and end are the same
        if (calendar_schedule_start_date.getTimeInMillis() == calendar_stop_date_of_schedule.getTimeInMillis()) {
            return 0;
        }

        List<Integer> selectedDays = new ArrayList<Integer>();
        for (String d : slot_on_week_days) {
            if (d.equalsIgnoreCase("su"))
                selectedDays.add(1);
            if (d.equalsIgnoreCase("m"))
                selectedDays.add(2);
            if (d.equalsIgnoreCase("t"))
                selectedDays.add(3);
            if (d.equalsIgnoreCase("w"))
                selectedDays.add(4);
            if (d.equalsIgnoreCase("th"))
                selectedDays.add(5);
            if (d.equalsIgnoreCase("f"))
                selectedDays.add(6);
            if (d.equalsIgnoreCase("s"))
                selectedDays.add(7);
        }

        do {
            //excluding start date

            if (selectedDays.contains(calendar_schedule_start_date.get(Calendar.DAY_OF_WEEK))) {
                ++workDays;

            }
            calendar_schedule_start_date.add(Calendar.DAY_OF_MONTH, 1);


        }
        while (calendar_schedule_start_date.getTimeInMillis() <= calendar_stop_date_of_schedule.getTimeInMillis()); //excluding end date

        return workDays;
    }


    private void populateFields() {


        String[] mentor_for = {getResources().getString(R.string.self), getResources().getString(R.string.child)};
        ArrayAdapter arrayAdapter1_mentor_for = new ArrayAdapter(this, R.layout.textview, mentor_for);
        arrayAdapter1_mentor_for.setDropDownViewResource(R.layout.textview);
        sp_mentor_for.setAdapter(arrayAdapter1_mentor_for);
        sp_mentor_for.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                child_DOB = null;
                selected_mentor_for = null;
                selected_mentor_for = (String) parent.getItemAtPosition(position);
                if (selected_mentor_for.equals(getResources().getString(R.string.child))) {
                    ll_child_dob.setVisibility(View.VISIBLE);
                } else {
                    ll_child_dob.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ScheduleNewClass.this, "onNothingSelected", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void initialize() {
        sp_subjects = (Spinner) findViewById(R.id.sp_subjects);
        tv_class_timing = (TextView) findViewById(R.id.tv_class_timing);
        tv_subject = (TextView) findViewById(R.id.tv_subject);
        tv_subject.setVisibility(View.GONE);
        sp_subjects.setVisibility(View.GONE);
        tv_from_date = (TextView) findViewById(R.id.tv_date_from_dp);
        tv_from_date.setOnClickListener(this);
        tv_to_date = (TextView) findViewById(R.id.tv_date_to_dp);
        tv_to_date.setOnClickListener(this);
        tv_child_dob = (TextView) findViewById(R.id.tv_child_dob);
        tv_child_dob.setOnClickListener(this);
        tv_total_charges = (TextView) findViewById(R.id.tv_total_charge);
        ll_child_dob = (LinearLayout) findViewById(R.id.ll_child_dob);
        ll_location = (LinearLayout) findViewById(R.id.ll_location);
        ll_location.setVisibility(View.GONE);
        sp_mentor_for = (Spinner) findViewById(R.id.sp_mentor_for);
        tv_number_of_classes = (TextView) findViewById(R.id.tv_number_of_classes);
        ll_vacation = (LinearLayout) findViewById(R.id.ll_vacations);
        tv_vacation = (TextView) findViewById(R.id.tv_vacations);

        gridView = (ScrollableGridView) findViewById(R.id.calendar);


        et_location = (EditText) findViewById(R.id.et_location);

        rb_pay_now = (RadioButton) findViewById(R.id.rb_pay_now);
        rb_pay_personally = (RadioButton) findViewById(R.id.pay_personally);

        b_payment = (Button) findViewById(R.id.b_proceed_to_payment);
        b_payment.setOnClickListener(this);


        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = (TextView) findViewById(R.id.title);

    }


    private void showAlertMessageOnPayPersonally() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.payment_note_title));
        ScrollView scrollView = new ScrollView(this);
        final TextView contentView = new TextView(this);
        contentView.setText(getResources().getString(R.string.payment_note_content));
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
                        RequestParams requestParams1 = new RequestParams();
                        requestParams1.add("id", slot_id.toString());
                        requestParams1.add("mentor_id", mentor_id);
                        String student_id = StorageHelper.getUserDetails(ScheduleNewClass.this, "user_id");
                        Log.d(TAG, "student_id what getting sent to server : " + student_id);
                        requestParams1.add("student_id", student_id);
                        String from_date = tv_from_date.getText().toString();
                        requestParams1.add("start_date", from_date.split("-", 3)[2] + "-" + from_date.split("-", 3)[1] + "-" + from_date.split("-", 3)[0]);
                        requestParams1.add("slot_type", slot_type);
                        if (mentor_availability.equals("1")) {
                            requestParams1.add("location", et_location.getText().toString());
                        }
//                        int sub_category_id = DataBase.singleton(ScheduleNewClass.this).getSubCategoryId(selected_subject);
//                        requestParams1.add("sub_category_id", String.valueOf(sub_category_id));
                        if (selected_mentor_for.equalsIgnoreCase("child")) {
                            String date_of_birth_kid = tv_child_dob.getText().toString().split("-", 3)[2] + "-" + tv_child_dob.getText().toString().split("-", 3)[1] + "-" + tv_child_dob.getText().toString().split("-", 3)[0];
                            requestParams1.add("date_of_birth_kid", date_of_birth_kid);
                        }
                        requestParams1.add("total_price", tv_total_charges.getText().toString());
                        progressDialog.show();
                        NetworkClient.postScheduleRequest(ScheduleNewClass.this, requestParams1, ScheduleNewClass.this, 46);
                        // Log.d(TAG,"id : "+slot_id.toString()+"student_id"+student_id+"mentor_id: "+mentor_id+" start_date : "+from_date.split("-",3)[2]+"-"+from_date.split("-",3)[1]+"-"+from_date.split("-",3)[0]+"slot_type : "+slot_type+" sub_category_id : "+sub_category_id+" total_price : "+tv_total_charges.getText().toString()+"date_of_birth_kid"+tv_child_dob.getText().toString().split("-",3)[2]+"-"+tv_child_dob.getText().toString().split("-",3)[1]+"-"+tv_child_dob.getText().toString().split("-",3)[0]);

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


    private void applyActionbarProperties(String name) {
//        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle(name);
//        }
    }


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
        switch (v.getId()) {
            case R.id.b_proceed_to_payment:
                if (rb_pay_now.isChecked()) {
                    if (validate()) {
                        Toast.makeText(ScheduleNewClass.this, "Waiting for payment gateway integration. ", Toast.LENGTH_SHORT).show();
                    }
                }
                if (rb_pay_personally.isChecked()) {
                    if (validate()) {
                        showAlertMessageOnPayPersonally();
                    }
                }
                break;

            case R.id.tv_child_dob:
                FragmentManager fragmentManager2 = getFragmentManager();
                ChildDOB childDOB = new ChildDOB();
                childDOB.scheduleNewClass = ScheduleNewClass.this;
                childDOB.show(fragmentManager2, null);
                break;
        }
    }

    private boolean validate() {

        if (mentor_availability.equals("1")) {
            if (!slot_type.equalsIgnoreCase(getResources().getString(R.string.group))) {
                if (et_location.getText().toString().trim().length() <= 0) {

                    Toast.makeText(this, getResources().getString(R.string.your_address_please), Toast.LENGTH_SHORT).show();
                    showErrorMessage(et_location, getResources().getString(R.string.your_address_please));
                    return false;

                }
            }

        }
        if (selected_mentor_for.equalsIgnoreCase("child")) {
            if (tv_child_dob.getText().toString().trim().length() <= 0) {
                Toast.makeText(this, getResources().getString(R.string.child_date_of_birth_please), Toast.LENGTH_SHORT).show();
                showErrorMessage(et_location, getResources().getString(R.string.child_date_of_birth_please));
                return false;
            }
        }
        return true;

    }


    /**
     * Displaying error is any detail is wrong
     */
    private void showErrorMessage(final TextView view, String string) {
        view.setError(string);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setError(null);
            }
        }, 3500);
    }


    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        /*try {
           // JSONObject jsonObject=new JSONObject((String)object);
           // String message=jsonObject.getString("message");

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        Toast.makeText(ScheduleNewClass.this, (String) object, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        /*try {
            JSONObject jsonObject=new JSONObject((String)object);
            String message=jsonObject.getString("message");

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        Toast.makeText(ScheduleNewClass.this, (String) object, Toast.LENGTH_SHORT).show();

    }
}
