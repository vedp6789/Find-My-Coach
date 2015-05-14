package com.findmycoach.app.beans.UserNotifications;

import android.os.Parcel;
import android.os.Parcelable;

import com.findmycoach.app.beans.CalendarSchedule.DayEvent;
import com.findmycoach.app.beans.CalendarSchedule.DaySlot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ved on 14/5/15.
 */
public class MentorNotifications implements Parcelable{
    private ArrayList<ScheduleRequest> list_of_schedule_request;
    private ArrayList<ConnectionRequest> list_of_connection_request;

    public ArrayList<ScheduleRequest> getList_of_schedule_request() {
        return list_of_schedule_request;
    }

    public void setList_of_schedule_request(ArrayList<ScheduleRequest> list_of_schedule_request) {
        this.list_of_schedule_request = list_of_schedule_request;
    }

    public ArrayList<ConnectionRequest> getList_of_connection_request() {
        return list_of_connection_request;
    }

    public void setList_of_connection_request(ArrayList<ConnectionRequest> list_of_connection_request) {
        this.list_of_connection_request = list_of_connection_request;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.list_of_schedule_request);
        dest.writeTypedList(this.list_of_connection_request);
    }

    public MentorNotifications(){
        list_of_connection_request=new ArrayList<ConnectionRequest>();
        list_of_schedule_request=new ArrayList<ScheduleRequest>();
    }

    public MentorNotifications(Parcel parcel){
        //readFromParcel(parcel);
        this();
        parcel.readTypedList(this.list_of_connection_request, ConnectionRequest.CREATOR);
        parcel.readTypedList(this.list_of_schedule_request, ScheduleRequest.CREATOR);
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MentorNotifications createFromParcel(Parcel in) {
            return new MentorNotifications(in);
        }

        public MentorNotifications[] newArray(int size) {
            return new MentorNotifications[size];
        }
    };


}
