package com.findmycoach.mentor.dao;

/**
 * Created by prem on 12/2/15.
 */
public class NotificationModel {
    private String id;
    private String message;
    private String time;

    public NotificationModel(String id, String message, String time) {
        this.id = id;
        this.message = message;
        this.time = time;
    }

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
