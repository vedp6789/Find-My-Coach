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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment_mentor.StartDateForVaccationSchedule;
import com.findmycoach.app.fragment_mentor.StartTimeForVaccationSchedule;
import com.findmycoach.app.fragment_mentor.StopDateForVacationSchedule;
import com.findmycoach.app.fragment_mentor.StopTimeForVaccationSchedule;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.SetDate;
import com.findmycoach.app.util.SetTime;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by praka_000 on 3/3/2015.
 */
public class ScheduleYourVacation extends Activity implements SetDate, SetTime {
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
    private String date_to;
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

    private Date newDate;

    ArrayList<String> days_array = new ArrayList<String>();

    ProgressDialog progressDialog;

    private void applyActionbarProperties() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
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
        date_from = current_date.substring(0, 2) + "-" + current_date.substring(2, 4) + "-" + current_date.substring(4, 8);
        from_day = Integer.parseInt(current_date.substring(0, 2));
        from_month = Integer.parseInt(current_date.substring(2, 4));
        from_year = Integer.parseInt(current_date.substring(4, 8));
        tv_start_date.setText(date_from);
        date_to = getResources().getString(R.string.how_long);


        tv_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                StartTimeForVaccationSchedule startTimeForVaccationSchedule = new StartTimeForVaccationSchedule();
                startTimeForVaccationSchedule.show(fragmentManager, null);
            }
        });

        tv_stop_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_start_time.getText().length() > 0) {
                    FragmentManager fragmentManager = getFragmentManager();
                    StopTimeForVaccationSchedule stopTimeForVaccationSchedule = new StopTimeForVaccationSchedule();
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
                StartDateForVaccationSchedule startDateForVaccationSchedule = new StartDateForVaccationSchedule();
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
                            stringBuilder.append("-" + from_month);
                        } else {
                            stringBuilder.append("-" + 0 + from_month);
                        }
                        if ((from_day / 10) > 0) {
                            stringBuilder.append("-" + from_day);
                        } else {
                            stringBuilder.append("-" + 0 + from_day);
                        }
                        Log.d(TAG, "start date:" + stringBuilder.toString());

                        requestParams.add("start_date", stringBuilder.toString());

                        if (tv_till_date.getText().toString().equals(getResources().getString(R.string.how_long))) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(String.valueOf(from_year + 10));
                            if ((from_month / 10) > 0) {
                                stringBuilder2.append("-" + from_month);
                            } else {
                                stringBuilder2.append("-" + 0 + from_month);
                            }
                            if ((from_day / 10) > 0) {
                                stringBuilder2.append("-" + from_day);
                            } else {
                                stringBuilder2.append("-" + 0 + from_day);
                            }
                            Log.d(TAG, "till date1:" + stringBuilder2.toString());

                            requestParams.add("stop_date", stringBuilder2.toString());
                        } else {
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(String.valueOf(till_year));
                            if ((till_month / 10) > 0) {
                                stringBuilder3.append("-" + till_month);
                            } else {
                                stringBuilder3.append("-" + 0 + till_month);
                            }
                            if ((till_day / 10) > 0) {
                                stringBuilder3.append("-" + till_day);
                            } else {
                                stringBuilder3.append("-" + 0 + till_day);
                            }
                            Log.d(TAG, "till date2:" + stringBuilder3.toString());

                            requestParams.add("stop_date", stringBuilder3.toString());
                        }

                        requestParams.add("start_time", start_hour + ":" + start_min + ":" + "00");
                        requestParams.add("stop_time", stop_hour + ":" + stop_min + ":" + "00");
                        String[] days = new String[days_array.size()];

                        requestParams.add("name", StorageHelper.getUserDetails(ScheduleYourVacation.this, "user_id") + "_vacation_schedule");

                        requestParams.add("dates", String.valueOf(days_array));

                        for (int i = 0; i < days_array.size(); i++) {
                            Log.d(TAG, "Day" + days_array.get(i));
                        }
                        Log.d(TAG, "days array" + String.valueOf(days_array));

                        String auth_token = StorageHelper.getUserDetails(ScheduleYourVacation.this, "auth_token");
                        progressDialog.show();
                        NetworkClient.scheduleVacation(ScheduleYourVacation.this, requestParams, auth_token, new Callback() {

                            @Override
                            public void successOperation(Object object, int statusCode, int calledApiValue) {
                                Toast.makeText(ScheduleYourVacation.this, getResources().getString(R.string.vacation_scheduled_success), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void failureOperation(Object object, int statusCode, int calledApiValue) {
                                Toast.makeText(ScheduleYourVacation.this, (String) object, Toast.LENGTH_SHORT).show();
                                String failure_response = (String) object;
                                try {
                                    JSONObject jsonObject = new JSONObject(failure_response);
                                    JSONArray jsonArray_coincidingExceptions = jsonObject.getJSONArray("coincidingExceptions");
                                    //JSONArray coninciding_days=jsonArray_coincidingExceptions.getJSONArray("W");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                            }
                        }, 36);

                    }
                }
            }
        });
    }

    private boolean validate() {
        if (cb_mon.isChecked() || cb_tue.isChecked() || cb_wed.isChecked() || cb_thur.isChecked() || cb_fri.isChecked() || cb_sat.isChecked() || cb_sun.isChecked()) {
            if (time_from.equals("00:00") || time_to.equals("24:00")) {


                /*  default time is available so we do not need to restrict user on time selection */

                /*if (time_from.equals(getResources().getString(R.string.select)) && time_to.equals(getResources().getString(R.string.select))) {
                    Toast.makeText(ScheduleYourVacation.this, getResources().getString(R.string.select_start_and_end_time), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    if (time_from.equals(getResources().getString(R.string.select))) {
                        Toast.makeText(ScheduleYourVacation.this, getResources().getString(R.string.select_start_time), Toast.LENGTH_SHORT).show();
                        return false;

                    } else {
                        if (time_to.equals(getResources().getString(R.string.select))) {
                            Toast.makeText(ScheduleYourVacation.this, getResources().getString(R.string.select_end_time), Toast.LENGTH_SHORT).show();
                            return false;
                        }


                    }

                }
                return false;*/

                return dateValidation();


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

                return dateValidation();

            }

           /* }*/

        } else {
            Toast.makeText(ScheduleYourVacation.this, getResources().getString(R.string.select_at_least_one_day), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    boolean dateValidation() {
        if (tv_start_date.getText().length() > 0) {
            if (tv_till_date.getText().length() > 0) {
                if (checkDaysAvailability(tv_start_date.getText().toString(), tv_till_date.getText().toString())) {
                    return true;
                } else {
                    return false;
                }

            } else {
                Toast.makeText(ScheduleYourVacation.this, getResources().getString(R.string.select_end_date_of_vacation), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(ScheduleYourVacation.this, getResources().getString(R.string.select_start_date_of_vacation), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    boolean checkDaysAvailability(String start_date, String till_date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date start_convertedDate = null, till_CovertedDate = null/*,todayWithZeroTime=null*/;
        try {
            start_convertedDate = dateFormat.parse(start_date);
            till_CovertedDate = dateFormat.parse(till_date);

            /*Date today = new Date();

            todayWithZeroTime =dateFormat.parse(dateFormat.format(today));*/
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int s_year = 0, s_month = 0, s_day = 0;

        Calendar s_cal = Calendar.getInstance();
        s_cal.setTime(start_convertedDate);

        s_year = s_cal.get(Calendar.YEAR);
        s_month = s_cal.get(Calendar.MONTH);
        s_day = s_cal.get(Calendar.DAY_OF_MONTH);


        Calendar t_cal = Calendar.getInstance();
        t_cal.setTime(till_CovertedDate);

        int t_year = t_cal.get(Calendar.YEAR);
        int t_month = t_cal.get(Calendar.MONTH);
        int t_day = t_cal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(s_year, s_month, s_day);
        date2.clear();
        date2.set(t_year, t_month, t_day);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);
        int day_count = (int) dayCount + 1;

        Log.d(TAG, "Duration difference : " + dayCount + ", in Days : " + day_count);

        int no_of_odd_days = (day_count % 7);

        if (no_of_odd_days > 0 && day_count < 7) {
            int weeks_having_no_odd_days = (day_count / 7);   /* Week number from start date and stop date having odd days*/
            int move_by = weeks_having_no_odd_days * 7;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start_convertedDate);
            calendar.add(Calendar.DAY_OF_YEAR, move_by);

            newDate = calendar.getTime();
            Log.d(TAG, " start of odd dates from this date by adding one date " + dateFormat.format(newDate) + " no of odd days : " + no_of_odd_days);

            ArrayList<String> checkedWeekDays = getListForCheckedDays();
            Log.d(TAG, "Checked day arraylist values");
            for (int i = 0; i < checkedWeekDays.size(); i++) {
                Log.d(TAG, " Checked days :" + checkedWeekDays.get(i));
            }

            Log.d(TAG, "ArrayList of selected week days : " + checkedWeekDays.size());
            if (checkedWeekDays.size() > 0) {

                String weeK_day = dayOfDate(newDate);
                Log.d(TAG, "First week day from odd days : " + weeK_day);
                for (int i = 0; i < checkedWeekDays.size(); i++) {
                    String day = checkedWeekDays.get(i);
                    if (weeK_day.equals(day)) {
                        Log.d(TAG, "Initial day get removed !");
                        checkedWeekDays.remove(i);
                    }
                }


                Log.d(TAG, "Checked day arraylist values after first deletion ");
                for (int i = 0; i < checkedWeekDays.size(); i++) {
                    Log.d(TAG, " Checked days :" + checkedWeekDays.get(i));
                }

                int loop = 1;

                while (loop < no_of_odd_days) {
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(newDate);
                    calendar1.add(Calendar.DAY_OF_YEAR, 1);
                    newDate = calendar1.getTime();
                    Log.d(TAG, " new odd date from " + loop + " : " + dateFormat.format(newDate));
                    Log.d(TAG, "ArrayList of selected week days from odd day loop " + loop + " is :" + checkedWeekDays.size());
                    if (checkedWeekDays.size() > 0) {
                        String weeK_day1 = dayOfDate(newDate);
                        for (int i = 0; i < checkedWeekDays.size(); i++) {
                            String day = checkedWeekDays.get(i);
                            if (weeK_day1.equals(day)) {
                                checkedWeekDays.remove(i);
                            }
                        }
                    }
                    ++loop;
                }

                /*do {
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(newDate);
                    calendar1.add(Calendar.DAY_OF_YEAR, 1);
                    newDate = calendar1.getTime();
                    Log.d(TAG, " new odd date from " + loop +" : "+ dateFormat.format(newDate));
                    Log.d(TAG, "ArrayList of selected week days from odd day loop " + loop + " is :" + checkedWeekDays.size());
                    if (checkedWeekDays.size() > 0) {
                        String weeK_day1 = dayOfDate(newDate);
                        for (int i = 0; i < checkedWeekDays.size(); i++) {
                            String day = checkedWeekDays.get(i);
                            if (weeK_day1.equals(day)) {
                                checkedWeekDays.remove(i);
                            }
                        }
                    }
                    ++loop;

                } while (loop < no_of_odd_days);
*/
                Log.d(TAG, "Selected week day size after all operation " + checkedWeekDays.size());

                Log.d(TAG, "Checked day arraylist values after all deletions");
                for (int i = 0; i < checkedWeekDays.size(); i++) {
                    Log.d(TAG, " Checked days :" + checkedWeekDays.get(i));
                }

                if (checkedWeekDays.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String day = checkedWeekDays.get(0);
                    if (day.equals("1"))
                        stringBuilder.append("Sun");
                    if (day.equals("2"))
                        stringBuilder.append("Mon");
                    if (day.equals("3"))
                        stringBuilder.append("Tue");
                    if (day.equals("4"))
                        stringBuilder.append("Wed");
                    if (day.equals("5"))
                        stringBuilder.append("Thu");
                    if (day.equals("6"))
                        stringBuilder.append("Fri");
                    if (day.equals("7"))
                        stringBuilder.append("Sat");


                    for (int s = 1; s < checkedWeekDays.size(); s++) {
                        String day1 = checkedWeekDays.get(s);

                        if (day1.equals("1"))
                            stringBuilder.append(", Sun");
                        if (day1.equals("2"))
                            stringBuilder.append(", Mon");
                        if (day1.equals("3"))
                            stringBuilder.append(", Tue");
                        if (day1.equals("4"))
                            stringBuilder.append(", Wed");
                        if (day1.equals("5"))
                            stringBuilder.append(", Thu");
                        if (day1.equals("6"))
                            stringBuilder.append(", Fri");
                        if (day1.equals("7"))
                            stringBuilder.append(", Sat");


                    }
                    Toast.makeText(ScheduleYourVacation.this, "Selected week-days for " + stringBuilder.toString() + " are not coming in the selected duration.", Toast.LENGTH_LONG).show();
                    return false;
                } else
                    return true;


            } else {
                return true;
            }
        } else {
            return true;
        }


    }

    private String dayOfDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date); // yourdate is a object of type Date

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return String.valueOf(dayOfWeek);
    }

    private ArrayList<String> getListForCheckedDays() {
        ArrayList<String> days_checked = new ArrayList<String>();
        if (cb_mon.isChecked())
            days_checked.add("2");
        if (cb_tue.isChecked())
            days_checked.add("3");
        if (cb_wed.isChecked())
            days_checked.add("4");
        if (cb_thur.isChecked())
            days_checked.add("5");
        if (cb_fri.isChecked())
            days_checked.add("6");
        if (cb_sat.isChecked())
            days_checked.add("7");
        if (cb_sun.isChecked())
            days_checked.add("1");

        return days_checked;

    }

    void initialize() {
        cb_mon = (CheckBox) findViewById(R.id.cb_mon);
        cb_mon.setChecked(true);
        cb_tue = (CheckBox) findViewById(R.id.cb_tue);
        cb_tue.setChecked(true);
        cb_wed = (CheckBox) findViewById(R.id.cb_wed);
        cb_wed.setChecked(true);
        cb_thur = (CheckBox) findViewById(R.id.cb_thur);
        cb_thur.setChecked(true);
        cb_fri = (CheckBox) findViewById(R.id.cb_fri);
        cb_fri.setChecked(true);
        cb_sat = (CheckBox) findViewById(R.id.cb_sat);
        cb_sat.setChecked(true);
        cb_sun = (CheckBox) findViewById(R.id.cb_sun);
        cb_sun.setChecked(true);
        tv_start_date = (TextView) findViewById(R.id.tv_slot_start_date);
        tv_till_date = (TextView) findViewById(R.id.tv_slot_till_date);
        tv_start_time = (TextView) findViewById(R.id.tv_slot_start_time);
        tv_start_time.setText("00:00");
        tv_stop_time = (TextView) findViewById(R.id.tv_slot_stop_time);
        tv_stop_time.setText("24:00");

        et_note_vaccation = (EditText) findViewById(R.id.et_vacation_note);

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
            stringBuilder.append("-" + month);
        } else {
            stringBuilder.append("-" + 0 + month);
        }
        stringBuilder.append("-" + year);
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
            stringBuilder.append("-" + month);
        } else {
            stringBuilder.append("-" + 0 + month);
        }
        stringBuilder.append("-" + year);
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
