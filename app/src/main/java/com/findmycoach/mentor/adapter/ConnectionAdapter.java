package com.findmycoach.mentor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.mentor.beans.requests.Data;
import com.findmycoach.mentor.R;

import java.util.List;

/**
 * Created by IgluLabs on 1/22/2015.
 */
public class ConnectionAdapter extends BaseAdapter {

    private Context context;
    private List<Data> connectionList;

    public ConnectionAdapter(Context context, List<Data> connectionList) {
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
            view = inflater.inflate(R.layout.connection_single_row, null);
        }
        TextView nameTV = (TextView) view.findViewById(R.id.nameTV);
        TextView lastMsgTV = (TextView) view.findViewById(R.id.lastMsgTV);
        ImageButton connectionButton = (ImageButton) view.findViewById(R.id.detailsTV);
        nameTV.setText(singleConnection.getOwnerName());
        lastMsgTV.setText("  Created on : " + singleConnection.getCreatedOn());

        final String status = singleConnection.getStatus();
        if(status.equals("accepted")){
            connectionButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.btn_minus));
        }else if(status.equals("pending")){
            connectionButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.btn_plus));
        }else if(status.equals("broken")){
            connectionButton.setVisibility(View.GONE);
        }

        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Connection will " + (status.equals("accepted") ? "disconnected" : "accepted"), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
