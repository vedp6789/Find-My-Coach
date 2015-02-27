package com.findmycoach.app.beans.chats;

import com.google.gson.annotations.Expose;

public class Data {

    @Expose
    private String id;
    @Expose
    private String sender_id;
    @Expose
    private String receiver_id;
    @Expose
    private String message_type;
    @Expose
    private String message;
    @Expose
    private String created_on;
    @Expose
    private String updated_on;

    public String getId() {
        return id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public String getMessage_type() {
        return message_type;
    }

    public String getMessage() {
        return message;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }
}
