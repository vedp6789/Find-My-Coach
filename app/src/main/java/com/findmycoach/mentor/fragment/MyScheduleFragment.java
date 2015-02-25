package com.findmycoach.mentor.fragment;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.mentor.activity.AddNewSlotActivity;
import com.findmycoach.mentor.adapter.CalendarGridAdapter;
import com.findmycoach.mentor.R;

import java.util.Calendar;
import java.util.Locale;


public class MyScheduleFragment extends Fragment implements View.OnClickListener {

    private TextView currentMonth,add_slot;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private GridView calendarView;
    private CalendarGridAdapter adapter;
    private Calendar _calendar;
    private int month, year;
    private static final String dateTemplate = "MMMM yyyy";

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_calendar_view, container, false);
        initialize(view);
        applyListeners();
        return view;
    }

    private void applyListeners() {


    }

    private void initialize(View view) {
        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);

        add_slot= (TextView) view.findViewById(R.id.tv_add_new_slot);
        add_slot.setOnClickListener(this);


        prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));

        nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (GridView) view.findViewById(R.id.calendar);

        // Initialised
        adapter = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year);
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
        if(v == add_slot){
            //Toast.makeText(getActivity(),"Add available slot clicked !",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(getActivity(),AddNewSlotActivity.class);
            startActivity(intent);




        }
        if (v == prevMonth) {
            if (month <= 1) {
                month = 12;
                year--;
            } else {
                month--;
            }
            setGridCellAdapterToDate(month, year);
        }
        if (v == nextMonth) {
            if (month > 11) {
                month = 1;
                year++;
            } else {
                month++;
            }
            setGridCellAdapterToDate(month, year);
        }
    }

    private void setGridCellAdapterToDate(int month, int year) {
        adapter = new CalendarGridAdapter(getActivity().getApplicationContext(), month, year);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));


        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

}
