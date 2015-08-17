package com.findmycoach.app.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.beans.search.Datum;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.MetaData;
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
    private int clickedPosition = -1;

    private static final String TAG = "FMC";

    public MentorListAdapter(Context context, List<Datum> users, String searchFor) {
        this.context = context;
        this.users = users;
        studentId = StorageHelper.getUserDetails(context, "user_id");
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

            ViewHolder holder = new ViewHolder();
            holder.userImage = (ImageView) view.findViewById(R.id.mentor_image);
            holder.nameTV = (TextView) view.findViewById(R.id.mentor_name);
            holder.ageTV = (TextView) view.findViewById(R.id.age);
            holder.daysTV = (TextView) view.findViewById(R.id.daysTV);
            holder.noOfStudentsTV = (TextView) view.findViewById(R.id.numberOfStudents);
            holder.ratingTV = (TextView) view.findViewById(R.id.rating);
            holder.distanceTV = (TextView) view.findViewById(R.id.distanceTV);
            holder.experienceTV = (TextView) view.findViewById(R.id.experience);
            holder.chargesTV = (TextView) view.findViewById(R.id.charges);
            holder.imageConnect = (ImageView) view.findViewById(R.id.connect_mentor);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        final Datum user = users.get(position);


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
            holder.daysTV.setText(Html.fromHtml(daysAsString));
        } catch (Exception ignored) {
        }


        try {
            int charges = Integer.parseInt(user.getChargesClass());
            String currency = MetaData.getCurrencySymbol(MetaData.countryCode(context), context);
            if (currency.equals(""))
                holder.chargesTV.setText(charges == 0 ? user.getChargesHour() + "/hr" : charges + "/cl");
            else
                holder.chargesTV.setText(Html.fromHtml(currency + " " + (charges == 0 ? user.getChargesHour() + "/hr" : charges + "/cl")));
        } catch (Exception ignored) {
        }


        holder.nameTV.setText(user.getFirstName());
        try {
            holder.distanceTV.setText(String.format("%.1f", Double.parseDouble(user.getDistance())) + " km");
        } catch (Exception e) {
        }
        try {
            holder.ratingTV.setText(user.getRating());
        } catch (Exception e) {
        }
        if (user.getPhotograph() != null && !(user.getPhotograph()).equals("")) {
            Picasso.with(context)
                    .load((String) user.getPhotograph())
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.user_icon)
                    .into(holder.userImage);
        }
        if (user.getConnectionStatus() != null && !user.getConnectionStatus().equals("broken") && !user.getConnectionStatus().equals("rejected")) {
            if (user.getConnectionStatus().equals("accepted") || user.getConnectionStatus().contains("mentor_mentee")) {
                holder.imageConnect.setImageDrawable(context.getResources().getDrawable(R.drawable.disconnect));
            } else if (user.getConnectionStatus().equals("pending")) {
                holder.imageConnect.setImageDrawable(context.getResources().getDrawable(R.drawable.pending));
            }
            holder.imageConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedPosition = position;
                    showDisconnectDialog(user.getConnectionId(), user.getId());
                }
            });
        } else {
            holder.imageConnect.setImageDrawable(context.getResources().getDrawable(R.drawable.connect));
            holder.imageConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedPosition = position;
                    showAlert(user.getId());
                }
            });
        }
        return view;
    }

    private void showDisconnectDialog(final String connectionId, final String id) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.disconnect_confirmation_dialog);

        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect(connectionId, id);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void showAlert(final String userId) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.send_connection_request_dialog);
        final EditText editText = (EditText) dialog.findViewById(R.id.editText);
        final Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        Button okButton = (Button) dialog.findViewById(R.id.okButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                if (message.trim().length() < 1)
                    message = context.getResources().getString(R.string.connection_request_msg);
                sendConnectionRequest(message, userId);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void sendConnectionRequest(String message, String userId) {
        Log.d(TAG, "\n" + message + "\nMentor id : " + userId + "\nStudent id : " + studentId);
        RequestParams requestParams = new RequestParams();
        requestParams.add("owner", studentId);
        requestParams.add("invitee", userId);
        requestParams.add("message", message);
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.sendConnectionRequest(context, requestParams, this, 17);
    }

    private void disconnect(String connectionId, String oppositeUSerId) {
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
        Toast.makeText(context, (String) object, Toast.LENGTH_LONG).show();
    }

    private class ViewHolder {
        ImageView userImage;
        TextView nameTV;
        TextView ageTV;
        TextView daysTV;
        TextView noOfStudentsTV;
        TextView ratingTV;
        TextView distanceTV;
        TextView experienceTV;
        TextView chargesTV;
        ImageView imageConnect;
    }
}
