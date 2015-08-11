package com.findmycoach.app.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.EditProfileActivityMentor;
import com.findmycoach.app.adapter.MediumOfEducationAdapter;

import java.util.ArrayList;
import java.util.List;


public class TeachingMediumPreferenceDialog {

    private Context context;
    private Dialog dialog;
    private ListView mediumOfEducationLV;
    private List<String> languageList;
    private List<String> isSelected;

    public TeachingMediumPreferenceDialog(Context context, List<String> languageList, String preSelected) {
        this.context = context;
        this.languageList = languageList;
        isSelected = new ArrayList<>();
        for (String s : languageList)
            isSelected.add("false");

        try {
            String[] temp = preSelected.split(",");
            for (int i = 0; i < languageList.size(); i++) {
                for (String s : temp) {
                    if (languageList.get(i).equalsIgnoreCase(s.trim()))
                        isSelected.set(i, "true");
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void showPopUp() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.teaching_medium_preference_dialog);
        mediumOfEducationLV = (ListView) dialog.findViewById(R.id.mediumOfEducationLV);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
        Button doneButton = (Button) dialog.findViewById(R.id.done);

        final MediumOfEducationAdapter mediumOfEducationAdapter = new MediumOfEducationAdapter(languageList, isSelected, context);
        mediumOfEducationLV.setAdapter(mediumOfEducationAdapter);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditProfileActivityMentor) context).onTeachingMediumAdded(mediumOfEducationAdapter.selectedLanguages);
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
