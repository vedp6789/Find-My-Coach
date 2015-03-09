package com.findmycoach.app.fragment;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.ScheduleYourVacation;
import com.findmycoach.app.adapter.CalendarGridAdapter;
import com.findmycoach.app.R;
import com.findmycoach.app.beans.CalendarSchedule.Day;
import com.findmycoach.app.beans.CalendarSchedule.DayEvent;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class MyScheduleFragment extends Fragment implements View.OnClickListener {

    private TextView currentMonth, add_slot, add_vacation;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private GridView calendarView;
    private CalendarGridAdapter adapter;
    private Calendar _calendar;
    private int month, year;
    private static final String dateTemplate = "MMMM yyyy";
    private MyScheduleFragment myScheduleFragment;
    private int days_in_month;
    private static final String TAG="FMC";


    public static ArrayList<Day> day_schedule=null;


    public MyScheduleFragment() {
        // Required empty public constructor
    }

    public static MyScheduleFragment newInstance(String param1, String param2) {
        MyScheduleFragment fragment = new MyScheduleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myScheduleFragment = this;

        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);



        Calendar calendar=new GregorianCalendar(year,month-1,1);
        days_in_month=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.d(TAG,month+" Month of "+year+" have days: "+calendar.getActualMaximum(Calendar.DAY_OF_MONTH));


        day_schedule=new ArrayList<Day>();


        JSONArray jsonArray=getDemoCalendarDetails(days_in_month,month,year);

        calendarEventSchduler(jsonArray);







        //RequestParams requestParams=new RequestParams();

    }

    private void calendarEventSchduler(JSONArray jsonArray) {
        day_schedule=new ArrayList<Day>();
        for(int e=0;e <jsonArray.length();e++){
            Day day=new Day();
            try {
                JSONObject jsonObject=jsonArray.getJSONObject(e);
                day.setDay(jsonObject.getInt("day"));
                day.setMonth(jsonObject.getInt("month"));
                day.setYear(jsonObject.getInt("year"));
                List<DayEvent> dayEvents = new ArrayList<DayEvent>();
                JSONArray eventArray=jsonObject.getJSONArray("event");
                for (int e_no=0; e_no < eventArray.length();e_no++){
                    DayEvent dayEvent=new DayEvent();
                    JSONObject jsonObject1=eventArray.getJSONObject(e_no);
                    dayEvent.setEvent_start_hour(jsonObject1.getInt("start_hour"));
                    dayEvent.setEvent_start_min(jsonObject1.getInt("start_min"));
                    dayEvent.setEvent_stop_hour(jsonObject1.getInt("stop_hour"));
                    dayEvent.setEvent_stop_min(jsonObject1.getInt("stop_min"));
                    dayEvent.setEvent_name(jsonObject1.getString("name"));
                    dayEvents.add(dayEvent);
                }
                day.setDayEvents(dayEvents);
                day_schedule.add(day);


            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }



    }

    private JSONArray getDemoCalendarDetails(int days,int month,int year) {
        JSONArray jsonArray=new JSONArray();
        for (int i=1;i<days+1;i++){
            JSONObject jsonObject1=new JSONObject();
            try {
                JSONObject event1=new JSONObject();
                event1.put("start_hour",10);
                event1.put("start_min",30);
                event1.put("stop_hour",11);
                event1.put("stop_min",45);
                event1.put("name","Android Tutorial");
                JSONObject event2=new JSONObject();
                event2.put("start_hour",13);
                event2.put("start_min",50);
                event2.put("stop_hour",17);
                event2.put("stop_min",45);
                event2.put("name","Science Tutorial");
                JSONObject event3=new JSONObject();
                event3.put("start_hour",8);
                event3.put("start_min",0);
                event3.put("stop_hour",9);
                event3.put("stop_min",30);
                event3.put("name","Java Programming");

                JSONArray event_array=new JSONArray();
                if((i%8) != 0 ){
                    event_array.put(event1);
                    event_array.put(event2);
                    event_array.put(event3);
                }


                jsonObject1.put("day",i);
                jsonObject1.put("month",month);
                jsonObject1.put("year",year);
                jsonObject1.put("event",event_array);

                jsonArray.put(jsonObject1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
return jsonArray;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        // Checking logged in user and return respective view
        if (DashboardActivity.dashboardActivity.user_group == 2) {
            view = inflater.inflate(R.layout.fragment_schedule_mentee, container, false);
        } else if (DashboardActivity.dashboardActivity.user_group == 3) {
            view = inflater.inflate(R.layout.my_calendar_view, container, false);
            initialize(view);
            applyListeners();
        }
        return view;
    }

    private void applyListeners() {


    }

    private void initialize(View view) {


        add_slot = (TextView) view.findViewById(R.id.tv_add_new_slot);
        add_slot.setOnClickListener(this);

        add_vacation = (TextView) view.findViewById(R.id.tv_add_vacation);
        add_vacation.setOnClickListener(this);

        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (GridView) view.findViewById(R.id.calendar);

        // Initialised
        adapter = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment,day_schedule);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v == add_slot) {

            Intent intent = new Intent(getActivity(), AddNewSlotActivity.class);
            startActivity(intent);

        }
        if (v == add_vacation) {

            Intent intent = new Intent(getActivity(), ScheduleYourVacation.class);
            startActivity(intent);

        }


        if (v == prevMonth) {
            showNextMonth();
        }
        if (v == nextMonth) {
            showPrevMonth();
        }
    }

    public void showPrevMonth() {
        if (month > 11) {
            month = 1;
            year++;
        } else {
            month++;
        }
        setGridCellAdapterToDate(month, year);
    }

    public void showNextMonth() {
        if (month <= 1) {
            month = 12;
            year--;
        } else {
            month--;
        }
        setGridCellAdapterToDate(month, year);
    }

    private void setGridCellAdapterToDate(int month, int year) {

        Calendar calendar=new GregorianCalendar(year,month-1,1);
        days_in_month=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.d(TAG,month+" Month of "+year+" have days: "+calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        JSONArray jsonArray=getDemoCalendarDetails(days_in_month,month,year);

        calendarEventSchduler(jsonArray);


        adapter = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year, myScheduleFragment,day_schedule);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

}
