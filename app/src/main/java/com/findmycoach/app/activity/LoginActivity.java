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
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.findmycoach.app.R;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.loopj.android.http.RequestParams;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A login screen that offers login via email/password, Facebook and Google+ sign in.
 */
public class LoginActivity extends Activity implements OnClickListener, Callback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /* Request code used to invoke sign in user interactions. */
    public static boolean doLogout = false;
    // UI references.
    private TextView registerAction;
    private EditText inputUserName;
    private EditText inputPassword;
    private Button actionLogin;
    private LoginButton actionFacebook;
    private TextView forgotAction;
    private ProgressDialog progressDialog;
    private RadioGroup radioGroup_user_login;
    private RadioButton radioButton_mentee_login, radioButton_mentor_login;
    private int user_group;
    private TextView countryCodeTV;
    private String[] country_code;

    //Related to G+
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
    private SignInButton mSignInButton;
    private Button mSignOutButton;
    private Button mRevokeButton;
    private LinearLayout mSignOutButtons;

    private static final String TAG = "FMC1";
    private static final String TAG1 = "FMC1: Permissions:";
    private static final String TAG2 = "FMC1: User:";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String userToken = StorageHelper.getUserDetails(this, "auth_token");
        String phnVerified = StorageHelper.getUserDetails(this, "phone_verified");
        if (userToken != null && phnVerified != null) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            this.finish();

        } else {
            setContentView(R.layout.activity_login);
            user_group = 2;
            radioGroup_user_login = (RadioGroup) findViewById(R.id.radio_group_user_login);
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


    /* This method get all views references */
    private void initialize(Bundle savedInstanceState) {

        //G+ related
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignOutButton = (Button) findViewById(R.id.sign_out_button);
        mRevokeButton = (Button) findViewById(R.id.revoke_access_button);
        mSignOutButtons = (LinearLayout) findViewById(R.id.plus_sign_out_buttons);
        mSignInButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
        mRevokeButton.setOnClickListener(this);
        if (savedInstanceState != null) {
            mSignInProgress = savedInstanceState.getInt(SAVED_PROGRESS, STATE_DEFAULT);
        }
        mGoogleApiClient = buildGoogleApiClient();

        radioButton_mentee_login = (RadioButton) findViewById(R.id.radio_button_mentee_login);
        radioButton_mentor_login = (RadioButton) findViewById(R.id.radio_button_mentor_login);
        registerAction = (TextView) findViewById(R.id.action_register);
        registerAction.setOnClickListener(this);
        inputUserName = (EditText) findViewById(R.id.input_login_id);
        inputPassword = (EditText) findViewById(R.id.input_login_password);
        actionLogin = (Button) findViewById(R.id.email_sign_in_button);
        actionLogin.setOnClickListener(this);
        actionFacebook = (LoginButton) findViewById(R.id.facebook_login_button);
        actionFacebook.setReadPermissions(Arrays.asList("user_location", "user_birthday", "email"));
        forgotAction = (TextView) findViewById(R.id.action_forgot_password);
        forgotAction.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.logging_in));
    }

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
                if (!mGoogleApiClient.isConnecting()) {
                    resolveSignInError();
                }
                break;
            case R.id.sign_out_button:
                if (!mGoogleApiClient.isConnecting()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                }
                break;
            case R.id.revoke_access_button:
                if (!mGoogleApiClient.isConnecting()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                    mGoogleApiClient = buildGoogleApiClient();
                    mGoogleApiClient.connect();
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

    private void showCountryCodeDialog() {
        final Dialog countryDialog = new Dialog(this);
        countryDialog.setCanceledOnTouchOutside(true);
        countryDialog.setTitle(getResources().getString(R.string.select_country_code));
        countryDialog.setContentView(R.layout.dialog_country_code);
        ListView listView = (ListView) countryDialog.findViewById(R.id.countryCodeListView);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, country_code));
        countryDialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryCodeTV.setText(country_code[position].split(",")[0]);
                countryDialog.dismiss();
            }
        });
    }


    private void callForgotPasswordActivity() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }


    private void logIn() {
        String userId = inputUserName.getText().toString();
        String userPassword = inputPassword.getText().toString();
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

    private boolean validateLoginForm(String userId, String userPassword) {
        boolean isValid = true;
        if (!isEmailValid(userId)) {
            isValid = false;
            showErrorMessage(inputUserName, getResources().getString(R.string.error_invalid_email));
        } else if (userPassword.equals("")) {
            isValid = false;
            showErrorMessage(inputPassword, getResources().getString(R.string.error_field_required));
        } else if (userPassword.length() < 5) {
            isValid = false;
            showErrorMessage(inputPassword, getResources().getString(R.string.error_password_size));
        }
        return isValid;
    }


    /*Checking entered email is valid email or not*/
    public boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private void showErrorMessage(final EditText editText, String errorMessage) {
        editText.setError(errorMessage);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.setError(null);
            }
        }, 3500);
    }

    private void callSignUpActivity() {
        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        startActivity(signUpIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.dismiss();
        Log.d(TAG,"Inside onActivityResult method!");
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mSignInProgress = STATE_SIGN_IN;
            } else {
                mSignInProgress = STATE_DEFAULT;
            }
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
            Log.d(TAG,"request code : "+ RC_SIGN_IN);
            StorageHelper.storePreference(this, "login_with", "G+");
        } else { // If Result from Facebook
            Session session = Session.getActiveSession();
            session.onActivityResult(this, requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                getSession();
            }
            StorageHelper.storePreference(this, "login_with", "fb");
        }
    }


    /*This function will get Facebook Session*/
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

    private void callWebservice(GraphUser user) {
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("first_name", user.getFirstName());
        requestParams.add("last_name", user.getLastName());
        requestParams.add("dob", user.getBirthday());
        requestParams.add("facebook_link", user.getLink());
        requestParams.add("email", (String) user.getProperty("email"));
        requestParams.add("gender", (String) user.getProperty("gender"));
        requestParams.add("photograph", "http://graph.facebook.com/" + user.getId() + "/picture?type=large");
        requestParams.add("user_group", String.valueOf(user_group));
        saveUserEmail((String) user.getProperty("email"));
        NetworkClient.registerThroughSocialMedia(LoginActivity.this, requestParams, LoginActivity.this, 23);
    }

    private void getPhoneNumber(final RequestParams requestParams) {
        progressDialog.dismiss();
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(getResources().getString(R.string.phone_no_must));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.phone_number_dialog);
        countryCodeTV = (TextView) dialog.findViewById(R.id.countryCodeTV);
        countryCodeTV.setText(getCountryZipCode());
        countryCodeTV.setOnClickListener(this);
        final EditText phoneEditText = (EditText) dialog.findViewById(R.id.phoneEditText);
        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phnNum = phoneEditText.getText().toString();
                if (phnNum.equals("") || phnNum.length() < 10) {
                    phoneEditText.setError(getResources().getString(R.string.enter_valid_phone_no));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            phoneEditText.setError(null);
                        }
                    }, 3500);
                } else if (countryCodeTV.getText().toString().trim().equals("Select")) {
                    countryCodeTV.setError(getResources().getString(R.string.select_country_code));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            countryCodeTV.setError(null);
                        }
                    }, 3500);
                } else {
                    dialog.dismiss();
                    requestParams.add("phone_number",countryCodeTV.getText().toString().trim() + phnNum);
                    requestParams.add("user_group", String.valueOf(user_group));
                    saveUserPhoneNumber(phnNum);
                    NetworkClient.updatePhoneForSocialMedia(LoginActivity.this, requestParams, LoginActivity.this, 26);
                    progressDialog.show();
                }
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbClearToken();
                finish();
                dialog.dismiss();
                String user_group_saved = StorageHelper.getUserGroup(LoginActivity.this, "user_group");
                if (user_group_saved != null && user_group_saved.equals(String.valueOf(user_group))) {
                    // No need to change saved value of user_group
                } else {
                    StorageHelper.storePreference(LoginActivity.this, "user_group", String.valueOf(user_group));
                }
                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
            }
        });
        dialog.show();

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

    private void saveUser(String authToken, String userId) {
        StorageHelper.storePreference(this, "auth_token", authToken);
        StorageHelper.storePreference(this, "user_id", userId);
    }

    private void saveUserPhn(String isPhnVerified) {
        StorageHelper.storePreference(this, "phone_verified", isPhnVerified);
    }

    private void saveUserEmail(String emailId) {
        StorageHelper.storePreference(this, "user_email", emailId);
    }

    private void saveUserPhoneNumber(String phoneNumber) {
        StorageHelper.storePreference(this, "phone_number", phoneNumber);
    }


    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();

        // Saving user group
        String user_group_saved = StorageHelper.getUserGroup(LoginActivity.this, "user_group");
        if (user_group_saved == null || !user_group_saved.equals(String.valueOf(user_group))) {
            StorageHelper.storePreference(LoginActivity.this, "user_group", String.valueOf(user_group));
        }

        Response response = (Response) object;

        if (response == null)
            return;

        // Saving auth token and user id of user for further use
        if (response.getAuthToken() != null && response.getData().getId() != null) {
            saveUser(response.getAuthToken(), response.getData().getId());
        }
        if (response.getMessage() != null && response.getMessage().equals("Success")) {
            saveUserPhn("True");
            finish();
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            if (response.getData().getPhonenumber() == null) {
                RequestParams requestParams = new RequestParams();
                requestParams.add("email", response.getData().getEmail());
                getPhoneNumber(requestParams);
                return;
            } else {
                // Saving phone number for validating purpose and starting ValidatePhoneActivity
                saveUserPhoneNumber(response.getData().getPhonenumber());
                Intent intent = new Intent(this, ValidatePhoneActivity.class);

                finish();
                startActivity(intent);
            }

        }
    }

    @Override
    public void failureOperation(Object message, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        String msg = (String) message;

        // Saving user group
        String user_group_saved = StorageHelper.getUserGroup(LoginActivity.this, "user_group");
        if (user_group_saved == null || !user_group_saved.equals(String.valueOf(user_group)))
            StorageHelper.storePreference(LoginActivity.this, "user_group", String.valueOf(user_group));

        if (msg.equals("Phone number is not set")) {
            RequestParams requestParams = new RequestParams();
            requestParams.add("email", StorageHelper.getUserDetails(this, "user_email"));
            getPhoneNumber(requestParams);
            return;
        }

        // If phone number is not verified then starting ValidatePhoneActivity
        if (msg.equals("Validate Phone number to login") || msg.equals("Validate Phone number and Email to Login ")) {
            startActivity(new Intent(this, ValidatePhoneActivity.class));
            finish();
            return;
        }

        // Displaying error message to user
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        //Clearing facebook token and G+ user if connected
        fbClearToken();
        if (!mGoogleApiClient.isConnecting()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }

        //Restarting the LoginActivity
        finish();
        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
    }

    public String getCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "Select";
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();
        country_code = this.getResources().getStringArray(R.array.country_codes);
        for (int i = 1; i < country_code.length; i++) {
            String[] g = country_code[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }


    //old method
    private void getProfileInformation(Person currentPerson) {
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        try {
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            requestParams.add("email", email);
            saveUserEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            requestParams.add("first_name", currentPerson.getDisplayName().split(" ")[0]);
            requestParams.add("last_name", currentPerson.getDisplayName().split(" ")[1]);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            requestParams.add("dob", currentPerson.getBirthday());
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            requestParams.add("gender", currentPerson.getGender() == 0 ? "M" : "F");
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            requestParams.add("google_link", currentPerson.getUrl());
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            requestParams.add("photograph",currentPerson.getImage().getUrl());
        }catch (Exception e){
            e.printStackTrace();
        }
        requestParams.add("user_group", String.valueOf(user_group));
        NetworkClient.registerThroughSocialMedia(LoginActivity.this, requestParams, this, 23);
    }


    // G+ related
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        if(doLogout){
            try{
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }catch (Exception e){
                e.printStackTrace();
            }
            doLogout = false;
            return;
        }

        mSignInButton.setVisibility(View.GONE);
        mSignOutButtons.setVisibility(View.VISIBLE);

        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        getProfileInformation(currentUser);
        mSignInProgress = STATE_DEFAULT;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            mSignInIntent = result.getResolution();
            mSignInError = result.getErrorCode();
            if (mSignInProgress == STATE_SIGN_IN) {
                resolveSignInError();
            }
        }
        onSignedOut();
    }

    private GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PROGRESS, mSignInProgress);
    }

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

    private void onSignedOut() {
        mSignInButton.setVisibility(View.VISIBLE);
        mSignOutButtons.setVisibility(View.GONE);
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
}



