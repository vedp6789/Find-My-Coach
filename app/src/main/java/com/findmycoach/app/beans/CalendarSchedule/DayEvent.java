package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ved on 6/3/15.
 */
public class DayEvent implements Parcelable{
    private int event_start_hour;
    private int event_start_min;
    private int event_stop_hour;
    private int event_stop_min;
    private String event_name;

    public DayEvent() {

    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public int getEvent_start_hour() {
        return event_start_hour;
    }

    public void setEvent_start_hour(int event_start_hour) {
        this.event_start_hour = event_start_hour;
    }

    public int getEvent_start_min() {
        return event_start_min;
    }

    public void setEvent_start_min(int event_start_min) {
        this.event_start_min = event_start_min;
    }

    public int getEvent_stop_hour() {
        return event_stop_hour;
    }

    public void setEvent_stop_hour(int event_stop_hour) {
        this.event_stop_hour = event_stop_hour;
    }

    public int getEvent_stop_min() {
        return event_stop_min;
    }

    public void setEvent_stop_min(int event_stop_min) {
        this.event_stop_min = event_stop_min;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
         dest.writeInt(this.event_start_hour);
         dest.writeInt(this.event_start_min);
         dest.writeInt(this.event_stop_hour);
        dest.writeInt(this.event_start_min);
        dest.writeString(this.event_name);
    }

    public DayEvent(Parcel source){
    /*
     * Reconstruct from the Parcel
     */

        this.event_start_hour = source.readInt();
        this.event_start_min = source.readInt();
        this.event_stop_hour=source.readInt();
        this.event_stop_min=source.readInt();
        this.event_name=source.readString();
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
