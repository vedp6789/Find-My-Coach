package com.findmycoach.app.beans.Promotions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ved on 10/8/15.
 */
public class Promotions implements Parcelable {

    @SerializedName("data")
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


