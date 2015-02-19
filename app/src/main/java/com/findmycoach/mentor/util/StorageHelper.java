package com.findmycoach.mentor.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by prem on 1/1/15.
 */
public class StorageHelper {

    public static void storePreference(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void checkGcmRegIdSentToSever(Context context, String key, boolean value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getGcmRegIfSentToServer(Context context, String key){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        boolean reg_saved= preferences.getBoolean(key , false);
        return reg_saved;
    }

    public static String getUserDetails(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userToken = preferences.getString(key, null);
        return userToken;
    }

    public static void clearUser(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("auth_token");
        editor.remove("user_id");
        editor.remove("user_email");
        editor.remove("phone_verified");
        editor.commit();
    }

    public static void clearUserPhone(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("phone_number");
        editor.commit();
    }

}
