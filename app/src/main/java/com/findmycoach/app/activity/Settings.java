package com.findmycoach.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment.ChangePasswordFragment;
import com.findmycoach.app.fragment.ChangePhoneNoFragment;
import com.findmycoach.app.util.StorageHelper;

/**
 * Created by prem on 29/1/15.
 */
public class Settings extends FragmentActivity implements View.OnClickListener, FragmentManager.OnBackStackChangedListener {

//    private ActionBar actionbar;

    LinearLayout ll_calendar_preferences;
    public static Settings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = this;
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settings = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("FMC", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        try {
            String loginWith = StorageHelper.getUserDetails(this, "login_with");
            Log.e("LOGIN", loginWith);
            ll_calendar_preferences = (LinearLayout) findViewById(R.id.ll_calendarPreferences);


            if (!loginWith.equalsIgnoreCase("Login"))
                findViewById(R.id.changePasswordLayout).setVisibility(View.GONE);
            if (DashboardActivity.dashboardActivity.user_group == 2) {
                findViewById(R.id.paymentOption).setVisibility(View.VISIBLE);
                ll_calendar_preferences.setVisibility(View.GONE);
                findViewById(R.id.ll_my_promotions).setVisibility(View.GONE);
            }

            findViewById(R.id.profileSettings).setOnClickListener(this);
            findViewById(R.id.change_password).setOnClickListener(this);
            findViewById(R.id.change_phone_no).setOnClickListener(this);
            findViewById(R.id.paymentMethods).setOnClickListener(this);
            findViewById(R.id.backButton).setOnClickListener(this);
            findViewById(R.id.tv_calendar_preferences).setOnClickListener(this);
            findViewById(R.id.tv_my_promotions).setOnClickListener(this);
            TextView textView = (TextView) findViewById(R.id.title);
            textView.setText(getResources().getString(R.string.action_settings));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.profileSettings:
                setContentView(R.layout.profile_edit);
                if (DashboardActivity.dashboardActivity.user_group == 3)
                    getSupportFragmentManager().beginTransaction().add(R.id.profileContainer, new com.findmycoach.app.fragment_mentor.ProfileFragment()).addToBackStack("edit_profile").commit();
                else if (DashboardActivity.dashboardActivity.user_group == 2)
                    getSupportFragmentManager().beginTransaction().add(R.id.profileContainer, new com.findmycoach.app.fragment_mentee.ProfileFragment()).addToBackStack("edit_profile").commit();
//                actionbar.setTitle(getResources().getString(R.string.profile));
                getSupportFragmentManager().addOnBackStackChangedListener(this);
                break;
            case R.id.change_password:
                FragmentManager fragmentManager = getSupportFragmentManager();
                ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                changePasswordFragment.show(fragmentManager, null);

                break;
            case R.id.change_phone_no:
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                ChangePhoneNoFragment changePhoneNoFragment = new ChangePhoneNoFragment();
                changePhoneNoFragment.show(fragmentManager1, null);
                break;

            case R.id.paymentMethods:
                startActivity(new Intent(this, PaymentDetailsActivity.class));
                break;

            case R.id.tv_calendar_preferences:
                startActivity(new Intent(this, CalendarPreferences.class));
                break;

            case R.id.tv_my_promotions:
                startActivity(new Intent(this,MyPromotions.class));
                break;

            case R.id.backButton:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("fmc", "Settings OnBackPressed");
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Log.e("fmc", "Back stack is empty");
            startActivity(new Intent(this, DashboardActivity.class));
        }
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                finish();
            else
                getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            setContentView(R.layout.activity_settings);
//            actionbar.setTitle(getResources().getString(R.string.action_settings));
            initView();
        }

    }
}
