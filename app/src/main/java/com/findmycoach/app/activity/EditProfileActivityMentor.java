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
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.findmycoach.app.R;
import com.findmycoach.app.adapter.AddressAdapter;
import com.findmycoach.app.adapter.QualifiedAreaOfCoachingAdapter;
import com.findmycoach.app.beans.authentication.AgeGroupPreferences;
import com.findmycoach.app.beans.authentication.Data;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.beans.authentication.SubCategoryName;
import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Datum;
import com.findmycoach.app.beans.category.DatumSub;
import com.findmycoach.app.beans.mentor.Currency;
import com.findmycoach.app.beans.student.Address;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.AddAddressDialog;
import com.findmycoach.app.util.AddressFromZip;
import com.findmycoach.app.util.BinaryForImage;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DataBase;
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

public class EditProfileActivityMentor extends Activity implements Callback, TeachingMediumPreferenceDialog.TeachingMediumAddedListener, AddAddressDialog.AddressAddedListener {

    int REQUEST_CODE = 100;
    private ImageView profilePicture;
    private TextView profileEmail;
    private TextView areaOfCoaching;
    private EditText profileFirstName;
    private EditText profileMiddleName;
    private EditText profileLastName;
    private Spinner profileGender;
    private TextView profileDOB;
    private EditText profileAddress;
    private AutoCompleteTextView profileAddress1;
    private EditText pinCode;
    private EditText accomplishment;
    private EditText chargeInput;
    private Spinner experienceInput, teachingPreference, classTypeSpinner;
    // private CheckBox isReadyToTravel;
    private Button updateAction;
    private Spinner chargesPerUnit;
    private ProgressDialog progressDialog;
    private Data userInfo;
    private String imageInBinary = "";
    ArrayAdapter<String> arrayAdapter;
    private String city = null;
    private String TAG = "FMC";
    private boolean isGettingAddress, isDobForReview;
    public boolean needToCheckOnDestroy;
    private List<Prediction> predictions;
    private ChizzleTextView addPhoto;
    private ChizzleTextView currencySymbol;
    private String userCurrentAddress = "";
    private ScrollView scrollView;
    private TextView students_preference;
    private RelativeLayout summaryHeader;
    private LinearLayout summaryDetailsLayout;
    private boolean hiddenFlag;
    private ImageButton arrow;
    private ChizzleTextView teachingMediumPreference;
    String o;
    ArrayList<Integer> arrayList;
    public static ArrayList<Integer> integerArrayList_Of_UpdatedStudentPreference;
    public EditProfileActivityMentor editProfileActivityMentor;
    private ChizzleEditText myQualification, myAccredition, myExperience, myTeachingMethodology, myAwards;
    private int ageAndExperienceErrorCounter;
    private JSONArray selectedAreaOfCoachingJson;
    private Category category;
    private CheckBox multipleAddressMentor;
    private Button addMoreAddress;
    private ListView addressListViewMentor;
    private ArrayList<Address> addressArrayListMentor;
    private AddressAdapter addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_mentor);
        initialize();
        applyAction();
        populateUserData();
        isGettingAddress = false;
        isDobForReview = false;
        needToCheckOnDestroy = false;
        integerArrayList_Of_UpdatedStudentPreference = new ArrayList<>();
        editProfileActivityMentor = this;
        if (userInfo.getCity() == null || userInfo.getCity().toString().trim().equals(""))
            getAddress();

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
        userInfo = new Gson().fromJson(getIntent().getStringExtra("user_info"), Data.class);
        ageAndExperienceErrorCounter = 0;
        Log.e(TAG, getIntent().getStringExtra("user_info"));
        profileGender = (Spinner) findViewById(R.id.input_gender);
        profilePicture = (ImageView) findViewById(R.id.profile_image);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileFirstName = (EditText) findViewById(R.id.input_first_name);
        profileMiddleName = (EditText) findViewById(R.id.input_middle_name);
        profileLastName = (EditText) findViewById(R.id.input_last_name);
        profileAddress = (EditText) findViewById(R.id.input_address);
        profileAddress1 = (AutoCompleteTextView) findViewById(R.id.input_address1);
        profileDOB = (TextView) findViewById(R.id.input_date_of_birth);
        pinCode = (EditText) findViewById(R.id.input_pin);
        chargeInput = (EditText) findViewById(R.id.input_charges);
        currencySymbol = (ChizzleTextView) findViewById(R.id.currencySymbol);
        chargeInput.setSelectAllOnFocus(true);
        accomplishment = (EditText) findViewById(R.id.input_accomplishment);
        experienceInput = (Spinner) findViewById(R.id.input_experience);
        teachingPreference = (Spinner) findViewById(R.id.teachingPreferencesSpinner);
        classTypeSpinner = (Spinner) findViewById(R.id.classTypeSpinner);
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
        addMoreAddress = (Button) findViewById(R.id.addAddressMentor);
        addressListViewMentor = (ListView) findViewById(R.id.addressesListViewMentor);
        addressArrayListMentor = new ArrayList<>();
        addressAdapter = new AddressAdapter(this, R.layout.muti_address_list_item, addressArrayListMentor);
        addressListViewMentor.setAdapter(addressAdapter);

        String[] yearOfExperience = new String[51];
        for (int i = 0; i < yearOfExperience.length; i++) {
            yearOfExperience[i] = String.valueOf(i);
        }

        multipleAddressMentor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addMoreAddress.setVisibility(View.VISIBLE);

                } else {
                    addressArrayListMentor.clear();
                    addressAdapter.notifyDataSetChanged();
                    addressListViewMentor.setVisibility(View.GONE);
                    addMoreAddress.setVisibility(View.GONE);
                }
            }
        });


        addMoreAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAddressDialog dialog = new AddAddressDialog(EditProfileActivityMentor.this);
                dialog.setAddressAddedListener(EditProfileActivityMentor.this);
                dialog.showPopUp();
            }
        });

        String[] preferences = getResources().getStringArray(R.array.teaching_preferences);
        String[] classType = getResources().getStringArray(R.array.mentor_class_type);

        addPhoto = (ChizzleTextView) findViewById(R.id.addPhotoMentor);
        experienceInput.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, yearOfExperience));
        teachingPreference.setAdapter(new ArrayAdapter<>(this, R.layout.textview, preferences));
        classTypeSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.textview, classType));
        //  isReadyToTravel = (CheckBox) findViewById(R.id.input_willing);
        updateAction = (Button) findViewById(R.id.button_update);
        chargesPerUnit = (Spinner) findViewById(R.id.chargesPerUnit);
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
        areaOfCoaching.setOnFocusChangeListener(onFocusChangeListener);
        areaOfCoaching.setOnTouchListener(onTouchListener);

        o = userInfo.getAgeGroupPreferences();

