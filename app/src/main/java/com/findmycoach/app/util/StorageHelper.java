package com.findmycoach.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.findmycoach.app.R;

import java.io.File;

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

    public static String getUserGroup(Context context, String key){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        String user_group= preferences.getString(key , null);
        return user_group;
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

    public static void createAppMediaFolders(Context context) {
        final File path = Environment.getExternalStoragePublicDirectory(context.getString(R.string.stored_path));
        path.mkdirs();

        final File pathMedia = new File(path.getPath() + "/media");
        pathMedia.mkdirs();

        final File pathImages = new File(pathMedia.getPath() + "/images");
        pathImages.mkdirs();

        final File pathVideos = new File(pathMedia.getPath() + "/videos");
        pathVideos.mkdirs();

        storePreference(context,"image_path", pathImages.getPath());
        Log.e("FMC", "StorageHelper : " + pathImages.getPath());
        storePreference(context,"video_path", pathVideos.getPath());
        Log.e("FMC", "StorageHelper : " + pathVideos.getPath());
    }

}