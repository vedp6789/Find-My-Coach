package com.findmycoach.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.CalendarSchedule.EventDuration;
import com.findmycoach.app.beans.CalendarSchedule.Mentee;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;

import java.util.ArrayList;

/**
 * Created by ved on 12/6/15.
 */
public class MenteeList extends BaseAdapter {
    ArrayList<Mentee> mentees = null;
    ArrayList<Vacation> vacations= null;

    Context context;
    public MenteeList(Context context,ArrayList<Mentee> mentees){
      this.mentees = mentees;
        this.context = context;
    }
    public MenteeList(ArrayList<Vacation> vacations,Context context){
        this.vacations =vacations;
                this.context = context;
    }

    public MenteeList(Context context,ArrayList<Mentee> mentees,String no_use){
       this.mentees = mentees;
        this.context = context;
    }
    @Override
    public int getCount() {
        if(mentees != null){
            return mentees.size();

        }else {
            return vacations.size();

        }
    }

    @Override
    public Object getItem(int position) {
        if(mentees != null){
            return mentees.get(position);

        }else{
            return vacations.get(position);

        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenteeViewHolder menteeViewHolder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.mentee_list,parent,false);
            menteeViewHolder = new MenteeViewHolder();


            menteeViewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_mentee_name);
            menteeViewHolder.tv_subject = (TextView) convertView.findViewById(R.id.tv_subject);
            menteeViewHolder.tv_start_date = (TextView) convertView.findViewById(R.id.tv_start_date);
            menteeViewHolder.tv_stop_date = (TextView) convertView.findViewById(R.id.tv_stop_date);
            convertView.setTag(menteeViewHolder);
        }else{
            menteeViewHolder = (MenteeViewHolder) convertView.getTag();
        }



            if(mentees != null){
                if(mentees.size()> 0){
                    Mentee mentee= mentees.get(position);
                    menteeViewHolder.tv_name.setText(mentee.getFirst_name()+"\t"+mentee.getLast_name().trim());
                    menteeViewHolder.tv_subject.setVisibility(View.GONE);
                    menteeViewHolder.tv_start_date.setVisibility(View.GONE);
                    menteeViewHolder.tv_stop_date.setVisibility(View.GONE);
                }

            }else{

                Vacation vacation= vacations.get(position);
                menteeViewHolder.tv_start_date.setText(String.format(String.format("%02d-%02d-%d", Integer.parseInt(vacation.getStart_date().split("-")[2]), Integer.parseInt(vacation.getStart_date().split("-")[1]), Integer.parseInt(vacation.getStart_date().split("-")[0]))));
                menteeViewHolder.tv_stop_date.setText(String.format(String.format("%02d-%02d-%d", Integer.parseInt(vacation.getStop_date().split("-")[2]), Integer.parseInt(vacation.getStop_date().split("-")[1]), Integer.parseInt(vacation.getStop_date().split("-")[0]))));
                menteeViewHolder.tv_name.setVisibility(View.GONE);
                menteeViewHolder.tv_subject.setVisibility(View.GONE);
            }



        return convertView;


    }

    class MenteeViewHolder{
        TextView tv_name;
        TextView tv_subject;
        TextView tv_start_date;
        TextView tv_stop_date;

    }

}
