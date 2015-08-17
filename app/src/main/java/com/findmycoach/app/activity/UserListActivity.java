package com.findmycoach.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.findmycoach.app.R;
import com.findmycoach.app.beans.student.ChildDetails;
import com.findmycoach.app.beans.student.Data;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.fragment.SearchResultsFragment;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserListActivity extends FragmentActivity implements Callback {

    private ProgressDialog progressDialog;
    private String searchFor;
    private ImageView menuItem;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ViewPager searchViewPager;
    private SearchPagerAdapter searchPagerAdapter;
    private List<String> pagerTitleList;
    private List<Integer> currentPagerUserAge;
    //new logic
    private String requestParams;
    private int noOfTabs, mentorFor;

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
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.search_tabs);
        searchViewPager = (ViewPager) findViewById(R.id.search_view_pager);


        //String json = getIntent().getStringExtra("list");
        searchFor = getIntent().getStringExtra("search_for");

        // Log.d(TAG, "Intent String:" + json);
        // Log.e(TAG, json);
        noOfTabs = getIntent().getIntExtra("no_of_tabs", 1);
        mentorFor = getIntent().getIntExtra("mentor_for", 0);
        requestParams = getIntent().getStringExtra("request_params");

        pagerTitleList = new ArrayList<>();
        currentPagerUserAge = new ArrayList<>();
        ProfileResponse profileResponse = new Gson().fromJson(StorageHelper.getUserProfile(this), ProfileResponse.class);
        Data userInfo = profileResponse.getData();
        if (mentorFor == 0) {
            pagerTitleList.add(userInfo.getFirstName());
            currentPagerUserAge.add(getAgeToSearchMentorFor(userInfo.getDob().toString()));
        }
        if (mentorFor > 0) {
            for (ChildDetails childDetails : userInfo.getChildren()) {
                pagerTitleList.add(childDetails.getName());
                currentPagerUserAge.add(getAgeToSearchMentorFor(childDetails.getDob()));
            }

            if (mentorFor == 2) {
                pagerTitleList.add(userInfo.getFirstName());
                currentPagerUserAge.add(getAgeToSearchMentorFor(userInfo.getDob().toString()));
            }
        }


        //   SearchResponse searchResponse = new Gson().fromJson(json, SearchResponse.class);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        searchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager(), noOfTabs);
        searchViewPager.setAdapter(searchPagerAdapter);
        pagerSlidingTabStrip.setViewPager(searchViewPager);

        TextView title = (TextView) findViewById(R.id.title);
        String titleName = getResources().getString(R.string.mentor);
        title.setText(titleName + " " + getResources().getString(R.string.forrr) + " " + getIntent().getStringExtra("search_for").split("-")[0]);
        menuItem = (ImageView) findViewById(R.id.menuItem);
//        menuItem.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_sort_by_size));

    }

    private int getAgeToSearchMentorFor(String dobString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(dobString);
            Calendar dob = Calendar.getInstance();
            dob.setTime(date);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
                age--;
            } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
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
//        isGettingMentor = false;
        Toast.makeText(getApplicationContext(), (String) object, Toast.LENGTH_LONG).show();
    }


    private class SearchPagerAdapter extends FragmentStatePagerAdapter {

        private int count;

        public SearchPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            this.count = count;
        }

        @Override
        public Fragment getItem(int position) {
            return SearchResultsFragment.newInstance(position, requestParams, searchFor, currentPagerUserAge.get(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pagerTitleList.get(position);
        }

        @Override
        public int getCount() {
            return count;
        }
    }

}
