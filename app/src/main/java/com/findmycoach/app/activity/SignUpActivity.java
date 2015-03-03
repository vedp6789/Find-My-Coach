package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.beans.registration.SignUpResponse;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.R;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends Activity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, Callback {


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        applyActionbarProperties();
        radioGroup_user_sigup = (RadioGroup) findViewById(R.id.radio_group_user_signup);
//        radioGroup_user_sigup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.radio_button_mentee_signup) {
//                    user_group=2;
//                    //Toast.makeText(SignUpActivity.this,"User group"+user_group,Toast.LENGTH_SHORT).show();
//                }
//                if (checkedId == R.id.radio_button_mentor_signup){
//                    user_group=3;
//                    //Toast.makeText(SignUpActivity.this,"User group"+user_group,Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        initialize();

    }

    private void applyActionbarProperties() {
        ActionBar actionBar = getActionBar();
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
        signUpButton.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
            datePickerDialog.setTitle(getResources().getString(R.string.dob));
            return datePickerDialog;
        }
        return null;
    }

    private void showDate(int year, int month, int day) {
//        Calendar calendar = Calendar.getInstance();
//        if (year > calendar.get(Calendar.YEAR)) {
//            showErrorMessage(dateOfBirthInput, getResources().getString(R.string.error_invalid_date));
//            return;
//        } else if (year == calendar.get(Calendar.YEAR) && month > (calendar.get(Calendar.MONTH) + 1)) {
//            showErrorMessage(dateOfBirthInput, getResources().getString(R.string.error_invalid_date));
//            return;
//        } else if (year == calendar.get(Calendar.YEAR) && month == (calendar.get(Calendar.MONTH) + 1) &&
//                day >= calendar.get(Calendar.DATE)) {
//            showErrorMessage(dateOfBirthInput, getResources().getString(R.string.error_invalid_date));
//            return;
//        }
//        dateOfBirthInput.setText(new StringBuilder().append(day).append("/")
//                .append(month).append("/").append(year));
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
        }
    }

    private void registerUser() {

        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String phone = phoneNumberInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        boolean isValid = validate(firstName, lastName, phone, email,
                password, confirmPassword);

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
                requestParams.add("phone_number", phone);
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

    private boolean validate(String firstName, String lastName, String phone,
                             String email, String password, String confirmPassword) {

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
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
        showDate(year, month + 1, date);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
        finish();
    }


    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
    }
}
