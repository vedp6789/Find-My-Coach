package com.findmycoach.app.util;

import android.content.Context;

import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Country;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by ShekharKG on 3/8/15.
 */
public class MetaData {

    public static List<Country> getCountryObject(Context context){
        DataBase dataBase = DataBase.singleton(context);
        String categoryData = dataBase.getAll();
        Category categoryFromDb = new Gson().fromJson(categoryData, Category.class);
        return categoryFromDb.getCountries();
    }

    public static int otpPosition(Context context){
        DataBase dataBase = DataBase.singleton(context);
        String categoryData = dataBase.getAll();
        Category categoryFromDb = new Gson().fromJson(categoryData, Category.class);
        return categoryFromDb.getOtpPosition();
    }

    public static int otpLength(Context context){
        DataBase dataBase = DataBase.singleton(context);
        String categoryData = dataBase.getAll();
        Category categoryFromDb = new Gson().fromJson(categoryData, Category.class);
        return categoryFromDb.getOtpLength();
    }

}
