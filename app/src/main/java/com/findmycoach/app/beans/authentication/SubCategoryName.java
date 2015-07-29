package com.findmycoach.app.beans.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ShekharKG on 29/7/15.
 */
public class SubCategoryName {

    @Expose
    boolean qualified;
    @SerializedName("name")
    @Expose
    String sub_category_name;

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public boolean isQualified() {
        return qualified;
    }

    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }
}
