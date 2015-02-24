package com.findmycoach.mentor.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.findmycoach.mentor.adapter.ChatWidgetAdapter;
import com.findmycoach.mentor.beans.attachment.Attachment;
import com.findmycoach.mentor.beans.chats.Chats;
import com.findmycoach.mentor.beans.chats.Data;
import com.findmycoach.mentor.util.Callback;
import com.findmycoach.mentor.util.ImageLoadTask;
import com.findmycoach.mentor.util.NetworkClient;
import com.findmycoach.mentor.util.StorageHelper;
import com.fmc.mentor.findmycoach.R;
import com.loopj.android.http.RequestParams;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IgluLabs on 1/23/2015.
 */
public class ChatWidgetActivity extends Activity implements View.OnClickListener, Callback {

    private String studentId, mentorId;
    private ListView chatWidgetLv;
    private EditText msgToSend;
    private ChatWidgetAdapter chatWidgetAdapter;
    private WebSocketClient mWebSocketClient;
    private ProgressDialog progressDialog;
    private final int STORAGE_GALLERY_IMAGE_REQUEST_CODE = 100;
    private final int STORAGE_GALLERY_VIDEO_REQUEST_CODE = 101;
    private boolean isSocketConnected;

    private static final String TAG="FMC";
    private static final String TAG1="FMC-WebSocket";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_widget);
        initialize();
        applyActionbarProperties();
        progressDialog.show();

        /*Creating/Checking folder for media storage*/
        StorageHelper.createAppMediaFolders(this);
