package com.findmycoach.mentor;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;


/**
 * Created by praka_000 on 2/13/2015.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver{
    private static final String TAG="FMC:";

    @Override
    public void onReceive(Context context, Intent intent) {
// Explicitly specify that GcmIntentService will handle the intent.
        String x=intent.getStringExtra("NOT_GCM");


        if(x!=null && x.length() > 0){
            if(x.equals("true")){

            }
        }else{
            Log.d(TAG, "Inside GCM Broadcast receiver");
            ComponentName comp = new ComponentName(context.getPackageName(),
                    GcmFmcService.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }







    }
}
