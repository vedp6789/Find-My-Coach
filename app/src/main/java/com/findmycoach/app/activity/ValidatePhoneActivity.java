package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.findmycoach.app.R;
import com.findmycoach.app.adapter.CountryCodeAdapter;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by prem on 6/2/15.
 */
public class ValidatePhoneActivity extends Activity implements View.OnClickListener, Callback {

    private EditText verificationCode;
    private String email;
    private Dialog progressDialog;
    private int user_group;
    private TextView countryCodeTV, msg;
    private ArrayList<String> country_code;

    public static ValidatePhoneActivity validatePhoneActivity;

    private static final String TAG = "FMC";
    private String from=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validatePhoneActivity = this;

        from=getIntent().getStringExtra("from");
        /** Getting user group of user, logout and close if user group not present **/
        try {
            user_group = Integer.parseInt(StorageHelper.getUserGroup(this, "user_group"));
        } catch (Exception e) {
            e.printStackTrace();
            logout();
            return;
        }
        setContentView(R.layout.activity_validate_phone);
        initView();
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        validatePhoneActivity = null;
    }

    public void getOtpFromMsg(String OTP){
        if(OTP.length() == 6 && verificationCode != null){
            verificationCode.setText(OTP);
            try{
                sendVerificationCode();
            }catch (Exception ignored){}
        }
    }

    /**
     * Getting references of view
     */
    private void initView() {
        email = StorageHelper.getUserDetails(this, "user_email");
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setContentView(R.layout.progressbar_textview);
        msg = (TextView) progressDialog.findViewById(R.id.msg);
        verificationCode = (EditText) findViewById(R.id.verificationCodeET);
        findViewById(R.id.btnVerify).setOnClickListener(this);
        findViewById(R.id.btnResend).setOnClickListener(this);
        Log.e(TAG, email);

        verificationCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    sendVerificationCode();
                }
                return false;
            }
        });

        findViewById(R.id.action_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnResend) {
            if (email != null) {
                RequestParams requestParams = new RequestParams();
                requestParams.add("email", email);
                getPhoneNumber(requestParams);
            }
        } else if (id == R.id.btnVerify) {
            sendVerificationCode();
        } else if (id == R.id.countryCodeTV) {
            showCountryCodeDialog();
        }
    }


    /**
     * Verifying OTP
     */
    private void sendVerificationCode() {
        String code = verificationCode.getText().toString();

        /** Show error is OTP is null */
        if (code.equals("") || code.length() != 6) {
            verificationCode.setError(getResources().getString(R.string.hint_enter_code));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    verificationCode.setError(null);
                }
            }, 3500);
        }

        /** Verifying entered OTP by sending it to server */
        else {
            if (email != null && NetworkManager.isNetworkConnected(this)) {
                msg.setText(getResources().getString(R.string.verifying));
                progressDialog.show();
                RequestParams requestParams = new RequestParams();
                requestParams.add("email", email);
                requestParams.add("otp", code);
                requestParams.add("user_group", user_group + "");
                NetworkClient.verifyPhoneNumber(this, requestParams, this, 27);
                Log.d(TAG, "Sent code for verification \n Email : " + email + "\n OTP : " + code);
            } else {
                Toast.makeText(this, getResources().getString(R.string.check_connection), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Response response = (Response) object;

        /** Phone number is updated or OTP is resend then response message is shown */
        if (calledApiValue == 26 || calledApiValue == 28) {
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        /** Entered OTP is verified successful */
        else if (calledApiValue == 27) {
            if(response.getData() == null){
                Toast toast =  Toast.makeText(ValidatePhoneActivity.this, response.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            else if (response.getAuthToken() != null && !response.getAuthToken().equals(""))
                saveUser(response.getAuthToken(), response.getData().getId());
        }

        /** Opens Dashboard activity*/
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
        Log.e(TAG, "DashBoard");

        /** If newly registered user is mentee then open PaymentDetail Activity for getting card details */
        if (user_group == 2) {
            Log.e(TAG, "User group");
            Log.d(TAG,"user_group and payment initiate :"+user_group);
            Log.d(TAG,"Launched from : "+from);
            if (from == null || !from.equals("ChangePhoneNoFragment")) {
                Log.e(TAG, "DashBoard if");
                startActivity(new Intent(this, PaymentDetailsActivity.class));
            }


        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
    }

    /**
     * Saving user details in shared preferences
     */
    private void saveUser(String authToken, String userId) {
        StorageHelper.storePreference(this, "auth_token", authToken);
        StorageHelper.storePreference(this, "user_id", userId);
        StorageHelper.storePreference(this, "phone_verified", "True");
    }

    /**
     * Sign out clicked
     */
    private void logout() {
        StorageHelper.clearUser(this);
        fbClearToken();

        /** Setting flag is G+ user signing out */
        String loginWith = StorageHelper.getUserDetails(this, "login_with");
        if (loginWith == null || loginWith.equals("G+")) {
            LoginActivity.doLogout = true;
        }

        this.finish();
        startActivity(new Intent(ValidatePhoneActivity.this, LoginActivity.class));
    }

    /**
     * Clearing FB user if exists
     */
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
     * Dialog to get/update phone number and sending OTP
     */
    private void getPhoneNumber(final RequestParams requestParams) {
        final String lastPhoneNumber = StorageHelper.getUserDetails(this, "phone_number");
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.phone_number_dialog);
        final EditText phoneEditText = (EditText) dialog.findViewById(R.id.phoneEditText);
        countryCodeTV = (TextView) dialog.findViewById(R.id.countryCodeTV);
        countryCodeTV.setText(getCountryZipCode());
        countryCodeTV.setOnClickListener(this);
        phoneEditText.setText(lastPhoneNumber);

        /** Ok button clicked */
        Button okButton = (Button) dialog.findViewById(R.id.okButton);
        okButton.setText(getResources().getString(R.string.send));
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phnNum = phoneEditText.getText().toString();

                /** If phone number is null */
                if (phnNum.equals("") || phnNum.length() < 8) {
                    phoneEditText.setError(getResources().getString(R.string.enter_valid_phone_no));
                }

                /** if country code is not update automatically or not selected */
                else if (countryCodeTV.getText().toString().trim().equals("Select")) {
                    countryCodeTV.setError(getResources().getString(R.string.select_country_code));
                } else {
                    dialog.dismiss();
                    requestParams.add("user_group", user_group + "");
                    msg.setText(getResources().getString(R.string.sending_code));

                    /** Phone number is not changed, sending OTP to registered phone number */
                    if (lastPhoneNumber != null && lastPhoneNumber.equals(phnNum)) {
                        progressDialog.show();
                        NetworkClient.repostOtp(ValidatePhoneActivity.this, requestParams, ValidatePhoneActivity.this, 28);
                    }

                    /** Phone number is provided with country code, updating user's phone number in server and sending OTP to that number */
                    else {
                        StorageHelper.storePreference(ValidatePhoneActivity.this, "phone_number", phnNum);
                        requestParams.add("phone_number", countryCodeTV.getText().toString().trim() + "-" + phnNum);
                        Log.e("Validate phone dialog", "phone_number : " + countryCodeTV.getText().toString().trim() + "-" + phnNum);
                        Log.d(TAG, countryCodeTV.getText().toString().trim() + phnNum);
                        progressDialog.show();
                        NetworkClient.updatePhoneForSocialMedia(ValidatePhoneActivity.this, requestParams, ValidatePhoneActivity.this, 26);
                    }
                }

                Log.e(TAG, countryCodeTV.getText().toString().trim() + "-" + phnNum);
            }
        });

        /** Cancel button clicked */
        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
                countryCodeTV.setError(null);
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
        String CountryZipCode = getResources().getString(R.string.select);
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
}
