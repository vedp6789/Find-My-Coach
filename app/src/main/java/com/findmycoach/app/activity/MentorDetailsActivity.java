package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.mentor.Data;
import com.findmycoach.app.beans.mentor.Response;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

public class MentorDetailsActivity extends Activity implements Callback {

    private Response mentorDetails;
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
    private ProgressDialog progressDialog;

    private static final String TAG="FMC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_mentor);
        initialize();
        applyActionbarProperties();
        populateFields();
    }

    private void applyActionbarProperties() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(userInfo.getFirstName());
        }
    }

    private void initialize() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        String jsonData = getIntent().getStringExtra("mentorDetails");
        Log.d(TAG, jsonData);
        mentorDetails = new Gson().fromJson(jsonData, Response.class);
        userInfo = mentorDetails.getData();
        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileName = (TextView) findViewById(R.id.profile_name);
        profileAddress = (TextView) findViewById(R.id.profile_address);
        profileRatting = (TextView) findViewById(R.id.profile_rating);
        profileProfession = (TextView) findViewById(R.id.profile_profession);
        profileAccomplishment = (TextView) findViewById(R.id.profile_accomplishment);
        profileCharges = (TextView) findViewById(R.id.profile_charges);
        profileTravelAvailable = (TextView) findViewById(R.id.profile_travel_available);
        profileLocation = (TextView) findViewById(R.id.profile_location);
        googleLink = (Button) findViewById(R.id.profile_google_button);
        facebookLink = (Button) findViewById(R.id.profile_facebook_button);
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
        if (userInfo.getCharges() != null) {
            profileCharges.setText("\u20B9 " + userInfo.getCharges());
        }
        profileRatting.setText(userInfo.getRating());
        profileLocation.setText(NetworkManager.getCurrentLocation(this));
        if (userInfo.getAvailabilityYn() != null && userInfo.getAvailabilityYn().equals("1")) {
            profileTravelAvailable.setText(getResources().getString(R.string.yes));
        } else {
            profileTravelAvailable.setText(getResources().getString(R.string.no));
        }
        if (userInfo.getPhotograph() != null && !userInfo.getPhotograph().equals("")) {
            Picasso.with(this)
                    .load((String) userInfo.getPhotograph()).resize(150, 150)
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.user_icon)
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mentor_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_connect) {
            showAlert();
            return true;
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlert() {
        final String defaultMessage =  getResources().getString(R.string.connection_request_msg);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.connection_request));
        alertDialog.setMessage(getResources().getString(R.string.enter_msg));
        final EditText input = new EditText(this);
        input.setHint(defaultMessage);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(8, 8, 8, 8);
        input.setLayoutParams(params);
        alertDialog.setView(input);
        input.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittext));
        alertDialog.setPositiveButton(getResources().getString(R.string.send),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String message = input.getText().toString();
                        if(message.trim().length() < 1)
                            message = defaultMessage;
                        sendConnectionRequest(message);
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

    private void sendConnectionRequest(String message) {
        progressDialog.show();
        String studentId = StorageHelper.getUserDetails(this, getResources().getString(R.string.user_id));
        Log.d(TAG, "\n" + message + "\nMentor id : " + userInfo.getId() + "\nStudent id : " + studentId);
        RequestParams requestParams = new RequestParams();
        requestParams.add("owner", studentId);
        requestParams.add("invitee", userInfo.getId());
        requestParams.add("message", message);
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.sendConnectionRequest(this, requestParams, this, 17);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), (String) object, Toast.LENGTH_LONG).show();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), (String) object, Toast.LENGTH_LONG).show();
    }
}
