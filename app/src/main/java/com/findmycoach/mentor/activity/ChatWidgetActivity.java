package com.findmycoach.mentor.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import com.findmycoach.mentor.util.StorageHelper;
import com.fmc.mentor.findmycoach.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by IgluLabs on 1/23/2015.
 */
public class ChatWidgetActivity extends Activity implements View.OnClickListener {

    private String studentId;
    private ListView chatWidgetLv;
    private EditText msgToSend;
    private ChatWidgetAdapter chatWidgetAdapter;
    private WebSocketClient mWebSocketClient;
    private ProgressDialog progressDialog;

    private static final String TAG="FMC";
    private static final String TAG1="FMC-Websocket";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_widget);
        initialize();
        applyActionbarProperties();
        progressDialog.show();
        populateData();
        connectWebSocket();
    }

    private void initialize() {
        Intent getUserIntent = getIntent();
        if (getUserIntent != null)
            studentId = getUserIntent.getStringExtra("student_id");
        chatWidgetLv = (ListView) findViewById(R.id.chatWidgetLv);
        msgToSend = (EditText) findViewById(R.id.msgToSendET);
        findViewById(R.id.sendButton).setOnClickListener(this);
        chatWidgetLv.setDivider(null);
        chatWidgetLv.setSelector(new ColorDrawable(0));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.connecting));
    }

    private void populateData() {
        ArrayList<String> dummyList = new ArrayList<String>();
        ArrayList<Integer> senderListList = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++) {
            dummyList.add(i, getResources().getString(R.string.dummy_contain_of_message) + i);
            senderListList.add(0);
        }
        chatWidgetAdapter = new ChatWidgetAdapter(this, dummyList, senderListList);
        chatWidgetLv.setAdapter(chatWidgetAdapter);
        
    }

    private void applyActionbarProperties() {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        if (studentId != null)
            actionbar.setTitle(studentId);
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
                break;
            case R.id.attach_image:
                Toast.makeText(this, getResources().getString(R.string.attach_image_clicked), Toast.LENGTH_SHORT).show();
                break;
            case R.id.attach_video:
                Toast.makeText(this, getResources().getString(R.string.attach_video_clicked), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebSocketClient.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectWebSocket();
    }

    private void sendMsg() {
        String msg = msgToSend.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            msgToSend.requestFocus();
            return;
        }

        JSONObject messageObject=new JSONObject();
        try {
            messageObject.put("receiver_id", studentId);
            messageObject.put("type", "text");
            messageObject.put("data", msg);
            String msgJson = messageObject.toString();
            mWebSocketClient.send(msgJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, studentId.trim());
        msgToSend.setText("");
        chatWidgetAdapter.updateMessageList(msg, 0);
        chatWidgetAdapter.notifyDataSetChanged();
        chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://192.241.196.244:9302");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i(TAG, "Opened");
                mWebSocketClient.send(StorageHelper.getUserDetails(ChatWidgetActivity.this, "auth_token"));
                progressDialog.dismiss();
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG1, message);
                        chatWidgetAdapter.updateMessageList(message, 1);
                        chatWidgetAdapter.notifyDataSetChanged();
                        chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d(TAG1, "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG1, "Error " + e.getMessage());
                progressDialog.dismiss();
            }
        };
        mWebSocketClient.connect();
    }

}
