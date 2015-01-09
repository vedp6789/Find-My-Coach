package com.findmycoach.mentor.activity;

import android.app.Activity;
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
import com.findmycoach.mentor.util.StorageHelper;
import com.fmc.mentor.findmycoach.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.loopj.android.http.RequestParams;

import java.util.Arrays;

/**
 * A login screen that offers login via email/password, Facebook and Google+ sign in.
 */
public class LoginActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, View.OnClickListener, Callback {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    // UI references.
    private TextView registerAction;
    private EditText inputUserName;
    private EditText inputPassword;
    private Button actionLogin;
    private LoginButton actionFacebook;
    private SignInButton actionGooglePlus;
    private TextView forgotAction;
    /* Client used to interact with Google APIs. */
    private PlusClient mPlusClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userToken = StorageHelper.getUserDetails(this, "auth_token");
        if (userToken != null) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            setContentView(R.layout.activity_login);
            initialize();
            initializeGoogleClient();
        }
    }

    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mPlusClient.isConnected()) {
            mPlusClient.disconnect();
        }
    }

    private void initializeGoogleClient() {
        mPlusClient =
                new PlusClient.Builder(this, this, this).setScopes(Scopes.PLUS_LOGIN,
                        Scopes.PLUS_ME).build();
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
//        actionFacebook.setReadPermissions("email");
        actionFacebook.setReadPermissions(Arrays.asList("user_location", "user_birthday", "email"));
        actionGooglePlus = (SignInButton) findViewById(R.id.google_login_button);
        actionGooglePlus.setOnClickListener(this);
        forgotAction = (TextView) findViewById(R.id.action_forgot_password);
        forgotAction.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In...");
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
            case R.id.facebook_login_button:
                //TODO: Implement Facebook Authentication
                break;
            case R.id.google_login_button:
                Log.d("FMC1:", "Google + clicked");
                if (supportsGooglePlayServices()) {
                    Log.d("FMC1:", "Google Play Support");
                    connectGoogle();
                } else {
                    actionGooglePlus.setVisibility(View.GONE);
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

    private void connectGoogle() {
        Log.d("FMC1:", "connect Google");
        mPlusClient.connect();
    }

    private void logIn() {
        String userId = inputUserName.getText().toString();
        String userPassword = inputPassword.getText().toString();
        boolean isFormValid = validateLoginForm(userId, userPassword);
        if (isFormValid) {
            Log.d("FMC1:", "email:" + userId + "\n Password:" + userPassword);
            progressDialog.show();
            RequestParams requestParams = new RequestParams();
            requestParams.add("email", userId);
            requestParams.add("password", userPassword);
            NetworkClient.login(this, requestParams, this);
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
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }
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
                                Log.d("FMC1:", "user not null");
                                Log.d("FMC1", (String) user.getProperty("email"));
                                Log.d("FMC1", (String) user.getFirstName());
                                Log.d("FMC1", (String) user.getLastName());
                                Log.d("FMC1", (String) user.getLink());
                                Log.d("FMC1", (String) user.getBirthday());
                                Log.d("FMC1", user.getLocation() + "");
                                Log.d("FMC1", user.asMap() + "");

//                                String userImage = "http://graph.facebook.com/" + user.getId() + "/picture?type=large";
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
        requestParams.add("address", user.getLocation().getName());
        requestParams.add("gender", (String) user.getProperty("gender"));
        requestParams.add("photograph", "http://graph.facebook.com/" + user.getId() + "/picture?type=large");
        NetworkClient.registerThroughSocialMedia(this, requestParams, this);
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
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mPlusClient.connect();
            }
        }
    }


    private void getProfileInformation() {
        Person currentPerson = mPlusClient.getCurrentPerson();
        String accountName = mPlusClient.getAccountName();
        Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        getProfileInformation();
    }

    @Override
    public void onDisconnected() {

    }


    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    public void successOperation(Object object) {
        Response response = (Response) object;
        saveUser(response.getAuthToken(), response.getData().getId());
        progressDialog.dismiss();
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    private void saveUser(String authToken, String userId) {
        StorageHelper.storePreference(this, "auth_token", authToken);
        StorageHelper.storePreference(this, "user_id", userId);
        Log.d("FMC1:", "User Id:" + userId);
    }

    @Override
    public void failureOperation(Object message) {
        progressDialog.dismiss();
        Toast.makeText(this, (String) message, Toast.LENGTH_LONG).show();
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
//    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//        private final String mEmail;
//        private final String mPassword;
//
//        UserLoginTask(String email, String password) {
//            mEmail = email;
//            mPassword = password;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//
//            try {
//                // Simulate network access.
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                return false;
//            }
//
//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }
//
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            mAuthTask = null;
//            showProgress(false);
//
//            if (success) {
//                finish();
//            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mAuthTask = null;
//            showProgress(false);
//        }
//    }
}



