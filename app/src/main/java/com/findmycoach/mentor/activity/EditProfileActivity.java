package com.findmycoach.mentor.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.findmycoach.mentor.beans.suggestion.Prediction;
import com.findmycoach.mentor.beans.suggestion.Suggestion;
import com.findmycoach.mentor.util.BinaryForImage;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.findmycoach.mentor.util.StorageHelper;
import com.fmc.mentor.findmycoach.R;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends Activity implements DatePickerDialog.OnDateSetListener, Callback {

    int year = 1990, month = 1, day = 1;
    int REQUEST_CODE = 100;
    private TextView dateOfBirthInput;
    private ImageView profilePicture;
    private TextView profileEmail;
    private TextView profilePhone;
    private EditText profileFirstName;
    private EditText profileLastName;
    private Spinner profileGender;
    private TextView profileDOB;
    private EditText profileAddress;
    private AutoCompleteTextView profileAddress1;
    private EditText pinCode;
    private EditText profession;
    private EditText accomplishment;
    private EditText chargeInput;
    private EditText experienceInput;
    private EditText facebookLink;
    private EditText googlePlusLink;
    private CheckBox isReadyToTravel;
    private Button updateAction;
    private Spinner chargesPerUnit;
    private ProgressDialog progressDialog;
    private Data userInfo;
    private String imageInBinary = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initialize();
        applyActionbarProperties();
        populateUserData();
    }

    private void populateUserData() {
        PicassoTools.clearCache(Picasso.with(this));
        if (userInfo == null) {
            return;
        }
        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
            Picasso.with(this)
                    .load((String) userInfo.getPhotograph()).skipMemoryCache()
                    .into(profilePicture);
        }
        profileEmail.setText(userInfo.getEmail());
        profilePhone.setText(userInfo.getPhonenumber());
        profileFirstName.setText(userInfo.getFirstName());
        profileLastName.setText(userInfo.getLastName());
        profileAddress.setText((String) userInfo.getAddress());
        profileAddress1.setText((String) userInfo.getCity());
        profileDOB.setText((String) userInfo.getDob());
        pinCode.setText((String) userInfo.getZip());
        chargeInput.setText(userInfo.getCharges());
        ArrayAdapter<String> chargesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"hour", "30 mins", "class"});
        chargesPerUnit.setAdapter(chargesAdapter);
        experienceInput.setText(userInfo.getExperience());
        facebookLink.setText(userInfo.getFacebookLink());
        googlePlusLink.setText(userInfo.getGoogleLink());
        if (userInfo.getAccomplishments() != null) {
            accomplishment.setText(userInfo.getAccomplishments());
        }
        if (userInfo.getProfession() != null) {
            profession.setText(userInfo.getProfession());
        }
        if (userInfo.getAvailabilityYn().equals("1")) {
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
        profilePicture = (ImageView) findViewById(R.id.profile_image);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profilePhone = (TextView) findViewById(R.id.profile_phone);
        profileFirstName = (EditText) findViewById(R.id.input_first_name);
        profileLastName = (EditText) findViewById(R.id.input_last_name);
        profileAddress = (EditText) findViewById(R.id.input_address);
        profileAddress1 = (AutoCompleteTextView) findViewById(R.id.input_address1);
        profileDOB = (TextView) findViewById(R.id.input_date_of_birth);
        pinCode = (EditText) findViewById(R.id.input_pin);
        facebookLink = (EditText) findViewById(R.id.input_facebook);
        googlePlusLink = (EditText) findViewById(R.id.input_google_plus);
        chargeInput = (EditText) findViewById(R.id.input_charges);
        profession = (EditText) findViewById(R.id.input_profession);
        accomplishment = (EditText) findViewById(R.id.input_accomplishment);
        experienceInput = (EditText) findViewById(R.id.input_experience);
        isReadyToTravel = (CheckBox) findViewById(R.id.input_willing);
        updateAction = (Button) findViewById(R.id.button_update);
        chargesPerUnit = (Spinner) findViewById(R.id.chargesPerUnit);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        applyAction();
    }

    private void applyAction() {
        profileAddress1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = profileAddress1.getText().toString();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                }
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

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", getResources().getString(R.string.google_location_api_key));
        NetworkClient.autoComplete(getApplicationContext(), requestParams, this);
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
            requestParams.add("city", profileAddress1.getText().toString());
            requestParams.add("zip", pinCode.getText().toString());
            requestParams.add("charges", chargeInput.getText().toString());
            requestParams.add("charges_unit", chargesPerUnit.getSelectedItemPosition() + "");
            requestParams.add("experience", experienceInput.getText().toString());
            requestParams.add("profession", profession.getText().toString());
            requestParams.add("accomplishments", accomplishment.getText().toString());
            requestParams.add("google_link", googlePlusLink.getText().toString());
            requestParams.add("facebook_link", facebookLink.getText().toString());
            if (!imageInBinary.equals(""))
                requestParams.add("photograph", imageInBinary);
            if (isReadyToTravel.isChecked())
                requestParams.add("availability_yn", "1");
            else
                requestParams.add("availability_yn", "0");
            Log.d("FMC", "charges_unit : " + chargesPerUnit.getSelectedItemPosition());

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
            Bitmap userPic = (Bitmap) data.getParcelableExtra("image");
            profilePicture.setImageBitmap(userPic);
            try {
                imageInBinary = BinaryForImage.getBinaryStringFromBitmap(userPic);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        dateOfBirthInput.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        showDate(year, monthOfYear + 1, dayOfMonth);
    }

    private void updateAutoSuggestion(Suggestion suggestion) {
        ArrayList<String> list = new ArrayList<String>();
        List<Prediction> suggestions = suggestion.getPredictions();
        for (int index = 0; index < suggestions.size(); index++) {
            list.add(suggestions.get(index).getDescription());
        }
        profileAddress1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
    }

    @Override
    public void successOperation(Object object) {
        if (object instanceof Suggestion) {
            Suggestion suggestion = (Suggestion) object;
            updateAutoSuggestion(suggestion);
        } else {
            progressDialog.dismiss();
            Response response = (Response) object;
            userInfo = response.getData();
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra("user_info", new Gson().toJson(userInfo));
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void failureOperation(Object object) {
        String message = (String) object;
        progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
