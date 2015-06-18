package com.findmycoach.app.activity;

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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.ChatWidgetAdapter;
import com.findmycoach.app.beans.attachment.Attachment;
import com.findmycoach.app.beans.chats.Chats;
import com.findmycoach.app.beans.chats.Data;
import com.findmycoach.app.fragment.MyConnectionsFragment;
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
import java.util.Calendar;
import java.util.List;

/**
 * Created by IgluLabs on 1/23/2015.
 */
public class ChatWidgetActivity extends Activity implements View.OnClickListener, Callback {

    private String receiverName, receiverId, currentUserId, receiverImage;
    private ListView chatWidgetLv;
    private EditText msgToSend;
    public ChatWidgetAdapter chatWidgetAdapter;
    private WebSocketClient mWebSocketClient;
    private ProgressDialog progressDialog;
    private final int STORAGE_GALLERY_IMAGE_REQUEST_CODE = 100;
    private final int STORAGE_GALLERY_VIDEO_REQUEST_CODE = 101;
    private final int PROFILE_DETAILS = 102;
    private boolean isSocketConnected;
    public static ChatWidgetActivity chatWidgetActivity;
    private boolean isGettingProfile = false;

    private static final String TAG = "FMC";
    private static final String TAG1 = "FMC-WebSocket";
    private String receiverGroupId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatWidgetActivity = this;
        setContentView(R.layout.activity_chat_widget);
        initialize();
        progressDialog.show();
        receiverGroupId = DashboardActivity.dashboardActivity.user_group == 3 ? "2" : "3";

        /** Creating/Checking folder for media storage */
        StorageHelper.createAppMediaFolders(this);
        getChatHistory();
    }

    /**
     * Getting chat history
     */
    public void getChatHistory() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("sender_id", currentUserId);
        requestParams.add("receiver_id", receiverId);
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");
        NetworkClient.getChatHistory(this, requestParams, this, 29);
    }

    /**
     * Getting references of views
     */
    private void initialize() {
        Intent getUserIntent = getIntent();
        if (getUserIntent != null) {
            receiverId = getUserIntent.getStringExtra("receiver_id").trim();
            receiverName = getUserIntent.getStringExtra("receiver_name").trim();
            try {
                receiverImage = getUserIntent.getStringExtra("receiver_image");
            } catch (Exception e) {
                receiverImage = "";
            }
        }
        chatWidgetLv = (ListView) findViewById(R.id.chatWidgetLv);
        msgToSend = (EditText) findViewById(R.id.msgToSendET);
        currentUserId = StorageHelper.getUserDetails(this, "user_id");
        findViewById(R.id.sendButton).setOnClickListener(this);
        chatWidgetLv.setSelector(new ColorDrawable(0));

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(receiverName);
        title.setOnClickListener(this);

        findViewById(R.id.backButton).setOnClickListener(this);

        ImageView attachmentView = (ImageView) findViewById(R.id.attachment);
        attachmentView.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.connecting));
