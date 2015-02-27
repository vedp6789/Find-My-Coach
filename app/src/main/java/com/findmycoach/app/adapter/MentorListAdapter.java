package com.findmycoach.app.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.UserListActivity;
import com.findmycoach.app.beans.search.Datum;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by prem on 21/1/15.
 */
public class MentorListAdapter extends BaseAdapter {
    private Context context;
    private List<Datum> users;
    private String studentId;
    private UserListActivity userListActivity;
    private ProgressDialog progressDialog;

    private static final String TAG="FMC";

    public MentorListAdapter(Context context, List<Datum> users, UserListActivity userListActivity, ProgressDialog progressDialog) {
        this.context = context;
        this.users = users;
        this.userListActivity = userListActivity;
        this.progressDialog = progressDialog;
        studentId =  StorageHelper.getUserDetails(context, "user_id");
    }

    @Override
    public int getCount() {
        return users.size();
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
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.user_list_item, null);
        }
        ImageView image = (ImageView) view.findViewById(R.id.mentor_image);
        ImageView imageConnect = (ImageView) view.findViewById(R.id.connect_mentor);
        RatingBar rating = (RatingBar) view.findViewById(R.id.mentor_rating);
        TextView name = (TextView) view.findViewById(R.id.mentor_name);
        final Datum user = users.get(position);
        name.setText(user.getFirstName() + " " + user.getLastName());
//            rating.setRating(user.get);
        if (user.getPhotograph() != null && !( user.getPhotograph()).equals("")) {
            Picasso.with(context)
                    .load((String) user.getPhotograph()).resize(150, 150)
                    .placeholder(R.drawable.user_icon).resize(150, 150)
                    .error(R.drawable.user_icon).resize(150, 150)
                    .into(image);
        }
        imageConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert(user.getId());
            }
        });
        return view;
    }


    private void showAlert(final String userId) {
        final String defaultMessage = context.getResources().getString(R.string.connection_request_msg);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getResources().getString(R.string.connection_request));
        alertDialog.setMessage(context.getResources().getString(R.string.enter_msg));
        final EditText input = new EditText(context);
        input.setHint(defaultMessage);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(8, 8, 8, 8);
        input.setLayoutParams(params);
        alertDialog.setView(input);
        input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.custom_edittext));
        alertDialog.setPositiveButton(context.getResources().getString(R.string.send),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String message = input.getText().toString();
                        if(message.trim().length() < 1)
                            message = defaultMessage;
                        sendConnectionRequest(message, userId);
                    }
                }
        );

        alertDialog.setNegativeButton(context.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        alertDialog.show();
    }

    private void sendConnectionRequest(String message, String userId) {
        progressDialog.show();
        Log.d(TAG,"\n" + message + "\nMentor id : " + userId + "\nStudent id : " + studentId);
        RequestParams requestParams = new RequestParams();
        requestParams.add("owner", studentId);
        requestParams.add("invitee", userId);
        requestParams.add("message", message);
        NetworkClient.sendConnectionRequest(context, requestParams, userListActivity);
    }
}
