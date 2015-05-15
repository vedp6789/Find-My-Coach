package com.findmycoach.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by ved on 15/5/15.
 */
public class MenteeNotificationRecyclerViewAdapter extends RecyclerView.Adapter<MenteeNotificationRecyclerViewAdapter.ViewHolder> {
private Context context;
    private JSONArray jsonArray_notifications;
    boolean notification_not_found;
    public MenteeNotificationRecyclerViewAdapter(Context context,JSONArray jsonArray_notifications){
        notification_not_found=false;
        this.context=context;
        this.jsonArray_notifications=jsonArray_notifications;
        if(jsonArray_notifications.length() <=0){
            notification_not_found=true;
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.mentee_notification,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(notification_not_found){
            holder.tv_message.setText(context.getResources().getString(R.string.no_notification_found));
        }else{
            try {
                JSONObject jsonObject_notification=jsonArray_notifications.getJSONObject(position);
                String title=jsonObject_notification.getString("title");
                String first_name=jsonObject_notification.getString("first_name");
                String start_date=jsonObject_notification.getString("start_date");

                Calendar cal = new GregorianCalendar();
                cal.set(Integer.parseInt(start_date.split("-")[0]),Integer.parseInt(start_date.split("-")[1])-1, Integer.parseInt(start_date.split("-")[2]));
                long connection_request_start_date_in_millis = cal.getTimeInMillis();


                Calendar rightNow = Calendar.getInstance();
                long rightNow_in_millis = rightNow.getTimeInMillis();
                int current_hour = rightNow.get(Calendar.HOUR_OF_DAY);
                int current_minute = rightNow.get(Calendar.MINUTE);


                long difference=rightNow_in_millis-connection_request_start_date_in_millis;
                long days = difference / (24 * 60 * 60 * 1000);
                switch(title){
                    case "Connection accepted":
                        holder.tv_message.setText(first_name+context.getResources().getString(R.string.connection_request_accepted));
                        holder.tv_message.setText(days+context.getResources().getString(R.string.days_ago));
                        break;
                    case "Connection rejected":
                        holder.tv_message.setText(first_name+context.getResources().getString(R.string.connection_request_rejected));
                        holder.tv_message.setText(days+context.getResources().getString(R.string.days_ago));
                        break;
                    case "Schedule accepted":
                        holder.tv_message.setText(first_name+context.getResources().getString(R.string.schedule_request_accepted));
                        holder.tv_message.setText(days+context.getResources().getString(R.string.days_ago));
                        break;
                    case "Schedule rejected":
                        holder.tv_message.setText(first_name+context.getResources().getString(R.string.schedule_request_rejected));
                        holder.tv_message.setText(days+context.getResources().getString(R.string.days_ago));
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if(jsonArray_notifications.length() <=0){
            return 1;
        }else{
            return jsonArray_notifications.length();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
    TextView tv_message, tv_arrival_durations;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_message= (TextView) itemView.findViewById(R.id.tv_mentee_notification_message);
            tv_arrival_durations= (TextView) itemView.findViewById(R.id.tv_mentee_message_duration);
        }
    }


}
