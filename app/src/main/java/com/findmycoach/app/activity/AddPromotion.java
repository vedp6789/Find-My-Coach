package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

/**
 * Created by ved on 11/8/15.
 */
public class AddPromotion extends Activity implements Callback {
    RelativeLayout rl_discount_percentage, rl_free_classes, rl_mandatory_classes;
    TextView tv_title;
    EditText et_promotion_title, et_promotion_discount, et_free_classes, et_mandatory_classes;
    Button b_add_promotion;
    ImageView b_back_button;
    RadioGroup rg_promotion_type;
    RadioButton rb_discount_type, rb_free_classes;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion);
        initViews();
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        rl_discount_percentage = (RelativeLayout) findViewById(R.id.rlDiscountPercentage);
        rl_free_classes = (RelativeLayout) findViewById(R.id.rlFreeClasses);
        rl_mandatory_classes = (RelativeLayout) findViewById(R.id.rlMandatoryClasses);
        tv_title = (TextView) findViewById(R.id.title);
        tv_title.setText(getResources().getString(R.string.create_promotion));
        b_back_button = (ImageView) findViewById(R.id.backButton);
        et_promotion_title = (EditText) findViewById(R.id.et_title_val);
        et_promotion_discount = (EditText) findViewById(R.id.et_discount_percentage);
        et_free_classes = (EditText) findViewById(R.id.et_free_classes);
        et_mandatory_classes = (EditText) findViewById(R.id.et_mandatory_classes);
        b_add_promotion = (Button) findViewById(R.id.b_add_promotion);
        rg_promotion_type = (RadioGroup) findViewById(R.id.rg_promotion_type);
        rb_discount_type = (RadioButton) findViewById(R.id.rb_discount);
        rb_free_classes = (RadioButton) findViewById(R.id.rb_free_Class);
        b_add_promotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("promotion_title", et_promotion_title.getText().toString());
                    requestParams.add("is_active","1");
                    if (rb_discount_type.isChecked()) {
                        requestParams.add("promotion_type", "discount");
                        requestParams.add("discount_percentage", et_promotion_discount.getText().toString());
                    } else {
                        requestParams.add("promotion_type", "trial");
                        requestParams.add("free_classes", et_free_classes.getText().toString());
                        requestParams.add("free_min_classes", et_mandatory_classes.getText().toString());
                    }
                    progressDialog.show();
                    NetworkClient.addPromotion(AddPromotion.this, requestParams, StorageHelper.getUserGroup(AddPromotion.this, "auth_token"), AddPromotion.this, 59);

                }
            }
        });
        rl_free_classes.setVisibility(View.GONE);
        rl_mandatory_classes.setVisibility(View.GONE);
        rl_discount_percentage.setVisibility(View.VISIBLE);
        et_promotion_discount.setText("");

        b_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rg_promotion_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_discount) {
                    rl_free_classes.setVisibility(View.GONE);
                    rl_mandatory_classes.setVisibility(View.GONE);
                    rl_discount_percentage.setVisibility(View.VISIBLE);
                    et_promotion_discount.setText("");

                } else if (checkedId == R.id.rb_free_Class) {
                    rl_free_classes.setVisibility(View.VISIBLE);
                    rl_mandatory_classes.setVisibility(View.VISIBLE);
                    rl_discount_percentage.setVisibility(View.GONE);
                    et_free_classes.setText("");
                    et_mandatory_classes.setText("");
                }
            }
        });

    }

    private boolean validate() {

        boolean isValid = true;
        if (et_promotion_title.getText().toString().trim() == "") {
            et_promotion_title.setError(getResources().getString(R.string.add_promotion_title));
            if (isValid)
                et_promotion_title.requestFocus();
            isValid = false;

        }
        if (rb_discount_type.isSelected()) {
            if (et_promotion_discount.getText().toString().trim() == "") {
                et_promotion_discount.setError(getResources().getString(R.string.add_dicount_percentage));
                if (isValid)
                    et_promotion_discount.requestFocus();
                isValid = false;
            }

        } else {
            if (et_free_classes.getText().toString().trim() == "") {
                et_free_classes.setError(getResources().getString(R.string.add_free_classes));
                if (isValid)
                    et_free_classes.requestFocus();
                isValid = false;

            }

            if (et_mandatory_classes.getText().toString().trim() == "") {
                et_mandatory_classes.setError(getResources().getString(R.string.add_mandatory_classes));
                if (isValid)
                    et_mandatory_classes.requestFocus();
                isValid = false;
            }

        }
        return isValid;

    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        try {
            JSONObject jsonObject = (JSONObject) object;
            Log.e("FMC: ","added promotion response: "+jsonObject.toString());
            if (jsonObject.getInt("data") > 0) {
                Toast.makeText(AddPromotion.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                if(MyPromotions.myPromotions != null ){
                    MyPromotions.myPromotions.onActivityCompletion();
                }

                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(AddPromotion.this, (String) object, Toast.LENGTH_SHORT).show();
    }
}
