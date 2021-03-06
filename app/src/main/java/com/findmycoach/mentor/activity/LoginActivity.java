package com.findmycoach.mentor.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.findmycoach.mentor.beans.authentication.Response;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.findmycoach.mentor.util.NetworkManager;
import com.findmycoach.mentor.util.StorageHelper;
import com.fmc.mentor.findmycoach.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.model.people.Person;
import com.loopj.android.http.RequestParams;

import java.util.Arrays;

/**
 * A login screen that offers login via email/password, Facebook and Google+ sign in.
 */
public class LoginActivity extends PlusBaseActivity implements View.OnClickListener, Callback {

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

    @Override
    protected void onPlusClientSignIn() {
        if (doLogout) {
            signOut();
            doLogout = false;
            progressDialog.dismiss();
        } else {
            enableDisconnectButtons();
            getProfileInformation();
        }
    }

    @Override
    protected void onPlusClientBlockingUI(boolean show) {
        progressDialog.show();
    }

    @Override
    protected void updateConnectButtonState() {
        boolean connected = getPlusClient().isConnected();
        mSignOutButtons.setVisibility(connected ? View.VISIBLE : View.GONE);
        mPlusSignInButton.setVisibility(connected ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onPlusClientRevokeAccess() {
        // TODO: Access to the user's G+ account has been revoked.  Per the developer terms, delete
        // any stored user data here.
    }

    @Override
    protected void onPlusClientSignOut() {
        mPlusClient.clearDefaultAccount();
        mPlusClient.disconnect();
    }

    private void authenticateUser(Person user) {
        progressDialog.show();
        if (NetworkManager.isNetworkConnected(this)) {
            try{
                RequestParams requestParams = new RequestParams();
                requestParams.add("email", mPlusClient.getAccountName());
                String displayName = user.getDisplayName();
                String[] names = displayName.split(" ");
                requestParams.add("first_name", names[0]);
                requestParams.add("last_name", names[1]);
                requestParams.add("dob", user.getBirthday());
                requestParams.add("google_link", user.getUrl());
                requestParams.add("address", user.getCurrentLocation());
                requestParams.add("gender", user.getGender() + "");
                requestParams.add("photograph", (user.getImage().getUrl()));
                saveUserEmail(mPlusClient.getAccountName());
                NetworkClient.registerThroughSocialMedia(LoginActivity.this, requestParams, LoginActivity.this);
            }catch (Exception ex){
                Toast.makeText(this, "Please Check Network Connection", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please Check Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void enableDisconnectButtons() {
        //Set up sign out and disconnect buttons.

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                updateConnectButtonState();
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revokeAccess();
                updateConnectButtonState();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userToken = StorageHelper.getUserDetails(this, "auth_token");
        String phnVerified = StorageHelper.getUserDetails(this, "phone_verified");
        if (userToken != null && phnVerified != null) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            setContentView(R.layout.activity_login);
            initialize();
        }
    }


    /* This method get all views references */
    private void initialize() {
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
        progressDialog.setMessage("Logging In...");
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
                Log.d("FMC1:", "Google + clicked");
                if (supportsGooglePlayServices()) {
                    Log.d("FMC1:", "Google Play Support");
                    signIn();
                } else {
                    mPlusSignInButton.setVisibility(View.GONE);
                }
                break;
            case R.id.action_forgot_password:
                callForgotPasswordActivity();
                break;
        }
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
            Log.d("FMC1:", "email:" + userId + "\n Password:" + userPassword);
            if (NetworkManager.isNetworkConnected(this)) {
                progressDialog.show();
                RequestParams requestParams = new RequestParams();
                requestParams.add("email", userId);
                requestParams.add("password", userPassword);
                saveUserEmail(userId);
                NetworkClient.login(this, requestParams, this);
            } else
                Toast.makeText(this, "Check internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateLoginForm(String userId, String userPassword) {
        boolean isValid = true;
        if (userId.equals("")) {
            isValid = false;
            showErrorMessage(inputUserName);
        } else if (userPassword.equals("")) {
            isValid = false;
            showErrorMessage(inputPassword);
        }
        return isValid;
    }

    private void showErrorMessage(final EditText editText) {
        editText.setError(getResources().getString(R.string.error_field_required));
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
        if (requestCode == OUR_REQUEST_CODE) {
            mIntentInProgress = false;
            if (!mPlusClient.isConnecting()) {
                mPlusClient.connect();
            }
        } else { // If Result from Facebook
            Session session = Session.getActiveSession();
            session.onActivityResult(this, requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                getSession();
            }
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
                    Log.d("FMC1:", "session opened");
                    Log.d("FMC1: Permissions:", session.getPermissions().toString());
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, com.facebook.Response response) {
                            if (user != null) {
                                callWebservice(user);
                            } else {
                                Log.d("FMC1:", "user null");
                            }
                        }
                    }).executeAsync();
                } else {
                    Log.d("FMC1:", "session not opened");
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
        try {
            requestParams.add("address", user.getLocation().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestParams.add("gender", (String) user.getProperty("gender"));
        requestParams.add("photograph", "http://graph.facebook.com/" + user.getId() + "/picture?type=large");
        saveUserEmail((String) user.getProperty("email"));
        NetworkClient.registerThroughSocialMedia(LoginActivity.this, requestParams, LoginActivity.this);
    }

    private void getPhoneNumber(final RequestParams requestParams) {
        progressDialog.dismiss();
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Phone number is must!!!");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.phone_number_dialog);
        final EditText phoneEditText = (EditText) dialog.findViewById(R.id.phoneEditText);
        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phnNum = phoneEditText.getText().toString();
                if (phnNum.equals("") || phnNum.length()<10){
                    phoneEditText.setError("Enter valid phone number");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            phoneEditText.setError(null);
                        }
                    }, 3500);
                } else {
                    dialog.dismiss();
                    requestParams.add("phonenumber", phnNum);
                    saveUserPhoneNumber(phnNum);
                    NetworkClient.updatePhoneForSocialMedia(LoginActivity.this, requestParams, LoginActivity.this);
                    progressDialog.show();
                }
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbClearToken();
                if (mPlusClient.isConnected()) {
                    mPlusClient.clearDefaultAccount();
                    mPlusClient.disconnect();
                }
                finish();
                dialog.dismiss();
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

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        Log.d("FMC1:", "resolve SignIn Error");
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        OUR_REQUEST_CODE, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mPlusClient.connect();
            }
        }
    }


