package com.findmycoach.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.util.StorageHelper;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by praka_000 on 2/13/2015.
 */
public class GcmFmcService extends IntentService {
    public static final int NOTIFICATION_ID_CONNECTION_REQ = 1;
    public static final int NOTIFICATION_ID_NEW_SCHEDULE = 2;
    public static final int NOTIFICATION_ID_TEXT_CHAT = 3;
    public static final int NOTIFICATION_ID_IMAGE_CHAT = 4;
    public static final int NOTIFICATION_ID_VIDEO_CHAT = 5;

    int opcode = 0;
    String user_group = null;
    JSONObject jsonObject=null;
    JSONObject jsonObject1=null;
    JSONObject jsonObject2=null;
    String contentTitle = null;
    String message = null;
    String f_name = null;
    String url = null;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    PendingIntent contentIntent;

    static String TAG = "GCM-FMC-Service-Mentor";

    public GcmFmcService() {
        super("GcmFmcService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        Log.d(TAG,"String getting from gcm cloud: "+messageType);
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.d(TAG, "MESSAGE_TYPE_SEND_ERROR: " + "error message from GCM server!");
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " +extras.toString());
                Log.d(TAG, "MESSAGE_TYPE_DELETED: " + "message from GCM server!");
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                Log.i(TAG, "Received: " + extras.toString());
                sendNotification(extras.get("data").toString());
                try {
                    JSONObject jsonObject=new JSONObject((String) extras.get("data"));
                    JSONObject jsonObject2 = jsonObject.getJSONObject("data");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {

        String userToken = StorageHelper.getUserDetails(this, "auth_token");
        //String phnVerified = StorageHelper.getUserDetails(this, "phone_verified");  // commented as


        if (userToken != null /*&& phnVerified != null*/) {
            contentIntent=null;
            try {
                jsonObject = new JSONObject(msg);
                opcode = jsonObject.getInt("opcode");
                user_group = jsonObject.getString("user_group");
                jsonObject1 = new JSONObject(jsonObject.getString("data"));
                Log.d(TAG,"json object data"+ jsonObject1.toString());

                jsonObject2= new JSONObject(jsonObject1.getString("user"));
                Log.d(TAG,"User Object"+jsonObject2.toString());
                f_name=jsonObject2.getString("first_name");


                if (Integer.parseInt(user_group) == 3) {
                    if (opcode == 1) {
                        Log.d(TAG,"Inside opcode match for opcode 1");
                        contentTitle = "Find My Coach";
                        message="Hi, connection request from "+f_name;
                        Intent intent=new Intent(this,DashboardActivity.class);
                        intent.putExtra("fragment",1);
                        intent.setAction("" + Math.random());


                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_CONNECTION_REQ);
                    }
                    if (opcode == 4) {
                        contentTitle = "Find My Coach";
                        message="Hi, you have a new class schedule from "+f_name;
                        Intent intent=new Intent(this,DashboardActivity.class);
                        intent.putExtra("fragment",2);
                        intent.setAction("" + Math.random());


                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_NEW_SCHEDULE);
                    }
                    if (opcode == 7) {
                        contentTitle = "Find My Coach";
                        message = "Hi, "+f_name+ " has sent a text message.";
                        Intent intent=new Intent(this,DashboardActivity.class);
                        intent.putExtra("fragment",3);
                        intent.setAction("" + Math.random());

                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_TEXT_CHAT);
                    }
                    if (opcode == 8) {
                        contentTitle = "Find My Coach";
                        message = "Hi, "+f_name+" has sent an image message.";
                        Intent intent=new Intent(this,DashboardActivity.class);
                        intent.putExtra("fragment",4);
                        intent.setAction("" + Math.random());


                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_IMAGE_CHAT);

                    }
                    if (opcode == 9) {
                        contentTitle = "Find My Coach";
                        message = "Hi, "+f_name+ " has sent a video message." ;
                        Intent intent=new Intent(this,DashboardActivity.class);
                        intent.putExtra("fragment",5);
                        intent.setAction("" + Math.random());

                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);

                        createNotification(NOTIFICATION_ID_VIDEO_CHAT);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }else{
            Log.d(TAG,"Inside GcmFmcService of Mentor app, User in not login, notification not allowed for pop up");
        }


    }

    void createNotification(int notification_id){
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(contentTitle)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setContentText(message);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notification_id, mBuilder.build());
    }

}
