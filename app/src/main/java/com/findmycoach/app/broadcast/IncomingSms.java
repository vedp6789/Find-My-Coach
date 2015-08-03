package com.findmycoach.app.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.findmycoach.app.activity.ValidatePhoneActivity;
import com.findmycoach.app.util.MetaData;

/**
 * Created by ShekharKG on 6/2/2015.
 */
public class IncomingSms extends BroadcastReceiver {

    final String TAG = "IncomingSms";

    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i(TAG, "senderNum: " + senderNum + "; message: " + message);
                    String OTP = "";

                    try {
                        int temp = Integer.parseInt(message.split(" ")[MetaData.otpPosition(context)]);
                        OTP = String.valueOf(temp);
                    } catch (Exception ignored) {
                    }

                    if (OTP.length() == MetaData.otpLength(context) && ValidatePhoneActivity.validatePhoneActivity != null) {
                        ValidatePhoneActivity.validatePhoneActivity.getOtpFromMsg(OTP);
                    }


                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception smsReceiver" + e);

        }
    }
}