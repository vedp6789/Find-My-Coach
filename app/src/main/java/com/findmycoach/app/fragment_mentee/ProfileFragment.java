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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.EditProfileActivityMentee;
import com.findmycoach.app.adapter.AddressAdapter;
import com.findmycoach.app.adapter.ChildDetailsAdapter;
import com.findmycoach.app.beans.student.Address;
import com.findmycoach.app.beans.student.ChildDetails;
import com.findmycoach.app.beans.student.Data;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DateAsPerChizzle;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.views.ChizzleTextView;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements Callback {

    private final int REQUEST_CODE = 102;
    private ProgressDialog progressDialog;
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileDob;
    private TextView profileAddress;
    //private TextView profileCountry;
    private TextView trainingLocation;
    private TextView mentorFor;
    private TextView coachingType;
    private TextView profilePhone;
    private Data userInfo = null;
    private ImageLoader imgLoader;
    private ImageView editProfile;
    private TextView title;
    private RelativeLayout childDetailsLayout, preferredAddressLayout;
    private ChizzleTextView locationPreference;
    private ListView childrenDetailsListViewProfile, addressListView;
    private ArrayList<Address> addressArrayList;
    private AddressAdapter addressAdapter;
    private ChildDetailsAdapter childDetailsAdapter;
    private ArrayList<ChildDetails> childDetailsArrayList;


    private static final String TAG = "FMC";

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
//        progressDialog.show();
//        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
//        RequestParams requestParams = new RequestParams();
//        Log.d(TAG, "Stored User Id : " + StorageHelper.getUserDetails(getActivity(), "user_id"));
//        Log.d(TAG, "Auth Token : " + authToken);
//        requestParams.add("id", StorageHelper.getUserDetails(getActivity(), "user_id"));
//        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
//        Log.d(TAG, "getprofile");
//        NetworkClient.getProfile(getActivity(), requestParams, authToken, this, 4);

        ProfileResponse response = new Gson().fromJson(StorageHelper.getUserProfile(getActivity()), ProfileResponse.class);
        userInfo = response.getData();
        Log.e(TAG, StorageHelper.getUserProfile(getActivity()));
        populateFields();
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
        locationPreference = (ChizzleTextView) view.findViewById(R.id.location_preference);
        childDetailsLayout = (RelativeLayout) view.findViewById(R.id.childDetailsProfile);
        preferredAddressLayout = (RelativeLayout) view.findViewById(R.id.preferredAddressLayout);
        editProfile.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.edit_profile));
        addressListView = (ListView) view.findViewById(R.id.addressProfileListView);
        addressListView.setDivider(null);
        addressListView.setDividerHeight(0);

        childrenDetailsListViewProfile = (ListView) view.findViewById(R.id.childrenDetailsListViewProfile);
        childrenDetailsListViewProfile.setDivider(null);
        childrenDetailsListViewProfile.setDividerHeight(0);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "In onActivity Result" + requestCode + " : " + REQUEST_CODE + " : " + resultCode);
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
//        Log.d(TAG, "show profile");
//        ProfileResponse response = (ProfileResponse) object;
//        userInfo = response.getData();
//        populateFields();
    }

    private void populateFields() {
        try {
            try {
                if (userInfo.getMiddleName() == null || userInfo.getMiddleName().trim().equals(""))
                    profileName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
                else
                    profileName.setText(userInfo.getFirstName() + " " + userInfo.getMiddleName() + " " + userInfo.getLastName());
            } catch (Exception e) {
            }
            try {
                profileEmail.setText(userInfo.getEmail());
            } catch (Exception e) {
            }
            try {
                if(DateAsPerChizzle.YYYY_MM_DD_into_DD_MM_YYYY((String) userInfo.getDob()) != null){
                    profileDob.setText(DateAsPerChizzle.YYYY_MM_DD_into_DD_MM_YYYY((String) userInfo.getDob()));
                }
                //profileDob.setText((String) userInfo.getDob());
            } catch (Exception e) {
            }

            if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
                imgLoader = new ImageLoader(profileImage);
                imgLoader.execute((String) userInfo.getPhotograph());
            } else {
                profileImage.setImageDrawable(getResources().getDrawable(R.drawable.user_icon));
            }
            String[] mentor_for1 = getResources().getStringArray(R.array.mentor_for);
            try {
                mentorFor.setText(mentor_for1[Integer.parseInt(userInfo.getMentorFor())]);
            } catch (Exception ignored) {

            }
            trainingLocation.setText((String) userInfo.getTrainingLocation());

            String[] coachingType = getResources().getStringArray(R.array.coaching_type);
            switch (userInfo.getCoachingType()) {
                case 0:
                    this.coachingType.setText(coachingType[0]);
                    break;
                case 1:
                    this.coachingType.setText(coachingType[1]);
                    break;
                case 2:
                    this.coachingType.setText(coachingType[2]);
                    break;
            }


            String[] locationPreference = getResources().getStringArray(R.array.location_preference);
            switch (userInfo.getLocationPreference()) {
                case 0:
                    this.locationPreference.setText(locationPreference[0]);
                    break;
                case 1:
                    this.locationPreference.setText(locationPreference[1]);
                    break;
                case 2:
                    this.locationPreference.setText(locationPreference[2]);
                    break;
            }
            //      coachingType.setText((String) userInfo.getCoachingType());
            profilePhone.setText(userInfo.getPhonenumber());

            if (userInfo.getChildren().size() < 1) {
                childDetailsLayout.setVisibility(View.GONE);
            }


            Log.e(TAG, userInfo.getChildren().size() + " Child size");
            if (userInfo.getChildren() != null && userInfo.getChildren().size() >= 1) {
                childDetailsLayout.setVisibility(View.VISIBLE);
                childDetailsArrayList = new ArrayList<>();
                for (int i = 0; i < userInfo.getChildren().size(); i++) {
                    childDetailsArrayList.add(userInfo.getChildren().get(i));
                }
                childDetailsAdapter = new ChildDetailsAdapter(getActivity(), R.layout.child_details_list_item_centre_horizontal, childDetailsArrayList);
                childrenDetailsListViewProfile.setAdapter(childDetailsAdapter);
                childDetailsAdapter.notifyDataSetChanged();
                EditProfileActivityMentee.setHeight(childrenDetailsListViewProfile);
            }



            addressArrayList = new ArrayList<>();

            if (userInfo.getMultipleAddress() != null && userInfo.getMultipleAddress().size() > 1) {
                preferredAddressLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < userInfo.getMultipleAddress().size(); i++) {
                    int default_yn = userInfo.getMultipleAddress().get(i).getDefault_yn();
                    if (default_yn == 1) {
                        String address = "";
                        if (userInfo.getMultipleAddress().get(i).getAddressLine1() != null && !userInfo.getMultipleAddress().get(i).getAddressLine1().trim().equals("")) {
                            address = address + userInfo.getMultipleAddress().get(i).getAddressLine1().trim() + ", ";
                        }
                        if (userInfo.getMultipleAddress().get(i).getLocality() != null) {
                            address = address + userInfo.getMultipleAddress().get(i).getLocality() + ", ";
                        }
/*                        if (userInfo.getState() != null) {
                            address = address + userInfo.getState() + ", ";
                        }*/
                        if (userInfo.getMultipleAddress().get(i).getZip() != null) {
                            address = address + userInfo.getMultipleAddress().get(i).getZip();
                        }
                        profileAddress.setText(address);
                        continue;
                    }
                    addressArrayList.add(userInfo.getMultipleAddress().get(i));
                }


                addressAdapter = new AddressAdapter(getActivity(), R.layout.muti_address_list_item_centre_horizontal, addressArrayList);
                addressListView.setAdapter(addressAdapter);
                EditProfileActivityMentee.setListViewHeightBasedOnChildren(addressListView);
                //ListViewInsideScrollViewHelper.getListViewSize(addressListView);
            }


            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userInfo != null) {
                        ProfileResponse response = new Gson().fromJson(StorageHelper.getUserProfile(getActivity()), ProfileResponse.class);
                        Intent intent = new Intent(getActivity(), EditProfileActivityMentee.class);
                        intent.putExtra("user_info", new Gson().toJson(response.getData()));
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(userInfo.getAddressFlag().equalsIgnoreCase("0")){
            preferredAddressLayout.setVisibility(View.GONE);
        }
        else {
            preferredAddressLayout.setVisibility(View.VISIBLE);

        }

    }


    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.hide();
        String message = (String) object;
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
