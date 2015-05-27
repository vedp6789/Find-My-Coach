package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 27/5/15.
 */
public class VacationDurationDetailBean implements Parcelable {
    private String date;
    private String week_day;

    public VacationDurationDetailBean(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek_day() {
        return week_day;
    }

    public void setWeek_day(String week_day) {
        this.week_day = week_day;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.week_day);
    }

    public VacationDurationDetailBean(Parcel parcel){
        this.date=parcel.readString();
        this.week_day=parcel.readString();
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public VacationDurationDetailBean createFromParcel(Parcel in) {
            return new VacationDurationDetailBean(in);
        }

        public VacationDurationDetailBean[] newArray(int size) {
            return new VacationDurationDetailBean[size];
        }
    };
}