    /**
     * Successfully connected (called by PlusClient)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        updateConnectButtonState();
        setProgressBarVisible(false);
        onPlusClientSignIn();
    }

    public void getProfileInformation() {
        Person currentPerson = mPlusClient.getCurrentPerson();
        String accountName = mPlusClient.getAccountName();
//        Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
        authenticateUser(currentPerson);
    }

    private void populateUserFields(Person currentPerson) {
        Log.d("User:", "Name:" + currentPerson.getName());
        Log.d("User:", "Display Name:" + currentPerson.getDisplayName());
        Log.d("User:", "Nick Name:" + currentPerson.getNickname());
        Log.d("User:", "Date of Birth:" + currentPerson.getBirthday());
        Log.d("User:", "Current Location:" + currentPerson.getCurrentLocation());
        Log.d("User:", "Gender:" + currentPerson.getGender());
        Log.d("User:", "Gender:" + currentPerson.getImage());
    }

    @Override
    public void successOperation(Object object) {
        progressDialog.dismiss();
        if(object == null) {
            RequestParams requestParams = new RequestParams();
            requestParams.add("email", StorageHelper.getUserDetails(this, "user_email"));
            getPhoneNumber(requestParams);
            return;
        }
        Response response = (Response) object;

        if(response.getMessage().equals("Successfully Updated Please Validate your profile to use it ")){
            if(StorageHelper.getUserDetails(this,"phone_number") == null){
                RequestParams requestParams = new RequestParams();
                requestParams.add("email", response.getData().getEmail());
                getPhoneNumber(requestParams);
                return;
            }
            startActivity(new Intent(this, ValidatePhoneActivity.class));
            finish();
            return;
        }

        if (response.getAuthToken() != null && response.getData().getId() != null)
            saveUser(response.getAuthToken(), response.getData().getId());
        if(response.getMessage().equals("Success"))
            saveUserPhn("True");
        progressDialog.dismiss();
        if (response.getData().getPhonenumber() == null) {
            RequestParams requestParams = new RequestParams();
            requestParams.add("email", response.getData().getEmail());
            getPhoneNumber(requestParams);
        } else {
            saveUserPhoneNumber(response.getData().getPhonenumber());
            Intent intent = new Intent(this, DashboardActivity.class);
            finish();
            startActivity(intent);
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

    void showSharedPreferences(String from){
        Log.d("FMC",from + " auth_token : " + StorageHelper.getUserDetails(this,"auth_token"));
        Log.d("FMC",from + " user_id : " + StorageHelper.getUserDetails(this,"user_id"));
        Log.d("FMC",from + " phone_verified : " + StorageHelper.getUserDetails(this,"phone_verified"));
        Log.d("FMC",from + " user_email : " + StorageHelper.getUserDetails(this,"user_email"));
        Log.d("FMC",from + " phone_number : " + StorageHelper.getUserDetails(this,"phone_number"));
    }

    @Override
    public void failureOperation(Object message) {
        progressDialog.dismiss();
        String msg = (String) message;
        if(msg.equals("Validate Phone number to login") || msg.equals("Successfully Updated Please Validate your profile to use it ")){
            startActivity(new Intent(this, ValidatePhoneActivity.class));
            finish();
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        fbClearToken();
        if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
            mPlusClient.disconnect();
        }
        finish();
        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
    }
}



