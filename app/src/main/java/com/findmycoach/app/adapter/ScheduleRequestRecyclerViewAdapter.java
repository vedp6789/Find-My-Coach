package com.findmycoach.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.UserNotifications.ScheduleRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by ved on 14/5/15.
 */
public class ScheduleRequestRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRequestRecyclerViewAdapter.ViewHolder>{

    private ArrayList<ScheduleRequest> scheduleRequests;
    private Context context;
    private boolean no_schedule_request;
    private long days;
    public ScheduleRequestRecyclerViewAdapter(Context context, ArrayList<ScheduleRequest> scheduleRequests) {
       this.context=context;
       this.scheduleRequests=scheduleRequests;
       no_schedule_request=false;
        if(scheduleRequests.size() <=0){
            no_schedule_request=true;
        }

    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout relativeLayout;
        TextView tv_schedule_request_message;
        TextView tv_schedule_request_date_info;

        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout= (RelativeLayout) itemView.findViewById(R.id.rl_schedule_request_notification);
            tv_schedule_request_message= (TextView) itemView.findViewById(R.id.tv_schedule_request_message);
            tv_schedule_request_date_info= (TextView) itemView.findViewById(R.id.tv_how_old_schedule_request);

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_request_notification,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(no_schedule_request){
            Log.d("FMC", "no schedule request message !");
            holder.tv_schedule_request_message.setText(context.getResources().getString(R.string.no_schedule_request));
        }else{
            String first_name=scheduleRequests.get(position).getFirst_name();
            String start_date=scheduleRequests.get(position).getStart_date();
            String start_time=scheduleRequests.get(position).getStart_time();
            String status=scheduleRequests.get(position).getStatus();

            if(status.equals("read"))
                holder.relativeLayout.setBackgroundColor(Color.LTGRAY);

            Calendar cal = new GregorianCalendar();
            cal.set(Integer.parseInt(start_date.split("-")[0]),Integer.parseInt(start_date.split("-")[1])-1, Integer.parseInt(start_date.split("-")[2]));
            long connection_request_start_date_in_millis = cal.getTimeInMillis();


            Calendar rightNow = Calendar.getInstance();
            long rightNow_in_millis = rightNow.getTimeInMillis();
            int current_hour = rightNow.get(Calendar.HOUR_OF_DAY);
            int current_minute = rightNow.get(Calendar.MINUTE);


            long difference=rightNow_in_millis-connection_request_start_date_in_millis;
            days = difference / (24 * 60 * 60 * 1000);

            holder.tv_schedule_request_message.setText(first_name + context.getResources().getString(R.string.schedule_request_message));
            actionDay(holder);

        }

    }

    @Override
    public int getItemCount() {
        if(no_schedule_request){
            return 1;
        }else {
            return scheduleRequests.size();
        }
    }


    private void actionDay(ViewHolder holder) {
        if(days == 0){
            holder.tv_schedule_request_date_info.setText(context.getResources().getString(R.string.today));
        }else{
            if(days == 1){
                holder.tv_schedule_request_date_info.setText(context.getResources().getString(R.string.yesterday));
            }else{
                holder.tv_schedule_request_date_info.setText(days+context.getResources().getString(R.string.days_ago));
            }
        }
    }




}
