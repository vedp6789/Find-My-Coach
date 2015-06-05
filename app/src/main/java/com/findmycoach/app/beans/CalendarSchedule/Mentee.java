package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ved on 3/6/15.
 */
public class Mentee implements Parcelable{
    public Mentee(){
       eventDurations = new ArrayList<EventDuration>();
    }
    private List<EventDuration>  eventDurations;
    private String first_name;
    private String last_name;


    public List<EventDuration> getEventDurations() {
        return eventDurations;
    }

    public void setEventDurations(List<EventDuration> eventDurations) {
        this.eventDurations = eventDurations;
    }

    public static Creator getCreator() {
        return CREATOR;
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
        dest.writeTypedList(eventDurations);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
    }

    private Mentee(Parcel parcel){
        this();
        parcel.readTypedList(this.eventDurations,EventDuration.CREATOR);
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
