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
    @SerializedName("age_range")
    @Expose
    String ageGroup;

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

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
}
