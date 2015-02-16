package com.findmycoach.mentor.activity;

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

import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.fmc.mentor.findmycoach.R;
import com.loopj.android.http.RequestParams;

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
        NetworkClient.forgetPassword(this, requestParams, this);
    }

    private boolean isInputValid() {
        String inputEmail = emailInput.getText().toString();
        if (inputEmail.isEmpty()) {
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
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void successOperation(Object object) {
        progressDialog.dismiss();
        String message = (String) object;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    public void failureOperation(Object object) {
        progressDialog.dismiss();
        String message = (String) object;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
