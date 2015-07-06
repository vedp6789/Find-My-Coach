package com.findmycoach.app.beans.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @Expose
    private String message;
    @Expose
    private Data data;
    @SerializedName("auth_token")
    @Expose
    private String authToken;
    @Expose
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

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

    /**
     * @return The authToken
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * @param authToken The auth_token
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
