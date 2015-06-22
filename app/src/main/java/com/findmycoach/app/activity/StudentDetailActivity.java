package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.NotificationAdapter;
import com.findmycoach.app.beans.student.Data;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

/**
 * Created by prem on 25/2/15.
 */
public class StudentDetailActivity extends Activity implements Callback {

    private ImageView profileImage;
    private TextView profileName;
    private TextView profileAddress;
    private TextView trainingLocation;
    private TextView email;
    private TextView dob;
    private TextView mentorFor;
    private TextView coachingType;
    private TextView profilePhone;
    private Data studentDetails;
    private ProgressDialog progressDialog;
    private static final String TAG = "FMC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        initialize();
        getDataFromIntent();
    }

    private void getDataFromIntent() {
        try {
            String response = getIntent().getStringExtra("student_detail");
            ProfileResponse profileResponse = new Gson().fromJson(response, ProfileResponse.class);
            studentDetails = profileResponse.getData();
            if (studentDetails != null) {
                populateFields();
            }
        } catch (Exception e) {
            finish();
            Toast.makeText(this, getResources().getString(R.string.problem_in_connection_server), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void initialize() {
        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileName = (TextView) findViewById(R.id.profile_name);
        profileAddress = (TextView) findViewById(R.id.profile_address);
        trainingLocation = (TextView) findViewById(R.id.training_location);
        mentorFor = (TextView) findViewById(R.id.mentor_for);
        coachingType = (TextView) findViewById(R.id.coaching_type);
        profilePhone = (TextView) findViewById(R.id.profile_phone);
        email = (TextView) findViewById(R.id.profile_email);
        dob = (TextView) findViewById(R.id.profile_dob);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.title_student_details));

    }

    private void populateFields() {
        profileName.setText(studentDetails.getFirstName() + " " + studentDetails.getLastName());
        try {
            email.setText(studentDetails.getEmail());
        } catch (Exception e) {
        }
        try {
            dob.setText((String) studentDetails.getDob());
        } catch (Exception e) {
        }
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
        if (studentDetails.getPhotograph() != null && !studentDetails.getPhotograph().equals("")) {
            ImageLoader imgLoader = new ImageLoader(profileImage);
            imgLoader.execute((String) studentDetails.getPhotograph());
        }
        mentorFor.setText(studentDetails.getMentorFor());
        trainingLocation.setText((String) studentDetails.getTrainingLocation());
        coachingType.setText((String) studentDetails.getCoachingType());
        profilePhone.setText(studentDetails.getPhonenumber());

        ImageView connectionButton = (ImageView) findViewById(R.id.menuItem);

//        studentDetails.setConnectionStatus(getIntent().getStringExtra("connection_status"));
        if (studentDetails.getConnectionStatus() == null || studentDetails.getConnectionStatus().equals("broken"))
            studentDetails.setConnectionStatus("not connected");

        if (studentDetails.getConnectionStatus().equals("accepted")) {
            connectionButton.setImageDrawable(getResources().getDrawable(R.drawable.disconnect_white));
            connectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDisconnectDialog(studentDetails.getConnectionId(), studentDetails.getId());
                }
            });
        } else if (studentDetails.getConnectionStatus().equals("pending")) {
            connectionButton.setImageDrawable(getResources().getDrawable(R.drawable.pending_white));
            connectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    respondToRequest("rejected");
                }
            });
        }
    }

    private void showDisconnectDialog(final String connectionId, final String id) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.disconnect_confirmation_dialog);

        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect(connectionId, id);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void disconnect(String connectionId, String oppositeUSerId) {
        progressDialog.show();
        Log.d(TAG, "id : " + connectionId + ", user_id : " + oppositeUSerId +
                ", user_group : " + DashboardActivity.dashboardActivity.user_group);
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", connectionId);
        requestParams.add("user_id", oppositeUSerId);
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.breakConnection(this, requestParams, this, 21);
    }

    private void respondToRequest(String response) {
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", NotificationAdapter.connection_id + "");
        requestParams.add("status", response);
        NetworkClient.respondToConnectionRequest(this, requestParams, this, 18);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        if (calledApiValue == 21 || calledApiValue == 18) {
            Intent intent = new Intent();
            intent.putExtra("status", "close_activity");
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
