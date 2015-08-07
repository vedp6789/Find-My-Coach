package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.EditProfileActivityMentee;
import com.findmycoach.app.beans.student.ChildDetails;
import com.findmycoach.app.util.DateAsPerChizzle;

import java.util.ArrayList;

/**
 * Created by abhi7 on 27/07/15.
 */
public class ChildDetailsAdapter extends ArrayAdapter<ChildDetails> {

    private Context context;
    private ArrayList<ChildDetails> childDetailsArrayList;
    private LayoutInflater inflater;
    private int resourceId;
    private ListView childDetailsListView;

    public ChildDetailsAdapter(Context context, int resource, ArrayList<ChildDetails> childDetailsArrayList,ListView childDetailsListView) {
        super(context, resource);
        this.context = context;
        resourceId = resource;
        inflater = LayoutInflater.from(context);
        this.childDetailsArrayList = childDetailsArrayList;
        this.childDetailsListView=childDetailsListView;
    }

    @Override
    public int getCount() {
        return childDetailsArrayList.size();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.childName = (TextView) convertView.findViewById(R.id.childNameEditProfile);
            viewHolder.dobText = (TextView) convertView.findViewById(R.id.dobText);
            viewHolder.genderText = (TextView) convertView.findViewById(R.id.genderText);
            viewHolder.grade = (TextView) convertView.findViewById(R.id.gradeText);
            viewHolder.deleteButton=(ImageButton)convertView.findViewById(R.id.deleteButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.childName.setText(childDetailsArrayList.get(position).getName());
        if (DateAsPerChizzle.YYYY_MM_DD_into_DD_MM_YYYY(childDetailsArrayList.get(position).getDob()) != null) {
            viewHolder.dobText.setText(DateAsPerChizzle.YYYY_MM_DD_into_DD_MM_YYYY(childDetailsArrayList.get(position).getDob()));
        }
//        viewHolder.genderText.setText(childDetailsArrayList.get(position).getGender());
        String sex = childDetailsArrayList.get(position).getGender();

        if(childDetailsArrayList.get(position).getGrade()!=null)
                viewHolder.grade.setText(childDetailsArrayList.get(position).getGrade());
        if (sex.equalsIgnoreCase("M"))
            viewHolder.genderText.setText(context.getResources().getStringArray(R.array.gender)[0]);
        else if (sex.equalsIgnoreCase("F"))
            viewHolder.genderText.setText(context.getResources().getStringArray(R.array.gender)[1]);

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childDetailsArrayList.remove(position);
                notifyDataSetChanged();
                EditProfileActivityMentee.setListViewHeightBasedOnChildren(childDetailsListView);

            }
        });
        return convertView;
    }


    private class ViewHolder {
        private TextView childName, dobText, genderText,grade;
        private ImageButton deleteButton;
    }
}
