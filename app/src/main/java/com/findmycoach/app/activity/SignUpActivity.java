package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends Activity implements View.OnClickListener, Callback {


    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private EditText emailInput;
    private EditText phoneNumberInput;
    private ProgressDialog progressDialog;
    private RadioButton radioButton_mentee_signup, radioButton_mentor_signup;
    private int user_group = 0;
    private TextView countryCodeTV;
    private String[] country_code;
    private String email, phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initialize();
    }


    /**
     * Getting references of views
     */
    private void initialize() {
        country_code = this.getResources().getStringArray(R.array.country_codes);
        radioButton_mentee_signup = (RadioButton) findViewById(R.id.radio_button_mentee_signup);
        radioButton_mentor_signup = (RadioButton) findViewById(R.id.radio_button_mentor_signup);
        firstNameInput = (EditText) findViewById(R.id.input_first_name);
        lastNameInput = (EditText) findViewById(R.id.input_last_name);
        emailInput = (EditText) findViewById(R.id.input_email);
        passwordInput = (EditText) findViewById(R.id.input_password);
        confirmPasswordInput = (EditText) findViewById(R.id.input_confirm_password);
        phoneNumberInput = (EditText) findViewById(R.id.input_phone);
        countryCodeTV = (TextView) findViewById(R.id.countryCodeTV);
        String code = getCountryZipCode();
        countryCodeTV.setText(code.equals("") ? country_code[0] : code);
        countryCodeTV.setOnClickListener(this);
        findViewById(R.id.button_signup).setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        findViewById(R.id.action_login).setOnClickListener(this);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_signup) {
            registerUser();
        } else if (id == R.id.countryCodeTV) {
            showCountryCodeDialog();
        } else if(id == R.id.action_login)
            finish();
    }

    /**
     * Dialog for selecting country code
     */
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
                countryCodeTV.setError(null);
                countryCodeTV.setText(country_code[position].split(",")[0]);
                countryDialog.dismiss();
            }
        });
    }

    /**
     * Registering user with FMC
     */
    private void registerUser() {
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String phone = phoneNumberInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String countryCode = countryCodeTV.getText().toString().trim();

        /** Validating form data */
        boolean isValid = validate(firstName, lastName, phone, email, password, confirmPassword, countryCode);

        if (isValid) {
            if (radioButton_mentee_signup.isChecked())
                user_group = 2;
            else if (radioButton_mentor_signup.isChecked())
                user_group = 3;
            if (user_group == 0) {
                Toast.makeText(SignUpActivity.this, "Please select user type.", Toast.LENGTH_SHORT).show();
            } else {
                RequestParams requestParams = new RequestParams();
                requestParams.add("first_name", firstName);
                requestParams.add("last_name", lastName);
                requestParams.add("email", email);
                requestParams.add("password", password);
                requestParams.add("phone_number", countryCode + "-" + phone);
                requestParams.add("user_group", String.valueOf(user_group));
                StorageHelper.storePreference(this, "phone_number", phone);
                callApiToRegister(requestParams);
                this.email = email;
                phoneNumber = phone;
            }

        }
    }

    /**
     * Sending data to server for new registration
     */
    private void callApiToRegister(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.register(this, requestParams, this, 2);
    }

    /**
     * validating inserted user details
     */
    private boolean validate(String firstName, String lastName, String phone, String email, String password, String confirmPassword, String countryCode) {

        if (firstName.equals("")) {
            showErrorMessage(firstNameInput, getResources().getString(R.string.error_field_required));
            return false;
        } else {
            for (int i = 0; i < firstName.length() - 1; i++) {
                if (!Character.isLetter(firstName.charAt(i))) {
                    showErrorMessage(firstNameInput, getResources().getString(R.string.error_not_a_name));
                    return false;
                }
            }
        }
        firstNameInput.setError(null);

        if (lastName.equals("")) {
            showErrorMessage(lastNameInput, getResources().getString(R.string.error_field_required));
            return false;
        } else {
            for (int i = 0; i < lastName.length() - 1; i++) {
                if (!Character.isLetter(lastName.charAt(i))) {
                    showErrorMessage(lastNameInput, getResources().getString(R.string.error_not_a_name));
                    return false;
                }
            }
        }
        lastNameInput.setError(null);

        if (countryCode.trim().equalsIgnoreCase("Select")) {
            showErrorMessage(countryCodeTV, getResources().getString(R.string.select_country_code));
            return false;
        }

        if (phone.equals("")) {
            showErrorMessage(phoneNumberInput, getResources().getString(R.string.error_field_required));
            return false;
        } else if (phone.length() < 8) {
            showErrorMessage(phoneNumberInput, getResources().getString(R.string.error_phone_number_invalid));
            return false;
        }
        phoneNumberInput.setError(null);

        if (!isEmailValid(email)) {
            showErrorMessage(emailInput, getResources().getString(R.string.enter_valid_email));
            return false;
        }
        emailInput.setError(null);

        if (password.equals("")) {
            showErrorMessage(passwordInput, getResources().getString(R.string.error_field_required));
            return false;
        } else if (password.length() < 5) {
            showErrorMessage(passwordInput, getResources().getString(R.string.error_password_size));
            return false;
        }
        passwordInput.setError(null);

        if (confirmPassword.equals("")) {
            showErrorMessage(confirmPasswordInput, getResources().getString(R.string.error_field_required));
            return false;
        } else if (!password.equals(confirmPassword)) {
            showErrorMessage(confirmPasswordInput, getResources().getString(R.string.error_field_not_match));
            return false;
        }
        confirmPasswordInput.setError(null);


        return true;
    }

    /**
     * Checking entered email is valid email or not
     */
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

    /**
     * Displaying error is any detail is wrong
     */
    private void showErrorMessage(final TextView view, String string) {
        view.setError(string);
    }

    /**
     * If user registered successfully close activity
     */
    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();

        try{
            Response response = (Response) object;
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
            StorageHelper.storePreference(this, getResources().getString(R.string.new_user), "true#" + response.getData().getId());
            Log.e("SignUp", StorageHelper.getUserDetails(this, getResources().getString(R.string.new_user)) + "");
        }catch (Exception e){
            e.printStackTrace();
        }

        saveUserEmail(email);
        saveUserPhoneNumber(phoneNumber);
        StorageHelper.storePreference(this, "user_group", String.valueOf(user_group));
        Intent intent=new Intent(SignUpActivity.this,ValidatePhoneActivity.class);
        intent.putExtra("from","SignUpActivity");
        startActivity(intent);

        finish();
        LoginActivity.loginActivity.finish();
        StorageHelper.storePreference(this, "login_with", "Login");
    }

    /**
     * If registration is not successful
     */
    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
    }

    /**
     * Automatic detect country code
     */
    public String getCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();
        for (int i = 1; i < country_code.length; i++) {
            String[] g = country_code[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    /**
     * Saving email of user in shared preferences
     */
    private void saveUserEmail(String emailId) {
        StorageHelper.storePreference(this, "user_email", emailId);
    }

    /**
     * Saving user's phone number in shared preferences
     */
    private void saveUserPhoneNumber(String phoneNumber) {
        StorageHelper.storePreference(this, "phone_number", phoneNumber);
    }
}
