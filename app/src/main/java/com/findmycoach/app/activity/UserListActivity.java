package com.findmycoach.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.findmycoach.app.R;
import com.findmycoach.app.adapter.MentorListAdapter;
import com.findmycoach.app.beans.search.Datum;
import com.findmycoach.app.beans.search.SearchResponse;
import com.findmycoach.app.fragment.SearchResultsFragment;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.util.List;

public class UserListActivity extends FragmentActivity implements Callback {

    private ProgressDialog progressDialog;
    private Datum datum;
    private boolean isGettingMentor = false;
    private static final String TAG = "FMC";
    private int selectedPosition = -1;
    private String searchFor;
    private ImageView menuItem;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ViewPager searchViewPager;
    private SearchPagerAdapter searchPagerAdapter;


    //new logic

    private String requestParams;
    private int noOfTabs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        initialize();
        applyActions();
    }

    private void applyActions() {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                connection_status_for_Selected_mentor = null;
//                if (users != null) {
////                    Log.d(TAG, "ListView click");
////                    selectedPosition = position;
////                    datum = users.get(position);
////                    connection_status_for_Selected_mentor = datum.getConnectionStatus();
////                    getMentorDetails(datum.getId());
//                    Toast.makeText(UserListActivity.this, "Please use connection button to send connection. Mentor Profile is under construction.", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashboardActivity.class));
        super.onBackPressed();
    }



    private void initialize() {
       // listView = (ListView) findViewById(R.id.user_list);
        pagerSlidingTabStrip=(PagerSlidingTabStrip)findViewById(R.id.search_tabs);
        searchViewPager=(ViewPager)findViewById(R.id.search_view_pager);


        //String json = getIntent().getStringExtra("list");
        searchFor = getIntent().getStringExtra("search_for");

       // Log.d(TAG, "Intent String:" + json);
       // Log.e(TAG, json);
        noOfTabs=getIntent().getIntExtra("no_of_tabs", 1);
        requestParams=getIntent().getStringExtra("request_params");

     //   SearchResponse searchResponse = new Gson().fromJson(json, SearchResponse.class);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        searchPagerAdapter=new SearchPagerAdapter(getSupportFragmentManager(),noOfTabs);
        searchViewPager.setAdapter(searchPagerAdapter);
        pagerSlidingTabStrip.setViewPager(searchViewPager);

        TextView title = (TextView) findViewById(R.id.title);
        String titleName = getResources().getString(R.string.mentor);
        title.setText(titleName + " " + getResources().getString(R.string.forrr) + " " + getIntent().getStringExtra("search_for").split("-")[0]);
        menuItem = (ImageView) findViewById(R.id.menuItem);
//        menuItem.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_sort_by_size));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == NEED_TO_REFRESH) {
//            if (users != null && selectedPosition != -1) {
//                try {
//                    users.get(selectedPosition).setConnectionId(data.getStringExtra("connectionId"));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                users.get(selectedPosition).setConnectionStatus(data.getStringExtra("connectionStatus"));
//                mentorListAdapter.notifyDataSetChanged();
//                datum = null;
//            }
//        }
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        // For displaying selected Mentor details

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        isGettingMentor = false;
        Toast.makeText(getApplicationContext(), (String) object, Toast.LENGTH_LONG).show();
    }


    private class SearchPagerAdapter extends FragmentStatePagerAdapter {

        private  int count;
        public SearchPagerAdapter(FragmentManager fm,int count) {
            super(fm);
            this.count=count;
        }

        @Override
        public Fragment getItem(int position) {
            return SearchResultsFragment.newInstance(position,requestParams,searchFor);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return "Child"+position;
        }

        @Override
        public int getCount() {
            return count;
        }
    }

}
