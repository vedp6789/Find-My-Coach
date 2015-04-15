package com.findmycoach.app.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.ChatWidgetActivity;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.beans.requests.Data;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by IgluLabs on 1/22/2015.
 */
public class ConnectionAdapter extends BaseAdapter implements Callback {

    private Context context;
    private List<Data> connectionList;
    public int connection_clicked = -1;
    private ProgressDialog progressDialog;

    private static final String TAG = "FMC";

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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.connection_single_row_mentor, null);
        RelativeLayout singleRow = (RelativeLayout) view.findViewById(R.id.singleRow);

        singleRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("pending") || status.equals("broken"))
                    return;
                Intent chatWidgetIntent = new Intent(context, ChatWidgetActivity.class);
                if(DashboardActivity.dashboardActivity.user_group == 3){
                    chatWidgetIntent.putExtra("receiver_id", singleConnection.getOwnerId()+"");
                    chatWidgetIntent.putExtra("receiver_name", singleConnection.getOwnerName());
                    chatWidgetIntent.putExtra("receiver_image", singleConnection.getOwnerImage());
                    context.startActivity(chatWidgetIntent);
                }else if(DashboardActivity.dashboardActivity.user_group == 2){
                    chatWidgetIntent.putExtra("receiver_id", singleConnection.getInviteeId()+"");
                    chatWidgetIntent.putExtra("receiver_name", singleConnection.getInviteeName());
                    chatWidgetIntent.putExtra("receiver_image", singleConnection.getInviteeImage());
                    context.startActivity(chatWidgetIntent);
                }
            }
        });
        ImageView imageView = (ImageView) view.findViewById(R.id.studentImageView);
        TextView nameTV = (TextView) view.findViewById(R.id.nameTV);
        TextView lastMsgTV = (TextView) view.findViewById(R.id.lastMsgTV);
        ImageButton connectionButton = (ImageButton) view.findViewById(R.id.detailsTV);

        /** Called to show respective row for mentee and mentor */
        populateSingleRow(imageView, nameTV, lastMsgTV, connectionButton, DashboardActivity.dashboardActivity.user_group, singleConnection, position);
        return view;
    }

    private void populateSingleRow(ImageView imageView, TextView nameTV, TextView lastMsgTV, ImageButton connectionButton, int user_group, final Data singleConnection, final int position ){
        final String status = singleConnection.getStatus();
        lastMsgTV.setText("Receiver id : " + singleConnection.getInviteeId() + ", Sender id : " + singleConnection.getOwnerId() + ", Status : " + status);
        switch (user_group){
            case 3:

                Picasso.with(context)
                        .load(singleConnection.getOwnerImage())
                        .placeholder(R.drawable.user_icon)
                        .error(R.drawable.user_icon)
                        .into(imageView);

                nameTV.setText(singleConnection.getOwnerName());

                if(status.equals("accepted")){
                    connectionButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
                }else if(status.equals("pending")){
                    connectionButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_add));
                }else{
                    connectionButton.setVisibility(View.GONE);
                }

                connectionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(status.equals("accepted")) {
                            connection_clicked = position;
                            disconnect(singleConnection.getId() + "", singleConnection.getOwnerId());
                        }
                        else if(status.equals("pending")) {
                            connection_clicked = position;
                            connect(singleConnection.getId() + "");
                        }
                    }
                });
                break;

            case 2:

                Picasso.with(context)
                        .load(singleConnection.getInviteeImage())
                        .placeholder(R.drawable.user_icon)
                        .error(R.drawable.user_icon)
                        .into(imageView);

                nameTV.setText(singleConnection.getInviteeName());

                if(status.equals("accepted")){
                    connectionButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
                }else if(status.equals("pending")){
                connectionButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_notification_clear_all));
                }else{
                    connectionButton.setVisibility(View.GONE);
                }

                connectionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(status.equals("accepted") || status.equals("pending")) {
                            connection_clicked = position;
                            disconnect(singleConnection.getId() + "", singleConnection.getInviteeId());
                        }
                    }
                });
                break;
        }
    }

    private void connect(String connectionId) {
        Log.e(TAG,"Connect");
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", connectionId);
        requestParams.add("status", "accepted");
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.respondToConnectionRequest(context, requestParams, this, 18);
    }

    private void disconnect(String connectionId, int oppositeUSerId) {
        Log.e(TAG,"Disconnect");
        progressDialog.show();
        Log.d(TAG, "id : " + connectionId + ", user_id : " + oppositeUSerId +
                ", user_group : " + DashboardActivity.dashboardActivity.user_group);
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", connectionId);
        requestParams.add("user_id", oppositeUSerId+"");
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.breakConnection(context, requestParams, this, 21);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        Log.e(TAG,object + " : " + connection_clicked);
        progressDialog.dismiss();
        if(object != null && connection_clicked != -1){
            String status = connectionList.get(connection_clicked).getStatus();
            // Updating status of connection i.e. accept/reject/broke
            if(DashboardActivity.dashboardActivity.user_group == 3) {
                Log.e(TAG, status + ", Position : " + connection_clicked);
                connectionList.get(connection_clicked).setStatus(status.equals("accepted") ? "broken" : "accepted");
                try{
                    connectionList.get(connection_clicked).setId(Integer.parseInt(status));
                }catch (Exception e){
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                }
            }
            else if(DashboardActivity.dashboardActivity.user_group == 2)
                connectionList.get(connection_clicked).setStatus("broken");

            if(connectionList.get(connection_clicked).getStatus().trim().equalsIgnoreCase("broken"))
                connectionList.remove(connection_clicked);
            connection_clicked = -1;
            this.notifyDataSetChanged();
        }

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(context,(String) object,Toast.LENGTH_LONG).show();
    }
}
