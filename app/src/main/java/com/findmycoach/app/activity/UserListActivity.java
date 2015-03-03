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

    private static final String TAG="FMC";


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
                    Datum datum = users.get(position);
                    getMentorDetails(datum.getId());
                }
            }
        });
    }

    private void getMentorDetails(String id) {
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", id);
        String authToken = StorageHelper.getUserDetails(this, "auth_token");
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.getMentorDetails(this, requestParams, authToken, this, 25);
    }

    private void applyActionbarProperties() {
        ActionBar actionbar = getActionBar();
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
        listView.setAdapter(new MentorListAdapter(this, users, this, progressDialog));
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
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        // If for showing connection request response. Else for displaying selected Mentor details
        if(calledApiValue == 17){
            Toast.makeText(this,(String) object, Toast.LENGTH_LONG).show();
        }else if(calledApiValue == 25){
            Intent intent = new Intent(getApplicationContext(), MentorDetailsActivity.class);
            intent.putExtra("mentorDetails", (String) object);
            startActivity(intent);
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), (String) object, Toast.LENGTH_LONG).show();
    }

}
