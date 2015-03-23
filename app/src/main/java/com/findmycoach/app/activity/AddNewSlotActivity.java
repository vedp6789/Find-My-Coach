package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment_mentor.StartDateDialogFragment;
import com.findmycoach.app.fragment_mentor.StartTimeDialogFragment;
import com.findmycoach.app.fragment_mentor.StopTimeDialogFragment;
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
 * Created by praka_000 on 2/12/2015.
 */
public class AddNewSlotActivity extends Activity implements SetDate, SetTime {
    CheckBox cb_mon, cb_tue,
            cb_wed, cb_thur,
            cb_fri, cb_sat,
            cb_sun;

    public static TextView tv_start_date, tv_till_date, tv_start_time, tv_stop_time;
    boolean boo_mon_checked, boo_tue_checked,
            boo_wed_checked, boo_thurs_checked,
            boo_fri_checked, boo_sat_checked,
            boo_sun_checked;
    Button b_create_slot;
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

    private static ArrayList<String> days_array=null;

    ProgressDialog progressDialog;

    private void applyActionbarProperties() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.add_new_slot));
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
        setContentView(R.layout.activity_add_avail_slot);

        time_from = getResources().getString(R.string.select);
        time_to = getResources().getString(R.string.select);
        date_to = getResources().getString(R.string.forever);

        FOREVER = getResources().getString(R.string.forever);

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
        progressDialog = new ProgressDialog(AddNewSlotActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        /* Current date */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        String current_date = simpleDateFormat.format(new Date());

        date_from = current_date.substring(0, 2) + "/" + current_date.substring(2, 4) + "/" + current_date.substring(4, 8);
        from_day=Integer.parseInt(current_date.substring(0, 2));
        from_month=Integer.parseInt(current_date.substring(2, 4));
        from_year=Integer.parseInt(current_date.substring(4, 8));
        tv_start_date.setText(date_from);
        date_to = getResources().getString(R.string.forever);

        allListeners();

    }

    private void allListeners() {
        tv_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                StartTimeDialogFragment timeDialogFragment = new StartTimeDialogFragment();
                timeDialogFragment.show(fragmentManager, null);
            }
        });

        tv_stop_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_start_time.getText().length() > 0) {
                    FragmentManager fragmentManager = getFragmentManager();
                    StopTimeDialogFragment timeDialogFragment = new StopTimeDialogFragment();
                    timeDialogFragment.show(fragmentManager, null);
                } else {
                    Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.start_time_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "On click");
                FragmentManager fragmentManager = getFragmentManager();
