package com.findmycoach.app.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.Session;
import com.findmycoach.app.R;
import com.findmycoach.app.activity.DashboardActivity;
import com.findmycoach.app.activity.EditProfileActivityMentee;
import com.findmycoach.app.activity.EditProfileActivityMentor;
import com.findmycoach.app.activity.LoginActivity;

/**
 * Created by ShekharKG on 26/6/15.
 */
public class TermsAndCondition {


    public void showTermsAndConditions(final Activity context) {
        Log.e("TnC", "Dialog");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getResources().getString(R.string.t_and_c));
//        ScrollView scrollView = new ScrollView(context);
//        final TextView contentView = new TextView(context);
//        contentView.setText(context.getResources().getString(R.string.terms));
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        params.setMargins(18, 18, 18, 18);
//        scrollView.addView(contentView);
//        scrollView.setLayoutParams(params);

        LayoutInflater inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.dialog_terms_conditions, null);
        alertDialog.setView(view);
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

                        if (context instanceof EditProfileActivityMentee) {
                            EditProfileActivityMentee activityMentee = (EditProfileActivityMentee) context;
                            activityMentee.needToCheckOnDestroy = true;
                            activityMentee.finish();
                        } else if (context instanceof EditProfileActivityMentor) {
                            EditProfileActivityMentor activityMentor = (EditProfileActivityMentor) context;
                            activityMentor.needToCheckOnDestroy = true;
                            activityMentor.finish();
                        }

                        updateTermsAndConditionsStatus(context, "cancel");
                        if (DashboardActivity.dashboardActivity != null)
                            DashboardActivity.dashboardActivity.logout();
                        else {
                            StorageHelper.clearUser(context);
                            logout(context);
                            context.startActivity(new Intent(context, LoginActivity.class));
                        }
                    }
                }
        );
        alertDialog.show();
    }

    private void updateTermsAndConditionsStatus(Context context, String status) {
        StorageHelper.storePreference(context, "terms", status);
    }


    public void logout(Context context) {

        String loginWith = StorageHelper.getUserDetails(context, "login_with");
        if (loginWith == null || loginWith.equals("G+")) {
            LoginActivity.doLogout = true;
            Log.e("FMC", "Logout G+ true");
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        StorageHelper.clearUser(context);
        StorageHelper.clearUserPhone(context);

        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
            }
        } else {
            session = new Session(context);
            Session.setActiveSession(session);
            session.closeAndClearTokenInformation();
        }
    }

}
