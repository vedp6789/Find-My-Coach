package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.findmycoach.app.R;
import com.findmycoach.app.adapter.AddressAdapter;
import com.findmycoach.app.adapter.ChildDetailsAdapter;
import com.findmycoach.app.beans.CityDetails;
import com.findmycoach.app.beans.category.Country;
import com.findmycoach.app.beans.student.Address;
import com.findmycoach.app.beans.student.ChildDetails;
import com.findmycoach.app.beans.student.Data;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.AddAddressDialog;
import com.findmycoach.app.util.BinaryForImage;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.ChildDetailsDialog;
import com.findmycoach.app.util.DateAsPerChizzle;
import com.findmycoach.app.util.MetaData;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.util.TermsAndCondition;
import com.findmycoach.app.views.ChizzleTextView;
import com.findmycoach.app.views.ChizzleTextViewBold;
import com.findmycoach.app.views.DobPicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditProfileActivityMentee extends Activity implements Callback, ChildDetailsDialog.ChildDetailsAddedListener, AddAddressDialog.AddressAddedListener {


    int REQUEST_CODE = 100;
    private ImageView profilePicture;
    private TextView profileEmail;
    private EditText profileFirstName;
    private EditText profileMiddleName;
    private EditText profileLastName;
    private Spinner profileGender, profileCountry;
    private TextView profileDOB;
    private EditText physicalAddress;
    private AutoCompleteTextView city_with_states;
    private AutoCompleteTextView locale;
    private Spinner mentorFor;
    private TextView trainingLocation;
    private Spinner coachingType;
    private Button updateAction;
    private ProgressDialog progressDialog;
    private Data userInfo;
    private String imageInBinary = "";
    private String[] coachingTypeOptions;
    ArrayAdapter<String> arrayAdapter;
    private String city = null, selected_city = null;
    private static final String TAG = "FMC";
    private boolean isGettingAddress;
    private List<Prediction> predictions;
    private ChizzleTextView addPhoto;
    public boolean needToCheckOnDestroy;
    private String userCurrentAddress = "";
    private ScrollView scroll_view;
    private Spinner locationPreferenceSpinner;
    private RelativeLayout groupDetailsLayout;
    private ArrayList<ChildDetails> childDetailsArrayList;
    private ListView childDetailsListView, addressListView;
    private ChildDetailsAdapter childDetailsAdapter;
    private Button addMore, addAddress;
    private ArrayList<Address> addressArrayList;
    private AddressAdapter addressAdapter;
    private RadioGroup radioGroup;
    private RelativeLayout addChildLayout;
    private CheckBox multipleAddress;
    private boolean removeProfilePicture;
    private List<Country> countries;
    private ArrayList<String> country_names;
    public static int FLAG_FOR_EDIT_PROFILE_MENTEE = -5;
    private ChizzleTextView mentorForHeader;
    private String city_name, state_name;
    private ArrayList<CityDetails> list_of_city;
    private int country_id, city_id;
    private int user_info_multiple_address = 0;
    private LinearLayout ll_physical_address;
    private boolean training_location_similar_to_profile_locale;
    private ArrayList<Integer>  city_id_from_suggestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_mentee);
        initialize();
        applyAction();

        Log.e(TAG, userInfo.getMultipleAddress().size() + "");
        if (userInfo.getMultipleAddress() == null || userInfo.getMultipleAddress().size() == 0) {
            updateCountryByLocation();
            getAddress();
        }

        populateUserData();
        isGettingAddress = false;
        needToCheckOnDestroy = false;
    }

    public void updateCountryByLocation() {
        Log.e(TAG, "updateCountryByLocation method");
        if (countries != null && countries.size() > 0) {

            String country_code = "";
            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            //getNetworkCountryIso
            country_code = manager.getSimCountryIso().toUpperCase();
            if (country_code != "") {
                Log.e(TAG, "country code from SIM: " + country_code);
                populateCountry(country_code);
            } else {
                country_code = NetworkManager.countryCode;
                Log.e(TAG, "country code 1: " + country_code);
                if (country_code != null && country_code != "") {
                    populateCountry(country_code);
                }
            }

        }
    }

    public void populateCountry(String country_code) {
        for (int i = 0; i < countries.size(); i++) {
            Country country = countries.get(i);
            String country_code_from_server = country.getIso();
            Log.e(TAG, "country code" + i + " " + country_code_from_server);
            if (country_code_from_server.equalsIgnoreCase(country_code)) {
                profileCountry.setSelection(i + 1);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        String tNc = StorageHelper.getUserDetails(this, "terms");
        if (tNc == null || !tNc.equals("yes")) {
            Log.e(TAG, "TnC");
            TermsAndCondition termsAndCondition = new TermsAndCondition();
            termsAndCondition.showTermsAndConditions(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!needToCheckOnDestroy && DashboardActivity.dashboardActivity == null)
            startActivity(new Intent(this, DashboardActivity.class));
    }

    private void initialize() {
        list_of_city = new ArrayList<>();
        city_with_states = (AutoCompleteTextView) findViewById(R.id.city_with_state);
        ll_physical_address = (LinearLayout) findViewById(R.id.ll_physical_address);
        physicalAddress = (EditText) findViewById(R.id.physical_address);
        locale = (AutoCompleteTextView) findViewById(R.id.locale);
        city_name = "";
        country_id = 0;
        city_id = 0;
        coachingTypeOptions = getResources().getStringArray(R.array.coaching_type);
        userInfo = new Gson().fromJson(getIntent().getStringExtra("user_info"), Data.class);
        training_location_similar_to_profile_locale = false;
        if (userInfo != null && userInfo.getMultipleAddress() != null) {
            user_info_multiple_address = userInfo.getMultipleAddress().size();

            String preffered_training_location = "";
            if (userInfo.getTrainingLocation() != null) {
                preffered_training_location = userInfo.getTrainingLocation().toString();
            }
            if (userInfo.getMultipleAddress().size() > 0) {
                for (int i = 0; i < userInfo.getMultipleAddress().size(); i++) {
                    Address address = userInfo.getMultipleAddress().get(i);
                    if (address.getDefault_yn() == 1) {
                        if (preffered_training_location.equals(address.getLocale())) {
                            training_location_similar_to_profile_locale = true;
                        }
                        break;
                    }
                }
            }


        }

        removeProfilePicture = false;
        profileGender = (Spinner) findViewById(R.id.input_gender);
        profilePicture = (ImageView) findViewById(R.id.profile_image);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileFirstName = (EditText) findViewById(R.id.input_first_name);
        profileMiddleName = (EditText) findViewById(R.id.input_middle_name);
        profileLastName = (EditText) findViewById(R.id.input_last_name);
        profileDOB = (TextView) findViewById(R.id.input_date_of_birth);
        mentorFor = (Spinner) findViewById(R.id.input_mentor_for);
        locationPreferenceSpinner = (Spinner) findViewById(R.id.locationPreferenceSpinner);
        trainingLocation = (TextView) findViewById(R.id.input_training_location);
        coachingType = (Spinner) findViewById(R.id.input_coaching_type);
        updateAction = (Button) findViewById(R.id.button_update);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        addPhoto = (ChizzleTextView) findViewById(R.id.addPhotoMentee);
        scroll_view = (ScrollView) findViewById(R.id.main_scroll_view);
        groupDetailsLayout = (RelativeLayout) findViewById(R.id.groupClassesDetails);
        childDetailsListView = (ListView) findViewById(R.id.childDetailsListView);
        addMore = (Button) findViewById(R.id.addMoreButton);
        addAddress = (Button) findViewById(R.id.addAddress);
        multipleAddress = (CheckBox) findViewById(R.id.inputMutipleAddresses);
        addressListView = (ListView) findViewById(R.id.addressesListView);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupDetails);
        addChildLayout = (RelativeLayout) findViewById(R.id.addChildLayout);
        profileGender.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, getResources().getStringArray(R.array.gender)));
        childDetailsArrayList = new ArrayList<>();
        addressArrayList = new ArrayList<>();
        locationPreferenceSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, getResources().getStringArray(R.array.location_preference)));
        profileCountry = (Spinner) findViewById(R.id.country);
        country_names = new ArrayList<String>();
        countries = new ArrayList<Country>();
        countries = MetaData.getCountryObject(this);
        mentorForHeader = (ChizzleTextView) findViewById(R.id.mentorForHeader);


        profileCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String authToken = StorageHelper.getUserDetails(EditProfileActivityMentee.this, "auth_token");
