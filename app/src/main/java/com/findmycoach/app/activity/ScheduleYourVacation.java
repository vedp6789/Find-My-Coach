package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.AddSlotAdapter;
import com.findmycoach.app.fragment_mentor.StartDateForVaccationSchedule;
import com.findmycoach.app.fragment_mentor.StartTimeForVaccationSchedule;
import com.findmycoach.app.fragment_mentor.StopDateForVacationSchedule;
import com.findmycoach.app.fragment_mentor.StopTimeForVaccationSchedule;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.ScrollableGridView;
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
import java.util.TreeSet;

/**
 * Created by praka_000 on 3/3/2015.
 */
public class ScheduleYourVacation extends Activity implements SetDate, SetTime {

    public static TextView tv_start_date, tv_till_date, tv_start_time, tv_stop_time;
    public boolean boo_mon_checked, boo_tue_checked,
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

    private static int start_hour = 0;
    private static int start_min = 0;
    private static int stop_hour = 0;
    private static int stop_min = 0;


    private static final String TAG = "FMC";
    private static String FOREVER;

    private Date newDate;

    public ArrayList<String> days_array = null;

    ProgressDialog progressDialog;


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

        days_array = new ArrayList<String>();

        boo_mon_checked = true;
        boo_tue_checked = true;
        boo_wed_checked = true;
        boo_thurs_checked = true;
        boo_fri_checked = true;
        boo_sat_checked = true;
        boo_sun_checked = true;

        initialize();

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


        days_array.add("M");
        days_array.add("T");
        days_array.add("W");
        days_array.add("Th");
        days_array.add("F");
        days_array.add("S");
        days_array.add("Su");


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

                        Log.d(TAG, "Vacation schedule time" + " start time : " + start_hour + ":" + start_min + ":" + "00" + " /n stop time : " + stop_hour + ":" + stop_min + ":" + "00");

                        StringBuilder stringBuilder1 = new StringBuilder();
                        stringBuilder1.append(days_array.get(0));
                        if (days_array.size() > 1) {
                            for (int i = 1; i < days_array.size(); i++) {
                                stringBuilder1.append("," + days_array.get(i));
                            }
                        }

                        if (et_note_vaccation.getText().toString().trim() != null)
                            requestParams.add("vacation_reason", et_note_vaccation.toString());
                        else
                            requestParams.add("vacation_reason", getResources().getString(R.string.schedule_vacation_note));

                        requestParams.add("name", StorageHelper.getUserDetails(ScheduleYourVacation.this, "user_id") + "_vacation_schedule");

                        /* requestParams.add("dates", stringBuilder1.toString()); */  // commented as week_Days are not needed to saved in case of Vacation schedule.
                        Log.d(TAG, "Week days selected and sent to api : " + stringBuilder1.toString());

                        String[] arr = stringBuilder1.toString().split(",");
                        for (String s : arr)
                            Log.e(TAG, s);

                        for (int i = 0; i < days_array.size(); i++) {
                            Log.d(TAG, "Day" + days_array.get(i));
                        }
                        Log.d(TAG, "days array" + String.valueOf(days_array));

                        String auth_token = StorageHelper.getUserDetails(ScheduleYourVacation.this, "auth_token");
                        progressDialog.show();

