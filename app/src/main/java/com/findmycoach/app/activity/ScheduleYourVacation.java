package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment_mentor.StartDateDialogFragment;
import com.findmycoach.app.fragment_mentor.StartDateForVaccationSchedule;
import com.findmycoach.app.fragment_mentor.StartTimeDialogFragment;
import com.findmycoach.app.fragment_mentor.StartTimeForVaccationSchedule;
import com.findmycoach.app.fragment_mentor.StopDateForVacationSchedule;
import com.findmycoach.app.fragment_mentor.StopTimeDialogFragment;
import com.findmycoach.app.fragment_mentor.StopTimeForVaccationSchedule;
import com.findmycoach.app.fragment_mentor.TillDateDialogFragment;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.SetDate;
import com.findmycoach.app.util.SetTime;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by praka_000 on 3/3/2015.
 */
public class ScheduleYourVacation extends Activity implements SetDate,SetTime {
    CheckBox cb_mon, cb_tue,
            cb_wed, cb_thur,
            cb_fri, cb_sat,
            cb_sun;

    public static TextView tv_start_date, tv_till_date, tv_start_time, tv_stop_time;
    boolean boo_mon_checked, boo_tue_checked,
            boo_wed_checked, boo_thurs_checked,
            boo_fri_checked, boo_sat_checked,
            boo_sun_checked;
    EditText et_note_vaccation;
    Button b_schedule_your_vacation;
    private static String time_from;
    private static String time_to;
    private static String date_from;
    private static String date_to;
    private static int from_day;// Starting day of the slot
    private static int from_month;//Starting month of the slot
    private static int from_year;//Starting year of the slot.
    private static int till_day;// completion day of the slot
    private static int till_month;//completion month of the slot
    private static int till_year;//completion year of the slot.

    private static int start_hour;
    private static int start_min;
    private static int stop_hour;
    private static int stop_min;


    private static final String TAG = "FMC";
    private static String FOREVER;

    ArrayList<String> days_array = new ArrayList<String>();

    ProgressDialog progressDialog;

