package com.findmycoach.app.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.adapter.ConnectionAdapter;
import com.findmycoach.app.beans.requests.ConnectionRequestsResponse;
import com.findmycoach.app.beans.requests.Data;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class MyConnectionsFragment extends Fragment implements Callback {

    private ListView connectionListView;
    private ProgressDialog progressDialog;
    private ConnectionRequestsResponse connectionRequestsResponse;
    private ConnectionAdapter connectionAdapter;
    public static boolean needToRefresh = false;
    private static final String TAG = "FMC";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public MyConnectionsFragment() {
        // Required empty public constructor
        needToRefresh = false;
    }

    public static MyConnectionsFragment newInstance(String param1, String param2) {
        MyConnectionsFragment fragment = new MyConnectionsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needToRefresh) {
            getConnectionList();
            needToRefresh = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_connections, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View rootView) {
        connectionListView = (ListView) rootView.findViewById(R.id.connectionListView);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.purple);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getConnectionList();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*Creating/Checking folder for media storage*/
        StorageHelper.createAppMediaFolders(getActivity());

        getConnectionList();
    }

    private void getConnectionList() {
        //connections
        if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
            progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("user_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.getAllConnectionRequest(getActivity(), requestParams, this, 22);
    }

    private void populateData(final List<Data> data) {
        List<Data> dataToShow = new ArrayList<Data>();
        for (Data d : data) {
            boolean flag = false;
            for (Data d1 : dataToShow) {
                if (d1.getInviteeId().equals(d.getInviteeId()) && d1.getOwnerId().equals(d.getOwnerId())) {
                    flag = true;
                }
            }
            if (!flag)
                dataToShow.add(d);
        }
        Log.d(TAG, "Actual connection size : " + data.size() + ", current size : " + dataToShow.size());
        connectionAdapter = new ConnectionAdapter(getActivity(), dataToShow);
        connectionListView.setAdapter(connectionAdapter);
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
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        mSwipeRefreshLayout.setRefreshing(false);
        connectionRequestsResponse = (ConnectionRequestsResponse) object;
        if (connectionRequestsResponse.getData() != null && connectionRequestsResponse.getData().size() > 0) {
            populateData(connectionRequestsResponse.getData());
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        mSwipeRefreshLayout.setRefreshing(false);
        String msg = (String) object;
        if (msg.equals("Success")) {
            connectionListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.no_data_found, new String[]{getResources().getString(R.string.not_connected)}));
            connectionListView.setSelector(new ColorDrawable(0));
        } else
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}
