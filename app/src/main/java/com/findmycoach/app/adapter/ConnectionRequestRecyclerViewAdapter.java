package com.findmycoach.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.UserNotifications.ConnectionRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by ved on 14/5/15.
 */
public class ConnectionRequestRecyclerViewAdapter extends RecyclerView.Adapter<ConnectionRequestRecyclerViewAdapter.ViewHolder> {
    private ArrayList<ConnectionRequest> connectionRequests;


    public ConnectionRequestRecyclerViewAdapter(ArrayList<ConnectionRequest> connectionRequests) {
        this.connectionRequests = connectionRequests;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.connection_request_notification, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String first_name=connectionRequests.get(position).getFirst_name();
        String start_date=connectionRequests.get(position).getStart_date();
        String start_time=connectionRequests.get(position).getStart_time();

        Calendar cal = new GregorianCalendar();
        cal.set(Integer.parseInt(start_date.split("-")[0]),Integer.parseInt(start_date.split("-")[1])-1, Integer.parseInt(start_date.split("-")[2]));
        long slot_start_date = cal.getTimeInMillis();


        Calendar rightNow = Calendar.getInstance();
        long rightNow_in_millis = rightNow.getTimeInMillis();
        int current_hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int current_minute = rightNow.get(Calendar.MINUTE);
    }


    @Override
    public int getItemCount() {
        return connectionRequests.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_connection_request_message;
        TextView tv_connection_request_delivery_time;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_connection_request_message = (TextView) itemView.findViewById(R.id.tv_connection_request_message);
            tv_connection_request_delivery_time = (TextView) itemView.findViewById(R.id.tv_how_old_message);
        }
    }


}
