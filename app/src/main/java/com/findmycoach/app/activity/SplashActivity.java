package com.findmycoach.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import com.findmycoach.app.R;
import com.findmycoach.app.util.AppFonts;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.NetworkClient;
import com.loopj.android.http.RequestParams;

import java.util.TimeZone;

/**
 * Created by prem on 11/3/15.
 */
public class SplashActivity extends Activity implements Callback {

    private DataBase dataBase;
    private boolean isStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        AppFonts.HelveticaNeue = Typeface.createFromAsset(getAssets(), "HelveticaNeue.dfont");
        AppFonts.HelveticaNeue = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Medium.otf");
        AppFonts.HelveticaNeueMedium = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Medium.otf");

        setContentView(R.layout.activity_splash);

        isStart = true;
        getDataFromServer();

        TimeZone tz = TimeZone.getDefault();
        Log.e("VED", tz.getDisplayName(false, TimeZone.SHORT));

    }

    /**
     * Updating app data from server
     */
    private void getDataFromServer() {
        /** Checking if sub categories are already present */
        dataBase = DataBase.singleton(this);
        getCategories();
    }

    /**
     * Thread to hold splash screen
     */
    private void runHoldThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(getResources().getInteger(R.integer.splash_screen_hold_duration));
                        if (isStart)
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        isStart = false;
                        finish();
                    }
                } catch (InterruptedException ex) {
                    Log.i("FMC", ex.getMessage());
                }
            }
        };
        thread.start();
    }

    /**
     * Get Sub Categories
     */
    private void getCategories() {
        NetworkClient.getSubCategories(this, new RequestParams(), null, this, 34);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        /** Caching subcategories into database */
        dataBase.clearDatabase();
        dataBase.insertData((String) object);
        runHoldThread();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        runHoldThread();
    }
}
