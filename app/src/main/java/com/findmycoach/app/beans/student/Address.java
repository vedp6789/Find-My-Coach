package com.findmycoach.app.beans.student;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by abhi7 on 27/07/15.
 */
public class Address {
    @SerializedName("city_id")
    @Expose
    private int city_id;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    private String physical_address;
    private String locale;
    private int default_yn;
    @SerializedName("country_id")
    @Expose
    private int country;   // country id
    @SerializedName("country_name")
    @Expose
    private String countryName;   // country id

    @SerializedName("state_name")
    @Expose
    private String stateName;



    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public int getDefault_yn() {
        return default_yn;
    }

    public void setDefault_yn(int default_yn) {
        this.default_yn = default_yn;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public String getPhysical_address() {
        return physical_address;
    }

    public void setPhysical_address(String physical_address) {
        this.physical_address = physical_address;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
