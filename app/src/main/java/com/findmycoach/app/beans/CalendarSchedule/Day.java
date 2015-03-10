package com.findmycoach.app.beans.CalendarSchedule;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by ved on 6/3/15.
 */
public class Day implements Parcelable {

    public Day(){
        dayEvents=new ArrayList<DayEvent>();
    }



    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static Creator getCreator() {
        return CREATOR;
    }

    private List<DayEvent> dayEvents;


    public List<DayEvent> getDayEvents() {
        return dayEvents;
    }

    public void setDayEvents(List<DayEvent> dayEvents) {
        this.dayEvents = dayEvents;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);

        dest.writeTypedList(this.dayEvents);
    }
    public Day(Parcel parcel){
        //readFromParcel(parcel);
        this();
        this.date=parcel.readString();
        //List<DayEvent> list = null;
        parcel.readTypedList(this.dayEvents, DayEvent.CREATOR);
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        public Day[] newArray(int size) {
            return new Day[size];
        }
    };


}
