package com.findmycoach.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.NetworkClient;
import com.loopj.android.http.RequestParams;

/**
 * Created by prem on 11/3/15.
 */
public class SplashActivity extends Activity implements Callback{

    private DataBase dataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getDataFromServer();
    }


    /** Updating app data from server */
    private void getDataFromServer() {
        /** Checking if sub categories are already present */
        dataBase = DataBase.singleton(this);
        Category categoryFromDb = dataBase.selectAllSubCategory();


        /*

        *//** If sub category is not present then call api to get *//*
        if(categoryFromDb.getData().size() < 1)
            getCategories();

        *//** Subcategories is present *//*
        else
            runHoldThread();

        */


        runHoldThread();
    }

    /** Thread to hold splash screen */
    private void runHoldThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(getResources().getInteger(R.integer.splash_screen_hold_duration));
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                } catch (InterruptedException ex) {
                    Log.i("FMC", ex.getMessage());
                }
            }
        };
        thread.start();
    }

    /** Get Sub Categories */
    private void getCategories() {
        /* TODO remove hard coded auth token */
        NetworkClient.getCategories(this, new RequestParams(), "916bb76e90beb6f87a97e6d3de1daebff6859d58", this, 34);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        /** Caching subcategories into database */
        dataBase.insertData((Category) object);
        runHoldThread();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        runHoldThread();
    }
}
