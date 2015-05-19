package com.findmycoach.app.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        jsonArray_notifications = new JSONArray();
        getNotifications();
    }

    private void getNotifications() {
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
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));
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
                                case "Connection accepted":
                                    action_for = "connection_accepted";
                                    break;
                                case "Connection rejected":
                                    action_for = "connection_rejected";
                                    break;
                                case "Schedule accepted":
                                    action_for = "schedule_accepted";
                                    break;
                                case "Schedule rejected":
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

        return view;
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
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
        Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
    }













    /*private ListView notificationListView;
    private ProgressDialog progressDialog;
    private NotificationAdapter notificationAdapter;
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
            notificationListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.no_data_found, new String[]{getActivity().getResources().getString(R.string.no_notification)}));
            notificationListView.setSelector(new ColorDrawable(0));
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

                    notificationAdapter = new NotificationAdapter(getActivity(), data, this, progressDialog);
                    notificationListView.setAdapter(notificationAdapter);
                }else {
                    notificationAdapter = new NotificationAdapter(getActivity());
                    notificationListView.setAdapter(notificationAdapter);
                    notificationListView.setSelector(new ColorDrawable(0));
                }
            }else {
                if(notificationAdapter != null && notificationAdapter.positionToRemove != -1 && connectionRequestsResponse != null){
                    notificationAdapter.notifications.remove(notificationAdapter.positionToRemove);
                    notificationAdapter.notifyDataSetChanged();
                    notificationAdapter.positionToRemove = -1;
                    Toast.makeText(getActivity(),getResources().getString(R.string.success),Toast.LENGTH_LONG).show();
                    return;
                }

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
    }*/
}
