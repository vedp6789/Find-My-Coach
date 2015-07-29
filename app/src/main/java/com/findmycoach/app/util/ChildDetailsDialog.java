package com.findmycoach.app.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.student.ChildDetails;
import com.findmycoach.app.views.ChizzleAutoCompleteTextView;
import com.findmycoach.app.views.DobPicker;

import java.util.Calendar;

/**
 * Created by abhi7 on 24/07/15.
 */
public class ChildDetailsDialog {

    private Context context;
    private Dialog dialog;
    private Spinner genderSpinner;
    private Button doneButton;
    private Button cancelButton;
    private ChildDetails childDetails;
    private ChizzleAutoCompleteTextView childName;
    private ChildDetailsAddedListener mChildDetailsAddedListener;

    public ChildDetailsDialog(Context context) {
        this.context = context;


    }

    public void setChildAddedListener(
            ChildDetailsAddedListener childDetailsAddedListener) {
        mChildDetailsAddedListener = childDetailsAddedListener;
    }

    public void onChildDetailsAdded(ChildDetails childDetails) {
        mChildDetailsAddedListener.onChildDetailsAdded(childDetails);
    }


    public void showPopUp() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.child_details_layout);
        genderSpinner = (Spinner) dialog.findViewById(R.id.childGender);
        doneButton = (Button) dialog.findViewById(R.id.done);
        childName = (ChizzleAutoCompleteTextView) dialog.findViewById(R.id.childName);
        cancelButton = (Button) dialog.findViewById(R.id.cancel);
        final TextView childDOB = (TextView) dialog.findViewById(R.id.childDOB);
        childDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DobPicker(context, childDOB,
                        context.getResources().getInteger(R.integer.starting_year_child),
                        Calendar.getInstance().get(Calendar.YEAR) - context.getResources().getInteger(R.integer.mentee_min_age_child));
            }
        });
        genderSpinner.setAdapter(new ArrayAdapter<String>(context, R.layout.textview, context.getResources().getStringArray(R.array.gender)));
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childDetails = new ChildDetails();
                childDetails.setGender(genderSpinner.getSelectedItem().toString());
                childDetails.setName(childName.getText().toString());
                childDetails.setDob(childDOB.getText().toString());
                onChildDetailsAdded(childDetails);
                dialog.dismiss();

            }
        });

        dialog.show();


    }

    public interface ChildDetailsAddedListener {
        public void onChildDetailsAdded(ChildDetails childDetails);
    }


}
