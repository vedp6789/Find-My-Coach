package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.findmycoach.app.R;
import com.findmycoach.app.adapter.CountryCodeAdapter;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A login screen that offers login via email/password, Facebook and Google+ sign in.
 */
public class LoginActivity extends Activity implements OnClickListener, Callback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Request code used to invoke sign in user interactions.
     */
    public static boolean doLogout = false;
    private EditText inputUserName;
    private EditText inputPassword;
    private ProgressDialog progressDialog;
    private int user_group;
    private TextView countryCodeTV;
    private ArrayList<String> country_code;
    private int retryFbLogin;
    public LoginButton actionFacebook;
    public ImageButton mSignInButton;

    /**
     * Related to G+
     */
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private static final int RC_SIGN_IN = 0;
    private static final int DIALOG_PLAY_SERVICES_ERROR = 0;
    private static final String SAVED_PROGRESS = "sign_in_progress";
    private GoogleApiClient mGoogleApiClient;
    private int mSignInProgress;
    private PendingIntent mSignInIntent;
    private int mSignInError;
    public static LoginActivity loginActivity;

    /**
     * Log tags
     */
    private static final String TAG = "FMC";
    private static final String TAG1 = "FMC: Permissions:";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    loginActivity = this;

    final String userToken = StorageHelper.getUserDetails(this, "auth_token");
    String phnVerified = StorageHelper.getUserDetails(this, "phone_verified");

    /** If user is already logged-in in app then open Dashboard Activity */
    if (userToken != null && phnVerified != null) {
        Log.e(TAG, "Login from onCreate");
        Response response = new Gson().fromJson(StorageHelper.getUserProfile(this), Response.class);
        int userGroup = Integer.parseInt(StorageHelper.getUserGroup(this, "user_group"));
        if (response != null && (response.getData().getCity() == null || response.getData().getCity().toString().trim().equals(""))) {
            if (userGroup == 2) {
                Intent intent = new Intent(this, EditProfileActivityMentee.class);
                intent.putExtra("user_info", new Gson().toJson(response.getData()));
                intent.putExtra("new_user", true);
                startActivity(intent);
            } else if (userGroup == 3) {
                Intent intent = new Intent(this, EditProfileActivityMentor.class);
                intent.putExtra("user_info", new Gson().toJson(response.getData()));
                intent.putExtra("new_user", true);
                startActivity(intent);
            }

        } else if (userGroup == 3 && (response.getData().getSubCategoryName() == null || response.getData().getSubCategoryName().size() < 1)) {
            Intent intent = new Intent(this, EditProfileActivityMentor.class);
            intent.putExtra("user_info", new Gson().toJson(response.getData()));
            intent.putExtra("new_user", true);
            startActivity(intent);
        } else
            startActivity(new Intent(this, DashboardActivity.class));
        this.finish();
    }
    /** Else setup the login screen */
    else {
        setContentView(R.layout.activity_login);
        user_group = 3;
        RadioGroup radioGroup_user_login = (RadioGroup) findViewById(R.id.radio_group_user_login);
        radioGroup_user_login.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_mentee_login) {
                    user_group = 2;
                }
                if (checkedId == R.id.radio_button_mentor_login) {
                    user_group = 3;
                }
            }
        });
        initialize(savedInstanceState);
    }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginActivity = null;
    }

    /**
     * This method get references of all views
     */
    private void initialize(Bundle savedInstanceState) {

        /** G+ related */
        mSignInButton = (ImageButton) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);
        if (savedInstanceState != null) {
            mSignInProgress = savedInstanceState.getInt(SAVED_PROGRESS, STATE_DEFAULT);
        }
        mGoogleApiClient = buildGoogleApiClient();


        /** FB related */
        actionFacebook = (LoginButton) findViewById(R.id.facebook_login_button);
        actionFacebook.setReadPermissions(Arrays.asList("user_location", "user_birthday", "email", "user_friends"));
        actionFacebook.setBackgroundResource(R.drawable.facebook);

        /** Other views related to generic login, sign up, forget password */
        TextView registerAction = (TextView) findViewById(R.id.action_register);
        registerAction.setOnClickListener(this);
        inputUserName = (EditText) findViewById(R.id.input_login_id);
        inputPassword = (EditText) findViewById(R.id.input_login_password);
        Button actionLogin = (Button) findViewById(R.id.email_sign_in_button);
        actionLogin.setOnClickListener(this);
        TextView forgotAction = (TextView) findViewById(R.id.action_forgot_password);
        forgotAction.setOnClickListener(this);

        /** initializing progress dialog and setting up progress message*/
        progressDialog = new ProgressDialog(this);
