package com.findmycoach.app.beans.student;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by abhi7 on 27/07/15.
 */
public class Address {
    @SerializedName("addressline1")
    @Expose
    private String addressLine1;
    private String locality;
    private String zip;
    private int default_yn;

    public int getDefault_yn() {
        return default_yn;
    }

    public void setDefault_yn(int default_yn) {
        this.default_yn = default_yn;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
