package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 6/3/15.
 */
public class EventBean implements Parcelable{
    private String event_id;
    private String event_start_date;
    private String event_stop_date;
    private String event_start_time;
    private String event_stop_time;
    private String event_type;
    private String event_total_mentee;  /* Number of mentees having same event */
    private String fname;/* fname when event_type is solo else not going to be used*/
    private String lname;/* lname when event_type is solo else not going to be used*/
    private String sub_category_name;
    private SlotBean slot;

    public SlotBean getSlot() {
        return slot;
    }

    public void setSlot(SlotBean slot) {
        this.slot = slot;
    }

    public EventBean() {

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

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
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
        dest.writeString(this.event_start_date);
        dest.writeString(this.event_stop_date);
        dest.writeString(this.event_start_time);
        dest.writeString(this.event_stop_time);
        dest.writeString(this.event_type);
        dest.writeString(this.event_total_mentee);
        dest.writeString(this.fname);
        dest.writeString(this.lname);
        dest.writeString(this.sub_category_name);
        dest.writeParcelable(this.slot, flags);

    }

    public EventBean(Parcel source){
    /*
     * Reconstruct from the Parcel
     */

        this.event_id=source.readString();
        this.event_start_date=source.readString();
        this.event_stop_date=source.readString();
        this.event_start_time=source.readString();
        this.event_stop_time=source.readString();
        this.event_type=source.readString();
        this.event_total_mentee=source.readString();
        this.fname=source.readString();
        this.lname=source.readString();
        this.sub_category_name=source.readString();
        this.slot=source.readParcelable(getClass().getClassLoader());

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public EventBean createFromParcel(Parcel in) {
            return new EventBean(in);
        }

        public EventBean[] newArray(int size) {
            return new EventBean[size];
        }
    };


}
