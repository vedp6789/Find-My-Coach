package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
    private static final String TAG = "FMC";
    private static final int NEED_TO_REFRESH = 100;
    private MentorListAdapter mentorListAdapter;
    private int selectedPosition = -1;
    private String connection_status_for_Selected_mentor;
    private String searchFor;
    private ImageView menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        initialize();
        applyActions();
    }

    private void applyActions() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connection_status_for_Selected_mentor = null;
                if (users != null) {
                    Log.d(TAG, "ListView click");
                    selectedPosition = position;
                    datum = users.get(position);
                    connection_status_for_Selected_mentor = datum.getConnectionStatus();
                    getMentorDetails(datum.getId());
                }
            }
        });

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getMentorDetails(String id) {
        if (isGettingMentor) {
            Toast.makeText(this, getResources().getString(R.string.get_mentor_is_already_called), Toast.LENGTH_SHORT).show();
            return;
        }
        isGettingMentor = true;
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", id);
        requestParams.add("owner_id", StorageHelper.getUserDetails(this, "user_id"));
        int limit = 7;                                          //  This is a limit for getting free slots details for this mentor in terms of limit days from current date
        requestParams.add("limit", String.valueOf(limit));
        String authToken = StorageHelper.getUserDetails(this, "auth_token");
        String user_group = StorageHelper.getUserGroup(UserListActivity.this, "user_group");
        if (user_group != null) {
            requestParams.add("user_group", StorageHelper.getUserGroup(UserListActivity.this, "user_group"));
            /*
            requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
            */
            NetworkClient.getMentorDetails(this, requestParams, authToken, this, 24);
        } else {
            Toast.makeText(UserListActivity.this, getResources().getString(R.string.check_network_connection), Toast.LENGTH_SHORT).show();
        }


    }

    private void initialize() {
        listView = (ListView) findViewById(R.id.user_list);
        String json = getIntent().getStringExtra("list");
        searchFor = getIntent().getStringExtra("search_for");
        Log.d(TAG, "Intent String:" + json);
        Log.e(TAG, json);
        SearchResponse searchResponse = new Gson().fromJson(json, SearchResponse.class);
        users = searchResponse.getData();
        Log.d(TAG, "Users:" + users);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        mentorListAdapter = new MentorListAdapter(this, users, progressDialog, searchFor);
        listView.setAdapter(mentorListAdapter);

        TextView title = (TextView) findViewById(R.id.title);
        String titleName = users.size() > 1 ? getResources().getString(R.string.mentors) : getResources().getString(R.string.mentor);
        title.setText(titleName + " " + getResources().getString(R.string.forrr) + " " + getIntent().getStringExtra("search_for").split("-")[0]);
        menuItem = (ImageView) findViewById(R.id.menuItem);
//        menuItem.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_sort_by_size));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == NEED_TO_REFRESH) {
            if (users != null && selectedPosition != -1) {
                try {
                    users.get(selectedPosition).setConnectionId(data.getStringExtra("connectionId"));
                } catch (Exception e) {
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
        if (calledApiValue == 24) {
            Intent intent = new Intent(getApplicationContext(), MentorDetailsActivity.class);
            intent.putExtra("mentorDetails", (String) object);
            try {
                intent.putExtra("searched_keyword", getIntent().getStringExtra("searched_keyword"));
            } catch (Exception ignored) {
            }
            intent.putExtra("connection_status", connection_status_for_Selected_mentor);
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
