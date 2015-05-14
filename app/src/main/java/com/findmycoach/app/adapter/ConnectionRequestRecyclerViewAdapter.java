package com.findmycoach.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    boolean no_connection_request=false;
    Context context;

    public ConnectionRequestRecyclerViewAdapter(Context context,ArrayList<ConnectionRequest> connectionRequests) {
        no_connection_request=false;
        this.connectionRequests = connectionRequests;
        if(connectionRequests.size() <=0){
            no_connection_request=true;
        }
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.connection_request_notification, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(no_connection_request){
            Log.d("FMC","NO connection request found");
            holder.tv_connection_request_message.setText(context.getResources().getString(R.string.no_connection_request));
        }else{
            String first_name=connectionRequests.get(position).getFirst_name();
            String start_date=connectionRequests.get(position).getStart_date();
            String start_time=connectionRequests.get(position).getStart_time();

            Calendar cal = new GregorianCalendar();
            cal.set(Integer.parseInt(start_date.split("-")[0]),Integer.parseInt(start_date.split("-")[1])-1, Integer.parseInt(start_date.split("-")[2]));
            long connection_request_start_date_in_millis = cal.getTimeInMillis();


            Calendar rightNow = Calendar.getInstance();
            long rightNow_in_millis = rightNow.getTimeInMillis();
            int current_hour = rightNow.get(Calendar.HOUR_OF_DAY);
            int current_minute = rightNow.get(Calendar.MINUTE);


            long difference=rightNow_in_millis-connection_request_start_date_in_millis;
            long days = difference / (24 * 60 * 60 * 1000);

            holder.tv_connection_request_message.setText(first_name+context.getResources().getString(R.string.connection_request_message));
            holder.tv_connection_request_delivery_time.setText(days+context.getResources().getString(R.string.days_ago));

        }



    }


    @Override
    public int getItemCount() {
        if(no_connection_request){
            return 1;
        }else{

        }
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
