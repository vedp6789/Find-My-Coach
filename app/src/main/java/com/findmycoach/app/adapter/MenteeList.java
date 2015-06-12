package com.findmycoach.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.CalendarSchedule.Mentee;

import java.util.ArrayList;

/**
 * Created by ved on 12/6/15.
 */
public class MenteeList extends BaseAdapter {
    ArrayList<Mentee> mentees;
    Context context;
    public MenteeList(Context context,ArrayList<Mentee> mentees){
      this.mentees = mentees;
        this.context = context;
    }
    @Override
    public int getCount() {
        return mentees.size();
    }

    @Override
    public Object getItem(int position) {
      return mentees.get(position);
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

            convertView.setTag(menteeViewHolder);
        }else{
            menteeViewHolder = (MenteeViewHolder) convertView.getTag();
        }

        if(mentees.size() > 0){
            Mentee mentee= mentees.get(position);
            menteeViewHolder.tv_name.setText(mentee.getFirst_name()+"\t"+mentee.getLast_name().trim());
            menteeViewHolder.tv_subject.setText("");
        }

        return convertView;


    }

    class MenteeViewHolder{
        TextView tv_name;
        TextView tv_subject;


    }

}
