package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.MentorListAdapter;
import com.findmycoach.app.beans.search.Datum;
import com.findmycoach.app.beans.search.SearchResponse;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.List;

public class UserListActivity extends Activity implements Callback {

    private ListView listView;
    private List<Datum> users;
    private ProgressDialog progressDialog;
    private Datum datum;
    private boolean isGettingMentor = false;
    private static final String TAG="FMC";
    private static final int NEED_TO_REFRESH = 100;
    private MentorListAdapter mentorListAdapter;
    private int selectedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        applyActionbarProperties();
        initialize();
        applyActions();
    }

    private void applyActions() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (users != null) {
                    Log.d(TAG,"ListView click");
                    selectedPosition = position;
                    datum = users.get(position);
                    getMentorDetails(datum.getId());
                }
            }
        });
    }

    private void getMentorDetails(String id) {
        if(isGettingMentor){
            Toast.makeText(this, getResources().getString(R.string.get_mentor_is_already_called),Toast.LENGTH_SHORT).show();
            return;
        }
        isGettingMentor = true;
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", id);
        requestParams.add("owner_id",StorageHelper.getUserDetails(this,"user_id"));
        int limit = 7;                                          //  This is a limit for getting free slots details for this mentor in terms of limit days from current date
        requestParams.add("limit", String.valueOf(limit));
        String authToken = StorageHelper.getUserDetails(this, "auth_token");
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.getMentorDetails(this, requestParams, authToken, this, 24);
    }

    private void applyActionbarProperties() {
        ActionBar actionbar = getActionBar();
        if(actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void initialize() {
        listView = (ListView) findViewById(R.id.user_list);
        String json = getIntent().getStringExtra("list");
        Log.d(TAG, "Intent String:" + json);
        SearchResponse searchResponse = new Gson().fromJson(json, SearchResponse.class);
        users = searchResponse.getData();
        Log.d(TAG, "Users:" + users);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        mentorListAdapter = new MentorListAdapter(this, users, progressDialog);
        listView.setAdapter(mentorListAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == NEED_TO_REFRESH){
            if(users != null && selectedPosition != -1){
                try{
                    users.get(selectedPosition).setConnectionId(data.getStringExtra("connectionId"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                users.get(selectedPosition).setConnectionStatus(data.getStringExtra("connectionStatus"));
                mentorListAdapter.notifyDataSetChanged();
                datum = null;
            }
        }
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        // For displaying selected Mentor details
        if(calledApiValue == 24){
            Intent intent = new Intent(getApplicationContext(), MentorDetailsActivity.class);
            intent.putExtra("mentorDetails", (String) object);
            intent.putExtra("connection_status", datum.getConnectionStatus());
            datum = null;
            startActivityForResult(intent, NEED_TO_REFRESH);
            isGettingMentor = false;
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        isGettingMentor = false;
        Toast.makeText(getApplicationContext(), (String) object, Toast.LENGTH_LONG).show();
    }

}
