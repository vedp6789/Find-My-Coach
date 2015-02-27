package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.connections.Data;

import java.util.List;

/**
 * Created by IgluLabs on 1/22/2015.
 */
public class ConnectionAdapterMentee extends BaseAdapter {

    private Context context;
    private List<Data> connectionList;

    public ConnectionAdapterMentee(Context context, List<Data> connectionList) {
        this.context = context;
        this.connectionList = connectionList;
    }

    @Override
    public int getCount() {
        return connectionList.size();
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
        Data singleConnection = connectionList.get(position);
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.connection_single_row_mentee, null);
        }
        TextView nameTV = (TextView) view.findViewById(R.id.nameTV);
        TextView lastMsgTV = (TextView) view.findViewById(R.id.lastMsgTV);
        TextView detailsTV = (TextView) view.findViewById(R.id.detailsTV);

        nameTV.setText(singleConnection.getInviteeName());
        lastMsgTV.setText(context.getResources().getString(R.string.created_on) + singleConnection.getCreatedOn());
        detailsTV.setText(singleConnection.getStatus());

        return view;
    }
}