//                city_with_states.setText("");
//                physicalAddress.setText("");
//                locale.setText("");
//                country_id = 0;
//                city_id = 0;
                if (position != 0) {
                    if (countries != null && countries.size() > 0) {
                        country_id = countries.get(position - 1).getId();
                        RequestParams requestParams = new RequestParams();
                        requestParams.add("country_id", String.valueOf(country_id));
                        NetworkClient.cities(EditProfileActivityMentee.this, requestParams, EditProfileActivityMentee.this, 54);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        country_names.add(getResources().getString(R.string.select));
        if (countries != null && countries.size() > 0) {
            for (int i = 0; i < countries.size(); i++) {
                Country country = countries.get(i);
                country_names.add(country.getShortName());
            }

            profileCountry.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, country_names));
        }

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        childDetailsAdapter = new ChildDetailsAdapter(this, R.layout.child_details_list_item, childDetailsArrayList, childDetailsListView);
        childDetailsListView.setAdapter(childDetailsAdapter);
        addressAdapter = new AddressAdapter(this, R.layout.muti_address_list_item, addressArrayList, addressListView);
        addressListView.setAdapter(addressAdapter);


        ///ListViewInsideScrollViewHelper.getListViewSize(addressListView);


        mentorFor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (position == 1 || position == 2) {
//                    childDetailsArrayList.clear();
//                    childDetailsAdapter.notifyDataSetChanged();
//                    childDetailsListView.setVisibility(View.VISIBLE);
                    addChildLayout.setVisibility(View.VISIBLE);
                    childDetailsListView.setVisibility(View.VISIBLE);

                } else {
                    childDetailsListView.setVisibility(View.GONE);
                    addChildLayout.setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        addChildLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChildDetailsDialog childDetailsDialog = new ChildDetailsDialog(EditProfileActivityMentee.this);
                childDetailsDialog.setChildAddedListener(EditProfileActivityMentee.this);
                childDetailsDialog.showPopUp();

            }
        });


        multipleAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addressListView.setVisibility(View.VISIBLE);
                    addAddress.setVisibility(View.VISIBLE);
                } else {

                    addressListView.setVisibility(View.GONE);
                    addAddress.setVisibility(View.GONE);

                }
            }
        });


        coachingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (position == 1 || position == 2) {
                    groupDetailsLayout.setVisibility(View.VISIBLE);
                } else {
                    groupDetailsLayout.setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChildDetailsDialog childDetailsDialog = new ChildDetailsDialog(EditProfileActivityMentee.this);
                childDetailsDialog.setChildAddedListener(EditProfileActivityMentee.this);
                childDetailsDialog.showPopUp();
            }
        });


        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (country_id != 0 && !city_with_states.getText().toString().trim().isEmpty() && !locale.getText().toString().trim().isEmpty()) {
                    AddAddressDialog dialog = new AddAddressDialog(EditProfileActivityMentee.this);
                    dialog.setAddressAddedListener(EditProfileActivityMentee.this);
                    dialog.showPopUp();
                } else {
                    if (country_id == 0) {
                        TextView errorText = (TextView) profileCountry.getSelectedView();
                        errorText.setError(getResources().getString(R.string.error_not_selected));
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    }

                    if (city_with_states.getText().toString().trim().equals("")) {
                        city_with_states.setError(getResources().getString(R.string.enter_city));
                        city_with_states.requestFocus();
                    }

                    if (locale.getText().toString().trim().equals("")) {
                        locale.setError(getResources().getString(R.string.enter_locale));
                        locale.requestFocus();
                    }
                }
            }
        });

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.title_edit_profile_menu));

