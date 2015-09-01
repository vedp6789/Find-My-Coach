package com.findmycoach.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.util.AppFonts;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.ChizzleConstants;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.NetworkClient;
import com.loopj.android.http.RequestParams;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by prem on 11/3/15.
 */
public class SplashActivity extends Activity implements Callback {

    private DataBase dataBase;
    private boolean isStart;
    private GifImageView gifImageView;
    private GifDrawable gifFromResource = null;
    private Thread thread;
    private boolean isThreadRunCompleted, isNetworkCallSuccess;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        AppFonts.HelveticaNeue = Typeface.createFromAsset(getAssets(), "HelveticaNeue.dfont");
        AppFonts.HelveticaNeue = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Medium.otf");
        AppFonts.HelveticaNeueMedium = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Medium.otf");
        isThreadRunCompleted = false;
        isNetworkCallSuccess = false;
        setContentView(R.layout.activity_splash);
        gifImageView = (GifImageView) findViewById(R.id.app_logo_anim);
        try {
            gifFromResource = new GifDrawable(getResources(), R.drawable.animated_logo1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifImageView.setImageDrawable(gifFromResource);


        runHoldThread();
        //isStart = true;
        getDataFromServer();
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
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        Log.e(ChizzleConstants.TAG, "Splash thread started ");
                        wait(getResources().getInteger(R.integer.splash_screen_hold_duration));
                        Log.e(ChizzleConstants.TAG, "Splash thread after wait ");
                        isThreadRunCompleted = true;
                        if (isNetworkCallSuccess) {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            gifFromResource.stop();
                            finish();
                        }
                        //isStart = false;

                    }
                } catch (InterruptedException ex) {
                    Log.i("FMC", ex.getMessage());
                }
            }
        };
        thread.start();
        //thread.interrupt();
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

        Log.e(ChizzleConstants.TAG, "saved metadata :" + dataBase.getAll());
        isNetworkCallSuccess = true;
        if (isThreadRunCompleted) {
            gifFromResource.stop();
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
        //    runHoldThread();

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        //  runHoldThread();
        isNetworkCallSuccess = true;
        if(isThreadRunCompleted){
            Toast.makeText(SplashActivity.this,getResources().getString(R.string.problem_in_connection_server),Toast.LENGTH_LONG).show();
            gifFromResource.stop();
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            finish();
        }else {
            Toast.makeText(SplashActivity.this,getResources().getString(R.string.problem_in_connection_server),Toast.LENGTH_LONG).show();
        }


    }
}
