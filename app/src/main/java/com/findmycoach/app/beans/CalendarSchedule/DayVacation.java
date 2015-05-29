package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 19/5/15.
 */
public class DayVacation implements Parcelable{
    private String start_date;
    private String start_time;
    private String stop_date;
    private String stop_time;
    private String cause_of_the_vacation;
    private String [] week_days;

    public DayVacation(){

    }

    public String getCause_of_the_vacation() {
        return cause_of_the_vacation;
    }

    public void setCause_of_the_vacation(String cause_of_the_vacation) {
        this.cause_of_the_vacation = cause_of_the_vacation;
    }

    public String[] getWeek_days() {
        return week_days;
    }

    public void setWeek_days(String[] week_days) {
        this.week_days = week_days;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStop_date() {
        return stop_date;
    }

    public void setStop_date(String stop_date) {
        this.stop_date = stop_date;
    }

    public String getStop_time() {
        return stop_time;
    }

    public void setStop_time(String stop_time) {
        this.stop_time = stop_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.start_date);
        dest.writeString(this.start_time);
        dest.writeString(this.stop_date);
        dest.writeString(this.stop_time);
        dest.writeString(this.cause_of_the_vacation);
        dest.writeStringArray(this.week_days);
    }

    public DayVacation(Parcel parcel){
        this.start_date=parcel.readString();
        this.start_time=parcel.readString();
        this.stop_date=parcel.readString();
        this.stop_time=parcel.readString();
        this.cause_of_the_vacation=parcel.readString();
        this.week_days=parcel.createStringArray();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DayVacation createFromParcel(Parcel in) {
            return new DayVacation(in);
        }

        public DayVacation[] newArray(int size) {
            return new DayVacation[size];
        }
    };


}
