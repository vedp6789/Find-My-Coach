package com.findmycoach.app.beans.UserNotifications;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ved on 14/5/15.
 */
public class ConnectionRequest implements Parcelable {
    private String id;
    private String message;
    private String first_name;
    private String last_name;
    private String start_date;
    private String stop_date;
    private String start_time;
    private String stop_time;
    private String subject;
    private String class_type;
    private String [] week_days;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.message);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeString(this.subject);
    }

    public ConnectionRequest(){
    }

    public ConnectionRequest(Parcel parcel){

        this.id=parcel.readString();
        this.message=parcel.readString();
        this.first_name=parcel.readString();
        this.last_name=parcel.readString();
        this.subject=parcel.readString();


    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ConnectionRequest createFromParcel(Parcel in) {
            return new ConnectionRequest(in);
        }

        public ConnectionRequest[] newArray(int size) {
            return new ConnectionRequest[size];
        }
    };


}
