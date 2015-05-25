package com.findmycoach.app.fragment_mentor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.EditProfileActivityMentor;
import com.findmycoach.app.beans.authentication.Data;
import com.findmycoach.app.beans.authentication.Response;
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements Callback {

    private final int REQUEST_CODE = 102;
    private ProgressDialog progressDialog;
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileDob;
    private TextView profileAddress;
    private RatingBar profileRatting;
    private TextView profileExperience;
    private TextView profileAccomplishment;
    private TextView profileCharges;
    private LinearLayout areaOfCoaching;
    private TextView profileTravelAvailable;
    private TextView profilePhone;
    private Data userInfo = null;
    private ImageLoader imgLoader;
    private ImageView editProfile;
    private TextView title;

    private static final String TAG = "FMC:";

    public ProfileFragment() {
        // Required empty public constructor
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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        profileImage = (ImageView) view.findViewById(R.id.profile_image);
        profileName = (TextView) view.findViewById(R.id.profile_name);
        profileAddress = (TextView) view.findViewById(R.id.profile_address);
        profileRatting = (RatingBar) view.findViewById(R.id.profile_rating);
        profileExperience = (TextView) view.findViewById(R.id.profile_experience);
        profileAccomplishment = (TextView) view.findViewById(R.id.profile_accomplishment);
        profileCharges = (TextView) view.findViewById(R.id.profile_charges);
        profileTravelAvailable = (TextView) view.findViewById(R.id.profile_travel_available);
        areaOfCoaching = (LinearLayout) view.findViewById(R.id.areas_of_coaching);
        profileEmail = (TextView) view.findViewById(R.id.profile_email);
        profileDob = (TextView) view.findViewById(R.id.profile_dob);
        profilePhone = (TextView) view.findViewById(R.id.profile_phone);
        editProfile = (ImageView) view.findViewById(R.id.menuItem);
        title = (TextView) view.findViewById(R.id.title);
        title.setText(getResources().getString(R.string.profile));
        editProfile.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.edit_profile));

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "IN onActivity Result" + requestCode + "   " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String updatedUserJson = data.getStringExtra("user_info");
            userInfo = new Gson().fromJson(updatedUserJson, Data.class);
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
        profileName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
        try {
            profileEmail.setText(userInfo.getEmail());
        } catch (Exception e) {
        }
        try {
            profileDob.setText((String) userInfo.getDob());
        } catch (Exception e) {
        }
        String address = "";
        if (userInfo.getAddress() != null) {
            address = address + userInfo.getAddress() + ", ";
        }
        if (userInfo.getCity() != null) {
            address = address + userInfo.getCity() + ", ";
        }
        if (userInfo.getState() != null) {
            address = address + userInfo.getState() + ", ";
        }
        if (userInfo.getZip() != null) {
            address = address + userInfo.getZip();
        }
        profileAddress.setText(address);
        if (userInfo.getAccomplishments() != null) {
            profileAccomplishment.setText(userInfo.getAccomplishments());
        }
        if (userInfo.getExperience() != null) {
            profileExperience.setText(userInfo.getExperience() + " year(s)");
        }
        if (userInfo.getCharges() != null) {
            profileCharges.setText("\u20B9 " + (userInfo.getCharges().equals("0") ? userInfo.getCharges() + "/hr" : userInfo.getCharges() + "/hr"));

        }

        profilePhone.setText(userInfo.getPhonenumber());
        List<String> areaOfInterests = userInfo.getSubCategoryName();
        if (areaOfInterests.size() > 0 && areaOfInterests.get(0) != null && !areaOfInterests.get(0).trim().equals("")) {
            List<Button> buttons = new ArrayList<>();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (String areaOfInterest : areaOfInterests) {
                Button button = (Button) inflater.inflate(R.layout.button, null);
                button.setText(areaOfInterest);
                buttons.add(button);
            }
            populateViews(areaOfCoaching, buttons, getActivity());
        }

       try{
           profileRatting.setRating(Float.parseFloat(userInfo.getRating()));
       }catch (Exception e){
           profileRatting.setRating(0f);
       }
        LayerDrawable stars = (LayerDrawable) profileRatting.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.purple_light), PorterDuff.Mode.SRC_ATOP);

        if (userInfo.getAvailabilityYn() != null && userInfo.getAvailabilityYn().equals("1")) {
            profileTravelAvailable.setText(getResources().getString(R.string.yes));
        } else {
            profileTravelAvailable.setText(getResources().getString(R.string.no));
        }
        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
            imgLoader = new ImageLoader(profileImage);
            imgLoader.execute((String) userInfo.getPhotograph());
        }


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo != null) {
                    Intent intent = new Intent(getActivity(), EditProfileActivityMentor.class);
                    intent.putExtra("user_info", new Gson().toJson(userInfo));
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
    }


    private void populateViews(LinearLayout linearLayout, List<Button> views, Context context) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        linearLayout.removeAllViews();
        int maxWidth = display.getWidth() - 40;

        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(context);
        newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        newLL.setOrientation(LinearLayout.HORIZONTAL);
        newLL.setGravity(Gravity.CENTER_HORIZONTAL);

        int widthSoFar = 0;

        for (Button view : views) {
            LinearLayout LL = new LinearLayout(context);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            LL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(0, 0);
            params = new LinearLayout.LayoutParams(view.getMeasuredWidth(), profileEmail.getHeight());
            params.setMargins(2, 2, 2, 2);

            LL.addView(view, params);
            LL.measure(0, 0);
            widthSoFar += view.getMeasuredWidth();
            if (widthSoFar >= maxWidth) {
                linearLayout.addView(newLL);

                newLL = new LinearLayout(context);
                newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, profileEmail.getHeight()));
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
        String message = (String) object;
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
