package com.findmycoach.mentor.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fmc.student.findmycoach.R;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private ListView notificationListView;
    ArrayList<NotificationModel> notifications;

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
        notifications = getDummyNotifications();
    }

    @Override
    public void onStart() {
        super.onStart();
        notificationListView.setAdapter(new NotificationAdapter());
    }

    private ArrayList<NotificationModel> getDummyNotifications() {
        NotificationModel notfication1 = new NotificationModel("1", "Teja sent connection request", "Today 9 A.M");
        NotificationModel notfication2 = new NotificationModel("2", "Ravi posted comment", "Today 8.40 A.M");
        NotificationModel notfication3 = new NotificationModel("3", "John wants to chat", "Yesterday 7 P.M");
        NotificationModel notfication4 = new NotificationModel("4", "Smite sent connection request", "Yesterday 1 P.M");
        NotificationModel notfication5 = new NotificationModel("5", "Reddy sent a file", "24-Jan 10 A.M");
        NotificationModel notfication6 = new NotificationModel("6", "Teja requested class", "20-Jan 1 P.M");
        ArrayList<NotificationModel> notifications = new ArrayList<>();
        notifications.add(notfication1);
        notifications.add(notfication2);
        notifications.add(notfication3);
        notifications.add(notfication4);
        notifications.add(notfication5);
        notifications.add(notfication6);
        return notifications;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private class NotificationModel {
        private String id;
        private String message;
        private String time;

        public NotificationModel(String id, String message, String time) {
            this.id = id;
            this.message = message;
            this.time = time;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    private class NotificationAdapter extends BaseAdapter {

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
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.notification_list_view, null);
            }
            TextView message = (TextView) view.findViewById(R.id.notification_message);
            TextView time = (TextView) view.findViewById(R.id.notification_time);
            message.setText(notifications.get(position).getMessage());
            time.setText(notifications.get(position).getTime());
            return view;
        }
    }
}
