package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 3/6/15.
 */
public class Mentee implements Parcelable{
    public Mentee(){

    }
    private String event_start_date;
    private String first_name;
    private String last_name;

    public String getEvent_start_date() {
        return event_start_date;
    }

    public void setEvent_start_date(String event_start_date) {
        this.event_start_date = event_start_date;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.event_start_date);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
    }

    private Mentee(Parcel parcel){
        this.event_start_date = parcel.readString();
        this.first_name = parcel.readString();
        this.last_name = parcel.readString();

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Mentee createFromParcel(Parcel in) {
            return new Mentee(in);
        }

        public Mentee[] newArray(int size) {
            return new Mentee[size];
        }
    };


}
