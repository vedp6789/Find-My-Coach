package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.CountryCodeAdapter;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends Activity implements View.OnClickListener, Callback {


    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private EditText emailInput;
    private EditText phoneNumberInput;
    public Dialog progressDialog;
    private RadioButton radioButton_mentee_signup, radioButton_mentor_signup;
    private int user_group = 3;
    private TextView countryCodeTV;
    private ArrayList<String> country_code;
    private String email, phoneNumber;
    private ScrollView scrollView;


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
        country_code = new ArrayList<>();
        Collections.addAll(country_code, this.getResources().getStringArray(R.array.country_codes));
        radioButton_mentee_signup = (RadioButton) findViewById(R.id.radio_button_mentee_signup);
        radioButton_mentor_signup = (RadioButton) findViewById(R.id.radio_button_mentor_signup);
        firstNameInput = (EditText) findViewById(R.id.input_first_name);
        lastNameInput = (EditText) findViewById(R.id.input_last_name);
        emailInput = (EditText) findViewById(R.id.input_email);
        passwordInput = (EditText) findViewById(R.id.input_password);
        confirmPasswordInput = (EditText) findViewById(R.id.input_confirm_password);
        phoneNumberInput = (EditText) findViewById(R.id.input_phone);
        countryCodeTV = (TextView) findViewById(R.id.countryCodeTV);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        findViewById(R.id.facebook_login_button).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        String code = getCountryZipCode();
        countryCodeTV.setText(code.equals("") ? getResources().getString(R.string.select) : code);
        countryCodeTV.setOnClickListener(this);
        findViewById(R.id.button_signup).setOnClickListener(this);
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setContentView(R.layout.progressbar_textview);
        TextView msg = (TextView) progressDialog.findViewById(R.id.msg);
        msg.setText(getResources().getString(R.string.please_wait));

        findViewById(R.id.action_login).setOnClickListener(this);

        confirmPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
                return false;
            }
        });

        user_group = getIntent().getIntExtra("user_group", 3);
        if (user_group == 2)
            radioButton_mentee_signup.setChecked(true);

        firstNameInput.setOnTouchListener(onTouchListener);
        lastNameInput.setOnTouchListener(onTouchListener);
        countryCodeTV.setOnTouchListener(onTouchListener);
        phoneNumberInput.setOnTouchListener(onTouchListener);
        emailInput.setOnTouchListener(onTouchListener);
        passwordInput.setOnTouchListener(onTouchListener);
        confirmPasswordInput.setOnTouchListener(onTouchListener);

        firstNameInput.setOnFocusChangeListener(onFocusChangeListener);
        lastNameInput.setOnFocusChangeListener(onFocusChangeListener);
        countryCodeTV.setOnFocusChangeListener(onFocusChangeListener);
        phoneNumberInput.setOnFocusChangeListener(onFocusChangeListener);
        emailInput.setOnFocusChangeListener(onFocusChangeListener);
        passwordInput.setOnFocusChangeListener(onFocusChangeListener);
        confirmPasswordInput.setOnFocusChangeListener(onFocusChangeListener);
    }

    /**
     * Clear error from edit text when focused or on touch
     */
    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                ((TextView) v).setError(null);
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ((TextView) v).setError(null);
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_signup) {
            registerUser();
        } else if (id == R.id.countryCodeTV) {
            showCountryCodeDialog();
        } else if (id == R.id.action_login)
            finish();
        else if (id == R.id.facebook_login_button && LoginActivity.loginActivity != null) {
            progressDialog.show();
            LoginActivity.loginActivity.actionFacebook.callOnClick();
            finish();
        } else if (id == R.id.sign_in_button && LoginActivity.loginActivity != null) {
            progressDialog.show();
            LoginActivity.loginActivity.mSignInButton.callOnClick();
            finish();
        }
    }

    /**
     * Dialog for selecting country code
     */
    private void showCountryCodeDialog() {
        final Dialog countryDialog = new Dialog(this);
        countryDialog.setCanceledOnTouchOutside(true);
        countryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        countryDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        countryDialog.setTitle(getResources().getString(R.string.select_country_code));
        countryDialog.setContentView(R.layout.dialog_country_code);
        ListView listView = (ListView) countryDialog.findViewById(R.id.countryCodeListView);
        ArrayList<String> a = new ArrayList<>();
        Collections.addAll(a, getResources()
                .getStringArray(R.array.country_names));
        final ArrayList<String> b = new ArrayList<>();
        Collections.addAll(b, getResources().getStringArray(R.array.country_codes_only));
        final CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(a, b,
                this, (EditText) countryDialog.findViewById(R.id.searchBox));
        listView.setAdapter(countryCodeAdapter);
        countryDialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryCodeTV.setError(null);
                countryCodeTV.setText(countryCodeAdapter.countryNameAndCode
                        .get(position).getCountryCode().replace("(", "").replace(")", ""));
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
                Log.e("Sign up : phone_number", countryCode.trim() + "-" + phone);
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

        boolean isCorrect = true;
        firstName = firstName.replaceAll(" ", "");
        lastName = lastName.replaceAll(" ", "");

        if (firstName.equals("")) {
            showErrorMessage(firstNameInput, getResources().getString(R.string.error_field_required));
            isCorrect = false;
        } else {
            for (int i = 0; i < firstName.length() - 1; i++) {
                if (!Character.isLetter(firstName.charAt(i))) {
                    showErrorMessage(firstNameInput, getResources().getString(R.string.error_not_a_name));
                    isCorrect = false;
                }
            }
        }
        if (isCorrect)
            firstNameInput.setError(null);

        if (lastName.equals("")) {
            showErrorMessage(lastNameInput, getResources().getString(R.string.error_field_required));
            isCorrect = false;
        } else {
            for (int i = 0; i < lastName.length() - 1; i++) {
                if (!Character.isLetter(lastName.charAt(i))) {
                    showErrorMessage(lastNameInput, getResources().getString(R.string.error_not_a_name));
                    isCorrect = false;
                }
            }
        }
        if (isCorrect)
            lastNameInput.setError(null);

        if (phone.equals("")) {
            showErrorMessage(phoneNumberInput, getResources().getString(R.string.error_field_required));
            isCorrect = false;
        } else if (phone.length() < 8) {
            showErrorMessage(phoneNumberInput, getResources().getString(R.string.error_phone_number_invalid));
            isCorrect = false;
        }
        if (isCorrect)
            phoneNumberInput.setError(null);

        if (!isEmailValid(email)) {
            showErrorMessage(emailInput, getResources().getString(R.string.enter_valid_email));
            isCorrect = false;
        }
        if (isCorrect)
            emailInput.setError(null);

        if (password.equals("") || password.length() < 5) {
            showErrorMessage(passwordInput, getResources().getString(R.string.error_password_size));
            isCorrect = false;
        }
        if (isCorrect)
            passwordInput.setError(null);

        if (confirmPassword.equals("")) {
            showErrorMessage(confirmPasswordInput, getResources().getString(R.string.error_field_required));
            isCorrect = false;
        } else if (!password.equals(confirmPassword)) {
            showErrorMessage(confirmPasswordInput, getResources().getString(R.string.error_password_not_match));
            isCorrect = false;
        }

        if (isCorrect)
            confirmPasswordInput.setError(null);

        if (countryCode.trim().equalsIgnoreCase("Select") && isCorrect) {
            showErrorMessage(countryCodeTV, getResources().getString(R.string.select_country_code));
            isCorrect = false;
        }

        return isCorrect;
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
//        progressDialog.dismiss();
//
//        try {
//            String name = firstNameInput.getText().toString() + " " + lastNameInput.getText().toString();
//            StorageHelper.storePreference(this, "user_full_name", name);
//        } catch (Exception ignored) {
//        }
//
//        try {
//            Response response = (Response) object;
//            Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
//            StorageHelper.storePreference(this, getResources().getString(R.string.new_user), "true#" + response.getData().getId());
//            Log.e("SignUp", StorageHelper.getUserDetails(this, getResources().getString(R.string.new_user)) + "");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        saveUserEmail(email);
//        saveUserPhoneNumber(phoneNumber);
//        StorageHelper.storePreference(this, "user_group", String.valueOf(user_group));
//        Intent intent = new Intent(SignUpActivity.this, ValidatePhoneActivity.class);
//        intent.putExtra("from", "SignUpActivity");
//        startActivity(intent);
//
//        LoginActivity.loginActivity.finish();
//        finish();
//        StorageHelper.storePreference(this, "login_with", "Login");
        progressDialog.dismiss();


        Response response = (Response) object;
        if (response == null)
            return;
        saveUserEmail(email);
        saveUserPhoneNumber(phoneNumber);


        /** If phone number is not verified then starting ValidatePhoneActivity */
        if (response.getData() == null) {
            StorageHelper.storePreference(this, getResources().getString(R.string.new_user), "true#" + response.getId());
            StorageHelper.storePreference(this, "user_group", String.valueOf(user_group));
            StorageHelper.storePreference(this, "login_with", "Login");
            startActivity(new Intent(this, ValidatePhoneActivity.class));
            finish();
            return;
        }

        /** Login is successful start DashBoard Activity */


        if (statusCode == 200 && response.getData() != null) {
            /** Saving user group */


            String user_group_saved = StorageHelper.getUserGroup(SignUpActivity.this, "user_group");
            if (user_group_saved == null || !user_group_saved.equals(String.valueOf(user_group))) {
                StorageHelper.storePreference(SignUpActivity.this, "user_group", String.valueOf(user_group));
            }


            /** Saving auth token and user id of user for further use */
            if (response.getData().getAuthToken() != null && response.getData() != null && response.getData().getId() != null) {
                saveUser(response.getData().getAuthToken(), response.getData().getId());
            }

            try {
                String currencyCode = StorageHelper.getCurrency(this);
                if (currencyCode == null || currencyCode.trim().equals("")) {
                    StorageHelper.setCurrency(this, response.getData().getCurrencyCode());
                }
            } catch (Exception ignored) {
            }

            try {
                /** Saving address, city and zip of user */
                if (response.getData() != null && response.getData().getAddress() != null) {
                    StorageHelper.storePreference(this, "user_local_address", (String) response.getData().getAddress());
                    if (response.getData().getCity() != null) {
                        StorageHelper.storePreference(this, "user_city_state", (String) response.getData().getCity());
                    }
                    if (response.getData().getZip() != null) {

                        StorageHelper.storePreference(this, "user_zip_code", (String) response.getData().getZip());

                    }
                }
            } catch (Exception ignored) {

            }

            try {
             /* Saving training location for mentee type user */
                if (StorageHelper.getUserGroup(SignUpActivity.this, "user_group").equals("2") && response.getData().isTrainingLocation() != null) {
                    StorageHelper.storePreference(this, "training_location", (String) response.getData().isTrainingLocation());
                }
            } catch (Exception ignored) {
            }

            try {
        /*Saving mentor's area of coaching i.e. subcategories to sharedpreference in string set*/
                if (StorageHelper.getUserGroup(SignUpActivity.this, "user_group").equals("3")) {
                    List<String> sub_category_list = response.getData().getSubCategoryName();
                    Set<String> sub_category_stringSet = new HashSet<String>();
                    for (int i = 0; i < sub_category_list.size(); i++) {
                        sub_category_stringSet.add(sub_category_list.get(i));
                    }
                    StorageHelper.storeListOfCoachingSubCategories(SignUpActivity.this, sub_category_stringSet);
                }
            } catch (Exception ignored) {
            }

            try {
                String name = response.getData().getFirstName() + " " + response.getData().getLastName();
                StorageHelper.storePreference(this, "user_full_name", name);
            } catch (Exception ignored) {
            }

            saveUserPhn("True");
            finish();
            if (user_group == 2)
                startActivity(new Intent(this, PaymentDetailsActivity.class));
            else
                startActivity(new Intent(this, DashboardActivity.class));

        }
    }


    private void saveUserPhn(String isPhnVerified) {
        StorageHelper.storePreference(this, "phone_verified", isPhnVerified);
    }

    /**
     * If registration is not successful
     */
    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
    }

    private void saveUser(String authToken, String userId) {
        StorageHelper.storePreference(this, "auth_token", authToken);
        StorageHelper.storePreference(this, "user_id", userId);

    }


    /**
     * Automatic detect country code
     */
    public String getCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();
        for (int i = 1; i < country_code.size(); i++) {
            String[] g = country_code.get(i).split(",");
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
