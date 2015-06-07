package com.findmycoach.app.beans.CalendarSchedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 2/6/15.
 */
public class MentorInfo implements Parcelable {

    public MentorInfo(){

    }
    String mentor_id;
    String first_name;
    String last_name;
    String address;
    String contact_number;
    String gender;

    public String getMentor_id() {
        return mentor_id;
    }

    public void setMentor_id(String mentor_id) {
        this.mentor_id = mentor_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mentor_id);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeString(this.address);
        dest.writeString(this.contact_number);
        dest.writeString(this.gender);
    }

    public MentorInfo(Parcel parcel){
        this.mentor_id = parcel.readString();
        this.first_name=parcel.readString();
        this.last_name=parcel.readString();
        this.address=parcel.readString();
        this.contact_number=parcel.readString();
        this.gender=parcel.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MentorInfo createFromParcel(Parcel in) {
            return new MentorInfo(in);
        }

        public MentorInfo[] newArray(int size) {
            return new MentorInfo[size];
        }
    };
}
