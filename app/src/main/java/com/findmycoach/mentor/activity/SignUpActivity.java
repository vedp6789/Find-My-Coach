package com.findmycoach.mentor.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fmc.mentor.findmycoach.R;

public class SignUpActivity extends Activity implements View.OnClickListener {


    private int year, month, day;
    private TextView dateOfBirthInput;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private EditText emailInput;
    private EditText phoneNumberInput;
    private Button signUpButton;
    private Spinner genderInput;
    private DatePickerDialog.OnDateSetListener myDateListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
            showDate(year, month + 1, date);
        }
    };

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
        dateOfBirthInput = (TextView) findViewById(R.id.input_date_of_birth);
        phoneNumberInput = (EditText) findViewById(R.id.input_phone);
        genderInput = (Spinner) findViewById(R.id.input_gender);
        signUpButton = (Button) findViewById(R.id.button_signup);
        signUpButton.setOnClickListener(this);
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, year, month, day);
            datePickerDialog.setTitle("Date od Birth");
            return datePickerDialog;
        }
        return null;
    }

    private void showDate(int year, int month, int day) {
        dateOfBirthInput.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_signup) {
            registerUser();
        }

    }

    private void registerUser() {
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String gender = genderInput.getSelectedItem().toString();
        String dateOfBirth = dateOfBirthInput.getText().toString();
        String phone = phoneNumberInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        boolean isValid = validate(firstName, lastName, gender, dateOfBirth, phone, email, password, confirmPassword);
        if (isValid) {
//            callApiToRegister();
        }


    }

    private boolean validate(String firstName, String lastName, String gender, String dateOfBirth, String phone, String email, String password, String confirmPassword) {

        if (firstName.equals("")) {
            showErrorMessage(firstNameInput, getResources().getString(R.string.error_field_required));
            return false;
        }
        if (dateOfBirth.equals("")) {
            showErrorMessage(dateOfBirthInput, getResources().getString(R.string.error_field_required));
            return false;
        }
        if (phone.equals("")) {
            showErrorMessage(phoneNumberInput, getResources().getString(R.string.error_field_required));
            return false;
        }
        if (password.equals("")) {
            showErrorMessage(passwordInput, getResources().getString(R.string.error_field_required));
            return false;
        }
        if (confirmPassword.equals("")) {
            showErrorMessage(confirmPasswordInput, getResources().getString(R.string.error_field_required));
            return false;
        }
        if (email.equals("")) {
            showErrorMessage(emailInput, getResources().getString(R.string.error_field_required));
            return false;
        }
        if (password.equals(confirmPassword)) {
            showErrorMessage(confirmPasswordInput, getResources().getString(R.string.error_field_not_match));
            return false;
        }
        if (phone.length() < 10) {
            showErrorMessage(phoneNumberInput, getResources().getString(R.string.error_phone_number_invalid));
            return false;
        }
        return true;
    }

    private void showErrorMessage(final TextView view, String string) {
        view.setError(string);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setError(null);
            }
        }, 2500);
    }
}
