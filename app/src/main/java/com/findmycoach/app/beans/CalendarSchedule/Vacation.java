package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 31/5/15.
 */
public class Vacation implements Parcelable {
    private String vacation_id;
    private String start_date;
    private String stop_date;
    private String cause_of_the_vacation;

    private String start_time;
    private String stop_time;
    private String vacation_made_at_network_success;     /* This field is getting data only in case of non coinciding vacation array creation
                                                           "true" when network is success otherwise "false"
    */

    public Vacation(){

    }

    public String getVacation_made_at_network_success() {
        return vacation_made_at_network_success;
    }

    public void setVacation_made_at_network_success(String vacation_made_at_network_success) {
        this.vacation_made_at_network_success = vacation_made_at_network_success;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStop_time() {
        return stop_time;
    }

    public void setStop_time(String stop_time) {
        this.stop_time = stop_time;
    }

    public String getVacation_id() {
        return vacation_id;
    }

    public void setVacation_id(String vacation_id) {
        this.vacation_id = vacation_id;
    }

    public String getCause_of_the_vacation() {
        return cause_of_the_vacation;
    }

    public void setCause_of_the_vacation(String cause_of_the_vacation) {
        this.cause_of_the_vacation = cause_of_the_vacation;
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
        dest.writeString(this.vacation_id);
        dest.writeString(this.start_date);
        dest.writeString(this.stop_date);
        dest.writeString(this.cause_of_the_vacation);
        dest.writeString(this.start_time);
        dest.writeString(this.stop_time);
        dest.writeString(this.vacation_made_at_network_success);
    }

    public Vacation(Parcel parcel){
        this.vacation_id=parcel.readString();
        this.start_date=parcel.readString();
        this.stop_date=parcel.readString();
        this.cause_of_the_vacation=parcel.readString();
        this.start_time=parcel.readString();
        this.stop_time=parcel.readString();
        this.vacation_made_at_network_success=parcel.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Vacation createFromParcel(Parcel in) {
            return new Vacation(in);
        }

        public Vacation[] newArray(int size) {
            return new Vacation[size];
        }
    };

}
