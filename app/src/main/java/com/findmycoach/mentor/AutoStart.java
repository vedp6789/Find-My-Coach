package com.findmycoach.mentor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.findmycoach.mentor.activity.LoginActivity;

/**
 * Created by praka_000 on 2/24/2015.
 */
public class AutoStart extends BroadcastReceiver {

        LoginActivity loginActivity = new LoginActivity();


        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
            {
                loginActivity.startAlarm();
            }
        }

}
