package com.findmycoach.app.adapter;

import android.content.Context;
import android.util.Log;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ved on 27/7/15.
 */
public class StudentPreferenceSelection extends BaseAdapter {
    Context context;
    public JSONArray jsonArray;
    public ArrayList<Integer> selected_preferences;
    String TAG = "FMC";


    public StudentPreferenceSelection(Context context, JSONArray jsonArray, ArrayList<Integer> selected_preferences) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.selected_preferences = selected_preferences;
        if (this.selected_preferences == null)
            this.selected_preferences = new ArrayList<Integer>();
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        JSONObject s = null;
        try {
            s = jsonArray.getJSONObject(position);
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
        StudentViewHolder studentViewHolder = null;
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
            final CheckBox checkBox = studentViewHolder.student_selection;

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(false);

            final JSONObject jsonObject = jsonArray.getJSONObject(position);

            String description = jsonObject.getString("description");
            final int id = Integer.parseInt(jsonObject.getString("id"));

            checkBox.setText(description);

            for (int i = 0; i < selected_preferences.size(); i++) {
                if (selected_preferences.contains(id)) {
                    checkBox.setChecked(true);
                }
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked && !selected_preferences.contains(id)) {
                        selected_preferences.add(id);
                    } else if (!isChecked && selected_preferences.contains(id)) {
                        selected_preferences.remove(selected_preferences.indexOf(id));
                    }

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return convertView;
    }


    class StudentViewHolder {
        CheckBox student_selection;
    }


}
