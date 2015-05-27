package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 27/5/15.
 */
public class VacationCoincidingSlot implements Parcelable {
    public VacationCoincidingSlot() {

    }

    private String vacation_start_date;
    private String vacation_start_time;
    private String vacation_stop_date;
    private String vacation_stop_time;
    private String[] vacation_week_days;  /* these week days are coinciding with slot_week_day*/
    private int vacation_coincide_type;

    public int getVacation_coincide_type() {
        return vacation_coincide_type;
    }

    public void setVacation_coincide_type(int vacation_coincide_type) {
        this.vacation_coincide_type = vacation_coincide_type;
    }

    public String getVacation_start_date() {
        return vacation_start_date;
    }

    public void setVacation_start_date(String vacation_start_date) {
        this.vacation_start_date = vacation_start_date;
    }

    public String getVacation_start_time() {
        return vacation_start_time;
    }

    public void setVacation_start_time(String vacation_start_time) {
        this.vacation_start_time = vacation_start_time;
    }

    public String getVacation_stop_date() {
        return vacation_stop_date;
    }

    public void setVacation_stop_date(String vacation_stop_date) {
        this.vacation_stop_date = vacation_stop_date;
    }

    public String getVacation_stop_time() {
        return vacation_stop_time;
    }

    public void setVacation_stop_time(String vacation_stop_time) {
        this.vacation_stop_time = vacation_stop_time;
    }

    public String[] getVacation_week_days() {
        return vacation_week_days;
    }

    public void setVacation_week_days(String[] vacation_week_days) {
        this.vacation_week_days = vacation_week_days;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.vacation_start_date);
        dest.writeString(this.vacation_start_time);
        dest.writeString(this.vacation_stop_date);
        dest.writeString(this.vacation_stop_time);
        dest.writeStringArray(this.vacation_week_days);
        dest.writeInt(this.vacation_coincide_type);
    }

    public VacationCoincidingSlot(Parcel parcel) {
        this.vacation_start_date = parcel.readString();
        this.vacation_start_time = parcel.readString();
        this.vacation_stop_date = parcel.readString();
        this.vacation_stop_date = parcel.readString();
        this.vacation_week_days = parcel.createStringArray();
        this.vacation_coincide_type = parcel.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public VacationCoincidingSlot createFromParcel(Parcel in) {
            return new VacationCoincidingSlot(in);
        }

        public VacationCoincidingSlot[] newArray(int size) {
            return new VacationCoincidingSlot[size];
        }
    };

}
