package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.authentication.Data;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ved on 11/8/15.
 */
public class AddPromotion extends Activity implements Callback, View.OnClickListener {
    RelativeLayout rl_discount_percentage, rl_free_classes, rlNumberOfClasses;
    TextView tv_title, tv_note, tv_min_classes_val;
    Spinner spNumberOfClasses;
    EditText et_promotion_title, et_promotion_discount, et_free_classes;
    Button b_add_promotion;
    ImageView b_back_button;
    RadioGroup rg_promotion_type;
    RadioButton rb_discount_type, rb_free_classes;
    ProgressDialog progressDialog;
    private Data userInfo;
    private String TAG = "FMC";
    private ArrayList<String> number_of_lessons;
    private Dialog dialog;
    private TextView tvDialogMessage, tvDialogTitle;
    private Button bDialog1, bDialog2;
    private ImageView bDialogBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion);
        initViews();
    }

    private void initViews() {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_action_on_promotion);
        //myDialog.setTitle();
        dialog.setCancelable(false);
        tvDialogMessage = (TextView) dialog.findViewById(R.id.tv_message);
        tvDialogTitle = (TextView) dialog.findViewById(R.id.title);
        tvDialogTitle.setText(getResources().getString(R.string.promotion));
        bDialogBack = (ImageView) dialog.findViewById(R.id.backButton);
        bDialog1 = (Button) dialog.findViewById(R.id.b1);
        bDialog2 = (Button) dialog.findViewById(R.id.b2);
        bDialogBack.setVisibility(View.GONE);
        bDialog1.setText(getResources().getString(R.string.yes));
        bDialog2.setText(getResources().getString(R.string.no));
        bDialog1.setVisibility(View.VISIBLE);
        bDialog2.setVisibility(View.VISIBLE);
        bDialog1.setOnClickListener(this);
        bDialog2.setOnClickListener(this);

        String profile_details = StorageHelper.getUserProfile(this);
        Log.e(TAG, "profile details: " + profile_details);
        try {
            JSONObject jsonObject = new JSONObject(profile_details);
            if (profile_details != null) {
                userInfo = new Gson().fromJson(jsonObject.getString("data"), Data.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.e(TAG, "min class" + userInfo.getMin_number_of_classes());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        rl_discount_percentage = (RelativeLayout) findViewById(R.id.rlDiscountPercentage);
        rl_free_classes = (RelativeLayout) findViewById(R.id.rlFreeClasses);
        rlNumberOfClasses = (RelativeLayout) findViewById(R.id.rlNumberOfClasses);
        tv_title = (TextView) findViewById(R.id.title);
        tv_note = (TextView) findViewById(R.id.tv_note);
        tv_title.setText(getResources().getString(R.string.create_promotion));
        tv_min_classes_val = (TextView) findViewById(R.id.tv_min_classes_val);
        number_of_lessons = new ArrayList<>();
        spNumberOfClasses = (Spinner) findViewById(R.id.spNumberOfClasses);
        if (userInfo.getMin_number_of_classes() >= 0) {
            for (int i = userInfo.getMin_number_of_classes(); i <= 156; i++) {
                number_of_lessons.add("" + i);
            }

        }

        spNumberOfClasses.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, number_of_lessons));

        b_back_button = (ImageView) findViewById(R.id.backButton);
        et_promotion_title = (EditText) findViewById(R.id.et_title_val);
        et_promotion_discount = (EditText) findViewById(R.id.et_discount_percentage);
        et_free_classes = (EditText) findViewById(R.id.et_free_classes);
        b_add_promotion = (Button) findViewById(R.id.b_add_promotion);
        rg_promotion_type = (RadioGroup) findViewById(R.id.rg_promotion_type);
        rb_discount_type = (RadioButton) findViewById(R.id.rb_discount);
        rb_free_classes = (RadioButton) findViewById(R.id.rb_free_Class);
        b_add_promotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    addPromotion();

                }
            }
        });
        rl_free_classes.setVisibility(View.GONE);
        rl_discount_percentage.setVisibility(View.VISIBLE);
        if (userInfo != null && userInfo.getMin_number_of_classes() != 0) {
            et_promotion_discount.setText("");
            tv_min_classes_val.setText("" + userInfo.getMin_number_of_classes());
        } else {
            et_promotion_discount.setText("");
            tv_min_classes_val.setText("");

        }
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
                    tv_note.setText(getResources().getString(R.string.discount_type_promotion_note));
                    rl_free_classes.setVisibility(View.GONE);
                    rl_discount_percentage.setVisibility(View.VISIBLE);
                    et_promotion_discount.setText("");
                    if (userInfo != null && userInfo.getMin_number_of_classes() != 0) {
                        tv_min_classes_val.setText("" + userInfo.getMin_number_of_classes());
                    }

                } else if (checkedId == R.id.rb_free_Class) {
                    tv_note.setText(getResources().getString(R.string.free_class_type_promotion_note));
                    rl_free_classes.setVisibility(View.VISIBLE);
                    rl_discount_percentage.setVisibility(View.GONE);
                    et_free_classes.setText("");
                    if (userInfo != null && userInfo.getMin_number_of_classes() != 0) {
                        tv_min_classes_val.setText("" + userInfo.getMin_number_of_classes());
                    } else {
                        tv_min_classes_val.setText("");
                    }
                }
            }
        });


    }

    private void addPromotion() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("promotion_title", et_promotion_title.getText().toString());
        requestParams.add("is_active", "1");
        if (rb_discount_type.isChecked()) {
            requestParams.add("promotion_type", "discount");
            requestParams.add("discount_number_of_classes", String.valueOf(spNumberOfClasses.getSelectedItem()));
            requestParams.add("discount_percentage", et_promotion_discount.getText().toString());
        } else {
            requestParams.add("promotion_type", "trial");
            requestParams.add("free_classes", et_free_classes.getText().toString());
            requestParams.add("free_min_classes", String.valueOf(spNumberOfClasses.getSelectedItem()));
        }
        progressDialog.show();
        NetworkClient.addPromotion(AddPromotion.this, requestParams, StorageHelper.getUserGroup(AddPromotion.this, "auth_token"), AddPromotion.this, 59);


    }

    private boolean validate() {

        boolean isValid = true;
        if (et_promotion_title.getText().toString().trim().isEmpty()) {
            et_promotion_title.setError(getResources().getString(R.string.add_promotion_title));
            if (isValid)
                et_promotion_title.requestFocus();
            isValid = false;

        }
        int selectedRadioGroupChild = rg_promotion_type.indexOfChild(findViewById(rg_promotion_type.getCheckedRadioButtonId()));
        if (selectedRadioGroupChild == 0) {
            Log.e(TAG, "Radio button discount selection");

            if (et_promotion_discount.getText().toString().trim().isEmpty()) {
                et_promotion_discount.setError(getResources().getString(R.string.add_dicount_percentage));
                if (isValid)
                    et_promotion_discount.requestFocus();
                isValid = false;
            }

            if (isValid && !et_promotion_discount.getText().toString().trim().isEmpty()) {
                if (Integer.parseInt(et_promotion_discount.getText().toString()) > 50) {
                    isValid = false;
                    if (Integer.parseInt(spNumberOfClasses.getSelectedItem().toString()) == 1)
                        tvDialogMessage.setText(getResources().getString(R.string.confirmation_msg1) + " " + et_promotion_discount.getText().toString() + getResources().getString(R.string.percentage) + " " + getResources().getString(R.string.discount_for) + " " + spNumberOfClasses.getSelectedItem().toString().trim() + " " + getResources().getString(R.string.classes_booked));
                    else
                        tvDialogMessage.setText(getResources().getString(R.string.confirmation_msg1) + " " + et_promotion_discount.getText().toString() + getResources().getString(R.string.percentage) + " " + getResources().getString(R.string.discount_for) + " " + spNumberOfClasses.getSelectedItem().toString().trim() + " " + getResources().getString(R.string.classes_booked));
                    dialog.show();
                }
            }


        } else if (selectedRadioGroupChild == 1) {
            Log.e(TAG, "Radio button free class selection");

            if (et_free_classes.getText().toString().trim().isEmpty()) {
                et_free_classes.setError(getResources().getString(R.string.add_free_classes));
                if (isValid)
                    et_free_classes.requestFocus();
                isValid = false;
            }

            if (Integer.parseInt(et_free_classes.getText().toString()) > Integer.parseInt(String.valueOf(spNumberOfClasses.getSelectedItem()))) {
                et_free_classes.setError(getResources().getString(R.string.free_class_cannot_bigger_than_mandatory));
                if (isValid)
                    et_free_classes.requestFocus();
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
            Log.e("FMC: ", "added promotion response: " + jsonObject.toString());
            if (jsonObject.getInt("data") > 0) {
                Toast.makeText(AddPromotion.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                if (MyPromotions.myPromotions != null) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b1:
                addPromotion();
                dialog.dismiss();
                break;
            case R.id.b2:
                dialog.dismiss();
                break;

        }

    }
}
