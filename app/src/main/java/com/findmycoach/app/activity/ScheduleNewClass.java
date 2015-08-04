package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.AddSlotAdapter;
import com.findmycoach.app.beans.CalendarSchedule.MentorInfo;
import com.findmycoach.app.beans.CalendarSchedule.Slot;
import com.findmycoach.app.beans.CalendarSchedule.SlotDurationDetailBean;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;
import com.findmycoach.app.beans.CalendarSchedule.VacationCoincidingSlot;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Datum;
import com.findmycoach.app.beans.category.DatumSub;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.fragment_mentee.ChildDOB;
import com.findmycoach.app.util.AddressFromZip;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.ScrollableGridView;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by praka_000 on 3/4/2015.
 */
public class ScheduleNewClass extends Activity implements Button.OnClickListener, Callback {

    private LinearLayout ll_child_dob, ll_location, ll_vacation, ll_class_location_preference, ll_city, ll_pin_code, ll_tutorial_location;
    public static TextView tv_child_dob;
    private AutoCompleteTextView city;
    private CheckBox cb_location_preference;
    private static TextView tv_from_date, tv_to_date, tv_class_timing, tv_subject, tv_total_charges, tv_class_of_type, tv_tutorial_location, tv_asterisk_location;
    Spinner sp_subjects, sp_mentor_for;
    EditText et_tutorial_location, et_pin_code;    /* this will be used to show mentee's location to mentee if mentee*/
    RadioButton rb_pay_now, rb_pay_personally;
    private static Button b_payment;
    private final String TAG = "FMC";
    private String selected_mentor_for;
    RequestParams requestParams;
    private ProgressDialog progressDialog;
    public static String child_DOB = null;
    Bundle bundle;
    private String selected_subject = null;
    private Slot slot;
    private MentorInfo mentorInfo;
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
    private List<Vacation> vacations_on_the_slot;
    private ArrayList<String> arrayList_subcategory = null;
    private String slot_type;
    private String[] slot_on_week_days;
    private String charges;
    private ArrayList<SlotDurationDetailBean> slotDurationDetailBeans;
    private ArrayList<VacationCoincidingSlot> vacationCoincidingSlots;
    private ScrollableGridView gridView;
    private TextView title;
    private TextView tv_number_of_classes, tv_vacation;
    private int class_days_after_reducing_vacation = 0; /* No of days which will be possible for class i.e. if vacation found then these are coming from reducing vacations */
    List<DurationOfSuccessfulClassDays> durationOfSuccessfulClassDayses = new ArrayList<DurationOfSuccessfulClassDays>();
    private boolean class_not_possible;
    private ImageButton ib_info, ib_mentor_availability_info;
    private String tutorial_location;
    private String city_selected_by_mentee = null;
    ArrayAdapter<String> arrayAdapter;
    private boolean isGettingAddress;
    private List<Prediction> predictions;
    public static int FLAG_FOR_SCHEDULE_NEW_CLASS=-7;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(SetScheduleActivity.setScheduleActivity != null){
            SetScheduleActivity.setScheduleActivity.makeOnEventClickEnable();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_new_class);
        try {
            bundle = getIntent().getBundleExtra("slot_bundle");
            slot = (Slot) bundle.getParcelable("slot");
            mentorInfo = (MentorInfo) bundle.getParcelable("mentor_info");
            mentor_id = bundle.getString("mentor_id");
            mentor_availability = bundle.getString("mentor_availability");
            charges = bundle.getString("charges");
            arrayList_subcategory = bundle.getStringArrayList("arrayList_sub_category");

            slot_start_day = Integer.parseInt(slot.getSlot_start_date().split("-")[2]);
            slot_start_month = Integer.parseInt(slot.getSlot_start_date().split("-")[1]);
            slot_start_year = Integer.parseInt(slot.getSlot_start_date().split("-")[0]);

            slot_stop_day = Integer.parseInt(slot.getSlot_stop_date().split("-")[2]);
            slot_stop_month = Integer.parseInt(slot.getSlot_stop_date().split("-")[1]);
            slot_stop_year = Integer.parseInt(slot.getSlot_stop_date().split("-")[0]);

            slot_start_hour = Integer.parseInt(slot.getSlot_start_time().split(":")[0]);
            slot_start_minute = Integer.parseInt(slot.getSlot_start_time().split(":")[1]);

            slot_stop_hour = Integer.parseInt(slot.getSlot_stop_time().split(":")[0]);
            slot_stop_minute = Integer.parseInt(slot.getSlot_stop_time().split(":")[1]);

            slot_type = slot.getSlot_type();
            slot_on_week_days = slot.getSlot_week_days();

            vacations_on_the_slot = new ArrayList<Vacation>();
            vacations_on_the_slot = slot.getVacations();
            Log.d(TAG, "vacations for this slot: " + slot.getVacations().size());


            durationOfSuccessfulClassDayses = new ArrayList<DurationOfSuccessfulClassDays>();


            progressDialog = new ProgressDialog(ScheduleNewClass.this);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            applyActionbarProperties(mentorInfo.getFirst_name());
            initialize();

            if (vacations_on_the_slot.size() > 0) {
                ib_info.setVisibility(View.VISIBLE);     /* ib_info is an option, from which mentee can knew about vacation coming on the slot. */
            } else {
                ib_info.setVisibility(View.GONE);
            }

            finalizeDateTimeAndCharges();
            populateFields();
            isGettingAddress = false;

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void finalizeDateTimeAndCharges() {


        /*
        * IN CASE OF GROUP SLOT_TYPE, THERE IS NO NEED TO TAKE ADDRESS FROM USERS AS IT IS ASSUMED THAT GROUP CLASS WILL BE SCHEDULED AT MENTOR'S ADDRESS
        *
        *
        *
        * */

        Log.d(TAG, "slot_type : " + slot_type);

        String slot_type = slot.getSlot_type();
        if (slot_type.equalsIgnoreCase("group")) {
            tv_class_of_type.setText(getResources().getString(R.string.group));
        } else {
            if (slot_type.equalsIgnoreCase("individual")) {
                tv_class_of_type.setText(getResources().getString(R.string.individual));
            }
        }


        if (slot_type.equalsIgnoreCase(getResources().getString(R.string.group))) {
            /** Till now if slot is of group type, i am assuming that location will be as per defined by mentor*/
            tutorial_location = slot.getTutorial_location();

            tv_tutorial_location.setText(tutorial_location);

        } else {
            if (mentor_availability != null && mentor_availability.equals("1")) {
                ll_class_location_preference.setVisibility(View.VISIBLE);
                cb_location_preference.setChecked(true);
                tutorial_location = slot.getTutorial_location();
                tv_tutorial_location.setText(tutorial_location);
            } else {
                if (mentor_availability != null) {
                    tutorial_location = slot.getTutorial_location();
                    tv_tutorial_location.setText(tutorial_location);
                } else {
                    Log.d(TAG, "mentor_availability null found");
                }
            }
        }


        cb_location_preference.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    tutorial_location = "";
                    ll_tutorial_location.setVisibility(View.GONE);
                    tv_asterisk_location.setVisibility(View.VISIBLE);
                    et_tutorial_location.setVisibility(View.VISIBLE);
                    ll_city.setVisibility(View.VISIBLE);
                    ll_pin_code.setVisibility(View.VISIBLE);

                    Log.d(TAG, "address get saved: " + StorageHelper.addressInformation(ScheduleNewClass.this, "user_local_address"));
                    Log.d(TAG, "city get saved: " + StorageHelper.addressInformation(ScheduleNewClass.this, "user_city_state"));
                    Log.d(TAG, "pin code get saved: " + StorageHelper.addressInformation(ScheduleNewClass.this, "user_pin_code"));


                    if (StorageHelper.addressInformation(ScheduleNewClass.this, "user_local_address") != null) {
                        et_tutorial_location.setText(StorageHelper.addressInformation(ScheduleNewClass.this, "user_local_address"));
                        city.setText(StorageHelper.addressInformation(ScheduleNewClass.this, "user_city_state"));
                        et_pin_code.setText(StorageHelper.addressInformation(ScheduleNewClass.this, "user_zip_code"));

                        StringBuilder stringBuilder_tutorial_location = new StringBuilder();
                        stringBuilder_tutorial_location.append(et_tutorial_location.getText().toString().trim());
                        stringBuilder_tutorial_location.append(", " + city.getText().toString().trim());
                        stringBuilder_tutorial_location.append(", " + et_pin_code.getText().toString().trim());

                        tutorial_location = stringBuilder_tutorial_location.toString();


                    } else {
                        Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.prompt_update_profile), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    tutorial_location = "";
                    ll_tutorial_location.setVisibility(View.VISIBLE);
                    tv_asterisk_location.setVisibility(View.GONE);
                    et_tutorial_location.setVisibility(View.GONE);
                    ll_city.setVisibility(View.GONE);
                    ll_pin_code.setVisibility(View.GONE);

                    tutorial_location = slot.getTutorial_location();
                    tv_tutorial_location.setText(tutorial_location);

                }

            }
        });


        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                city_selected_by_mentee = null;
                String input = city.getText().toString();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                }
            }
        });

        city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city_selected_by_mentee = arrayAdapter.getItem(position);
