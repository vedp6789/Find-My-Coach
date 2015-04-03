package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.mentor.Data;
import com.findmycoach.app.beans.mentor.Response;
import com.findmycoach.app.load_image_from_url.ImageLoader;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MentorDetailsActivity extends Activity implements Callback,Button.OnClickListener{

    private ImageView profileImage;
    private TextView profileName;
    private TextView profileAddress;
    private TextView profileRatting;
    private TextView profileCharges;
    private TextView profileTravelAvailable;
    private TextView profilePhone;
    private TextView areaOfCoaching;
    private Button googleLink;
    private Button facebookLink;
    private Data userInfo = null;
    private ProgressDialog progressDialog;
    private TextView tv_mon_slots,tv_tue_slots,tv_wed_slots,tv_thur_slots,tv_fri_slots,tv_sat_slots,tv_sun_slots;
    private Button b_schedule_class;
    private String connectionStatus;

    JSONObject jsonObject,jsonObject_Data,jsonObject_slots;
    JSONArray jsonArray_sub_category, jArray_mon_slots, jArray_tue_slots, jArray_wed_slots, jArray_thu_slots, jArray_fri_slots, jArray_sat_slots, jArray_sun_slots;



    private static final String TAG="FMC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_details);

        try {
            jsonObject=new JSONObject(getIntent().getStringExtra("mentorDetails"));
            jsonObject_Data=jsonObject.getJSONObject("data");
            jsonArray_sub_category=jsonObject_Data.getJSONArray("sub_category_name");
            jsonObject_slots=jsonObject.getJSONObject("freeSlots");

            jArray_mon_slots = jsonObject_slots.getJSONArray("M");
            jArray_tue_slots = jsonObject_slots.getJSONArray("T");
            jArray_wed_slots = jsonObject_slots.getJSONArray("W");
            jArray_thu_slots = jsonObject_slots.getJSONArray("Th");
            jArray_fri_slots = jsonObject_slots.getJSONArray("F");
            jArray_sat_slots = jsonObject_slots.getJSONArray("S");
            jArray_sun_slots = jsonObject_slots.getJSONArray("Su");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initialize();
        Log.d(TAG, connectionStatus);
        applyActionbarProperties();
        populateFields();
        populateDaysSlotsListView();
    }

    private void populateDaysSlotsListView(){
        ArrayList<String> mon_slots=new ArrayList<String>();
        for(int i=0;i<jArray_mon_slots.length();i++){
            try {
                JSONArray jsonArray=jArray_mon_slots.getJSONArray(i);
                String start_time=jsonArray.getString(0);
                String stop_time=jsonArray.getString(1);
                mon_slots.add(start_time.split(":")[0]+":"+start_time.split(":")[1]+" - "+stop_time.split(":")[0]+":"+stop_time.split(":")[1]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> tue_slots=new ArrayList<String>();
        for(int i=0;i<jArray_tue_slots.length();i++){
            try {
                JSONArray jsonArray=jArray_tue_slots.getJSONArray(i);
                String start_time=jsonArray.getString(0);
                String stop_time=jsonArray.getString(1);
               tue_slots.add(start_time.split(":")[0]+":"+start_time.split(":")[1]+" - "+stop_time.split(":")[0]+":"+stop_time.split(":")[1]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> wed_slots=new ArrayList<String>();
        for(int i=0;i<jArray_wed_slots.length();i++){
            try {
                JSONArray jsonArray=jArray_wed_slots.getJSONArray(i);
                String start_time=jsonArray.getString(0);
                String stop_time=jsonArray.getString(1);
                wed_slots.add(start_time.split(":")[0]+":"+start_time.split(":")[1]+" - "+stop_time.split(":")[0]+":"+stop_time.split(":")[1]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> thur_slots=new ArrayList<String>();
        for(int i=0;i<jArray_thu_slots.length();i++){
            try {
                JSONArray jsonArray=jArray_thu_slots.getJSONArray(i);
                String start_time=jsonArray.getString(0);
                String stop_time=jsonArray.getString(1);
                thur_slots.add(start_time.split(":")[0]+":"+start_time.split(":")[1]+" - "+stop_time.split(":")[0]+":"+stop_time.split(":")[1]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> fri_slots=new ArrayList<String>();
        for(int i=0;i<jArray_fri_slots.length();i++){
            try {
                JSONArray jsonArray=jArray_fri_slots.getJSONArray(i);
                String start_time=jsonArray.getString(0);
                String stop_time=jsonArray.getString(1);
                fri_slots.add(start_time.split(":")[0]+":"+start_time.split(":")[1]+" - "+stop_time.split(":")[0]+":"+stop_time.split(":")[1]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> sat_slots=new ArrayList<String>();
        for(int i=0;i<jArray_sat_slots.length();i++){
            try {
                JSONArray jsonArray=jArray_sat_slots.getJSONArray(i);
                String start_time=jsonArray.getString(0);
                String stop_time=jsonArray.getString(1);
                sat_slots.add(start_time.split(":")[0]+":"+start_time.split(":")[1]+" - "+stop_time.split(":")[0]+":"+stop_time.split(":")[1]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> sun_slots=new ArrayList<String>();
        for(int i=0;i<jArray_sun_slots.length();i++){
            try {
                JSONArray jsonArray=jArray_sun_slots.getJSONArray(i);
                String start_time=jsonArray.getString(0);
                String stop_time=jsonArray.getString(1);
                sun_slots.add(start_time.split(":")[0]+":"+start_time.split(":")[1]+" - "+stop_time.split(":")[0]+":"+stop_time.split(":")[1]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try{
            Log.d(TAG,"Mon slots size"+mon_slots.size()+" sample of data in mon_slots :"+mon_slots.get(0));
        }catch (Exception e){
            e.printStackTrace();
        }
        StringBuilder stringBuilder0=new StringBuilder();
        StringBuilder stringBuilder1=new StringBuilder();
        StringBuilder stringBuilder2=new StringBuilder();
        StringBuilder stringBuilder3=new StringBuilder();
        StringBuilder stringBuilder4=new StringBuilder();
        StringBuilder stringBuilder5=new StringBuilder();
        StringBuilder stringBuilder6=new StringBuilder();

        if(mon_slots.size() <=0){
            tv_mon_slots.setText(getResources().getString(R.string.not_free));
        }
        if(tue_slots.size() <=0){
            tv_tue_slots.setText(getResources().getString(R.string.not_free));
        }
        if(wed_slots.size() <=0){
            tv_wed_slots.setText(getResources().getString(R.string.not_free));
        }
        if(thur_slots.size() <=0){
            tv_thur_slots.setText(getResources().getString(R.string.not_free));
        }
        if(fri_slots.size() <=0){
            tv_fri_slots.setText(getResources().getString(R.string.not_free));
        }if(sat_slots.size() <=0){
            tv_sat_slots.setText(getResources().getString(R.string.not_free));
        }
        if(sun_slots.size() <=0){
            tv_sun_slots.setText(getResources().getString(R.string.not_free));
        }



        for (int j=0;j<mon_slots.size();j++){
            Log.d(TAG,"Mon slots size"+mon_slots.size()+" sample of data in mon_slots :"+mon_slots.get(j));
            if(j==0){
                stringBuilder0.append(mon_slots.get(j));
            }else{
                stringBuilder0.append(", "+mon_slots.get(j));
            }
            tv_mon_slots.setText(stringBuilder0.toString());
        }
        for (int j=0;j<tue_slots.size();j++){
            Log.d(TAG,"Tue slots size"+tue_slots.size()+" sample of data in tue_slots :"+tue_slots.get(j));
            if(j==0){
                stringBuilder1.append(tue_slots.get(j));
            }else{
                stringBuilder1.append(", "+tue_slots.get(j));
            }
            tv_tue_slots.setText(stringBuilder1.toString());
        }
        for (int j=0;j<wed_slots.size();j++){
            Log.d(TAG,"Wed slots size"+wed_slots.size()+" sample of data in wed_slots :"+wed_slots.get(j));
            if(j==0){
                stringBuilder2.append(wed_slots.get(j));
            }else{
                stringBuilder2.append(", "+wed_slots.get(j));
            }
            tv_wed_slots.setText(stringBuilder2.toString());
        }for (int j=0;j<thur_slots.size();j++){
            Log.d(TAG,"Thur slots size"+thur_slots.size()+" sample of data in thur_slots :"+thur_slots.get(j));
            if(j==0){
                stringBuilder3.append(thur_slots.get(j));
            }else{
                stringBuilder3.append(", "+thur_slots.get(j));
            }
            tv_thur_slots.setText(stringBuilder3.toString());
        }for (int j=0;j<fri_slots.size();j++){
            Log.d(TAG,"Fri slots size"+fri_slots.size()+" sample of data in fri_slots :"+fri_slots.get(j));
            if(j==0){
                stringBuilder4.append(fri_slots.get(j));
            }else{
                stringBuilder4.append(", "+fri_slots.get(j));
            }
            tv_fri_slots.setText(stringBuilder4.toString());
        }for (int j=0;j<sat_slots.size();j++){
            Log.d(TAG,"Sat slots size"+sat_slots.size()+" sample of data in sat_slots :"+sat_slots.get(j));
            if(j==0){
                stringBuilder5.append(sat_slots.get(j));
            }else{
                stringBuilder5.append(", "+sat_slots.get(j));
            }
            tv_sat_slots.setText(stringBuilder5.toString());
        }
        for (int j=0;j<sun_slots.size();j++){

            if(j==0){
                stringBuilder6.append(sun_slots.get(j));
            }else{
                stringBuilder6.append(", "+sun_slots.get(j));
            }
            tv_sun_slots.setText(stringBuilder6.toString());

        }
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
        Response mentorDetails = new Gson().fromJson(jsonData, Response.class);
        userInfo = mentorDetails.getData();
        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileName = (TextView) findViewById(R.id.profile_name);
        profileAddress = (TextView) findViewById(R.id.profile_address);
        profileRatting = (TextView) findViewById(R.id.profile_rating);
        profileCharges = (TextView) findViewById(R.id.profile_charges);
        profileTravelAvailable = (TextView) findViewById(R.id.profile_travel_available);
        areaOfCoaching = (TextView) findViewById(R.id.areas_of_coaching);
        googleLink = (Button) findViewById(R.id.profile_google_button);
        facebookLink = (Button) findViewById(R.id.profile_facebook_button);
        profilePhone = (TextView) findViewById(R.id.profile_phone);

        tv_mon_slots= (TextView) findViewById(R.id.tv_mon_available_slots);
        tv_tue_slots= (TextView) findViewById(R.id.tv_tue_available_slots);
        tv_wed_slots= (TextView) findViewById(R.id.tv_wed_available_slots);
        tv_thur_slots= (TextView) findViewById(R.id.tv_thur_available_slots);
        tv_fri_slots= (TextView) findViewById(R.id.tv_fri_available_slots);
        tv_sat_slots= (TextView) findViewById(R.id.tv_sat_available_slots);
        tv_sun_slots= (TextView) findViewById(R.id.tv_sun_available_slots);

        b_schedule_class= (Button) findViewById(R.id.b_schedule_class);
        b_schedule_class.setOnClickListener(this);

        Log.d(TAG,"sub category length : "+jsonArray_sub_category.length()+"");
        if(jsonArray_sub_category.length() > 0){
            Log.i(TAG,"sub_category size not null");
        }
        if(jsonArray_sub_category.length() <= 0){
            Log.i(TAG,"sub_category size is null");
        }

        if(connectionStatus.equals("not connected") || connectionStatus.equals("pending") || jsonArray_sub_category.length() <= 0 )
            b_schedule_class.setVisibility(View.GONE);


    }

    private boolean noSlotsAvailable(){
        if(jArray_mon_slots.length() <= 0){
            if(jArray_tue_slots.length() <= 0){
                if(jArray_wed_slots.length() <= 0){
                    if(jArray_thu_slots.length() <= 0){
                        if(jArray_fri_slots.length() <= 0){
                            if(jArray_fri_slots.length() <= 0){
                                if(jArray_sat_slots.length() <= 0){
                                    if(jArray_sun_slots.length() <= 0){
                                        return true;
                                    }else{
                                        return false;
                                    }
                                }else{
                                    return false;
                                }
                            }else{
                                return false;
                            }
                        }else{
                            return false;
                        }
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
        //return jArray_mon_slots.length() <= 0 && jArray_tue_slots.length() <= 0 && jArray_wed_slots.length() <= 0 && jArray_thu_slots.length() <= 0 && jArray_fri_slots.length() <= 0 && jArray_sat_slots.length() <= 0 && jArray_sun_slots.length() <= 0;
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
            ImageLoader imgLoader = new ImageLoader(profileImage);
            imgLoader.execute((String) userInfo.getPhotograph());
        }

        List<String> areaOfInterests = userInfo.getSubCategoryName();
        if (areaOfInterests.size() > 0 && areaOfInterests.get(0)!=null && !areaOfInterests.get(0).trim().equals("")) {
            String areaOfInterest = "";
            for (int index = 0; index < areaOfInterests.size(); index++) {
                if (index != 0) {
                    areaOfInterest = areaOfInterest + ", " + areaOfInterests.get(index);
                } else {
                    areaOfInterest = areaOfInterest + areaOfInterests.get(index);
                }
            }
            areaOfCoaching.setText(areaOfInterest);
        }else{
            areaOfCoaching.setText("");
        }
        profilePhone.setText(userInfo.getPhonenumber());
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
            menu.add(0, Menu.FIRST, Menu.NONE, R.string.rate);
        }if(connectionStatus.equals("pending")) {
            getMenuInflater().inflate(R.menu.menu_mentor_details_pending, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG,userInfo.getConnectionStatus() + " : " + userInfo.getConnectionId());
        if (id == R.id.action_connect) {
            showAlert();
        }
        else if (id == R.id.action_disconnect) {
            disconnect(userInfo.getConnectionId(), userInfo.getId());
        }
        if (id == android.R.id.home) {
            finish();
        }
        if(id == Menu.FIRST){
            showRatingDialog();
        }
        return true;
    }

    private void disconnect(String connectionId, String oppositeUSerId) {
        progressDialog.show();
        Log.d(TAG, "id : " + connectionId + ", user_id : " + oppositeUSerId +
                ", user_group : " + DashboardActivity.dashboardActivity.user_group);
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", connectionId);
        requestParams.add("user_id", oppositeUSerId);
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.breakConnection(this, requestParams, this, 21);
    }

    /** Dialog to rate mentor */
    private void showRatingDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(getResources().getString(R.string.rate) + " " + userInfo.getFirstName());
        dialog.setContentView(R.layout.dialog_rate_mentor);

        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);

        dialog.findViewById(R.id.submitRating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MentorDetailsActivity.this,getResources().getString(R.string.rating_for) + userInfo.getFirstName() + getResources().getString(R.string.is) + ratingBar.getRating() +getResources().getString(R.string.will_be_submitted), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dialog.show();
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
        if(calledApiValue == 21 || calledApiValue == 17){
            Intent intent = new Intent();
            intent.putExtra("status", "close_activity");
            String status = object+"";
            try{
                intent.putExtra("connectionId", Integer.parseInt(status)+"");
            }catch (Exception e){
                e.printStackTrace();
            }
            intent.putExtra("connectionStatus", calledApiValue == 17 ? "pending" : "broken");
            setResult(RESULT_OK, intent);
            finish();
        }
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
                  intent.putExtra("mentor_details",jsonObject.toString());
                  startActivity(intent);
            }
        }
    }

    /** validate method will check whether the mentor have any free slot for class or not */
    private boolean validate() {
        boolean b_allow_schedule_creation=true;
        return b_allow_schedule_creation;

    }
}
