package com.findmycoach.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    public static final int NOTIFICATION_ID_NEW_SCHEDULE = 4;
    public static final int NOTIFICATION_ID_TEXT_CHAT_MENTOR = 7;
    public static final int NOTIFICATION_ID_IMAGE_CHAT_MENTOR = 8;
    public static final int NOTIFICATION_ID_VIDEO_CHAT_MENTOR = 9;

    public static final int NOTIFICATION_ID_REQUEST_ACCEPTED = 2;
    public static final int NOTIFICATION_ID_REQUEST_REJECTED = 3;
    public static final int NOTIFICATION_ID_SCHEDULE_CONFIRMED = 5;
    public static final int NOTIFICATION_ID_SCHEDULE_REJECTED = 6;
    public static final int NOTIFICATION_ID_TEXT_CHAT_MENTEE = 7;
    public static final int NOTIFICATION_ID_IMAGE_CHAT_MENTEE = 8;
    public static final int NOTIFICATION_ID_VIDEO_CHAT_MENTEE = 9;

    int opcode = 0;
    String user_group = null;
    JSONObject jsonObject = null;
    JSONObject jsonObject1 = null;
    JSONObject jsonObject2 = null;
    JSONObject jsonObject_receiver = null;
    private String receiver_id;
    String contentTitle = null;
    String message = null;
    String f_name = null;
    String photograph_url = null;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    PendingIntent contentIntent;

    static String TAG = "GCM-FMC-Service-Mentor";

    public GcmFmcService() {
        super("GcmFmcService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String messageType = gcm.getMessageType(intent);
            Log.d(TAG, "String getting from gcm cloud: " + messageType);
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
                    sendNotification(extras.get("data").toString());

                }
            }
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendNotification(String push_message) {

        String userToken = StorageHelper.getUserDetails(this, "auth_token");
        Log.d(TAG, "push message: " + push_message.toString());

        //String phnVerified = StorageHelper.getUserDetails(this, "phone_verified");  // commented as it is not working from server side


        if (userToken != null /*&& phnVerified != null*/) {
            contentIntent = null;
            try {
                jsonObject = new JSONObject(push_message);
                opcode = jsonObject.getInt("opcode");
                Log.d(TAG, "opcode getting received: " + opcode);
                user_group = jsonObject.getString("user_group");
                jsonObject1 = new JSONObject(jsonObject.getString("data"));
                Log.d(TAG, "json object data" + jsonObject1.toString());
                //message = jsonObject1.getString("message");
                jsonObject2 = new JSONObject(jsonObject1.getString("user"));
                jsonObject_receiver = jsonObject1.getJSONObject("receiver");
                receiver_id = jsonObject_receiver.getString("id");
                Log.d(TAG, "User Object" + jsonObject2.toString());
                f_name = jsonObject2.getString("first_name");
                photograph_url=jsonObject2.getString("photograph");



                if (Integer.parseInt(user_group) == 3 && StorageHelper.getUserGroup(getApplicationContext(), "user_group").equals(user_group) && StorageHelper.getUserDetails(getApplicationContext(),"user_id").equals(receiver_id)) {
                    if (opcode == 1) {
                        Log.d(TAG, "Inside opcode match for opcode 1");
                        contentTitle = "Find My Coach";
                        message = getResources().getString(R.string.conn_req_push) + f_name;
                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.putExtra("fragment", 1);
                        intent.putExtra("group", 3);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_CONNECTION_REQ);
                    }
                    if (opcode == 4) {
                        contentTitle = "Find My Coach";
                        message = getResources().getString(R.string.schedule_request_push_notification) +" "+ f_name;
                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.putExtra("fragment", 4);
                        //String slot_type = jsonObject1.getString("slot_type");
                        //String event_id = jsonObject1.getString("event_id");
                        //String student_id = jsonObject1.getString("student_id");
                        //intent.putExtra("slot_type", slot_type);
                        //intent.putExtra("event_id", event_id);
                        //intent.putExtra("student_id", student_id);
                        intent.putExtra("group", 3);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_NEW_SCHEDULE);
                    }
                    if (opcode == 7) {
                        contentTitle = "Find My Coach";
                        message = getResources().getString(R.string.push_text_message);
                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.putExtra("fragment", 7);
                        intent.putExtra("group", 3);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        contentIntent = PendingIntent.getActivity(this,0, intent, 0);
                        createNotification(NOTIFICATION_ID_TEXT_CHAT_MENTOR);
                    }
                    if (opcode == 8) {
                        contentTitle = "Find My Coach";
                        message = getResources().getString(R.string.push_image_message);

                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.putExtra("fragment", 8);
                        intent.putExtra("group", 3);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_IMAGE_CHAT_MENTOR);

                    }
                    if (opcode == 9) {
                        contentTitle = "Find My Coach";
                        message = getResources().getString(R.string.push_video_message);
                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.putExtra("fragment", 9);
                        intent.putExtra("group", 3);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);

                        createNotification(NOTIFICATION_ID_VIDEO_CHAT_MENTOR);
                    }
                }

                if (Integer.parseInt(user_group) == 2 && StorageHelper.getUserGroup(getApplicationContext(), "user_group").equals(user_group) && StorageHelper.getUserDetails(getApplicationContext(),"user_id").equals(receiver_id)) {
                    if (opcode == 2) {
                        contentTitle = "Chizzle";
                        message = f_name+" "+getResources().getString(R.string.connection_request_accepted);
                        Intent intent = new Intent(this, com.findmycoach.app.activity.DashboardActivity.class);
                        intent.putExtra("fragment", 2);
                        intent.putExtra("group", 2);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_REQUEST_ACCEPTED);
                    }
                    if (opcode == 3) {
                        contentTitle = "Chizzle";
                        message =f_name+" "+ getResources().getString(R.string.connection_request_rejected);
                        Intent intent = new Intent(this, com.findmycoach.app.activity.DashboardActivity.class);
                        intent.putExtra("fragment", 3);
                        intent.putExtra("group", 2);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_REQUEST_REJECTED);
                    }
                    if (opcode == 5) {
                        contentTitle = "Chizzle";
                        message = getResources().getString(R.string.schedule_confirmation) +" "+ f_name;
                        Intent intent = new Intent(this, com.findmycoach.app.activity.DashboardActivity.class);
                        intent.putExtra("fragment", 5);
                        intent.putExtra("group", 2);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_SCHEDULE_CONFIRMED);
                    }
                    if (opcode == 6) {
                        contentTitle = "Chizzle";
                        message = getResources().getString(R.string.schedule_rejection) +" "+ f_name;
                        Intent intent = new Intent(this, com.findmycoach.app.activity.DashboardActivity.class);
                        intent.putExtra("fragment", 6);
                        intent.putExtra("group", 2);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_SCHEDULE_REJECTED);
                    }
                    if (opcode == 7) {
                        Log.d(TAG, "opcode 7 for mentee");
                        contentTitle = "Chizzle";
                        message = getResources().getString(R.string.push_text_message);
                        Intent intent = new Intent(this, com.findmycoach.app.activity.DashboardActivity.class);
                        intent.putExtra("fragment", 7);
                        intent.putExtra("group", 2);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

                        //contentIntent = PendingIntent.getActivity(this,0,intent, 0);
                        createNotification(NOTIFICATION_ID_TEXT_CHAT_MENTEE);
                    }
                    if (opcode == 8) {
                        contentTitle = "Chizzle";
                        message = getResources().getString(R.string.push_image_message);
                        Intent intent = new Intent(this, com.findmycoach.app.activity.DashboardActivity.class);
                        intent.putExtra("fragment", 8);
                        intent.putExtra("group", 2);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
                        createNotification(NOTIFICATION_ID_IMAGE_CHAT_MENTEE);
                    }
                    if (opcode == 9) {
                        contentTitle = "Chizzle";
                        message = getResources().getString(R.string.push_video_message);
                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.putExtra("fragment", 9);
                        intent.putExtra("group", 2);
                        intent.setAction("" + Math.random());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        contentIntent = PendingIntent.getActivity(this, 0,
                                intent, 0);
                        createNotification(NOTIFICATION_ID_VIDEO_CHAT_MENTEE);

                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Log.d(TAG, "Inside GcmFmcService of Mentor app, User in not login, notification not allowed for pop up");
        }


    }

    void createNotification(int notification_id) {
        try {
            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            if (contentIntent != null) {
                Log.d(TAG, "contentIntent not found null ");

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(contentTitle)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(message))
                                .setAutoCancel(true)
                                .setContentText(message);


                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(notification_id, mBuilder.build());
            } else {
                Log.d(TAG, "contentIntent found null ");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
