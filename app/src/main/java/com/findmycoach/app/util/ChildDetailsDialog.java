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
import com.findmycoach.app.activity.AddNewSlotActivity;
import com.findmycoach.app.fragment.MyScheduleFragment;
import com.findmycoach.app.views.ChizzleTextView;
import com.findmycoach.app.views.DobPicker;

import java.util.Calendar;

/**
 * Created by abhi7 on 24/07/15.
 */
public class ChildDetailsDialog {

    private Context context;
    private Dialog dialog;
    private Spinner genderSpinner;

    public ChildDetailsDialog(Context context){
        this.context=context;


    }
    public void showPopUp() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.child_details_layout);
        genderSpinner=(Spinner)dialog.findViewById(R.id.childGender);
        final TextView childDOB=(TextView)dialog.findViewById(R.id.childDOB);
        childDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DobPicker(context, childDOB,
                        context.getResources().getInteger(R.integer.starting_year),
                        Calendar.getInstance().get(Calendar.YEAR) - context.getResources().getInteger(R.integer.mentee_min_age));
            }
        });
        genderSpinner.setAdapter(new ArrayAdapter<String>(context, R.layout.textview,context.getResources().getStringArray(R.array.gender)));

        dialog.show();

    }

}
