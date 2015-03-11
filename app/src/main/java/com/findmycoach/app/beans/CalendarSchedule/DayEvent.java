package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ved on 6/3/15.
 */
public class DayEvent implements Parcelable{

    private String event_start_time;
    private String event_stop_time;
    private String event_start_date;
    private String event_stop_date;


    public DayEvent() {

    }



    public String getEvent_start_time() {
        return event_start_time;
    }

    public void setEvent_start_time(String event_start_time) {
        this.event_start_time = event_start_time;
    }

    public String getEvent_stop_date() {
        return event_stop_date;
    }

    public void setEvent_stop_date(String even_stop_date) {
        this.event_stop_date = even_stop_date;
    }

    public String getEvent_start_date() {
        return event_start_date;
    }

    public void setEvent_start_date(String event_start_date) {
        this.event_start_date = event_start_date;
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
         dest.writeString(this.event_start_date);
         dest.writeString(this.event_stop_date);
         dest.writeString(this.event_start_time);
        dest.writeString(this.event_stop_time);

    }

    public DayEvent(Parcel source){
    /*
     * Reconstruct from the Parcel
     */

        this.event_start_date = source.readString();
        this.event_stop_date = source.readString();
        this.event_start_time=source.readString();
        this.event_stop_time=source.readString();

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
