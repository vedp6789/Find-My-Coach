package com.findmycoach.app.util;


import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.findmycoach.app.fragment_mentee.HomeFragment;

/**
 * Created by ShekharKG on 8/5/15.
 */
public class GetLocationAsync extends AsyncTask<Void, Void, Void> {

    private Fragment fragment;
    private Activity activity;

    public GetLocationAsync(Fragment fragment, Activity activity) {
        this.fragment = fragment;
        this.activity = activity;
        Log.e("GetLocationAsync", "Constructor");
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }


    @Override
    protected void onPostExecute(Void s) {

        String loc = NetworkManager.getCurrentLocation(activity);

        Log.e("GetLocationAsync", "onPostExecute " + loc);

        if(fragment instanceof HomeFragment){
            ((HomeFragment) fragment).updateLocationFromAsync(loc);
        }

    }
}
