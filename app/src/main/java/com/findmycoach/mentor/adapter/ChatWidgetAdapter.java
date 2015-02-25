package com.findmycoach.mentor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.findmycoach.mentor.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by IgluLabs on 1/23/2015.
 */
public class ChatWidgetAdapter  extends ArrayAdapter<String> {


    private Context context;
    // Getting all messages in a list
    private ArrayList<String> messageList;
    // For determining messages are received or sent (by mapping with messageList. 0=sent, 1=received)
    private ArrayList<Integer> senderList;
    // For determining message type i.e. text/image/video (0=text, 1=image, 2=video)
    private ArrayList<Integer> messageType;

    public ChatWidgetAdapter(Context context, ArrayList<String> messageList, ArrayList<Integer> sender, ArrayList<Integer> messageType ) {
        super(context, R.layout.signle_chat_cointainer_sent, messageList);
        this.context = context;
        this.messageList = messageList;
        this.senderList = sender;
        this.messageType = messageType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;

        if(senderList.get(position) == 1 && messageType.get(position) == 0) {
            rowView = inflater.inflate(R.layout.signle_chat_cointainer_received, parent, false);
            showTextMsg(rowView, position);
        }
        else if(senderList.get(position) == 1 && messageType.get(position) == 1) {
            rowView = inflater.inflate(R.layout.signle_chat_cointainer_received_image_video, parent, false);
            showImageMsg(rowView, position);
        }
        else if(senderList.get(position) == 1 && messageType.get(position) == 2) {
            rowView = inflater.inflate(R.layout.signle_chat_cointainer_received_image_video, parent, false);
            showVideoMsg(rowView, position);
        }
        else if(senderList.get(position) == 0 && messageType.get(position) == 0) {
            rowView = inflater.inflate(R.layout.signle_chat_cointainer_sent, parent, false);
            showTextMsg(rowView, position);
        }
        else if(senderList.get(position) == 0 && messageType.get(position) == 1) {
            rowView = inflater.inflate(R.layout.signle_chat_cointainer_sent_image_video, parent, false);
            showImageMsg(rowView, position);
        }
        else if(senderList.get(position) == 0 && messageType.get(position) == 2) {
            rowView = inflater.inflate(R.layout.signle_chat_cointainer_sent_image_video, parent, false);
            showVideoMsg(rowView, position);
        }
        return rowView;
    }

    private void showTextMsg(View v, int position){
        final TextView msgTextView = (TextView) v.findViewById(R.id.messageTV);
        msgTextView.setText(messageList.get(position));
    }

    private void showImageMsg(View v, int position){
        final ImageView imageView = (ImageView) v.findViewById(R.id.mediaIV);
        Picasso.with(context)
                .load(messageList.get(position))
                .into(imageView);
    }

    private void showVideoMsg(View v, int position){
        final ImageView imageView = (ImageView) v.findViewById(R.id.mediaIV);
//        Picasso.with(context)
//                .load(messageList.get(position))
//                .into(imageView);
    }

    public void updateMessageList(String msg, int sender, int messageType){
        this.messageList.add(messageList.size(),msg);
        this.senderList.add(sender);
        this.messageType.add(messageType);
    }
}
