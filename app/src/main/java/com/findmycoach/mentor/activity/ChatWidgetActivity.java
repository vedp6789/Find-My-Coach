package com.findmycoach.mentor.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.findmycoach.mentor.adapter.ChatWidgetAdapter;
import com.fmc.mentor.findmycoach.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by IgluLabs on 1/23/2015.
 */
public class ChatWidgetActivity extends Activity implements View.OnClickListener {

    private String mentorName;
    private ListView chatWidgetLv;
    private EditText msgToSend;
    private ChatWidgetAdapter chatWidgetAdapter;

    /////////////////////////////////////////////////////////////////////////////////////
//    private static final int REQUEST_LOGIN = 0;
//    private static final int TYPING_TIMER_LENGTH = 600;
//    private Handler mTypingHandler = new Handler();
//    private Socket mSocket;
//    {
//        try {
////            mSocket = IO.socket("ws://10.1.1.129:3001");
//            mSocket = IO.socket("http://chat.socket.io");
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }
    /////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_widget);
        initialize();
        applyActionbarProperties();
        populateData();
    }

    private void initialize() {
        Intent getUserIntent = getIntent();
        if(getUserIntent != null)
            mentorName = getUserIntent.getStringExtra("mentor_name");
        chatWidgetLv = (ListView) findViewById(R.id.chatWidgetLv);
        msgToSend = (EditText) findViewById(R.id.msgToSendET);
        findViewById(R.id.sendButton).setOnClickListener(this);
        chatWidgetLv.setDivider(null);
        chatWidgetLv.setSelector(new ColorDrawable(0));

        /////////////////////////////////////////////////////////////////////////////////////
//        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//        mSocket.on("new message", onNewMessage);
//        mSocket.connect();
//        startConnecting();
        /////////////////////////////////////////////////////////////////////////////////////

    }

    private void populateData() {
        //Dummy chatting
        ArrayList<String> dummyList = new ArrayList<String>();
        for(int i=0; i<5; i++){
            dummyList.add(i,"This is the dummy contain of message " + i);
        }
        chatWidgetAdapter = new ChatWidgetAdapter(this, dummyList);
        chatWidgetLv.setAdapter(chatWidgetAdapter);
    }

    private void applyActionbarProperties() {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        if(mentorName != null)
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
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.attach_image:
                Toast.makeText(this,"Attach Image is clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.attach_video:
                Toast.makeText(this,"Attach Video is clicked",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sendButton){
//            attemptSend(msgToSend.getText().toString());
        sendMsg();
        }
    }

    private void sendMsg() {
        String msg =msgToSend.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            msgToSend.requestFocus();
            return;
        }
        msgToSend.setText("");
        chatWidgetAdapter.updateMessageList(msg);
        chatWidgetAdapter.notifyDataSetChanged();
        chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount()-1);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

//    public void onDestroy() {
//        mSocket.disconnect();
//        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
//        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//        mSocket.off("new message", onNewMessage);
//    }
//
//
//
//    //add msg to current list shown to user
//    private void addMessage(String username, String message) {
//        if(username.equals("Mentor-Name"))
//            chatWidgetAdapter.updateMessageList(message);
//        else
//            chatWidgetAdapter.updateMessageList(username + " : : " + message);
//        chatWidgetAdapter.notifyDataSetChanged();
//        chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount()-1);
//    }
//
//    //call to send msg
//    private void attemptSend(String message) {
//        if (!mSocket.connected()) return;
//        message = message.trim();
//        if (TextUtils.isEmpty(message)) {
//            msgToSend.requestFocus();
//            return;
//        }
//        msgToSend.setText("");
//        addMessage("Mentor-Name", message);
//        // perform the sending message attempt.
//        mSocket.emit("new message", message);
//    }
//
//    private void startConnecting() {
//        mSocket.connect();
//    }
//
//    //Disconnect socket
//    private void leave() {
//        mSocket.disconnect();
//    }
//
//
//    private Emitter.Listener onConnectError = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            ChatWidgetActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(ChatWidgetActivity.this.getApplicationContext(),"error_connect", Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onNewMessage = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            ChatWidgetActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    String username;
//                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        return;
//                    }
//                    addMessage(username, message);
//                }
//            });
//        }
//    };
}
