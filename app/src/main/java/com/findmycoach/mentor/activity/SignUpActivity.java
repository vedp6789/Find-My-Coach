package com.findmycoach.mentor.activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.mentor.beans.registration.SignUpResponse;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.findmycoach.mentor.util.StorageHelper;
import com.findmycoach.mentor.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        applyActionbarProperties();
        initialize();
    }

    private void applyActionbarProperties() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initialize() {
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
            RequestParams requestParams = new RequestParams();
            requestParams.add("first_name", firstName);
            requestParams.add("last_name", lastName);
            requestParams.add("email", email);
            requestParams.add("password", password);
            requestParams.add("phone_number", phone);
            StorageHelper.storePreference(this, "phone_number", phone);
            callApiToRegister(requestParams);
        }
    }

    private void callApiToRegister(RequestParams requestParams) {
        progressDialog.show();
        NetworkClient.register(this, requestParams, this);
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
    public void successOperation(Object object) {
        SignUpResponse response = (SignUpResponse) object;
        progressDialog.dismiss();
        Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
        this.finish();
    }


    @Override
    public void failureOperation(Object message) {
        String errMessage = (String) message;
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), errMessage, Toast.LENGTH_LONG).show();
    }
}
