package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
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
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.AddressFromZip;
import com.findmycoach.app.util.BinaryForImage;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.xml.sax.InputSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class EditProfileActivityMentee extends Activity implements DatePickerDialog.OnDateSetListener, Callback {

    int REQUEST_CODE = 100;
    private ImageView profilePicture;
    private TextView profileEmail;
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
    private Button updateAction;
    private ProgressDialog progressDialog;
    private Data userInfo;
    private String imageInBinary = "";
    private String[] coachingTypeOptions;
    ArrayAdapter<String> arrayAdapter;
    private String city = null;
    private static final String TAG = "FMC";
    private String newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_mentee);
        newUser = StorageHelper.getUserDetails(this, getResources().getString(R.string.new_user));
        initialize();
        applyAction();
        populateUserData();

        if (newUser != null)
            getAddress();
    }

    private void initialize() {
        coachingTypeOptions = getResources().getStringArray(R.array.coaching_type);
        userInfo = new Gson().fromJson(getIntent().getStringExtra("user_info"), Data.class);
        profileGender = (Spinner) findViewById(R.id.input_gender);
        profilePicture = (ImageView) findViewById(R.id.profile_image);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileFirstName = (EditText) findViewById(R.id.input_first_name);
        profileLastName = (EditText) findViewById(R.id.input_last_name);
        profileAddress = (AutoCompleteTextView) findViewById(R.id.input_address);
        profileAddress1 = (AutoCompleteTextView) findViewById(R.id.input_address1);
        profileDOB = (TextView) findViewById(R.id.input_date_of_birth);
        pinCode = (EditText) findViewById(R.id.input_pin);
        mentorFor = (Spinner) findViewById(R.id.input_mentor_for);
        trainingLocation = (EditText) findViewById(R.id.input_training_location);
        coachingType = (Spinner) findViewById(R.id.input_coaching_type);
        updateAction = (Button) findViewById(R.id.button_update);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        profileGender.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, getResources().getStringArray(R.array.gender)));

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.title_edit_profile_menu));
    }

    private void applyAction() {
        profileDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(profileDOB);
            }
        });

        profileAddress1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                city = null;
                String input = profileAddress1.getText().toString();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                }
            }
        });

        profileAddress1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = arrayAdapter.getItem(position).toString();
                Geocoder geocoder = new Geocoder(EditProfileActivityMentee.this, Locale.getDefault());
                try {
                    ArrayList<Address> addresses = (ArrayList<Address>) geocoder.getFromLocationName(city.toString(), 1);
                    Address address = addresses.get(0);
                    Log.i(TAG, "address according to Geo coder : " + "\n postal code : " + address.getPostalCode() + "\n country name : " + address.getCountryName() + "\n address line 0 : " + address.getAddressLine(0) + "\n address line 1 : " + address.getAddressLine(1));
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        Log.i(TAG, "address line " + i + " : " + address.getAddressLine(i));
                    }
                    Log.i(TAG, "address locality " + address.getLocality() + "latitude : " + address.getLatitude() + "longitude : " + address.getLongitude());


                    double latitude = address.getLatitude();
                    double longitude = address.getLongitude();

                    final ArrayList<Double> doubles = new ArrayList<Double>();
                    doubles.add(latitude);
                    doubles.add(longitude);


                    new AsyncTask<ArrayList, Void, String>() {

                        @Override
                        protected String doInBackground(ArrayList... params) {
                            ArrayList<Double> doubles1 = params[0];
                            XPath xpath = XPathFactory.newInstance().newXPath();
                            String expression = "//GeocodeResponse/result/address_component[type=\"postal_code\"]/long_name/text()";
                            Log.d(TAG, "lat in async " + doubles1.get(0));
                            Log.d(TAG, "long in async " + doubles1.get(1));

                            InputSource inputSource = new InputSource("https://maps.googleapis.com/maps/api/geocode/xml?latlng=" + doubles1.get(0) + "," + doubles1.get(1) + "&sensor=true");
                            String zipcode = null;
                            try {
                                zipcode = (String) xpath.evaluate(expression, inputSource, XPathConstants.STRING);
                            } catch (XPathExpressionException e) {
                                e.printStackTrace();
                            }


                            Log.i(TAG, "zip code 1 : " + zipcode);


                            return zipcode;
                        }


                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            if (s != null) {
                                pinCode.setText(s);
                            } else {
                                pinCode.setText("");
                            }


                        }
                    }.execute(doubles);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseImageActivity.class);
                profilePicture.buildDrawingCache();
                Bitmap bitMap = profilePicture.getDrawingCache();
                intent.putExtra("BitMap", BinaryForImage.getBinaryStringFromBitmap(bitMap));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        updateAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUserUpdate())
                    callUpdateService();
            }
        });

        pinCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    try {
                        new AddressFromZip(EditProfileActivityMentee.this, profileAddress1).execute(pinCode.getText().toString());
                    } catch (Exception ignored) {
                    }
                }
                return false;
            }
        });

        profileAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    profileAddress1.setFocusable(true);
                }
                return false;
            }
        });

        profileAddress1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    pinCode.setFocusable(true);
                }
                return false;
            }
        });

    }

    public void populateUserData() {

        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
            ImageLoader imgLoader = new ImageLoader(profilePicture);
            imgLoader.execute((String) userInfo.getPhotograph());
        }

        try {
            mentorFor.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, new String[]{"Self", "Kid"}));
        } catch (Exception ignored) {
        }
        try {
            profileEmail.setText(userInfo.getEmail());
        } catch (Exception ignored) {
        }
        try {
            profileFirstName.setText(userInfo.getFirstName());
        } catch (Exception ignored) {
        }
        try {
            profileLastName.setText(userInfo.getLastName());
        } catch (Exception ignored) {
        }
        try {
            profileAddress.setText((String) userInfo.getAddress());
        } catch (Exception ignored) {
        }
        try {
            profileAddress1.setText((String) userInfo.getCity());
        } catch (Exception ignored) {
        }
        try {
            city = (String) userInfo.getCity();  /* city string initially set to the city i.e. earlier get updated*/
        } catch (Exception ignored) {
        }
        try {
            profileDOB.setText((String) userInfo.getDob());
        } catch (Exception ignored) {
        }
        try {
            pinCode.setText((String) userInfo.getZip());
        } catch (Exception ignored) {
        }
        try {
            if (userInfo.getMentorFor().equalsIgnoreCase("kid"))
                mentorFor.setSelection(1);
        } catch (Exception ignored) {
        }

        if (userInfo.getGender() != null) {
            if (userInfo.getGender().equals("M"))
                profileGender.setSelection(0);
            else
                profileGender.setSelection(1);
        }
        try {
            String temp = (String) userInfo.getTrainingLocation();
            if (temp == null || temp.equals(""))
                userInfo.setTrainingLocation(userInfo.getCity());
            trainingLocation.setText((String) userInfo.getTrainingLocation());
        } catch (Exception ignored) {
        }
        try {
            String selectedCoachingType = (String) userInfo.getCoachingType();
            coachingType.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, coachingTypeOptions));
            if (selectedCoachingType.equalsIgnoreCase(coachingTypeOptions[0]))
                coachingType.setSelection(0);
            else
                coachingType.setSelection(1);
        } catch (Exception ignored) {
        }


    }

    private void getAddress() {
        try {
            final GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if (DashboardActivity.dashboardActivity.userCurrentAddress.equals("")) {
                        DashboardActivity.dashboardActivity.userCurrentAddress = NetworkManager.getCompleteAddressString(EditProfileActivityMentee.this, location.getLatitude(), location.getLongitude());

                        if (!DashboardActivity.dashboardActivity.userCurrentAddress.equals("")) {
                            map.setOnMyLocationChangeListener(null);
                            updateAddress();
                            populateUserData();
                        }

                        DashboardActivity.dashboardActivity.latitude = location.getLatitude();
                        DashboardActivity.dashboardActivity.longitude = location.getLongitude();
                    }
                }
            };
            map.setMyLocationEnabled(true);
            map.setOnMyLocationChangeListener(myLocationChangeListener);
        } catch (Exception ignored) {
        }
    }

    public void updateAddress() {
        if (userInfo != null && (userInfo.getAddress() == null || userInfo.getAddress().toString().trim().equals(""))) {
            try {
                userInfo.setAddress(NetworkManager.localityName);
            } catch (Exception ignored) {
            }
        }

        if (userInfo != null && (userInfo.getCity() == null || userInfo.getCity().toString().trim().equals(""))) {
            try {
                userInfo.setCity(NetworkManager.cityName);
            } catch (Exception ignored) {
            }
        }

        if (userInfo != null && (userInfo.getZip() == null || userInfo.getZip().toString().trim().equals("") || userInfo.getZip().toString().trim().equals("0"))) {
            try {
                userInfo.setZip(NetworkManager.postalCodeName);
            } catch (Exception ignored) {
            }
        }
    }

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", getResources().getString(R.string.google_location_api_key));
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.autoComplete(getApplicationContext(), requestParams, this, 32);
    }

    // Validate user first name, last name and address
    private boolean validateUserUpdate() {
        boolean isValid = true;
        if (profileAddress1.getText().toString().trim().equals("")) {
            profileAddress1.setError(getResources().getString(R.string.enter_city));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    profileAddress1.setError(null);
                }
            }, 3500);
            isValid = false;
        }

        if (profileAddress.getText().toString().trim().equals("")) {
            profileAddress.setError(getResources().getString(R.string.enter_address));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    profileAddress.setError(null);
                }
            }, 3500);
            isValid = false;
        }

        if (pinCode.getText().toString().trim().equals("")) {
            pinCode.setError(getResources().getString(R.string.enter_pin));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pinCode.setError(null);
                }
            }, 3500);
            isValid = false;
        }

        String firstName = profileFirstName.getText().toString().trim().replaceAll(" ", "");
        if (firstName.equals("")) {
            showErrorMessage(profileFirstName, getResources().getString(R.string.error_field_required));
            isValid = false;
        } else {
            for (int i = 0; i < firstName.length(); i++) {
                if (!Character.isLetter(firstName.charAt(i))) {
                    showErrorMessage(profileFirstName, getResources().getString(R.string.error_not_a_name));
                    isValid = false;
                }
            }
        }

        String lastName = profileLastName.getText().toString().trim().replaceAll(" ", "");
        if (lastName.equals("")) {
            showErrorMessage(profileLastName, getResources().getString(R.string.error_field_required));
            isValid = false;
        } else {
            for (int i = 0; i < lastName.length(); i++) {
                if (!Character.isLetter(lastName.charAt(i))) {
                    showErrorMessage(profileLastName, getResources().getString(R.string.error_not_a_name));
                    isValid = false;
                }
            }
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

    private void callUpdateService() {
        progressDialog.show();
        try {
            RequestParams requestParams = new RequestParams();
            requestParams.add("first_name", profileFirstName.getText().toString());
            requestParams.add("last_name", profileLastName.getText().toString());
            String sex = profileGender.getSelectedItem().toString();
            if (sex.equals("Male"))
                requestParams.add("gender", "M");
            else
                requestParams.add("gender", "F");
            requestParams.add("dob", profileDOB.getText().toString());
            requestParams.add("address", profileAddress.getText().toString());
            requestParams.add("city", profileAddress1.getText().toString());
            requestParams.add("zip", pinCode.getText().toString());
            requestParams.add("mentor_for", mentorFor.getSelectedItem().toString());
            requestParams.add("training_location", trainingLocation.getText().toString());
            requestParams.add("coaching_type", coachingType.getSelectedItem().toString());
            if (!imageInBinary.equals(""))
                requestParams.add("photograph", imageInBinary);

            String authToken = StorageHelper.getUserDetails(this, getResources().getString(R.string.auth_token));
            requestParams.add("id", StorageHelper.getUserDetails(this, getResources().getString(R.string.user_id)));
            requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
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
    }

    @Override
    public void onBackPressed() {
        if (newUser == null || !newUser.contains(userInfo.getId()))
            finish();
        else
            Toast.makeText(this, R.string.prompt_update_profile, Toast.LENGTH_LONG).show();
    }

    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int day = c.get(Calendar.DATE);
            int month = c.get(Calendar.MONTH);
            c.set(Calendar.YEAR, year - 5);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year - 15, month, day);
            datePickerDialog.setTitle(getResources().getString(R.string.dob));
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return datePickerDialog;
        }
        return null;
    }

    private void showDate(int year, int month, int day) {
        profileDOB.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
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
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.textview, list);
        profileAddress1.setAdapter(arrayAdapter);
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
            try {
                String name = profileFirstName.getText().toString() + " " + profileLastName.getText().toString();
                StorageHelper.storePreference(this, "user_full_name", name);
            } catch (Exception ignored) {
            }
            finish();

            if (newUser != null && newUser.contains(userInfo.getId())) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(getResources().getString(R.string.new_user));
                editor.apply();
                DashboardActivity.dashboardActivity.container.setVisibility(View.VISIBLE);
                DashboardActivity.dashboardActivity.showTermsAndConditions();
            }
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        String message = (String) object;
        progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
