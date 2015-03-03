package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.NotificationAdapter;
import com.findmycoach.app.beans.student.Data;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.NetworkManager;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.util.List;

/**
 * Created by prem on 25/2/15.
 */
public class StudentDetailActivity  extends Activity implements Callback {

    private ImageView profileImage;
    private TextView profileName;
    private TextView profileAddress;
    private TextView profileLocation;
    private TextView trainingLocation;
    private TextView mentorFor;
    private TextView coachingType;
    private ListView areaOfInterest;
    private Data studentDetails;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        initialize();
        getDataFromIntent();
    }

    private void getDataFromIntent() {
        try{
            String response = getIntent().getStringExtra("student_detail");
            ProfileResponse profileResponse = new Gson().fromJson(response, ProfileResponse.class);
            studentDetails = profileResponse.getData();
            if(studentDetails != null) {
                applyActionbarProperties();
                populateFields();
            }
        }catch (Exception e){
            finish();
            Toast.makeText(this, getResources().getString(R.string.problem_in_connection_server),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void applyActionbarProperties() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(studentDetails.getFirstName());
        }
    }

    private void initialize() {
        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileName = (TextView) findViewById(R.id.profile_name);
        profileAddress = (TextView) findViewById(R.id.profile_address);
        trainingLocation = (TextView) findViewById(R.id.training_location);
        mentorFor = (TextView) findViewById(R.id.mentor_for);
        coachingType = (TextView) findViewById(R.id.coaching_type);
        areaOfInterest = (ListView) findViewById(R.id.areas_of_interest);
        profileLocation = (TextView) findViewById(R.id.profile_location);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
    }

    private void populateFields() {
        profileName.setText(studentDetails.getFirstName() + " " + studentDetails.getLastName());
        String address = "";
        if (studentDetails.getAddress() != null) {
            address = address + studentDetails.getAddress() + ", ";
        }
        if (studentDetails.getCity() != null) {
            address = address + studentDetails.getCity() + ", ";
        }
        if (studentDetails.getState() != null) {
            address = address + studentDetails.getState() + ", ";
        }
        if (studentDetails.getZip() != null) {
            address = address + studentDetails.getZip();
        }
        profileAddress.setText(address);
        profileLocation.setText(NetworkManager.getCurrentLocation(this));
        if (studentDetails.getPhotograph() != null && !studentDetails.getPhotograph().equals("")) {
            PicassoTools.clearCache(Picasso.with(this));
            Picasso.with(this)
                    .load((String) studentDetails.getPhotograph()).skipMemoryCache()
                    .into(profileImage);
        }
        mentorFor.setText(studentDetails.getMentorFor());
        trainingLocation.setText((String) studentDetails.getTrainingLocation());
        coachingType.setText((String) studentDetails.getCoachingType());
        List<String> list = studentDetails.getSubCategoryName();
        if(list.get(0)!=null && !list.get(0).equals(" "))
            areaOfInterest.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
        else
            areaOfInterest.setAdapter(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.accept) {
            respondToRequest("accepted");
        }else if (id == R.id.decline) {
            respondToRequest("rejected");
        }else if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void respondToRequest(String response) {
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", NotificationAdapter.connection_id + "");
        requestParams.add("status", response);
        Log.d("FMC", NotificationAdapter.connection_id + "");
        Log.d("FMC", response);
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.respondToConnectionRequest(this, requestParams, this, 18);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        if(object == null) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
        Toast.makeText(this, getResources().getString(R.string.success), Toast.LENGTH_LONG).show();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
    }
}
