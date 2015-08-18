package com.findmycoach.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.findmycoach.app.R;
import com.findmycoach.app.beans.student.ChildDetails;
import com.findmycoach.app.beans.student.Data;
import com.findmycoach.app.beans.student.ProfileResponse;
import com.findmycoach.app.fragment.SearchResultsFragment;
import com.findmycoach.app.util.StorageHelper;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserListActivity extends FragmentActivity {

    private ProgressDialog progressDialog;
    private String searchFor;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ViewPager searchViewPager;
    private SearchPagerAdapter searchPagerAdapter;
    private List<String> pagerTitleList;
    private List<Integer> currentPagerUserAge;

    private String requestParams, aroundTime;
    private int noOfTabs, mentorFor;
    private boolean showTimeNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        initializeForMultipleTabs();
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


    private void initializeForMultipleTabs() {
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.search_tabs);
        searchViewPager = (ViewPager) findViewById(R.id.search_view_pager);

        searchFor = getIntent().getStringExtra("search_for");
        noOfTabs = getIntent().getIntExtra("no_of_tabs", 1);
        mentorFor = getIntent().getIntExtra("mentor_for", 0);
        showTimeNavigation = getIntent().getBooleanExtra("show_time_navigation", false);
        if(showTimeNavigation)
            aroundTime = getIntent().getStringExtra("around_time");
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

        searchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager(), noOfTabs);
        searchViewPager.setAdapter(searchPagerAdapter);
        pagerSlidingTabStrip.setViewPager(searchViewPager);

        TextView title = (TextView) findViewById(R.id.title);
        String titleName = getResources().getString(R.string.mentor);
        title.setText(titleName + " " + getResources().getString(R.string.forrr) + " " + getIntent().getStringExtra("search_for").split("-")[0]);
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

    private class SearchPagerAdapter extends FragmentStatePagerAdapter {

        private int count;

        public SearchPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            this.count = count;
        }

        @Override
        public Fragment getItem(int position) {
            return SearchResultsFragment.newInstance(position, requestParams, searchFor, currentPagerUserAge.get(position), showTimeNavigation, aroundTime);
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
