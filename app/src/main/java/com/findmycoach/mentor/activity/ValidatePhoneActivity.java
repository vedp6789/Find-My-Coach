package com.findmycoach.mentor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fmc.mentor.findmycoach.R;

/**
 * Created by prem on 6/2/15.
 */
public class ValidatePhoneActivity extends Activity implements View.OnClickListener {

    private EditText verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_phone);
        initView();
    }

    private void initView() {
        verificationCode = (EditText) findViewById(R.id.verificationCodeET);
        findViewById(R.id.btnVerify).setOnClickListener(this);
        findViewById(R.id.btnResend).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnResend){
            Toast.makeText(this,"Resend Click",Toast.LENGTH_LONG).show();
        }else if(id == R.id.btnVerify){
            Toast.makeText(this,"Verify Click",Toast.LENGTH_LONG).show();
        }
    }
}
