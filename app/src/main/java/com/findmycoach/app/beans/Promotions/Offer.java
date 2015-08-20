package com.findmycoach.app.beans.Promotions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ved on 11/8/15.
 */
/*
* Offer class is used for different Promotions
*
* */
public class Offer implements Parcelable {

    private String id;
    private String promotion_title;
    private String promotion_type;
    private String discount_percentage;
    private String free_classes;/* This is actually number of free classes*/
    private String free_min_classes;  /* This is the number of classes mentee have to attend to get free classes*/
    private String user_id;
    private String is_active;

    @SerializedName("discount_number_of_classes")
    @Expose
    private String discount_over_classes;

    public Offer(Parcel parcel) {
        id = parcel.readString();
        promotion_title = parcel.readString();
        promotion_type = parcel.readString();
        discount_percentage = parcel.readString();
        free_classes = parcel.readString();
        free_min_classes = parcel.readString();
        user_id = parcel.readString();
        is_active = parcel.readString();
        discount_over_classes = parcel.readString();
    }

    public String getDiscount_over_classes() {
        return discount_over_classes;
    }

    public void setDiscount_over_classes(String discount_over_classes) {
        this.discount_over_classes = discount_over_classes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPromotion_title() {
        return promotion_title;
    }

    public void setPromotion_title(String promotion_title) {
        this.promotion_title = promotion_title;
    }

    public String getPromotion_type() {
        return promotion_type;
    }

    public void setPromotion_type(String promotion_type) {
        this.promotion_type = promotion_type;
    }

    public String getDiscount_percentage() {
        return discount_percentage;
    }

    public void setDiscount_percentage(String discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    public String getFree_classes() {
        return free_classes;
    }

    public void setFree_classes(String free_classes) {
        this.free_classes = free_classes;
    }

    public String getFree_min_classes() {
        return free_min_classes;
    }

    public void setFree_min_classes(String free_min_classes) {
        this.free_min_classes = free_min_classes;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.promotion_title);
        dest.writeString(this.promotion_type);
        dest.writeString(this.discount_percentage);
        dest.writeString(this.free_classes);
        dest.writeString(this.free_min_classes);
        dest.writeString(this.user_id);
        dest.writeString(this.is_active);
        dest.writeString(this.discount_over_classes);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };


}

