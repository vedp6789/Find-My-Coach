package com.findmycoach.mentor.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.findmycoach.mentor.adapter.ChatWidgetAdapter;
import com.fmc.mentor.findmycoach.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

/**
 * Created by IgluLabs on 1/23/2015.
 */
public class ChatWidgetActivity extends Activity implements View.OnClickListener {

    private String mentorName;
    private ListView chatWidgetLv;
    private EditText msgToSend;
    private ChatWidgetAdapter chatWidgetAdapter;
    private SocketIO socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_widget);
        initialize();
        applyActionbarProperties();
        new SocketTask().execute("http://10.1.1.129:3700");
    }

    private void initialize() {
        Intent getUserIntent = getIntent();
        if (getUserIntent != null)
            mentorName = getUserIntent.getStringExtra("mentor_name");
        chatWidgetLv = (ListView) findViewById(R.id.chatWidgetLv);
        msgToSend = (EditText) findViewById(R.id.msgToSendET);
        findViewById(R.id.sendButton).setOnClickListener(this);
        chatWidgetLv.setDivider(null);
        chatWidgetLv.setSelector(new ColorDrawable(0));
    }

    private void populateData() {
        //Dummy chatting
        ArrayList<String> dummyList = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            dummyList.add(i, "This is the dummy contain of message " + i);
        }
        chatWidgetAdapter = new ChatWidgetAdapter(this, dummyList);
        chatWidgetLv.setAdapter(chatWidgetAdapter);
    }

    private void applyActionbarProperties() {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        if (mentorName != null)
            actionbar.setTitle(mentorName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attach_files, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
//                if(socket != null){
//                    Log.d("FMC","Socket-isConnected : " + socket.isConnected());
//                }else
//                    Log.d("FMC","Socket-Null");
                break;
            case R.id.attach_image:
                Toast.makeText(this, "Attach Image is clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.attach_video:
                Toast.makeText(this, "Attach Video is clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendButton) {
            sendMsg();
        }
    }

    private void sendMsg() {
        String msg = msgToSend.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            msgToSend.requestFocus();
            return;
        }
        msgToSend.setText("");
        chatWidgetAdapter.updateMessageList(msg);
        chatWidgetAdapter.notifyDataSetChanged();
        chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
    }

    private class SocketTask extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;
            @Override
            protected void onProgressUpdate(String... progress) {
                Log.d("FMC", progress[0]);
                progressDialog = new ProgressDialog(ChatWidgetActivity.this);
                progressDialog.setMessage("Connecting...");
                progressDialog.show();
            }

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Trying to connect to " + params[0] + '\n');
            try {
                if(socket == null)
                    socket = new SocketIO(params[0].trim());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
                if(!socket.isConnected()){

                    socket.connect(new IOCallback() {
                        @Override
                        public void onMessage(JSONObject json, IOAcknowledge ack) {
                            try {
                                Log.d("FMC","Socket-Connected : Server said:" + json.toString(2));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onMessage(String data, IOAcknowledge ack) {
                            Log.d("FMC","Socket-Message-Received : " + data);
                        }

                        @Override
                        public void onError(SocketIOException socketIOException) {
                            Log.d("FMC","Socket-Error");
                            socketIOException.printStackTrace();
                        }

                        @Override
                        public void onDisconnect() {
                            Log.d("FMC","Socket-Disconnected");
                        }

                        @Override
                        public void onConnect() {
                            Log.d("FMC","Socket-Connected");
                            populateData();
                        }

                        @Override
                        public void on(String event, IOAcknowledge ack, Object... args) {
                            System.out.println("Server triggered event '" + event + "'");
                        }
                    });
                }
            return null;
        }

        @Override
            protected void onPostExecute(String result) {
                try{
                    Log.d("FMC", result);
                }catch (NullPointerException e){
                    Log.d("FMC", "result is null");
                }
            }

    }

}
