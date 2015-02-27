package com.findmycoach.app.fragment_mentee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.student.R;
import com.findmycoach.student.activity.EditProfileActivity;
import com.findmycoach.student.beans.profile.Data;
import com.findmycoach.student.beans.profile.ProfileResponse;
import com.findmycoach.student.util.Callback;
import com.findmycoach.student.util.NetworkClient;
import com.findmycoach.student.util.NetworkManager;
import com.findmycoach.student.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.util.List;

public class ProfileFragment extends Fragment implements Callback {

    private final int REQUEST_CODE = 102;
    private ProgressDialog progressDialog;
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileAddress;
    private TextView profileLocation;
    private TextView trainingLocation;
    private TextView mentorFor;
    private TextView coachingType;
    private ListView areaOfInterest;
    private com.findmycoach.student.beans.profile.Data userInfo = null;

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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
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
        NetworkClient.getProfile(getActivity(), requestParams, authToken, this);
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
        areaOfInterest = (ListView) view.findViewById(R.id.areas_of_interest);
        profileLocation = (TextView) view.findViewById(R.id.profile_location);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_management, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_profile) {
            if (userInfo != null) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("user_info", new Gson().toJson(userInfo));
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
        return super.onOptionsItemSelected(item);
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
    public void successOperation(Object object) {
        progressDialog.hide();
        ProfileResponse response = (ProfileResponse) object;
        userInfo = response.getData();
        populateFields();
    }

    private void populateFields() {
        profileName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
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
        profileLocation.setText(NetworkManager.getCurrentLocation(getActivity()));
        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
            PicassoTools.clearCache(Picasso.with(getActivity()));
            Picasso.with(getActivity())
                    .load((String) userInfo.getPhotograph()).skipMemoryCache()
                    .into(profileImage);
        }
        mentorFor.setText(userInfo.getMentorFor());
        trainingLocation.setText((String) userInfo.getTrainingLocation());
        coachingType.setText((String) userInfo.getCoachingType());
        List<String> list = userInfo.getSubCategoryName();
        if(list.get(0)!=null && !list.get(0).equals(" "))
            areaOfInterest.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list));
        else
            areaOfInterest.setAdapter(null);
    }


    @Override
    public void failureOperation(Object object) {
        progressDialog.hide();
        String message = (String) object;
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