//        profileFirstName.setOnFocusChangeListener(onFocusChangeListener);
//        profileFirstName.setOnTouchListener(onTouchListener);
//        profileLastName.setOnFocusChangeListener(onFocusChangeListener);
//        profileLastName.setOnTouchListener(onTouchListener);
        profileDOB.setOnFocusChangeListener(onFocusChangeListener);
        profileDOB.setOnTouchListener(onTouchListener);
//        profileAddress.setOnFocusChangeListener(onFocusChangeListener);
//        profileAddress.setOnTouchListener(onTouchListener);
//        profileAddress1.setOnFocusChangeListener(onFocusChangeListener);
//        profileAddress1.setOnTouchListener(onTouchListener);
//        pinCode.setOnFocusChangeListener(onFocusChangeListener);
//        pinCode.setOnTouchListener(onTouchListener);
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

    private void applyAction() {
        profileDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DobPicker(EditProfileActivityMentee.this, profileDOB,
                        getResources().getInteger(R.integer.starting_year),
                        Calendar.getInstance().get(Calendar.YEAR) - getResources().getInteger(R.integer.mentee_min_age));
            }
        });

        city_with_states.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                selected_city = null;/* making selected_city null because if user do changes in city and does not select city from suggested city then this selected_city string should be null which is used to validate the city */
                String input = city_with_states.getText().toString().trim();
                if (input.length() >= 2) {

                    if(city_with_states.isPerformingCompletion()) {
                        return;
                    }
                    else {
                        if (list_of_city != null && list_of_city.size() > 0) {
                            updateAutoSuggestionForCity(list_of_city, input);
                        }
                    }

                }
            }
        });


        city_with_states.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(city_id_from_suggestion != null && city_id_from_suggestion.size() > 0){
                    city_id = city_id_from_suggestion.get(position);
                }
            }
        });

        locale.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = locale.getText().toString().trim();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                }
            }
        });

        locale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userInfo != null && userInfo.getMultipleAddress() != null) {
                    if (user_info_multiple_address == 0 || user_info_multiple_address == 1 ||
                            training_location_similar_to_profile_locale) {
                            /* preferred training location will get populated, similar to profile
                             locale only when profile locale is first time populated or if it is
                              get edited when there is only one locale i.e. user has not not selected multiple addresses */

                            /*
                             or if training location is similar to locale of the
                            * */
                        trainingLocation.setText(locale.getText().toString());
                    }
                }
            }
        });


        locationPreferenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == 2) {
                    ll_physical_address.setVisibility(View.VISIBLE);
                    physicalAddress.setVisibility(View.VISIBLE);
                } else {
                    ll_physical_address.setVisibility(View.GONE);
                    physicalAddress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*profileAddress1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                city = null;
                String input = profileAddress1.getText().toString().trim();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                }
            }
        });

        profileAddress1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = arrayAdapter.getItem(position);
//                getPostalFromCity(city);
                try {
                    NetworkManager.countryName = predictions.get(position).getCountry();
                    isGettingAddress = true;
                } catch (Exception e) {
                    NetworkManager.countryName = "";
                }
            }
        });
*/

        trainingLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseImageActivity.class);
                profilePicture.buildDrawingCache();
                Bitmap bitMap = profilePicture.getDrawingCache();
                intent.putExtra("BitMap", BinaryForImage.getBinaryStringFromBitmap(bitMap));
                intent.putExtra("removeImageOption", addPhoto.getText());
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

        /*pinCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)
                        || actionId == EditorInfo.IME_ACTION_NEXT) {
                    try {
                        new AddressFromZip(EditProfileActivityMentee.this, EditProfileActivityMentee.this, profileAddress1, profileAddress, true, FLAG_FOR_EDIT_PROFILE_MENTEE).execute(pinCode.getText().toString());
                        isGettingAddress = true;
                    } catch (Exception ignored) {
                    }
                    hideKeyboard();
                    pinCode.clearFocus();
                    mentorForHeader.requestFocus();
                    return true;
                }
                return false;
            }
        });
*/
        /*profileAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        });*/

    }

