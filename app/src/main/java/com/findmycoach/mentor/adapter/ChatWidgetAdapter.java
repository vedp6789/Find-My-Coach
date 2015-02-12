package com.findmycoach.mentor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fmc.mentor.findmycoach.R;
import java.util.ArrayList;

/**
 * Created by IgluLabs on 1/23/2015.
 */
public class ChatWidgetAdapter  extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> messageList;
    private ArrayList<Integer> senderList;

    public ChatWidgetAdapter(Context context, ArrayList<String> messageList, ArrayList<Integer> sender ) {
        super(context, R.layout.signle_chat_cointainer_sent, messageList);
        this.context = context;
        this.messageList = messageList;
        this.senderList = sender;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;
        if(senderList.get(position) == 1)
           rowView = inflater.inflate(R.layout.signle_chat_cointainer_received, parent, false);
        else
            rowView = inflater.inflate(R.layout.signle_chat_cointainer_sent, parent, false);

        final TextView msgTextView = (TextView) rowView.findViewById(R.id.messageTV);
        msgTextView.setText(messageList.get(position));
        return rowView;
    }

    public void updateMessageList(String msg, int sender){
        this.messageList.add(messageList.size(),msg);
        this.senderList.add(sender);
    }
}
