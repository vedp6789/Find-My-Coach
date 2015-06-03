package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ved on 31/5/15.
 */
public class Event implements Parcelable{
    private String event_id;
    private String event_stop_date;
    private String event_total_mentee; /* Number of mentees having same event */
    private String sub_category_name;
    private List<Mentee> mentees;   /* This is having different mentees data for this event like start date of event, first name and last name*/

    public List<Mentee> getMentees() {
        return mentees;
    }

    public void setMentees(List<Mentee> mentees) {
        this.mentees = mentees;
    }

    public Event() {
        mentees = new ArrayList<Mentee>();
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
        dest.writeString(this.event_stop_date);
        dest.writeString(this.sub_category_name);
        dest.writeString(this.event_total_mentee);
        dest.writeTypedList(this.mentees);
    }

    public Event(Parcel source){
    /*
     * Reconstruct from the Parcel
     */
        this();
        this.event_id=source.readString();
        this.event_stop_date=source.readString();
        this.sub_category_name=source.readString();
        this.event_total_mentee=source.readString();
        source.readTypedList(this.mentees,Mentee.CREATOR);
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
