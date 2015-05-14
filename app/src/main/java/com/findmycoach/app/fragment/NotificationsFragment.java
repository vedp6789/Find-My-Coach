package com.findmycoach.app.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.StudentDetailActivity;
import com.findmycoach.app.adapter.NotificationAdapter;
import com.findmycoach.app.adapter.NotificationRecyclerViewAdapter;
import com.findmycoach.app.beans.requests.ConnectionRequestsResponse;
import com.findmycoach.app.beans.requests.Data;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment implements Callback,ActionBar.TabListener {

    private RecyclerView rv_notifications;
    private NotificationRecyclerViewAdapter notificationRecyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;
    private ConnectionRequestsResponse connectionRequestsResponse;
    private static final int REFRESH_PAGE = 100;

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
        getNotifications();
    }

    private void getNotifications() {
        progressDialog.show();
        String userId = StorageHelper.getUserDetails(getActivity(), "user_id");
        String authToken = StorageHelper.getUserDetails(getActivity(), "auth_token");
        if(DashboardActivity.dashboardActivity.user_group == 3){
            Log.d(TAG, "Auth Token : " + authToken + "\nUser ID : " + userId);
            RequestParams requestParams = new RequestParams();
            requestParams.add("invitee", userId);
            NetworkClient.getConnectionRequests(getActivity(), requestParams, authToken, this, 22);
        }else if(DashboardActivity.dashboardActivity.user_group == 2){
            progressDialog.dismiss();
            /*notificationListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.no_data_found, new String[]{getActivity().getResources().getString(R.string.no_notification)}));
            notificationListView.setSelector(new ColorDrawable(0));*/
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REFRESH_PAGE)
            getNotifications();
        Log.e(TAG,"'OnResult'");
        NotificationAdapter.connection_id = -1;
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {

        progressDialog.dismiss();
        if(DashboardActivity.dashboardActivity.user_group == 3){
            if(object instanceof ConnectionRequestsResponse){
                connectionRequestsResponse = (ConnectionRequestsResponse) object;
                if(connectionRequestsResponse.getData() != null && connectionRequestsResponse.getData().size() > 0) {

                    List<Data> tempData = connectionRequestsResponse.getData();
                    List<Data> data = new ArrayList<Data>();

                    for(Data d : tempData){
                        boolean flag = false;

                        for(Data d1 : data){
                            if(d1.getInviteeId().equals(d.getInviteeId()) && d1.getOwnerId().equals(d.getOwnerId()))
                                flag = true;
                        }

                        if(!flag)
                            data.add(d);
                    }

                    /*notificationAdapter = new NotificationAdapter(getActivity(), data, this, progressDialog);
                    notificationListView.setAdapter(notificationAdapter);
*/                }else {
                    /*notificationAdapter = new NotificationAdapter(getActivity());
                    notificationListView.setAdapter(notificationAdapter);
                    notificationListView.setSelector(new ColorDrawable(0));*/
                }
            }else {
                /*if(notificationAdapter != null && notificationAdapter.positionToRemove != -1 && connectionRequestsResponse != null){
                    notificationAdapter.notifications.remove(notificationAdapter.positionToRemove);
                    notificationAdapter.notifyDataSetChanged();
                    notificationAdapter.positionToRemove = -1;
                    Toast.makeText(getActivity(),getResources().getString(R.string.success),Toast.LENGTH_LONG).show();
                    return;
                }
*/
                if(NotificationAdapter.connection_id == -1)
                    return;
                Intent intent = new Intent(getActivity(), StudentDetailActivity.class);
                intent.putExtra("student_detail",(String) object);
                startActivityForResult(intent, REFRESH_PAGE);
            }
        }else if(DashboardActivity.dashboardActivity.user_group == 2){
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
