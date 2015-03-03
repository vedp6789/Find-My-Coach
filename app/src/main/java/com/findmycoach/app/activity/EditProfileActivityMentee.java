package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.student.Data;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.util.BinaryForImage;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivityMentee extends Activity implements DatePickerDialog.OnDateSetListener, Callback {

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
    private AutoCompleteTextView profileAddress;
    private AutoCompleteTextView profileAddress1;
    private EditText pinCode;
    private Spinner mentorFor;
    private EditText trainingLocation;
    private Spinner coachingType;
    private TextView areasOfInterest;
    //    private EditText facebookLink;
//    private EditText googlePlusLink;
    private Button updateAction;
    private ProgressDialog progressDialog;
    private Data userInfo;
    private String imageInBinary = "";
    private final String[] coachingTypeOptions = new String[]{"Solo", "Multiple"};

    private static final String TAG="FMC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_mentee);
        initialize();
        applyActionbarProperties();
        populateUserData();
    }

    private void
    populateUserData() {
        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
            PicassoTools.clearCache(Picasso.with(this));
            Picasso.with(this)
                    .load((String) userInfo.getPhotograph()).skipMemoryCache()
                    .into(profilePicture);
        }
        try {
            profileEmail.setText(userInfo.getEmail());
            profilePhone.setText(userInfo.getPhonenumber());
            profileFirstName.setText(userInfo.getFirstName());
            profileLastName.setText(userInfo.getLastName());
            profileAddress.setText((String) userInfo.getAddress());
            profileAddress1.setText((String) userInfo.getCity());
            profileDOB.setText((String) userInfo.getDob());
            pinCode.setText((String) userInfo.getZip());
            mentorFor.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"Self","Kid"}));
            if(userInfo.getMentorFor().equalsIgnoreCase("kid"))
                mentorFor.setSelection(1);
            trainingLocation.setText((String) userInfo.getTrainingLocation());
            String selectedCoachingType = (String) userInfo.getCoachingType();
            coachingType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, coachingTypeOptions));
            if(selectedCoachingType.equalsIgnoreCase(coachingTypeOptions[0]))
                coachingType.setSelection(0);
            else
                coachingType.setSelection(1);
            List<String> areaOfInterests = userInfo.getSubCategoryName();
            if (areaOfInterests.get(0)!=null && !areaOfInterests.get(0).equals(" ")) {
                String areaOfInterest = "";
                for (int index = 0; index < areaOfInterests.size(); index++) {
                    if (index != 0) {
                        areaOfInterest = areaOfInterest + ", " + areaOfInterests.get(index);
                    } else {
                        areaOfInterest = areaOfInterest + areaOfInterests.get(index);
                    }
                }
                areasOfInterest.setText(areaOfInterest);
            }
//            facebookLink.setText(userInfo.getFacebookLink());
//            googlePlusLink.setText(userInfo.getGoogleLink());
//            if (userInfo.getAvailabilityYn().equals("1")) {
//                isReadyToTravel.setChecked(true);
//            } else {
//                isReadyToTravel.setChecked(false);
//            }
        } catch (Exception e) {

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
        profileAddress = (AutoCompleteTextView) findViewById(R.id.input_address);
        profileAddress1 = (AutoCompleteTextView) findViewById(R.id.input_address1);
        profileDOB = (TextView) findViewById(R.id.input_date_of_birth);
        pinCode = (EditText) findViewById(R.id.input_pin);
//        facebookLink = (EditText) findViewById(R.id.input_facebook);
//        googlePlusLink = (EditText) findViewById(R.id.input_google_plus);
        mentorFor = (Spinner) findViewById(R.id.input_mentor_for);
        trainingLocation = (EditText) findViewById(R.id.input_training_location);
        coachingType = (Spinner) findViewById(R.id.input_coaching_type);
        areasOfInterest = (TextView) findViewById(R.id.input_areas_of_interest);
        updateAction = (Button) findViewById(R.id.button_update);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        applyAction();
    }

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", getResources().getString(R.string.google_location_api_key));
        NetworkClient.autoComplete(getApplicationContext(), requestParams, this, 32);
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
                if(profileAddress1.getText().toString().trim().equals("")){
                    Toast.makeText(EditProfileActivityMentee.this,getResources().getString(R.string.choose_suggested_city),Toast.LENGTH_LONG).show();
                    profileAddress1.setError(getResources().getString(R.string.choose_suggested_city));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            profileAddress1.setError(null);
                        }
                    }, 3500);
                    return;
                }
                callUpdateService();
            }
        });

        areasOfInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String interests = areasOfInterest.getText().toString();
                Log.d(TAG, "Area of Interests:" + interests);
                Intent intent = new Intent(getApplicationContext(), AreasOfInterestActivity.class);
                if(!interests.trim().equals(""))
                    intent.putExtra("interests", interests);
                startActivityForResult(intent, 500);
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
            requestParams.add("city", profileAddress1.getText().toString());
            requestParams.add("zip", pinCode.getText().toString());
            requestParams.add("mentor_for", mentorFor.getSelectedItem().toString());
            requestParams.add("training_location", trainingLocation.getText().toString());
            requestParams.add("coaching_type", coachingType.getSelectedItem().toString());
//            requestParams.add("sub_category", areasOfInterest.getText().toString());
            requestParams.add("sub_category", areasOfInterest.getText().toString().length()<2 ? " " : areasOfInterest.getText().toString());
            if (!imageInBinary.equals(""))
                requestParams.add("photograph", imageInBinary);

            String authToken = StorageHelper.getUserDetails(this, getResources().getString(R.string.auth_token));
            requestParams.add("id", StorageHelper.getUserDetails(this,getResources().getString(R.string.user_id)));
            requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
            NetworkClient.updateProfile(this, requestParams, authToken, this, 4);
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, getResources().getString(R.string.enter_necessary_details), Toast.LENGTH_LONG).show();
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
        if (requestCode == 500 && resultCode == RESULT_OK && data != null) {
            String listJson = data.getStringExtra("interests");
            List<String> list = new Gson().fromJson(listJson, List.class);
            String interests = "";
            for (int index = 0; index < list.size(); index++) {
                if (interests.equalsIgnoreCase("")) {
                    interests = list.get(index);
                } else {
                    interests = interests + ", " + list.get(index);
                }
            }
            areasOfInterest.setText(interests);
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
            datePickerDialog.setTitle(getResources().getString(R.string.dob));
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
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        if (object instanceof Suggestion) {
            Suggestion suggestion = (Suggestion) object;
            updateAutoSuggestion(suggestion);
        } else {
            progressDialog.dismiss();
            ProfileResponse response = (ProfileResponse) object;
            userInfo = response.getData();
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra("user_info", new Gson().toJson(userInfo));
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        String message = (String) object;
        progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}