package com.findmycoach.app.beans.UserNotifications;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ved on 14/5/15.
 */
public class ScheduleRequest implements Parcelable {
    private String id;
    private String image_url;
    private String student_id;
    private String event_id;
    private String message;
    private String first_name;
    private String last_name;
    private String start_date;
    private String status; /* read or unread*/

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    private String stop_date;
    private String start_time;
    private String stop_time;
    private String subject;
    private String class_type;
    private String[] week_days;

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

    public String getClass_type() {
        return class_type;
    }

    public void setClass_type(String class_type) {
        this.class_type = class_type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String[] getWeek_days() {
        return week_days;
    }

    public void setWeek_days(String[] week_days) {
        this.week_days = week_days;
    }


    public ScheduleRequest() {
    }

    public ScheduleRequest(Parcel parcel) {

        this.id = parcel.readString();
        this.image_url=parcel.readString();
        this.student_id=parcel.readString();
        this.event_id=parcel.readString();
        this.message = parcel.readString();
        this.first_name = parcel.readString();
        this.last_name = parcel.readString();
        this.start_date = parcel.readString();
        this.stop_date = parcel.readString();
        this.start_time = parcel.readString();
        this.stop_time = parcel.readString();
        this.subject = parcel.readString();
        this.class_type = parcel.readString();
        this.week_days = parcel.createStringArray();
        this.status= parcel.readString();

    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ScheduleRequest createFromParcel(Parcel in) {
            return new ScheduleRequest(in);
        }

        public ScheduleRequest[] newArray(int size) {
            return new ScheduleRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.image_url);
        dest.writeString(this.student_id);
        dest.writeString(this.event_id);
        dest.writeString(this.message);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeString(this.start_date);
        dest.writeString(this.stop_date);
        dest.writeString(this.start_time);
        dest.writeString(this.stop_time);
        dest.writeString(this.subject);
        dest.writeString(this.class_type);
        dest.writeStringArray(this.week_days);
        dest.writeString(this.status);
    }
}
