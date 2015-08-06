package com.findmycoach.app.beans.mentor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by abhi7 on 06/08/15.
 */
public class CountryConfig {

    @SerializedName("country_id")
    @Expose
    private int countryId;

    @Expose
    private int id;

    @SerializedName("config_title")
    @Expose
    private String configTitle;

    @SerializedName("config_description")
    @Expose
    private String configDescription;

    @SerializedName("config_value")
    @Expose
    private String configValue;

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConfigTitle() {
        return configTitle;
    }

    public void setConfigTitle(String configTitle) {
        this.configTitle = configTitle;
    }

    public String getConfigDescription() {
        return configDescription;
    }

    public void setConfigDescription(String configDescription) {
        this.configDescription = configDescription;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
