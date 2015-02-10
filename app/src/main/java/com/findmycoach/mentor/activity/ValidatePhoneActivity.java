package com.findmycoach.mentor.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Session;
import com.findmycoach.mentor.beans.authentication.Response;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.findmycoach.mentor.util.NetworkManager;
import com.findmycoach.mentor.util.StorageHelper;
import com.fmc.mentor.findmycoach.R;
import com.loopj.android.http.RequestParams;

/**
 * Created by prem on 6/2/15.
 */
public class ValidatePhoneActivity extends Activity implements View.OnClickListener, Callback {

    private EditText verificationCode;
    private String email;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_phone);
        initView();
    }

    private void initView() {
        email = StorageHelper.getUserDetails(this,"user_email");
        progressDialog = new ProgressDialog(this);
        verificationCode = (EditText) findViewById(R.id.verificationCodeET);
        findViewById(R.id.btnVerify).setOnClickListener(this);
        findViewById(R.id.btnResend).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnResend){
            if(email != null){
                RequestParams requestParams = new RequestParams();
                requestParams.add("email", email);
                getPhoneNumber(requestParams);
            }
        }else if(id == R.id.btnVerify){
            sendVerificationCode();
        }
    }

    private void sendVerificationCode() {
        String code = verificationCode.getText().toString();
        if (code.equals("") || code.length() != 6){
            verificationCode.setError("Enter 6 character code here.");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    verificationCode.setError(null);
                }
            }, 3500);
        }else{
            if(email != null && NetworkManager.isNetworkConnected(this)){
                progressDialog.setMessage("Verifying...");
                progressDialog.show();
                RequestParams requestParams = new RequestParams();
                requestParams.add("email",email);
                requestParams.add("otp", code);
                NetworkClient.verifyPhoneNumber(this, requestParams, this);
                Log.d("FMC", "Sent code for verification \n Email : " + email + "\n OTP : " + code);
            }else{
                Toast.makeText(this, "Check connection.",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void successOperation(Object object) {
        progressDialog.dismiss();
        Response response = (Response) object;
        if(response.getMessage().equals("Successfully Updated Please Validate your profile to use it ")){
            if(response.getData() != null){
                if(response.getData().getPhonenumber() != null)
                    StorageHelper.storePreference(ValidatePhoneActivity.this, "phone_number", response.getData().getPhonenumber());
            }
            Toast.makeText(this,"Please enter code sent to your registered number.",Toast.LENGTH_LONG).show();
        }else if(response.getMessage().equals("Success , please login")){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


//        saveUser(response.getAuthToken(), response.getData().getId());
//        finish();
//        startActivity(new Intent(this, DashboardActivity.class));


    }

    @Override
    public void failureOperation(Object object) {
        progressDialog.dismiss();
        Toast.makeText(this,(String) object,Toast.LENGTH_LONG).show();
    }

    private void saveUser(String authToken, String userId) {
        StorageHelper.storePreference(this, "auth_token", authToken);
        StorageHelper.storePreference(this, "user_id", userId);
        StorageHelper.storePreference(this, "phone_verified", "True");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.logout, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         if (item.getItemId() == R.id.log_out) {
            logout();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        StorageHelper.clearUser(this);
        LoginActivity.doLogout = true;
        fbClearToken();
        this.finish();
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

    private void getPhoneNumber(final RequestParams requestParams) {
        final String lastPhoneNumber = StorageHelper.getUserDetails(this,"phone_number");
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Phone number is must!!!");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.phone_number_dialog);
        final EditText phoneEditText = (EditText) dialog.findViewById(R.id.phoneEditText);
        phoneEditText.setText(lastPhoneNumber);
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
                    progressDialog.setMessage("Sending code...");
                    if(lastPhoneNumber.equals(phnNum)){
                        progressDialog.show();
                        NetworkClient.repostOtp(ValidatePhoneActivity.this, requestParams, ValidatePhoneActivity.this);
                    }else{
                        StorageHelper.storePreference(ValidatePhoneActivity.this, "phone_number", phnNum);
                        requestParams.add("phonenumber", phnNum);
                        progressDialog.show();
                        NetworkClient.updatePhoneForSocialMedia(ValidatePhoneActivity.this, requestParams, ValidatePhoneActivity.this);
                    }
                }
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
