package com.findmycoach.app.beans.UserNotifications;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 25/6/15.
 */
public class Durations implements Parcelable {
    private String start_date;
    private String stop_date;

    public Durations() {

    }


    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStop_date() {
        return stop_date;
    }

    public void setStop_date(String stop_date) {
        this.stop_date = stop_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.start_date);
        dest.writeString(this.stop_date);
    }

    public Durations(Parcel parcel){

        this.start_date =parcel.readString();
        this.stop_date = parcel.readString();
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Durations createFromParcel(Parcel in) {
            return new Durations(in);
        }

        public Durations[] newArray(int size) {
            return new Durations[size];
        }
    };

}
