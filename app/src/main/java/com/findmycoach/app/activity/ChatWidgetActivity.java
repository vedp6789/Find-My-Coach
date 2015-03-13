package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.ChatWidgetAdapter;
import com.findmycoach.app.adapter.NotificationAdapter;
import com.findmycoach.app.beans.attachment.Attachment;
import com.findmycoach.app.beans.chats.Chats;
import com.findmycoach.app.beans.chats.Data;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
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

    private String receiverName, receiverId, currentUserId;
    private ListView chatWidgetLv;
    private EditText msgToSend;
    public ChatWidgetAdapter chatWidgetAdapter;
    private WebSocketClient mWebSocketClient;
    private ProgressDialog progressDialog;
    private final int STORAGE_GALLERY_IMAGE_REQUEST_CODE = 100;
    private final int STORAGE_GALLERY_VIDEO_REQUEST_CODE = 101;
    private boolean isSocketConnected;
    public static ChatWidgetActivity chatWidgetActivity;
    private boolean isGettingProfile = false;

    private static final String TAG="FMC";
    private static final String TAG1="FMC-WebSocket";
    private String receiverGroupId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatWidgetActivity = this;
        setContentView(R.layout.activity_chat_widget);
        initialize();
        applyActionbarProperties();
        progressDialog.show();
        receiverGroupId = DashboardActivity.dashboardActivity.user_group == 3 ? "2" : "3";

        /*Creating/Checking folder for media storage*/
        StorageHelper.createAppMediaFolders(this);
//        populateData();
//        connectWebSocket();
        getChatHistory();
    }

    public void getChatHistory() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("sender_id", currentUserId);
        requestParams.add("receiver_id", receiverId);
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        NetworkClient.getChatHistory(this, requestParams, this, 29);
    }

    private void initialize() {
        Intent getUserIntent = getIntent();
        if (getUserIntent != null) {
            receiverId = getUserIntent.getStringExtra("receiver_id").trim();
            receiverName = getUserIntent.getStringExtra("receiver_name").trim();
        }
        chatWidgetLv = (ListView) findViewById(R.id.chatWidgetLv);
        msgToSend = (EditText) findViewById(R.id.msgToSendET);
        currentUserId = StorageHelper.getUserDetails(this, "user_id");
        findViewById(R.id.sendButton).setOnClickListener(this);
        chatWidgetLv.setDivider(null);
        chatWidgetLv.setSelector(new ColorDrawable(0));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.connecting));
//        progressDialog.setCancelable(false);
        isSocketConnected = false;
    }

    private void populateData(List<Data> chats) {
        ArrayList<String> messageList = new ArrayList<String>();
        ArrayList<Integer> senderList = new ArrayList<Integer>();
        ArrayList<Integer> messageTypeList = new ArrayList<Integer>();
        for(int i=chats.size()-1; i>=0; i--){
            Data data = chats.get(i);
            messageList.add(data.getMessage());

            if(data.getSender_id().equals(receiverId))
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
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View customView = getLayoutInflater().inflate(R.layout.actionbar_title, null);
            TextView customTitle = (TextView) customView.findViewById(R.id.actionbarTitle);
            if (receiverId != null)
                customTitle.setText(receiverName);
            customTitle.setTypeface(Typeface.MONOSPACE);
            customTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getProfile();
                    Log.d(TAG,receiverName + " <= Name : Id => " + receiverId);
                }
            });
            actionBar.setCustomView(customView);
        }
    }

    private void getProfile(){
        if(isGettingProfile)
            return;
        isGettingProfile = true;
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id",receiverId);
        String authToken = StorageHelper.getUserDetails(this, "auth_token");
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");
        if(DashboardActivity.dashboardActivity.user_group == 3)
            NetworkClient.getStudentDetails(this, requestParams, authToken, this, 25);
        else if(DashboardActivity.dashboardActivity.user_group == 2)
            NetworkClient.getMentorDetails(this, requestParams, authToken, this, 24);
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
//        progressDialog.setMessage(getResources().getString(R.string.sending));
//        progressDialog.show();
        try {
            RequestParams requestParams = new RequestParams();
            requestParams.add("sender_id", currentUserId);
            requestParams.add("receiver_id", receiverId);
            requestParams.add("type", type);
            requestParams.put("file", new File(filePath));
            requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group+"");

            chatWidgetAdapter.fileNames.add(filePath);
            if(type.equals("image"))
                chatWidgetAdapter.updateMessageList(filePath, 0, 1);
            else
                chatWidgetAdapter.updateMessageList(filePath, 0, 2);
            chatWidgetAdapter.notifyDataSetChanged();
            chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);

            NetworkClient.sendAttachment(this, requestParams, this, 30);
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
            progressDialog.show();
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
            messageObject.put("receiver_id", receiverId);
            messageObject.put("type", type);
            messageObject.put("data", msg);
            messageObject.put("receiver_group_id", receiverGroupId);
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
        try{
            mWebSocketClient.close();
        }catch (Exception e){
            e.printStackTrace();
        }
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
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        // For displaying selected Mentor details
        if(calledApiValue == 24){
            progressDialog.dismiss();
            Intent intent = new Intent(this, MentorDetailsActivity.class);
            intent.putExtra("mentorDetails", (String) object);
            intent.putExtra("connection_status", "accepted");
            startActivity(intent);
            isGettingProfile = false;
            return;
        }
        if(calledApiValue == 25){
            progressDialog.dismiss();
            Intent intent = new Intent(this, StudentDetailActivity.class);
            intent.putExtra("coming_from","ChatWidget");
            intent.putExtra("student_detail",(String) object);
            startActivityForResult(intent, NotificationAdapter.connection_id);
            isGettingProfile = false;
            return;
        }

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
            if(isSocketConnected) {
                mWebSocketClient.send(msgJson);
                Log.d(TAG, msgJson);
                chatWidgetAdapter.downloadFile(attachmentPath, attachment.getData().getFile_type().contains("image") ? "image" : "video");
            }
            else {
                progressDialog.show();
                connectWebSocket();
                return;
            }
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
    }
}
