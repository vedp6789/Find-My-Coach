package com.findmycoach.app.beans.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ved on 28/7/15.
 */
public class AgeGroupPreferences {

    @Expose
    int id;
    @Expose
    String description;
    @Expose
    String min;

    @Expose
    String max;

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return description;
    }

    public void setValue(String value) {
        this.description = value;
    }

}
