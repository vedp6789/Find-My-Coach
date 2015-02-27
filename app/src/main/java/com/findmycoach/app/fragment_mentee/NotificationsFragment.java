package com.findmycoach.app.fragment_mentee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.findmycoach.student.R;
import com.findmycoach.student.adapter.ConnectionAdapter;
import com.findmycoach.student.beans.connections.ConnectionRequestsResponse;
import com.findmycoach.student.beans.connections.Data;
import com.findmycoach.student.util.Callback;
import com.findmycoach.student.util.NetworkClient;
import com.findmycoach.student.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.List;

public class NotificationsFragment extends Fragment implements Callback {

    private ListView connectionListView;
    private ProgressDialog progressDialog;
    private ConnectionRequestsResponse connectionRequestsResponse;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static MyConnectionsFragment newInstance(String param1, String param2) {
        MyConnectionsFragment fragment = new MyConnectionsFragment();
        return fragment;
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getConnectionList();
    }

    private void getConnectionList(){
        //connections
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("user_id", StorageHelper.getUserDetails(getActivity(), "user_id"));
        NetworkClient.getAllConnectionRequest(getActivity(), requestParams, this);
    }

    private void populateData(final List<Data> data) {
        ConnectionAdapter connectionAdapter = new ConnectionAdapter(getActivity(), data);
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
    public void successOperation(Object object) {
        progressDialog.dismiss();
        connectionRequestsResponse = (ConnectionRequestsResponse) object;
        if(connectionRequestsResponse.getData() != null && connectionRequestsResponse.getData().size() > 0) {
            populateData(connectionRequestsResponse.getData());
        }
    }

    @Override
    public void failureOperation(Object object) {
        progressDialog.dismiss();
        String msg = (String) object;
        if(msg.equals("Success"))
            connectionListView.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.no_data_found, new String[]{getResources().getString(R.string.not_connected_to_anyone)}));
        else
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}