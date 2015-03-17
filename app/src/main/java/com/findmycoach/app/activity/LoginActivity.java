package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.loopj.android.http.RequestParams;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password, Facebook and Google+ sign in.
 */
public class LoginActivity extends Activity implements OnClickListener, Callback, ConnectionCallbacks,
        OnConnectionFailedListener {

    /* Request code used to invoke sign in user interactions. */
    public static boolean doLogout = false;
    // UI references.
    private TextView registerAction;
    private EditText inputUserName;
    private EditText inputPassword;
    private Button actionLogin;
    private LoginButton actionFacebook;
    private SignInButton mPlusSignInButton;
    private TextView forgotAction;
    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private ProgressDialog progressDialog;
    private View mProgressView;
    private View mSignOutButtons;
    private Button signOutButton;
    private Button disconnectButton;
    private RadioGroup radioGroup_user_login;
    private RadioButton radioButton_mentee_login, radioButton_mentor_login;
    private int user_group;
    PendingIntent pendingIntent;
    Context context;
    private TextView countryCodeTV;
    private String[] country_code;

    private static final String TAG = "FMC1";
    private static final String TAG1 = "FMC1: Permissions:";
    private static final String TAG2 = "FMC1: User:";

    public GoogleApiClient googleApiClient;
    private boolean mSignInClicked;
    private static final int G_SING_IN = 0;
    private ConnectionResult connectionResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        /*Intent intent_service = new Intent(LoginActivity.this, BackgroundService.class);
        startService(intent_service);*/

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
            initialize();
        }
    }


    /* This method get all views references */
    private void initialize() {
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
        mPlusSignInButton = (SignInButton) findViewById(R.id.google_login_button);
        mPlusSignInButton.setOnClickListener(this);
        forgotAction = (TextView) findViewById(R.id.action_forgot_password);
        forgotAction.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.logging_in));
        mProgressView = findViewById(R.id.login_progress);
        signOutButton = (Button) findViewById(R.id.plus_sign_out_button);
        disconnectButton = (Button) findViewById(R.id.plus_disconnect_button);
        mSignOutButtons = findViewById(R.id.plus_sign_out_buttons);
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
                //Intent intent=new Intent(this,DashboardActivity.class);
                //startActivity(intent);
                break;
            case R.id.google_login_button:
                Log.d(TAG, "Google + clicked");
                if (supportsGooglePlayServices()) {
                    signInWithGooglePlus();
                    Log.d(TAG, "Google Play Support");
                } else {
                    mPlusSignInButton.setVisibility(View.GONE);
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

    private void signInWithGooglePlus() {
        googleApiClient.connect();
        if (!googleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }else{
            Log.d(TAG,"Trying to connect with Google plus");
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            mIntentInProgress = true;
            try {
                mConnectionResult.startResolutionForResult(this, G_SING_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                googleApiClient.connect();
                e.printStackTrace();
            }

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
        if (requestCode == G_SING_IN) {
            Log.d(TAG,"request code :"+G_SING_IN);
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;

            }
            mIntentInProgress = false;
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
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
                } else if (countryCodeTV.getText().toString().trim().equals("")) {
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

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }


    private void populateUserFields(Person currentPerson) {
        Log.d(TAG2, "Name:" + currentPerson.getName());
        Log.d(TAG2, "Display Name:" + currentPerson.getDisplayName());
        Log.d(TAG2, "Nick Name:" + currentPerson.getNickname());
        Log.d(TAG2, "Date of Birth:" + currentPerson.getBirthday());
        Log.d(TAG2, "Current Location:" + currentPerson.getCurrentLocation());
        Log.d(TAG2, "Gender:" + currentPerson.getGender());
        Log.d(TAG2, "Gender:" + currentPerson.getImage());
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
        if (googleApiClient.isConnected()) {
            //googleApiClient.clearDefaultAccount();
            googleApiClient.disconnect();
            //mPlusClient.disconnect();
        }

        //Restarting the LoginActivity
        finish();
        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
    }

    public String getCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();
        country_code = this.getResources().getStringArray(R.array.country_codes);
        for (int i = 0; i < country_code.length; i++) {
            String[] g = country_code[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        getProfileInformation();
        updateUI(true);
    }

    private void updateUI(boolean b) {
        if (b) {
            mPlusSignInButton.setVisibility(View.GONE);
            mSignOutButtons.setVisibility(View.VISIBLE);

        } else {
            mPlusSignInButton.setVisibility(View.VISIBLE);
            mSignOutButtons.setVisibility(View.GONE);
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(googleApiClient);
                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
        updateUI(false);
        Log.i(TAG,"Google Plus login suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG,"Google Plus login failed.");
        if (!connectionResult.hasResolution()) {
            Log.i(TAG,"Google Plus login failed. connectionResult.hasResolution is false");
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }
        if(!mIntentInProgress){
            Log.i(TAG,"Google Plus login failed. mIntentInProgress is false");
            this.connectionResult=connectionResult;
            if(mSignInClicked){
                resolveSignInError();
            }
        }
    }
}



