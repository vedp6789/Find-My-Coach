package com.findmycoach.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by ved on 13/5/15.
 */
public class MentorNotificationTabsPagerAdapter extends FragmentStatePagerAdapter {
    private String [] titles = {"Connection request","Schedule request"};
    public MentorNotificationTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    private String TAG="FMC";
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.d(TAG,"connection_request_notifications");
//                return ConnectionRequestFragment.newInstance();
            case 1:
                Log.d(TAG,"schedule_request_notifications");
//                return ScheduleRequestFragment.newInstance();

        }
        return null;

    }

    @Override
    public int getCount() {
        return titles.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
