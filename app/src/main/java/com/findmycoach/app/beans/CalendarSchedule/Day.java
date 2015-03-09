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




    private int day;
    private int month;
    private int year;
    private List<DayEvent> dayEvents;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

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
        dest.writeInt(this.day);
        dest.writeInt(this.month);
        dest.writeInt(this.year);
        dest.writeTypedList(this.dayEvents);
    }
    public Day(Parcel parcel){
        //readFromParcel(parcel);
        this();
        this.day = parcel.readInt();
        this.month=parcel.readInt();
        this.year=parcel.readInt();
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
