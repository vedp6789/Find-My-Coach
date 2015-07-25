package com.findmycoach.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.findmycoach.app.R;

import java.io.File;
import java.util.Set;

/**
 * Created by prem on 1/1/15.
 */
public class StorageHelper {

    public static void storePreference(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public static void storeListOfCoachingSubCategories(Context context, Set<String> stringSet) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("area_of_coaching_set", stringSet);
        editor.commit();
    }

    public static Set<String> getListOfCoachingSubCategories(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getStringSet(key, null);
    }

    public static void storeClassTypePreference(Context context, int position) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("class_type", position);
        editor.commit();
    }

    public static int getClassTypePreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("class_type", -1);
    }

    public static void checkGcmRegIdSentToSever(Context context, String key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getGcmRegIfSentToServer(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }


    public static String getGridClickDetails(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }


    public static String getUserGroup(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static String getUserDetails(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static String addressInformation(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String information = preferences.getString(key, null);
        return information;
    }


    public static void clearUser(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("auth_token");
        editor.remove("user_id");
        editor.remove("user_email");
        editor.remove("phone_verified");
        editor.remove("user_local_address");
        editor.remove("user_city_state");
        editor.remove("user_zip_code");
        editor.remove("user_profile_whole_data");
//        editor.remove("terms");
        editor.remove("currency_code");

        if (StorageHelper.getUserGroup(context, "user_group").equals("2")) {
            editor.remove("training_location");

        }

        if (StorageHelper.getUserGroup(context, "user_group").equals("3")) {
            editor.remove("area_of_coaching_set");
            editor.remove("class_type");
        }
        editor.apply();
    }

    public static void clearUserPhone(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("phone_number");
        editor.apply();
    }

    public static void createAppMediaFolders(Context context) {
        final File path = Environment.getExternalStoragePublicDirectory(context.getString(R.string.stored_path));
        path.mkdirs();

        final File pathMedia = new File(path.getPath() + "/media");
        pathMedia.mkdirs();

        final File pathImages = new File(pathMedia.getPath() + "/images");
        pathImages.mkdirs();

        final File pathVideos = new File(pathMedia.getPath() + "/videos");
        pathVideos.mkdirs();

        storePreference(context, "image_path", pathImages.getPath());
//        Log.e("FMC", "StorageHelper : " + pathImages.getPath());
        storePreference(context, "video_path", pathVideos.getPath());
//        Log.e("FMC", "StorageHelper : " + pathVideos.getPath());
    }

    public static String getCurrency(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("currency_code", "");
    }

    public static void setCurrency(Context context, String currencyCode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString("currency_code", currencyCode).apply();
    }

    public static String getUserAddress(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("user_local_address", "") + " " + preferences.getString("user_city_state", "")
                + " " + preferences.getString("user_country", "") + " " + preferences.getString("user_zip_code", "");

    }

    public static void saveUserProfile(Context context, String data) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString("user_profile_whole_data", data).apply();
    }

    public static String getUserProfile(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("user_profile_whole_data", null);
    }

}
