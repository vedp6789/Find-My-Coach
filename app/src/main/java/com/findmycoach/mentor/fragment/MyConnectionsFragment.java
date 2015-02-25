package com.findmycoach.mentor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.mentor.activity.ChatWidgetActivity;
import com.findmycoach.mentor.adapter.ConnectionAdapter;
import com.findmycoach.mentor.beans.requests.ConnectionRequestsResponse;
import com.findmycoach.mentor.beans.requests.Data;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.findmycoach.mentor.util.StorageHelper;
import com.findmycoach.mentor.R;
import com.loopj.android.http.RequestParams;

import java.util.List;

public class MyConnectionsFragment extends Fragment implements Callback {

    private ListView connectionListView;
    private ProgressDialog progressDialog;
    private ConnectionRequestsResponse connectionRequestsResponse;

    public MyConnectionsFragment() {
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

        /*Creating/Checking folder for media storage*/
        StorageHelper.createAppMediaFolders(getActivity());

        getConnectionList();
    }

    private void getConnectionList(){
        //connections
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("user_id", StorageHelper.getUserDetails(getActivity(),"user_id"));
        NetworkClient.getAllConnectionRequest(getActivity(), requestParams, this);
    }

    private void populateData(final List<Data> data) {
        ConnectionAdapter connectionAdapter = new ConnectionAdapter(getActivity(), data);
        connectionListView.setAdapter(connectionAdapter);
        connectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatWidgetIntent = new Intent(getActivity(), ChatWidgetActivity.class);
                chatWidgetIntent.putExtra("student_id", data.get(position).getOwnerId()+"");
                chatWidgetIntent.putExtra("student_name", data.get(position).getOwnerName());
                startActivity(chatWidgetIntent);
            }
        });
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
            connectionListView.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.no_data_found, new String[]{getResources().getString(R.string.not_connected)}));
        else
            Toast.makeText(getActivity(),msg , Toast.LENGTH_LONG).show();
    }
}
