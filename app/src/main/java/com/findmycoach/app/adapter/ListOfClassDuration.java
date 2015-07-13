package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.CalendarSchedule.EventDuration;
import com.findmycoach.app.beans.CalendarSchedule.Mentee;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;

import java.util.List;

/**
 * Created by ved on 12/6/15.
 */
public class ListOfClassDuration extends BaseAdapter {
    List<Mentee> mentees=null;
    List<EventDuration> eventDurations =null;
    List<Vacation> vacations=null;
    Context context;
    public ListOfClassDuration(Context context, List<Mentee> menteeList){
        this.mentees = menteeList;
        eventDurations = mentees.get(0).getEventDurations();
        this.context = context;
    }
    public ListOfClassDuration(List<Vacation> vacations,Context context){
        this.vacations = vacations;
        this.context = context;
    }


    @Override
    public int getCount() {
        if(mentees != null){
            return eventDurations.size();

        }else{
            return vacations.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if(mentees != null)
            return eventDurations.get(position);
        else
            return vacations.get(position);

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DurationViewHolder menteeViewHolder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.mentee_list,parent,false);
            menteeViewHolder = new DurationViewHolder();

            menteeViewHolder.tv_name_field= (TextView) convertView.findViewById(R.id.nameField);
            menteeViewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_mentee_name);
            menteeViewHolder.tv_start_date = (TextView) convertView.findViewById(R.id.tv_start_date);
            menteeViewHolder.tv_stop_date = (TextView) convertView.findViewById(R.id.tv_stop_date);
            convertView.setTag(menteeViewHolder);
        }else{
            menteeViewHolder = (DurationViewHolder) convertView.getTag();
        }



        if(mentees != null){
            if(mentees.size()> 0){
                EventDuration eventDuration = eventDurations.get(position);
                menteeViewHolder.tv_name.setVisibility(View.GONE);
                menteeViewHolder.tv_name_field.setVisibility(View.GONE);
                menteeViewHolder.tv_start_date.setText(String.format(String.format("%02d-%02d-%d", Integer.parseInt(eventDuration.getStart_date().split("-")[2]), Integer.parseInt(eventDuration.getStart_date().split("-")[1]), Integer.parseInt(eventDuration.getStart_date().split("-")[0]))));
                menteeViewHolder.tv_stop_date.setText(String.format(String.format("%02d-%02d-%d", Integer.parseInt(eventDuration.getStop_date().split("-")[2]), Integer.parseInt(eventDuration.getStop_date().split("-")[1]), Integer.parseInt(eventDuration.getStop_date().split("-")[0]))));
            }

        }else{

            Vacation vacation= vacations.get(position);
            menteeViewHolder.tv_start_date.setText(String.format(String.format("%02d-%02d-%d", Integer.parseInt(vacation.getStart_date().split("-")[2]), Integer.parseInt(vacation.getStart_date().split("-")[1]), Integer.parseInt(vacation.getStart_date().split("-")[0]))));
            menteeViewHolder.tv_stop_date.setText(String.format(String.format("%02d-%02d-%d", Integer.parseInt(vacation.getStop_date().split("-")[2]), Integer.parseInt(vacation.getStop_date().split("-")[1]), Integer.parseInt(vacation.getStop_date().split("-")[0]))));
            menteeViewHolder.tv_name.setVisibility(View.GONE);
            menteeViewHolder.tv_name_field.setVisibility(View.GONE);
        }



        return convertView;
    }

    class DurationViewHolder{
        TextView tv_name_field;
        TextView tv_name;
        TextView tv_start_date;
        TextView tv_stop_date;
    }

}
