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
import com.findmycoach.app.beans.authentication.AgeGroupPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ved on 27/7/15.
 */
public class StudentPreferenceSelection extends BaseAdapter {
    Context context;
    public ArrayList<Integer> selected_preferences;
    List<AgeGroupPreferences> different_preferences;
    String TAG = "FMC";
    private boolean willTeachEveryone;


    public StudentPreferenceSelection(Context context, List<AgeGroupPreferences> different_preferences, ArrayList<Integer> selected_preferences) {
        this.context = context;
        this.different_preferences = different_preferences;
        this.selected_preferences = selected_preferences;
        willTeachEveryone = false;
        if (this.selected_preferences == null)
            this.selected_preferences = new ArrayList<Integer>();
    }

    @Override
    public int getCount() {
        return different_preferences.size();
    }

    @Override
    public Object getItem(int position) {
        AgeGroupPreferences s = null;
        try {
            s = different_preferences.get(position);
        } catch (Exception e) {
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
            convertView = inflater.inflate(R.layout.qualified_coaching_single_row, parent, false);
            studentViewHolder = new StudentViewHolder();
            studentViewHolder.student_selection = (CheckBox) convertView.findViewById(R.id.checkbox);
            studentViewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(studentViewHolder);
        } else {
            studentViewHolder = (StudentViewHolder) convertView.getTag();
        }

        try {
            final CheckBox checkBox = studentViewHolder.student_selection;
            final TextView textView = studentViewHolder.textView;

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(false);
            convertView.setOnClickListener(null);

            final AgeGroupPreferences ageGroupPreferences = different_preferences.get(position);


            StringBuilder stringBuilder = new StringBuilder();
            String description = ageGroupPreferences.getValue();
            final int id = ageGroupPreferences.getId();
            String min = ageGroupPreferences.getMin();
            String max = ageGroupPreferences.getMax();
            stringBuilder.append(description);
            if (min != null && max != null) {
                if (min.equals("any") && max.equals("any")) {

                } else if (min.equals("any") && !max.equals("any")) {
                    stringBuilder.append(System.getProperty("line.separator"));
                    stringBuilder.append("(" + context.getResources().getString(R.string.up_to) + " " + max + " " + context.getResources().getString(R.string.years) + ")");
                } else if (!min.equals("any") && max.equals("any")) {
                    stringBuilder.append(System.getProperty("line.separator"));
                    stringBuilder.append("(" + min + " " + context.getResources().getString(R.string.and) + " " + context.getResources().getString(R.string.above) + ")");

                } else {
                    stringBuilder.append(System.getProperty("line.separator"));
                    stringBuilder.append("(" + min + " - " + max + " " + context.getResources().getString(R.string.years) + ")");
                }
            }

            textView.setText(stringBuilder);


            for (int i = 0; i < selected_preferences.size(); i++) {
                if (selected_preferences.contains(id)) {
                    checkBox.setChecked(true);
                    if(position == 0)
                        willTeachEveryone = true;
                }
            }

            checkBox.setEnabled(true);


            if (!willTeachEveryone) {
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked && !selected_preferences.contains(id)) {
                            selected_preferences.add(id);
                        } else if (!isChecked && selected_preferences.contains(id)) {
                            selected_preferences.remove(selected_preferences.indexOf(id));
                        }

                        if (position == 0 && isChecked) {
                            willTeachEveryone = true;
                            selected_preferences.clear();
                            selected_preferences.add(id);
                            notifyDataSetChanged();
                        }
                    }
                });

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkBox.setChecked(!checkBox.isChecked());
                    }
                });
            } else {
                if (position == 0) {
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            selected_preferences.clear();
                            willTeachEveryone = false;
                            notifyDataSetChanged();
                        }
                    });

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkBox.setChecked(!checkBox.isChecked());
                        }
                    });
                } else {
                    checkBox.setChecked(false);
                    checkBox.setEnabled(false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


    class StudentViewHolder {
        CheckBox student_selection;
        TextView textView;
    }


}
