package com.findmycoach.app.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.findmycoach.app.R;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.fragment.MyConnectionsFragment;
import com.findmycoach.app.fragment.MyScheduleFragment;
import com.findmycoach.app.fragment.NotificationsFragment;
import com.findmycoach.app.fragment_mentee.HomeFragment;
import com.findmycoach.app.reside_menu.ResideMenu;
import com.findmycoach.app.reside_menu.ResideMenuItem;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.io.IOException;

public class DashboardActivity extends FragmentActivity
        implements Callback, View.OnClickListener {

    private CharSequence mTitle;
    public int user_group;
    public static DashboardActivity dashboardActivity;
    private String[] navigationTitle;

    /* Below are the class variables which are mainly used GCM Client registration and Google Play Services device configuration check*/
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String GCM_SHARED_PREFERENCE = " gcm_preference";
    private static String REG_ID_SAVED_TO_SERVER;
    String regid;
    boolean regid_saved_to_server = false;
    /**
     * This is the project number we got
     * from the API Console,
     */
    String SENDER_ID = "944443780210";
    /**
     * Tag used on log messages.
     */
    static final String TAG = "Google Play Services status:";
    static final String TAG1 = "GCMCommunication";
    static final String TAG2 = "FMC-GCM";
    GoogleCloudMessaging gcm;
    Context context;

    int fragment_to_launch_from_notification = 0;  ///  On a tap over Push notification, then it will be used to identify which operation to perform
    int group_push_notification = 0;      /// it will identify push notification for which type of user.

    /**Related to reside menu*/
    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemNotification;
    private ResideMenuItem itemConnection;
    private ResideMenuItem itemSchedule;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardActivity = this;


        String userId = StorageHelper.getUserDetails(this, getResources().getString(R.string.user_id));
        String newUser = StorageHelper.getUserDetails(this, getResources().getString(R.string.new_user));

        try {
            user_group = Integer.parseInt(StorageHelper.getUserGroup(DashboardActivity.this, "user_group"));
            if(userId != null && newUser != null && userId.equals(newUser.split("#")[1])){
                String authToken = StorageHelper.getUserDetails(this, getResources().getString(R.string.auth_token));
                RequestParams requestParams = new RequestParams();
                Log.d(TAG, "Stored User Id:" + userId);
                Log.d(TAG, "auth_token" + authToken);
                requestParams.add("id", userId);
                requestParams.add("user_group", user_group+"");
                NetworkClient.getProfile(this, requestParams, authToken, this, 4);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logout();
            return;
        }
        Log.e("FMC - user_group", "" + user_group);
        setContentView(R.layout.activity_dashboard);

        context = getApplicationContext();
        REG_ID_SAVED_TO_SERVER = getResources().getString(R.string.reg_id_saved_to_server);

        /*Creating folder for media storage*/
        StorageHelper.createAppMediaFolders(this);

        fragment_to_launch_from_notification = getIntent().getIntExtra("fragment", 0);
        group_push_notification = getIntent().getIntExtra("group", 0);

        if (fragment_to_launch_from_notification == 0) {
            // Check device for Play Services APK.
            if (checkPlayServices()) {
                // If this check succeeds, proceed with normal processing.
                // Otherwise, prompt user to get valid Play Services APK.
                gcm = GoogleCloudMessaging.getInstance(this);
                regid = getRegistrationId(context);

                regid_saved_to_server = getRegistrationSaveStat(context);

                if (regid.isEmpty() || !regid_saved_to_server) {
                    registerInBackground();
                }

                if (StorageHelper.getUserDetails(this, "terms") == null || !StorageHelper.getUserDetails(this, "terms").equals("yes")) {
                    showTermsAndConditions();
                }
                initialize();
            } else {
                Toast.makeText(DashboardActivity.this, getResources().getString(R.string.google_play_services_not_supported), Toast.LENGTH_LONG).show();
                Log.i(TAG, "No valid Google Play Services APK found.");
            }

        } else {
            initialize();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.drawer)
            resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
        return true;
    }



    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        Log.d(TAG2, "GCM Registration previously saved:" + registrationId);
        //Toast.makeText(DashboardActivity.this,"GCM Registration previously saved:"+registrationId,Toast.LENGTH_LONG).show();
        if (registrationId.isEmpty()) {
            Log.i(TAG1, "Registration not found for GCM client");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG1, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * this method is checking whether reg_id is previously saved to server or not
     */
    private boolean getRegistrationSaveStat(Context context) {
        boolean b = StorageHelper.getGcmRegIfSentToServer(context, REG_ID_SAVED_TO_SERVER);
        Log.d(TAG2, "Registration id available to app server: " + b);
        return b;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {

        /*return getSharedPreferences(DashboardActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);*/
        return getSharedPreferences(GCM_SHARED_PREFERENCE,
                Context.MODE_PRIVATE);
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new StoreGcmRegistrationId().execute();
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        if(calledApiValue == 4){
            if(user_group == 2){
                ProfileResponse response = (ProfileResponse) object;
                Intent intent = new Intent(this, EditProfileActivityMentee.class);
                intent.putExtra("user_info", new Gson().toJson(response.getData()));
                startActivity(intent);
            }else if(user_group == 3){
                Response response = (Response) object;
                Intent intent = new Intent(this, EditProfileActivityMentor.class);
                intent.putExtra("user_info", new Gson().toJson(response.getData()));
                startActivity(intent);
            }
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {

    }


    class StoreGcmRegistrationId extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID = " + regid;
                Log.d(TAG, "Registration Id:" + regid);

                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = getResources().getString(R.string.error) + ex.getMessage();

            } catch (Exception e) {
                Log.d(TAG, "Exception occurred while doing GCM registration:" + e);
            }
            return msg;

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            sendRegistrationIdToBackend();
        }

        private void sendRegistrationIdToBackend() {
            // Your implementation here.
            String user_id = StorageHelper.getUserDetails(DashboardActivity.this, "user_id");
            String authToken = StorageHelper.getUserDetails(DashboardActivity.this, "auth_token");
            RequestParams requestParams = new RequestParams();
            Log.d("user_id:", user_id);
            Log.d("registration_id:", regid);
            requestParams.add("user_id", user_id);
            requestParams.add("registration_id", regid);
            requestParams.add("user_group", user_group + "");


            NetworkClient.registerGcmRegistrationId(DashboardActivity.this, requestParams, authToken, new Callback() {
                @Override
                public void successOperation(Object object, int statusCode, int calledApiValue) {
                    Log.d(TAG2, "Inside sendRegistrationIdToBackend callback successOperation method:" + object.toString());
                }

                @Override
                public void failureOperation(Object object, int statusCode, int calledApiValue) {
                    Log.d(TAG2, "Inside sendRegistrationIdToBackend callback failureOperation method:" + object.toString());
                }
            }, 31);
        }

        private void storeRegistrationId(Context context, String regId) {
            final SharedPreferences prefs = getGCMPreferences(context);
            int appVersion = getAppVersion(context);
            Log.i(TAG, "Saving regId on app version " + appVersion);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROPERTY_REG_ID, regId);
            editor.putInt(PROPERTY_APP_VERSION, appVersion);
            editor.commit();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (fragment_to_launch_from_notification == 0) {
            if (!checkPlayServices()) {
                Toast.makeText(DashboardActivity.this, getResources().getString(R.string.google_play_services_not_supported), Toast.LENGTH_LONG).show();
                Log.i(TAG, "No valid Google Play Services APK found.");
            }
        } else {

            if (Integer.parseInt(StorageHelper.getUserGroup(DashboardActivity.this, "user_group")) == group_push_notification) {
                user_group = Integer.parseInt(StorageHelper.getUserGroup(DashboardActivity.this, "user_group"));

                ResideMenuItem item = null;

                switch (fragment_to_launch_from_notification) {
                    case 1:case 2:case 3:
                        item = itemNotification;
                        break;
                    case 7:case 8:case 9:
                        item = itemConnection;
                        break;
                    case 4:case 5:case 6:
                        item = itemSchedule;
                        break;
                }
                fragment_to_launch_from_notification = 0;

                if(resideMenu == null)
                    setUpMenu(item);
                else if(item != null)
                    item.callOnClick();

                setTitle(mTitle);
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.switch_login), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not google play services supported.");
                //finish();
            }
            return false;
        }
        return true;
    }


    private void showTermsAndConditions() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.t_and_c));
        ScrollView scrollView = new ScrollView(this);
        final TextView contentView = new TextView(this);
        contentView.setText(getResources().getString(R.string.terms));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(8, 8, 8, 8);
        scrollView.addView(contentView);
        scrollView.setLayoutParams(params);
        alertDialog.setView(scrollView);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateTermsAndConditionsStatus();
                    }
                }
        );
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        logout();
                    }
                }
        );
        alertDialog.show();
    }

    private void logout() {
        String loginWith = StorageHelper.getUserDetails(this, "login_with");
        if (loginWith == null || loginWith.equals("G+")) {
            LoginActivity.doLogout = true;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        StorageHelper.clearUser(this);
        StorageHelper.clearUserPhone(this);
        fbClearToken();
        this.finish();
        startActivity(new Intent(this, LoginActivity.class));
        NetworkManager.counter = 0;
    }

    private void updateTermsAndConditionsStatus() {
        StorageHelper.storePreference(this, "terms", "yes");
    }

    private void initialize() {
        navigationTitle = getResources().getStringArray(R.array.navigation_items);
        mTitle = getResources().getString(R.string.app_name);
        if(resideMenu == null)
            setUpMenu(null);
    }

    private void setUpMenu(ResideMenuItem item) {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome     = new ResideMenuItem(this, R.drawable.icon_home, navigationTitle[0]);
        itemNotification  = new ResideMenuItem(this, android.R.drawable.stat_notify_chat, navigationTitle[1]);
        itemConnection = new ResideMenuItem(this, R.drawable.icon_profile, navigationTitle[2]);
        itemSchedule = new ResideMenuItem(this, R.drawable.icon_calendar, navigationTitle[3]);
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, navigationTitle[4]);
        itemLogout = new ResideMenuItem(this, android.R.drawable.ic_menu_close_clear_cancel, navigationTitle[5]);

        itemHome.setOnClickListener(this);
        itemNotification.setOnClickListener(this);
        itemConnection.setOnClickListener(this);
        itemSchedule.setOnClickListener(this);
        itemSettings.setOnClickListener(this);
        itemLogout.setOnClickListener(this);
        resideMenu.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemNotification, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemConnection, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemSchedule, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemLogout, ResideMenu.DIRECTION_RIGHT);

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);


        if(item != null)
            item.callOnClick();
        else
            itemHome.callOnClick();

    }

    @Override
    public void onClick(View view) {
        int position = -1;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (user_group == 3) {
            if (view == itemHome) {
                fragmentTransaction.replace(R.id.container, new com.findmycoach.app.fragment_mentor.HomeFragment());
                position = 0;
            }
            else if (view == itemNotification) {
                fragmentTransaction.replace(R.id.container, new NotificationsFragment());
                position = 1;
            }
            else if (view == itemConnection) {
                fragmentTransaction.replace(R.id.container, new MyConnectionsFragment());
                position = 2;
            }
            else if (view == itemSchedule) {
                fragmentTransaction.replace(R.id.container, new MyScheduleFragment());
                position = 3;
            }
            fragmentTransaction.commit();
        }
        if (user_group == 2) {
            if (view == itemHome) {
                fragmentTransaction.replace(R.id.container, new HomeFragment());
                position = 0;
            }
            else if (view == itemNotification) {
                fragmentTransaction.replace(R.id.container, new NotificationsFragment());
                position = 1;
            }
            else if (view == itemConnection) {
                fragmentTransaction.replace(R.id.container, new MyConnectionsFragment());
                position = 2;
            }
            else if (view == itemSchedule) {
                fragmentTransaction.replace(R.id.container, new MyScheduleFragment());
                position = 3;
            }
            fragmentTransaction.commit();
        }

        if(position > -1)
            onSectionAttached(position);


        if (view == itemSettings)
            startActivity(new Intent(this, Settings.class));
        else if (view == itemLogout)
            logout();
        if(resideMenu != null && resideMenu.isOpened())
            resideMenu.closeMenu();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            setTitle(getResources().getString(R.string.app_name));
        }

        @Override
        public void closeMenu() {
            setTitle(mTitle);
        }
    };

    public void onSectionAttached(int number) {
        mTitle = navigationTitle[number];
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void fbClearToken() {
        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
            }
        } else {
            session = new Session(this);
            Session.setActiveSession(session);
            session.closeAndClearTokenInformation();
        }
    }
}