//        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        progressDialog.setContentView(R.layout.progressbar_textview);
//        TextView msg = (TextView) progressDialog.findViewById(R.id.msg);
        progressDialog.setMessage(getResources().getString(R.string.logging_in));

        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    logIn();
                }
                return false;
            }
        });

//        inputUserName.setOnTouchListener(onTouchListener);
//        inputPassword.setOnTouchListener(onTouchListener);
//        inputUserName.setOnFocusChangeListener(onFocusChangeListener);
//        inputPassword.setOnFocusChangeListener(onFocusChangeListener);
    }

    /**
     * Clear error from edit text when focused or on touch
     */
    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                ((TextView) v).setError(null);
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ((TextView) v).setError(null);
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        int actionId = v.getId();
        switch (actionId) {
            case R.id.action_register:
                callSignUpActivity();
                break;
            case R.id.email_sign_in_button:
                logIn();
                break;
            case R.id.sign_in_button:
                progressDialog.show();
                if (!mGoogleApiClient.isConnecting()) {
                    resolveSignInError();
                }
                break;
            case R.id.action_forgot_password:
                callForgotPasswordActivity();
                break;
            case R.id.countryCodeTV:
                showCountryCodeDialog();
                break;
        }
    }

    /**
     * Opens register activity
     */
    private void callSignUpActivity() {
        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        signUpIntent.putExtra("user_group", user_group);
        startActivity(signUpIntent);
    }

    /**
     * Opens forget password activity
     */
    private void callForgotPasswordActivity() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }


    /**
     * Called when user clicks the generic login button
     */
    private void logIn() {

        String userId = inputUserName.getText().toString();              /** Getting user's email id */
        String userPassword = inputPassword.getText().toString();        /** Getting entered password */
        boolean isFormValid = validateLoginForm(userId, userPassword);
        if (isFormValid) {
            Log.d(TAG, "email:" + userId + "\n Password:" + userPassword);
            if (NetworkManager.isNetworkConnected(this)) {
                progressDialog.show();
                RequestParams requestParams = new RequestParams();
                requestParams.add("email", userId);
                requestParams.add("password", userPassword);
                requestParams.add("user_group", String.valueOf(user_group));
                saveUserEmail(userId);
                NetworkClient.login(this, requestParams, this, 1);
            } else
                Toast.makeText(this, getResources().getString(R.string.check_network_connection), Toast.LENGTH_LONG).show();
        }
        StorageHelper.storePreference(this, "login_with", "Login");
    }

    /**
     * Validating login details i.e email and password
     */
    private boolean validateLoginForm(String userId, String userPassword) {
        boolean isValid = true;
        if (!isEmailValid(userId)) {
            isValid = false;
            showErrorMessage(inputUserName, getResources().getString(R.string.error_invalid_email));
        }
        if (userPassword.equals("") || userPassword.length() < 5) {
            isValid = false;
            showErrorMessage(inputPassword, getResources().getString(R.string.error_password_size));
        }
        return isValid;
    }


    /**
     * Checking entered email is valid email or not
     */
    public boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Used to show error for empty or wrong entered details in corresponding EditText with error message
     */
    private void showErrorMessage(final EditText editText, String errorMessage) {
        editText.setError(errorMessage);
    }


    /**
     * Called when user wants to login using G+ or fb
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progressDialog.dismiss();

        /** Attempt to login with G+ */
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mSignInProgress = STATE_SIGN_IN;
            } else {
                mSignInProgress = STATE_DEFAULT;
            }
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
            Log.d(TAG, "request code : " + RC_SIGN_IN);
            StorageHelper.storePreference(this, "login_with", "G+");
        }

        /** Attempt to login with FB */
        else {
            Log.d(TAG, "fb login result : " + resultCode);
            Session session = Session.getActiveSession();
            session.onActivityResult(this, requestCode, resultCode, data);
            if (resultCode == RESULT_OK || retryFbLogin++ == 0) {
                Log.d(TAG, "get session");
                getSession();
            }
            StorageHelper.storePreference(this, "login_with", "fb");
        }
    }

    /**
     * Getting Facebook Session
     */
    private void getSession() {
        Session.openActiveSession(this, true, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (!session.isOpened())
                    session = new Session(getApplicationContext());
                if (session.isOpened()) {
                    Log.d(TAG, "session opened");
                    Log.d(TAG1, session.getPermissions().toString());
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, com.facebook.Response response) {
                            if (user != null) {
                                callWebservice(user);
                            } else {
                                Log.d(TAG, "user null");
                            }
                        }
                    }).executeAsync();
                } else {
                    Log.d(TAG, "session not opened");
                }
            }
        });
    }

    /**
     * Clearing Fb user if exists
     */
    public void fbClearToken() {
        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
            }
        }
    }

    /**
     * Calling socialAuthentication api with user's fb details
     */
    private void callWebservice(GraphUser user) {
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("first_name", user.getFirstName());
        requestParams.add("last_name", user.getLastName());
        requestParams.add("dob", user.getBirthday());
        try {
            getFbFriends();
            Log.e(TAG, user.getId());
            Log.e(TAG, user.getFirstName());
            Log.e(TAG, user.getLastName());
            Log.e(TAG, user.getBirthday());
            Log.e(TAG, (String) user.getProperty("email"));
            Log.e(TAG, (String) user.getProperty("gender"));
            GraphPlace location = user.getLocation();
            Log.e(TAG, location.getName());
        } catch (Exception ignored) {
        }
        requestParams.add("facebook_link", user.getLink());
        requestParams.add("gender", (String) user.getProperty("gender"));
        requestParams.add("photograph", "http://graph.facebook.com/" + user.getId() + "/picture?type=large");
        requestParams.add("user_group", String.valueOf(user_group));
        saveUserEmail((String) user.getProperty("email"));
        try {
            String email = (String) user.getProperty("email");
            if (email == null || email.trim().equals("")) {
                showEmailDialog(requestParams);
                return;
            } else
                requestParams.add("email", email);
        } catch (Exception e) {
            showEmailDialog(requestParams);
            return;
        }
        NetworkClient.registerThroughSocialMedia(LoginActivity.this, requestParams, LoginActivity.this, 23);
    }

    private void getFbFriends() {
        Log.e(TAG, "Method called");
        Session activeSession = Session.getActiveSession();
        if (activeSession.getState().isOpened()) {
            Log.e(TAG, "inside if");
            Request friendRequest = Request.newMyFriendsRequest(activeSession,
                    new Request.GraphUserListCallback() {
                        @Override
                        public void onCompleted(List<GraphUser> users, com.facebook.Response response) {
                            Log.e(TAG, "OnComplete : " + users.size());
                            for(GraphUser user : users){
                                try {
                                    Log.e(TAG, user.getFirstName());
                                    Log.e(TAG, user.getLastName());
                                    Log.e(TAG, user.getBirthday());
                                    Log.e(TAG, (String) user.getProperty("email"));
                                    Log.e(TAG, (String) user.getProperty("gender"));
                                    GraphPlace location = user.getLocation();
                                    Log.e(TAG, location.getName());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            Bundle params = new Bundle();
            params.putString("fields", "id,name,friends");
            friendRequest.setParameters(params);
            friendRequest.executeAsync();
        }
    }

    /**
     * Dialog to manually get email from user
     */
    private void showEmailDialog(final RequestParams requestParams) {

        progressDialog.dismiss();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.email_id_dialog);
        final EditText emailET = (EditText) dialog.findViewById(R.id.emailEditText);
        emailET.setOnTouchListener(onTouchListener);
        emailET.setOnFocusChangeListener(onFocusChangeListener);

        /** Ok button clicked */
        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailET.getText().toString();

                boolean isValid = false;
                String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(email);
                if (matcher.matches()) {
                    isValid = true;
                }

                /** If email is null or invalid */
                if (email.equals("") || !isValid) {
                    emailET.setError(getResources().getString(R.string.enter_valid_email));
                }

                /** email is correct */
                else {
                    dialog.dismiss();
                    requestParams.add("email", email);
                    StorageHelper.storePreference(LoginActivity.this, "user_email", email);
                    NetworkClient.registerThroughSocialMedia(LoginActivity.this, requestParams, LoginActivity.this, 23);
                    progressDialog.show();
                }
            }
        });

        /** Cancel button clicked */
        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbClearToken();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    /**
     * Calling socialAuthentication api with user's G+ account details
     */
    private void getProfileInformation(Person currentPerson) {
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        try {
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            requestParams.add("email", email);
            Log.e(TAG, email);
            saveUserEmail(email);
        } catch (Exception ignored) {
        }
        try {
            requestParams.add("first_name", currentPerson.getDisplayName().split(" ")[0]);
            requestParams.add("last_name", currentPerson.getDisplayName().split(" ")[1]);
            Log.e(TAG, currentPerson.getDisplayName().split(" ")[0]);
            Log.e(TAG, currentPerson.getDisplayName().split(" ")[1]);
        } catch (Exception ignored) {
        }
        try {
            Log.e(TAG, currentPerson.getCurrentLocation());
            Log.e(TAG, currentPerson.getBirthday());
            requestParams.add("dob", currentPerson.getBirthday());
        } catch (Exception ignored) {
        }
        try {
            requestParams.add("gender", currentPerson.getGender() == 0 ? "M" : "F");
        } catch (Exception ignored) {
        }
        try {
            requestParams.add("google_link", currentPerson.getUrl());
        } catch (Exception ignored) {
        }
        try {
            requestParams.add("photograph", currentPerson.getImage().getUrl());
        } catch (Exception ignored) {
        }
        requestParams.add("user_group", String.valueOf(user_group));
        NetworkClient.registerThroughSocialMedia(LoginActivity.this, requestParams, this, 23);
    }

    /**
     * G+ account is connected
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        /** If G+ logged in user logged out (DashBoard Activity) */
        if (doLogout) {
            try {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            } catch (Exception ignored) {
            }
            doLogout = false;
            return;
        }

        /** Getting details of connected G+ user and sending details to api for logging in */
        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        getProfileInformation(currentUser);
        mSignInProgress = STATE_DEFAULT;
    }

    /**
     * if suspended the reconnect G+ user
     */
    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    /**
     * When G+ connection failed
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            /* If G+ API is unavailable */
        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            mSignInIntent = result.getResolution();
            mSignInError = result.getErrorCode();
            if (mSignInProgress == STATE_SIGN_IN) {
                resolveSignInError();
            }
        }
    }

    /**
     * Getting reference of Google Api client into mGoogleApiClient
     */
    private GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    /**
     * On next start, if G+ user is already provide access then connect client
     */
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /**
     * Disconnecting G+ connection if connected
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Saving G+ client state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PROGRESS, mSignInProgress);
    }

    /**
     * Used to resolve G+ connection error
     */
    private void resolveSignInError() {
        if (mSignInIntent != null) {
            try {
                mSignInProgress = STATE_IN_PROGRESS;
                startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.i(TAG, "Sign in intent could not be sent: " + e.getLocalizedMessage());
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
            }
        } else {
            showDialog(DIALOG_PLAY_SERVICES_ERROR);
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_PLAY_SERVICES_ERROR:
                if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
                    return GooglePlayServicesUtil.getErrorDialog(
                            mSignInError,
                            this,
                            RC_SIGN_IN,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    Log.e(TAG, "Google Play services resolution cancelled");
                                    mSignInProgress = STATE_DEFAULT;
                                }
                            });
                } else {
                    return new AlertDialog.Builder(this)
                            .setMessage(R.string.play_services_error)
                            .setPositiveButton(R.string.close,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.e(TAG, "Google Play services error could not be "
                                                    + "resolved: " + mSignInError);
                                            mSignInProgress = STATE_DEFAULT;
                                        }
                                    }).create();
                }
            default:
                return super.onCreateDialog(id);
        }
    }

    /**
     * Dialog to get phone number if not present in case of Social registration
     */
    private void getPhoneNumber(final RequestParams requestParams) {
        progressDialog.dismiss();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.phone_number_dialog);
        countryCodeTV = (TextView) dialog.findViewById(R.id.countryCodeTV);
        countryCodeTV.setOnTouchListener(onTouchListener);
        countryCodeTV.setOnFocusChangeListener(onFocusChangeListener);
        countryCodeTV.setText(getCountryZipCode());
        countryCodeTV.setOnClickListener(this);
        final EditText phoneEditText = (EditText) dialog.findViewById(R.id.phoneEditText);
        phoneEditText.setOnTouchListener(onTouchListener);
        phoneEditText.setOnFocusChangeListener(onFocusChangeListener);

        /** Ok button clicked */
        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phnNum = phoneEditText.getText().toString();

                /** If phone number is null */
                if (phnNum.equals("") || phnNum.length() < getResources().getInteger(R.integer.min_phone_length)) {
                    phoneEditText.setError(getResources().getString(R.string.enter_valid_phone_no));

                }

                /** if country code is not update automatically or not selected */
                else if (countryCodeTV.getText().toString().trim().equals("Select")) {
                    countryCodeTV.setError(getResources().getString(R.string.select_country_code));
                }

                /** Phone number is provided with country code, updating user's phone number in server */
                else {
                    dialog.dismiss();
                    requestParams.add("phone_number", countryCodeTV.getText().toString().trim().replace("+", "") + "-" + phnNum);
                    requestParams.add("user_group", String.valueOf(user_group));
                    saveUserPhoneNumber(phnNum);
                    Log.e("Login dialog", "phone_number : " + countryCodeTV.getText().toString().trim() + "-" + phnNum);
                    NetworkClient.updatePhoneForSocialMedia(LoginActivity.this, requestParams, LoginActivity.this, 26);
                    progressDialog.show();
                }
            }
        });

        /** Cancel button clicked */
        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbClearToken();
                if (!mGoogleApiClient.isConnecting() && mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                }
                dialog.dismiss();
                String user_group_saved = StorageHelper.getUserGroup(LoginActivity.this, "user_group");
                if (user_group_saved == null || !user_group_saved.equals(String.valueOf(user_group))) {
                    StorageHelper.storePreference(LoginActivity.this, "user_group", String.valueOf(user_group));
                }
            }
        });
        dialog.show();
    }

    /**
     * Dialog for selecting country code
     */
    private void showCountryCodeDialog() {
        final Dialog countryDialog = new Dialog(this);
        countryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        countryDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        countryDialog.setCanceledOnTouchOutside(true);
        countryDialog.setContentView(R.layout.dialog_country_code);
        ListView listView = (ListView) countryDialog.findViewById(R.id.countryCodeListView);
        ArrayList<String> a = new ArrayList<>();
        Collections.addAll(a, getResources()
                .getStringArray(R.array.country_names));
        final ArrayList<String> b = new ArrayList<>();
        Collections.addAll(b, getResources().getStringArray(R.array.country_codes_only));
        final CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(a, b,
                this, (EditText) countryDialog.findViewById(R.id.searchBox));
        listView.setAdapter(countryCodeAdapter);
        countryDialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryCodeTV.setText(countryCodeAdapter.countryNameAndCode
                        .get(position).getCountryCode().replace("(", "").replace(")", ""));
                countryDialog.dismiss();
            }
        });
    }

    /**
     * Getting country code using TelephonyManager
     */
    public String getCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "Select";
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();
        country_code = new ArrayList<>();
        Collections.addAll(country_code, this.getResources().getStringArray(R.array.country_codes));
        for (int i = 1; i < country_code.size(); i++) {
            String[] g = country_code.get(i).split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    /**
     * Saving logged in user's id and auth token in shared preferences
     */
    private void saveUser(String authToken, String userId) {
        StorageHelper.storePreference(this, "auth_token", authToken);
        StorageHelper.storePreference(this, "user_id", userId);

    }

    /**
     * Saving whether phone is verified or not in shared preferences
     */
    private void saveUserPhn(String isPhnVerified) {
        StorageHelper.storePreference(this, "phone_verified", isPhnVerified);
    }

    /**
     * Saving email of user in shared preferences
     */
    private void saveUserEmail(String emailId) {
        StorageHelper.storePreference(this, "user_email", emailId);
    }

    /**
     * Saving user's phone number in shared preferences
     */
    private void saveUserPhoneNumber(String phoneNumber) {
        StorageHelper.storePreference(this, "phone_number", phoneNumber);
    }


    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();

        Log.e(TAG, statusCode + " : " + calledApiValue);

        Response response = (Response) object;
        if (response == null)
            return;

        /** Saving user group */
        String user_group_saved = StorageHelper.getUserGroup(LoginActivity.this, "user_group");
        if (user_group_saved == null || !user_group_saved.equals(String.valueOf(user_group))) {
            StorageHelper.storePreference(LoginActivity.this, "user_group", String.valueOf(user_group));
        }


        /** Saving auth token and user id of user for further use */
        if (response.getAuthToken() != null && response.getData() != null && response.getData().getId() != null) {
            saveUser(response.getAuthToken(), response.getData().getId());
        }

        try {
            String currencyCode = StorageHelper.getCurrency(this);
            if (currencyCode == null || currencyCode.trim().equals("")) {
                StorageHelper.setCurrency(this, response.getData().getCurrencyCode());
                Log.d(TAG, "Currency code : " + currencyCode);
                Log.e(TAG, "Currency code : " + response.getData().getCurrencyCode());
            }
        } catch (Exception ignored) {
        }

        try {
            /** Saving address, city and zip of user */
            if (response.getData() != null && response.getData().getAddress() != null) {
                StorageHelper.storePreference(this, "user_local_address", (String) response.getData().getAddress());
                Log.d(TAG, "address get saved: " + StorageHelper.addressInformation(LoginActivity.this, "user_local_address"));
                if (response.getData().getCity() != null) {
                    StorageHelper.storePreference(this, "user_city_state", (String) response.getData().getCity());
                    Log.d(TAG, "city get saved: " + StorageHelper.addressInformation(LoginActivity.this, "user_city_state"));
                }
                if (response.getData().getZip() != null) {

                    StorageHelper.storePreference(this, "user_zip_code", (String) response.getData().getZip());
                    Log.d(TAG, "pin code get saved: " + StorageHelper.addressInformation(LoginActivity.this, "user_zip_code"));

                }
            }
        } catch (Exception ignored) {

        }

        try {
             /* Saving training location for mentee type user */
            if (StorageHelper.getUserGroup(LoginActivity.this, "user_group").equals("2") && response.getData().isTrainingLocation() != null) {
                StorageHelper.storePreference(this, "training_location", (String) response.getData().isTrainingLocation());
            }
        } catch (Exception ignored) {
        }

        try {
        /*Saving mentor's area of coaching i.e. subcategories to sharedpreference in string set*/
            if (StorageHelper.getUserGroup(LoginActivity.this, "user_group").equals("3")) {
                List<String> sub_category_list = response.getData().getSubCategoryName();
                Set<String> sub_category_stringSet = new HashSet<String>();
                for (int i = 0; i < sub_category_list.size(); i++) {
                    sub_category_stringSet.add(sub_category_list.get(i));
                }
                StorageHelper.storeListOfCoachingSubCategories(LoginActivity.this, sub_category_stringSet);
            }
        } catch (Exception ignored) {
        }


        /**phone number not present*/
        if (statusCode == 206 || response.getStatus() == 2) {
            RequestParams requestParams = new RequestParams();
            requestParams.add("email", StorageHelper.getUserDetails(this, "user_email"));
//            try {
//                StorageHelper.storePreference(this, getResources().getString(R.string.new_user), "true#" + response.getData().getId());
//            } catch (Exception e) {
//                try {
//                    StorageHelper.storePreference(this, getResources().getString(R.string.new_user), "true#" +
//                            StorageHelper.getUserDetails(this, getResources().getString(R.string.user_id)));
//                } catch (Exception ex) {
//                }
//            }

            if (response.getStatus() == 2)
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();

            getPhoneNumber(requestParams);
            return;
        }

        /** If phone number is not verified then starting ValidatePhoneActivity */
        if (statusCode == 400 || calledApiValue == 26) {
            startActivity(new Intent(this, ValidatePhoneActivity.class));
            finish();
            fbClearToken();
            if (!mGoogleApiClient.isConnecting() && mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }
            return;
        }

        /** Login is successful start DashBoard Activity */
        try {
            String name = response.getData().getFirstName() + " " + response.getData().getLastName();
            StorageHelper.storePreference(this, "user_full_name", name);
        } catch (Exception ignored) {
        }

        if (statusCode == 200) {
            saveUserPhn("True");
            Log.e(TAG, "Login from Success");

            int userGroup = Integer.parseInt(StorageHelper.getUserGroup(this, "user_group"));
            if (response.getData().getCity() == null || response.getData().getCity().toString().trim().equals("")) {
                if (userGroup == 2) {
                    Intent intent = new Intent(this, EditProfileActivityMentee.class);
                    intent.putExtra("user_info", new Gson().toJson(response.getData()));
                    intent.putExtra("new_user", true);
                    startActivity(intent);
                } else if (userGroup == 3) {
                    Intent intent = new Intent(this, EditProfileActivityMentor.class);
                    intent.putExtra("user_info", new Gson().toJson(response.getData()));
                    intent.putExtra("new_user", true);
                    startActivity(intent);
                }

            } else if (userGroup == 3 && (response.getData().getSubCategoryName() == null || response.getData().getSubCategoryName().size() < 1)) {
                Intent intent = new Intent(this, EditProfileActivityMentor.class);
                intent.putExtra("user_info", new Gson().toJson(response.getData()));
                intent.putExtra("new_user", true);
                startActivity(intent);

            } else
                startActivity(new Intent(this, DashboardActivity.class));

//            if (response.getData().isNewUser()) {
//                StorageHelper.storePreference(this, getResources().getString(R.string.new_user), "true#" + response.getId());
//                if (user_group == 2)
//                    startActivity(new Intent(this, PaymentDetailsActivity.class));
//            }
            finish();
        }
    }

    @Override
    public void failureOperation(Object message, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        String msg = (String) message;

        /** Saving user group */
        String user_group_saved = StorageHelper.getUserGroup(LoginActivity.this, "user_group");
        if (user_group_saved == null || !user_group_saved.equals(String.valueOf(user_group)))
            StorageHelper.storePreference(LoginActivity.this, "user_group", String.valueOf(user_group));

        if (statusCode == 206) {
            RequestParams requestParams = new RequestParams();
            requestParams.add("email", StorageHelper.getUserDetails(this, "user_email"));
            getPhoneNumber(requestParams);
            return;
        }

        /** If phone number is not verified then starting ValidatePhoneActivity */
        if (statusCode == 400) {
            startActivity(new Intent(this, ValidatePhoneActivity.class));
            finish();
            return;
        }

        /** Displaying error message to user */
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        /** Clearing facebook token and G+ user if connected */
        fbClearToken();
        if (!mGoogleApiClient.isConnecting() && mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

}



