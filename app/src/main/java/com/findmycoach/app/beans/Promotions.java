package com.findmycoach.app.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.analytics.ecommerce.Promotion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ved on 10/8/15.
 */
public class Promotions implements Parcelable {

    List<Offer> promotions;


    public Promotions(Parcel parcel) {
        parcel.readTypedList(this.promotions, Offer.CREATOR);
    }

    public List<Offer> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Offer> promotions) {
        this.promotions = promotions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.promotions);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Promotions createFromParcel(Parcel in) {
            return new Promotions(in);
        }

        public Promotions[] newArray(int size) {
            return new Promotions[size];
        }
    };

}


/*
* Offer class is used for different Promotions
*
* */
class Offer implements Parcelable {

    private String id;
    private String promotion_title;
    private String promotion_type;
    private String discount_percentage;
    private String trial_classes;
    private String trial_min_classes;
    private String user_id;
    private String is_active;

    public Offer(Parcel parcel) {
        id = parcel.readString();
        promotion_title = parcel.readString();
        promotion_type = parcel.readString();
        discount_percentage = parcel.readString();
        trial_classes = parcel.readString();
        trial_min_classes = parcel.readString();
        user_id = parcel.readString();
        is_active = parcel.readString();
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

    public String getTrial_classes() {
        return trial_classes;
    }

    public void setTrial_classes(String trial_classes) {
        this.trial_classes = trial_classes;
    }

    public String getTrial_min_classes() {
        return trial_min_classes;
    }

    public void setTrial_min_classes(String trial_min_classes) {
        this.trial_min_classes = trial_min_classes;
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
        dest.writeString(this.trial_classes);
        dest.writeString(this.trial_min_classes);
        dest.writeString(this.user_id);
        dest.writeString(this.is_active);
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
