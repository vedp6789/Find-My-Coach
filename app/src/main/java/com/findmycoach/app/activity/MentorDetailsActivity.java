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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.mentor.Data;
import com.findmycoach.app.beans.mentor.Response;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

public class MentorDetailsActivity extends Activity implements Callback,Button.OnClickListener{

    private Response mentorDetails;
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileAddress;
    private TextView profileRatting;
    private TextView profileProfession;
    private TextView profileAccomplishment;
    private TextView profileCharges;
    private TextView profileTravelAvailable;
    private Button googleLink;
    private Button facebookLink;
    private Data userInfo = null;
    private ProgressDialog progressDialog;
    private ListView listView_mon_slots,listView_tue_slots,listView_wed_slots,listView_thur_slots,listView_fri_slots,listView_sat_slots,listView_sun_slots;
    private Button b_schedule_class;
    private String connectionStatus;


    private static final String TAG="FMC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_details);
        initialize();
        Log.d(TAG, connectionStatus);
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
        connectionStatus = getIntent().getStringExtra("connection_status");
        if(connectionStatus == null)
            connectionStatus = "not connected";
        if(connectionStatus.equals("broken"))
            connectionStatus = "not connected";
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
        googleLink = (Button) findViewById(R.id.profile_google_button);
        facebookLink = (Button) findViewById(R.id.profile_facebook_button);

        listView_mon_slots= (ListView) findViewById(R.id.lv_mon_available_slots);
        listView_tue_slots= (ListView) findViewById(R.id.lv_tue_available_slots);
        listView_wed_slots= (ListView) findViewById(R.id.lv_wed_available_slots);
        listView_thur_slots= (ListView) findViewById(R.id.lv_thur_available_slots);
        listView_fri_slots= (ListView) findViewById(R.id.lv_fri_available_slots);
        listView_sat_slots= (ListView) findViewById(R.id.lv_sat_available_slots);
        listView_sun_slots= (ListView) findViewById(R.id.lv_sun_available_slots);

        b_schedule_class= (Button) findViewById(R.id.b_schedule_class);
        if(connectionStatus.equals("not connected") || connectionStatus.equals("pending"))
            b_schedule_class.setVisibility(View.GONE);
        b_schedule_class.setOnClickListener(this);
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
            googleLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(userInfo.getGoogleLink()));
                    try{
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MentorDetailsActivity.this,getResources().getString(R.string.problem_in_url),Toast.LENGTH_LONG).show();
                    }
                }
            });

            facebookLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(userInfo.getFacebookLink()));
                    try{
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MentorDetailsActivity.this,getResources().getString(R.string.problem_in_url),Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(connectionStatus.equals("not connected")) {
            getMenuInflater().inflate(R.menu.menu_mentor_details_not_connected, menu);
        }else if(connectionStatus.equals("accepted")) {
            getMenuInflater().inflate(R.menu.menu_connected, menu);
        }if(connectionStatus.equals("pending")) {
            getMenuInflater().inflate(R.menu.menu_mentor_details_pending, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_connect) {
            showAlert();
            return true;
        }
        if (id == R.id.action_disconnect) {
            Toast.makeText(this,"Connection will disconnect",Toast.LENGTH_LONG).show();
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
        alertDialog.setCancelable(false);
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

    @Override
    public void onClick(View v) {
        if(v == b_schedule_class){

            if(validate()){
                Log.d(TAG,"Start Scheduling activity");
                  Intent intent=new Intent(MentorDetailsActivity.this, ScheduleNewClass.class);
                  intent.putExtra("fname",userInfo.getFirstName());
                  startActivity(intent);
            }
        }
    }


    /* validate method will check whether the mentor have any free slot for class or not */
    private boolean validate() {
        boolean b_allow_schedule_creation=true;
        return b_allow_schedule_creation;

    }
}
