package com.findmycoach.mentor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.findmycoach.mentor.dao.NotificationModel;
import com.fmc.mentor.findmycoach.R;

import java.util.ArrayList;

/**
 * Created by prem on 12/2/15.
 */
public class NotificationAdapter extends BaseAdapter {

    private Context context;
    ArrayList<NotificationModel> notifications;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public int getCount() {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.notification_list_view, null);
        }
        TextView message = (TextView) view.findViewById(R.id.notification_message);
        TextView time = (TextView) view.findViewById(R.id.notification_time);
        message.setText(notifications.get(position).getMessage());
        time.setText(notifications.get(position).getTime());
        return view;
    }
}
