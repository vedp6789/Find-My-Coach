package com.findmycoach.mentor.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmc.mentor.findmycoach.R;

import java.util.ArrayList;

/**
 * Created by IgluLabs on 1/22/2015.
 */
public class ConnectionAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> connectionList;

    public ConnectionAdapter(Context context, ArrayList<String> connectionList) {
        super(context, R.layout.connection_single_row, connectionList);
        this.context = context;
        this.connectionList = connectionList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ViewHolder holder = null;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.connection_single_row, parent, false);
            holder = new ViewHolder();
            holder.studentIv = (ImageView) rowView.findViewById(R.id.studentImageView);
            holder.studentNameTv = (TextView) rowView.findViewById(R.id.studentNameTV);
            holder.studentMsgTv = (TextView) rowView.findViewById(R.id.lastMsgTV);
            holder.studentDetailsTv = (TextView) rowView.findViewById(R.id.detailsTV);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        populateData(holder, position);

        return rowView;
    }

    private void populateData(ViewHolder holder, int position) {
        holder.studentNameTv.setText(connectionList.get(position));
        holder.studentMsgTv.setText("Last message sent by " + connectionList.get(position));
        if(position%3 == 0){
            holder.studentDetailsTv.setText("Online");
            holder.studentDetailsTv.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }else{
            holder.studentDetailsTv.setText("Offline");
            holder.studentDetailsTv.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private class ViewHolder{
        private ImageView studentIv;
        private TextView studentNameTv;
        private TextView studentMsgTv;
        private TextView studentDetailsTv;
    }
}
