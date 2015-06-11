package com.findmycoach.app.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.MenteeNotificationActions;
import com.findmycoach.app.adapter.MenteeNotificationRecyclerViewAdapter;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.DividerItemDecoration;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.RecyclerItemClickListener;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationsFragment extends Fragment implements Callback {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;
    private JSONArray jsonArray_notifications;
    String action_for;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        jsonArray_notifications = new JSONArray();
        getNotifications();
    }

    private void getNotifications() {
        if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
            progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        requestParams.add("user_group", StorageHelper.getUserGroup(getActivity(), "user_group"));
        NetworkClient.getUserNotifications(getActivity(), requestParams, StorageHelper.getUserDetails(getActivity(), "auth_token"), this, 19);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_mentee_notifications);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new MenteeNotificationRecyclerViewAdapter(getActivity(), jsonArray_notifications);
        recyclerView.setAdapter(adapter);

        /* on click listener */
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) throws JSONException {
                        if (jsonArray_notifications.length() > 0) {
                            JSONObject jsonObject_notification = jsonArray_notifications.getJSONObject(position);
                            String title = jsonObject_notification.getString("title");
                            String status = jsonObject_notification.getString("status");
                            switch (title) {
                                case "connection_accepted":
                                    action_for = "connection_accepted";
                                    break;
                                case "connection_rejected":
                                    action_for = "connection_rejected";
                                    break;
                                case "schedule_accepted":
                                    action_for = "schedule_accepted";
                                    break;
                                case "schedule_rejected":
                                    action_for = "schedule_rejected";
                                    break;
                            }
                            Intent intent = new Intent(getActivity(), MenteeNotificationActions.class);
                            intent.putExtra("action_for", action_for);
                            intent.putExtra("json_string", jsonObject_notification.toString());
                            if (status.equals("unread"))
                                startActivity(intent);

                        }


                    }
                })
        );

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.purple);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotifications();
            }
        });

        return view;
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        mSwipeRefreshLayout.setRefreshing(false);
        try {
            JSONObject jsonObject = new JSONObject((String) object);
            String status = jsonObject.getString("status");
            if (Boolean.parseBoolean(status)) {
                jsonArray_notifications = jsonObject.getJSONArray("data");
                if (jsonArray_notifications.length() > 0) {
                    adapter = new MenteeNotificationRecyclerViewAdapter(getActivity(), jsonArray_notifications);
                    recyclerView.setAdapter(adapter);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
    }

}