//        populateData();
//        connectWebSocket();
        getChatHistory();
    }

    public void getChatHistory() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("sender_id", mentorId);
        requestParams.add("receiver_id",studentId);
        NetworkClient.getChatHistory(this, requestParams, this);
    }

    private void initialize() {
        Intent getUserIntent = getIntent();
        if (getUserIntent != null)
            studentId = getUserIntent.getStringExtra("student_id").trim();
        chatWidgetLv = (ListView) findViewById(R.id.chatWidgetLv);
        msgToSend = (EditText) findViewById(R.id.msgToSendET);
        mentorId = StorageHelper.getUserDetails(this, "user_id");
        findViewById(R.id.sendButton).setOnClickListener(this);
        chatWidgetLv.setDivider(null);
        chatWidgetLv.setSelector(new ColorDrawable(0));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.connecting));
        progressDialog.setCancelable(false);
        isSocketConnected = false;
    }

    private void populateData(List<Data> chats) {
        ArrayList<String> messageList = new ArrayList<String>();
        ArrayList<Integer> senderList = new ArrayList<Integer>();
        ArrayList<Integer> messageTypeList = new ArrayList<Integer>();
        for(int i=chats.size()-1; i>=0; i--){
            Data data = chats.get(i);
            messageList.add(data.getMessage());

            if(data.getSender_id().equals(studentId))
                senderList.add(1);
            else
                senderList.add(0);

            if(data.getMessage_type().equals("text"))
                messageTypeList.add(0);
            else if(data.getMessage_type().equals("image"))
                messageTypeList.add(1);
            else if(data.getMessage_type().equals("video"))
                messageTypeList.add(2);

        }
        chatWidgetAdapter = new ChatWidgetAdapter(this, messageList, senderList, messageTypeList);
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
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, STORAGE_GALLERY_IMAGE_REQUEST_CODE);
                break;
            case R.id.attach_video:
                Intent videoPickerIntent = new Intent(Intent.ACTION_PICK);
                videoPickerIntent.setType("video/*");
                startActivityForResult(videoPickerIntent, STORAGE_GALLERY_VIDEO_REQUEST_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = null;
        // Image selected
        if (resultCode == RESULT_OK && requestCode == STORAGE_GALLERY_IMAGE_REQUEST_CODE){
            path = getRealPathFromURI(data.getData());
            Log.d(TAG, path);
            sendAttachment(path, "image");
        }

        //Video selected
        else if (resultCode == RESULT_OK && requestCode == STORAGE_GALLERY_VIDEO_REQUEST_CODE){
            path = getRealPathFromURI(data.getData());
            Log.d(TAG, path);
            sendAttachment(path, "video");
        }
    }

    private void sendAttachment(String filePath, String type) {
        progressDialog.setMessage(getResources().getString(R.string.sending));
        progressDialog.show();
        try {
            RequestParams requestParams = new RequestParams();
            requestParams.add("sender_id", mentorId);
            requestParams.add("receiver_id", studentId);
            requestParams.add("type", type);
            requestParams.put("file", new File(filePath));
            NetworkClient.sendAttachment(this, requestParams, this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // And to convert the image URI to the direct file system path of the image file
    public String getRealPathFromURI(Uri contentUri) {
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri,  proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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

        String msgJson = getMsgInJson("text", msg).toString();
        if(isSocketConnected) {
            mWebSocketClient.send(msgJson);
            Log.d(TAG, msgJson);
        }
        else {
            connectWebSocket();
            return;
        }

        msgToSend.setText("");
        chatWidgetAdapter.updateMessageList(msg, 0, 0);
        chatWidgetAdapter.notifyDataSetChanged();
        chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
    }

    private JSONObject getMsgInJson(String type, String msg) {
        JSONObject messageObject = new JSONObject();
        try {
            messageObject.put("receiver_id", studentId);
            messageObject.put("type", type);
            messageObject.put("data", msg);
            messageObject.put("receiver_group_id", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messageObject;
    }

    private void showReceivedMessage(String message) {
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String msg = jsonMessage.getString("message");
            String messageType = jsonMessage.getString("message_type");
            if(messageType.equals("text")){
                chatWidgetAdapter.updateMessageList(msg, 1, 0);
                chatWidgetAdapter.notifyDataSetChanged();
                chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
            }else if(messageType.equals("image")){
                chatWidgetAdapter.updateMessageList(msg, 1, 1);
                chatWidgetAdapter.notifyDataSetChanged();
                chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
            }else if(messageType.equals("video")){
                chatWidgetAdapter.updateMessageList(msg, 1, 2);
                chatWidgetAdapter.notifyDataSetChanged();
                chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebSocketClient.close();
    }

    private void connectWebSocket() {
        progressDialog.setMessage(getResources().getString(R.string.connecting));
        URI uri;
        try {
            uri = new URI(getResources().getString(R.string.CHAT_SOCKET_URL));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i(TAG, "Opened");
                isSocketConnected = true;
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
                        showReceivedMessage(message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d(TAG1, "Closed " + s);
                isSocketConnected = false;
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG1, "Error " + e.getMessage());
                isSocketConnected = false;
                progressDialog.dismiss();
            }
        };
        mWebSocketClient.connect();
    }

    @Override
    public void successOperation(Object object) {
        if(object instanceof Chats){
            Chats chats = (Chats) object;
            if(chats.getData() != null && chats.getData().size() > 0){
                populateData(chats.getData());
            }else{
                chatWidgetAdapter = new ChatWidgetAdapter(this, new ArrayList<String>(), new ArrayList<Integer>(), new ArrayList<Integer>());
                chatWidgetLv.setAdapter(chatWidgetAdapter);
            }
            connectWebSocket();
        }
        else if(object instanceof Attachment){
            progressDialog.dismiss();
            Attachment attachment = (Attachment) object;
            Log.d(TAG,attachment.getData().getPath());
            String attachmentPath = attachment.getData().getPath();

            String msgJson = getMsgInJson(attachment.getData().getFile_type().contains("image") ? "image" : "video", attachmentPath).toString();
            Log.d(TAG,"Sending to socket : " + msgJson);
            if(isSocketConnected) {
                mWebSocketClient.send(msgJson);
                Log.d(TAG, msgJson);
            }
            else {
                mWebSocketClient.connect();
                return;
            }
            chatWidgetAdapter.updateMessageList(attachmentPath, 0, 1);
            chatWidgetAdapter.notifyDataSetChanged();
            chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
        }
    }

    @Override
    public void failureOperation(Object object) {
        progressDialog.dismiss();
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
    }
}
