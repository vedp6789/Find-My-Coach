package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.UserNotifications.ConnectionRequest;
import com.findmycoach.app.beans.UserNotifications.ScheduleRequest;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

/**
 * Created by ved on 15/5/15.
 */
public class MentorNotificationActions extends Activity implements Callback{
    private TextView tv_start_date, tv_stop_date, tv_start_time, tv_stop_time, tv_week_days, tv_subject, tv_class_type, tv_title_message, tv_date, tv_conn_req_message_from_mentee;
    Button b_profile_view, b_accept, b_decline;
    EditText et_mentor_reply;
    ImageView iv_user_icon;
    Bundle bundle;
    String mentor_notificaton_for;
    ConnectionRequest connectionRequest;
    ScheduleRequest scheduleRequest;
    String first_name, connection_request_message_from_mentee;
    String start_date, start_time, stop_date, stop_time;
    String[] week_days;
    String image_url;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        mentor_notificaton_for = getIntent().getStringExtra("for");

        switch (mentor_notificaton_for) {
            case "connection_request":
                bundle = getIntent().getBundleExtra("conn_req_bundle");
                setContentView(R.layout.connection_request_action_page);
                connectionRequest = bundle.getParcelable("conn_req_data");
                initialize_for_connection_request();
                first_name = connectionRequest.getFirst_name();
                tv_title_message.setText(first_name + getResources().getString(R.string.connection_request_title));
                start_date = connectionRequest.getStart_date();
                start_time = connectionRequest.getStart_time();

                Log.d("FMC", "start_date of connection request :  " + start_date);
                String month = getResources().getStringArray(R.array.months)[Integer.parseInt(start_date.split("-")[1]) - 1];
                int start_day = Integer.parseInt(start_date.split("-")[2]);
                int start_year = Integer.parseInt(start_date.split("-")[0]);
                int start_hour = Integer.parseInt(start_time.split("-")[0]);
                int start_min = Integer.parseInt(start_time.split("-")[1]);
                tv_date.setText(String.format(month + " %02d,%d \t %02d:%02d", start_day,start_year, start_hour, start_min));

                connection_request_message_from_mentee = connectionRequest.getMessage();
                if (connection_request_message_from_mentee != null) {
                    tv_conn_req_message_from_mentee.setText(connection_request_message_from_mentee);
                }
                image_url=connectionRequest.getImage_url();
                Picasso.with(MentorNotificationActions.this)
                        .load(image_url)
                        .placeholder(R.drawable.user_icon)
                        .error(R.drawable.user_icon)
                        .into(iv_user_icon);




                b_profile_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                b_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         RequestParams  requestParams=new RequestParams();
                        requestParams.add("id",connectionRequest.getConnection_id());
                        requestParams.add("user_group", StorageHelper.getUserGroup(MentorNotificationActions.this, "user_group"));
                        requestParams.add("status","accepted");
                        progressDialog.show();
                        NetworkClient.respondToConnectionRequest(MentorNotificationActions.this,requestParams,MentorNotificationActions.this,18);
                    }
                });

                b_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams  requestParams=new RequestParams();
                        requestParams.add("id",connectionRequest.getConnection_id());
                        requestParams.add("user_group", StorageHelper.getUserGroup(MentorNotificationActions.this,"user_group"));
                        requestParams.add("status","rejected");
                        progressDialog.show();
                        NetworkClient.respondToConnectionRequest(MentorNotificationActions.this,requestParams,MentorNotificationActions.this,18);
                    }
                });

                break;
            case "schedule_request":
                bundle = getIntent().getBundleExtra("schedule_req_bundle");
                setContentView(R.layout.schedule_request_action_page);
                scheduleRequest = bundle.getParcelable("schedule_req_data");
                initialize_for_schedule_request();
                first_name = scheduleRequest.getFirst_name();
                tv_title_message.setText(first_name + getResources().getString(R.string.schedule_request_title));
                start_date=scheduleRequest.getStart_date();
                stop_date=scheduleRequest.getStop_date();
                start_time=scheduleRequest.getStart_time();
                stop_time=scheduleRequest.getStop_time();
                week_days=scheduleRequest.getWeek_days();

                tv_start_date.setText(String.format("%02d-%02d-%2d",start_date.split("-")[2],start_date.split("-")[1],start_date.split("-")[0]));
                tv_stop_date.setText(String.format("%02d-%02d-%2d",stop_date.split("-")[2],stop_date.split("-")[1],stop_date.split("-")[0]));
                tv_start_time.setText(String.format("%02d:%02d",start_time.split(":")[0],start_time.split(":")[1]));
                tv_stop_time.setText(String.format("%02d:%02d",stop_time.split(":")[0],stop_time.split(":")[1]));


                StringBuilder stringBuilder=new StringBuilder();
                for(int i=0; i < week_days.length;i++){
                    String week_day=week_days[i];

                    if(i== week_days.length-1){
                        if(week_day.equals("M"))
                            stringBuilder.append("Monday");
                        if(week_day.equals("T"))
                            stringBuilder.append("Tuesday");
                        if(week_day.equals("W"))
                            stringBuilder.append("Wednesday");
                        if(week_day.equals("Th"))
                            stringBuilder.append("Thursday");
                        if(week_day.equals("F"))
                            stringBuilder.append("Friday");
                        if(week_day.equals("S"))
                            stringBuilder.append("Saturday");
                        if(week_day.equals("Su"))
                            stringBuilder.append("Sunday");
                    }else{
                        if(week_day.equals("M"))
                            stringBuilder.append("Monday, ");
                        if(week_day.equals("T"))
                            stringBuilder.append("Tuesday, ");
                        if(week_day.equals("W"))
                            stringBuilder.append("Wednesday, ");
                        if(week_day.equals("Th"))
                            stringBuilder.append("Thursday, ");
                        if(week_day.equals("F"))
                            stringBuilder.append("Friday, ");
                        if(week_day.equals("S"))
                            stringBuilder.append("Saturday, ");
                        if(week_day.equals("Su"))
                            stringBuilder.append("Sunday, ");
                    }


                }

                tv_week_days.setText(stringBuilder.toString());
                tv_subject.setText(scheduleRequest.getSubject());
                tv_class_type.setText(scheduleRequest.getClass_type());

                image_url=scheduleRequest.getImage_url();
                Picasso.with(MentorNotificationActions.this)
                        .load(image_url)
                        .placeholder(R.drawable.user_icon)
                        .error(R.drawable.user_icon)
                        .into(iv_user_icon);


                b_profile_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams requestParams=new RequestParams();
                        //requestParams.add();
                    }
                });

                b_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams requestParams=new RequestParams();
                        requestParams.add("slot_type",scheduleRequest.getClass_type());
                        requestParams.add("event_id",scheduleRequest.getEvent_id());
                        requestParams.add("student_id",scheduleRequest.getStudent_id());
                        if(et_mentor_reply.getText().toString() != null){
                            requestParams.add("message",et_mentor_reply.getText().toString());
                        }else{
                            requestParams.add("message","");
                        }
                        requestParams.add("response","true");
                        progressDialog.show();
                        NetworkClient.finalizeEvent(MentorNotificationActions.this,requestParams,MentorNotificationActions.this,49);

                    }
                });

                b_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams requestParams=new RequestParams();
                        requestParams.add("slot_type",scheduleRequest.getClass_type());
                        requestParams.add("event_id",scheduleRequest.getEvent_id());
                        requestParams.add("student_id",scheduleRequest.getStudent_id());
                        if(et_mentor_reply.getText().toString() != null){
                            requestParams.add("message",et_mentor_reply.getText().toString());
                        }else{
                            requestParams.add("message","");
                        }
                        requestParams.add("response","false");
                        progressDialog.show();
                        NetworkClient.finalizeEvent(MentorNotificationActions.this,requestParams,MentorNotificationActions.this,49);

                    }
                });

                break;
        }


    }

    private void initialize_for_schedule_request() {
        iv_user_icon= (ImageView) findViewById(R.id.iv_user_icon);
        tv_start_date = (TextView) findViewById(R.id.tv_start_date_val);
        tv_stop_date = (TextView) findViewById(R.id.tv_stop_date_val);
        tv_start_time = (TextView) findViewById(R.id.tv_start_time_val);
        tv_stop_time = (TextView) findViewById(R.id.tv_start_time_val);
        tv_subject = (TextView) findViewById(R.id.tv_subject_val);
        tv_week_days = (TextView) findViewById(R.id.tv_week_days_val);
        tv_class_type = (TextView) findViewById(R.id.tv_class_type_val);
        tv_title_message = (TextView) findViewById(R.id.tv_schedule_request_title_message);
        et_mentor_reply= (EditText) findViewById(R.id.et_mentor_response_message);

        b_profile_view = (Button) findViewById(R.id.b_profile);
        b_accept = (Button) findViewById(R.id.b_accept);
        b_decline = (Button) findViewById(R.id.b_decline);


    }

    private void initialize_for_connection_request() {
        iv_user_icon= (ImageView) findViewById(R.id.iv_user_icon);
        tv_title_message = (TextView) findViewById(R.id.tv_connection_request_title_message);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_conn_req_message_from_mentee = (TextView) findViewById(R.id.tv_message);
        b_profile_view = (Button) findViewById(R.id.b_profile);
        b_accept = (Button) findViewById(R.id.b_accept);
        b_decline = (Button) findViewById(R.id.b_decline);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(MentorNotificationActions.this,(String)object,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(MentorNotificationActions.this,(String)object,Toast.LENGTH_SHORT).show();
    }
}