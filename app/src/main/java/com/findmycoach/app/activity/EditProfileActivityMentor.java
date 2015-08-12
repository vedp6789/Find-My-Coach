package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.findmycoach.app.R;
import com.findmycoach.app.adapter.AddressAdapter;
import com.findmycoach.app.adapter.ExperienceAdapter;
import com.findmycoach.app.adapter.QualifiedAreaOfCoachingAdapter;
import com.findmycoach.app.beans.CityDetails;
import com.findmycoach.app.beans.authentication.AgeGroupPreferences;
import com.findmycoach.app.beans.authentication.Data;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.beans.authentication.SubCategoryName;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Country;
import com.findmycoach.app.beans.category.Datum;
import com.findmycoach.app.beans.category.DatumSub;
import com.findmycoach.app.beans.mentor.CountryConfig;
import com.findmycoach.app.beans.student.Address;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.AddAddressDialog;
import com.findmycoach.app.util.BinaryForImage;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
import com.findmycoach.app.util.DateAsPerChizzle;
import com.findmycoach.app.util.MetaData;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.util.StudentsPreference;
import com.findmycoach.app.util.TeachingMediumPreferenceDialog;
import com.findmycoach.app.util.TermsAndCondition;
import com.findmycoach.app.views.ChizzleEditText;
import com.findmycoach.app.views.ChizzleTextView;
import com.findmycoach.app.views.ChizzleTextViewBold;
import com.findmycoach.app.views.DobPicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class EditProfileActivityMentor extends Activity implements Callback, AddAddressDialog.AddressAddedListener {

    int REQUEST_CODE = 100;
    private ImageView profilePicture;
    private TextView profileEmail;
    private TextView areaOfCoaching;
    private TextView experienceInput;
    private EditText profileFirstName;
    private EditText profileLastName;
    private Spinner profileGender, profileCountry;
    private TextView profileDOB;
    private EditText physicalAddress;
    private AutoCompleteTextView city_with_states;
    private AutoCompleteTextView locale;
    private Spinner teachingPreference;
    private Button updateAction;
    private ProgressDialog progressDialog;
    private Data userInfo;
    private String imageInBinary = "";
    ArrayAdapter<String> arrayAdapter;
    private String city = null, selected_city = null;
    private String TAG = "FMC";
    private boolean isGettingAddress, isDobForReview;
    public boolean needToCheckOnDestroy;
    private List<Prediction> predictions;
    private ChizzleTextView addPhoto;
    private String userCurrentAddress = "";
    private ScrollView scrollView;
    private TextView students_preference;
    private RelativeLayout summaryHeader, ll_physical_address;
    private LinearLayout summaryDetailsLayout, llCity;
    private boolean hiddenFlag;
    private ImageButton arrow;
    private ChizzleTextView teachingMediumPreference;
    String o;
    ArrayList<Integer> arrayList;
    public static ArrayList<Integer> integerArrayList_Of_UpdatedStudentPreference;
    public EditProfileActivityMentor editProfileActivityMentor;
    private ChizzleEditText myQualification, myAccredition, myExperience, myTeachingMethodology, myAwards;
    private ChizzleTextView myQualificationLimit, myAccreditionLimit, myExperienceLimit, myTeachingMethodologyLimit, myAwardsLimit;
    private int ageAndExperienceErrorCounter;
    private JSONArray selectedAreaOfCoachingJson;
    private Category category;
    private CheckBox multipleAddressMentor;
    private Button addMoreAddress;
    private ListView addressListViewMentor;
    private ArrayList<Address> addressArrayListMentor;
    private AddressAdapter addressAdapter;
    private boolean removeProfilePicture;
    private List<Country> countries;
    private ArrayList<String> country_names;
    public static int FLAG_FOR_EDIT_PROFILE_MENTOR = -11;
    private ChizzleTextView teachingMediumHeader;
    private String city_name, state_name;
    private ArrayList<CityDetails> list_of_city;
    private int country_id, city_id;
    boolean country_update;
    private ArrayList<CountryConfig> countryConfigArrayList;
    private RelativeLayout countryConditionLayout;
    private ChizzleTextView checkBoxCountryConditionText;
    private CheckBox countryConditionCheckBox;
    private int countyConfigId;
    private ArrayList<Integer> city_id_from_suggestion;
    private String city_updated;
    private RelativeLayout multipleAddressLayout;
    private ImageButton deleteLocaleButton;
    private List<String> mediumOfTeaching;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_mentor);
        initialize();
        applyAction();

        Log.e(TAG, userInfo.getMultipleAddress().size() + "");
        if (userInfo.getMultipleAddress() == null || userInfo.getMultipleAddress().size() == 0) {
            Log.e(TAG, "12");
            updateCountryByLocation();
            getAddress();

        }


        populateUserData();
        isGettingAddress = false;
        isDobForReview = false;
        needToCheckOnDestroy = false;
        integerArrayList_Of_UpdatedStudentPreference = new ArrayList<>();
        editProfileActivityMentor = this;

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
                profileCountry.setSelection(i + 1);  /* i+1 because first item is Select string of spinner*/
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
        editProfileActivityMentor = null;
        integerArrayList_Of_UpdatedStudentPreference = null;


        if (!needToCheckOnDestroy && DashboardActivity.dashboardActivity == null)
            startActivity(new Intent(this, DashboardActivity.class));
    }


    private void initialize() {
        list_of_city = new ArrayList<>();
        mediumOfTeaching = new ArrayList<>();
        city_with_states = (AutoCompleteTextView) findViewById(R.id.city_with_state);
        physicalAddress = (EditText) findViewById(R.id.physical_address);
        city_id_from_suggestion = new ArrayList<Integer>();
        locale = (AutoCompleteTextView) findViewById(R.id.locale);
        deleteLocaleButton = (ImageButton) findViewById(R.id.deleteLocaleButtonMentor);
        city_name = "";
        country_id = 0;
        city_id = 0;
        city_updated = "";
        userInfo = new Gson().fromJson(getIntent().getStringExtra("user_info"), Data.class);
        ageAndExperienceErrorCounter = 0;
        Log.e(TAG, getIntent().getStringExtra("user_info"));
        removeProfilePicture = false;
        profileGender = (Spinner) findViewById(R.id.input_gender);
        profilePicture = (ImageView) findViewById(R.id.profile_image);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileFirstName = (EditText) findViewById(R.id.input_first_name);
        profileLastName = (EditText) findViewById(R.id.input_last_name);
        profileDOB = (TextView) findViewById(R.id.input_date_of_birth);
        experienceInput = (TextView) findViewById(R.id.input_experience);
        teachingPreference = (Spinner) findViewById(R.id.teachingPreferencesSpinner);
        summaryHeader = (RelativeLayout) findViewById(R.id.summaryHeader);
        scrollView = (ScrollView) findViewById(R.id.main_scroll_view);
        summaryDetailsLayout = (LinearLayout) findViewById(R.id.summaryDetailsLayout);
        students_preference = (TextView) findViewById(R.id.students_preference);
        teachingMediumPreference = (ChizzleTextView) findViewById(R.id.teaching_medium_preference);
        arrow = (ImageButton) findViewById(R.id.arrow);
        multipleAddressMentor = (CheckBox) findViewById(R.id.inputMutipleAddressesMentor);
        myQualification = (ChizzleEditText) findViewById(R.id.myQualification);
        myAccredition = (ChizzleEditText) findViewById(R.id.myAccreditions);
        myExperience = (ChizzleEditText) findViewById(R.id.myExperience);
        myTeachingMethodology = (ChizzleEditText) findViewById(R.id.myTeachingMethodology);
        myAwards = (ChizzleEditText) findViewById(R.id.myAwards);
        myQualificationLimit = (ChizzleTextView) findViewById(R.id.myQualificationLimit);
        myAccreditionLimit = (ChizzleTextView) findViewById(R.id.myAccreditionsLimit);
        myExperienceLimit = (ChizzleTextView) findViewById(R.id.myExperienceLimit);
        myTeachingMethodologyLimit = (ChizzleTextView) findViewById(R.id.myTeachingMethodologyLimit);
        myAwardsLimit = (ChizzleTextView) findViewById(R.id.myAwardsLimit);
        addMoreAddress = (Button) findViewById(R.id.addAddressMentor);
        addressListViewMentor = (ListView) findViewById(R.id.addressesListViewMentor);
        addressArrayListMentor = new ArrayList<>();
        addressAdapter = new AddressAdapter(this, R.layout.muti_address_list_item, addressArrayListMentor, addressListViewMentor, false);
        addressListViewMentor.setAdapter(addressAdapter);
        teachingMediumHeader = (ChizzleTextView) findViewById(R.id.teachingMediumPreferenceHeader);
        profileCountry = (Spinner) findViewById(R.id.country);
        country_names = new ArrayList<String>();
        countries = new ArrayList<Country>();
        countryConfigArrayList = new ArrayList<>();
        countryConditionLayout = (RelativeLayout) findViewById(R.id.countryConditionLayout);
        countries = MetaData.getCountryObject(this);
        checkBoxCountryConditionText = (ChizzleTextView) findViewById(R.id.checkBoxCountryCondition);
        countryConditionCheckBox = (CheckBox) findViewById(R.id.inputCountryCondition);
        ll_physical_address = (RelativeLayout) findViewById(R.id.ll_physical_address);
        multipleAddressLayout = (RelativeLayout) findViewById(R.id.mutipleAddressCheckBoxLayout);
        llCity = (LinearLayout) findViewById(R.id.llCity);

        profileCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (countries != null && countries.size() > 0) {
                        country_id = countries.get(position - 1).getId();
                        RequestParams requestParams = new RequestParams();
                        requestParams.add("country_id", String.valueOf(country_id));
                        Log.e(TAG, "country_id: " + country_id);
                        NetworkClient.cities(EditProfileActivityMentor.this, requestParams, EditProfileActivityMentor.this, 54);
                    }

                    if (NetworkManager.isNetworkConnected(EditProfileActivityMentor.this)) {
                        RequestParams requestParams = new RequestParams();
                        requestParams.add("country_id", String.valueOf(countries.get(position - 1).getId()));
                        NetworkClient.getCountryConfig(EditProfileActivityMentor.this, requestParams, StorageHelper.getUserDetails(EditProfileActivityMentor.this, "auth_token"), EditProfileActivityMentor.this, 55);
                    } else
                        Toast.makeText(EditProfileActivityMentor.this, EditProfileActivityMentor.this.getString(R.string.check_network_connection), Toast.LENGTH_LONG).show();
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

        String[] yearOfExperience = new String[51];
        for (int i = 0; i < yearOfExperience.length; i++) {
            yearOfExperience[i] = String.valueOf(i);
        }

        deleteLocaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locale.setText("");
            }
        });


        multipleAddressMentor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addMoreAddress.setVisibility(View.VISIBLE);
                    addressListViewMentor.setVisibility(View.VISIBLE);
                } else {
                    addressListViewMentor.setVisibility(View.GONE);
                    addMoreAddress.setVisibility(View.GONE);
                }
            }
        });

        addMoreAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "country_id, :" + country_id + " city: " + city_with_states.getText().toString().trim() + "locale: " + locale.getText().toString().trim());
                if (country_id != 0 && !city_with_states.getText().toString().trim().isEmpty() && !locale.getText().toString().trim().isEmpty()) {
                    Log.e(TAG, "country_id, :" + country_id + " city: " + city_with_states.getText().toString() + "locale: " + locale.getText().toString().trim());
                    AddAddressDialog dialog = new AddAddressDialog(EditProfileActivityMentor.this);
                    dialog.setAddressAddedListener(EditProfileActivityMentor.this);
                    dialog.showPopUp();
                } else {
                    //Toast.makeText(EditProfileActivityMentor.this,getResources().getString(R.string.))
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

        String[] preferences = getResources().getStringArray(R.array.teaching_preferences);
        String[] classType = getResources().getStringArray(R.array.mentor_class_type);

        addPhoto = (ChizzleTextView) findViewById(R.id.addPhotoMentor);
        teachingPreference.setAdapter(new ArrayAdapter<>(this, R.layout.textview, preferences));
        updateAction = (Button) findViewById(R.id.button_update);
        areaOfCoaching = (TextView) findViewById(R.id.input_areas_of_coaching);
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


        profileDOB.setOnFocusChangeListener(onFocusChangeListener);
        profileDOB.setOnTouchListener(onTouchListener);
        areaOfCoaching.setOnFocusChangeListener(onFocusChangeListener);
        areaOfCoaching.setOnTouchListener(onTouchListener);

        o = userInfo.getAgeGroupPreferences();

        arrayList = new ArrayList<Integer>();
        if (o != null && !o.trim().equals("")) {
            String array_of_id[] = o.split(",");
            for (String id : array_of_id) {
                arrayList.add((Integer.parseInt(id)));
            }
        }
        populateSubjectPreference(arrayList, userInfo.getAllAgeGroupPreferences());


        findViewById(R.id.dobInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast =  Toast.makeText(EditProfileActivityMentor.this,
                        getResources().getText(R.string.dob_info_mentor), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
                toast.show();
            }
        });

        findViewById(R.id.physicalAddressInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfileActivityMentor.this,
                        getResources().getText(R.string.physical_address_mentor_info), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void populateSubjectPreference(ArrayList<Integer> arrayList, List<AgeGroupPreferences> allAgeGroupPreferences) {

        if (arrayList.size() > 0) {
            TreeSet<Integer> treeSet = new TreeSet<>();
            for (int i : arrayList) {
                treeSet.add(i);
            }
            boolean greater_than_one = false;

            if (treeSet.size() > 1) {
                greater_than_one = true;
            }


            StringBuilder stringBuilder = new StringBuilder();
            Iterator<Integer> iterator = treeSet.iterator();
            boolean first = true;
            while (iterator.hasNext()) {

                int id = iterator.next();
                for (AgeGroupPreferences ageGroupPreferences : allAgeGroupPreferences) {

                    if (greater_than_one) {

                        if (id == ageGroupPreferences.getId()) {
                            if (first) {
                                first = false;   /* To insert comma from second value */
                                stringBuilder.append(ageGroupPreferences.getValue().trim());
                                String min = ageGroupPreferences.getMin();
                                String max = ageGroupPreferences.getMax();
                                addInStringBuilder(min, max, stringBuilder);


                            } else {
                                stringBuilder.append(", " + ageGroupPreferences.getValue().trim());
                                String min = ageGroupPreferences.getMin();
                                String max = ageGroupPreferences.getMax();
                                addInStringBuilder(min, max, stringBuilder);

                            }
                        }


                    } else {
                        if (id == ageGroupPreferences.getId()) {
                            stringBuilder.append(ageGroupPreferences.getValue().trim());
                            String min1 = ageGroupPreferences.getMin();
                            String max1 = ageGroupPreferences.getMax();
                            addInStringBuilder(min1, max1, stringBuilder);
                        }
                    }
                }
            }
            students_preference.setText(stringBuilder.toString());
        } else {
            students_preference.setText(getResources().getString(R.string.select));
        }


    }

    private void addInStringBuilder(String min, String max, StringBuilder stringBuilder) {
        if (min != null && max != null) {
            if (min.equals("any") && max.equals("any")) {

            } else if (min.equals("any") && !max.equals("any")) {
                stringBuilder.append(" (" + getResources().getString(R.string.up_to) + " " + max + " " + getResources().getString(R.string.years) + ")");
            } else if (!min.equals("any") && max.equals("any")) {
                stringBuilder.append(" (" + min + " " + getResources().getString(R.string.and) + " " + getResources().getString(R.string.above) + ")");

            } else {
                stringBuilder.append(" (" + min + " - " + max + " " + getResources().getString(R.string.years) + ")");
            }
        }
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
                new DobPicker(EditProfileActivityMentor.this, profileDOB,
                        getResources().getInteger(R.integer.starting_year),
                        Calendar.getInstance().get(Calendar.YEAR) - getResources().getInteger(R.integer.mentor_min_age));
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
                    if (city_with_states.isPerformingCompletion()) {
                        return;
                    } else {
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
                Log.e(TAG, "postion: " + position);

                // Log.e(TAG,"city id on auto suggestion click: "+city_id_from_suggestion.get(position));
                Log.e(TAG, "city id with suggestions arraylist: " + city_id_from_suggestion.size());
                if (city_id_from_suggestion != null && city_id_from_suggestion.size() > 0) {
                    city_id = city_id_from_suggestion.get(position);
                }
            }
        });

        city_with_states.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    locale.requestFocus();
                }
                return false;
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
                if (!input.isEmpty())
                    deleteLocaleButton.setVisibility(View.VISIBLE);
                else
                    deleteLocaleButton.setVisibility(View.GONE);
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                }
            }
        });

        locale.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return false;
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
                if (validateUserUpdate()) {
                    callUpdateService();

                }
            }
        });

        students_preference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (integerArrayList_Of_UpdatedStudentPreference != null && integerArrayList_Of_UpdatedStudentPreference.size() > 0) {
                        StudentsPreference studentsPreference = new StudentsPreference(EditProfileActivityMentor.this, userInfo.getAllAgeGroupPreferences(), integerArrayList_Of_UpdatedStudentPreference, editProfileActivityMentor);
                        studentsPreference.showStudentPreferenceDialog();

                    } else {
                        StudentsPreference studentsPreference = new StudentsPreference(EditProfileActivityMentor.this, userInfo.getAllAgeGroupPreferences(), arrayList, editProfileActivityMentor);
                        studentsPreference.showStudentPreferenceDialog();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        summaryHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenFlag) {
                    summaryDetailsLayout.setVisibility(View.GONE);
                    arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                    hiddenFlag = false;

                } else {
                    summaryDetailsLayout.setVisibility(View.VISIBLE);
                    arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                    hiddenFlag = true;
                }
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenFlag) {
                    summaryDetailsLayout.setVisibility(View.GONE);
                    arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                    hiddenFlag = false;

                } else {
                    summaryDetailsLayout.setVisibility(View.VISIBLE);
                    arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                    hiddenFlag = true;
                }
            }
        });

        teachingMediumPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediumOfTeaching == null || mediumOfTeaching.size() < 1) {
                    progressDialog.show();
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("user_group", StorageHelper.getUserGroup(EditProfileActivityMentor.this, "user_group"));
                    requestParams.add("city_id", String.valueOf(city_id));
                    NetworkClient.getMediumOfEducation(EditProfileActivityMentor.this, requestParams,
                            StorageHelper.getUserDetails(EditProfileActivityMentor.this, "auth_token"), EditProfileActivityMentor.this, 57);
                } else
                    showMediumOfTeachingDialog(mediumOfTeaching);
            }
        });


        experienceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(EditProfileActivityMentor.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dob_dialog);
                GridView gridView = (GridView) dialog.findViewById(R.id.dobGrid);
                TextView title = (TextView) dialog.findViewById(R.id.title);
                title.setText(getResources().getString(R.string.prompt_select_experience_in_years));
                gridView.setAdapter(new ExperienceAdapter(1, EditProfileActivityMentor.this, dialog, experienceInput));
                dialog.show();
            }
        });


    }

    private void showMediumOfTeachingDialog(List<String> mediumOfTeaching) {

        TeachingMediumPreferenceDialog dialog = new TeachingMediumPreferenceDialog(EditProfileActivityMentor.this, mediumOfTeaching, teachingMediumPreference.getText().toString().trim());
        dialog.showPopUp();
    }


    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public void populateUserData() {
        try {
            if (userInfo == null) {
                return;
            }
            if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
                ImageLoader imgLoader = new ImageLoader(profilePicture);
                imgLoader.execute((String) userInfo.getPhotograph());
            } else {
                addPhoto.setText(getResources().getString(R.string.add_photo));
            }
            profileEmail.setText(userInfo.getEmail());

            try {
                profileFirstName.setText(userInfo.getFirstName());
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

            } catch (Exception ignored) {
            }


            try {
                experienceInput.setText(userInfo.getExperience());
            } catch (Exception e) {
                experienceInput.setText("");
            }
            if (userInfo.getGender() != null) {
                if (userInfo.getGender().equals("M"))
                    profileGender.setSelection(0);
                else
                    profileGender.setSelection(1);
            }
            try {
                teachingMediumPreference.setText(userInfo.getMediumOfEducation());
            } catch (Exception ignored) {
            }

            try {
                List<SubCategoryName> areaOfInterests = userInfo.getSubCategoryName();
                if (areaOfInterests != null && areaOfInterests.size() > 0 && areaOfInterests.get(0) != null) {
                    String areaOfInterest = "";
                    for (int index = 0; index < areaOfInterests.size(); index++) {
                        if (index != 0) {
                            areaOfInterest = areaOfInterest + ", " + areaOfInterests.get(index).getSub_category_name();
                        } else {
                            areaOfInterest = areaOfInterest + areaOfInterests.get(index).getSub_category_name();
                        }
                    }
                    areaOfCoaching.setText(areaOfInterest);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            areaOfCoaching.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAreaOfCoachingActivity();
                }
            });

            teachingPreference.setSelection(Integer.parseInt(userInfo.getAvailabilityYn()));

            teachingPreference.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0 || position == 2) {
                        ll_physical_address.setVisibility(View.VISIBLE);
                        physicalAddress.setVisibility(View.VISIBLE);
                        physicalAddress.setText(userInfo.getPhysical_address());
                    } else {
                        ll_physical_address.setVisibility(View.GONE);
                        physicalAddress.setVisibility(View.GONE);
                    }
                    if (position == 1 || position == 2) {
                        multipleAddressLayout.setVisibility(View.VISIBLE);
                        addressListViewMentor.setVisibility(View.VISIBLE);
                        addMoreAddress.setVisibility(View.VISIBLE);

                    } else {
                        multipleAddressLayout.setVisibility(View.GONE);
                        addressListViewMentor.setVisibility(View.GONE);
                        addMoreAddress.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            if (!userInfo.getSection1().equalsIgnoreCase("")) {
                myQualification.setText(userInfo.getSection1());
            }

            if (!userInfo.getSection2().equalsIgnoreCase("")) {
                myAccredition.setText(userInfo.getSection2());
            }
            if (!userInfo.getSection3().equalsIgnoreCase("")) {
                myExperience.setText(userInfo.getSection3());
            }
            if (!userInfo.getSection4().equalsIgnoreCase("")) {
                myTeachingMethodology.setText(userInfo.getSection4());
            }
            if (!userInfo.getSection5().equalsIgnoreCase("")) {
                myAwards.setText(userInfo.getSection5());
            }


            if (userInfo.getCountryConfigArrayList().size() >= 1) {
                if (userInfo.getCountryConfigArrayList().get(0).getConfigValue().equals("0"))
                    countryConditionCheckBox.setChecked(false);
                else
                    countryConditionCheckBox.setChecked(true);

            }

            addLimitListener(myQualification, myQualificationLimit);
            addLimitListener(myAccredition, myAccreditionLimit);
            addLimitListener(myExperience, myExperienceLimit);
            addLimitListener(myTeachingMethodology, myTeachingMethodologyLimit);
            addLimitListener(myAwards, myAwardsLimit);

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


                for (int i = 0; i < userInfo.getMultipleAddress().size(); i++) {
                    addressArrayListMentor.add(userInfo.getMultipleAddress().get(i));
                }
                addressAdapter.notifyDataSetChanged();
                setHeight(addressListViewMentor);

                if (userInfo.getMultipleAddress() != null && userInfo.getMultipleAddress().size() > 0) {
                    // multipleAddressMentor.setChecked(true);
                    addressListViewMentor.setVisibility(View.VISIBLE);
                    addMoreAddress.setVisibility(View.VISIBLE);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        if (userInfo.getAddressFlagMentor().equalsIgnoreCase("0")) {
            multipleAddressMentor.setChecked(false);
            addressListViewMentor.setVisibility(View.GONE);
            addMoreAddress.setVisibility(View.GONE);
        } else {
            multipleAddressMentor.setChecked(true);
            addMoreAddress.setVisibility(View.VISIBLE);
            addressListViewMentor.setVisibility(View.VISIBLE);

        }

    }

    private void addLimitListener(ChizzleEditText editText, final ChizzleTextView textViewForLimit) {
        textViewForLimit.setText(editText.getText().toString().length() + "/250");
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textViewForLimit.setText(s.length() + "/250");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void openAreaOfCoachingActivity() {
        String interests = areaOfCoaching.getText().toString();
        Log.d("FMC", "Area of Interests:" + interests);
        Intent intent = new Intent(getApplicationContext(), AreasOfInterestActivity.class);
        if (!interests.trim().equals(""))
            intent.putExtra("interests", interests);
        startActivityForResult(intent, 500);
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
                        userCurrentAddress = NetworkManager.getCompleteAddressString(EditProfileActivityMentor.this, location.getLatitude(), location.getLongitude());

                        if (!userCurrentAddress.equals("")) {
                            map.setOnMyLocationChangeListener(null);
                            if (updateAddress()) ;

                        }

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


    public boolean updateAddress() {   // TODO
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
        updateCountryByLocation();
        return true;
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

        if (city_with_states.getText().toString().trim().equals("")) {
            city_with_states.setError(getResources().getString(R.string.enter_city));
            if (isValid)
                city_with_states.requestFocus();

            isValid = false;
        }

        if (city_id == 0) {
            city_with_states.setError(getResources().getString(R.string.enter_city_from_suggestion));
            city_with_states.requestFocus();
            isValid = false;
        }

        if (teachingPreference.getSelectedItemPosition() == 0 || teachingPreference.getSelectedItemPosition() == 2) {
            if (physicalAddress.getText().toString().trim().isEmpty()) {
                physicalAddress.setError(getResources().getString(R.string.enter_physical_address));
                if (isValid)
                    physicalAddress.requestFocus();

                isValid = false;
            }
        }


        if (locale.getText().toString().trim().equals("")) {
            locale.setError(getResources().getString(R.string.enter_locale));
            if (isValid)
                locale.requestFocus();

            isValid = false;
        }


        if (profileDOB.getText().toString().trim().equals("")) {
            profileDOB.setError(getResources().getString(R.string.enter_dob));
            scrollView.fullScroll(ScrollView.FOCUS_UP);
            if (isValid)
                profileDOB.requestFocus();

            isValid = false;
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

        if (areaOfCoaching.getText().toString().trim().equals("")) {
            showErrorMessage(areaOfCoaching, getResources().getString(R.string.error_field_required));
            if (isValid)
                areaOfCoaching.requestFocus();
            isValid = false;
        }


        if (isValid) {
            int dobYear = 0;
            try {
                dobYear = Integer.parseInt(profileDOB.getText().toString().split("-")[2]);
            } catch (Exception e) {
                e.printStackTrace();
                dobYear = 0;
            }


            int yearsOfExperience = 0;
            try {
                yearsOfExperience = Integer.parseInt(experienceInput.getText().toString());
            } catch (Exception e) {
                yearsOfExperience = 0;
            }
            int age = Calendar.getInstance().get(Calendar.YEAR) - dobYear;
            int minExperience = getResources().getInteger(R.integer.mentor_min_age_experience_difference);

            Log.e(TAG, yearsOfExperience + " : " + age + " : " + minExperience);
            if (dobYear > 0 && age - yearsOfExperience < minExperience) {
                Toast.makeText(this, getResources().getString(R.string.age_experience_message), Toast.LENGTH_LONG).show();
                ageAndExperienceErrorCounter++;
                isValid = false;
            } else if (dobYear > 0 && (Calendar.getInstance().get(Calendar.YEAR) - (dobYear) < 19)) {
                Toast.makeText(this, getResources().getString(R.string.age_review_message), Toast.LENGTH_LONG).show();
                isDobForReview = true;
            }

            if (ageAndExperienceErrorCounter > 2) {
                needToCheckOnDestroy = true;
                logout();
                startActivity(new Intent(EditProfileActivityMentor.this, LoginActivity.class));
                if (Settings.settings != null)
                    Settings.settings.finish();
                if (DashboardActivity.dashboardActivity != null)
                    DashboardActivity.dashboardActivity.finish();

                Toast.makeText(this, getResources().getString(R.string.account_blocked), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        return isValid;

    }

    private void showErrorMessage(final TextView view, String string) {
        view.setError(string);
    }

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key", getResources().getString(R.string.google_location_api_key));
        requestParams.add("user_group", StorageHelper.getUserGroup(this, "user_group"));
        NetworkClient.autoComplete(getApplicationContext(), requestParams, this, 32);
    }

    private void callUpdateService() {
        progressDialog.show();
        try {
            RequestParams requestParams = new RequestParams();
            requestParams.add("first_name", profileFirstName.getText().toString().trim());
            requestParams.add("last_name", profileLastName.getText().toString().trim());
            String sex = profileGender.getSelectedItem().toString();
            if (multipleAddressMentor.isChecked()) {
                requestParams.add("multiple_address_flag", "1");

            } else {
                requestParams.add("multiple_address_flag", "0");

            }
            if (sex.equals("Male"))
                requestParams.add("gender", "M");
            else
                requestParams.add("gender", "F");

            try {
                String[] dob = profileDOB.getText().toString().split("-");
                requestParams.add("dob", dob[2] + "-" + dob[1] + "-" + dob[0]);
                Log.e(TAG + " dob", dob[2] + "-" + dob[1] + "-" + dob[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!experienceInput.getText().toString().equals(""))
                requestParams.add("experience", experienceInput.getText().toString());
            if (!imageInBinary.equals("") && !removeProfilePicture)
                requestParams.add("photograph", imageInBinary);
            else if (removeProfilePicture)
                requestParams.add("photograph", "");

            requestParams.add("availability_yn", String.valueOf(teachingPreference.getSelectedItemPosition()));

            requestParams.add("medium_of_education", teachingMediumPreference.getText().toString());
            Log.e(TAG, "medium_of_education : " + teachingMediumPreference.getText().toString());

            if (teachingPreference.getSelectedItemPosition() == 0 || teachingPreference.getSelectedItemPosition() == 2) {
                requestParams.add("address", physicalAddress.getText().toString().trim());
            }

            if (selectedAreaOfCoachingJson != null) {
                requestParams.add("sub_category", selectedAreaOfCoachingJson.toString());
                Log.e(TAG, selectedAreaOfCoachingJson.toString());
            }


            if (integerArrayList_Of_UpdatedStudentPreference != null && integerArrayList_Of_UpdatedStudentPreference.size() > 0) {
                for (int i : integerArrayList_Of_UpdatedStudentPreference) {
                    Log.e(TAG, "students age preference: " + i);
                }

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < integerArrayList_Of_UpdatedStudentPreference.size(); i++) {
                    if (integerArrayList_Of_UpdatedStudentPreference.size() > 1) {
                        if (i == 0) {
                            stringBuilder.append(integerArrayList_Of_UpdatedStudentPreference.get(i));
                        } else {
                            stringBuilder.append("," + integerArrayList_Of_UpdatedStudentPreference.get(i));
                        }
                    } else {
                        stringBuilder.append(integerArrayList_Of_UpdatedStudentPreference.get(i));
                    }
                }

                requestParams.add("preferences", stringBuilder.toString());
                Log.d(TAG, "preferences if :  " + stringBuilder.toString());
            } else {
                if (arrayList != null && arrayList.size() > 0) {
                    for (int i : arrayList) {
                        Log.e(TAG, "students age preference: " + i);
                    }

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.size() > 1) {
                            if (i == 0) {
                                stringBuilder.append(arrayList.get(i));
                            } else {
                                stringBuilder.append("," + arrayList.get(i));
                            }
                        } else {
                            stringBuilder.append(arrayList.get(i));
                        }
                    }

                    requestParams.add("preferences", stringBuilder.toString());
                    Log.d(TAG, "preferences: " + stringBuilder.toString());
                } else {
                    requestParams.add("preferences", "");
                    Log.d(TAG, "preferences else : " + "");
                }
            }

            Address address = new Address();
            try {
                address.setCountry(country_id);
                address.setCity_id(city_id);
                address.setPhysical_address("");
                address.setLocale(locale.getText().toString());
                address.setDefault_yn(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int position = -1;
            Log.e(TAG, addressArrayListMentor.size() + " address size");
            for (Address a : addressArrayListMentor) {
                Log.e(TAG, "Inside address for loop : " + a.getDefault_yn());
                if (a.getDefault_yn() == 1) {
                    position = addressArrayListMentor.indexOf(a);
                    Log.e(TAG, "found at : " + position);
                    break;
                }
            }
            try {
                if (position == -1)
                    addressArrayListMentor.add(address);
                else
                    addressArrayListMentor.set(position, address);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (countryConfigArrayList.size() > 0) {

                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("country_config_id", countyConfigId);
                    if (countryConditionCheckBox.isChecked()) {
                        jsonObject.put("config_value", "1");

                    } else {
                        jsonObject.put("config_value", "0");
                    }
                    jsonArray.put(0, jsonObject);
                    requestParams.add("country_config", jsonArray.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            requestParams.add("locations", new Gson().toJson(addressArrayListMentor));
            Log.e(TAG, "locations sending : " + new Gson().toJson(addressArrayListMentor));
            String authToken = StorageHelper.getUserDetails(this, "auth_token");
            requestParams.add("id", StorageHelper.getUserDetails(this, "user_id"));
            requestParams.add("user_group", StorageHelper.getUserGroup(this, "user_group"));
            requestParams.add("section_1", myQualification.getText().toString());
            requestParams.add("section_2", myAccredition.getText().toString());
            requestParams.add("section_3", myExperience.getText().toString());
            requestParams.add("section_4", myTeachingMethodology.getText().toString());
            requestParams.add("section_5", myAwards.getText().toString());
            Log.e(TAG, "request params: " + requestParams.toString());
            try {
                requestParams.add("country", String.valueOf(countries.get(profileCountry.getSelectedItemPosition() - 1).getId()));
            } catch (Exception ignored) {
            }
            NetworkClient.updateProfile(this, requestParams, authToken, this, 4);
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(this, getResources().getString(R.string.enter_necessary_details), Toast.LENGTH_LONG).show();
        }
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
        if (requestCode == 500 && resultCode == RESULT_OK && data != null) {
            String listJson = data.getStringExtra("interests");
            List<String> list = new Gson().fromJson(listJson, List.class);
            List<String> isSelected = new ArrayList<>();
            for (String s : list)
                isSelected.add("false");
            String interests = "";
            for (int index = 0; index < list.size(); index++) {
                if (interests.equalsIgnoreCase("")) {
                    interests = list.get(index);
                } else {
                    interests = interests + ", " + list.get(index);
                }
            }
            areaOfCoaching.setText(interests);
            if (list.size() > 0)
                showQualifiedOrNotDialog(list, isSelected);
        }
    }

    private void showQualifiedOrNotDialog(List<String> selectedAreaOfCoaching, List<String> isSelected) {
        List<SubCategoryName> subCategoryNames = userInfo.getSubCategoryName();
        for (int i = 0; i < selectedAreaOfCoaching.size(); i++) {
            for (SubCategoryName subCategoryName : subCategoryNames) {
                if (subCategoryName.getSub_category_name().trim().equalsIgnoreCase(selectedAreaOfCoaching.get(i).trim())) {
                    isSelected.set(i, "true");
                }
            }
        }

        final Dialog dialog = new Dialog(EditProfileActivityMentor.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.qualified_area_of_coachings);
        ListView listView = (ListView) dialog.findViewById(R.id.listView);
        final QualifiedAreaOfCoachingAdapter adapter = new QualifiedAreaOfCoachingAdapter(selectedAreaOfCoaching,
                isSelected, EditProfileActivityMentor.this);
        listView.setAdapter(adapter);

        DataBase dataBase = DataBase.singleton(this);
        try {
            String categoryData = dataBase.getAll();
            category = new Gson().fromJson(categoryData, Category.class);
        } catch (Exception e) {
            category = null;
            e.printStackTrace();
        }

        final Category finalCategory = category;
        dialog.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedAreaOfCoachingJson = new JSONArray();
                List<Integer> integers = new ArrayList<Integer>();

                if (category != null && category.getData().size() > 0) {
                    for (Datum datum : finalCategory.getData()) {
                        for (DatumSub datumSub : datum.getSubCategories()) {
                            if (adapter.selectedAreaOfCoaching.contains(datumSub.getName().trim())) {
                                integers.add(Integer.parseInt(datumSub.getId()));
                            }
                        }

                        for (Datum datum1 : datum.getCategories())
                            for (DatumSub datumSub : datum1.getSubCategories()) {
                                if (adapter.selectedAreaOfCoaching.contains(datumSub.getName().trim())) {
                                    integers.add(Integer.parseInt(datumSub.getId()));
                                }
                            }
                    }


                    for (int i = 0; i < adapter.selectedAreaOfCoaching.size(); i++) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            if (adapter.isSelected.get(i).equalsIgnoreCase("true"))
                                jsonObject.put("qualified", true);
                            else
                                jsonObject.put("qualified", false);
                            jsonObject.put("sub_category_id", integers.get(i));


                            selectedAreaOfCoachingJson.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.show();
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
                        startActivity(new Intent(EditProfileActivityMentor.this, LoginActivity.class));
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
        city_id_from_suggestion.clear();
        for (int index = 0; index < cityDetailses.size(); index++) {
            CityDetails cityDetails = cityDetailses.get(index);
            if (cityDetails.getCity_name().toLowerCase().contains(input_string.toLowerCase())) {
                if (cityDetails.getCity_state() != null && !cityDetails.getCity_state().trim().equals(""))
                    list.add(cityDetails.getCity_name() + " (" + cityDetails.getCity_state() + ")");
                else
                    list.add(cityDetails.getCity_name());
                city_id_from_suggestion.add(cityDetails.getCity_id());
            }
        }
        Log.e(TAG, "city id with suggestions arraylist: " + city_id_from_suggestion.size());
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.textview, list);
        city_with_states.setAdapter(arrayAdapter);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        Log.e(TAG, calledApiValue + " : ===");
        if (calledApiValue == 57) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject((String) object);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++)
                    mediumOfTeaching.add(jsonArray.getJSONObject(i).getString("name"));
                showMediumOfTeachingDialog(mediumOfTeaching);
            } catch (JSONException e) {
                e.printStackTrace();
                mediumOfTeaching.clear();
            }

        } else if (calledApiValue == 54) {
            list_of_city = (ArrayList<CityDetails>) object;

            try {
                if (list_of_city.size() == 1) {
                    city_id = list_of_city.get(0).getCity_id();
                    city_with_states.setText(list_of_city.get(0).getCity_name());
                    llCity.setVisibility(View.GONE);
                } else {
                    llCity.setVisibility(View.VISIBLE);
                }
            } catch (Exception ignored) {
                llCity.setVisibility(View.VISIBLE);
            }

            if (userInfo.getMultipleAddress() != null && userInfo.getMultipleAddress().size() == 0) {
                if (city_name != "" && list_of_city != null && list_of_city.size() > 0) {
                    for (int i = 0; i < list_of_city.size(); i++) {
                        try {
                            CityDetails cityDetails = list_of_city.get(i);

                            String city = cityDetails.getCity_name();
                            String state = cityDetails.getCity_state();
                            int city_country_id = cityDetails.getCity_country();
                            if (city.equalsIgnoreCase(city_name) && state.equalsIgnoreCase(state_name) && (city_country_id == country_id)) {
                                String s = "";
                                if (!state.trim().equals(""))
                                    s = city.trim() + " (" + state + ")";
                                else
                                    s = city.trim();
                                city_with_states.setText(s);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            } else {
                if (list_of_city != null && list_of_city.size() > 0 && city_id != 0)

                    for (int i = 0; i < list_of_city.size(); i++) {
                        try {
                            CityDetails cityDetails = list_of_city.get(i);
                            String city = cityDetails.getCity_name();
                            String state = cityDetails.getCity_state();
                            int city_country_id = cityDetails.getCity_country();
                            if (cityDetails.getCity_id() == city_id && country_id == city_country_id) {
                                String s = "";
                                if (!state.trim().equals(""))
                                    s = city.trim() + " (" + state + ")";
                                else
                                    s = city.trim();
                                city_with_states.setText(s);
                                break;
                            }
                        } catch (Exception ignored) {
                        }

                    }
            }
        } else if (object instanceof Suggestion) {
            Suggestion suggestion = (Suggestion) object;
            updateAutoSuggestion(suggestion);
        } else if (calledApiValue == 55) {
            countryConfigArrayList.clear();
            String response = (String) object;
            try {
                JSONArray jsonArray = new JSONArray(response);
                if (jsonArray.length() >= 1) {
                    countryConfigArrayList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<CountryConfig>>() {
                    }.getType());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (countryConfigArrayList.size() >= 1) {
                countyConfigId = countryConfigArrayList.get(0).getId();
                countryConditionLayout.setVisibility(View.VISIBLE);
                checkBoxCountryConditionText.setText(countryConfigArrayList.get(0).getConfigTitle());
            } else {
                countryConditionLayout.setVisibility(View.GONE);
            }

        } else {
            progressDialog.dismiss();
            Response response = (Response) object;
            userInfo = response.getData();
            Log.d(TAG, "success response message : in EditProfileActivity : " + response.getMessage());

            Intent intent = new Intent();
            intent.putExtra("user_info", new Gson().toJson(userInfo));
            setResult(Activity.RESULT_OK, intent);
            try {
                String name = profileFirstName.getText().toString() + " " + profileLastName.getText().toString();
                StorageHelper.storePreference(this, "user_full_name", name);
            } catch (Exception ignored) {
            }


            /* Saving area of coaching in sharedpreference */
            if (areaOfCoaching.getText().toString().length() > 0) {
                Log.d(TAG, "area_of coaching: " + areaOfCoaching.getText().toString());
                String coaching_subject[] = areaOfCoaching.getText().toString().split(",");
                Log.d(TAG, "coaching_subject size" + coaching_subject.length);
                Set<String> sub_category_stringSet = new HashSet<String>();
                for (int i = 0; i < coaching_subject.length; i++) {
                    sub_category_stringSet.add(coaching_subject[i].trim());
                }
                StorageHelper.storeListOfCoachingSubCategories(EditProfileActivityMentor.this, sub_category_stringSet);

            }


            Set<String> stringSet = new HashSet<String>();
            Log.d(TAG, "string set size: " + stringSet.size());
            if (stringSet != null) {
                stringSet = StorageHelper.getListOfCoachingSubCategories(EditProfileActivityMentor.this, "area_of_coaching_set");

                Iterator<String> iterator = stringSet.iterator();
                while (iterator.hasNext()) {
                    Log.d(TAG, "sub: " + iterator.next());
                }

            } else {
                Log.d(TAG, "string set null");
            }

            boolean isAddSlotOpen = false;
            boolean isNewUser = getIntent().getBooleanExtra("new_user", false);
            if (!isDobForReview && isNewUser) {
                needToCheckOnDestroy = true;
                isAddSlotOpen = true;
                startActivity(new Intent(this, AddNewSlotActivity.class));
            }

            if (!isAddSlotOpen && DashboardActivity.dashboardActivity == null)
                startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        String message = (String) object;
        progressDialog.dismiss();
        Log.d(TAG, "success response message : in EditProfileActivity : " + message);
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

    public void onTeachingMediumAdded(List<String> mediumOfTeachingSelected) {
        teachingMediumPreference.setText("");
        String temp = "";
        for (String s : mediumOfTeachingSelected)
            temp = temp + ", " + s;
        temp = temp.replaceFirst(", ", "");
        teachingMediumPreference.setText(temp.trim());
    }

    @Override
    public void onAddressAdded(Address address) {

        address.setCountry(country_id);
        address.setCity_id(city_id);
        address.setPhysical_address("");
        addressArrayListMentor.add(address);
        addressAdapter.notifyDataSetChanged();
        setHeight(addressListViewMentor);
        addMoreAddress.setVisibility(View.VISIBLE);
    }


    public static void setHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}
