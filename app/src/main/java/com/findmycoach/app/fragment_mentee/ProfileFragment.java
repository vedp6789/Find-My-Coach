package com.findmycoach.app.fragment_mentee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.EditProfileActivityMentee;
import com.findmycoach.app.beans.student.StudentBean;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

public class ProfileFragment extends Fragment implements Callback {

    private final int REQUEST_CODE = 102;
    private ProgressDialog progressDialog;
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileDob;
    private TextView profileAddress;
    private TextView trainingLocation;
    private TextView mentorFor;
    private TextView coachingType;
    private TextView profilePhone;
    private StudentBean userInfo = null;
    private ImageLoader imgLoader;
    private ImageView editProfile;
    private TextView title;

    private static final String TAG="TAG";

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
        View view = inflater.inflate(R.layout.fragment_profile_mentee, container, false);
        initialize(view);
        getProfileInfo();
        return view;
    }

    private void getProfileInfo() {
        progressDialog.show();
        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
        RequestParams requestParams = new RequestParams();
        Log.d(TAG, "Stored User Id : " + StorageHelper.getUserDetails(getActivity(), "user_id"));
        Log.d(TAG, "Auth Token : " + authToken);
        requestParams.add("id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        Log.d(TAG, "getprofile");
        NetworkClient.getProfile(getActivity(), requestParams, authToken, this, 4);
    }

    private void initialize(View view) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        profileImage = (ImageView) view.findViewById(R.id.profile_image);
        profileName = (TextView) view.findViewById(R.id.profile_name);
        profileAddress = (TextView) view.findViewById(R.id.profile_address);
        trainingLocation = (TextView) view.findViewById(R.id.training_location);
        mentorFor = (TextView) view.findViewById(R.id.mentor_for);
        coachingType = (TextView) view.findViewById(R.id.coaching_type);
        profilePhone = (TextView) view.findViewById(R.id.profile_phone);
        profileEmail = (TextView) view.findViewById(R.id.profile_email);
        profileDob = (TextView) view.findViewById(R.id.profile_dob);
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
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String updatedUserJson = data.getStringExtra("user_info");
            userInfo = new Gson().fromJson(updatedUserJson, StudentBean.class);
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
        Log.d(TAG, "show profile");
        ProfileResponse response = (ProfileResponse) object;
        userInfo = response.getStudentBean();
        populateFields();
    }

    private void populateFields() {
        try{
            profileName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
        }catch (Exception e){
        }
        try{
            profileEmail.setText(userInfo.getEmail());
        }catch (Exception e){
        }
        try{
            profileDob.setText((String) userInfo.getDob());
        }catch (Exception e){
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
        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
           imgLoader = new ImageLoader(profileImage);
           imgLoader.execute((String) userInfo.getPhotograph());
        }
        mentorFor.setText(userInfo.getMentorFor());
        trainingLocation.setText((String) userInfo.getTrainingLocation());
        coachingType.setText((String) userInfo.getCoachingType());
        profilePhone.setText(userInfo.getPhonenumber());

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo != null) {
                    Intent intent = new Intent(getActivity(), EditProfileActivityMentee.class);
                    intent.putExtra("user_info", new Gson().toJson(userInfo));
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });


    }


    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.hide();
        String message = (String) object;
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