//                getPostalFromCity(city);
                try {
                    NetworkManager.countryName = predictions.get(position).getCountry();
                    isGettingAddress = true;
                } catch (Exception e) {
                    NetworkManager.countryName = "";
                }
            }
        });


        et_pin_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)
                        || actionId == EditorInfo.IME_ACTION_NEXT) {
                    try {
                        new AddressFromZip(ScheduleNewClass.this,ScheduleNewClass.this, city, et_tutorial_location, true,FLAG_FOR_SCHEDULE_NEW_CLASS).execute(et_pin_code.getText().toString());
                        isGettingAddress = true;
                    } catch (Exception ignored) {
                    }
                }
                return false;
            }
        });


        et_tutorial_location.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    city.setFocusable(true);
                }
                return false;
            }
        });

        city.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    et_pin_code.setFocusable(true);
                }
                return false;
            }
        });




/*
        if (slot_type.equalsIgnoreCase(getResources().getString(R.string.group))) {
            ll_location.setVisibility(View.GONE);
        } else {
            if (mentor_availability != null && mentor_availability.equals("1")) {
                String tutorial_location = StorageHelper.addressInformation(ScheduleNewClass.this, "training_location");
                if (tutorial_location.trim().length() <= 0) {
                    if (!DashboardActivity.dashboardActivity.userCurrentAddress.equals("")) {
                        et_location.setText(DashboardActivity.dashboardActivity.userCurrentAddress);
                    }
                } else {
                    et_location.setText(tutorial_location);
                }
            } else {
                ll_location.setVisibility(View.GONE);
            }
        }*/


        Log.d(TAG, "subject: " + slot.getSlot_subject());

        if (slot.getSlot_subject() != null) {
            tv_subject.setVisibility(View.VISIBLE);
            selected_subject = slot.getSlot_subject();
            tv_subject.setText(selected_subject);
            sp_subjects.setVisibility(View.GONE);
        }


        String hr_24_start_time = String.format("%02d:%02d", slot_start_hour, slot_start_minute);
        String hr_24_stop_time = String.format("%02d:%02d", slot_stop_hour, slot_stop_minute);
        String hr_12_start = getTime(hr_24_start_time);
        String hr_12_stop = getTime(hr_24_stop_time);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(hr_12_start + " to " + hr_12_stop);
        String timing = String.format("%02d:%02d to %02d:%02d", slot_start_hour, slot_start_minute, slot_stop_hour, slot_stop_minute);
        tv_class_timing.setText(stringBuilder.toString());

        ArrayList<String> week_days_as_per_calendar_class = new ArrayList<String>();

        List<Integer> selectedDays = new ArrayList<>();
        for (int slot_week_day = 0; slot_week_day < slot_on_week_days.length; slot_week_day++) {
            String day = slot_on_week_days[slot_week_day];
            if (day.equals("M")) {
                selectedDays.add(0);
                week_days_as_per_calendar_class.add("M");
            }
            if (day.equals("T")) {
                selectedDays.add(1);
                week_days_as_per_calendar_class.add("T");

            }
            if (day.equals("W")) {
                selectedDays.add(2);
                week_days_as_per_calendar_class.add("W");

            }
            if (day.equals("Th")) {
                selectedDays.add(3);
                week_days_as_per_calendar_class.add("Th");

            }
            if (day.equals("F")) {
                selectedDays.add(4);
                week_days_as_per_calendar_class.add("F");

            }
            if (day.equals("S")) {
                selectedDays.add(5);
                week_days_as_per_calendar_class.add("S");

            }
            if (day.equals("Su")) {
                selectedDays.add(6);
                week_days_as_per_calendar_class.add("Su");
            }
        }



        Log.d(TAG, "mentor availability : " + bundle.getString("mentor_availability"));


        Calendar cal = Calendar.getInstance();
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

        String class_schedule_start_date = tv_from_date.getText().toString();   /* Date which is getting prompted to student, from where student class schedule starts */
        int class_schedule_start_day = Integer.parseInt(class_schedule_start_date.split("-", 3)[0]);
        int class_schedule_start_month = Integer.parseInt(class_schedule_start_date.split("-", 3)[1]);
        int class_schedule_start_year = Integer.parseInt(class_schedule_start_date.split("-", 3)[2]);


        Calendar calendar_schedule_start_date = Calendar.getInstance();    /* Possible start date of this class */
        calendar_schedule_start_date.set(class_schedule_start_year, class_schedule_start_month - 1, class_schedule_start_day);
        long start_date_of_this_class_millis = calendar_schedule_start_date.getTimeInMillis();
        Log.d(TAG, "schedule start date and millis: " + class_schedule_start_year + "/" + class_schedule_start_month + "/" + class_schedule_start_day + " millis:" + start_date_of_this_class_millis);
        Log.d(TAG, "schedule start date in millis " + start_date_of_this_class_millis);

        Calendar calendar_stop_date_of_schedule = Calendar.getInstance();
        calendar_stop_date_of_schedule.set(slot_stop_year, slot_stop_month - 1, slot_stop_day);
        long stop_date_of_this_class_in_millis = calendar_stop_date_of_schedule.getTimeInMillis();
        Log.d(TAG, "schedule stop date in millis " + stop_date_of_this_class_in_millis);
        Log.d(TAG, "schedule stop date and millis: " + slot_stop_year + "/" + slot_stop_month + "/" + slot_stop_day + " millis: " + stop_date_of_this_class_in_millis);

        String week_days[] = new String[week_days_as_per_calendar_class.size()];
        for (int i = 0; i < week_days_as_per_calendar_class.size(); i++) {
            week_days[i] = week_days_as_per_calendar_class.get(i);
        }

        for(String i:week_days){
            Log.d(TAG,"week dya: "+i);
        }

        List<Integer> week_day_list_as_per_duration= new ArrayList<Integer>();
        Calendar calendar_start= (Calendar) calendar_schedule_start_date.clone();
        Calendar calendar_stop = (Calendar) calendar_stop_date_of_schedule.clone();
        ArrayList<SlotDurationDetailBean> slotDurationDetailBeans1 = slot.calculateNoOfTotalClassDays(calendar_start, calendar_stop, week_days);
        if(slotDurationDetailBeans1.size() < 7){
            Log.d(TAG,"week_days size: "+slotDurationDetailBeans1.size());
            for(int i =0 ; i < slotDurationDetailBeans1.size(); i++){
                 SlotDurationDetailBean slotDurationDetailBean=slotDurationDetailBeans1.get(i);
                 int i1=Integer.parseInt(slotDurationDetailBean.getWeek_day());
                 week_day_list_as_per_duration.add(i1);
                 /*boolean match_not_found=true;
                 for(int u=0 ; u < week_days_as_per_calendar_class.size(); u++){
                     int day=week_days_as_per_calendar_class.get(u);
                     if(day == i1){
                         match_not_found =false;
                         break;
                     }
                 }
                 if(match_not_found){
                     int index = week_days_as_per_calendar_class.indexOf(i1);
                     week_days_as_per_calendar_class.remove(index);
                 }*/

            }
            selectedDays = new ArrayList<>();
            for(int s=0; s < week_day_list_as_per_duration.size();s++){
                int i = week_day_list_as_per_duration.get(s);
                switch (i){
                    case 1:
                        selectedDays.add(6);
                        break;
                    case 2:
                        selectedDays.add(0);
                        break;
                    case 3:
                        selectedDays.add(1);
                        break;
                    case 4:
                        selectedDays.add(2);
                        break;
                    case 5:
                        selectedDays.add(3);
                        break;
                    case 6:
                        selectedDays.add(4);
                        break;
                    case 7:
                        selectedDays.add(5);
                        break;
                }
            }
            gridView.setAdapter(new AddSlotAdapter(getResources().getStringArray(R.array.week_days_mon), selectedDays, this));


        }else{
            gridView.setAdapter(new AddSlotAdapter(getResources().getStringArray(R.array.week_days_mon), selectedDays, this));

        }




        Calendar calendar_temp_start_date = Calendar.getInstance();
        calendar_temp_start_date = (Calendar) calendar_schedule_start_date.clone();
        Log.d(TAG, "schedule start temp clone date and millis: " + calendar_temp_start_date.get(Calendar.YEAR) + "/" + calendar_temp_start_date.get(Calendar.MONTH) + "/" + calendar_temp_start_date.get(Calendar.DAY_OF_MONTH) + " millis: " + start_date_of_this_class_millis);

        Log.d(TAG, "schedule start date " + calendar_schedule_start_date.get(Calendar.YEAR) + "/" + calendar_schedule_start_date.get(Calendar.MONTH) + 1 + "/" + calendar_schedule_start_date.get(Calendar.DAY_OF_MONTH));


