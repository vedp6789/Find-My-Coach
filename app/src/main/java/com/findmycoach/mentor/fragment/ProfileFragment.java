package com.findmycoach.mentor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.mentor.activity.EditProfileActivity;
import com.findmycoach.mentor.beans.authentication.Data;
import com.findmycoach.mentor.beans.authentication.Response;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.findmycoach.mentor.util.NetworkManager;
import com.findmycoach.mentor.util.StorageHelper;
import com.fmc.mentor.findmycoach.R;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements Callback {

    private final int REQUEST_CODE = 102;
    private ProgressDialog progressDialog;
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileAddress;
    private TextView profileRatting;
    private TextView profileProfession;
    private TextView profileAccomplishment;
    private TextView profileCharges;
    private TextView profileTravelAvailable;
    private TextView profileLocation;
    private Button googleLink;
    private Button facebookLink;
    private Data userInfo = null;

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
        Log.d("FMC:", "Stored User Id:" + StorageHelper.getUserDetails(getActivity(), "user_id"));
        requestParams.add("id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        NetworkClient.getProfile(getActivity(), requestParams, authToken, this);
    }

    private void initialize(View view) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        profileImage = (ImageView) view.findViewById(R.id.profile_image);
        profileName = (TextView) view.findViewById(R.id.profile_name);
        profileAddress = (TextView) view.findViewById(R.id.profile_address);
        profileRatting = (TextView) view.findViewById(R.id.profile_rating);
        profileProfession = (TextView) view.findViewById(R.id.profile_profession);
        profileAccomplishment = (TextView) view.findViewById(R.id.profile_accomplishment);
        profileCharges = (TextView) view.findViewById(R.id.profile_charges);
        profileTravelAvailable = (TextView) view.findViewById(R.id.profile_travel_available);
        profileLocation = (TextView) view.findViewById(R.id.profile_location);
        googleLink = (Button) view.findViewById(R.id.profile_google_button);
        facebookLink = (Button) view.findViewById(R.id.profile_facebook_button);
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
        Log.d("FMC:", "IN onActivity Result" + requestCode + "   " + requestCode);
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
        Response response = (Response) object;
        userInfo = response.getData();
        populateFields();
    }

    private void populateFields() {
        profileName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
        profileAddress.setText(userInfo.getAddress() + ", " + userInfo.getCity() + ", " + userInfo.getState() + ", " + userInfo.getZip());
        profileCharges.setText(userInfo.getCharges());
        profileRatting.setText(userInfo.getRating());
        profileLocation.setText(NetworkManager.getCurrentLocation(getActivity()));
        if (userInfo.getAvailabilityYn() != null && userInfo.getAvailabilityYn().equals("1")) {
            profileTravelAvailable.setText("YES");
        } else {
            profileTravelAvailable.setText("No");
        }
        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
            Picasso.with(getActivity())
                    .load((String) userInfo.getPhotograph())
                    .into(profileImage);
        }
        applySocialLinks();
    }

    private void applySocialLinks() {
        if (userInfo.getGoogleLink() != null && !userInfo.getGoogleLink().equals("")) {
            googleLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(userInfo.getGoogleLink()));
                    startActivity(intent);
                }
            });
        }
        if (userInfo.getFacebookLink() != null && !userInfo.getFacebookLink().equals("")) {
            facebookLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(userInfo.getFacebookLink()));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void failureOperation(Object object) {
        progressDialog.hide();
        String message = (String) object;
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
