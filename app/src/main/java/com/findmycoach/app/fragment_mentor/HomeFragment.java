package com.findmycoach.app.fragment_mentor;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.findmycoach.app.R;
import com.findmycoach.app.adapter.MentorNotificationTabsPagerAdapter;
import com.findmycoach.app.beans.UserNotifications.ConnectionRequest;
import com.findmycoach.app.beans.UserNotifications.MentorNotifications;
import com.findmycoach.app.beans.UserNotifications.ScheduleRequest;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements Callback {
    private ViewPager notifications_on_viewpager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private MentorNotificationTabsPagerAdapter mentorNotificationTabsPagerAdapter;
    private ProgressDialog progressDialog;
    MentorNotifications mentorNotifications;



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
        Log.d("FMC", "INside mentor home fragment");
        mentorNotifications=new MentorNotifications();
        mentorNotificationTabsPagerAdapter = new MentorNotificationTabsPagerAdapter(getActivity().getSupportFragmentManager(),mentorNotifications);
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
        progressDialog.dismiss();
        String mentor_notifications= (String) object;
        if(calledApiValue == 19){
            try {
                JSONObject jsonObject=new JSONObject(mentor_notifications);
                String status=jsonObject.getString("status");
                if(status.equals("true")){


                    ArrayList<ConnectionRequest> connectionRequests=new ArrayList<ConnectionRequest>();
                    ArrayList<ScheduleRequest>  scheduleRequests=new ArrayList<ScheduleRequest>();
                    JSONArray jsonArray_notifications=jsonObject.getJSONArray("data");
                    for(int notification_no=0;notification_no < jsonArray_notifications.length();notification_no++){

                          JSONObject jsonObject_notification=jsonArray_notifications.getJSONObject(notification_no);
                          String title=jsonObject_notification.getString("title");
                          if(title.equalsIgnoreCase("Connection request")){
                              ConnectionRequest connectionRequest=new ConnectionRequest();
                              connectionRequest.setId(jsonObject_notification.getString("id"));
                              connectionRequest.setMessage(jsonObject_notification.getString("message"));
                              connectionRequest.setFirst_name(jsonObject_notification.getString("first_name"));
                              connectionRequest.setLast_name(jsonObject_notification.getString("last_name"));
                              connectionRequest.setStart_date(jsonObject_notification.getString("start_date"));
                              connectionRequest.setStart_time(jsonObject_notification.getString("start_time"));
                              connectionRequest.setSubject(jsonObject_notification.getString("subject"));
                              connectionRequests.add(connectionRequest);
                          }else {
                              if(title.equalsIgnoreCase("Schedule request")){
                                  ScheduleRequest scheduleRequest=new ScheduleRequest();
                                  scheduleRequest.setId(jsonObject_notification.getString("id"));
                                  scheduleRequest.setMessage(jsonObject_notification.getString("message"));
                                  scheduleRequest.setFirst_name(jsonObject_notification.getString("first_name"));
                                  scheduleRequest.setLast_name(jsonObject_notification.getString("last_name"));
                                  scheduleRequest.setStart_date(jsonObject_notification.getString("start_date"));
                                  scheduleRequest.setStop_date(jsonObject_notification.getString("stop_date"));
                                  scheduleRequest.setStart_time(jsonObject_notification.getString("start_time"));
                                  scheduleRequest.setStop_time(jsonObject_notification.getString("stop_time"));
                                  scheduleRequest.setSubject(jsonObject_notification.getString("subject"));
                                  scheduleRequest.setClass_type(jsonObject_notification.getString("class_type"));

                                  JSONArray week_days_jsonArray = jsonObject.getJSONArray("week_days");
                                  String[] week_days = new String[week_days_jsonArray.length()];
                                  for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                                      week_days[week_day] = week_days_jsonArray.getString(week_day);
                                  }

                                  scheduleRequest.setWeek_days(week_days);
                                  scheduleRequests.add(scheduleRequest);
                              }
                          }
                    }

                    mentorNotifications.setList_of_connection_request(connectionRequests);
                    mentorNotifications.setList_of_schedule_request(scheduleRequests);

                    mentorNotificationTabsPagerAdapter = new MentorNotificationTabsPagerAdapter(getActivity().getSupportFragmentManager(),mentorNotifications);
                    notifications_on_viewpager.setAdapter(mentorNotificationTabsPagerAdapter);
                    pagerSlidingTabStrip.setViewPager(notifications_on_viewpager);


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
        progressDialog.dismiss();
        Toast.makeText(getActivity(),(String)object,Toast.LENGTH_SHORT).show();
    }
}