/*********************************************** Below two instances are just used to count no of possible class within class start and class end**************************************************************/
        Calendar calendar_schedule_start_date2 = Calendar.getInstance();    /* Possible start date of this class */
        calendar_schedule_start_date2 = (Calendar) calendar_schedule_start_date.clone();


        Calendar calendar_stop_date_of_schedule2 = Calendar.getInstance();
        calendar_stop_date_of_schedule2 = (Calendar) calendar_stop_date_of_schedule.clone();
/*************************************************************************************************************************************************************************************************************/

        int no_of_possible_classes_without_considering_vacation = new Slot().calculateNoOfTotalClassDays(calendar_schedule_start_date2, calendar_stop_date_of_schedule2, slot_on_week_days).size();
        Log.d(TAG, "no of class without considering vacation: " + no_of_possible_classes_without_considering_vacation);

        if (no_of_possible_classes_without_considering_vacation > 0) {

            if (vacations_on_the_slot.size() > 0) {
                Log.d(TAG, "parsing slots from vacations size: " + vacations_on_the_slot.size());

                for (int vacation_no = 0; vacation_no < vacations_on_the_slot.size(); vacation_no++) {

                    if (calendar_temp_start_date.getTimeInMillis() <= stop_date_of_this_class_in_millis) {

                        Vacation vacation = vacations_on_the_slot.get(vacation_no);
                        String start_date = vacation.getStart_date();
                        String stop_date = vacation.getStop_date();

                        Calendar calendar_vacation_start = Calendar.getInstance();
                        calendar_vacation_start.set(Integer.parseInt(start_date.split("-")[0]), Integer.parseInt(start_date.split("-")[1]) - 1, Integer.parseInt(start_date.split("-")[2]));
                        long vacation_start_in_millis = calendar_vacation_start.getTimeInMillis();

                        Calendar calendar_vacation_stop = Calendar.getInstance();
                        calendar_vacation_stop.set(Integer.parseInt(stop_date.split("-")[0]), Integer.parseInt(stop_date.split("-")[1]) - 1, Integer.parseInt(stop_date.split("-")[2]));
                        long vacation_stop_in_millis = calendar_vacation_stop.getTimeInMillis();

                        if (vacation_start_in_millis > calendar_temp_start_date.getTimeInMillis()) {    /* This is the case when a vacation start date is ahead of temp_slot_start_date*/

                            Calendar calendar_temp_stop_date = Calendar.getInstance();
                            calendar_temp_stop_date = (Calendar) calendar_vacation_start.clone();
                            calendar_temp_stop_date.add(Calendar.DATE, -1);

                            Calendar calendar_temp_start_date2 = Calendar.getInstance();   /* This instance of temp start date will check whether any valid no of day coming between start and stop after parsing vacation */
                            calendar_temp_start_date2 = (Calendar) calendar_temp_start_date.clone();


                            int days = new Slot().calculateNoOfTotalClassDays(calendar_temp_start_date2, calendar_temp_stop_date, slot_on_week_days).size();
                            if (days > 0) {
                                class_days_after_reducing_vacation += days;
                                DurationOfSuccessfulClassDays durationOfSuccessfulClassDays = new DurationOfSuccessfulClassDays();
                                String temp_start_date = String.format("%d-%02d-%02d", calendar_temp_start_date.get(Calendar.YEAR), calendar_temp_start_date.get(Calendar.MONTH) + 1, calendar_temp_start_date.get(Calendar.DAY_OF_MONTH));
                                String temp_stop_date = String.format("%d-%02d-%02d", calendar_temp_stop_date.get(Calendar.YEAR), calendar_temp_stop_date.get(Calendar.MONTH) + 1, calendar_temp_stop_date.get(Calendar.DAY_OF_MONTH));
                                durationOfSuccessfulClassDays.setStart_date(temp_start_date);
                                durationOfSuccessfulClassDays.setStop_date(temp_stop_date);
                                durationOfSuccessfulClassDayses.add(durationOfSuccessfulClassDays);
                            }
                            calendar_temp_start_date = (Calendar) calendar_vacation_stop.clone();
                            calendar_temp_start_date.add(Calendar.DATE, 1);    /* Once checked for the span of slot time for availble class days, again initiating temp start date of slot to day next to vacation completion date */

                        } else {    /*  Start date of slot is found either equal to Vacation start of found inbetween of vacation, so in case we have to make next day of vacation completion day as temp start date of slot */
                            calendar_temp_start_date = (Calendar) calendar_vacation_stop.clone();
                            calendar_temp_start_date.add(Calendar.DATE, 1);
                        }
                    } else {
                        break;
                    }

                }
                /* after the completion of for loop it means all vacation are traversed, and after this if calendar_temp_start_date is behind stop date of calendar_stop_date_schedule then we have to check for this period of time for possible classes*/
                if (calendar_temp_start_date.getTimeInMillis() < stop_date_of_this_class_in_millis) {

                    Calendar calendar_temp_start_date3 = (Calendar) calendar_temp_start_date.clone();
                    int days = new Slot().calculateNoOfTotalClassDays(calendar_temp_start_date3, calendar_stop_date_of_schedule, slot_on_week_days).size();
                    if (days > 0) {
                        class_days_after_reducing_vacation += days;
                        DurationOfSuccessfulClassDays durationOfSuccessfulClassDays = new DurationOfSuccessfulClassDays();
                        String temp_start_date = String.format("%d-%02d-%02d", calendar_temp_start_date.get(Calendar.YEAR), calendar_temp_start_date.get(Calendar.MONTH) + 1, calendar_temp_start_date.get(Calendar.DAY_OF_MONTH));
                        String temp_stop_date = String.format("%d-%02d-%02d", calendar_stop_date_of_schedule.get(Calendar.YEAR), calendar_stop_date_of_schedule.get(Calendar.MONTH) + 1, calendar_stop_date_of_schedule.get(Calendar.DAY_OF_MONTH));
                        durationOfSuccessfulClassDays.setStart_date(temp_start_date);
                        durationOfSuccessfulClassDays.setStop_date(temp_stop_date);
                        durationOfSuccessfulClassDayses.add(durationOfSuccessfulClassDays);
                    }
                }


            } else {
                /* No vacation found */

                Calendar calendar_schedule_start_date3 = Calendar.getInstance();    /* Possible start date of this class */
                calendar_schedule_start_date3.set(class_schedule_start_year, class_schedule_start_month - 1, class_schedule_start_day);
                long start_date_of_this_class_millis3 = calendar_schedule_start_date3.getTimeInMillis();


                if (no_of_possible_classes_without_considering_vacation > 0) {
                    class_days_after_reducing_vacation += no_of_possible_classes_without_considering_vacation;   /* In case of no vacations found on one slot */
                    Log.d(TAG, "class days after checking vacation when no vacation found : " + class_days_after_reducing_vacation);
                    Log.d(TAG, "class days after checking vacation: " + class_days_after_reducing_vacation);
                    DurationOfSuccessfulClassDays durationOfSuccessfulClassDays = new DurationOfSuccessfulClassDays();
                    String temp_start_date = String.format("%d-%02d-%02d", calendar_schedule_start_date3.get(Calendar.YEAR), calendar_schedule_start_date3.get(Calendar.MONTH) + 1, calendar_schedule_start_date3.get(Calendar.DAY_OF_MONTH));
                    Log.d(TAG, "start date: " + temp_start_date);
                    String temp_stop_date = String.format("%d-%02d-%02d", calendar_stop_date_of_schedule.get(Calendar.YEAR), calendar_stop_date_of_schedule.get(Calendar.MONTH) + 1, calendar_stop_date_of_schedule.get(Calendar.DAY_OF_MONTH));
                    Log.d(TAG, "stop date: " + temp_stop_date);
                    durationOfSuccessfulClassDays.setStart_date(temp_start_date);
                    durationOfSuccessfulClassDays.setStop_date(temp_stop_date);
                    durationOfSuccessfulClassDayses.add(durationOfSuccessfulClassDays);
                }
            }

        } else {
            /* No class can be possible to schedule */
            Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.no_class_possible), Toast.LENGTH_SHORT).show();

        }


        for (int i = 0; i < durationOfSuccessfulClassDayses.size(); i++) {
            DurationOfSuccessfulClassDays durationOfSuccessfulClassDays = durationOfSuccessfulClassDayses.get(i);
            Log.d(TAG, " Class start_date: " + durationOfSuccessfulClassDays.getStart_date() + " Class stop date: " + durationOfSuccessfulClassDays.getStop_date());
        }


        if (class_days_after_reducing_vacation > 0) {
            if (class_days_after_reducing_vacation > 1) {
                tv_number_of_classes.setText("" + class_days_after_reducing_vacation + "\t" + getResources().getString(R.string.class_days));
            } else {
                tv_number_of_classes.setText("" + class_days_after_reducing_vacation + "\t" + getResources().getString(R.string.class_day));
            }


            int total_amount = 0;
            int cost = Integer.parseInt(charges.split(" per ", 2)[0]);
            String cost_basis = charges.split(" per ", 2)[1];
            Log.d(TAG, "cost: " + cost + "cost basis: " + cost_basis);

            if (cost_basis.equalsIgnoreCase("hour")) {               /* assuming mentor cost per hour */

                /* one day cost */

                int start_time_in_seconds = ((slot_start_hour * 60) * 60) + (slot_start_minute * 60);
                int stop_time_in_seconds = ((slot_stop_hour * 60) * 60) + (slot_stop_minute * 60);

                double time_duration_in_day = stop_time_in_seconds - start_time_in_seconds;  /* One day class durations in seconds*/

                int one_hour_seconds = 60 * 60;

                double one_day_amount = (time_duration_in_day / one_hour_seconds) * cost;
                Log.d(TAG, "one day amount: " + one_day_amount);

                double total_class_duration_amount = class_days_after_reducing_vacation * one_day_amount;

                Log.d(TAG, "total_amount:" + total_class_duration_amount);


                try {
                    String currency = StorageHelper.getCurrency(this);
                    if (currency.equals(""))
                        tv_total_charges.setText(String.valueOf(total_class_duration_amount));
                    else
                        tv_total_charges.setText(Html.fromHtml(currency + " " + String.valueOf(total_class_duration_amount)));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                /*class_days_after_reducing_vacation = slotDurationDetailBeans.size();
                int no_of_hours_in_a_day = slot_stop_hour - slot_start_hour;
                int no_of_total_hours = class_days_after_reducing_vacation * no_of_hours_in_a_day;
                total_amount = no_of_total_hours * cost;
*/
            } else {
             /*this will not allowed now as there is only cost_basis that is per hour*/
            }


        } else {
            class_not_possible = true;
            Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.no_class_possible),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", getResources().getString(R.string.google_location_api_key));
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.autoComplete(getApplicationContext(), requestParams, this, 32);
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


    private int calculateNoOfTotalClassDays(Calendar calendar_schedule_start_date,
                                            Calendar calendar_stop_date_of_schedule, String[] slot_on_week_days) {

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
            calendar_schedule_start_date.add(Calendar.DATE, 1);


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
        ll_tutorial_location = (LinearLayout) findViewById(R.id.ll_tutorial_location);
        ll_class_location_preference = (LinearLayout) findViewById(R.id.ll_class_location_preference);
        ll_city = (LinearLayout) findViewById(R.id.ll_city);
        ll_pin_code = (LinearLayout) findViewById(R.id.ll_pin_code);
        city = (AutoCompleteTextView) findViewById(R.id.city_selection);
        city.setOnFocusChangeListener(onFocusChangeListener);
        city.setOnTouchListener(onTouchListener);

        tv_asterisk_location = (TextView) findViewById(R.id.tv_asterisk_location);
        tv_tutorial_location = (TextView) findViewById(R.id.tv_tutorial_location);
        et_tutorial_location = (EditText) findViewById(R.id.et_tutorial_location);
        et_pin_code = (EditText) findViewById(R.id.et_input_pin);
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
        tv_class_of_type = (TextView) findViewById(R.id.tv_class_of_type);
        ib_mentor_availability_info = (ImageButton) findViewById(R.id.ib_mentor_availability_info);
        ib_mentor_availability_info.setOnClickListener(this);
        ll_child_dob = (LinearLayout) findViewById(R.id.ll_child_dob);
        ll_location = (LinearLayout) findViewById(R.id.ll_location);
        sp_mentor_for = (Spinner) findViewById(R.id.sp_mentor_for);
        tv_number_of_classes = (TextView) findViewById(R.id.tv_number_of_classes);
        ll_vacation = (LinearLayout) findViewById(R.id.ll_vacations);
        tv_vacation = (TextView) findViewById(R.id.tv_vacations);
        ib_info = (ImageButton) findViewById(R.id.ib_info);
        ib_info.setOnClickListener(this);
        gridView = (ScrollableGridView) findViewById(R.id.calendar);
        rb_pay_now = (RadioButton) findViewById(R.id.rb_pay_now);
        rb_pay_personally = (RadioButton) findViewById(R.id.pay_personally);
        cb_location_preference = (CheckBox) findViewById(R.id.cb_location_preference);
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


    /**
     * Clear error from edit text when focused or on touch
     */
    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                ((TextView) v).setError(null);
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ((TextView) v).setError(null);
            return false;
        }
    };


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

                        b_payment.setEnabled(false);
                        progressDialog.show();
                        NetworkClient.postScheduleRequest(ScheduleNewClass.this,
                                getRequestParamsRelatedToThisClass(), ScheduleNewClass.this, 46);
                        // Log.d(TAG,"id : "+slot_id.toString()+"student_id"+student_id+"mentor_id:
                        // "+mentor_id+" start_date : "+from_date.split("-",3)[2]+"-"+
                        // from_date.split("-",3)[1]+"-"+from_date.split("-",3)[0]+"slot_type :
                        // "+slot_type+" sub_category_id : "+sub_category_id+" total_price : "+
                        // tv_total_charges.getText().toString()+"date_of_birth_kid"+
                        // tv_child_dob.getText().toString().split("-",3)[2]+"-"+
                        // tv_child_dob.getText().toString().split("-",3)[1]+"-"+
                        // tv_child_dob.getText().toString().split("-",3)[0]);

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


    private void showAlertMessageForCoincidingVacation() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.vacations));
        ScrollView scrollView = new ScrollView(this);


        StringBuilder stringBuilder = new StringBuilder();
        for (int vacation_no = 0; vacation_no < vacations_on_the_slot.size(); vacation_no++) {
            Vacation vacation = vacations_on_the_slot.get(vacation_no);
            int number = vacation_no + 1;
            if (vacation_no == 0)
                stringBuilder.append("Vacation " + number + ": " +
                        String.format("%02d-%02d-%d - %02d-%02d-%d",
                                Integer.parseInt(vacation.getStart_date().split("-")[2]),
                                Integer.parseInt(vacation.getStart_date().split("-")[1]),
                                Integer.parseInt(vacation.getStart_date().split("-")[0]),
                                Integer.parseInt(vacation.getStop_date().split("-")[2]),
                                Integer.parseInt(vacation.getStop_date().split("-")[1]),
                                Integer.parseInt(vacation.getStop_date().split("-")[0])));
            else
                stringBuilder.append("\nVacation " + number + ": " +
                        String.format("%02d-%02d-%d - %02d-%02d-%d",
                                Integer.parseInt(vacation.getStart_date().split("-")[2]),
                                Integer.parseInt(vacation.getStart_date().split("-")[1]),
                                Integer.parseInt(vacation.getStart_date().split("-")[0]),
                                Integer.parseInt(vacation.getStop_date().split("-")[2]),
                                Integer.parseInt(vacation.getStop_date().split("-")[1]),
                                Integer.parseInt(vacation.getStop_date().split("-")[0])));

        }

        final TextView contentView = new TextView(this);
        contentView.setText(stringBuilder.toString());


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(25, 25, 25, 25);
        scrollView.addView(contentView);
        scrollView.setLayoutParams(params);
        alertDialog.setView(scrollView);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );

        alertDialog.show();
    }


    private RequestParams getRequestParamsRelatedToThisClass() {
        RequestParams requestParams1 = new RequestParams();
        requestParams1.add("id", slot.getSlot_id().toString());
        requestParams1.add("mentor_id", mentor_id);
        String student_id = StorageHelper.getUserDetails(ScheduleNewClass.this, "user_id");
        Log.d(TAG, "student_id what getting sent to server : " + student_id);
        requestParams1.add("student_id", student_id);

        JSONArray jsonArray = new JSONArray();
        for (int duration_number = 0; duration_number < durationOfSuccessfulClassDayses.size();
             duration_number++) {
            DurationOfSuccessfulClassDays durationOfSuccessfulClassDays =
                    durationOfSuccessfulClassDayses.get(duration_number);
            JSONArray jsonArray1 = new JSONArray();
            jsonArray1.put(durationOfSuccessfulClassDays.getStart_date());
            jsonArray1.put(durationOfSuccessfulClassDays.getStop_date());
            jsonArray.put(jsonArray1);


        }


        String from_date = tv_from_date.getText().toString();
        requestParams1.add("dates", jsonArray.toString());
        Log.d(TAG, "event duration dates string: " + jsonArray.toString());


        requestParams1.add("slot_type", slot_type);
        requestParams1.add("location", tutorial_location);
        Log.d(TAG, "location for this class save as : " + tutorial_location);

        /*
        if (mentor_availability.equals("1") && slot_type.equalsIgnoreCase("individual")) {
            requestParams1.add("location", tutorial_location);
        }*/
        Log.d(TAG, "sub category id: " + getSubCategoryId());
        requestParams1.add("sub_category_id", getSubCategoryId());

        if (selected_mentor_for.equalsIgnoreCase("child")) {
            String date_of_birth_kid = tv_child_dob.getText().toString().split("-", 3)[2] + "-" +
                    tv_child_dob.getText().toString().split("-", 3)[1] + "-" +
                    tv_child_dob.getText().toString().split("-", 3)[0];
            requestParams1.add("date_of_birth_kid", date_of_birth_kid);
        }
        requestParams1.add("total_price", tv_total_charges.getText().toString());
        return requestParams1;
    }

    private String getSubCategoryId() {
        String sub_category_id = "";
        Category category = new Gson().fromJson(DataBase.singleton(this).getAll(), Category.class);
        parentForLoop:
        for (Datum d : category.getData()) {

            for (DatumSub datumSub : d.getSubCategories()) {
                if (datumSub.getName().trim().equalsIgnoreCase(selected_subject.trim())) {
                    sub_category_id = datumSub.getId();
                    break parentForLoop;
                }
            }

            for (Datum datum : d.getCategories()) {
                if (!sub_category_id.equals(""))
                    break parentForLoop;

                for (DatumSub datumSub : datum.getSubCategories()) {
                    if (datumSub.getName().trim().equalsIgnoreCase(selected_subject.trim())) {
                        sub_category_id = datumSub.getId();
                        break parentForLoop;
                    }
                }
            }
        }
        Log.e(TAG, sub_category_id + " : from get subCategoryId()");
        return sub_category_id;
    }


    private void applyActionbarProperties(String name) {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_proceed_to_payment:
                if (class_not_possible) {
                    Toast.makeText(ScheduleNewClass.this,
                            getResources().getString(R.string.no_days_free_to_schedule),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (rb_pay_now.isChecked()) {
                        if (validate()) {
                            Toast.makeText(ScheduleNewClass.this,
                                    "Waiting for payment gateway integration. ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (rb_pay_personally.isChecked()) {

                        if (validate()) {

                            showAlertMessageOnPayPersonally();
                        }
                    }
                }
                break;

            case R.id.tv_child_dob:
                FragmentManager fragmentManager2 = getFragmentManager();
                ChildDOB childDOB = new ChildDOB();
                childDOB.scheduleNewClass = ScheduleNewClass.this;
                childDOB.show(fragmentManager2, null);
                break;

            case R.id.ib_info:
                showAlertMessageForCoincidingVacation();
                break;

            case R.id.ib_mentor_availability_info:
                if (mentor_availability.equals("1")) {
                    Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.mentor_availability_message), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScheduleNewClass.this, getResources().getString(R.string.mentor_non_availability_message), Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private boolean validate() {

        if (mentor_availability.equals("1")) {
            if (!slot_type.equalsIgnoreCase(getResources().getString(R.string.group))) {
                if (!cb_location_preference.isChecked()) {
                    if (tutorial_location.trim().length() <= 0) {
                        Toast.makeText(this, getResources().getString(R.string.address_missing), Toast.LENGTH_SHORT).show();
                        showErrorMessage(et_tutorial_location, getResources().getString(R.string.your_address_please));
                        showErrorMessage(city, getResources().getString(R.string.choose_suggested_city));
                        showErrorMessage(et_pin_code, getResources().getString(R.string.enter_pin));
                        return false;

                    }
                }

            }

        }
        if (selected_mentor_for.equalsIgnoreCase("child")) {
            if (tv_child_dob.getText().toString().trim().length() <= 0) {
                Toast.makeText(this, getResources().getString(R.string.child_date_of_birth_please), Toast.LENGTH_SHORT).show();
                showErrorMessage(tv_child_dob, getResources().getString(R.string.child_date_of_birth_please));
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

    private void updateAutoSuggestion(Suggestion suggestion) {
        ArrayList<String> list = new ArrayList<String>();
        predictions = suggestion.getPredictions();
        for (int index = 0; index < predictions.size(); index++) {
            list.add(predictions.get(index).getDescription());
        }
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.textview, list);
        city.setAdapter(arrayAdapter);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        if (object instanceof Suggestion) {
            Suggestion suggestion = (Suggestion) object;
            updateAutoSuggestion(suggestion);
        } else {
            if (calledApiValue == 46) {
                progressDialog.dismiss();
                b_payment.setEnabled(true);
                Toast.makeText(ScheduleNewClass.this, (String) object, Toast.LENGTH_SHORT).show();
                if (MentorDetailsActivity.mentorDetailsActivity != null) {
                    Log.d(TAG, "mentor details activity found not null ");
                    if (SetScheduleActivity.setScheduleActivity != null) {
                        Log.d(TAG, "Set schedule activity found not null ");
                        SetScheduleActivity.setScheduleActivity.finish();
                    }

                    MentorDetailsActivity.mentorDetailsActivity.getCalendarDetailsAPICall();
                } else {
                    Log.d(TAG, "mentor details activity found null ");
                }
                finish();
            }

        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        if (calledApiValue == 46) {
            b_payment.setEnabled(true);
            Toast.makeText(ScheduleNewClass.this, (String) object, Toast.LENGTH_SHORT).show();

        }

    }


    class DurationOfSuccessfulClassDays {
        private String start_date;
        private String stop_date;

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getStop_date() {
            return stop_date;
        }

        public void setStop_date(String stop_date) {
            this.stop_date = stop_date;
        }
    }
}
