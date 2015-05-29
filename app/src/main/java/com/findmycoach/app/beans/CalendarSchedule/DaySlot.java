package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 11/3/15.
 */
public class DaySlot implements Parcelable{
    public DaySlot(){

    }

    public String slot_id;
    public String slot_start_time;
    public String slot_stop_time;
    public String slot_start_date;
    public String slot_stop_date;
    public String slot_type;
    public String slot_max_users;
    public String [] slot_week_days;

    public String getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    public String[] getSlot_week_days() {
        return slot_week_days;
    }

    public void setSlot_week_days(String[] slot_week_days) {
        this.slot_week_days = slot_week_days;
    }

    public String getSlot_max_users() {
        return slot_max_users;
    }

    public void setSlot_max_users(String slot_max_users) {
        this.slot_max_users = slot_max_users;
    }

    public String getSlot_type() {
        return slot_type;
    }

    public void setSlot_type(String slot_type) {
        this.slot_type = slot_type;
    }

    public String getSlot_start_date() {
        return slot_start_date;
    }

    public void setSlot_start_date(String slot_start_date) {
        this.slot_start_date = slot_start_date;
    }

    public String getSlot_stop_date() {
        return slot_stop_date;
    }

    public void setSlot_stop_date(String slot_stop_date) {
        this.slot_stop_date = slot_stop_date;
    }

    public String getSlot_start_time() {
        return slot_start_time;
    }

    public void setSlot_start_time(String slot_start_time) {
        this.slot_start_time = slot_start_time;
    }

    public String getSlot_stop_time() {
        return slot_stop_time;
    }

    public void setSlot_stop_time(String slot_stop_time) {
        this.slot_stop_time = slot_stop_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.slot_start_date);
        dest.writeString(this.slot_stop_date);
        dest.writeString(this.slot_start_time);
        dest.writeString(this.slot_stop_time);
        dest.writeString(this.slot_type);
        dest.writeString(this.slot_max_users);
        dest.writeStringArray(this.slot_week_days);
        dest.writeString(this.slot_id);
    }


    public DaySlot(Parcel source){
    /*
     * Reconstruct from the Parcel
     */
        this.slot_start_date=source.readString();
        this.slot_stop_date=source.readString();
        this.slot_start_time=source.readString();
        this.slot_stop_time=source.readString();
        this.slot_type=source.readString();
        this.slot_max_users=source.readString();
        this.slot_week_days=source.createStringArray();
        this.slot_id=source.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DaySlot createFromParcel(Parcel in) {
            return new DaySlot(in);
        }

        public DaySlot[] newArray(int size) {
            return new DaySlot[size];
        }
    };
}
