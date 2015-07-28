package com.findmycoach.app.util;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.student.Address;
import com.findmycoach.app.views.ChizzleEditText;

import java.util.ArrayList;


public class TeachingMediumPreferenceDialog
{

    private Context context;
    private Dialog dialog;
    private Spinner language1,language2,language3,language4;
    private ArrayList<String> languageList;
    private Button doneButton;
    private Button cancelButton;
    private TeachingMediumAddedListener mTeachingMediumAddedListener;

    public TeachingMediumPreferenceDialog(Context context,ArrayList<String> languageList) {
        this.context = context;
        this.languageList=languageList;


    }

    public void showPopUp() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.teaching_medium_preference_dialog);
        language1=(Spinner)dialog.findViewById(R.id.language1);
        language2=(Spinner)dialog.findViewById(R.id.language2);
        language3=(Spinner)dialog.findViewById(R.id.language3);
        language4=(Spinner)dialog.findViewById(R.id.language4);
        doneButton=(Button)dialog.findViewById(R.id.done);
        cancelButton=(Button)dialog.findViewById(R.id.cancel);

        language1.setAdapter(new ArrayAdapter<String>(context, R.layout.textview,languageList));
        language2.setAdapter(new ArrayAdapter<String>(context, R.layout.textview,languageList));
        language3.setAdapter(new ArrayAdapter<String>(context, R.layout.textview,languageList));
        language4.setAdapter(new ArrayAdapter<String>(context, R.layout.textview,languageList));
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 onTeachingMediumAdded(language1.getSelectedItem().toString(),language2.getSelectedItem().toString(),language3.getSelectedItem().toString(),language4.getSelectedItem().toString());
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

    public void setTeachingMediumAddedListener(
            TeachingMediumAddedListener teachingMediumAddedListener) {
        mTeachingMediumAddedListener = teachingMediumAddedListener;
    }

    public void onTeachingMediumAdded (String language1,String language2,String language3,String language4) {
        mTeachingMediumAddedListener.onTeachingMediumAdded(language1,language2,language3,language4);
    }

    public interface TeachingMediumAddedListener {
        public void onTeachingMediumAdded(String language1,String language2,String language3,String language4);
    }


}
