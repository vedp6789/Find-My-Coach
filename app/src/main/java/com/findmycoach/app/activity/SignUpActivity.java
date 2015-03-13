package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends Activity implements View.OnClickListener, Callback {


    private int year = 1990, month = 1, day = 1;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private EditText emailInput;
    private EditText phoneNumberInput;
    private Button signUpButton;
    private ProgressDialog progressDialog;
    private RadioGroup radioGroup_user_sigup;
    private RadioButton radioButton_mentee_signup, radioButton_mentor_signup;
    private int user_group=0;
    private TextView countryCodeTV;
    private String[] country_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        applyActionbarProperties();
        radioGroup_user_sigup = (RadioGroup) findViewById(R.id.radio_group_user_signup);
        initialize();
    }

    private void applyActionbarProperties() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initialize() {
        radioButton_mentee_signup= (RadioButton) findViewById(R.id.radio_button_mentee_signup);
        radioButton_mentor_signup= (RadioButton) findViewById(R.id.radio_button_mentor_signup);
        firstNameInput = (EditText) findViewById(R.id.input_first_name);
        lastNameInput = (EditText) findViewById(R.id.input_last_name);
        emailInput = (EditText) findViewById(R.id.input_email);
        passwordInput = (EditText) findViewById(R.id.input_password);
        confirmPasswordInput = (EditText) findViewById(R.id.input_confirm_password);
        phoneNumberInput = (EditText) findViewById(R.id.input_phone);
        signUpButton = (Button) findViewById(R.id.button_signup);
        countryCodeTV = (TextView) findViewById(R.id.countryCodeTV);
        countryCodeTV.setText(getCountryZipCode());
        countryCodeTV.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_signup) {
            registerUser();
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

    private void registerUser() {
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String phone = phoneNumberInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String countryCode = countryCodeTV.getText().toString().trim();

        boolean isValid = validate(firstName, lastName, phone, email, password, confirmPassword, countryCode);

        if (isValid) {
            if(radioButton_mentee_signup.isChecked())
                user_group = 2;
            else if(radioButton_mentor_signup.isChecked())
                user_group = 3;
            if(user_group == 0){
                Toast.makeText(SignUpActivity.this,"Please select user type.",Toast.LENGTH_SHORT).show();
            }else{
                RequestParams requestParams = new RequestParams();
                requestParams.add("first_name", firstName);
                requestParams.add("last_name", lastName);
                requestParams.add("email", email);
                requestParams.add("password", password);
                requestParams.add("phone_number", countryCode + phone);
                requestParams.add("user_group", String.valueOf(user_group));
                StorageHelper.storePreference(this, "phone_number", phone);
                callApiToRegister(requestParams);
            }

        }
    }

    private void callApiToRegister(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.register(this, requestParams, this, 2);
    }

    private boolean validate(String firstName, String lastName, String phone, String email, String password, String confirmPassword, String countryCode) {

        if (firstName.equals("")) {
            showErrorMessage(firstNameInput, getResources().getString(R.string.error_field_required));
            return false;
        }else{
            for (int i = 0; i < firstName.length()-1; i++) {
                if (!Character.isLetter(firstName.charAt(i))) {
                    showErrorMessage(firstNameInput, getResources().getString(R.string.error_not_a_name));
                    return false;
                }
            }
        }

        if (lastName.equals("")) {
            showErrorMessage(lastNameInput, getResources().getString(R.string.error_field_required));
            return false;
        }else{
            for (int i = 0; i < lastName.length()-1; i++) {
                if (!Character.isLetter(lastName.charAt(i))) {
                    showErrorMessage(lastNameInput, getResources().getString(R.string.error_not_a_name));
                    return false;
                }
            }
        }

        if(countryCode.trim().equals("")){
            showErrorMessage(countryCodeTV, getResources().getString(R.string.select_country_code));
            return false;
        }

        if (phone.equals("")) {
            showErrorMessage(phoneNumberInput, getResources().getString(R.string.error_field_required));
            return false;
        }else if (phone.length() < 10) {
            showErrorMessage(phoneNumberInput, getResources().getString(R.string.error_phone_number_invalid));
            return false;
        }

        if (!isEmailValid(email)) {
            showErrorMessage(emailInput, getResources().getString(R.string.enter_valid_email));
            return false;
        }

        if (password.equals("")) {
            showErrorMessage(passwordInput, getResources().getString(R.string.error_field_required));
            return false;
        }else if (password.length() < 5) {
            showErrorMessage(passwordInput, getResources().getString(R.string.error_password_size));
            return false;
        }

        if (confirmPassword.equals("")) {
            showErrorMessage(confirmPasswordInput, getResources().getString(R.string.error_field_required));
            return false;
        }else if (!password.equals(confirmPassword)) {
            showErrorMessage(confirmPasswordInput, getResources().getString(R.string.error_field_not_match));
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

    private void showErrorMessage(final TextView view, String string) {
        view.setError(string);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setError(null);
            }
        }, 3500);
    }


    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
        if(user_group == 2){
            startActivity(new Intent(this, PaymentDetailsActivity.class));
        }
        finish();
    }


    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
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
