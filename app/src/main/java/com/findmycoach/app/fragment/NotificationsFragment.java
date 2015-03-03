package com.findmycoach.app.fragment;

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

import com.findmycoach.app.activity.StudentDetailActivity;
import com.findmycoach.app.adapter.NotificationAdapter;
import com.findmycoach.app.beans.requests.ConnectionRequestsResponse;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.findmycoach.app.R;
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
        if(StorageHelper.getUserGroup(getActivity(),"user_group").equals("3")){
            Log.d(TAG,"Auth Token : " + authToken + "\nUser ID : " + userId);
            RequestParams requestParams = new RequestParams();
            requestParams.add("invitee", userId);
            NetworkClient.getConnectionRequests(getActivity(), requestParams, authToken, this,22);
        }
        if(StorageHelper.getUserGroup(getActivity(),"user_group").equals("2")){

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NotificationAdapter.connection_id && resultCode == Activity.RESULT_OK && connectionRequestsResponse != null){
            connectionRequestsResponse.getData().remove(notificationAdapter.positionToRemoveFromDetails);
            notificationAdapter.notifyDataSetChanged();
        }
        notificationAdapter.positionToRemoveFromDetails = -1;
        NotificationAdapter.connection_id = -1;
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {

        progressDialog.dismiss();
        if(StorageHelper.getUserGroup(getActivity(),"user_group").equals("3")){
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

                if(NotificationAdapter.connection_id == -1)
                    return;
                Intent intent = new Intent(getActivity(), StudentDetailActivity.class);
                intent.putExtra("student_detail",(String) object);
                startActivityForResult(intent, NotificationAdapter.connection_id);
            }
        }else if(StorageHelper.getUserGroup(getActivity(),"user_group").equals("2")){
            //TODO for student notification
        }

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
//        if(StorageHelper.getUserGroup(getActivity(),"user_group").equals("3")){
            progressDialog.dismiss();
            Toast.makeText(getActivity(),(String) object, Toast.LENGTH_LONG).show();
//        }else if(StorageHelper.getUserGroup(getActivity(),"user_group").equals("2")){
//            //TODO for student notification
//        }
    }
}
