package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 31/5/15.
 */
public class Event implements Parcelable{
    private String event_id;
    private String event_start_date;
    private String event_stop_date;
    private String event_total_mentee; /* Number of mentees having same event */
    private String fname; /*fname when event_type is solo else not going to be used*/
    private String lname; /*lname when event_type is solo else not going to be used*/
    private String sub_category_name;
    private String slot_id;
    private String [] week_days;

    public String getSlot_id() {
        return slot_id;
    }

    public String[] getWeek_days() {
        return week_days;
    }

    public void setWeek_days(String[] week_days) {
        this.week_days = week_days;
    }

    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    public Event() {

    }

    public String getEvent_start_date() {
        return event_start_date;
    }

    public void setEvent_start_date(String event_start_date) {
        this.event_start_date = event_start_date;
    }

    public String getEvent_stop_date() {
        return event_stop_date;
    }

    public void setEvent_stop_date(String event_stop_date) {
        this.event_stop_date = event_stop_date;
    }


    public String getEvent_total_mentee() {
        return event_total_mentee;
    }

    public void setEvent_total_mentee(String event_total_mentee) {
        this.event_total_mentee = event_total_mentee;
    }

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
        dest.writeString(this.event_start_date);
        dest.writeString(this.event_stop_date);
        dest.writeString(this.slot_id);
        dest.writeString(this.sub_category_name);
        dest.writeString(this.event_total_mentee);
        dest.writeStringArray(this.week_days);
        dest.writeString(this.fname);
        dest.writeString(this.lname);



    }

    public Event(Parcel source){
    /*
     * Reconstruct from the Parcel
     */

        this.event_id=source.readString();
        this.event_start_date=source.readString();
        this.event_stop_date=source.readString();
        this.slot_id=source.readString();
        this.sub_category_name=source.readString();
        this.event_total_mentee=source.readString();
        this.week_days=source.createStringArray();
        this.fname=source.readString();
        this.lname=source.readString();



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
