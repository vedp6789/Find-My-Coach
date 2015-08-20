package com.findmycoach.app.fragment_mentor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.EditProfileActivityMentor;
import com.findmycoach.app.adapter.AddressAdapter;
import com.findmycoach.app.beans.Price;
import com.findmycoach.app.beans.authentication.AgeGroupPreferences;
import com.findmycoach.app.beans.authentication.Data;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.beans.authentication.SubCategoryName;
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DateAsPerChizzle;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.views.ChizzleTextView;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class ProfileFragment extends Fragment implements Callback {

    private final int REQUEST_CODE = 102;
    private ProgressDialog progressDialog;
    private ImageView profileImage;
    private TextView profileName, tv_flexibility_on_class_duration, tv_min_class_time, tv_flexibility_window,
            tv_max_class_time, tv_fixed_class_duration, tv_max_classes_in_week,
            tv_individual_class_price, tv_group_class_price, tv_individual_class_price_label, tv_group_class_price_label,
            tv_fixed_class_duration_label, tv_number_trial_lessons,tv_min_lessons_val;
    private TextView profileEmail;
    private TextView profileDob;
    private TextView profileAddress;
    private RatingBar profileRatting;
    private TextView profileExperience;
    private LinearLayout areaOfCoaching, loveToTeachLL, ll_class_type, ll_flexible_class_duration;
    private TextView profileTravelAvailable;
    private TextView profilePhone;
    private Data userInfo = null;
    private ImageLoader imgLoader;
    private ImageView editProfile;
    private TextView title, otherAddressTV;
    private ChizzleTextView myQualification, myAccreditions, myExperience, myTeachingMethodology, myAwards;
    private RelativeLayout summaryHeader;
    private LinearLayout aboutMeLL, teachingMediumText;
    private boolean hiddenFlag;
    private RelativeLayout multipleAddressRL, rl_fixed_class_duration, rl_individual_class_price, rl_group_class_price;
    private LinearLayout multipleAddressValRL;
    private ListView multipleAddressLV;
    private ArrayList<com.findmycoach.app.beans.student.Address> addressArrayListMentor;
    private AddressAdapter addressAdapter;
    boolean multiple_address_visible = false;
    private ImageButton arrow, arrow_multiple_address;
    private static final String TAG = "FMC:";
    private ScrollView scrollView;
    private ChizzleTextView gender;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_profile_mentor, container, false);
        initialize(view);
        getProfileInfo();
        return view;
    }

    private void getProfileInfo() {
        progressDialog.show();
        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
        RequestParams requestParams = new RequestParams();
        Log.d(TAG, "Stored User Id:" + StorageHelper.getUserDetails(getActivity(), "user_id"));
        Log.d(TAG, "auth_token" + authToken);
        requestParams.add("id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.getProfile(getActivity(), requestParams, authToken, this, 4);
    }

    private void initialize(View view) {
        scrollView = (ScrollView) view.findViewById(R.id.scrollview);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        profileImage = (ImageView) view.findViewById(R.id.profile_image);
        profileName = (TextView) view.findViewById(R.id.profile_name);
        profileName.requestFocus();
        profileAddress = (TextView) view.findViewById(R.id.profile_address);
        profileRatting = (RatingBar) view.findViewById(R.id.profile_rating);
        profileExperience = (TextView) view.findViewById(R.id.profile_experience);
        profileTravelAvailable = (TextView) view.findViewById(R.id.profile_travel_available);
        areaOfCoaching = (LinearLayout) view.findViewById(R.id.areas_of_coaching);
        loveToTeachLL = (LinearLayout) view.findViewById(R.id.loveToTeachLL);
        ll_class_type = (LinearLayout) view.findViewById(R.id.ll_class_type);
        profileEmail = (TextView) view.findViewById(R.id.profile_email);
        profileDob = (TextView) view.findViewById(R.id.profile_dob);
        profilePhone = (TextView) view.findViewById(R.id.profile_phone);
        editProfile = (ImageView) view.findViewById(R.id.menuItem);
        title = (TextView) view.findViewById(R.id.title);
        title.setText(getResources().getString(R.string.profile));
        teachingMediumText = (LinearLayout) view.findViewById(R.id.teachingMediumText);
        myQualification = (ChizzleTextView) view.findViewById(R.id.myQualificationText);
        myAccreditions = (ChizzleTextView) view.findViewById(R.id.myAccreditionsText);
        myExperience = (ChizzleTextView) view.findViewById(R.id.myExperienceText);
        myAwards = (ChizzleTextView) view.findViewById(R.id.myAwardsText);
        myTeachingMethodology = (ChizzleTextView) view.findViewById(R.id.myTeachingMethodologyText);
        summaryHeader = (RelativeLayout) view.findViewById(R.id.summaryHeaderProfile);
        aboutMeLL = (LinearLayout) view.findViewById(R.id.aboutMeLL);
        arrow = (ImageButton) view.findViewById(R.id.arrowProfile);
        arrow_multiple_address = (ImageButton) view.findViewById(R.id.arrowOtherAddress);
        otherAddressTV = (TextView) view.findViewById(R.id.otherAddressesTV);
        multipleAddressRL = (RelativeLayout) view.findViewById(R.id.multiple_Address_RL);
        multipleAddressValRL = (LinearLayout) view.findViewById(R.id.multiple_Address_Val_RL);
        multipleAddressLV = (ListView) view.findViewById(R.id.multipleAddressLV);
        addressArrayListMentor = new ArrayList<>();
        gender = (ChizzleTextView) view.findViewById(R.id.profile_gender);
        addressAdapter = new AddressAdapter(getActivity(), R.layout.muti_address_list_item_centre_horizontal, addressArrayListMentor, multipleAddressLV, false);
        multipleAddressLV.setAdapter(addressAdapter);
        editProfile.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.edit_profile));
        tv_min_class_time = (TextView) view.findViewById(R.id.tv_min_class_time);
        tv_flexibility_window = (TextView) view.findViewById(R.id.tv_flexibility_window);
        tv_max_class_time = (TextView) view.findViewById(R.id.tv_max_class_time);
        tv_fixed_class_duration = (TextView) view.findViewById(R.id.tv_fixed_class_duration);
        tv_max_classes_in_week = (TextView) view.findViewById(R.id.tv_max_classes_in_week);
        tv_flexibility_on_class_duration = (TextView) view.findViewById(R.id.tv_flexibility_on_class_duration);
        ll_flexible_class_duration = (LinearLayout) view.findViewById(R.id.ll_flexible_class_duration);
        rl_fixed_class_duration = (RelativeLayout) view.findViewById(R.id.rl_fixed_class_duration);
        rl_individual_class_price = (RelativeLayout) view.findViewById(R.id.rl_individual_class_price);
        rl_group_class_price = (RelativeLayout) view.findViewById(R.id.rl_group_class_price);
        tv_individual_class_price = (TextView) view.findViewById(R.id.tv_individual_class_price);
        tv_group_class_price = (TextView) view.findViewById(R.id.tv_group_class_price);
        tv_individual_class_price_label = (TextView) view.findViewById(R.id.individual_class_price);
        tv_group_class_price_label = (TextView) view.findViewById(R.id.group_class_price);
        tv_fixed_class_duration_label = (TextView) view.findViewById(R.id.fixed_class_duration);
        tv_number_trial_lessons = (TextView) view.findViewById(R.id.tv_number_trial_lessons);
        tv_min_lessons_val= (TextView) view.findViewById(R.id.tv_min_lessons_val);


        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenFlag) {
                    aboutMeLL.setVisibility(View.GONE);
                    arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                    hiddenFlag = false;
                    summaryHeader.requestFocus();

                } else {
                    aboutMeLL.setVisibility(View.VISIBLE);
                    arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                    hiddenFlag = true;
                    myAwards.requestFocus();
                }
            }
        });

        multipleAddressRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiple_address_visible) {
                    multiple_address_visible = false;
                    multipleAddressValRL.setVisibility(View.GONE);
                    multipleAddressRL.requestFocus();
                    arrow_multiple_address.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                } else {
                    multipleAddressValRL.setVisibility(View.VISIBLE);
                    multiple_address_visible = true;
                    arrow_multiple_address.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                    summaryHeader.requestFocus();
                }

            }
        });

        arrow_multiple_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiple_address_visible) {
                    multiple_address_visible = false;
                    multipleAddressValRL.setVisibility(View.GONE);
                    multipleAddressRL.requestFocus();
                    arrow_multiple_address.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                } else {
                    multipleAddressValRL.setVisibility(View.VISIBLE);
                    multiple_address_visible = true;
                    arrow_multiple_address.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                    summaryHeader.requestFocus();
                }

            }
        });


        summaryHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenFlag) {
                    aboutMeLL.setVisibility(View.GONE);
                    hiddenFlag = false;
                    summaryHeader.requestFocus();
                    arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));

                } else {
                    aboutMeLL.setVisibility(View.VISIBLE);
                    arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                    hiddenFlag = true;
                    myAwards.requestFocus();

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String updatedUserJson = data.getStringExtra("user_info");
            userInfo = new Gson().fromJson(updatedUserJson, Data.class);
            scrollView.fullScroll(View.FOCUS_UP);
            Toast.makeText(getActivity(), getResources().getString(R.string.profile_update), Toast.LENGTH_LONG).show();
            populateFields();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.hide();
        Response response = (Response) object;
        userInfo = response.getData();
        populateFields();
    }


    private void populateFields() {
        try {
            profileName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
        } catch (Exception e) {
        }
        try {
            profileEmail.setText(userInfo.getEmail());
        } catch (Exception e) {
        }
        try {
            if (DateAsPerChizzle.YYYY_MM_DD_into_DD_MM_YYYY((String) userInfo.getDob()) != null) {
                profileDob.setText(DateAsPerChizzle.YYYY_MM_DD_into_DD_MM_YYYY((String) userInfo.getDob()));
            }
        } catch (Exception e) {
        }
        if (userInfo.getExperience() != null) {
            int ex = 0;
            try {
                ex = Integer.parseInt(userInfo.getExperience());
            } catch (Exception e) {
                ex = 0;
            }
            profileExperience.setText(ex > 1 ? ex + " " + getResources().getString(R.string.years) : ex + " " + getResources().getString(R.string.year));
        }

        if (userInfo.getSection1() != null && !userInfo.getSection1().equalsIgnoreCase("")) {
            myQualification.setText(userInfo.getSection1());
        }

        if (userInfo.getSection2() != null && !userInfo.getSection2().equalsIgnoreCase("")) {
            myAccreditions.setText(userInfo.getSection2());
        }
        if (userInfo.getSection3() != null && !userInfo.getSection3().equalsIgnoreCase("")) {
            myExperience.setText(userInfo.getSection3());
        }
        if (userInfo.getSection4() != null && !userInfo.getSection4().equalsIgnoreCase("")) {
            myTeachingMethodology.setText(userInfo.getSection4());
        }
        if (userInfo.getSection5() != null && !userInfo.getSection5().equalsIgnoreCase("")) {
            myAwards.setText(userInfo.getSection5());
        }

        profilePhone.setText(userInfo.getPhonenumber());

        try {
            List<SubCategoryName> areaOfInterests = userInfo.getSubCategoryName();
            if (areaOfInterests != null && areaOfInterests.get(0) != null && areaOfInterests.size() > 0) {
                List<View> buttons = new ArrayList<>();
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                for (SubCategoryName title : areaOfInterests) {
                    Button button = (Button) inflater.inflate(R.layout.button, null);
                    button.setText(title.getSub_category_name());
                    buttons.add(button);
                    Log.e(TAG, title.getSub_category_name() + " : " + title.isQualified());
                }
                populateViews(areaOfCoaching, buttons, getActivity(), 50);
            }
        } catch (Exception ignored) {
        }

        String agePreferences = userInfo.getAgeGroupPreferences();
        if (agePreferences != null && !agePreferences.trim().equals("")) {
            ArrayList<String> integerArrayList = new ArrayList<>();
            String[] temp = agePreferences.split(",");
            Collections.addAll(integerArrayList, temp);
            String[] userSelectedAgePreferences = populateSubjectPreference(integerArrayList, userInfo.getAllAgeGroupPreferences()).split("###");

            List<View> views = new ArrayList<>();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (String title : userSelectedAgePreferences) {
                Button button = (Button) inflater.inflate(R.layout.button, null);
                button.setText(title);
                views.add(button);
            }
            populateViews(loveToTeachLL, views, getActivity(), 50);
        } else {
            loveToTeachLL.removeAllViews();
        }


        if (userInfo.getNumber_of_trial_classes() > 0) {
            tv_number_trial_lessons.setText(getResources().getString(R.string.trial_class_message1).trim()+" "+ userInfo.getNumber_of_trial_classes()+" "+getResources().getString(R.string.trial_class_message2));
        }

        if(userInfo.getMin_number_of_classes() > 0){
            tv_min_lessons_val.setText(userInfo.getMin_number_of_classes());
        }


        if (userInfo.getSlotType() != null) {
            List<View> views = new ArrayList<>();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Button button = (Button) inflater.inflate(R.layout.button, null);

            if (userInfo.getSlotType().equals("0")) {

                button.setText(getResources().getString(R.string.individual));
                views.add(button);
                populateViews(ll_class_type, views, getActivity(), 50);
            } else if (userInfo.getSlotType().equals("1")) {
                button.setText(getResources().getString(R.string.group));
                views.add(button);
                populateViews(ll_class_type, views, getActivity(), 50);

            } else if (userInfo.getSlotType().equals("2")) {
                button.setText(getResources().getString(R.string.individual));
                views.add(button);
                Button button2 = (Button) inflater.inflate(R.layout.button, null);
                button2.setText(getResources().getString(R.string.group));
                views.add(button2);
                populateViews(ll_class_type, views, getActivity(), 50);
            }
        }


        if (userInfo.getFlexible_yn() >= 0) {
            if (userInfo.getFlexible_yn() == 0) {
                tv_flexibility_on_class_duration.setText(getResources().getString(R.string.no));
                ll_flexible_class_duration.setVisibility(View.GONE);
                rl_fixed_class_duration.setVisibility(View.VISIBLE);
                tv_fixed_class_duration.setText("" + userInfo.getFixed_class_duration());

                if (userInfo.getSlotType().equals("0")) {
                    rl_individual_class_price.setVisibility(View.VISIBLE);
                    rl_group_class_price.setVisibility(View.GONE);
                    tv_individual_class_price_label.setText(getResources().getString(R.string.pricing_per_class_individual) + " " + Html.fromHtml(userInfo.getCurrencyOfUpdatedProfile().getUnicode()));
                    for (int price = 0; price < userInfo.getPrices().size(); price++) {
                        Price price1 = userInfo.getPrices().get(price);
                        if (price1.getType().equals("individual")) {
                            tv_individual_class_price.setText("" + price1.getPrice());
                        }
                    }


                } else if (userInfo.getSlotType().equals("1")) {
                    rl_individual_class_price.setVisibility(View.GONE);
                    rl_group_class_price.setVisibility(View.VISIBLE);
                    tv_group_class_price_label.setText(getResources().getString(R.string.pricing_per_class_group) + " " + Html.fromHtml(userInfo.getCurrencyOfUpdatedProfile().getUnicode()));
                    tv_fixed_class_duration_label.setText(getResources().getString(R.string.group_class_time_flexibility));
                    for (int price = 0; price < userInfo.getPrices().size(); price++) {
                        Price price1 = userInfo.getPrices().get(price);
                        if (price1.getType().equals("group")) {
                            tv_group_class_price.setText("" + price1.getPrice());
                        }
                    }

                } else if (userInfo.getSlotType().equals("2")) {
                    rl_individual_class_price.setVisibility(View.VISIBLE);
                    rl_group_class_price.setVisibility(View.VISIBLE);
                    tv_individual_class_price_label.setText(getResources().getString(R.string.pricing_per_class_individual) + " " + Html.fromHtml(userInfo.getCurrencyOfUpdatedProfile().getUnicode()));
                    tv_group_class_price_label.setText(getResources().getString(R.string.pricing_per_class_group) + " " + Html.fromHtml(userInfo.getCurrencyOfUpdatedProfile().getUnicode()));


                    for (int price = 0; price < userInfo.getPrices().size(); price++) {
                        Price price1 = userInfo.getPrices().get(price);
                        if (price1.getType().equals("individual")) {
                            tv_individual_class_price.setText("" + price1.getPrice());
                        } else if (price1.getType().equals("group")) {
                            tv_group_class_price.setText("" + price1.getPrice());
                        }
                    }

                }


            } else if (userInfo.getFlexible_yn() == 1) {
                tv_flexibility_on_class_duration.setText(getResources().getString(R.string.yes));
                ll_flexible_class_duration.setVisibility(View.VISIBLE);
                rl_fixed_class_duration.setVisibility(View.GONE);
                tv_min_class_time.setText("" + userInfo.getMin_class_duration());
                tv_flexibility_window.setText("" + userInfo.getFlexibility_window());
                tv_max_class_time.setText("" + userInfo.getMax_class_duration());

                if (userInfo.getSlotType().equals("0")) {
                    ll_flexible_class_duration.setVisibility(View.VISIBLE);
                    rl_fixed_class_duration.setVisibility(View.GONE);
                    tv_min_class_time.setText("" + userInfo.getMin_class_duration());
                    tv_flexibility_window.setText("" + userInfo.getFlexibility_window());
                    tv_max_class_time.setText("" + userInfo.getMax_class_duration());


                    rl_individual_class_price.setVisibility(View.VISIBLE);
                    rl_group_class_price.setVisibility(View.GONE);
                    tv_individual_class_price_label.setText(getResources().getString(R.string.pricing_per_hour_individual) + " " + Html.fromHtml(userInfo.getCurrencyOfUpdatedProfile().getUnicode()));
                    for (int price = 0; price < userInfo.getPrices().size(); price++) {
                        Price price1 = userInfo.getPrices().get(price);
                        if (price1.getType().equals("individual")) {
                            tv_individual_class_price.setText("" + price1.getPrice());
                        }
                    }

                } else if (userInfo.getSlotType().equals("1")) {
                    ll_flexible_class_duration.setVisibility(View.GONE);
                    rl_fixed_class_duration.setVisibility(View.VISIBLE);
                    tv_fixed_class_duration_label.setText(getResources().getString(R.string.group_class_time_flexibility));
                    tv_fixed_class_duration.setText("" + userInfo.getFixed_class_duration());

                    rl_individual_class_price.setVisibility(View.GONE);
                    rl_group_class_price.setVisibility(View.VISIBLE);
                    tv_group_class_price_label.setText(getResources().getString(R.string.pricing_per_hour_group) + " " + Html.fromHtml(userInfo.getCurrencyOfUpdatedProfile().getUnicode()));
                    for (int price = 0; price < userInfo.getPrices().size(); price++) {
                        Price price1 = userInfo.getPrices().get(price);
                        if (price1.getType().equals("group")) {
                            tv_group_class_price.setText("" + price1.getPrice());
                        }
                    }
                } else if (userInfo.getSlotType().equals("2")) {
                    ll_flexible_class_duration.setVisibility(View.VISIBLE);
                    rl_fixed_class_duration.setVisibility(View.VISIBLE);

                    tv_min_class_time.setText("" + userInfo.getMin_class_duration());
                    tv_flexibility_window.setText("" + userInfo.getFlexibility_window());
                    tv_max_class_time.setText("" + userInfo.getMax_class_duration());


                    tv_fixed_class_duration_label.setText(getResources().getString(R.string.group_class_time_flexibility));
                    tv_fixed_class_duration.setText("" + userInfo.getFixed_class_duration());



                    rl_individual_class_price.setVisibility(View.VISIBLE);
                    rl_group_class_price.setVisibility(View.VISIBLE);
                    tv_individual_class_price_label.setText(getResources().getString(R.string.pricing_per_hour_individual) + " " + Html.fromHtml(userInfo.getCurrencyOfUpdatedProfile().getUnicode()));
                    tv_group_class_price_label.setText(getResources().getString(R.string.pricing_per_hour_group) + " " + Html.fromHtml(userInfo.getCurrencyOfUpdatedProfile().getUnicode()));
                    for (int price = 0; price < userInfo.getPrices().size(); price++) {
                        Price price1 = userInfo.getPrices().get(price);
                        if (price1.getType().equals("individual")) {
                            tv_individual_class_price.setText("" + price1.getPrice());
                        } else if (price1.getType().equals("group")) {
                            tv_group_class_price.setText("" + price1.getPrice());
                        }
                    }
                }

            }
        }

        if (userInfo.getMax_classes_per_week() > 0) {
            tv_max_classes_in_week.setText("" + userInfo.getMax_classes_per_week());
        }


        try {
            String mediumOfTeaching = userInfo.getMediumOfEducation();
            if (mediumOfTeaching != null && !mediumOfTeaching.trim().equals("")) {
                String[] array = mediumOfTeaching.split(",");
                List<View> views = new ArrayList<>();
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                for (String title : array) {
                    Button button = (Button) inflater.inflate(R.layout.button, null);
                    button.setText(title);
                    views.add(button);
                }
                populateViews(teachingMediumText, views, getActivity(), 50);
            }
        } catch (Exception e) {
            teachingMediumText.removeAllViews();
        }


        try {
            profileRatting.setRating(Float.parseFloat(userInfo.getRating()));
        } catch (Exception e) {
            profileRatting.setRating(0f);
        }
        LayerDrawable stars = (LayerDrawable) profileRatting.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.purple_light), PorterDuff.Mode.SRC_ATOP);


        String[] teachingPreferences = getResources().getStringArray(R.array.teaching_preferences);
        switch (Integer.parseInt(userInfo.getAvailabilityYn())) {
            case 0:
                profileTravelAvailable.setText(teachingPreferences[0]);
                break;
            case 1:
                profileTravelAvailable.setText(teachingPreferences[1]);
                break;
            case 2:
                profileTravelAvailable.setText(teachingPreferences[2]);
                break;

        }
        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
            imgLoader = new ImageLoader(profileImage);
            imgLoader.execute((String) userInfo.getPhotograph());
        } else {
            profileImage.setImageDrawable(getResources().getDrawable(R.drawable.user_icon));
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo != null) {
                    Response response = new Gson().fromJson(StorageHelper.getUserProfile(getActivity()), Response.class);
                    Intent intent = new Intent(getActivity(), EditProfileActivityMentor.class);
                    intent.putExtra("user_info", new Gson().toJson(response.getData()));
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });

        if (userInfo.getMultipleAddress() != null && userInfo.getMultipleAddress().size() > 0) {
            multipleAddressValRL.setVisibility(View.GONE);
            arrow_multiple_address.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
            multiple_address_visible = false;
            if (userInfo.getMultipleAddress().size() > 2) {
                multipleAddressRL.setVisibility(View.VISIBLE);
                otherAddressTV.setText(getResources().getString(R.string.other_addresses));
            } else {
                if (userInfo.getMultipleAddress().size() > 1) {
                    multipleAddressRL.setVisibility(View.VISIBLE);
                    otherAddressTV.setText(getResources().getString(R.string.other_address));
                } else {
                    multipleAddressRL.setVisibility(View.GONE);
                }
            }

            addressArrayListMentor.clear();
            addressAdapter.notifyDataSetChanged();
            setHeight(multipleAddressLV);

            for (int i = 0; i < userInfo.getMultipleAddress().size(); i++) {
                int default_yn = userInfo.getMultipleAddress().get(i).getDefault_yn();
                if (default_yn == 1) {
                    String address = "";
                    if (userInfo.getMultipleAddress().get(i).getLocale() != null) {
                        address = address + userInfo.getMultipleAddress().get(i).getLocale();
                    }
                    profileAddress.setText(address);
                } else {
                    addressArrayListMentor.add(userInfo.getMultipleAddress().get(i));
                }
            }
            addressAdapter.notifyDataSetChanged();
            setHeight(multipleAddressLV);
            //ListViewInsideScrollViewHelper.getListViewSize(addressListView);
        } else {
            multipleAddressRL.setVisibility(View.GONE);
            multipleAddressValRL.setVisibility(View.GONE);
        }
        if (userInfo.getAddressFlagMentor().equalsIgnoreCase("0")) {
            multipleAddressRL.setVisibility(View.GONE);
        } else {
            multipleAddressRL.setVisibility(View.VISIBLE);

        }

        if (userInfo.getGender().equalsIgnoreCase("M")) {
            gender.setText("Male");
        } else {
            gender.setText("Female");
        }


    }

    public String populateSubjectPreference(ArrayList<String> arrayListString, List<AgeGroupPreferences> allAgeGroupPreferences) {

        ArrayList<Integer> arrayList = new ArrayList<>();

        for (String s : arrayListString)
            arrayList.add(Integer.parseInt(s));

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
                                stringBuilder.append("###").append(ageGroupPreferences.getValue().trim());
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
            return stringBuilder.toString();
        }
        return null;
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


    private void populateViews(LinearLayout linearLayout, List<View> views, Context context, int height) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        linearLayout.removeAllViews();
        int maxWidth = display.getWidth() - 40;

        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(context);
        newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        newLL.setOrientation(LinearLayout.HORIZONTAL);
        newLL.setGravity(Gravity.CENTER_HORIZONTAL);

        int widthSoFar = 0;

        for (View view : views) {
            LinearLayout LL = new LinearLayout(context);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            LL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(0, 0);
            params = new LinearLayout.LayoutParams(view.getMeasuredWidth(), height);
            params.setMargins(2, 2, 2, 2);

            LL.addView(view, params);
            LL.measure(0, 0);
            widthSoFar += view.getMeasuredWidth();
            if (widthSoFar >= maxWidth) {
                linearLayout.addView(newLL);

                newLL = new LinearLayout(context);
                newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
                newLL.setOrientation(LinearLayout.HORIZONTAL);
                newLL.setGravity(Gravity.CENTER_HORIZONTAL);
                params = new LinearLayout.LayoutParams(LL.getMeasuredWidth(), LL.getMeasuredHeight());
                newLL.addView(LL, params);
                widthSoFar = LL.getMeasuredWidth();
            } else {
                newLL.addView(LL);
            }
        }
        linearLayout.addView(newLL);
    }


    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.hide();
        Response response = new Gson().fromJson(StorageHelper.getUserProfile(getActivity()), Response.class);
        userInfo = response.getData();
        Log.e(TAG, StorageHelper.getUserProfile(getActivity()));
        populateFields();
    }

    public void setHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
