package com.findmycoach.mentor.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.mentor.activity.ChatWidgetActivity;
import com.findmycoach.mentor.beans.requests.Data;
import com.findmycoach.mentor.R;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.NetworkClient;
import com.loopj.android.http.RequestParams;

import java.util.List;

/**
 * Created by IgluLabs on 1/22/2015.
 */
public class ConnectionAdapter extends BaseAdapter implements Callback {

    private Context context;
    private List<Data> connectionList;
    public int connection_clicked = -1;
    private ProgressDialog progressDialog;

    public ConnectionAdapter(Context context, List<Data> connectionList) {
        this.context = context;
        this.connectionList = connectionList;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Data singleConnection = connectionList.get(position);
        final String status = singleConnection.getStatus();
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.connection_single_row, null);
        }
        RelativeLayout singleRow = (RelativeLayout) view.findViewById(R.id.singleRow);

        singleRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("pending") || status.equals("broken"))
                    return;

                Intent chatWidgetIntent = new Intent(context, ChatWidgetActivity.class);
                chatWidgetIntent.putExtra("student_id", singleConnection.getOwnerId()+"");
                chatWidgetIntent.putExtra("student_name", singleConnection.getOwnerName());
                context.startActivity(chatWidgetIntent);
            }
        });
        TextView nameTV = (TextView) view.findViewById(R.id.nameTV);
        TextView lastMsgTV = (TextView) view.findViewById(R.id.lastMsgTV);
        ImageButton connectionButton = (ImageButton) view.findViewById(R.id.detailsTV);
        nameTV.setText(singleConnection.getOwnerName());
        lastMsgTV.setText("  Created on : " + singleConnection.getCreatedOn());
        if(status.equals("accepted")){
            connectionButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.btn_minus));
        }else if(status.equals("pending")){
            connectionButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.btn_plus));
        }else{
            connectionButton.setVisibility(View.GONE);
        }

        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("accepted")) {
                    connection_clicked = position;
                    disconnect(singleConnection.getId() + "");
                }
                else if(status.equals("pending")) {
                    connection_clicked = position;
                    connect(singleConnection.getId() + "");
                }
            }
        });

        return view;
    }

    private void connect(String connectionId) {
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", connectionId);
        requestParams.add("status", "accepted");
        NetworkClient.respondToConnectionRequest(context, requestParams, this);
    }

    private void disconnect(String connectionId) {
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", connectionId);
        NetworkClient.breakConnection(context, requestParams, this);
    }

    @Override
    public void successOperation(Object object) {
        progressDialog.dismiss();
        if(object == null && connection_clicked != -1){
            String status = connectionList.get(connection_clicked).getStatus();
            connectionList.get(connection_clicked).setStatus(status.equals("accepted") ? "broken" : "accepted");
            this.notifyDataSetChanged();
        }

    }

    @Override
    public void failureOperation(Object object) {
        progressDialog.dismiss();
        Toast.makeText(context,(String) object,Toast.LENGTH_LONG).show();

    }
}
