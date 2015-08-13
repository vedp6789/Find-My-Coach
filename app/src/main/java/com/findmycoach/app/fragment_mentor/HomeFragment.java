package com.findmycoach.app.fragment_mentor;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.findmycoach.app.R;
import com.findmycoach.app.adapter.MentorNotificationTabsPagerAdapter;
import com.findmycoach.app.beans.UserNotifications.ConnectionRequest;
import com.findmycoach.app.beans.UserNotifications.Durations;
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
    private MentorNotifications mentorNotifications;
    private String TAG = "FMC";
    private String tag;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    boolean after_action;  /* using for tab change after reload from action accetp or reject*/
    int move_to_TAB;  /* this will help in moving to different view pager page*/
    public static HomeFragment homeFragment;
    private static int tab_to_show = 0;

    public HomeFragment() {
        // Required empty public constructor
        homeFragment = this;
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.e(TAG,"onResume in Home");
        getNotifications();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeFragment = this;
        try {
            Log.e(TAG, tag + " before");
            tag = getArguments().getString("OpenTab");
            Log.e(TAG, tag + " after");
            after_action = false;
        } catch (Exception ignored) {
        }


        Log.d("FMC", "Inside mentor home fragment");
        mentorNotifications = new MentorNotifications();
        mentorNotificationTabsPagerAdapter = new MentorNotificationTabsPagerAdapter(getActivity().getSupportFragmentManager(), mentorNotifications, getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getResources().getString(R.string.please_wait));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("FMC", "Inside mentor home fragment onDestroy");
        homeFragment = null;
    }

    public void getNotifications() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        requestParams.add("user_group", StorageHelper.getUserDetails(getActivity(), "user_group"));
        Log.d(TAG, "Notification data for: " + " user_id: " + StorageHelper.getUserDetails(getActivity(), "user_id") + " user_group: " + StorageHelper.getUserGroup(getActivity(), "user_group"));