//        Log.d(TAG,"selected student pref: "+userInfo.getAgeGroupPreferences());
//        Log.d(TAG,"all selected student pref: "+userInfo.getAllAgeGroupPreferences().size());


        arrayList = new ArrayList<Integer>();
        if (o != null && !o.trim().equals("")) {
            String array_of_id[] = o.split(",");
            for (String id : array_of_id) {
                arrayList.add((Integer.parseInt(id)));
            }
        }
        populateSubjectPreference(arrayList, userInfo.getAllAgeGroupPreferences());


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

        profileAddress1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                city = null; /* making city null because if user do changes in city and does not select city from suggested city then this city string should be null which is used to validate the city */
                String input = profileAddress1.getText().toString().trim();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
                }
            }
        });


        profileAddress1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                getPostalFromCity(arrayAdapter.getItem(position));
                try {
                    NetworkManager.countryName = predictions.get(position).getCountry();
                    isGettingAddress = true;
                } catch (Exception e) {
                    NetworkManager.countryName = "";
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
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT)) {
                    try {
                        new AddressFromZip(EditProfileActivityMentor.this, profileAddress1, profileAddress, currencySymbol, true).execute(pinCode.getText().toString());
                        isGettingAddress = true;
                    } catch (Exception ignored) {
                    }
                    openAreaOfCoachingActivity();
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
                ArrayList<String> languagesList = new ArrayList<String>();
                languagesList.add(0, "Select");
                languagesList.add(1, "English");
                languagesList.add(2, "Mandarin");
                languagesList.add(3, "Hindi");
                languagesList.add(4, "Malay");
                languagesList.add(5, "Marathi");
                languagesList.add(6, "Spanish");

                int lng1 = 0;
                int lng2 = 0;
                int lng3 = 0;
                int lng4 = 0;

                String mediumSelected = teachingMediumPreference.getText().toString().trim();
                if (!mediumSelected.equals("") && !mediumSelected.equalsIgnoreCase(getResources().getString(R.string.select))) {
                    String[] arr = mediumSelected.split(",");
                    for (int i = 0; i < arr.length; i++) {
                        if (i == 0) {
                            lng1 = languagesList.indexOf(arr[i].trim());
                        } else if (i == 1) {
                            lng2 = languagesList.indexOf(arr[i].trim());
                        } else if (i == 2) {
                            lng3 = languagesList.indexOf(arr[i].trim());
                        } else if (i == 3) {
                            lng4 = languagesList.indexOf(arr[i].trim());
                        }
                    }
                }
                TeachingMediumPreferenceDialog dialog = new TeachingMediumPreferenceDialog(EditProfileActivityMentor.this, languagesList, lng1, lng2, lng3, lng4);
                dialog.setTeachingMediumAddedListener(EditProfileActivityMentor.this);
                dialog.showPopUp();
            }
        });


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
                profileMiddleName.setText(userInfo.getMiddleName());
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
                city = (String) userInfo.getCity();                 /* city string initially set to the city i.e. earlier get updated*/
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
                chargeInput.setText(userInfo.getCharges());
            } catch (Exception ignored) {
            }
            try {
                chargesPerUnit.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, new String[]{"hour"}));
                chargesPerUnit.setSelection(userInfo.getCharges().equals("0") ? 0 : 0);
            } catch (Exception ignored) {
            }

            try {
                int index = Integer.parseInt(userInfo.getExperience());
                if (index > 51)
                    index = 0;
                experienceInput.setSelection(index);
            } catch (Exception e) {
                experienceInput.setSelection(0);
            }
            if (userInfo.getGender() != null) {
                if (userInfo.getGender().equals("M"))
                    profileGender.setSelection(0);
                else
                    profileGender.setSelection(1);
            }
            if (userInfo.getAccomplishments() != null) {
                accomplishment.setText(userInfo.getAccomplishments());
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
            classTypeSpinner.setSelection(Integer.parseInt(userInfo.getSlotType()));
            if (userInfo.getCurrencyCode() != null) {
                String authToken = StorageHelper.getUserDetails(this, "auth_token");
                RequestParams requestParams = new RequestParams();
                requestParams.add("country", String.valueOf(userInfo.getCurrencyCode()));
                NetworkClient.getCurrencySymbol(this, requestParams, authToken, this, 52);

            } else {
                currencySymbol.setText(Html.fromHtml(StorageHelper.getCurrency(this)));
            }


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


            teachingMediumPreference.setText(StorageHelper.getUserDetails(EditProfileActivityMentor.this, "teaching_medium"));


            if (userInfo.getCity() == null || userInfo.getCity().toString().trim().equals(""))
                getAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (teachingMediumPreference.getText().toString().trim().equals(""))
            teachingMediumPreference.setText(StorageHelper.getUserGroup(this, "teaching_medium"));

        if (teachingMediumPreference.getText().toString().trim().equals(""))
            teachingMediumPreference.setText(getResources().getString(R.string.select));
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
                            if (updateAddress())
                                populateUserData();
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

    public boolean updateAddress() {
        if (userInfo != null && (userInfo.getAddress() == null || userInfo.getAddress().toString().trim().equals(""))) {
            try {
                userInfo.setAddress(NetworkManager.localityName);
                String s = NetworkManager.countryName;
            } catch (Exception ignored) {
            }
        }

        if (userInfo != null && (userInfo.getCity() == null || userInfo.getCity().toString().trim().equals(""))) {
            try {
                userInfo.setCity(NetworkManager.cityName);
            } catch (Exception ignored) {
            }
        }

        if (userInfo != null) {
            userInfo.setCurrencyCode(NetworkManager.countryCode);
        }

        if (userInfo != null && (userInfo.getZip() == null || userInfo.getZip().toString().trim().equals("") || userInfo.getZip().toString().trim().equals("0"))) {
            try {
                if (NetworkManager.postalCodeName != null && !NetworkManager.postalCodeName.trim().equals(""))
                    userInfo.setZip(NetworkManager.postalCodeName);
                else {
                    String address = userCurrentAddress.trim();
                    if (!address.equals("")) {
                        String[] temp = address.split(" ");
                        userInfo.setZip(temp[temp.length - 1]);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return true;
    }


    // Validate user first name, last name and address
    private boolean validateUserUpdate() {

        boolean isValid = true;

        if (profileAddress1.getText().toString().trim().equals("")) {
            profileAddress1.setError(getResources().getString(R.string.enter_locality));
            profileAddress1.requestFocus();
            isValid = false;
        }

//        if (profileAddress.getText().toString().trim().equals("")) {
//            profileAddress.setError(getResources().getString(R.string.enter_address));
//            if (isValid)
//                profileAddress.requestFocus();
//            isValid = false;
//        }

        if (profileDOB.getText().toString().trim().equals("")) {
            profileDOB.setError(getResources().getString(R.string.enter_dob));
            scrollView.fullScroll(ScrollView.FOCUS_UP);
            //Toast.makeText(EditProfileActivityMentor.this,getResources().getString(R.string.date_of_birth_please),Toast.LENGTH_SHORT).show();
            if (isValid)
                profileDOB.requestFocus();

            isValid = false;
        }

        if (pinCode.getText().toString().trim().equals("")) {
            pinCode.setError(getResources().getString(R.string.enter_pin));
            if (isValid)
                pinCode.requestFocus();
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
                dobYear = 0;
            }

            int yearsOfExperience = experienceInput.getSelectedItemPosition();
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
            requestParams.add("middle_name", profileMiddleName.getText().toString().trim());
            requestParams.add("last_name", profileLastName.getText().toString().trim());
            String sex = profileGender.getSelectedItem().toString();
            if (sex.equals("Male"))
                requestParams.add("gender", "M");
            else
                requestParams.add("gender", "F");


            try{
                String[] dob = profileDOB.getText().toString().split("-");
                int mon = 0;
                String[] months = getResources().getStringArray(R.array.months_short);
                for (int i = 0; i < months.length; i++) {
                    if (months[i].trim().equalsIgnoreCase(dob[1])) {
                        mon = i + 1;
                        break;
                    }
                }
                requestParams.add("dob", dob[2] + "-" + mon + "-" + dob[0]);
                Log.e(TAG + " dob", dob[2] + "-" + mon + "-" + dob[0]);
            }catch (Exception e){
                e.printStackTrace();
            }

            requestParams.add("address", profileAddress.getText().toString());
            requestParams.add("city", profileAddress1.getText().toString());
            requestParams.add("zip", pinCode.getText().toString());

            if (chargesPerUnit.getSelectedItemPosition() == 0) {
                Log.i(TAG, "select charges unit : " + chargesPerUnit.getSelectedItemPosition());
                requestParams.add("charges", chargeInput.getText().toString());
                requestParams.add("charges_class", "0");
            }

            requestParams.add("experience", experienceInput.getSelectedItemPosition() + "");
            requestParams.add("accomplishments", accomplishment.getText().toString());
            if (!imageInBinary.equals(""))
                requestParams.add("photograph", imageInBinary);
//            if (isReadyToTravel.isChecked())
//                requestParams.add("availability_yn", "1");
//            else
//                requestParams.add("availability_yn", "0");

            requestParams.add("availability_yn", String.valueOf(teachingPreference.getSelectedItemPosition()));
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
            requestParams.add("mutiple_address", new Gson().toJson(addressArrayListMentor));
            String authToken = StorageHelper.getUserDetails(this, "auth_token");
            requestParams.add("id", StorageHelper.getUserDetails(this, "user_id"));
            requestParams.add("user_group", StorageHelper.getUserGroup(this, "user_group"));
            requestParams.add("slot_type", String.valueOf(classTypeSpinner.getSelectedItemPosition()));
            requestParams.add("section_1", myQualification.getText().toString());
            requestParams.add("section_2", myAccredition.getText().toString());
            requestParams.add("section_3", myExperience.getText().toString());
            requestParams.add("section_4", myTeachingMethodology.getText().toString());
            requestParams.add("section_5", myAwards.getText().toString());
            if (isGettingAddress && NetworkManager.countryCode != null && !NetworkManager.countryCode.equals("")) {
                requestParams.add("country", NetworkManager.countryCode.trim());
                Log.e(TAG, "Country : " + NetworkManager.countryCode);
            }

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
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });

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
        profileAddress1.setAdapter(arrayAdapter);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {

        if (calledApiValue == 52) {
            Currency currency = (Currency) object;
            StorageHelper.setCurrency(this, currency.getCurrencySymbol());
            currencySymbol.setText(Html.fromHtml(currency.getCurrencySymbol()));
        } else if (object instanceof Suggestion) {
            Suggestion suggestion = (Suggestion) object;
            updateAutoSuggestion(suggestion);
        } else {
            progressDialog.dismiss();
            Response response = (Response) object;
            userInfo = response.getData();
            Log.d(TAG, "success response message : in EditProfileActivity : " + response.getMessage());

            String currencyCode = StorageHelper.getCurrency(this);
            if (currencyCode == null || currencyCode.trim().equals("")) {
                StorageHelper.setCurrency(this, userInfo.getCurrencyCode());
                Log.d(TAG, "Currency code : " + currencyCode);
            } else {
                currencySymbol.setText(Html.fromHtml(currencyCode));
            }

            Intent intent = new Intent();
            intent.putExtra("user_info", new Gson().toJson(userInfo));
            setResult(Activity.RESULT_OK, intent);
            try {
                String name = profileFirstName.getText().toString() + " " + profileLastName.getText().toString();
                StorageHelper.storePreference(this, "user_full_name", name);
            } catch (Exception ignored) {
            }

/* Saving mentor address in shared preference */

            /* Saving address, city and zip */
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
                StorageHelper.removePreference(this, "user_zip_code");

            if (isGettingAddress && NetworkManager.countryName != null && !NetworkManager.countryName.equals(""))
                StorageHelper.storePreference(this, "user_country", NetworkManager.countryName);


            Log.d(TAG, "local_add: " + StorageHelper.addressInformation(EditProfileActivityMentor.this, "user_local_address"));
            Log.d(TAG, "city: " + StorageHelper.addressInformation(EditProfileActivityMentor.this, "user_city_state"));
            Log.d(TAG, "local_add: " + StorageHelper.addressInformation(EditProfileActivityMentor.this, "user_zip_code"));




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


//            if (newUser != null && newUser.contains(userInfo.getId())) {
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.remove(getResources().getString(R.string.new_user));
//                editor.apply();
//                DashboardActivity.dashboardActivity.mainLayout.setVisibility(View.VISIBLE);

            boolean isAddSlotOpen = false;
            boolean isNewUser = getIntent().getBooleanExtra("new_user", false);
            if (!isDobForReview && isNewUser) {
                needToCheckOnDestroy = true;
                isAddSlotOpen = true;
                startActivity(new Intent(this, AddNewSlotActivity.class));
            }
//            }

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

    @Override
    public void onTeachingMediumAdded(String language1, String language2, String language3, String language4) {
        teachingMediumPreference.setText("");
        String finalString = "";
        if (!language1.equalsIgnoreCase("select")) {
            finalString += language1 + ", ";
        }
        if (!language2.equalsIgnoreCase("select")) {
            finalString += language2 + ", ";
        }
        if (!language3.equalsIgnoreCase("select")) {
            finalString += language3 + ", ";
        }
        if (!language4.equalsIgnoreCase("select")) {
            finalString += language4;
        }


        if (finalString.length() > 0 && finalString.charAt(finalString.length() - 1) == ' ') {
            finalString = finalString.substring(0, finalString.length() - 2);
        }

        teachingMediumPreference.setText(finalString);
        StorageHelper.storePreference(EditProfileActivityMentor.this, "teaching_medium", finalString);
    }

    @Override
    public void onAddressAdded(Address address) {
        addressArrayListMentor.add(address);
        addressAdapter.notifyDataSetChanged();
        EditProfileActivityMentee.setHeight(addressListViewMentor);
        addressListViewMentor.setVisibility(View.VISIBLE);
    }
}