    private void applyActionbarProperties() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.schedule_vacation));
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_your_vaccation);
        time_from = getResources().getString(R.string.select);
        time_to = getResources().getString(R.string.select);
        date_to = getResources().getString(R.string.select);
        from_day = 0;
        from_month = 0;
        from_year = 0;

        boo_mon_checked = false;
        boo_tue_checked = false;
        boo_wed_checked = false;
        boo_thurs_checked = false;
        boo_fri_checked = false;
        boo_sat_checked = false;
        boo_sun_checked = false;

        initialize();
        applyActionbarProperties();

        progressDialog = new ProgressDialog(ScheduleYourVacation.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Current date ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        String current_date = simpleDateFormat.format(new Date());
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        date_from = current_date.substring(0, 2) + "/" + current_date.substring(2, 4) + "/" + current_date.substring(4, 8);
        from_day=Integer.parseInt(current_date.substring(0, 2));
        from_month=Integer.parseInt(current_date.substring(2, 4));
        from_year=Integer.parseInt(current_date.substring(4, 8));
        tv_start_date.setText(date_from);
        date_to = getResources().getString(R.string.forever);

        /*et_start_date.setInputType(InputType.TYPE_NULL);
        et_till_date.setInputType(InputType.TYPE_NULL);
        et_start_time.setInputType(InputType.TYPE_NULL);
        et_stop_time.setInputType(InputType.TYPE_NULL);
*/
        /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter(AddNewSlotActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.time1));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_time_from.setAdapter(arrayAdapter);
        sp_time_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                time_from = parent.getItemAtPosition(position).toString();
                if (time_from.equals("Select")) {
                    //Toast.makeText(AddNewSlotActivity.this,"Please select your slot start time.",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter(AddNewSlotActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.time1));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_time_to.setAdapter(arrayAdapter1);
        sp_time_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                time_to = parent.getItemAtPosition(position).toString();
                //Toast.makeText(AddNewSlotActivity.this,".."+time_to,Toast.LENGTH_SHORT).show();
                if (time_to.equals("Select")) {
                    //Toast.makeText(AddNewSlotActivity.this,"Please select your slot completion time.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/

        tv_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                StartTimeForVaccationSchedule startTimeForVaccationSchedule=new StartTimeForVaccationSchedule();
                startTimeForVaccationSchedule.show(fragmentManager, null);
            }
        });

        tv_stop_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_start_time.getText().length() > 0) {
                    FragmentManager fragmentManager = getFragmentManager();
                    StopTimeForVaccationSchedule stopTimeForVaccationSchedule=new StopTimeForVaccationSchedule();
                    stopTimeForVaccationSchedule.show(fragmentManager, null);
                } else {
                    Toast.makeText(ScheduleYourVacation.this, getResources().getString(R.string.start_time_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "On click");
                FragmentManager fragmentManager = getFragmentManager();
                StartDateForVaccationSchedule startDateForVaccationSchedule=new StartDateForVaccationSchedule();
                startDateForVaccationSchedule.show(fragmentManager, null);

                /*if (tv_start_date.getText().length() > 0) {

                } else {
                    FragmentManager fragmentManager = getFragmentManager();
                    StartDateDialogFragment dateDialogFragment = new StartDateDialogFragment();
                    dateDialogFragment.show(fragmentManager, null);
                }*/

            }
        });

        tv_till_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_start_date.getText().length() > 0) {
                    FragmentManager fragmentManager = getFragmentManager();
                    StopDateForVacationSchedule stopDateForVacationSchedule = new StopDateForVacationSchedule();
                    stopDateForVacationSchedule.show(fragmentManager, null);
                } else {
                    Toast.makeText(ScheduleYourVacation.this, getResources().getString(R.string.from_date_first), Toast.LENGTH_SHORT).show();
                }

            }
        });

        cb_mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_mon.isChecked()) {
                    boo_mon_checked = true;
                    days_array.add("M");
                } else {
                    boo_mon_checked = false;
                }
            }
        });

        cb_tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_tue.isChecked()) {
                    boo_tue_checked = true;
                    days_array.add("T");
                } else {
                    boo_tue_checked = false;
                }
            }
        });

        cb_wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_wed.isChecked()) {
                    boo_wed_checked = true;
                    days_array.add("W");
                } else {
                    boo_wed_checked = false;
                }
            }
        });

        cb_thur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_thur.isChecked()) {
                    boo_thurs_checked = true;
                    days_array.add("Th");
                } else {
                    boo_thurs_checked = false;
                }
            }
        });

        cb_fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_fri.isChecked()) {
                    boo_fri_checked = true;
                    days_array.add("F");
                } else {
                    boo_fri_checked = false;
                }
            }
        });

        cb_sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_sat.isChecked()) {
                    boo_sat_checked = true;
                    days_array.add("S");
                } else {
                    boo_sat_checked = false;
                }
            }
        });

        cb_sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_sun.isChecked()) {
                    boo_sun_checked = true;
                    days_array.add("Su");
                } else {
                    boo_sun_checked = false;
                }
            }
        });

        b_schedule_your_vacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {

                    if (StorageHelper.getUserGroup(ScheduleYourVacation.this, "user_group").equals("3")) {
                        Log.d(TAG, "Going to create a new slot for you.");
                        RequestParams requestParams = new RequestParams();
                        requestParams.add("mentor_id", StorageHelper.getUserDetails(ScheduleYourVacation.this, "user_id"));
                        Log.d(TAG, "From date" + tv_start_date.getText().toString());
                        Log.d(TAG, "Till date" + tv_till_date.getText().toString());

                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(String.valueOf(from_year));
                        if ((from_month / 10) > 0) {
                            stringBuilder.append("/" + from_month);
                        } else {
                            stringBuilder.append("/" + 0 + from_month);
                        }
                        if ((from_day / 10) > 0) {
                            stringBuilder.append("/" + from_day);
                        } else {
                            stringBuilder.append("/" + 0 + from_day);
                        }
                        Log.d(TAG, "start date:" + stringBuilder.toString());

                        requestParams.add("start_date", stringBuilder.toString());

                        if (tv_till_date.getText().toString().equals(getResources().getString(R.string.forever))) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(String.valueOf(from_year + 10));
                            if ((from_month / 10) > 0) {
                                stringBuilder2.append("/" + from_month);
                            } else {
                                stringBuilder2.append("/" + 0 + from_month);
                            }
                            if ((from_day / 10) > 0) {
                                stringBuilder2.append("/" + from_day);
                            } else {
                                stringBuilder2.append("/" + 0 + from_day);
                            }
                            Log.d(TAG, "till date1:" + stringBuilder2.toString());

                            requestParams.add("stop_date", stringBuilder2.toString());
                        } else {
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(String.valueOf(till_year));
                            if ((till_month / 10) > 0) {
                                stringBuilder3.append("/" + till_month);
                            } else {
                                stringBuilder3.append("/" + 0 + till_month);
                            }
                            if ((till_day / 10) > 0) {
                                stringBuilder3.append("/" + till_day);
                            } else {
                                stringBuilder3.append("/" + 0 + till_day);
                            }
                            Log.d(TAG, "till date2:" + stringBuilder3.toString());

                            requestParams.add("stop_date", stringBuilder3.toString());
                        }

                        requestParams.add("start_time", start_hour + ":" + start_min + ":" + "00");
                        requestParams.add("stop_time", stop_hour + ":" + stop_min + ":" + "00");
                        String[] days = new String[days_array.size()];

                        requestParams.add("name",StorageHelper.getUserDetails(ScheduleYourVacation.this,"user_id")+"_vacation_schedule");
                        //requestParams.add("dates", String.valueOf(days_array.toArray(days)));

                        requestParams.add("dates", String.valueOf(days_array));

                        for(int i=0; i < days_array.size(); i++){
                            Log.d(TAG,"Day"+days_array.get(i));
                        }
                        Log.d(TAG,"days array"+String.valueOf(days_array));
                        //Log.d(TAG,"days array"+String.valueOf(days_array.toArray(days)));
                        //requestParams.add("dates", "M,S,Su");
/*                        int start_time = ((start_hour * 60) + start_min) * 60;
                        int stop_time = ((stop_hour * 60) + stop_min) * 60;
                        int intermediate_time = (24 * 60) * 60;
                        int slot_time_value;
                        if (start_hour > stop_hour) {
                            int first_half_time = intermediate_time - start_time;
                            slot_time_value = first_half_time + stop_time;
                        } else {
                            slot_time_value = stop_time - start_time;
                        }
                        Log.d(TAG,"Slot time value from AddNewSlotActivity"+slot_time_value/(60 * 60));
                        requestParams.add("slot_time_value", String.valueOf(slot_time_value));*/
                        String auth_token = StorageHelper.getUserDetails(ScheduleYourVacation.this, "auth_token");
                        progressDialog.show();
                        NetworkClient.scheduleVaccation(ScheduleYourVacation.this, requestParams, auth_token, new Callback() {

                            @Override
                            public void successOperation(Object object, int statusCode, int calledApiValue) {
                                Toast.makeText(ScheduleYourVacation.this, "Hi, your vacation get scheduled.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void failureOperation(Object object, int statusCode, int calledApiValue) {
                                Toast.makeText(ScheduleYourVacation.this, "Unfortunately there is some problem during registering your vacation schedule.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }, 36);


                        //requestParams.add("start_date",)
                    }
                }
            }
        });
    }

    private boolean validate() {
        if (boo_mon_checked || boo_tue_checked || boo_wed_checked || boo_thurs_checked || boo_fri_checked || boo_sat_checked || boo_sun_checked) {
            if (time_from.equals(getResources().getString(R.string.select)) || time_to.equals(getResources().getString(R.string.select))) {
                Log.d(TAG, "check1");
                if (time_from.equals(getResources().getString(R.string.select)) && time_to.equals(getResources().getString(R.string.select))) {
                    Log.d(TAG, "check2");
                    Toast.makeText(ScheduleYourVacation.this, "Please select starting time & completion time for this vacation.", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    Log.d(TAG, "check3");
                    if (time_from.equals(getResources().getString(R.string.select))) {
                        Toast.makeText(ScheduleYourVacation.this, "Select starting time of this vacation.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "check4");
                        return false;

                    } else {
                        if (time_to.equals(getResources().getString(R.string.select))) {
                            Toast.makeText(ScheduleYourVacation.this, "Select completion time of this vacation.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "check5");
                            return false;
                        }


                    }

                    Log.d(TAG, "check6");
                    //return false;
                }
                return false;
            } else {
                int start_time = ((start_hour * 60) + start_min) * 60;
                int stop_time = ((stop_hour * 60) + stop_min) * 60;
                int intermediate_time = (24 * 60) * 60;
                int slot_time_value;
                if (start_hour > stop_hour) {
                    int first_half_time = intermediate_time - start_time;
                    slot_time_value = first_half_time + stop_time;
                } else {
                    slot_time_value = stop_time - start_time;
                }
                int minimum_difference = (Integer.parseInt(getResources().getString(R.string.slot_time_difference)) * 60) * 60;

                if (slot_time_value < minimum_difference) {
                    Toast.makeText(ScheduleYourVacation.this, "Minimum vacation duration should be " + getResources().getString(R.string.slot_time_difference) + " hour !", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "check7");
                    return false;
                }else{
                    if (tv_start_date.getText().length() > 0) {
                        if(tv_till_date.getText().length() > 0 ){
                            return true;
                        }else {
                            Toast.makeText(ScheduleYourVacation.this, "Select completion date of vacation.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    } else {
                        Toast.makeText(ScheduleYourVacation.this, "Select starting date of vacation.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

            }

        } else {
            Toast.makeText(ScheduleYourVacation.this, "Please at least select one day for this schedule.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    void initialize() {
        cb_mon = (CheckBox) findViewById(R.id.cb_mon);
        cb_tue = (CheckBox) findViewById(R.id.cb_tue);
        cb_wed = (CheckBox) findViewById(R.id.cb_wed);
        cb_thur = (CheckBox) findViewById(R.id.cb_thur);
        cb_fri = (CheckBox) findViewById(R.id.cb_fri);
        cb_sat = (CheckBox) findViewById(R.id.cb_sat);
        cb_sun = (CheckBox) findViewById(R.id.cb_sun);
        //sp_time_from = (Spinner) findViewById(R.id.sp_time_from);
        //sp_time_to = (Spinner) findViewById(R.id.sp_time_to);
        tv_start_date = (TextView) findViewById(R.id.tv_slot_start_date);
        tv_till_date = (TextView) findViewById(R.id.tv_slot_till_date);
        tv_start_time = (TextView) findViewById(R.id.tv_slot_start_time);
        tv_stop_time = (TextView) findViewById(R.id.tv_slot_stop_time);

        et_note_vaccation= (EditText) findViewById(R.id.et_vacation_note);

        b_schedule_your_vacation = (Button) findViewById(R.id.b_schedule_vacation);

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
    public void setSelectedStartDate(Object o1, Object o2, Object o3) {
        Log.d(TAG, o1.toString() + "/" + o2.toString() + "/" + o3.toString());
        int day = Integer.parseInt(o1.toString());
        ScheduleYourVacation.from_day = day;
        int month = Integer.parseInt(o2.toString());
        ScheduleYourVacation.from_month = month;
        int year = Integer.parseInt(o3.toString());
        ScheduleYourVacation.from_year = year;
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
        ScheduleYourVacation.tv_start_date.setText(stringBuilder.toString());
        ScheduleYourVacation.date_from = String.valueOf(stringBuilder);

        ScheduleYourVacation.tv_till_date.setText(date_to);

        // et_start_date.setText(stringBuilder.toString());
    }

    @Override
    public void setSelectedTillDate(Object o1, Object o2, Object o3, boolean b) {
        Log.d(TAG, o1.toString() + "/" + o2.toString() + "/" + o3.toString());

            int day = Integer.parseInt(o1.toString());
            ScheduleYourVacation.till_day = day;
            int month = Integer.parseInt(o2.toString());
            ScheduleYourVacation.till_month = month;
            int year = Integer.parseInt(o3.toString());
            ScheduleYourVacation.till_year = year;
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
            ScheduleYourVacation.tv_till_date.setText(stringBuilder.toString());


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


    @Override
    public void setSelectedStartTime(Object o1, Object o2) {
        String hour = (String) o1;
        ScheduleYourVacation.start_hour = Integer.parseInt(hour);
        String minute = (String) o2;
        ScheduleYourVacation.start_min = Integer.parseInt(minute);
        ScheduleYourVacation.time_from = hour + ":" + minute;
        tv_start_time.setText(hour + ":" + minute);

    }

    @Override
    public void setSelectedTillTime(Object o1, Object o2) {
        String hour = (String) o1;
        ScheduleYourVacation.stop_hour = Integer.parseInt(hour);
        String minute = (String) o2;
        ScheduleYourVacation.stop_min = Integer.parseInt(minute);
        ScheduleYourVacation.time_to = hour + ":" + minute;
        tv_stop_time.setText(hour + ":" + minute);
    }

}
