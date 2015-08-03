package com.findmycoach.app.beans.category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ShekharKG on 3/8/15.
 */
public class Country {

    @Expose
    int id;
    @SerializedName("long_name")
    @Expose
    String longName;
    @SerializedName("short_name")
    @Expose
    String shortName;
    @SerializedName("calling_code")
    @Expose
    String callingCode;
    @Expose
    String iso;
    @SerializedName("numbers_in_phone_number")
    @Expose
    int digitsInPhoneNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCallingCode() {
        return callingCode;
    }

    public void setCallingCode(String callingCode) {
        this.callingCode = callingCode;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public int getDigitsInPhoneNumber() {
        return digitsInPhoneNumber;
    }

    public void setDigitsInPhoneNumber(int digitsInPhoneNumber) {
        this.digitsInPhoneNumber = digitsInPhoneNumber;
    }
}
