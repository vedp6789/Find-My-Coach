package com.findmycoach.app.beans.search;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    @Expose
    private String message;
    @Expose
    private List<Datum> data = new ArrayList<Datum>();

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
