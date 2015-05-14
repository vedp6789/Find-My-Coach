package com.findmycoach.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.findmycoach.app.beans.UserNotifications.ConnectionRequest;
import com.findmycoach.app.beans.UserNotifications.MentorNotifications;
import com.findmycoach.app.beans.UserNotifications.ScheduleRequest;
import com.findmycoach.app.fragment_mentor.ConnectionRequestFragment;
import com.findmycoach.app.fragment_mentor.ScheduleRequestFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ved on 13/5/15.
 */
public class MentorNotificationTabsPagerAdapter extends FragmentStatePagerAdapter {
    MentorNotifications mentorNotifications;
    ArrayList<ScheduleRequest> scheduleRequest_notification_list;
    ArrayList<ConnectionRequest> connectionRequest_notificaton_list;
    private String [] titles = {"Connection request","Schedule request"};
    public MentorNotificationTabsPagerAdapter(FragmentManager fm,MentorNotifications mentorNotifications) {
        super(fm);
        this.mentorNotifications=mentorNotifications;
        scheduleRequest_notification_list=mentorNotifications.getList_of_schedule_request();
        connectionRequest_notificaton_list=mentorNotifications.getList_of_connection_request();
    }
    private String TAG="FMC";
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.d(TAG,"connection_request_notifications");
                return ConnectionRequestFragment.newInstance(connectionRequest_notificaton_list);


            case 1:
                Log.d(TAG,"schedule_request_notifications");
                return ScheduleRequestFragment.newInstance(scheduleRequest_notification_list);

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