StartDateDialogFragment dateDialogFragment = new StartDateDialogFragment();
                dateDialogFragment.addNewSlotActivity = AddNewSlotActivity.this;
                dateDialogFragment.show(fragmentManager, null);

            }
        });

        tv_till_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_start_date.getText().length() > 0) {
                    FragmentManager fragmentManager = getFragmentManager();
                    TillDateDialogFragment dateDialogFragment = new TillDateDialogFragment();
                    dateDialogFragment.addNewSlotActivity = AddNewSlotActivity.this;
                    dateDialogFragment.show(fragmentManager, null);
                } else {
                    Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.from_date_first), Toast.LENGTH_SHORT).show();
                }

            }
        });

        cb_mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_mon.isChecked()) {
                    boo_mon_checked = true;
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
} else {
                    boo_sun_checked = false;
                }
            }
        });

        b_create_slot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {

                    days_array=new ArrayList<String>();

                    if(boo_mon_checked){

                        days_array.add("M");
                    }
                    if(boo_tue_checked){
                        days_array.add("T");
                    }
                    if(boo_wed_checked){
                        days_array.add("W");
                    }
                    if(boo_thurs_checked){
                        days_array.add("Th");
                    }
                    if(boo_fri_checked){
                        days_array.add("F");
                    }
                    if(boo_sat_checked){
                        days_array.add("S");
                    }
                    if(boo_sun_checked){
                        days_array.add("Su");
                    }

                    if (StorageHelper.getUserGroup(AddNewSlotActivity.this, "user_group").equals("3")) {
                        Log.d(TAG, "Going to create a new slot for you.");
                        RequestParams requestParams = new RequestParams();
                        requestParams.add("mentor_id", StorageHelper.getUserDetails(AddNewSlotActivity.this, "user_id"));
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

                        StringBuilder stringBuilder1=new StringBuilder();
                        stringBuilder1.append(days_array.get(0));
                        if(days_array.size() >1){
                            for(int i=1;i<days_array.size();i++){
                                stringBuilder1.append(","+days_array.get(i));
                            }
                        }



                        requestParams.add("name",StorageHelper.getUserDetails(AddNewSlotActivity.this,"user_id")+"_Slot");
                        requestParams.add("dates", stringBuilder1.toString());

                        for(int i=0; i < days_array.size(); i++){
                            Log.d(TAG,"Day"+days_array.get(i));
                        }
                        Log.d(TAG,"days array"+String.valueOf(days_array)+", days array in string form "+stringBuilder1.toString());
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
                        Log.d(TAG,"Slot time value from AddNewSlotActivity"+slot_time_value/(60 * 60));
                        requestParams.add("slot_time_value", String.valueOf(slot_time_value));
                        String auth_token = StorageHelper.getUserDetails(AddNewSlotActivity.this, "auth_token");
                        progressDialog.show();
                        NetworkClient.createNewSlot(AddNewSlotActivity.this, requestParams, auth_token, new Callback() {

                            @Override
                            public void successOperation(Object object, int statusCode, int calledApiValue) {
                                Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.created_new_slot_successfully),Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void failureOperation(Object object, int statusCode, int calledApiValue) {
                                Toast.makeText(AddNewSlotActivity.this,(String) object,Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        },35);
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
                    Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_start_and_end_time), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    Log.d(TAG, "check3");
                    if (time_from.equals(getResources().getString(R.string.select))) {
                        Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_start_time), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "check4");
                        return false;

                    } else {
                        if (time_to.equals(getResources().getString(R.string.select))) {
                            Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_end_time), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "check5");
                            return false;
                        }


                    }

                    Log.d(TAG, "check6");
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
                int minimum_difference = (Integer.parseInt(getResources().getString(R.string.slot_time_difference_in_hour)) * 60) * 60;

                if (slot_time_value < minimum_difference) {
                    Toast.makeText(AddNewSlotActivity.this,getResources().getString(R.string.slot_time_difference), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "check7");
                    return false;
                }else{
                    if((slot_time_value % 3600) > 0 ){
                        Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_slot_in_multiple_of_hour), Toast.LENGTH_SHORT).show();
                        return false;
                    }else{
                        if (tv_start_date.getText().length() > 0) {
                            return true;
                        } else {
                            Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_start_date_of_slot), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }




                }

            }

        } else {
            Toast.makeText(AddNewSlotActivity.this, getResources().getString(R.string.select_at_least_one_day), Toast.LENGTH_SHORT).show();
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
        tv_start_date = (TextView) findViewById(R.id.tv_slot_start_date);
        tv_till_date = (TextView) findViewById(R.id.tv_slot_till_date);
        tv_start_time = (TextView) findViewById(R.id.tv_slot_start_time);
        tv_stop_time = (TextView) findViewById(R.id.tv_slot_stop_time);

        b_create_slot = (Button) findViewById(R.id.b_create_slot);

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
        AddNewSlotActivity.from_day = day;
        int month = Integer.parseInt(o2.toString());
        AddNewSlotActivity.from_month = month;
        int year = Integer.parseInt(o3.toString());
        AddNewSlotActivity.from_year = year;
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
        AddNewSlotActivity.tv_start_date.setText(stringBuilder.toString());
        AddNewSlotActivity.date_from = String.valueOf(stringBuilder);

        AddNewSlotActivity.tv_till_date.setText(date_to);
}

    @Override
    public void setSelectedTillDate(Object o1, Object o2, Object o3, boolean b) {
        Log.d(TAG, o1.toString() + "/" + o2.toString() + "/" + o3.toString());
        if (b) {
            AddNewSlotActivity.tv_till_date.setText(FOREVER);

        } else {
            int day = Integer.parseInt(o1.toString());
            AddNewSlotActivity.till_day = day;
            int month = Integer.parseInt(o2.toString());
            AddNewSlotActivity.till_month = month;
            int year = Integer.parseInt(o3.toString());
            AddNewSlotActivity.till_year = year;
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
            AddNewSlotActivity.tv_till_date.setText(stringBuilder.toString());
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


    @Override
    public void setSelectedStartTime(Object o1, Object o2) {
        String hour = (String) o1;
        AddNewSlotActivity.start_hour = Integer.parseInt(hour);
        String minute = (String) o2;
        AddNewSlotActivity.start_min = Integer.parseInt(minute);
        AddNewSlotActivity.time_from = hour + ":" + minute;
        tv_start_time.setText(hour + ":" + minute);

    }

    @Override
    public void setSelectedTillTime(Object o1, Object o2) {
        String hour = (String) o1;
        AddNewSlotActivity.stop_hour = Integer.parseInt(hour);
        String minute = (String) o2;
        AddNewSlotActivity.stop_min = Integer.parseInt(minute);
        AddNewSlotActivity.time_to = hour + ":" + minute;
        tv_stop_time.setText(hour + ":" + minute);
    }
}
