package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 11/3/15.
 */
public class DaySlot implements Parcelable{
    public DaySlot(){

    }

    public String slot_start_time;
    public String slot_stop_time;

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
        dest.writeString(this.slot_start_time);
        dest.writeString(this.slot_stop_time);
    }


    public DaySlot(Parcel source){
    /*
     * Reconstruct from the Parcel
     */

        this.slot_start_time=source.readString();
        this.slot_stop_time=source.readString();

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
