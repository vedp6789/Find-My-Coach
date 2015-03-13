package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ved on 6/3/15.
 */
public class DayEvent implements Parcelable{
    private String event_id;
    private String event_start_time;
    private String event_stop_time;

    private String fname;
    private String lname;
    private String sub_category_name;

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public DayEvent() {

    }



    public String getEvent_start_time() {
        return event_start_time;
    }

    public void setEvent_start_time(String event_start_time) {
        this.event_start_time = event_start_time;
    }



    public String getEvent_stop_time() {
        return event_stop_time;
    }

    public void setEvent_stop_time(String event_stop_time) {
        this.event_stop_time = event_stop_time;
    }

    public static Creator getCreator() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.event_id);
        dest.writeString(this.event_start_time);
        dest.writeString(this.event_stop_time);
        dest.writeString(this.fname);
        dest.writeString(this.lname);
        dest.writeString(this.sub_category_name);


    }

    public DayEvent(Parcel source){
    /*
     * Reconstruct from the Parcel
     */

        this.event_id=source.readString();
        this.event_start_time=source.readString();
        this.event_stop_time=source.readString();
        this.fname=source.readString();
        this.lname=source.readString();
        this.sub_category_name=source.readString();

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DayEvent createFromParcel(Parcel in) {
            return new DayEvent(in);
        }

        public DayEvent[] newArray(int size) {
            return new DayEvent[size];
        }
    };


}
