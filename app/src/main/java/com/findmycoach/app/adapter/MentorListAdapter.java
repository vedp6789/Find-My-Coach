package com.findmycoach.app.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.text.Html;
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
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.beans.search.Datum;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by prem on 21/1/15.
 */
public class MentorListAdapter extends BaseAdapter implements Callback {
    private Context context;
    private List<Datum> users;
    private String studentId;
    private ProgressDialog progressDialog;
    private int clickedPosition = -1;
    private String searchFor = "";

    private static final String TAG = "FMC";

    public MentorListAdapter(Context context, List<Datum> users, ProgressDialog progressDialog, String searchFor) {
        this.context = context;
        this.users = users;
        this.progressDialog = progressDialog;
        studentId = StorageHelper.getUserDetails(context, "user_id");
        try {
            this.searchFor = searchFor.split("-")[0];
        } catch (Exception e) {
            this.searchFor = "";
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.user_list_item, null);
        }
        final Datum user = users.get(position);

        TextView nameTV = (TextView) view.findViewById(R.id.mentor_name);
        TextView chargesTV = (TextView) view.findViewById(R.id.chargesTV);
        TextView subCategoryTV = (TextView) view.findViewById(R.id.subCatTV);
        TextView daysTV = (TextView) view.findViewById(R.id.daysTV);
        TextView distanceTV = (TextView) view.findViewById(R.id.distanceTV);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.mentor_rating);
        ImageView imageView = (ImageView) view.findViewById(R.id.mentor_image);
        final ImageView imageConnect = (ImageView) view.findViewById(R.id.connect_mentor);


        try {
            ArrayList<String> days = new ArrayList<>();
            Collections.addAll(days, user.getAvailableDays().split(","));
            String daysAsString = "";
            String[] daysArray = {"M", "T", "W", "Th", "F", "S", "Su"};
            String fontPurple = "<font color='#392366'>";
            String fontPurpleLight = "<font color='#AFA4C4'>";
            String fontEnd = "</font>";
            for (String d : daysArray) {
                if (days.contains(d)) {
                    daysAsString = daysAsString + " " + fontPurple + d + fontEnd;
                } else
                    daysAsString = daysAsString + " " + fontPurpleLight + d + fontEnd;
            }
            daysTV.setText(Html.fromHtml(daysAsString));
        } catch (Exception ignored) {
        }


        try {
            int charges = user.getChargesClass();
            chargesTV.setText(charges == 0 ? user.getChargesHour() + "/hr" : charges + "/cl");
        } catch (Exception ignored) {
        }


        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(context.getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(context.getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(context.getResources().getColor(R.color.purple_light), PorterDuff.Mode.SRC_ATOP);

        nameTV.setText(user.getFirstName());
        subCategoryTV.setText(searchFor);
        try {
            distanceTV.setText(String.format("%.2f", Double.parseDouble(user.getDistance())) + " km");
        } catch (Exception e) {
            distanceTV.setText("");
        }
        try {
            ratingBar.setRating(Float.parseFloat(user.getRating()));
        } catch (Exception e) {
            ratingBar.setRating(0);
        }
        if (user.getPhotograph() != null && !(user.getPhotograph()).equals("")) {
            Picasso.with(context)
                    .load((String) user.getPhotograph())
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.user_icon)
                    .into(imageView);
        }
        if (user.getConnectionStatus() != null && !user.getConnectionStatus().equals("broken") && !user.getConnectionStatus().equals("rejected")) {
            if (user.getConnectionStatus().equals("accepted") || user.getConnectionStatus().contains("mentor_mentee")) {
                imageConnect.setImageDrawable(context.getResources().getDrawable(R.drawable.disconnect));
            } else if (user.getConnectionStatus().equals("pending")) {
                imageConnect.setImageDrawable(context.getResources().getDrawable(R.drawable.pending));
            }
            imageConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedPosition = position;
                    disconnect(user.getConnectionId(), user.getId());
                }
            });
        } else {
            imageConnect.setImageDrawable(context.getResources().getDrawable(R.drawable.connect));
            imageConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedPosition = position;
                    showAlert(user.getId());
                }
            });
        }
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
                        if (message.trim().length() < 1)
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
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void sendConnectionRequest(String message, String userId) {
        progressDialog.show();
        Log.d(TAG, "\n" + message + "\nMentor id : " + userId + "\nStudent id : " + studentId);
        RequestParams requestParams = new RequestParams();
        requestParams.add("owner", studentId);
        requestParams.add("invitee", userId);
        requestParams.add("message", message);
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.sendConnectionRequest(context, requestParams, this, 17);
    }

    private void disconnect(String connectionId, String oppositeUSerId) {
        progressDialog.show();
        Log.d(TAG, "id : " + connectionId + ", user_id : " + oppositeUSerId +
                ", user_group : " + DashboardActivity.dashboardActivity.user_group);
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", connectionId);
        requestParams.add("user_id", oppositeUSerId);
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.breakConnection(context, requestParams, this, 21);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        if (clickedPosition != -1) {
            if (calledApiValue == 17) {
                users.get(clickedPosition).setConnectionStatus("pending");
                String status = object + "";
                try {
                    users.get(clickedPosition).setConnectionId(Integer.parseInt(status) + "");
                } catch (Exception e) {
                    Toast.makeText(context, context.getResources().getString(R.string.success), Toast.LENGTH_LONG).show();
                }
            } else if (calledApiValue == 21) {
                users.get(clickedPosition).setConnectionStatus("broken");
                Toast.makeText(context, (String) object, Toast.LENGTH_LONG).show();
            }
            clickedPosition = -1;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(context, (String) object, Toast.LENGTH_LONG).show();
    }
}
