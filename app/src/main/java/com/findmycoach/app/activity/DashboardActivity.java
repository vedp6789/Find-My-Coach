package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.findmycoach.app.util.TermsAndCondition;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.HashMap;

public class DashboardActivity extends FragmentActivity
        implements Callback, View.OnClickListener {

    private CharSequence mTitle;
    public int user_group;
    public static DashboardActivity dashboardActivity;
    private String[] navigationTitle;
    private boolean isProfileOpen;

    /* Below are the class variables which are mainly used GCM Client registration and Google Play Services device configuration check*/
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String GCM_SHARED_PREFERENCE = " gcm_preference";
    private static String REG_ID_SAVED_TO_SERVER;
    private FragmentManager fragmentManager;
    String regid;
    boolean regid_saved_to_server = false;
    boolean onNewIntentCalled = false;
    int fragment_to_open; /* will get value from onNewIntent method */
    /**
     * This is the project number we got
     * from the API Console,
     */
    String SENDER_ID = "944443780210";
    /**
     * Tag used on log messages.
     */
    static final String TAG = "G Play Status:";
    static final String TAG1 = "GCMCommunication";
    static final String TAG2 = "FMC-GCM";

    public ImageView menuDrawer;
    public ImageView backButton;
    public TextView titleTV;
    public RelativeLayout container, mainLayout;

    GoogleCloudMessaging gcm;
    Context context;

    int fragment_to_launch_from_notification = 0;  ///  On a tap over Push notification, then it will be used to identify which operation to perform
    int group_push_notification = 0;      /// it will identify push notification for which type of user.
    int user_group_from_push = 0;
    /**
     * Related to reside menu
     */
    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemNotification;
    private ResideMenuItem itemConnection;
    public ResideMenuItem itemSchedule;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemLogout;
    private HashMap<String, Integer> resideMenuItemIcons;


    private GoogleMap map;
    public String userCurrentAddress;
    public double latitude;
    public double longitude;
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new GetLocation().execute();

        if (LoginActivity.loginActivity != null)
            LoginActivity.loginActivity.finish();

        dashboardActivity = this;
        fragmentManager = getSupportFragmentManager();
        isProfileOpen = false;

        Log.e("VedBabu", StorageHelper.getUserProfile(this));

//        String userId = StorageHelper.getUserDetails(this, getResources().getString(R.string.user_id));
//        String newUser = StorageHelper.getUserDetails(this, getResources().getString(R.string.new_user));
//
//        Log.e("SignUp", StorageHelper.getUserDetails(this, getResources().getString(R.string.new_user)) + "");

        try {
            user_group = Integer.parseInt(StorageHelper.getUserGroup(DashboardActivity.this, "user_group"));
//            if (userId != null && newUser != null && userId.equals(newUser.split("#")[1])) {
//                String authToken = StorageHelper.getUserDetails(this, getResources().getString(R.string.auth_token));
//                RequestParams requestParams = new RequestParams();
//                Log.d(TAG2, "Stored User Id:" + userId);
//                Log.d(TAG2, "auth_token" + authToken);
//                requestParams.add("id", userId);
//                requestParams.add("user_group", user_group + "");
//                isProfileOpen = true;
//                if (!NetworkManager.isNetworkConnected(this)) {
//                    logout();
//                } else {
//                    NetworkClient.getProfile(this, requestParams, authToken, this, 4);
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            logout();
            return;
        }
        Log.e("FMC - user_group", "" + user_group);
        setContentView(R.layout.activity_dashboard);

        try {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            map.setMyLocationEnabled(true);
            userCurrentAddress = "";
            map.setOnMyLocationChangeListener(myLocationChangeListener);
        } catch (Exception e) {
            userCurrentAddress = "";
        }

        container = (RelativeLayout) findViewById(R.id.container);
        mainLayout = (RelativeLayout) findViewById(R.id.drawer_layout);
        if (!isProfileOpen)
            mainLayout.setVisibility(View.VISIBLE);

        context = getApplicationContext();
        REG_ID_SAVED_TO_SERVER = getResources().getString(R.string.reg_id_saved_to_server);

        /*Creating folder for media storage*/
        StorageHelper.createAppMediaFolders(this);

        fragment_to_launch_from_notification = getIntent().getIntExtra("fragment", 0);
        Log.d(TAG, "on create " + fragment_to_launch_from_notification);
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

            } else {
                Toast.makeText(DashboardActivity.this, getResources().getString(R.string.google_play_services_not_supported), Toast.LENGTH_LONG).show();
                Log.i(TAG, "No valid Google Play Services APK found.");
            }
            initialize();
        } else {
            initialize();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dashboardActivity = null;
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
        if (calledApiValue == 4) {
            Log.e(TAG, "Start activity for result");

            if (user_group == 2) {
                ProfileResponse response = (ProfileResponse) object;
                Intent intent = new Intent(this, EditProfileActivityMentee.class);
                intent.putExtra("user_info", new Gson().toJson(response.getData()));
                startActivity(intent);
            } else if (user_group == 3) {
                Response response = (Response) object;
                Intent intent = new Intent(this, EditProfileActivityMentor.class);
                intent.putExtra("user_info", new Gson().toJson(response.getData()));
                startActivity(intent);
            }
            isProfileOpen = false;
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        isProfileOpen = false;
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
            try {
                sendRegistrationIdToBackend();
            } catch (Exception ignored) {
            }
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


    public void whenProfileNotGetUpdated() {
        Log.d(TAG, "Dashboard Activity whenProfileNotGetUpdated");
        logout();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        onNewIntentCalled = true;
        fragment_to_open = intent.getIntExtra("fragment", -2);
        user_group_from_push = intent.getIntExtra("group", -4);
        Log.d(TAG, "intent for fragment key in onNewIntent: " + intent.getIntExtra("fragment", -2));
        Log.d(TAG, "intent extra from getIntent for fragment key in onNewIntent: " + getIntent().getIntExtra("fragment", -3));

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "Dashboard Activity on Resume");

        String tNc = StorageHelper.getUserDetails(this, "terms");
        if ((tNc == null || !tNc.equals("yes")) && !isProfileOpen) {
            TermsAndCondition termsAndCondition = new TermsAndCondition();
            termsAndCondition.showTermsAndConditions(this);
        }

        try {
            if (HomeFragment.homeFragmentMentee != null && !userCurrentAddress.equals("") && user_group == 2) {
                HomeFragment.homeFragmentMentee.updateLocationFromAsync(userCurrentAddress);
                HomeFragment.homeFragmentMentee = null;
                map.setOnMyLocationChangeListener(null);
                map = null;
            }
            if (onNewIntentCalled) {
                onNewIntentCalled = false;
                fragment_to_launch_from_notification = fragment_to_open;
                group_push_notification = user_group_from_push;
            }

            if (fragment_to_launch_from_notification == 0) {
                if (!checkPlayServices()) {
                    Toast.makeText(DashboardActivity.this, getResources().getString(R.string.google_play_services_not_supported), Toast.LENGTH_LONG).show();
                    Log.i(TAG, "No valid Google Play Services APK found.");
                }
            } else {

                user_group = Integer.parseInt(StorageHelper.getUserGroup(DashboardActivity.this, "user_group"));
                if (user_group == group_push_notification) {

                    ResideMenuItem item = null;


                    Log.d(TAG, "fragment to launch from notification: " + fragment_to_launch_from_notification);

                    switch (fragment_to_launch_from_notification) {
                        case 1:
                            Log.e(TAG, user_group + " : " + group_push_notification + " : " + fragment_to_launch_from_notification + "==> 1");
                            itemHome.setTag(itemHome.getId(), "Connection");
                            item = itemHome;
                            break;
                        case 4:
                            Log.e(TAG, user_group + " : " + group_push_notification + " : " + fragment_to_launch_from_notification + "==> 4");
                            itemHome.setTag(itemHome.getId(), "Schedule");
                            item = itemHome;
                            break;
                        case 2:
                        case 3:
                        case 5:
                        case 6:
                            Log.e(TAG, user_group + " : " + group_push_notification + " : " + fragment_to_launch_from_notification + "==> 2,3,5,6");
                            item = itemNotification;
                            break;
                        case 7:
                        case 8:
                        case 9:
                            item = itemConnection;
                            break;
                    }
                    fragment_to_launch_from_notification = 0;

                    if (resideMenu == null) {
                        setUpMenu(item);
                        Log.e(TAG, "reside null");
                    } else if (item != null) {
//                    item.callOnClick();
                        updateUI(item);
                        Log.e(TAG, "reside menu item not null");
                    }
                } else {
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.switch_login), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ignored) {
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


    public void logout() {
        Log.e(TAG, "Logout");
        String loginWith = StorageHelper.getUserDetails(this, "login_with");
        if (loginWith == null || loginWith.equals("G+")) {
            LoginActivity.doLogout = true;
            Log.e(TAG, "Logout G+ true");
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        StorageHelper.clearUser(this);
        StorageHelper.clearUserPhone(this);
        fbClearToken();
        removeGCMRegistrationId();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void removeGCMRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences(context);
        Log.d(TAG, "After Logout selection and removal of GCM data: \nGCM Registration id: " + prefs.getString(PROPERTY_REG_ID, "") + "APP Version saved: " + prefs.getInt(PROPERTY_APP_VERSION, -1));
        Log.i(TAG, "Removing GCM registration id and App version on Logout");
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.remove(PROPERTY_APP_VERSION);
        editor.apply();
        Log.d(TAG, "After Logout selection and removal of GCM data: \nGCM Registration id: " + prefs.getString(PROPERTY_REG_ID, "") + "APP Version saved: " + prefs.getInt(PROPERTY_APP_VERSION, -1));
    }

    private void initialize() {
        navigationTitle = getResources().getStringArray(R.array.navigation_items);
        mTitle = getResources().getString(R.string.app_name);
        if (resideMenu == null)
            setUpMenu(null);
    }

    private void setUpMenu(ResideMenuItem item) {

        resideMenuItemIcons = new HashMap<>();

        resideMenuItemIcons.put("HomeSelected", R.drawable.home_selected);
        resideMenuItemIcons.put("HomeOutLined", R.drawable.home_outlined);

        resideMenuItemIcons.put("NotificationSelected", R.drawable.notifications);
        resideMenuItemIcons.put("NotificationOutLined", R.drawable.notifications_outlined);

        resideMenuItemIcons.put("ConnectionSelected", R.drawable.my_connections);
        resideMenuItemIcons.put("ConnectionOutLined", R.drawable.my_connections_outlined);

        resideMenuItemIcons.put("ScheduleSelected", R.drawable.my_schedules);
        resideMenuItemIcons.put("ScheduleOutLined", R.drawable.my_schedules_outlined);

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.bg);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome = new ResideMenuItem(this, resideMenuItemIcons.get("HomeOutLined"), navigationTitle[0]);
        itemNotification = new ResideMenuItem(this, resideMenuItemIcons.get("NotificationOutLined"), navigationTitle[1]);
        itemConnection = new ResideMenuItem(this, resideMenuItemIcons.get("ConnectionOutLined"), navigationTitle[2]);
        itemSchedule = new ResideMenuItem(this, resideMenuItemIcons.get("ScheduleOutLined"), navigationTitle[3]);
        itemSettings = new ResideMenuItem(this, R.drawable.settings_outlined, navigationTitle[4]);
        itemLogout = new ResideMenuItem(this, R.drawable.logout, navigationTitle[5]);

        itemHome.setOnClickListener(this);
        itemNotification.setOnClickListener(this);
        itemConnection.setOnClickListener(this);
        itemSchedule.setOnClickListener(this);
        itemSettings.setOnClickListener(this);
        itemLogout.setOnClickListener(this);
        resideMenu.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_RIGHT);
        if (user_group == 2)
            resideMenu.addMenuItem(itemNotification, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemConnection, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemSchedule, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemLogout, ResideMenu.DIRECTION_RIGHT);

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

        backButton = (ImageView) findViewById(R.id.backButton);
        titleTV = (TextView) findViewById(R.id.title);
        menuDrawer = (ImageView) findViewById(R.id.menuButton);

        backButton.setVisibility(View.INVISIBLE);
        titleTV.setText("");

        menuDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(DashboardActivity.this.getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (item != null)
            updateUI(item);
        else
            updateUI(itemHome);

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (fragmentManager.getBackStackEntryCount() > 1)
                    backButton.setVisibility(View.VISIBLE);
                else
                    backButton.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onClick(final View view) {

        if (resideMenu != null && resideMenu.isOpened())
            resideMenu.closeMenu();


        resideMenu.setMenuListener(new ResideMenu.OnMenuListener() {
            @Override
            public void openMenu() {

            }

            @Override
            public void closeMenu() {
                updateUI(view);
            }
        });
    }

    public void updateUI(View view) {
        int position = -1;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (user_group == 3) {
            if (view == itemHome) {
                clearIcon();
                itemHome.setIcon(resideMenuItemIcons.get("HomeSelected"));
                titleTV.setText(navigationTitle[1]);
                for (int i = 0; i < fragmentManager.getBackStackEntryCount() - 1; ++i) {
                    fragmentManager.popBackStack();
                }
                String tag = (String) itemHome.getTag(itemHome.getId());
                Bundle bundle = new Bundle();
                bundle.putString("OpenTab", tag);
                Log.e(TAG, tag + " <== Tag");
                try {
                    if (!fragmentManager.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("Home")) {
                    }
                } catch (Exception e) {
                    com.findmycoach.app.fragment_mentor.HomeFragment homeFragmentMentor = new com.findmycoach.app.fragment_mentor.HomeFragment();
                    homeFragmentMentor.setArguments(bundle);
                    fragmentTransaction.add(R.id.container, homeFragmentMentor).addToBackStack("Home");
                }
                position = 0;
            } else if (view == itemNotification) {
                clearIcon();
                itemNotification.setIcon(resideMenuItemIcons.get("NotificationSelected"));

                try {
                    if (!fragmentManager.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("Notification"))
                        fragmentTransaction.add(R.id.container, new NotificationsFragment()).addToBackStack("Notification");
                } catch (Exception e) {
                    fragmentTransaction.add(R.id.container, new NotificationsFragment()).addToBackStack("Notification");
                }

                position = 1;
            } else if (view == itemConnection) {
                clearIcon();
                itemConnection.setIcon(resideMenuItemIcons.get("ConnectionSelected"));
                titleTV.setText(navigationTitle[2]);

                try {
                    if (!fragmentManager.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("Connection"))
                        fragmentTransaction.add(R.id.container, new MyConnectionsFragment()).addToBackStack("Connection");
                } catch (Exception e) {
                    fragmentTransaction.add(R.id.container, new MyConnectionsFragment()).addToBackStack("Connection");
                }

                position = 2;
            } else if (view == itemSchedule) {
                clearIcon();
                itemSchedule.setIcon(resideMenuItemIcons.get("ScheduleSelected"));
                titleTV.setText(navigationTitle[3]);

                try {
                    if (!fragmentManager.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("Schedule"))
                        fragmentTransaction.add(R.id.container, new MyScheduleFragment()).addToBackStack("Schedule");
                } catch (Exception e) {
                    fragmentTransaction.add(R.id.container, new MyScheduleFragment()).addToBackStack("Schedule");
                }

                position = 3;
            }
            fragmentTransaction.commit();
        }
        if (user_group == 2) {
            if (view == itemHome) {
                clearIcon();
                itemHome.setIcon(resideMenuItemIcons.get("HomeSelected"));
                titleTV.setText("");
                for (int i = 0; i < fragmentManager.getBackStackEntryCount() - 1; ++i) {
                    fragmentManager.popBackStack();
                }
                try {
                    if (!fragmentManager.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("Home")) {
                    }
                } catch (Exception e) {
                    fragmentTransaction.add(R.id.container, new HomeFragment()).addToBackStack("Home");
                }
                position = 0;
            } else if (view == itemNotification) {
                clearIcon();
                itemNotification.setIcon(resideMenuItemIcons.get("NotificationSelected"));
                titleTV.setText(navigationTitle[1]);

                try {
                    if (!fragmentManager.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("Notification"))
                        fragmentTransaction.add(R.id.container, new NotificationsFragment()).addToBackStack("Notification");
                } catch (Exception e) {
                    fragmentTransaction.add(R.id.container, new NotificationsFragment()).addToBackStack("Notification");
                }

                position = 1;
            } else if (view == itemConnection) {
                clearIcon();
                itemConnection.setIcon(resideMenuItemIcons.get("ConnectionSelected"));
                titleTV.setText(navigationTitle[2]);

                try {
                    if (!fragmentManager.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("Connection"))
                        fragmentTransaction.add(R.id.container, new MyConnectionsFragment()).addToBackStack("Connection");
                } catch (Exception e) {
                    fragmentTransaction.add(R.id.container, new MyConnectionsFragment()).addToBackStack("Connection");
                }

                position = 2;
            } else if (view == itemSchedule) {
                clearIcon();
                itemSchedule.setIcon(resideMenuItemIcons.get("ScheduleSelected"));
                titleTV.setText(navigationTitle[3]);

                try {
                    if (!fragmentManager.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("Schedule"))
                        fragmentTransaction.add(R.id.container, new MyScheduleFragment()).addToBackStack("Schedule");
                } catch (Exception e) {
                    fragmentTransaction.add(R.id.container, new MyScheduleFragment()).addToBackStack("Schedule");
                }

                position = 3;
            }
            fragmentTransaction.commit();
        }

        if (position > -1)
            onSectionAttached(position);


        if (view == itemSettings)
            startActivity(new Intent(this, Settings.class));
        else if (view == itemLogout)
            logout();

    }

    private void clearIcon() {
        itemHome.setIcon(resideMenuItemIcons.get("HomeOutLined"));
        itemNotification.setIcon(resideMenuItemIcons.get("NotificationOutLined"));
        itemConnection.setIcon(resideMenuItemIcons.get("ConnectionOutLined"));
        itemSchedule.setIcon(resideMenuItemIcons.get("ScheduleOutLined"));
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
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
            clearIcon();

            String tag = fragmentManager.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName();
            switch (tag) {
                case "Home":
                    if (user_group == 3)
                        titleTV.setText(navigationTitle[1]);
                    else
                        titleTV.setText("");

                    itemHome.setIcon(resideMenuItemIcons.get("HomeSelected"));
                    break;
                case "Notification":
                    if (user_group == 2)
                        titleTV.setText(navigationTitle[1]);
                    else
                        titleTV.setText("");

                    itemNotification.setIcon(resideMenuItemIcons.get("NotificationSelected"));
                    break;
                case "Connection":
                    titleTV.setText(navigationTitle[2]);
                    itemConnection.setIcon(resideMenuItemIcons.get("ConnectionSelected"));
                    break;
                case "Schedule":
                    titleTV.setText(navigationTitle[3]);
                    itemSchedule.setIcon(resideMenuItemIcons.get("ScheduleSelected"));
                    break;
            }
        } else
            finish();
    }

    public void fbClearToken() {
        Log.e(TAG, "Logout fb clear");
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

    /**
     * Hide keyboard on touch view
     */
    public void setupUIForShowHideKeyBoard(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(DashboardActivity.this.getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                    }
                    return false;
                }

            });
        }
    }

    /**
     * Getting user current location
     */
    private class GetLocation extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    Log.e("MapTest", location.getLatitude() + " : " + location.getLongitude() + " : " + userCurrentAddress);
                    if (userCurrentAddress.equals("")) {
                        userCurrentAddress = NetworkManager.getCompleteAddressString(DashboardActivity.this, location.getLatitude(), location.getLongitude());
                        Log.e("MapTest", userCurrentAddress);

                        if (HomeFragment.homeFragmentMentee != null && !userCurrentAddress.equals("") && user_group == 2) {
                            HomeFragment.homeFragmentMentee.updateLocationFromAsync(userCurrentAddress);
                            HomeFragment.homeFragmentMentee = null;
                            map.setOnMyLocationChangeListener(null);
                            map = null;
                        } else if (!userCurrentAddress.equals("")) {
                            map.setOnMyLocationChangeListener(null);
                            map = null;
                        }

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            };
            return null;
        }
    }
}
