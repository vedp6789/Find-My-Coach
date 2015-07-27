package com.findmycoach.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.CalendarSchedule.EventDuration;
import com.findmycoach.app.beans.CalendarSchedule.Mentee;
import com.findmycoach.app.beans.CalendarSchedule.Vacation;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ved on 27/7/15.
 */
public class StudentPreferenceSelection extends BaseAdapter {
    Context context;
    JSONArray jsonArray;
    public ArrayList<Integer> integerArrayList;  /* This is used to store the list of jsonArray index whose values are checked*/

    public StudentPreferenceSelection(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
        integerArrayList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        String s = null;
        try {
            s = jsonArray.getString(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        StudentViewHolder studentViewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.student_preference_selection, parent, false);
            studentViewHolder = new StudentViewHolder();

            studentViewHolder.student_selection = (CheckBox) convertView.findViewById(R.id.cb_student_pref);
            convertView.setTag(studentViewHolder);
        } else {
            studentViewHolder = (StudentViewHolder) convertView.getTag();
        }

        try {
            if (jsonArray.getString(position) != null) {
                studentViewHolder.student_selection.setText(jsonArray.getString(position));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        studentViewHolder.student_selection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    integerArrayList.add(position);
                } else {
                    integerArrayList.remove(position);
                }
            }
        });
        return convertView;
    }


    class StudentViewHolder {
        CheckBox student_selection;
    }


}
