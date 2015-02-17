package com.findmycoach.mentor.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.findmycoach.mentor.activity.DashboardActivity;
import com.findmycoach.mentor.activity.SetScheduleActivity;
import com.findmycoach.mentor.util.StorageHelper;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by prem on 5/2/15.
 */
public class Schedule {

    private CaldroidFragment caldroidFragment;
    private HashMap<Date, Integer> backgroundForDateMap;
    private Context context;

    private static final String TAG="FMC";

    public Schedule(Context context){
        this.context = context;
    }
    public CaldroidFragment initCalendar() {
        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);
        showDummyDataToCalendar();
        addListener();
        return caldroidFragment;
    }

    private void addListener() {
        caldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                Log.d(TAG, date.toString());
                Intent intent = new Intent(context, SetScheduleActivity.class);
                intent.putExtra("DATE",date.toString());
                context.startActivity(intent);
            }
        });
        caldroidFragment.setBackgroundResourceForDates(backgroundForDateMap);
    }

    //USed to show dummy data in Calendar
    private void showDummyDataToCalendar() {
        backgroundForDateMap = new HashMap<Date, Integer>();
        long today = new Date().getTime();
        boolean isFree = true;
        for(int i=0; i<100; i++){
            if(isFree){
                backgroundForDateMap.put(new Date(today + (i*86400000)),android.R.color.holo_red_light);
                isFree = false;
            }else{
                backgroundForDateMap.put(new Date(today + (i*86400000)),android.R.color.holo_green_light);
                isFree = true;
            }
            if(i % 5 == 0)
                i += 3;
        }
    }
}
