package com.findmycoach.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ved on 16/5/15.
 */
public class MenteeNotificationActions extends Activity {

    String action_for;
    JSONObject jsonObject_notification;
    ImageView imageView_user_icon;
    TextView tv_notification_message;
    TextView tv_date;
    TextView tv_user_message;
    Button b_profile_view;
    String first_name, image_url, date, time;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metee_notification_action_page);
        action_for = getIntent().getStringExtra("action_for");
        initialize();
        try {
            jsonObject_notification = new JSONObject(getIntent().getStringExtra("json_string"));
            first_name = jsonObject_notification.getString("first_name");
            image_url = jsonObject_notification.getString("image");
            if(image_url != null){
                Picasso.with(MenteeNotificationActions.this)
                        .load(image_url)
                        .placeholder(R.drawable.user_icon)
                        .error(R.drawable.user_icon)
                        .into(imageView_user_icon);
            }



            switch (action_for) {
                case "connection_accepted":
                    tv_notification_message.setText(first_name.trim() +" "+ getResources().getString(R.string.connection_request_accepted));
                    date = jsonObject_notification.getString("created_date");
                    time = jsonObject_notification.getString("created_time");
                    tv_date.setText(getDate_Time_String(date,time));
                    tv_user_message.setText(jsonObject_notification.getString("message"));
                    break;
                case "connection_rejected":
                    tv_notification_message.setText(first_name.trim()+" " + getResources().getString(R.string.connection_request_rejected));
                    date = jsonObject_notification.getString("created_date");
                    time = jsonObject_notification.getString("created_time");
                    tv_date.setText(getDate_Time_String(date,time));
                    tv_user_message.setText(jsonObject_notification.getString("message"));
                    break;
                case "schedule_accepted":
                    tv_notification_message.setText(first_name.trim()+" " + getResources().getString(R.string.schedule_request_accepted));
                    date = jsonObject_notification.getString("created_date");
                    time = jsonObject_notification.getString("created_time");
                    tv_date.setText(getDate_Time_String(date,time));
                    tv_user_message.setText(jsonObject_notification.getString("message"));
                    break;
                case "schedule_rejected":
                    tv_notification_message.setText(first_name.trim()+" " + getResources().getString(R.string.schedule_request_rejected));
                    date = jsonObject_notification.getString("created_date");
                    time = jsonObject_notification.getString("created_time");
                    tv_date.setText(getDate_Time_String(date,time));
                    tv_user_message.setText(jsonObject_notification.getString("message"));
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String getDate_Time_String(String date,String time){
        return String.format(getResources().getStringArray(R.array.months)[Integer.parseInt(date.split("-")[1])-1]+" %02d, %d %02d:%02d",Integer.parseInt(date.split("-")[2]),Integer.parseInt(date.split("-")[0]),Integer.parseInt(time.split(":")[0]),Integer.parseInt(time.split(":")[1]));
    }

    private void initialize() {
        imageView_user_icon = (ImageView) findViewById(R.id.iv_user_icon);
        tv_notification_message = (TextView) findViewById(R.id.tv_notification_message);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_user_message = (TextView) findViewById(R.id.tv_user_message);
        b_profile_view = (Button) findViewById(R.id.b_profile);
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView title = (TextView) findViewById(R.id.title);
        switch (action_for) {
            case "connection_accepted":
                title.setText("Connection status");
                break;
            case "connection_rejected":
                title.setText("Connection status");
                break;
            case "schedule_accepted":
                title.setText("Schedule status");
                break;

            case "schedule_rejected":
                title.setText("Schedule status");
                break;
        }

    }


}
