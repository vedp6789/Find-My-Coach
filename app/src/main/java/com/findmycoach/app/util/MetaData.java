package com.findmycoach.app.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Country;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShekharKG on 3/8/15.
 */
public class MetaData {

    public static List<Country> getCountryObject(Context context) {
        DataBase dataBase = DataBase.singleton(context);
        String categoryData = dataBase.getAll();
        Category categoryFromDb = new Gson().fromJson(categoryData, Category.class);
        if (categoryFromDb != null && categoryFromDb.getCountries() != null)
            return categoryFromDb.getCountries();
        else
            return new ArrayList<Country>();
    }

    public static String getCurrencySymbol(String iso, Context context) {
        Log.e("FMC : MetaData : ", iso);
        for (Country country : getCountryObject(context)) {
            if (country.getIso().trim().equalsIgnoreCase(iso.trim()))
                return country.getCurrencySymbol();
        }
        return "";
    }

    public static String countryCode(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getSimCountryIso().toUpperCase();
    }

    public static int otpPosition(Context context) {
        DataBase dataBase = DataBase.singleton(context);
        String categoryData = dataBase.getAll();
        Category categoryFromDb = new Gson().fromJson(categoryData, Category.class);
        return categoryFromDb.getOtpPosition();
    }

    public static int otpLength(Context context) {
        DataBase dataBase = DataBase.singleton(context);
        String categoryData = dataBase.getAll();
        Category categoryFromDb = new Gson().fromJson(categoryData, Category.class);
        return categoryFromDb.getOtpLength();
    }

}
