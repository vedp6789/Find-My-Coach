package com.findmycoach.app.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.search.Datum;
import com.findmycoach.app.util.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by prem on 21/1/15.
 */
public class MentorListAdapter extends BaseAdapter implements Callback {
    private Context context;
    private List<Datum> users;
    private String year, years;
    private int clickedPosition = -1;
    private Drawable defaultDrawable, qualified, notQualified;

    public MentorListAdapter(Context context, List<Datum> users, String searchFor) {
        this.context = context;
        this.users = users;
        defaultDrawable = context.getResources().getDrawable(R.drawable.user_icon);
        qualified = context.getResources().getDrawable(R.drawable.radio_btn_selected);
        notQualified = context.getResources().getDrawable(R.drawable.radio_btn_unselected);
        year = context.getResources().getString(R.string.yr);
        years = context.getResources().getString(R.string.yrs);
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
            holder.userIV = (ImageView) view.findViewById(R.id.mentor_image);
            holder.nameTV = (TextView) view.findViewById(R.id.mentor_name);
            holder.ageTV = (TextView) view.findViewById(R.id.age);
            holder.daysTV = (TextView) view.findViewById(R.id.daysTV);
            holder.noOfStudentsTV = (TextView) view.findViewById(R.id.numberOfStudents);
            holder.ratingTV = (TextView) view.findViewById(R.id.rating);
            holder.distanceTV = (TextView) view.findViewById(R.id.distanceTV);
            holder.experienceTV = (TextView) view.findViewById(R.id.experience);
            holder.chargesTV = (TextView) view.findViewById(R.id.charges);
            holder.qualifiedIV = (ImageView) view.findViewById(R.id.imageView4);
//            holder.connectionIV = (ImageView) view.findViewById(R.id.connect_mentor);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        final Datum user = users.get(position);

        //Set Mentor image
        holder.userIV.setImageDrawable(defaultDrawable);
        if (user.getPhotograph() != null && !(user.getPhotograph()).equals("")) {
            Picasso.with(context)
                    .load((String) user.getPhotograph())
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.user_icon)
                    .into(holder.userIV);
        }

        //Set Mentor name
        try {
            holder.nameTV.setText(user.getFirstName().split(" ")[0]);
        } catch (Exception e) {
            holder.nameTV.setText(user.getFirstName());
        }

        //Set Mentor age
        try {
            int age = getAgeInyearsFromDOB(user.getDateOfBirth());
            if (age > 1)
                holder.ageTV.setText(age + years);
            else if (age == 0)
                holder.ageTV.setText(age + years);
        } catch (Exception e) {
            holder.ageTV.setText("");
        }

        //Set Mentor available days
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

        //Set Mentor current student count
        try {
            holder.noOfStudentsTV.setText(user.getNumberOfStudents());
        } catch (Exception e) {
        }

        //Set Mentor rating
        try {
            holder.ratingTV.setText(user.getRating());
        } catch (Exception e) {
        }

        //Set Mentor distance in KM
        try {
            holder.distanceTV.setText(String.format("%.1f", Double.parseDouble(user.getDistance())) + " km");
        } catch (Exception e) {
        }

        //Set Mentor Experience in years
        try {
            int experience = Integer.parseInt(user.getExperience());
            holder.experienceTV.setText(experience + (experience > 1 ? years : year));
        } catch (Exception e) {
            holder.experienceTV.setText("0" + year);
        }

        //Set Mentor is qualified or not in current subject
        try {
            if (user.isQualified())
                holder.qualifiedIV.setImageDrawable(qualified);
            else
                holder.qualifiedIV.setImageDrawable(notQualified);
        } catch (Exception e) {
        }

        //Set Mentor charges
        try {
            if (user.getPrice() != null)
                holder.chargesTV.setText(user.getPrice() + user.getPriceFor());
        } catch (Exception ignored) {
        }


//        //Set Connection button depending on current connection status
//        if (user.getConnectionStatus() != null && !user.getConnectionStatus().equals("broken") && !user.getConnectionStatus().equals("rejected")) {
//            if (user.getConnectionStatus().equals("accepted") || user.getConnectionStatus().contains("mentor_mentee")) {
//                holder.connectionIV.setImageDrawable(context.getResources().getDrawable(R.drawable.disconnect));
//            } else if (user.getConnectionStatus().equals("pending")) {
//                holder.connectionIV.setImageDrawable(context.getResources().getDrawable(R.drawable.pending));
//            }
//            holder.connectionIV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clickedPosition = position;
//                    showDisconnectDialog(user.getConnectionId(), user.getId());
//                }
//            });
//        } else {
//            holder.connectionIV.setImageDrawable(context.getResources().getDrawable(R.drawable.connect));
//            holder.connectionIV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clickedPosition = position;
//                    showAlert(user.getId());
//                }
//            });
//        }
        return view;
    }

//    private void showDisconnectDialog(final String connectionId, final String id) {
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.disconnect_confirmation_dialog);
//
//        dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                disconnect(connectionId, id);
//                dialog.dismiss();
//            }
//        });
//
//        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }


//    private void showAlert(final String userId) {
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.send_connection_request_dialog);
//        final EditText editText = (EditText) dialog.findViewById(R.id.editText);
//        final Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
//        Button okButton = (Button) dialog.findViewById(R.id.okButton);
//
//        okButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String message = editText.getText().toString();
//                if (message.trim().length() < 1)
//                    message = context.getResources().getString(R.string.connection_request_msg);
//                sendConnectionRequest(message, userId);
//                dialog.dismiss();
//            }
//        });
//
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.setCancelable(false);
//        dialog.show();
//    }

//    private void sendConnectionRequest(String message, String userId) {
//        Log.d(TAG, "\n" + message + "\nMentor id : " + userId + "\nStudent id : " + studentId);
//        RequestParams requestParams = new RequestParams();
//        requestParams.add("owner", studentId);
//        requestParams.add("invitee", userId);
//        requestParams.add("message", message);
//        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
//        NetworkClient.sendConnectionRequest(context, requestParams, this, 17);
//    }
//
//    private void disconnect(String connectionId, String oppositeUSerId) {
//        Log.d(TAG, "id : " + connectionId + ", user_id : " + oppositeUSerId +
//                ", user_group : " + DashboardActivity.dashboardActivity.user_group);
//        RequestParams requestParams = new RequestParams();
//        requestParams.add("id", connectionId);
//        requestParams.add("user_id", oppositeUSerId);
//        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
//        NetworkClient.breakConnection(context, requestParams, this, 21);
//    }

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
        ImageView userIV;
        TextView nameTV;
        TextView ageTV;
        TextView daysTV;
        TextView noOfStudentsTV;
        TextView ratingTV;
        TextView distanceTV;
        TextView experienceTV;
        TextView chargesTV;
        ImageView qualifiedIV;
//        ImageView connectionIV;
    }


    private int getAgeInyearsFromDOB(String dobString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(dobString);
            Calendar dob = Calendar.getInstance();
            dob.setTime(date);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
                age--;
            } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
