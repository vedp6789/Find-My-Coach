package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 5/6/15.
 */
public class EventDuration implements Parcelable {
private String start_date;
    private String stop_date;

    public EventDuration(Parcel in) {
        this.start_date =in.readString();
        this.stop_date =in.readString();
    }

    public String getStop_date() {
        return stop_date;
    }

    public void setStop_date(String stop_date) {
        this.stop_date = stop_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public EventDuration() {

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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public EventDuration createFromParcel(Parcel in) {
            return new EventDuration(in);
        }

        public EventDuration[] newArray(int size) {
            return new EventDuration[size];
        }
    };

}
