package com.findmycoach.app.activity;

import android.app.ActionBar;
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
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.findmycoach.app.R;
import com.findmycoach.app.fragment.MyConnectionsFragment;
import com.findmycoach.app.fragment.MyScheduleFragment;
import com.findmycoach.app.fragment.NavigationDrawerFragment;
import com.findmycoach.app.fragment.NotificationsFragment;
import com.findmycoach.app.fragment_mentee.HomeFragment;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class DashboardActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private String userToken;
    public int user_group;
    public static DashboardActivity dashboardActivity;

    /* Below are the class variables which are mainly used GCM Client registration and Google Play Services device configuration check*/
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String EXTRA_MESSAGE = "message";
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
    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    private ActionBar actionBar;
    public static TextView customTitle;
    private

    int fragment_to_launch_from_notification = 0;  ///  On a tap over Push notification, then it will be used to identify which operation to perform
    int group_push_notification = 0;      /// it will identify push notification for which type of user.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardActivity = this;

        try{
            user_group = Integer.parseInt(StorageHelper.getUserGroup(DashboardActivity.this, "user_group"));
        }catch (Exception e){
            e.printStackTrace();
            logout();
            return;
        }
        Log.e("FMC - user_group",""+user_group);
        setContentView(R.layout.activity_dashboard);

        context = getApplicationContext();
        REG_ID_SAVED_TO_SERVER = getResources().getString(R.string.reg_id_saved_to_server);

        /*Creating folder for media storage*/
        StorageHelper.createAppMediaFolders(this);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("fragment")) {
            int fragment_to_start_val = getIntent().getExtras().getInt("fragment");
        } else {
        }

        fragment_to_launch_from_notification = getIntent().getIntExtra("fragment", 0);
        group_push_notification= getIntent().getIntExtra("group",0);

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
                initialize();

                if (StorageHelper.getUserDetails(this, "terms") == null) {
                    showTermsAndConditions();
                }
            } else {
                Toast.makeText(DashboardActivity.this, getResources().getString(R.string.google_play_services_not_supported), Toast.LENGTH_LONG).show();
                Log.i(TAG, "No valid Google Play Services APK found.");
            }

        } else {
            initialize();
        }


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
            requestParams.add("user_group",user_group+"");


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
        if (fragment_to_launch_from_notification == 0){
            if (checkPlayServices()) {

           /* gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }*/
            } else {
                Toast.makeText(DashboardActivity.this, getResources().getString(R.string.google_play_services_not_supported), Toast.LENGTH_LONG).show();
                Log.i(TAG, "No valid Google Play Services APK found.");
            }
        }else{

            if(Integer.parseInt(StorageHelper.getUserGroup(DashboardActivity.this,"user_group")) == group_push_notification){

                if(Integer.parseInt(StorageHelper.getUserGroup(DashboardActivity.this,"user_group")) == 3){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    switch (fragment_to_launch_from_notification) {


                        case 1: {
                            Log.d(TAG, "Inside DashboardActivity of Mentor, going to start Notification fragment");
                            // initialize();
                            //NotificationsFragment notificationsFragment=new NotificationsFragment();
                            //fragmentTransaction.add(R.id.container,notificationsFragment);
                            fragmentTransaction.replace(R.id.container, new NotificationsFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case 4: {
                            Log.d(TAG, "Inside DashboardActivity of Mentor, going to start MySchedule fragment");
                            //initialize();
                            fragmentTransaction.replace(R.id.container, new MyScheduleFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case 7: {
                            Log.d(TAG, "Inside DashboardActivity of Mentor,going to start MyConnection Fragment");
                            // initialize();
                            fragmentTransaction.replace(R.id.container, new MyConnectionsFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case 8: {
                            Log.d(TAG, "Inside DashboardActivity of Mentor,going to start My Connection Fragment ");
                            // initialize();
                            fragmentTransaction.replace(R.id.container, new MyConnectionsFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case 9: {
                            Log.d(TAG, "Inside DashboardActivity of Mentor,going to start My Connection fragment ");
                            // initialize();
                            fragmentTransaction.replace(R.id.container, new MyConnectionsFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }

                    }
                }
                if(Integer.parseInt(StorageHelper.getUserGroup(DashboardActivity.this,"user_group")) == 2){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    switch (fragment_to_launch_from_notification) {


                        case 2: {

                            fragmentTransaction.replace(R.id.container, new NotificationsFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case 3: {

                            fragmentTransaction.replace(R.id.container, new NotificationsFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case 5: {

                            fragmentTransaction.replace(R.id.container, new MyScheduleFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case 6: {

                            fragmentTransaction.replace(R.id.container, new MyScheduleFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case 7: {

                            fragmentTransaction.replace(R.id.container, new MyConnectionsFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case 8: {

                            fragmentTransaction.replace(R.id.container, new MyConnectionsFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case 9: {

                            fragmentTransaction.replace(R.id.container, new MyConnectionsFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                    }
                }



            }else{
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.switch_login),Toast.LENGTH_SHORT).show();
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
        // alertDialog.show();
    }

    private void logout() {
        String loginWith = StorageHelper.getUserDetails(this,"login_with");
        if(loginWith == null || loginWith.equals("G+")) {
            LoginActivity.doLogout = true;
        }

        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        StorageHelper.clearUser(this);
        StorageHelper.clearUserPhone(this);
        fbClearToken();
        this.finish();
        startActivity(new Intent(this, LoginActivity.class));
        NetworkManager.counter = 0;
    }

    private void updateTermsAndConditionsStatus() {
        //TODO: Needs to call update status method of terms and conditions.
        StorageHelper.storePreference(this, "terms", "yes");
    }

    private void initialize() {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.wholeContainer);
        ImageView drawerIcon = (ImageView) findViewById(R.id.drawerIcon);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), container, drawerIcon);
        onNavigationDrawerItemSelected(0);
        userToken = StorageHelper.getUserDetails(this, getResources().getString(R.string.auth_token));


        actionBar = getActionBar();
        actionBar.hide();
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowCustomEnabled(true);
//        View customView = getLayoutInflater().inflate(R.layout.actionbar_title, null);
        customTitle = (TextView) findViewById(R.id.actionbarTitle);
        customTitle.setText(getResources().getString(R.string.app_name));
//        actionBar.setCustomView(customView);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(user_group == 3){
            if (position == 0)
                fragmentTransaction.replace(R.id.container, new com.findmycoach.app.fragment_mentor.HomeFragment());
            else if (position == 1)
                fragmentTransaction.replace(R.id.container, new NotificationsFragment());
            else if (position == 2)
                fragmentTransaction.replace(R.id.container, new MyConnectionsFragment());
            else if (position == 3)
                fragmentTransaction.replace(R.id.container, new MyScheduleFragment());

            fragmentTransaction.commit();
            onSectionAttached(position);
        }
        if(user_group == 2){
            if (position == 0)
                fragmentTransaction.replace(R.id.container, new HomeFragment());
            else if (position == 1)
                fragmentTransaction.replace(R.id.container, new NotificationsFragment());
            else if (position == 2)
                fragmentTransaction.replace(R.id.container, new MyConnectionsFragment());
            else if (position == 3)
                fragmentTransaction.replace(R.id.container, new MyScheduleFragment());

            fragmentTransaction.commit();
            onSectionAttached(position);
        }

    }

    public void onSectionAttached(int number) {
        mTitle = getResources().getStringArray(R.array.navigation_items)[number];
    }

    public void restoreActionBar() {
        customTitle.setText(mTitle);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment != null && !mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.dashboard, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        } else if (id == R.id.log_out) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
