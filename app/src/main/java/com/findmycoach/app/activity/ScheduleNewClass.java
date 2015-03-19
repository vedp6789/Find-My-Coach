package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment_mentor.StartDateDialogFragment;
import com.findmycoach.app.fragment_mentor.TillDateDialogFragment;
import com.findmycoach.app.util.SetDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by praka_000 on 3/4/2015.
 */
public class ScheduleNewClass extends Activity implements Button.OnClickListener, SetDate {
    Spinner sp_subjects, sp_class_timing, sp_mentor_for;
    CheckBox cb_mon, cb_tue, cb_wed, cb_thu, cb_fri, cb_sat, cb_sun;
    EditText et_location;
    RadioButton rb_pay_now, rb_pay_personally;
    private static Button b_payment, b_from_date, b_to_date;
    private static JSONObject mentor_Details, slots_detail, mentor_data;
    private static JSONArray jsonArray_sub_category_name, mon_slots, tue_slots, wed_slots, thu_slots, fri_slots, sat_slots, sun_slots;
    private final String TAG = "FMC";
    private ArrayList<String> sub_category_name, slot_timings;
    private static TreeSet<Float> slotsTimeTreeSet;
    private String selected_time;
    private JSONArray jsonArray;


    private static String date_from;
    private static String date_to;
    private static int from_day;// Starting day of schedule
    private static int from_month;//Starting month of the schedule
    private static int from_year;//Starting year of the schedule.
    private static int till_day;// completion day of the schedule
    private static int till_month;//completion month of the schedule
    private static int till_year;//completion year of the schedule


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_new_class);
        String fname = getIntent().getExtras().getString("fname");

        applyActionbarProperties(fname);


        initialize();


    }

    private void populateFields() {
        Log.d(TAG, "Inside populateFields method");
        sub_category_name = new ArrayList<String>();

        try {

            mentor_Details = new JSONObject(getIntent().getStringExtra("mentor_details"));
            Log.d(TAG,"Mentors Detail: "+mentor_Details.toString());
            mentor_data = mentor_Details.getJSONObject("data");
            slots_detail = new JSONObject();
            slots_detail = mentor_Details.getJSONObject("freeSlots");
            Log.d(TAG, "Slots detail: " + slots_detail.toString());
            jsonArray_sub_category_name = mentor_data.getJSONArray("sub_category_name");
            for (int i = 0; i < jsonArray_sub_category_name.length(); i++) {
                sub_category_name.add(jsonArray_sub_category_name.getString(i));
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ScheduleNewClass.this, android.R.layout.simple_spinner_item, sub_category_name.toArray(new String[sub_category_name.size()]));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_subjects.setAdapter(arrayAdapter);

            mon_slots = slots_detail.getJSONArray("M");
            tue_slots = slots_detail.getJSONArray("T");
            wed_slots = slots_detail.getJSONArray("W");
            thu_slots = slots_detail.getJSONArray("Th");
            fri_slots = slots_detail.getJSONArray("F");
            sat_slots = slots_detail.getJSONArray("S");
            sun_slots = slots_detail.getJSONArray("Su");
            //   Log.d(TAG,"Mon slots"+mon_slots.toString());


            slotsTimeTreeSet = new TreeSet<Float>();


            for (int m = 0; m < mon_slots.length(); m++) {
                jsonArray = mon_slots.getJSONArray(m);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                //  Log.d(TAG,"Float value for start time : "+ v);
                slotsTimeTreeSet.add(v);

            }
            for (int t = 0; t < tue_slots.length(); t++) {
                jsonArray = tue_slots.getJSONArray(t);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                //  Log.d(TAG,"Float value for start time : "+ v);
                slotsTimeTreeSet.add(v);

            }
            for (int w = 0; w < wed_slots.length(); w++) {
                jsonArray = wed_slots.getJSONArray(w);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                //  Log.d(TAG,"Float value for start time : "+ v);
                slotsTimeTreeSet.add(v);

            }
            for (int th = 0; th < thu_slots.length(); th++) {
                jsonArray = thu_slots.getJSONArray(th);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                //  Log.d(TAG,"Float value for start time : "+ v);
                slotsTimeTreeSet.add(v);

            }
            for (int f = 0; f < fri_slots.length(); f++) {
                jsonArray = fri_slots.getJSONArray(f);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                //  Log.d(TAG,"Float value for start time : "+ v);
                slotsTimeTreeSet.add(v);

            }
            for (int s = 0; s < sat_slots.length(); s++) {
                jsonArray = sat_slots.getJSONArray(s);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                // Log.d(TAG,"Float value for start time : "+ v);
                slotsTimeTreeSet.add(v);

            }
            for (int su = 0; su < sun_slots.length(); su++) {
                jsonArray = sun_slots.getJSONArray(su);
                String start_time = jsonArray.getString(0);
                String aFloat = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                float v = Float.parseFloat(aFloat);
                //Log.d(TAG,"Float value for start time : "+ v);
                slotsTimeTreeSet.add(v);

            }
            slot_timings = new ArrayList<String>();

            Iterator<Float> itr = slotsTimeTreeSet.iterator();
            while (itr.hasNext()) {
                float start_time = itr.next();
                //float stop_time=itr.next()+1;
                //System.out.println(TAG+"Start time in TreeSet : "+start_time+", Stop time acc to Tree set : "+ ++start_time);

                slot_timings.add(String.valueOf(start_time) + "-" + String.valueOf(++start_time));

            }

            ArrayAdapter arrayAdapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, slot_timings);
            arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_class_timing.setAdapter(arrayAdapter1);

            sp_class_timing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selected_time = parent.getItemAtPosition(position).toString();
                    boolean b_m = false, b_t = false, b_w = false, b_th = false, b_f = false, b_s = false, b_su = false;

                    cb_mon.setEnabled(true);
                    cb_tue.setEnabled(true);
                    cb_wed.setEnabled(true);
                    cb_thu.setEnabled(true);
                    cb_fri.setEnabled(true);
                    cb_sat.setEnabled(true);
                    cb_sun.setEnabled(true);

                    cb_mon.setChecked(false);
                    cb_tue.setChecked(false);
                    cb_wed.setChecked(false);
                    cb_thu.setChecked(false);
                    cb_fri.setChecked(false);
                    cb_sat.setChecked(false);
                    cb_sun.setChecked(false);


                    String selected_start_time = selected_time.split("-", 2)[0];
                    String selected_stop_time = selected_time.split("-", 2)[1];

                    Log.d(TAG, "selected start time :" + selected_start_time + " , selected stop time : " + selected_stop_time);


                    if (mon_slots.length() > 0) {
                        for (int m = 0; m < mon_slots.length(); m++) {
                            try {
                                jsonArray = mon_slots.getJSONArray(m);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
                                    Log.d(TAG, "time get matched for monday");
                                    b_m = true;
                                } else {
                                    Log.d(TAG, "time not get matched for monday");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        Log.d(TAG, "time not get matched for monday from else part");
                    }


                    if (tue_slots.length() > 0) {
                        for (int t = 0; t < tue_slots.length(); t++) {
                            try {
                                jsonArray = tue_slots.getJSONArray(t);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
                                    Log.d(TAG, "time get matched for tuesday");
                                    b_t = true;
                                } else {
                                    Log.d(TAG, "time not get matched for tuesday");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        Log.d(TAG, "time not get matched for tuesday from else part");
                    }


                    if (wed_slots.length() > 0) {
                        for (int w = 0; w < wed_slots.length(); w++) {
                            try {
                                jsonArray = wed_slots.getJSONArray(w);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
                                    Log.d(TAG, "time get matched for wednesday");
                                    b_w = true;
                                } else {
                                    Log.d(TAG, "time not get matched for wednesday");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        Log.d(TAG, "time not get matched for wednesday from else part");
                    }

                    if (thu_slots.length() > 0) {
                        for (int th = 0; th < thu_slots.length(); th++) {
                            try {
                                jsonArray = thu_slots.getJSONArray(th);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
                                    Log.d(TAG, "time get matched for thursday");
                                    b_th = true;
                                } else {
                                    Log.d(TAG, "time not get matched for thurday");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        Log.d(TAG, "time not get matched for thurday from else part");
                    }


                    if (fri_slots.length() > 0) {
                        for (int f = 0; f < fri_slots.length(); f++) {
                            try {
                                jsonArray = fri_slots.getJSONArray(f);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
                                    Log.d(TAG, "time get matched for fri");
                                    b_f = true;
                                } else {
                                    Log.d(TAG, "time not get matched for fri");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        Log.d(TAG, "time not get matched for fri from else part");
                    }


                    if (sat_slots.length() > 0) {
                        for (int s = 0; s < sat_slots.length(); s++) {
                            try {
                                jsonArray = sat_slots.getJSONArray(s);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
                                    Log.d(TAG, "time get matched for satday");
                                    b_s = true;
                                } else {
                                    Log.d(TAG, "time not get matched for satday");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        Log.d(TAG, "time not get matched for satday from else part");
                    }


                    if (sun_slots.length() > 0) {
                        for (int su = 0; su < sun_slots.length(); su++) {
                            try {
                                jsonArray = sun_slots.getJSONArray(su);
                                String start_time = jsonArray.getString(0);
                                String stop_time = jsonArray.getString(1);
                                String startTime_in_float_format = start_time.split(":", 3)[0] + "." + start_time.split(":", 3)[1];
                                String stopTime_in_float_format = stop_time.split(":", 3)[0] + "." + stop_time.split(":", 3)[1];
                                if (startTime_in_float_format.equals(selected_start_time) && stopTime_in_float_format.equals(selected_stop_time)) {
                                    Log.d(TAG, "time get matched for sunday");
                                    b_su = true;
                                } else {
                                    Log.d(TAG, "time not get matched for sunday");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        Log.d(TAG, "time not get matched for sunday from else part");
                    }

                    if (!b_m) {
                        cb_mon.setEnabled(false);
                    }
                    if (!b_t) {
                        cb_tue.setEnabled(false);
                    }
                    if (!b_w) {
                        cb_wed.setEnabled(false);
                    }
                    if (!b_th) {
                        cb_thu.setEnabled(false);
                    }
                    if (!b_f) {
                        cb_fri.setEnabled(false);
                    }
                    if (!b_s) {
                        cb_sat.setEnabled(false);
                    }
                    if (!b_su) {
                        cb_sun.setEnabled(false);
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            String availability=mentor_data.getString("availability_yn");
            if(!availability.equals("1")){

                et_location.setEnabled(false);
            }
           // mentor_data.get()

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    private void initialize() {
        sp_subjects = (Spinner) findViewById(R.id.sp_subjects);
        sp_class_timing = (Spinner) findViewById(R.id.sp_class_time);
        b_from_date = (Button) findViewById(R.id.b_from_date);
        b_from_date.setOnClickListener(this);
        b_to_date = (Button) findViewById(R.id.b_to_date);
        b_to_date.setOnClickListener(this);
        sp_mentor_for = (Spinner) findViewById(R.id.sp_mentor_for);
        cb_mon = (CheckBox) findViewById(R.id.cb_m);
        cb_tue = (CheckBox) findViewById(R.id.cb_t);
        cb_wed = (CheckBox) findViewById(R.id.cb_w);
        cb_thu = (CheckBox) findViewById(R.id.cb_th);
        cb_fri = (CheckBox) findViewById(R.id.cb_f);
        cb_sat = (CheckBox) findViewById(R.id.cb_s);
        cb_sun = (CheckBox) findViewById(R.id.cb_su);
        et_location = (EditText) findViewById(R.id.et_location);
        rb_pay_now = (RadioButton) findViewById(R.id.rb_pay_now);
        rb_pay_personally = (RadioButton) findViewById(R.id.pay_personally);
        b_payment = (Button) findViewById(R.id.b_proceed_to_payment);
        b_payment.setOnClickListener(this);
        populateFields();
    }


    private void applyActionbarProperties(String name) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(name);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mentor_details_not_connected, menu);
        return true;
    }*/

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
                break;
            case R.id.b_from_date:
                FragmentManager fragmentManager = getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("ComingFrom", "ScheduleNewClass");
                StartDateDialogFragment startDateDialogFragment = new StartDateDialogFragment();
                startDateDialogFragment.setArguments(bundle);
                startDateDialogFragment.show(fragmentManager, null);
                break;
            case R.id.b_to_date:
                FragmentManager fragmentManager1 = getFragmentManager();
                Bundle bundle1 = new Bundle();
                bundle1.putString("ComingFrom", "ScheduleNewClass");
                TillDateDialogFragment tillDateDialogFragment = new TillDateDialogFragment();
                tillDateDialogFragment.setArguments(bundle1);
                tillDateDialogFragment.show(fragmentManager1, null);
                break;
        }
    }

    @Override
    public void setSelectedStartDate(Object o1, Object o2, Object o3) {
        Log.d(TAG, o1.toString() + "/" + o2.toString() + "/" + o3.toString());
        int day = Integer.parseInt(o1.toString());
        ScheduleNewClass.from_day = day;
        int month = Integer.parseInt(o2.toString());
        ScheduleNewClass.from_month = month;
        int year = Integer.parseInt(o3.toString());
        ScheduleNewClass.from_year = year;
        Log.d(TAG, "From date:" + from_day + "/" + from_month + "/" + from_year);
        StringBuilder stringBuilder = new StringBuilder();
        if ((day / 10) > 0) {
            stringBuilder.append(day);
        } else {
            stringBuilder.append("" + 0 + day);
        }
        if ((month / 10) > 0) {
            stringBuilder.append("/" + month);
        } else {
            stringBuilder.append("/" + 0 + month);
        }
        stringBuilder.append("/" + year);
        Log.d(TAG, "start date:" + stringBuilder.toString());
        ScheduleNewClass.b_from_date.setText(stringBuilder.toString());
        ScheduleNewClass.date_from = String.valueOf(stringBuilder);


        if(ScheduleNewClass.date_from != null & ScheduleNewClass.date_to != null){
            Toast.makeText(ScheduleNewClass.this,"Start date : "+ScheduleNewClass.date_from,Toast.LENGTH_SHORT);
            Toast.makeText(ScheduleNewClass.this,"Stop date : "+ScheduleNewClass.date_to,Toast.LENGTH_SHORT);
        }




    }

    @Override
    public void setSelectedTillDate(Object o1, Object o2, Object o3, boolean b) {      /* fourth parameter is not going to be used here */
        Log.d(TAG, o1.toString() + "/" + o2.toString() + "/" + o3.toString());

        int day = Integer.parseInt(o1.toString());
        ScheduleNewClass.till_day = day;
        int month = Integer.parseInt(o2.toString());
        ScheduleNewClass.till_month = month;
        int year = Integer.parseInt(o3.toString());
        ScheduleNewClass.till_year = year;
        StringBuilder stringBuilder = new StringBuilder();
        if ((day / 10) > 0) {
            stringBuilder.append(day);
        } else {
            stringBuilder.append("" + 0 + day);
        }
        if ((month / 10) > 0) {
            stringBuilder.append("/" + month);
        } else {
            stringBuilder.append("/" + 0 + month);
        }
        stringBuilder.append("/" + year);
        Log.d(TAG, "till date:" + stringBuilder.toString());
        ScheduleNewClass.b_to_date.setText(stringBuilder.toString());
        date_to = stringBuilder.toString();
        if(ScheduleNewClass.date_from != null){
            Toast.makeText(ScheduleNewClass.this,"Start date : "+ScheduleNewClass.date_from.toString(),Toast.LENGTH_SHORT);
            Toast.makeText(ScheduleNewClass.this,"Stop date : "+ScheduleNewClass.date_to.toString(),Toast.LENGTH_SHORT);
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
        int[] from_date = {from_day, from_month - 1, from_year};
        return from_date;

    }

    @Override
    public void setTillUpperLimit(Object o1, Object o2, Object o3) {

    }
}
