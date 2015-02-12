package com.findmycoach.mentor.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.findmycoach.mentor.dao.NotificationModel;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.findmycoach.mentor.util.StorageHelper;
import com.fmc.mentor.findmycoach.R;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment implements Callback{

    private ListView notificationListView;
    ArrayList<NotificationModel> notifications;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {
        notificationListView = (ListView) view.findViewById(R.id.notification_list);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String userId = StorageHelper.getUserDetails(getActivity(), "user_id");
        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
        RequestParams requestParams = new RequestParams();
        requestParams.add("invitee", userId);
        NetworkClient.getConnectionRequests(getActivity(), requestParams, authToken, this);
    }

    @Override
    public void successOperation(Object object) {

    }

    @Override
    public void failureOperation(Object object) {

    }
}
