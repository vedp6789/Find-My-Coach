package com.findmycoach.mentor.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.mentor.beans.authentication.Data;
import com.findmycoach.mentor.beans.authentication.Response;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.findmycoach.mentor.util.StorageHelper;
import com.fmc.mentor.findmycoach.R;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

public class EditProfileActivity extends Activity implements DatePickerDialog.OnDateSetListener, Callback {

    int year = 1990, month = 1, day = 1;
    int REQUEST_CODE = 100;
    private TextView dateOfBirthInput;
    private Spinner countryInput;
    private Spinner stateInput;
    private Spinner cityInput;
    private ImageView profilePicture;
    private TextView profileEmail;
    private TextView profilePhone;
    private EditText profileFirstName;
    private EditText profileLastName;
    private Spinner profileGender;
    private TextView profileDOB;
    private EditText profileAddress;
    private EditText pinCode;
    private EditText profession;
    private EditText passion;
    private EditText accomplishment;
    private EditText chargeInput;
    private EditText experienceInput;

    private EditText facebookLink;
    private EditText googlePlusLink;
    private CheckBox isReadyToTravel;
    private Button updateAction;
    private ProgressDialog progressDialog;


    private Data userInfo;


    private String currentCountry;
    private String currentState;
    private String currentCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initialize();
        applyActionbarProperties();
        populateUserData();
    }

    private void populateUserData() {
//        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
//            Picasso.with(this)
//                    .load((String) userInfo.getPhotograph())
//                    .into(profilePicture);
//        }
        profileEmail.setText(userInfo.getEmail());
        profilePhone.setText(userInfo.getPhonenumber());
        profileFirstName.setText(userInfo.getFirstName());
        profileLastName.setText(userInfo.getLastName());
        profileAddress.setText((String) userInfo.getAddress());
        profileDOB.setText((String) userInfo.getDob());
        pinCode.setText((String) userInfo.getZip());
        facebookLink.setText(userInfo.getFacebookLink());
        googlePlusLink.setText(userInfo.getGoogleLink());
        if (userInfo.getAvailabilityYn().equals("Y")) {
            isReadyToTravel.setChecked(true);
        } else {
            isReadyToTravel.setChecked(false);
        }
    }

    private void applyActionbarProperties() {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void initialize() {
        userInfo = new Gson().fromJson(getIntent().getStringExtra("user_info"), Data.class);
        dateOfBirthInput = (TextView) findViewById(R.id.input_date_of_birth);
        profileGender = (Spinner) findViewById(R.id.input_gender);
        countryInput = (Spinner) findViewById(R.id.input_country);
        stateInput = (Spinner) findViewById(R.id.input_state);
        cityInput = (Spinner) findViewById(R.id.input_city);
        profilePicture = (ImageView) findViewById(R.id.profile_image);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profilePhone = (TextView) findViewById(R.id.profile_phone);
        profileFirstName = (EditText) findViewById(R.id.input_first_name);
        profileLastName = (EditText) findViewById(R.id.input_last_name);
        profileAddress = (EditText) findViewById(R.id.input_address);
        profileDOB = (TextView) findViewById(R.id.input_date_of_birth);
        pinCode = (EditText) findViewById(R.id.input_pin);
        facebookLink = (EditText) findViewById(R.id.input_facebook);
        googlePlusLink = (EditText) findViewById(R.id.input_google_plus);
        chargeInput = (EditText) findViewById(R.id.input_charges);
        experienceInput = (EditText) findViewById(R.id.input_experience);
        isReadyToTravel = (CheckBox) findViewById(R.id.input_willing);
        updateAction = (Button) findViewById(R.id.button_update);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        applyAction();
    }

    private void applyAction() {
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.countries));
        countryInput.setAdapter(countryAdapter);
        countryInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String country = countryInput.getSelectedItem().toString().toLowerCase();
                Log.d("FMC:", "Selected Item:" + country);
                country = country.replaceAll(" ", "_");
                try {
                    ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, getResources().getStringArray(getResources().getIdentifier(country, "array", getPackageName())));
                    stateInput.setAdapter(stateAdapter);
                } catch (Exception e) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stateInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String state = stateInput.getSelectedItem().toString().toLowerCase();
                state = state.replaceAll(" ", "_");
                try {
                    ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, getResources().getStringArray(getResources().getIdentifier(state, "array", getPackageName())));
                    cityInput.setAdapter(stateAdapter);
                } catch (Exception e) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseImageActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        updateAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUpdateService();
            }
        });

    }

    private void callUpdateService() {
        progressDialog.show();
        try {
            RequestParams requestParams = new RequestParams();
            requestParams.add("first_name", profileFirstName.getText().toString());
            requestParams.add("last_name", profileLastName.getText().toString());
            requestParams.add("gender", profileGender.getSelectedItem().toString());
            requestParams.add("dob", profileDOB.getText().toString());
            requestParams.add("address", profileAddress.getText().toString());
            requestParams.add("city", cityInput.getSelectedItem().toString());
            requestParams.add("state", stateInput.getSelectedItem().toString());
            requestParams.add("country", countryInput.getSelectedItem().toString());
            requestParams.add("zip", pinCode.getText().toString());
            requestParams.add("charges", chargeInput.getText().toString());
            requestParams.add("experience", experienceInput.getText().toString());
            requestParams.add("google_link", googlePlusLink.getText().toString());
            requestParams.add("facebook_link", facebookLink.getText().toString());
            if (isReadyToTravel.isChecked())
                requestParams.add("availability_yn", "Y");
            else
                requestParams.add("availability_yn", "N");

            String authToken = StorageHelper.getUserDetails(this, "auth_token");
            requestParams.add("id", StorageHelper.getUserDetails(this, "user_id"));

            NetworkClient.updateProfile(this, requestParams, authToken, this);
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please enter necessary details", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            profilePicture.setImageBitmap((Bitmap) data.getParcelableExtra("image"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
            datePickerDialog.setTitle("Date of Birth");
            return datePickerDialog;
        }
        return null;
    }

    private void showDate(int year, int month, int day) {
        dateOfBirthInput.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        showDate(year, monthOfYear + 1, dayOfMonth);
    }

    @Override
    public void successOperation(Object object) {
        progressDialog.dismiss();
        Response response = (Response) object;
        userInfo = response.getData();
//        populateUserData();
        Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.putExtra("user_info", new Gson().toJson(userInfo));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void failureOperation(Object object) {
        String message = (String) object;
        progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
