package com.findmycoach.app.util;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.StudentPreferenceSelection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ved on 27/7/15.
 */
public class StudentsPreference {
    JSONArray different_preferences;
    Context context;
    Dialog dialog;
    ImageView back_button;
    Button b_done;
    TextView title;
    ListView student_list_preference;
    StudentPreferenceSelection studentPreferenceSelection;
    ArrayList<Integer> id_of_selected_preferences;


    public StudentsPreference(Context context, JSONArray different_preferences, ArrayList<Integer> selected_preferences) {
        this.context = context;
        this.different_preferences = different_preferences;
        id_of_selected_preferences = selected_preferences;
    }

    public void showStudentPreferenceDialog() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.students_preferences);
        back_button = (ImageView) dialog.findViewById(R.id.backButton);
        title = (TextView) dialog.findViewById(R.id.title);
        title.setText(context.getResources().getString(R.string.students_preference));
        b_done = (Button) dialog.findViewById(R.id.b_done);
        student_list_preference = (ListView) dialog.findViewById(R.id.student_preference_list);
        studentPreferenceSelection = new StudentPreferenceSelection(context, different_preferences, id_of_selected_preferences);
        student_list_preference.setAdapter(studentPreferenceSelection);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        b_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> arrayList = studentPreferenceSelection.selected_preferences;

                for (int i : arrayList) {
                    Log.d("FMC", "list of pref: " + i);
                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }

}
