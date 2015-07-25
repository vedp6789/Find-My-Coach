package com.findmycoach.app.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.EditProfileActivityMentee;
import com.findmycoach.app.activity.EditProfileActivityMentor;

/**
 * Created by ShekharKG on 26/6/15.
 */
public class TermsAndCondition {


    public void showTermsAndConditions(final Activity context) {
        Log.e("TnC", "Dialog");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getResources().getString(R.string.t_and_c));
        ScrollView scrollView = new ScrollView(context);
        final TextView contentView = new TextView(context);
        contentView.setText(context.getResources().getString(R.string.terms));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(18, 18, 18, 18);
        scrollView.addView(contentView);
        scrollView.setLayoutParams(params);
        alertDialog.setView(scrollView);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateTermsAndConditionsStatus(context, "yes");
                    }
                }
        );
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        if(context instanceof EditProfileActivityMentee){
                            EditProfileActivityMentee activityMentee = (EditProfileActivityMentee) context;
                            activityMentee.needToCheckOnDestroy = true;
                            activityMentee.finish();
                        }else if(context instanceof EditProfileActivityMentor){
                            EditProfileActivityMentor activityMentor = (EditProfileActivityMentor) context;
                            activityMentor.needToCheckOnDestroy = true;
                            activityMentor.finish();
                        }

                        updateTermsAndConditionsStatus(context, "cancel");
                        if (DashboardActivity.dashboardActivity != null)
                            DashboardActivity.dashboardActivity.logout();


                    }
                }
        );
        alertDialog.show();
    }

    private void updateTermsAndConditionsStatus(Context context, String status) {
        StorageHelper.storePreference(context, "terms", status);
    }

}
