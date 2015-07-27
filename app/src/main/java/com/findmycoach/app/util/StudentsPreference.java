package com.findmycoach.app.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.StudentPreferenceSelection;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by ved on 27/7/15.
 */
public class StudentsPreference {
    JSONArray different_preferences;
    Context context;
    Dialog dialog;
    ImageView back_button;
    TextView title;
    ListView student_list_preference;
    StudentPreferenceSelection studentPreferenceSelection;
    ArrayList<Integer> selected_preference_arrayList;

    public StudentsPreference(Context context, JSONArray different_preferences) {
        this.context = context;
        this.different_preferences = different_preferences;
    }

    public ArrayList<Integer> showStudentPreferenceDialog() {
        selected_preference_arrayList = new ArrayList<>();
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.students_preferences);
        back_button = (ImageView) dialog.findViewById(R.id.backButton);
        title = (TextView) dialog.findViewById(R.id.title);
        title.setText(context.getResources().getString(R.string.students_preference));
        student_list_preference = (ListView) dialog.findViewById(R.id.student_preference_list);
        studentPreferenceSelection = new StudentPreferenceSelection(context, different_preferences);
        student_list_preference.setAdapter(studentPreferenceSelection);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_preference_arrayList = studentPreferenceSelection.integerArrayList;
                dialog.dismiss();
            }
        });
        dialog.show();
        return selected_preference_arrayList;
    }

}
