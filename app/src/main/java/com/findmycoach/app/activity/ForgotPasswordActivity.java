package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.R;
import com.loopj.android.http.RequestParams;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends Activity implements Callback {

    private EditText emailInput;
    private Button submitButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        applyActionbarProperties();
        initialize();
        applyActions();
    }

    private void applyActions() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    requestForgetPassword();
                }
            }
        });
    }

    private void requestForgetPassword() {
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("email", emailInput.getText().toString());
        NetworkClient.forgetPassword(this, requestParams, this, 3);
    }

    private boolean isInputValid() {
        String inputEmail = emailInput.getText().toString();
        if (!isEmailValid(inputEmail)) {
            emailInput.setError(getResources().getString(R.string.enter_valid_email));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    emailInput.setError(null);
                }
            }, 2500);

            return false;
        }
        return true;
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

    private void initialize() {
        emailInput = (EditText) findViewById(R.id.input_email);
        submitButton = (Button) findViewById(R.id.action_submit);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
    }

    private void applyActionbarProperties() {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        String message = (String) object;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        String message = (String) object;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
