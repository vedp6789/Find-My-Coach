package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.R;
import com.loopj.android.http.RequestParams;

/**
 * Created by prem on 6/2/15.
 */
public class ValidatePhoneActivity extends Activity implements View.OnClickListener, Callback {

    private EditText verificationCode;
    private String email;
    private ProgressDialog progressDialog;
    private int user_group;
    private TextView countryCodeTV;
    private String[] country_code;

    private static final String TAG="FMC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            user_group = Integer.parseInt(StorageHelper.getUserGroup(this, "user_group"));
        }catch (Exception e){
            e.printStackTrace();
            logout();
            return;
        }

        setContentView(R.layout.activity_validate_phone);
        initView();
    }

    private void initView() {
        email = StorageHelper.getUserDetails(this,"user_email");
        progressDialog = new ProgressDialog(this);
        verificationCode = (EditText) findViewById(R.id.verificationCodeET);
        findViewById(R.id.btnVerify).setOnClickListener(this);
        findViewById(R.id.btnResend).setOnClickListener(this);
        Log.e(TAG, email);
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
        }else if(id == R.id.countryCodeTV){
            showCountryCodeDialog();
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


    private void sendVerificationCode() {
        String code = verificationCode.getText().toString();
        if (code.equals("") || code.length() != 6){
            verificationCode.setError(getResources().getString(R.string.hint_enter_code));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    verificationCode.setError(null);
                }
            }, 3500);
        }else{
            if(email != null && NetworkManager.isNetworkConnected(this)){
                progressDialog.setMessage(getResources().getString(R.string.verifying));
                progressDialog.show();
                RequestParams requestParams = new RequestParams();
                requestParams.add("email",email);
                requestParams.add("otp", code);
                requestParams.add("user_group", user_group+"");
                NetworkClient.verifyPhoneNumber(this, requestParams, this, 27);
                Log.d(TAG, "Sent code for verification \n Email : " + email + "\n OTP : " + code);
            }else{
                Toast.makeText(this, getResources().getString(R.string.check_connection),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Response response = (Response) object;

        if(calledApiValue == 26 || calledApiValue == 28){
            Toast.makeText(this,response.getMessage(),Toast.LENGTH_LONG).show();
            return;
        }else if(calledApiValue == 27){
        if(response.getAuthToken() != null)
            saveUser(response.getAuthToken(), response.getData().getId());
        }
        finish();
        startActivity(new Intent(this, DashboardActivity.class));
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
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
        dialog.setTitle(getResources().getString(R.string.phone_no_must));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.phone_number_dialog);
        final EditText phoneEditText = (EditText) dialog.findViewById(R.id.phoneEditText);
        countryCodeTV = (TextView) dialog.findViewById(R.id.countryCodeTV);
        countryCodeTV.setText(getCountryZipCode());
        countryCodeTV.setOnClickListener(this);
        phoneEditText.setText(lastPhoneNumber);
        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phnNum = phoneEditText.getText().toString();
                if (phnNum.equals("") || phnNum.length()<10){
                    phoneEditText.setError(getResources().getString(R.string.enter_valid_phone_no));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            phoneEditText.setError(null);
                        }
                    }, 3500);
                }else if(countryCodeTV.getText().toString().trim().equals("")){
                    countryCodeTV.setError(getResources().getString(R.string.select_country_code));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            countryCodeTV.setError(null);
                        }
                    }, 3500);
                }else {
                    dialog.dismiss();
                    requestParams.add("phone_number", phnNum);
                    requestParams.add("user_group", user_group+"");
                    progressDialog.setMessage(getResources().getString(R.string.sending_code));
                    if(lastPhoneNumber != null && lastPhoneNumber.equals(phnNum)){
                        progressDialog.show();
                        NetworkClient.repostOtp(ValidatePhoneActivity.this, requestParams, ValidatePhoneActivity.this, 28);
                    }else{
                        StorageHelper.storePreference(ValidatePhoneActivity.this, "phone_number", phnNum);
                        requestParams.add("phone_number", phnNum);
                        progressDialog.show();
                        NetworkClient.updatePhoneForSocialMedia(ValidatePhoneActivity.this, requestParams, ValidatePhoneActivity.this, 26);
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

    public String getCountryZipCode(){
        String CountryID = "";
        String CountryZipCode = "";
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        CountryID= manager.getSimCountryIso().toUpperCase();
        country_code = this.getResources().getStringArray(R.array.country_codes);
        for(int i=0;i< country_code.length;i++){
            String[] g = country_code[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }
}
