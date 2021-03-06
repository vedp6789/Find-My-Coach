package com.findmycoach.mentor.beans.requests;

import com.google.gson.annotations.Expose;

import java.util.List;


public class ConnectionRequestsResponse {

    @Expose
    private String message;
    @Expose
    private List<Data> data;

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return The data
     */
    public List<Data> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(Data data) {
        this.data.add(data);
    }

}
