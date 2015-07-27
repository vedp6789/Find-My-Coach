package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.student.ChildDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by abhi7 on 27/07/15.
 */
public class ChildDetailsAdapter extends ArrayAdapter<ChildDetails> {

    private Context context;
    private ArrayList<ChildDetails> childDetailsArrayList;
    private LayoutInflater inflater;
    private int resourceId;

    public ChildDetailsAdapter(Context context,int resource,ArrayList<ChildDetails> childDetailsArrayList){
        super(context,resource);
        this.context=context;
        resourceId=resource;
        inflater = LayoutInflater.from(context);
        this.childDetailsArrayList=childDetailsArrayList;

    }
    @Override
    public int getCount() {
      return  childDetailsArrayList.size();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.childName=(TextView)convertView.findViewById(R.id.childNameEditProfile);
            viewHolder.dobText=(TextView)convertView.findViewById(R.id.dobText);
            viewHolder.genderText=(TextView)convertView.findViewById(R.id.genderText);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.childName.setText(childDetailsArrayList.get(position).getName());
        viewHolder.dobText.setText(childDetailsArrayList.get(position).getDob());
        viewHolder.genderText.setText(childDetailsArrayList.get(position).getGender());

        return convertView;
    }







    private class ViewHolder {
        private TextView childName,dobText,genderText;


    }
}