                        NetworkClient.scheduleVacation(ScheduleYourVacation.this, requestParams, auth_token, new Callback() {

                            @Override
                            public void successOperation(Object object, int statusCode, int calledApiValue) {
                                String response = (String) object;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("message").equalsIgnoreCase("success")) {
                                        Toast.makeText(ScheduleYourVacation.this, getResources().getString(R.string.vacation_scheduled_success), Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        if (jsonObject.getString("message").equalsIgnoreCase("failure")) {
                                            JSONArray jsonArray_coinciding_exceptions = jsonObject.getJSONArray("coincidingExceptions");
                                            coincidingExceptionMessage(jsonArray_coinciding_exceptions);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                progressDialog.dismiss();
                            }

                            @Override
                            public void failureOperation(Object object, int statusCode, int calledApiValue) {

                                Toast.makeText(ScheduleYourVacation.this, (String) object, Toast.LENGTH_SHORT).show();

                            }
                        }, 36);

                    }
                }
            }
        });

    }

    void coincidingExceptionMessage(JSONArray jsonArray) {

        if (jsonArray.length() > 1) {
            String s_date, st_date, s_time, st_time;

            Date start_date = null, stop_date = null;
            TreeSet<String> tset_days = new TreeSet<String>();
            TreeSet<Float> tset_s_time = new TreeSet<Float>();
            TreeSet<Float> tset_st_time = new TreeSet<Float>();
            JSONObject jO_coinciding_detail = null;
            try {
                jO_coinciding_detail = jsonArray.getJSONObject(0);
                JSONArray jA_Week_days = jO_coinciding_detail.getJSONArray("week_days");
                if (jA_Week_days.length() > 0) {

                    for (int jA_Week_day = 0; jA_Week_day < jA_Week_days.length(); jA_Week_day++) {
                        tset_days.add(jA_Week_days.getString(jA_Week_day));
                    }

                }
                s_date = jO_coinciding_detail.getString("start_date");
                st_date = jO_coinciding_detail.getString("stop_date");
                s_time = jO_coinciding_detail.getString("start_time");
                st_time = jO_coinciding_detail.getString("stop_time");

                float f_s_time = Float.parseFloat(s_time.split(":")[0] + "." + s_time.split(":")[1]);
                float f_st_time = Float.parseFloat(st_time.split(":")[0] + "." + st_time.split(":")[1]);

                tset_s_time.add(f_s_time);
                tset_st_time.add(f_st_time);


                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    start_date = dateFormat.parse(s_date);
                    stop_date = dateFormat.parse(st_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 1; i < jsonArray.length(); i++) {
                Date new_start_date, new_stop_date;
                JSONObject jO_coinciding_detail1 = null;
                try {
                    jO_coinciding_detail1 = jsonArray.getJSONObject(i);
                    JSONArray jA_Week_days = jO_coinciding_detail.getJSONArray("week_days");
                    if (jA_Week_days.length() > 0) {
                        for (int jA_Week_day = 0; jA_Week_day < jA_Week_days.length(); jA_Week_day++) {
                            tset_days.add(jA_Week_days.getString(jA_Week_day));
                        }
                    }
                    s_date = jO_coinciding_detail.getString("start_date");
                    st_date = jO_coinciding_detail.getString("stop_date");
                    s_time = jO_coinciding_detail.getString("start_time");
                    st_time = jO_coinciding_detail.getString("stop_time");

                    float f_s_time = Float.parseFloat(s_time.split(":")[0] + "." + s_time.split(":")[1]);
                    float f_st_time = Float.parseFloat(st_time.split(":")[0] + "." + st_time.split(":")[1]);

                    tset_s_time.add(f_s_time);
                    tset_st_time.add(f_st_time);


                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        new_start_date = dateFormat.parse(s_date);
                        new_stop_date = dateFormat.parse(st_date);

                        if (new_start_date.before(start_date)) {
                            start_date = new_start_date;
                        }

                        if (new_stop_date.after(stop_date)) {
                            stop_date = new_stop_date;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            StringBuilder stringBuilder = new StringBuilder();

            if (start_date != null && stop_date != null && tset_s_time.size() > 0 && tset_st_time.size() > 0) {
                float start_time = tset_s_time.first();
                float stop_time = tset_st_time.last();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());


                if (tset_days.size() > 0) {
                    ArrayList<String> days_selected = new ArrayList<String>(tset_days.size());
                    days_selected.addAll(tset_days);

                    String day = days_selected.get(0);

                    StringBuilder stringBuilder1 = new StringBuilder();


                    for (int i = 0; i < days_selected.size(); i++) {
                        String day1 = days_selected.get(i);
                        if (day1.equals("M"))
                            stringBuilder1.append("Mon");
                        if (day1.equals("T"))
                            stringBuilder1.append("Tue");
                        if (day1.equals("W"))
                            stringBuilder1.append("Wed");
                        if (day1.equals("Th"))
                            stringBuilder1.append("Thu");
                        if (day1.equals("F"))
                            stringBuilder1.append("Fri");
                        if (day1.equals("S"))
                            stringBuilder1.append("Sat");
                        if (day1.equals("Su"))
                            stringBuilder1.append("Sun");
                    }

                    stringBuilder.append("Sorry, there is already a schedule between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":") + " for " + stringBuilder1.toString() + " \n So please do changes in your schedule!");
                    showCoincidingAlertMessage(stringBuilder.toString());
                    Log.d(TAG, "Message for coinciding vaccation schedule : " + stringBuilder.toString());
                } else {
                    stringBuilder.append("Sorry, there is already a schedule between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + String.valueOf(start_time).replace(".", ":") + " to " + String.valueOf(stop_time).replace(".", ":") + " \n So please do changes in your schedule!");
                    showCoincidingAlertMessage(stringBuilder.toString());
                    Log.d(TAG, "Message for coinciding vaccation schedule : " + stringBuilder.toString());
                }

            }


        } else {
            try {
                String s_date, st_date, s_time, st_time;
                ArrayList<String> days = new ArrayList<String>();

                JSONObject jO_coinciding_detail = jsonArray.getJSONObject(0);
                JSONArray jA_Week_days = jO_coinciding_detail.getJSONArray("week_days");
                if (jA_Week_days.length() > 0) {
                    for (int jA_index = 0; jA_index < jA_Week_days.length(); jA_index++) {
                        days.add(jA_Week_days.getString(jA_index));
                    }
                }
                s_date = jO_coinciding_detail.getString("start_date");
                st_date = jO_coinciding_detail.getString("stop_date");
                s_time = jO_coinciding_detail.getString("start_time");
                st_time = jO_coinciding_detail.getString("stop_time");
                StringBuilder stringBuilder = new StringBuilder();
                Date start_date = null, stop_date = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    start_date = dateFormat.parse(s_date);
                    stop_date = dateFormat.parse(st_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                Log.d(TAG, "days : " + days);

                if (days != null) {

                    StringBuilder stringBuilder1 = new StringBuilder();
                    String day1 = days.get(0);
                    if (day1.equals("M"))
                        stringBuilder1.append("Mon");
                    if (day1.equals("T"))
                        stringBuilder1.append("Tue");
                    if (day1.equals("W"))
                        stringBuilder1.append("Wed");
                    if (day1.equals("Th"))
                        stringBuilder1.append("Thu");
                    if (day1.equals("F"))
                        stringBuilder1.append("Fri");
                    if (day1.equals("S"))
                        stringBuilder1.append("Sat");
                    if (day1.equals("Su"))
                        stringBuilder1.append("Sun");

                    for (int i = 1; i < days.size(); i++) {
                        String day = days.get(i);
                        if (day.equals("M"))
                            stringBuilder1.append(", Mon");
                        if (day.equals("T"))
                            stringBuilder1.append(", Tue");
                        if (day.equals("W"))
                            stringBuilder1.append(", Wed");
                        if (day.equals("Th"))
                            stringBuilder1.append(", Thu");
                        if (day.equals("F"))
                            stringBuilder1.append(", Fri");
                        if (day.equals("S"))
                            stringBuilder1.append(", Sat");
                        if (day.equals("Su"))
                            stringBuilder1.append(", Sun");

                    }


                    stringBuilder.append("Sorry, there is already a schedule between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0, 5) + " to " + st_time.substring(0, 5) + " for " + stringBuilder1.toString() + " \n So please do changes in your schedule!");
                    Log.d(TAG, "Message for coinciding vaccation schedule : " + stringBuilder.toString());
                    showCoincidingAlertMessage(stringBuilder.toString());

                    /*stringBuilder.append("")
                    showCoincidingAlertMessage();*/
                } else {
                    stringBuilder.append("Sorry, there is already a schedule between \n" + simpleDateFormat.format(start_date) + " & " + simpleDateFormat.format(stop_date) + " from " + s_time.substring(0, 5) + " to " + st_time.substring(0, 5) + " \n So please do changes in your schedule!");
                    Log.d(TAG, "Message for coinciding vaccation schedule : " + stringBuilder.toString());
                    showCoincidingAlertMessage(stringBuilder.toString());
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void showCoincidingAlertMessage(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Coinciding schedule")
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean validate() {
        if (days_array.size() > 0) {
            if (time_from.equals("00:00") && time_to.equals("24:00")) {
                start_hour = 0;
                start_min = 0;
                stop_hour = 24;
                stop_min = 0;

                return dateValidation();


            } else {
                int start_time = ((start_hour * 60) + start_min) * 60;
                int stop_time = ((stop_hour * 60) + stop_min) * 60;

                if (start_time > stop_time) {
                    Toast.makeText(ScheduleYourVacation.this, getResources().getString(R.string.stop_time_should_be_grater), Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    return dateValidation();
                }


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
                    Toast.makeText(ScheduleYourVacation.this, stringBuilder.toString() + " " + getResources().getString(R.string.out_of_duration), Toast.LENGTH_LONG).show();
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
        if (boo_mon_checked)
            days_checked.add("2");
        if (boo_tue_checked)
            days_checked.add("3");
        if (boo_wed_checked)
            days_checked.add("4");
        if (boo_thurs_checked)
            days_checked.add("5");
        if (boo_fri_checked)
            days_checked.add("6");
        if (boo_sat_checked)
            days_checked.add("7");
        if (boo_sun_checked)
            days_checked.add("1");

        return days_checked;

    }

    void initialize() {

        tv_start_date = (TextView) findViewById(R.id.tv_slot_start_date);
        tv_till_date = (TextView) findViewById(R.id.tv_slot_till_date);
        tv_start_time = (TextView) findViewById(R.id.tv_slot_start_time);
        tv_start_time.setText("00:00");
        start_hour = 0;
        start_min = 0;
        tv_stop_time = (TextView) findViewById(R.id.tv_slot_stop_time);
        tv_stop_time.setText("24:00");
        stop_hour = 24;
        stop_min = 0;

        et_note_vaccation = (EditText) findViewById(R.id.et_vacation_note);

        b_schedule_your_vacation = (Button) findViewById(R.id.b_schedule_vacation);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ScrollableGridView gridView = (ScrollableGridView) findViewById(R.id.calendar);
        gridView.setAdapter(new AddSlotAdapter(getResources().getStringArray(R.array.week_days_mon), this));

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.schedule_vacation));
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

        int hourOfDay = ScheduleYourVacation.start_hour % 12;
        if (ScheduleYourVacation.start_hour == 12)
            hourOfDay = 12;

        tv_start_time.setText(" " + (hourOfDay < 10 ? ("0" + hourOfDay) : hourOfDay) + ":" + start_min + (start_hour > 11 ? " PM" : " AM"));

    }

    @Override
    public void setSelectedTillTime(Object o1, Object o2) {
        String hour = (String) o1;
        ScheduleYourVacation.stop_hour = Integer.parseInt(hour);
        String minute = (String) o2;
        ScheduleYourVacation.stop_min = Integer.parseInt(minute);
        ScheduleYourVacation.time_to = hour + ":" + minute;

        int hourOfDay = ScheduleYourVacation.stop_hour % 12;
        if (ScheduleYourVacation.stop_hour == 12)
            hourOfDay = 12;

        tv_stop_time.setText(" " + (hourOfDay < 10 ? ("0" + hourOfDay) : hourOfDay) + ":" + stop_min + (stop_hour > 11 ? " PM" : " AM"));
    }

}
