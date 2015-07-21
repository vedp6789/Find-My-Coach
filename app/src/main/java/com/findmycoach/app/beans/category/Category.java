package com.findmycoach.app.beans.category;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Category {

    @Expose
    private String message;
    @SerializedName("otp_position")
    @Expose
    private int otpPosition;
    @SerializedName("otp_length")
    @Expose
    private int otpLength;
    @SerializedName("categories")
    @Expose
    private List<Datum> data = new ArrayList<>();

    public int getOtpPosition() {
        return otpPosition;
    }

    public void setOtpPosition(int otpPosition) {
        this.otpPosition = otpPosition;
    }

    public int getOtpLength() {
        return otpLength;
    }

    public void setOtpLength(int otpLength) {
        this.otpLength = otpLength;
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
    public List<Datum> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<Datum> data) {
        this.data = data;
    }

}

