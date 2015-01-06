package com.findmycoach.mentor.beans.registration;

import com.google.gson.annotations.Expose;

public class SignUpResponse {

    @Expose
    private String message;
    @Expose
    private Data data;

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
    public Data getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(Data data) {
        this.data = data;
    }

}
