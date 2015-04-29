package com.findmycoach.app.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.beans.requests.Data;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.R;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

import java.util.List;

/**
 * Created by prem on 12/2/15.
 */
public class NotificationAdapter extends BaseAdapter {

    private Context context;
    public List<Data> notifications;
    private Callback callback;
    private ProgressDialog progressDialog;
    public int positionToRemove = -1;
    public int positionToRemoveFromDetails = -1;
    private final String TAG = "FMC";
    public static int connection_id = -1;

    public NotificationAdapter(Context context, List<Data> notifications, Callback callback, ProgressDialog progressDialog) {
        this.context = context;
        this.notifications = notifications;
        this.callback = callback;
        this.progressDialog = progressDialog;
        connection_id = -1;
    }

    public NotificationAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if(notifications == null || notifications.size() == 0)
            return 1;
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        if(notifications == null || notifications.size() == 0){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.no_data_found, null);
            TextView textView = (TextView) view.findViewById(R.id.noDataTV);
            textView.setText(context.getResources().getString(R.string.no_new_request));
            return view;
        }

        final Data senderData = notifications.get(position);
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.notification_single_row, null);
        }
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.singleRow);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                connection_id = senderData.getId();
                positionToRemoveFromDetails = position;
                RequestParams requestParams = new RequestParams();
                requestParams.add("id",senderData.getOwnerId()+"");
                requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
                String authToken = StorageHelper.getUserDetails(context, "auth_token");
                NetworkClient.getStudentDetails(context, requestParams, authToken, callback, 25);
            }
        });

        TextView sender = (TextView) view.findViewById(R.id.senderTV);
        TextView message = (TextView) view.findViewById(R.id.messageTV);
        ImageButton acceptButton = (ImageButton) view.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                positionToRemove = position;
                RequestParams requestParams = new RequestParams();
                requestParams.add("id", senderData.getId() + "");
                requestParams.add("status", "accepted");
                NetworkClient.respondToConnectionRequest(context, requestParams, callback, 18);
            }
        });

        ImageButton declineButton = (ImageButton) view.findViewById(R.id.declineButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                positionToRemove = position;
                RequestParams requestParams = new RequestParams();
                requestParams.add("id", senderData.getId() + "");
                requestParams.add("status", "rejected");
                Log.d(TAG, senderData.getId() + "");
                Log.d(TAG, "rejected");
                NetworkClient.respondToConnectionRequest(context, requestParams, callback, 18);
            }
        });

        sender.setText(senderData.getOwnerName());
        message.setText(senderData.getConnectionMessage());
        return view;
    }
}