//    private void getPostalFromCity(String city) {
//        Geocoder geocoder = new Geocoder(EditProfileActivityMentee.this, Locale.getDefault());
//        try {
//            ArrayList<Address> addresses = (ArrayList<Address>) geocoder.getFromLocationName(city.toString(), 1);
//            Address address = addresses.get(0);
//            Log.i(TAG, "address according to Geo coder : " + "\n postal code : " + address.getPostalCode() + "\n country name : " + address.getCountryName() + "\n address line 0 : " + address.getAddressLine(0) + "\n address line 1 : " + address.getAddressLine(1));
//            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                Log.i(TAG, "address line " + i + " : " + address.getAddressLine(i));
//            }
//            Log.i(TAG, "address locality " + address.getLocality() + "latitude : " + address.getLatitude() + "longitude : " + address.getLongitude());
//
//            double latitude = address.getLatitude();
//            double longitude = address.getLongitude();
//
//            final ArrayList<Double> doubles = new ArrayList<Double>();
//            doubles.add(latitude);
//            doubles.add(longitude);
//
//
//            new AsyncTask<ArrayList, Void, String>() {
//
//                @Override
//                protected String doInBackground(ArrayList... params) {
//                    ArrayList<Double> doubles1 = params[0];
//                    XPath xpath = XPathFactory.newInstance().newXPath();
//                    String expression = "//GeocodeResponse/result/address_component[type=\"postal_code\"]/long_name/text()";
//                    Log.d(TAG, "lat in async " + doubles1.get(0));
//                    Log.d(TAG, "long in async " + doubles1.get(1));
//
//                    InputSource inputSource = new InputSource("https://maps.googleapis.com/maps/api/geocode/xml?latlng=" + doubles1.get(0) + "," + doubles1.get(1) + "&sensor=true");
//                    String zipcode = null;
//                    try {
//                        zipcode = (String) xpath.evaluate(expression, inputSource, XPathConstants.STRING);
//                    } catch (XPathExpressionException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    Log.i(TAG, "zip code 1 : " + zipcode);
//
//
//                    return zipcode;
//                }
//
//
//                @Override
//                protected void onPostExecute(String s) {
//                    super.onPostExecute(s);
//                    if (s != null) {
//                        try {
//                            pinCode.setText(s);
//                        } catch (Exception ignored) {
//                        }
//                    } else {
//                        try {
//                            pinCode.setText("");
//                        } catch (Exception ignored) {
//                        }
//                    }
//
//
//                }
//            }.execute(doubles);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void populateUserData() {   //TODO

        String[] mentorForArray = getResources().getStringArray(R.array.mentor_for);

        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
            ImageLoader imgLoader = new ImageLoader(profilePicture);
            imgLoader.execute((String) userInfo.getPhotograph());
        } else {
            addPhoto.setText(getResources().getString(R.string.add_photo));
        }

        try {
            mentorFor.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, mentorForArray));
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
            profileMiddleName.setText(userInfo.getMiddleName());
        } catch (Exception ignored) {
        }
        try {
            profileLastName.setText(userInfo.getLastName());
        } catch (Exception ignored) {
        }
        try {
            if (DateAsPerChizzle.YYYY_MM_DD_into_DD_MM_YYYY((String) userInfo.getDob()) != null) {
                profileDOB.setText(DateAsPerChizzle.YYYY_MM_DD_into_DD_MM_YYYY((String) userInfo.getDob()));
            }
            //profileDOB.setText((String) userInfo.getDob());
        } catch (Exception ignored) {
        }
        try {
            /*if (userInfo.getMentorFor().equalsIgnoreCase(mentorForArray[1]))
                mentorFor.setSelection(1);
*/
            mentorFor.setSelection(Integer.parseInt(userInfo.getMentorFor()));

        } catch (Exception ignored) {
        }

        if (userInfo.getGender() != null) {
            if (userInfo.getGender().equals("M"))
                profileGender.setSelection(0);
            else
                profileGender.setSelection(1);
        }


        try {
            locationPreferenceSpinner.setSelection(userInfo.getLocationPreference());
            if (userInfo.getLocationPreference() == 0 || userInfo.getLocationPreference() == 2) {
                physicalAddress.setText(userInfo.getPhysical_address());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userInfo.getChildren() != null && userInfo.getChildren().size() >= 1) {
            for (int i = 0; i < userInfo.getChildren().size(); i++) {
                childDetailsArrayList.add(userInfo.getChildren().get(i));
            }
            childDetailsAdapter.notifyDataSetChanged();
            setHeight(childDetailsListView);
            childDetailsListView.setVisibility(View.VISIBLE);
            addChildLayout.setVisibility(View.VISIBLE);

        }


        Log.e(TAG, userInfo.getMultipleAddress().size() + " address size");
        if (userInfo.getMultipleAddress() != null && userInfo.getMultipleAddress().size() > 0) {

            int position = -1;
            for (Address a : userInfo.getMultipleAddress()) {
                Log.e(TAG, "Inside address for loop : " + a.getDefault_yn());
                if (a.getDefault_yn() == 1) {
                    position = userInfo.getMultipleAddress().indexOf(a);
                    Log.e(TAG, "found at : " + position);
                    break;
                }
            }

            Address address = null;
            if (position != -1) {
                address = userInfo.getMultipleAddress().get(position);
                userInfo.getMultipleAddress().remove(position);
            } else {
                address = userInfo.getMultipleAddress().get(0);
                userInfo.getMultipleAddress().remove(0);
            }

            try {

                country_id = address.getCountry();
                if (countries != null && countries.size() > 0) {
                    for (int i = 0; i < countries.size(); i++) {
                        Country country = countries.get(i);
                        if (country_id == country.getId()) {
                            profileCountry.setSelection(i + 1);  /* i+1 because first item of profileCountry is Select string */
                            city_id = 0;
                            city_with_states.setText("");
                            locale.setText("");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                city_id = address.getCity_id();
            } catch (Exception ignored) {
            }
            try {
                    city_with_states.setText(address.getCityName());
            } catch (Exception ignored) {
            }

            try {
                locale.setText(address.getLocale());
                Log.e(TAG, "locale for defaultyn 1: " + address.getLocale());
            } catch (Exception ignored) {
            }

            addressArrayList.clear();
            addressAdapter.notifyDataSetChanged();
            for (int i = 0; i < userInfo.getMultipleAddress().size(); i++) {
                addressArrayList.add(userInfo.getMultipleAddress().get(i));
            }
            addressAdapter.notifyDataSetChanged();
            //ListViewInsideScrollViewHelper.getListViewSize(addressListView);
            if (userInfo.getMultipleAddress() != null && userInfo.getMultipleAddress().size() > 0) {
                addressListView.setVisibility(View.VISIBLE);
                addAddress.setVisibility(View.VISIBLE);
                // multipleAddress.setChecked(true);
            }
            setListViewHeightBasedOnChildren(addressListView);

        }


        try {
            String temp = (String) userInfo.getTrainingLocation();
            if (temp == null || temp.equals("")) {
                if (locale.getText().toString().trim() != "") {
                    userInfo.setTrainingLocation(locale.getText().toString());
                }
            }
            trainingLocation.setText((String) userInfo.getTrainingLocation());
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        try {
            String selectedCoachingType = String.valueOf(userInfo.getCoachingType());
            coachingType.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, coachingTypeOptions));
            if (selectedCoachingType.equalsIgnoreCase("0"))
                coachingType.setSelection(0);
            else if (selectedCoachingType.equalsIgnoreCase("1"))
                coachingType.setSelection(1);
            else
                coachingType.setSelection(2);

        } catch (Exception ignored) {
        }

        if (userInfo.getAddressFlag().equalsIgnoreCase("0")) {
            multipleAddress.setChecked(false);
            addressListView.setVisibility(View.GONE);
            addAddress.setVisibility(View.GONE);
        } else {
            multipleAddress.setChecked(true);
            addressListView.setVisibility(View.VISIBLE);
            addAddress.setVisibility(View.VISIBLE);

        }

        if (userInfo.getGroupClassPreference() != null && userInfo.getGroupClassPreference().equalsIgnoreCase("0")) {
            radioGroup.check(R.id.radioHostingYes);
        } else {
            radioGroup.check(R.id.radioHostingNo);

        }


//        if (userInfo.getCity() == null || userInfo.getCity().toString().trim().equals(""))
//            getAddress();

    }

    private void getAddress() {
        if (isGettingAddress)
            return;
        try {
            isGettingAddress = true;
            final GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if (userCurrentAddress.equals("")) {
                        userCurrentAddress = NetworkManager.getCompleteAddressString(EditProfileActivityMentee.this, location.getLatitude(), location.getLongitude());

                        if (!userCurrentAddress.equals("")) {
                            map.setOnMyLocationChangeListener(null);
                            if (updateAddress()) ;
                        }

//                        DashboardActivity.dashboardActivity.latitude = location.getLatitude();
//                        DashboardActivity.dashboardActivity.longitude = location.getLongitude();
                    } else if (!userCurrentAddress.equals("")) {
                        map.setOnMyLocationChangeListener(null);
                        if (updateAddress())
                            populateUserData();
                    }
                }
            };
            map.setMyLocationEnabled(true);
            map.setOnMyLocationChangeListener(myLocationChangeListener);


        } catch (Exception ignored) {
        }
    }

    public boolean updateAddress() {   //TODO

        StringBuilder locale_string = new StringBuilder();

        if (NetworkManager.localityName != "") {
            locale_string.append(NetworkManager.localityName);
        }

        if (NetworkManager.cityName != "") {
            city_name = NetworkManager.cityName;
            state_name = NetworkManager.stateName;
            locale_string.append(", " + NetworkManager.cityName);
        }

        if (NetworkManager.postalCodeName != "") {
            locale_string.append(", " + NetworkManager.postalCodeName);
        }

        locale.setText(locale_string);
        trainingLocation.setText(locale_string);

        updateCountryByLocation();

        return true;


        /*if (userInfo != null && (userInfo.getAddress() == null || userInfo.getAddress().toString().trim().equals(""))) {
            try {
                profileAddress.setText(NetworkManager.localityName);
            } catch (Exception ignored) {
            }
        }

        if (userInfo != null && (userInfo.getCity() == null || userInfo.getCity().toString().trim().equals(""))) {
            try {
                profileAddress1.setText(NetworkManager.cityName);
            } catch (Exception ignored) {
            }
        }

        if (userInfo != null && (userInfo.getZip() == null || userInfo.getZip().toString().trim().equals("") || userInfo.getZip().toString().trim().equals("0"))) {
            try {
                if (NetworkManager.postalCodeName != null && !NetworkManager.postalCodeName.trim().equals(""))
                    userInfo.setZip(NetworkManager.postalCodeName);
                else {
                    String address = userCurrentAddress.trim();
                    if (!address.equals("")) {
                        String[] temp = address.split(" ");
                        pinCode.setText(temp[temp.length - 1]);
                    }
                }
            } catch (Exception ignored) {
            }
        }


            updateCountryByLocation();

        return true;*/
    }

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", getResources().getString(R.string.google_location_api_key));
        requestParams.add("user_group", StorageHelper.getUserGroup(this, "user_group"));
        NetworkClient.autoComplete(getApplicationContext(), requestParams, this, 32);
    }

    // Validate user first name, last name and address
    private boolean validateUserUpdate() {
        boolean isValid = true;

        if (country_id == 0) {
            TextView errorText = (TextView) profileCountry.getSelectedView();
            errorText.setError(getResources().getString(R.string.error_not_selected));
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            isValid = false;
        }

        if (city_with_states.getText().toString().trim().isEmpty()) {
            city_with_states.setError(getResources().getString(R.string.enter_city));
            city_with_states.requestFocus();
            isValid = false;
        }

        if (city_id == 0) {
            city_with_states.setError(getResources().getString(R.string.enter_city_from_suggestion));
            city_with_states.requestFocus();
            isValid = false;
        }

        if (locale.getText().toString().trim().equals("")) {
            locale.setError(getResources().getString(R.string.enter_locale));
            locale.requestFocus();
            isValid = false;
        }

        if (profileDOB.getText().toString().trim().equals("")) {
            profileDOB.setError(getResources().getString(R.string.enter_dob));
            scroll_view.fullScroll(ScrollView.FOCUS_UP);
            Toast.makeText(EditProfileActivityMentee.this, getResources().getString(R.string.date_of_birth_please), Toast.LENGTH_SHORT).show();
            if (isValid)
                profileDOB.requestFocus();
            isValid = false;
        }


        if (locationPreferenceSpinner.getSelectedItemPosition() == 0 || locationPreferenceSpinner.getSelectedItemPosition() == 2) {
            if (physicalAddress.getText().toString().trim().isEmpty()) {
                physicalAddress.setError(getResources().getString(R.string.enter_physical_address));
                physicalAddress.requestFocus();
                isValid = false;
            }
        }

        String firstName = profileFirstName.getText().toString().trim().replaceAll(" ", "");
        if (firstName.equals("")) {
            showErrorMessage(profileFirstName, getResources().getString(R.string.error_field_required));
            if (isValid)
                profileFirstName.requestFocus();
            isValid = false;
        } else {
            for (int i = 0; i < firstName.length(); i++) {
                if (!Character.isLetter(firstName.charAt(i))) {
                    showErrorMessage(profileFirstName, getResources().getString(R.string.error_not_a_name));
                    if (isValid)
                        profileFirstName.requestFocus();
                    isValid = false;
                }
            }
        }

        String lastName = profileLastName.getText().toString().trim().replaceAll(" ", "");
        if (lastName.equals("")) {
            showErrorMessage(profileLastName, getResources().getString(R.string.error_field_required));
            if (isValid)
                profileLastName.requestFocus();
            isValid = false;
        } else {
            for (int i = 0; i < lastName.length(); i++) {
                if (!Character.isLetter(lastName.charAt(i))) {
                    showErrorMessage(profileLastName, getResources().getString(R.string.error_not_a_name));
                    if (isValid)
                        profileLastName.requestFocus();
                    isValid = false;
                }
            }
        }
        return isValid;
    }

    private void showErrorMessage(final TextView view, String string) {
        view.setError(string);
    }

    private void callUpdateService() {
        progressDialog.show();
        try {
            RequestParams requestParams = new RequestParams();
            requestParams.add("first_name", profileFirstName.getText().toString().trim());
            requestParams.add("middle_name", profileMiddleName.getText().toString().trim());
            requestParams.add("last_name", profileLastName.getText().toString().trim());
            String sex = profileGender.getSelectedItem().toString();
            if (sex.equals("Male"))
                requestParams.add("gender", "M");
            else
                requestParams.add("gender", "F");

            requestParams.add("dob", getDobInYyMmDdFormat(profileDOB.getText().toString().trim()));

            /*requestParams.add("address", profileAddress.getText().toString());
            requestParams.add("city", profileAddress1.getText().toString());
            requestParams.add("zip", pinCode.getText().toString());*/
            requestParams.add("mentor_for", String.valueOf(mentorFor.getSelectedItemPosition()));

            String trainLoc = trainingLocation.getText().toString().trim();
            requestParams.add("training_location", trainingLocation.getText().toString());
            requestParams.add("coaching_type", String.valueOf(coachingType.getSelectedItemPosition()));
            if (multipleAddress.isChecked()) {
                requestParams.add("multiple_address_flag", "1");

            } else {
                requestParams.add("multiple_address_flag", "0");

            }

            if (coachingType.getSelectedItemPosition() == 1 || coachingType.getSelectedItemPosition() == 2) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.radioHostingYes) {
                    requestParams.add("group_class_preference", "0");

                } else {
                    requestParams.add("group_class_preference", "1");

                }
            }
            //TODO
            requestParams.add("children", new Gson().toJson(childDetailsArrayList));
            Log.e(TAG + " Kids ", new Gson().toJson(childDetailsArrayList));
            Address address = null;
            try {
                address = new Address();
                address.setCountry(country_id);
                address.setCity_id(city_id);
                address.setPhysical_address("");
                address.setLocale(locale.getText().toString());
                address.setDefault_yn(1);

            } catch (Exception e) {
                e.printStackTrace();
            }
            int position = -1;
            Log.e(TAG, addressArrayList.size() + " address size");
            for (Address a : addressArrayList) {
                Log.e(TAG, "Inside address for loop : " + a.getDefault_yn());
                if (a.getDefault_yn() == 1) {
                    position = addressArrayList.indexOf(a);
                    Log.e(TAG, "found at : " + position);
                    break;
                }
            }

            try {
                if (position == -1)
                    addressArrayList.add(address);
                else
                    addressArrayList.set(position, address);
            } catch (Exception e) {
                e.printStackTrace();
            }

            requestParams.add("locations", new Gson().toJson(addressArrayList));
            Log.e(TAG, "mul add: " + new Gson().toJson(addressArrayList));
            requestParams.add("availability_yn", String.valueOf(locationPreferenceSpinner.getSelectedItemPosition()));

            if (locationPreferenceSpinner.getSelectedItemPosition() == 0 || locationPreferenceSpinner.getSelectedItemPosition() == 2) {
                requestParams.add("address", physicalAddress.getText().toString());
            }

            if (!imageInBinary.equals("") && !removeProfilePicture)
                requestParams.add("photograph", imageInBinary);
            else if (removeProfilePicture)
                requestParams.add("photograph", "");

            String authToken = StorageHelper.getUserDetails(this, getResources().getString(R.string.auth_token));
            requestParams.add("id", StorageHelper.getUserDetails(this, getResources().getString(R.string.user_id)));
            requestParams.add("user_group", StorageHelper.getUserGroup(this, "user_group"));
            Log.e(TAG, "Country : " + NetworkManager.countryCode);
            requestParams.add("country", String.valueOf(countries.get(profileCountry.getSelectedItemPosition()).getId()));
            Log.e(TAG, "request params in edit profile post: " + requestParams);
            NetworkClient.updateProfile(this, requestParams, authToken, this, 4);

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, getResources().getString(R.string.enter_necessary_details), Toast.LENGTH_LONG).show();
        }
    }

    private String getDobInYyMmDdFormat(String dobInDdMmmYyyy) {
        try {
            String[] dob = dobInDdMmmYyyy.split("-");
            return dob[2] + "-" + dob[1] + "-" + dob[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            boolean removeImage = data.getBooleanExtra("removeImage", false);
            if (removeImage) {
                imageInBinary = "";
                profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.user_icon));
                addPhoto.setText(getResources().getString(R.string.add_photo));
                removeProfilePicture = true;
                return;
            }

            Bitmap userPic = (Bitmap) data.getParcelableExtra("image");
            profilePicture.setImageBitmap(userPic);
            try {
                imageInBinary = BinaryForImage.getBinaryStringFromBitmap(userPic);
                addPhoto.setText(getResources().getString(R.string.change_photo));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (DashboardActivity.dashboardActivity != null)
            finish();
        else
            showSignOutDialog();
    }

    private void showSignOutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.action_logout));
        final TextView textView = new ChizzleTextViewBold(this);
        textView.setText(getResources().getString(R.string.sure_to_logout));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(18, 18, 18, 18);
        textView.setPadding(10, 10, 10, 10);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18f);
        alertDialog.setView(textView);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(getResources().getString(R.string.action_logout),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        needToCheckOnDestroy = true;
                        logout();
                        startActivity(new Intent(EditProfileActivityMentee.this, LoginActivity.class));
                        dialog.dismiss();
                        finish();
                    }
                }
        );
        alertDialog.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                }
        );
        alertDialog.show();
    }

    private void updateAutoSuggestion(Suggestion suggestion) {
        ArrayList<String> list = new ArrayList<String>();
        predictions = suggestion.getPredictions();
        for (int index = 0; index < predictions.size(); index++) {
            list.add(predictions.get(index).getDescription());
        }
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.textview, list);
        locale.setAdapter(arrayAdapter);
    }


    private void updateAutoSuggestionForCity(ArrayList<CityDetails> cityDetailses, String input_string) {
        ArrayList<String> list = new ArrayList<String>();
        city_id_from_suggestion= new ArrayList<Integer>();
        for (int index = 0; index < cityDetailses.size(); index++) {
            CityDetails cityDetails = cityDetailses.get(index);
            if (cityDetails.getCity_name().toLowerCase().contains(input_string.toLowerCase())) {
                list.add(cityDetails.getCity_name() + " (" + cityDetails.getCity_state() + ")");
                city_id_from_suggestion.add(cityDetails.getCity_id());
            }
        }
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.textview, list);
        city_with_states.setAdapter(arrayAdapter);
    }


    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {


        if (object instanceof Suggestion) {
            Suggestion suggestion = (Suggestion) object;
            updateAutoSuggestion(suggestion);
        } else if (calledApiValue == 54) {
            list_of_city = (ArrayList<CityDetails>) object;

            if (userInfo.getMultipleAddress() != null && userInfo.getMultipleAddress().size() == 0) {
                if (city_name != "" && list_of_city != null && list_of_city.size() > 0) {
                    for (int i = 0; i < list_of_city.size(); i++) {
                        CityDetails cityDetails = list_of_city.get(i);

                        String city = cityDetails.getCity_name();
                        String state = cityDetails.getCity_state();
                        int city_country_id = cityDetails.getCity_country();

                        if (city.equals(city_name) && state.equals(state_name) && (city_country_id == country_id)) {
                            String s = city.trim() + " (" + state + ")";
                            city_with_states.setText(s);
                        }
                    }
                }
            } else {
                if (list_of_city != null && list_of_city.size() > 0 && city_id != 0)

                    for (int i = 0; i < list_of_city.size(); i++) {
                        CityDetails cityDetails = list_of_city.get(i);
                        String city = cityDetails.getCity_name();
                        String state = cityDetails.getCity_state();
                        int city_country_id = cityDetails.getCity_country();
                        if (cityDetails.getCity_id() == city_id && country_id == city_country_id) {
                            String s = city.trim() + " (" + state + ")";
                            city_with_states.setText(s);
                            break;
                        }

                    }
            }
        } else {
            progressDialog.dismiss();
            ProfileResponse response = (ProfileResponse) object;
            userInfo = response.getData();
            Log.d(TAG, "local_add: " + userInfo);

            /*StorageHelper.storePreference(this, "user_local_address", profileAddress.getText().toString());
            StorageHelper.storePreference(this, "user_city_state", profileAddress1.getText().toString());
            if (isGettingAddress && NetworkManager.countryName != null && !NetworkManager.countryName.equals(""))
                StorageHelper.storePreference(this, "user_country", NetworkManager.countryName);
            StorageHelper.storePreference(this, "user_zip_code", pinCode.getText().toString());
*/

            /*Log.d(TAG, "city: " + StorageHelper.addressInformation(EditProfileActivityMentee.this, "user_city_state"));
            Log.d(TAG, "local_add: " + StorageHelper.addressInformation(EditProfileActivityMentee.this, "user_zip_code"));
*/
            try {
                String name = profileFirstName.getText().toString() + " " + profileLastName.getText().toString();
                StorageHelper.storePreference(this, "user_full_name", name);
            } catch (Exception ignored) {
            }
//            if (newUser != null && newUser.contains(userInfo.getId())) {
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.remove(getResources().getString(R.string.new_user));
//                editor.apply();
//                Intent intent1 = new Intent(this, PaymentDetailsActivity.class);
//                intent1.putExtra("onBackPress", 1);
//                startActivity(intent1);
//            }

            /* Saving address, city and zip *//*
            if (!profileAddress.getText().toString().trim().equals("")) {
                StorageHelper.storePreference(this, "user_local_address", profileAddress.getText().toString());
            } else
                StorageHelper.removePreference(this, "user_local_address");

            if (!profileAddress1.getText().toString().trim().equals("")) {
                StorageHelper.storePreference(this, "user_city_state_country_info", profileAddress1.getText().toString());
            } else
                StorageHelper.removePreference(this, "user_city_state_country_info");

            if (response.getData().getZip() != null) {
                StorageHelper.storePreference(this, "user_zip_code", pinCode.getText().toString());
            } else
                StorageHelper.removePreference(this, "user_zip_code");*/


            if (!trainingLocation.getText().toString().equals("")) {
                StorageHelper.storePreference(this, "training_location", trainingLocation.getText().toString());
            }

            Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra("user_info", new Gson().toJson(userInfo));
            setResult(Activity.RESULT_OK, intent);


            if (DashboardActivity.dashboardActivity == null)
                startActivity(new Intent(this, DashboardActivity.class));

            finish();

        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        String message = (String) object;
        progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void logout() {

        String loginWith = StorageHelper.getUserDetails(this, "login_with");
        if (loginWith == null || loginWith.equals("G+")) {
            LoginActivity.doLogout = true;
            Log.e(TAG, "Logout G+ true");
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        StorageHelper.clearUser(this);
        StorageHelper.clearUserPhone(this);

        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
            }
        } else {
            session = new Session(this);
            Session.setActiveSession(session);
            session.closeAndClearTokenInformation();
        }
    }

    @Override
    public void onChildDetailsAdded(ChildDetails childDetails) {
        if (childDetails != null) {
            childDetails.setDob(getDobInYyMmDdFormat(childDetails.getDob().trim()));
            Log.e(TAG, childDetails.getDob());
            childDetailsArrayList.add(childDetails);
            childDetailsAdapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(childDetailsListView);
            childDetailsListView.setVisibility(View.VISIBLE);
            addMore.setVisibility(View.VISIBLE);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);


            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                int h = listItem.getMeasuredHeight();
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onAddressAdded(Address address) {
        address.setCountry(country_id);
        address.setCity_id(city_id);
        address.setPhysical_address("");
        addressArrayList.add(address);
        addressAdapter.notifyDataSetChanged();
        setHeight(addressListView);
        addressListView.setVisibility(View.VISIBLE);


    }

    public static void setHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}
