package com.findmycoach.mentor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.findmycoach.mentor.activity.StudentDetailActivity;
import com.findmycoach.mentor.adapter.NotificationAdapter;
import com.findmycoach.mentor.beans.requests.ConnectionRequestsResponse;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.findmycoach.mentor.util.StorageHelper;
import com.findmycoach.mentor.R;
import com.loopj.android.http.RequestParams;

public class NotificationsFragment extends Fragment implements Callback {

    private ListView notificationListView;
    private ProgressDialog progressDialog;
    private NotificationAdapter notificationAdapter;
    private ConnectionRequestsResponse connectionRequestsResponse;

    private static final String TAG="FMC";

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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
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
        progressDialog.show();
        String userId = StorageHelper.getUserDetails(getActivity(), "user_id");
        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
        Log.d(TAG,"Auth Token : " + authToken + "\nUser ID : " + userId);
        RequestParams requestParams = new RequestParams();
        requestParams.add("invitee", userId);
        NetworkClient.getConnectionRequests(getActivity(), requestParams, authToken, this);
    }

    @Override
    public void successOperation(Object object) {
        progressDialog.dismiss();
        if(object instanceof ConnectionRequestsResponse){
            connectionRequestsResponse = (ConnectionRequestsResponse) object;
            if(connectionRequestsResponse.getData() != null && connectionRequestsResponse.getData().size() > 0) {
                notificationAdapter = new NotificationAdapter(getActivity(), connectionRequestsResponse.getData(), this, progressDialog);
                notificationListView.setAdapter(notificationAdapter);
            }else {
                notificationAdapter = new NotificationAdapter(getActivity());
                notificationListView.setAdapter(notificationAdapter);
            }

        }else {
            if(notificationAdapter != null && notificationAdapter.positionToRemove != -1 && connectionRequestsResponse != null){
                connectionRequestsResponse.getData().remove(notificationAdapter.positionToRemove);
                notificationAdapter.notifyDataSetChanged();
                notificationAdapter.positionToRemove = -1;
                Toast.makeText(getActivity(),getResources().getString(R.string.success),Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(getActivity(), StudentDetailActivity.class);
            intent.putExtra("student_detail",(String) object);
            startActivity(intent);
        }

    }

    @Override
    public void failureOperation(Object object) {
        progressDialog.dismiss();
       Toast.makeText(getActivity(),(String) object, Toast.LENGTH_LONG).show();
    }
}
