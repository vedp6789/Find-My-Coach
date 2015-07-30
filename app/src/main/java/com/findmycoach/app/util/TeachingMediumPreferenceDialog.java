package com.findmycoach.app.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.findmycoach.app.R;

import java.util.ArrayList;


public class TeachingMediumPreferenceDialog {

    private Context context;
    private Dialog dialog;
    private Spinner language1, language2, language3, language4;
    private ArrayList<String> languageList;
    private TeachingMediumAddedListener mTeachingMediumAddedListener;
    private int selectedLng1, selectedLng2, selectedLng3, selectedLng4;

    public TeachingMediumPreferenceDialog(Context context, ArrayList<String> languageList, int lng1, int lng2, int lng3, int lng4) {
        this.context = context;
        this.languageList = languageList;
        selectedLng1 = lng1;
        selectedLng2 = lng2;
        selectedLng3 = lng3;
        selectedLng4 = lng4;

        if(selectedLng1 == 0){
            selectedLng1 = 1;
        }
        if(selectedLng2 == 0){
            if(selectedLng1 != 2)
                selectedLng2 = 2;
            else
                selectedLng1 = 1;
        }
        if(selectedLng3 == 0){
            if(selectedLng1 != 3 && selectedLng2 != 3)
                selectedLng3 = 3;
            else selectedLng3 = 4;
        }
    }

    public void showPopUp() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.teaching_medium_preference_dialog);
        language1 = (Spinner) dialog.findViewById(R.id.language1);
        language2 = (Spinner) dialog.findViewById(R.id.language2);
        language3 = (Spinner) dialog.findViewById(R.id.language3);
        language4 = (Spinner) dialog.findViewById(R.id.language4);
        Button doneButton = (Button) dialog.findViewById(R.id.done);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel);

        language1.setAdapter(new ArrayAdapter<String>(context, R.layout.textview, languageList));
        language2.setAdapter(new ArrayAdapter<String>(context, R.layout.textview, languageList));
        language3.setAdapter(new ArrayAdapter<String>(context, R.layout.textview, languageList));
        language4.setAdapter(new ArrayAdapter<String>(context, R.layout.textview, languageList));

        setSelectionSpinner();

        language1.setOnItemSelectedListener(onItemSelectedListener);
        language2.setOnItemSelectedListener(onItemSelectedListener);
        language3.setOnItemSelectedListener(onItemSelectedListener);
        language4.setOnItemSelectedListener(onItemSelectedListener);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTeachingMediumAdded(language1.getSelectedItem().toString(),
                        language2.getSelectedItem().toString(),
                        language3.getSelectedItem().toString(),
                        language4.getSelectedItem().toString());
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

    private void setSelectionSpinner() {
        language1.setSelection(selectedLng1);
        language2.setSelection(selectedLng2);
        language3.setSelection(selectedLng3);
        language4.setSelection(selectedLng4);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int lng1 = language1.getSelectedItemPosition();
            int lng2 = language2.getSelectedItemPosition();
            int lng3 = language3.getSelectedItemPosition();
            int lng4 = language4.getSelectedItemPosition();

            if(position > 0){

                int selectedCount = 0;

                if (lng1 == position)
                    selectedCount++;
                if (lng2 == position)
                    selectedCount++;
                if (lng3 == position)
                    selectedCount++;
                if (lng4 == position)
                    selectedCount++;

                if (selectedCount > 1) {
                    Toast toast = Toast.makeText(context, context.getResources().getString(R.string.language_already_selected), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    setSelectionSpinner();
                } else {
                    if (selectedLng1 != lng1) {
                        selectedLng1 = position;
                    } else if (selectedLng2 != lng2) {
                        selectedLng2 = position;
                    } else if (selectedLng3 != lng3) {
                        selectedLng3 = position;
                    } else if (selectedLng4 != lng4) {
                        selectedLng4 = position;
                    }
                }
            }else {
                if (selectedLng1 != lng1) {
                    selectedLng1 = position;
                } else if (selectedLng2 != lng2) {
                    selectedLng2 = position;
                } else if (selectedLng3 != lng3) {
                    selectedLng3 = position;
                } else if (selectedLng4 != lng4) {
                    selectedLng4 = position;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void setTeachingMediumAddedListener(
            TeachingMediumAddedListener teachingMediumAddedListener) {
        mTeachingMediumAddedListener = teachingMediumAddedListener;
    }

    public void onTeachingMediumAdded(String language1, String language2, String language3, String language4) {
        mTeachingMediumAddedListener.onTeachingMediumAdded(language1, language2, language3, language4);
    }

    public interface TeachingMediumAddedListener {
        void onTeachingMediumAdded(String language1, String language2, String language3, String language4);
    }


}
