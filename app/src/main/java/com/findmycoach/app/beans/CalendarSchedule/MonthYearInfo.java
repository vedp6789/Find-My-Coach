package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 2/6/15.
 * This is used to save month, year and number of days for previous, current and coming month.
 */
public class MonthYearInfo implements Parcelable{

    public MonthYearInfo(){

    }

    private int month;
    private int year;
    private int days;

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
         dest.writeInt(this.month);
         dest.writeInt(this.year);
         dest.writeInt(this.days);
    }

    public MonthYearInfo(Parcel parcel){
        this.month=parcel.readInt();
        this.year=parcel.readInt();
        this.days=parcel.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MonthYearInfo createFromParcel(Parcel in) {
            return new MonthYearInfo(in);
        }

        public MonthYearInfo[] newArray(int size) {
            return new MonthYearInfo[size];
        }
    };


}