//        progressDialog.setCancelable(false);
        isSocketConnected = false;
    }

    /**
     * Populating data to their respective views
     */
    private void populateData(List<Data> chats) {
        ArrayList<String> messageList = new ArrayList<String>();
        ArrayList<Integer> senderList = new ArrayList<Integer>();
        ArrayList<Integer> messageTypeList = new ArrayList<Integer>();
        ArrayList<String> timeStampList = new ArrayList<String>();
        for (int i = chats.size() - 1; i >= 0; i--) {
            Data data = chats.get(i);
            messageList.add(data.getMessage());
            timeStampList.add(data.getUpdated_on());

            if (data.getSender_id().equals(receiverId))
                senderList.add(1);
            else
                senderList.add(0);

            switch (data.getMessage_type()) {
                case "text":
                    messageTypeList.add(0);
                    break;
                case "image":
                    messageTypeList.add(1);
                    break;
                case "video":
                    messageTypeList.add(2);
                    break;
            }

        }
        chatWidgetAdapter = new ChatWidgetAdapter(this, messageList, senderList,
                messageTypeList, timeStampList);
        chatWidgetLv.setAdapter(chatWidgetAdapter);
    }


    /**
     * Getting profile of receiver
     */
    private void getProfile() {
        if (isGettingProfile)
            return;
        isGettingProfile = true;
        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", receiverId);
        String authToken = StorageHelper.getUserDetails(this, "auth_token");
        requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");

        /** Checking whether mentee profile is required or mentor profile */
        if (DashboardActivity.dashboardActivity.user_group == 3) {
            requestParams.add("invitee_id", StorageHelper.getUserDetails(this, "user_id"));
            NetworkClient.getStudentDetails(this, requestParams, authToken, this, 25);
        } else if (DashboardActivity.dashboardActivity.user_group == 2) {
            requestParams.add("owner_id", StorageHelper.getUserDetails(this, "user_id"));
            NetworkClient.getMentorDetails(this, requestParams, authToken, this, 24);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = null;
        /** Image selected */
        if (resultCode == RESULT_OK && requestCode == STORAGE_GALLERY_IMAGE_REQUEST_CODE) {
            path = getRealPathFromURI(data.getData());
            Log.d(TAG, path);
            sendAttachment(path, "image");
        }

        /** Video selected */
        else if (resultCode == RESULT_OK && requestCode == STORAGE_GALLERY_VIDEO_REQUEST_CODE) {
            path = getRealPathFromURI(data.getData());
            Log.d(TAG, path);
            sendAttachment(path, "video");
        }

        /** Update if connection is broken */
        else if (resultCode == RESULT_OK && requestCode == PROFILE_DETAILS) {
            if (data.getStringExtra("status").equals("close_activity")) {
                MyConnectionsFragment.needToRefresh = true;
                finish();
            }

        }
    }

    /**
     * Sending attachment to server
     */
    private void sendAttachment(String filePath, String type) {
        try {
            RequestParams requestParams = new RequestParams();
            requestParams.add("sender_id", currentUserId);
            requestParams.add("receiver_id", receiverId);
            requestParams.add("type", type);
            requestParams.put("file", new File(filePath));
            requestParams.add("user_group", DashboardActivity.dashboardActivity.user_group + "");

            chatWidgetAdapter.fileNames.add(filePath);
            if (type.equals("image"))
                chatWidgetAdapter.updateMessageList(filePath, 0, 1,
                        String.valueOf(Calendar.getInstance().getTimeInMillis()));
            else
                chatWidgetAdapter.updateMessageList(filePath, 0, 2,
                        String.valueOf(Calendar.getInstance().getTimeInMillis()));
            chatWidgetAdapter.notifyDataSetChanged();
            chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);

            NetworkClient.sendAttachment(this, requestParams, this, 30);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert the attachment URI to the direct file system path
     */
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.sendButton) {
            sendMsg();
        } else if (id == R.id.title) {
            getProfile();
        } else if (id == R.id.attachment) {
            showAttachmentDialog();
        } else if (id == R.id.backButton) {
            finish();
        }
    }

    private void showAttachmentDialog() {
        final Dialog dialog = new Dialog(ChatWidgetActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.listview);
        ListView listView = (ListView) dialog.findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(ChatWidgetActivity.this,
                R.layout.textview, new String[]{"Image", "Video"}));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, STORAGE_GALLERY_IMAGE_REQUEST_CODE);
                } else if (position == 1) {
                    Intent videoPickerIntent = new Intent(Intent.ACTION_PICK);
                    videoPickerIntent.setType("video/*");
                    startActivityForResult(videoPickerIntent, STORAGE_GALLERY_VIDEO_REQUEST_CODE);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Sending msg to chat socket
     */
    private void sendMsg() {
        String msg = msgToSend.getText().toString().trim();

        /** If msg is null or nothing */
        if (TextUtils.isEmpty(msg)) {
            msgToSend.requestFocus();
            return;
        }

        String msgJson = getMsgInJson("text", msg).toString();

        /** Socket is connected or not */
        if (isSocketConnected) {
            mWebSocketClient.send(msgJson);
            Log.d(TAG, msgJson);
        } else {
            progressDialog.show();
            connectWebSocket();
            return;
        }

        /** Sending message to socket */
        msgToSend.setText("");
        chatWidgetAdapter.updateMessageList(msg, 0, 0,
                String.valueOf(Calendar.getInstance().getTimeInMillis()));
        chatWidgetAdapter.notifyDataSetChanged();
        chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
    }

    /**
     * Creating json object for sending message
     */
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

    /**
     * Displaying received message
     */
    private void showReceivedMessage(String message) {
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String msg = jsonMessage.getString("message");
            String messageType = jsonMessage.getString("message_type");
            /** Text message received */
            switch (messageType) {
                case "text":
                    chatWidgetAdapter.updateMessageList(msg, 1, 0,
                            String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    chatWidgetAdapter.notifyDataSetChanged();
                    chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
                    break;

                /** Image message received */
                case "image":
                    chatWidgetAdapter.updateMessageList(msg, 1, 1,
                            String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    chatWidgetAdapter.notifyDataSetChanged();
                    chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
                    break;

                /** Video message received */
                case "video":
                    chatWidgetAdapter.updateMessageList(msg, 1, 2,
                            String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    chatWidgetAdapter.notifyDataSetChanged();
                    chatWidgetLv.setSelection(chatWidgetLv.getAdapter().getCount() - 1);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnect web socket onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mWebSocketClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect to web socket server
     */
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
                mWebSocketClient.send(StorageHelper.getUserDetails(ChatWidgetActivity.this,
                        "auth_token"));
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
        /** For displaying selected Mentor details */
        if (calledApiValue == 24) {
            progressDialog.dismiss();
            Intent intent = new Intent(this, MentorDetailsActivity.class);
            intent.putExtra("mentorDetails", (String) object);
            intent.putExtra("searched_keyword", "-1");
            intent.putExtra("connection_status", "accepted");
            startActivityForResult(intent, PROFILE_DETAILS);
            isGettingProfile = false;
            return;
        }

        /** For displaying selected Student details */
        if (calledApiValue == 25) {
            progressDialog.dismiss();
            Intent intent = new Intent(this, StudentDetailActivity.class);
            intent.putExtra("coming_from", "ChatWidget");
            intent.putExtra("connection_status", "accepted");
            intent.putExtra("student_detail", (String) object);
            startActivityForResult(intent, PROFILE_DETAILS);
            isGettingProfile = false;
            return;
        }

        /** Chat history is received from api */
        if (object instanceof Chats) {
            Chats chats = (Chats) object;
            if (chats.getData() != null && chats.getData().size() > 0) {
                populateData(chats.getData());
            } else {
                chatWidgetAdapter = new ChatWidgetAdapter(this, new ArrayList<String>(),
                        new ArrayList<Integer>(), new ArrayList<Integer>(),
                        new ArrayList<String>());
                chatWidgetLv.setAdapter(chatWidgetAdapter);
            }
            connectWebSocket();
        }

        /** Attachment is uploaded to server */
        else if (object instanceof Attachment) {
            progressDialog.dismiss();
            Attachment attachment = (Attachment) object;
            Log.d(TAG, attachment.getData().getPath());
            String attachmentPath = attachment.getData().getPath();

            /** Sending attachment url with type to chat socket */
            String msgJson = getMsgInJson(attachment.getData().getFile_type().contains("image") ?
                    "image" : "video", attachmentPath).toString();
            if (isSocketConnected) {
                mWebSocketClient.send(msgJson);
                Log.d(TAG, msgJson);
                chatWidgetAdapter.downloadFile(attachmentPath, attachment.getData().getFile_type()
                        .contains("image") ? "image" : "video");
            } else {
                progressDialog.show();
                connectWebSocket();
            }
        }
    }


    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
    }
}
