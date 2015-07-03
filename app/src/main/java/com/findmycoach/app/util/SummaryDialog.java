package com.findmycoach.app.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.findmycoach.app.R;
import com.findmycoach.app.views.ChizzleTextView;

import java.util.ArrayList;

/**
 * Created by abhi7 on 03/07/15.
 */
public class SummaryDialog {

    private  Context context;
    private Dialog dialog;
    private ChizzleTextView subjectTextView,startDateTextView,stopDateTextView,timingTextView,weekdaysTextView,slotTypeTextView,locationTextView;
    private String subject,startDate,stopDate,timing,weekdays,slotType,location;
    private Button okButton;
    public SummaryDialog(Context context,String subject,String startDate, String stopDate, String timing, String weekdays, String slotType,String location) {
        this.context = context;
        this.subject=subject;
        this.startDate=startDate;
        this.stopDate=stopDate;
        this.timing=timing;
        this.weekdays=weekdays;
        this.slotType=slotType;
        this.location=location;

    }

    public void showPopUp() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.summary_dialog_layout);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        subjectTextView=(ChizzleTextView)dialog.findViewById(R.id.subjectValue);
        startDateTextView=(ChizzleTextView)dialog.findViewById(R.id.startDateValue);
        stopDateTextView=(ChizzleTextView)dialog.findViewById(R.id.stopDateValue);
        weekdaysTextView=(ChizzleTextView)dialog.findViewById(R.id.weekdaysValue);
        slotTypeTextView=(ChizzleTextView)dialog.findViewById(R.id.slotTypeValue);
        timingTextView=(ChizzleTextView)dialog.findViewById(R.id.timingValue);
        locationTextView=(ChizzleTextView)dialog.findViewById(R.id.locationValue);
        okButton=(Button)dialog.findViewById(R.id.okButtonSummary);
        subjectTextView.setText(subject);
        startDateTextView.setText(startDate);
        stopDateTextView.setText(stopDate);
        weekdaysTextView.setText(weekdays);
        slotTypeTextView.setText(slotType);
        timingTextView.setText(timing);
        locationTextView.setText(location);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    dialog.show();

    }
}