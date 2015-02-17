package com.findmycoach.mentor.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.findmycoach.mentor.beans.requests.Data;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.fmc.mentor.findmycoach.R;
import com.loopj.android.http.RequestParams;

import java.util.List;

/**
 * Created by prem on 12/2/15.
 */
public class NotificationAdapter extends BaseAdapter {

    private Context context;
    private List<Data> notifications;
    private Callback callback;
    private ProgressDialog progressDialog;
    public int positionToRemove = -1;

    public NotificationAdapter(Context context, List<Data> notifications, Callback callback, ProgressDialog progressDialog) {
        this.context = context;
        this.notifications = notifications;
        this.callback = callback;
        this.progressDialog = progressDialog;
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
            view = inflater.inflate(R.layout.notification_list_view, null);
        }
        TextView sender = (TextView) view.findViewById(R.id.senderTV);
        TextView message = (TextView) view.findViewById(R.id.messageTV);
        view.findViewById(R.id.acceptButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                positionToRemove = position;
                RequestParams requestParams = new RequestParams();
                requestParams.add("id", senderData.getConnectionId());
                requestParams.add("status", "accepted");
                NetworkClient.respondToConnectionRequest(context, requestParams, callback);
            }
        });
        view.findViewById(R.id.declineButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                positionToRemove = position;
                RequestParams requestParams = new RequestParams();
                requestParams.add("id", senderData.getConnectionId());
                requestParams.add("status", "rejected");
                NetworkClient.respondToConnectionRequest(context, requestParams, callback);
            }
        });


        sender.setText(senderData.getOwnerId()+"");
        message.setText(senderData.getConnectionMessage());
        return view;
    }
}
