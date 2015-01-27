package com.findmycoach.mentor.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.findmycoach.mentor.adapter.ChatWidgetAdapter;
import com.fmc.mentor.findmycoach.R;

import java.util.ArrayList;

/**
 * Created by IgluLabs on 1/23/2015.
 */
public class ChatWidgetActivity extends Activity implements View.OnClickListener {

    private String mentorName;
    private ListView chatWidgetLv;
    private EditText msgToSend;
    private ChatWidgetAdapter chatWidgetAdapter;

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
            if(chatWidgetAdapter != null && msgToSend.getText().toString().length() > 1) {
                chatWidgetAdapter.updateMessageList(msgToSend.getText().toString());
                msgToSend.setText("");
                chatWidgetAdapter.notifyDataSetChanged();
            }
            else
            Toast.makeText(this,"Type something to send.",Toast.LENGTH_SHORT).show();
        }
    }
}
