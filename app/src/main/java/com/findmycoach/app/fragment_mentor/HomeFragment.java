package com.findmycoach.app.fragment_mentor;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.findmycoach.app.R;
import com.findmycoach.app.adapter.MentorNotificationTabsPagerAdapter;
import com.findmycoach.app.beans.UserNotifications.MentorNotifications;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements Callback {
    private ViewPager notifications_on_viewpager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private MentorNotificationTabsPagerAdapter mentorNotificationTabsPagerAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<MentorNotifications> arrayList_schedule_request_notifications;


    public HomeFragment() {
        // Required empty public constructor

    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList_schedule_request_notifications=new ArrayList<MentorNotifications>();
        mentorNotificationTabsPagerAdapter = new MentorNotificationTabsPagerAdapter(getActivity().getSupportFragmentManager(),arrayList_schedule_request_notifications);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getResources().getString(R.string.please_wait));

        getNotifications();


    }

    private void getNotifications() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("user_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        requestParams.add("user_group", StorageHelper.getUserDetails(getActivity(), "user_group"));
        progressDialog.show();
        NetworkClient.getUserNotifications(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 19);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_mentor, container, false);
        populateView(view);
        return view;
    }

    private void populateView(View view) {
        notifications_on_viewpager = (ViewPager) view.findViewById(R.id.vp_mentor_notifications);
        notifications_on_viewpager.setAdapter(mentorNotificationTabsPagerAdapter);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.mentor_notification_tab);
        pagerSlidingTabStrip.setViewPager(notifications_on_viewpager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        String mentor_notifications= (String) object;
        if(calledApiValue == 19){
            try {
                JSONObject jsonObject=new JSONObject(mentor_notifications);
                String status=jsonObject.getString("status");
                if(status.equals("true")){
                    for(int notification_no=0;)
                }else{
                    Toast.makeText(getActivity(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {

    }
}
