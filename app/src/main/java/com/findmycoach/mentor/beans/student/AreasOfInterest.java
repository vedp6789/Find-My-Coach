package com.findmycoach.mentor.beans.student;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AreasOfInterest {

    @SerializedName("11")
    @Expose
    private String _11;
    @SerializedName("15")
    @Expose
    private String _15;
    @SerializedName("16")
    @Expose
    private String _16;

    /**
     * @return The _11
     */
    public String get11() {
        return _11;
    }

    /**
     * @param _11 The 11
     */
    public void set11(String _11) {
        this._11 = _11;
    }

    /**
     * @return The _15
     */
    public String get15() {
        return _15;
    }

    /**
     * @param _15 The 15
     */
    public void set15(String _15) {
        this._15 = _15;
    }

    /**
     * @return The _16
     */
    public String get16() {
        return _16;
    }

    /**
     * @param _16 The 16
     */
    public void set16(String _16) {
        this._16 = _16;
    }

    public String toString() {
        return get11() + ", " + get15() + ", " + get16();
    }


}
