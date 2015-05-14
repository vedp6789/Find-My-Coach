package com.findmycoach.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.findmycoach.app.beans.UserNotifications.MentorNotifications;
import com.findmycoach.app.fragment_mentor.ConnectionRequestFragment;
import com.findmycoach.app.fragment_mentor.ScheduleRequestFragment;

import java.util.ArrayList;

/**
 * Created by ved on 13/5/15.
 */
public class MentorNotificationTabsPagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<MentorNotifications> arrayList_mentor_notificaton;
    private String [] titles = {"Connection request","Schedule request"};
    public MentorNotificationTabsPagerAdapter(FragmentManager fm,ArrayList<MentorNotifications> arrayList_mentor_notifications) {
        super(fm);
        this.arrayList_mentor_notificaton=arrayList_mentor_notifications;
    }
    private String TAG="FMC";
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.d(TAG,"connection_request_notifications");
                if(arrayList_mentor_notificaton.size() <=0){
                    return ConnectionRequestFragment.newInstance();
                }else{
                    return ConnectionRequestFragment.newInstance(arrayList_mentor_notificaton);
                }

            case 1:
                Log.d(TAG,"schedule_request_notifications");
                return ScheduleRequestFragment.newInstance();

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