//        if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
//            progressDialog.show();
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
        /*notifications_on_viewpager.setCurrentItem(1);*/
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.mentor_notification_tab);
        pagerSlidingTabStrip.setShouldExpand(true);
        pagerSlidingTabStrip.setViewPager(notifications_on_viewpager);
        if (tag != null && tag.equalsIgnoreCase("Schedule")) {
            notifications_on_viewpager.setCurrentItem(1);
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.purple);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                move_to_TAB = notifications_on_viewpager.getCurrentItem();
                getNotifications();
            }
        });
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
//        progressDialog.dismiss();
        String mentor_notifications = (String) object;
        if (calledApiValue == 19) {
            try {
                JSONObject jsonObject = new JSONObject(mentor_notifications);
                int status = Integer.parseInt(jsonObject.getString("status"));
                if (status == 1) {


                    ArrayList<ConnectionRequest> connectionRequests = new ArrayList<ConnectionRequest>();
                    pagerSlidingTabStrip.setViewPager(notifications_on_viewpager);
                    ArrayList<ScheduleRequest> scheduleRequests = new ArrayList<ScheduleRequest>();
                    JSONArray jsonArray_notifications = jsonObject.getJSONArray("data");
                    for (int notification_no = 0; notification_no < jsonArray_notifications.length(); notification_no++) {

                        JSONObject jsonObject_notification = jsonArray_notifications.getJSONObject(notification_no);
                        Log.d(TAG, "jsonObject as string " + jsonObject_notification.toString());
                        String title = jsonObject_notification.getString("title");
                        if (title.equalsIgnoreCase("Connection_request")) {

                            ConnectionRequest connectionRequest = new ConnectionRequest();
                            connectionRequest.setId(jsonObject_notification.getString("id"));
                            String image_url = jsonObject_notification.getString("image");
                            Log.d(TAG, "image url not found");
                            if (image_url != null) {
                                connectionRequest.setImage_url(jsonObject_notification.getString("image"));
                            }
                            connectionRequest.setImage_url(jsonObject_notification.getString("image"));
                            connectionRequest.setConnection_id(jsonObject_notification.getString("connection_id"));
                            connectionRequest.setStudent_id(jsonObject_notification.getString("student_id"));
                            connectionRequest.setMessage(jsonObject_notification.getString("message"));
                            connectionRequest.setFirst_name(jsonObject_notification.getString("first_name"));
                            connectionRequest.setLast_name(jsonObject_notification.getString("last_name"));
                            connectionRequest.setStart_date(jsonObject_notification.getString("created_date"));
                            connectionRequest.setStart_time(jsonObject_notification.getString("created_time"));
                            /*connectionRequest.setSubject(jsonObject_notification.getString("subject"));*/
                            connectionRequest.setStatus(jsonObject_notification.getString("status"));

                            connectionRequests.add(connectionRequest);
                        } else {
                            if (title.equalsIgnoreCase("Schedule_request")) {
                                Log.d(TAG, "Inside Schedule request type title");
                                ScheduleRequest scheduleRequest = new ScheduleRequest();
                                scheduleRequest.setId(jsonObject_notification.getString("id"));
                                scheduleRequest.setImage_url(jsonObject_notification.getString("image"));
                                scheduleRequest.setStudent_id(jsonObject_notification.getString("student_id"));
                                scheduleRequest.setEvent_id(jsonObject_notification.getString("event_id"));
                                scheduleRequest.setMessage(jsonObject_notification.getString("message"));
                                scheduleRequest.setFirst_name(jsonObject_notification.getString("first_name"));
                                scheduleRequest.setLast_name(jsonObject_notification.getString("last_name"));
                                scheduleRequest.setStart_time(jsonObject_notification.getString("start_time"));
                                scheduleRequest.setStop_time(jsonObject_notification.getString("stop_time"));
                                scheduleRequest.setSubject(jsonObject_notification.getString("subject"));
                                scheduleRequest.setClass_type(jsonObject_notification.getString("class_type"));
                                JSONArray week_days_jsonArray = jsonObject_notification.getJSONArray("week_days");
                                String[] week_days = new String[week_days_jsonArray.length()];
                                for (int week_day = 0; week_day < week_days_jsonArray.length(); week_day++) {
                                    week_days[week_day] = week_days_jsonArray.getString(week_day);
                                }

                                scheduleRequest.setWeek_days(week_days);
                                scheduleRequest.setStatus(jsonObject_notification.getString("status"));
                                scheduleRequest.setCreated_date(jsonObject_notification.getString("created_date"));
                                scheduleRequest.setCreated_time(jsonObject_notification.getString("created_time"));

                                List<Durations> durationses = new ArrayList<Durations>();
                                //  Log.d(TAG,"durations: "+);
                                for (int duration = 0; duration < jsonObject_notification.getJSONArray("durations").length(); duration++) {
                                    JSONObject jsonObject1 = jsonObject_notification.getJSONArray("durations").getJSONObject(duration);
                                    Durations durations = new Durations();
                                    durations.setStart_date(jsonObject1.getString("start_date"));
                                    durations.setStop_date(jsonObject1.getString("stop_date"));
                                    durationses.add(durations);
                                }

                                scheduleRequest.setDurations_list(durationses);
                                scheduleRequest.setTutorial_location(jsonObject_notification.getString("tutorial_location"));
                                //scheduleRequest.setTutorial_location("tutorial location hardcoded");


                                scheduleRequests.add(scheduleRequest);
                            }
                        }
                    }

                    mentorNotifications.setList_of_connection_request(connectionRequests);
                    mentorNotifications.setList_of_schedule_request(scheduleRequests);

                    mentorNotificationTabsPagerAdapter = new MentorNotificationTabsPagerAdapter(getActivity().getSupportFragmentManager(), mentorNotifications, getActivity());
                    notifications_on_viewpager.setAdapter(mentorNotificationTabsPagerAdapter);

                    pagerSlidingTabStrip.setViewPager(notifications_on_viewpager);

                    if (tag != null && tag.equalsIgnoreCase("Schedule")) {
                        notifications_on_viewpager.setCurrentItem(1);

                    } else {

                        if (after_action) {
                            Log.d(TAG, "after action move To TAB: " + move_to_TAB);
                            after_action = false;
                            notifications_on_viewpager.setCurrentItem(move_to_TAB);
                        } else {
                            Log.d(TAG, "without action");
                            notifications_on_viewpager.setCurrentItem(move_to_TAB);
                        }
                    }


                } else {
                    //          Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mSwipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
//        progressDialog.dismiss();
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
//        Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
    }
}
