package com.findmycoach.app.activity;

import android.app.ActionBar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment.ChangePasswordFragment;
import com.findmycoach.app.fragment.ChangePhoneNoFragment;

/**
 * Created by prem on 29/1/15.
 */
public class Settings extends FragmentActivity implements View.OnClickListener, FragmentManager.OnBackStackChangedListener {

    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if(DashboardActivity.dashboardActivity.user_group == 2)
            findViewById(R.id.paymentOption).setVisibility(View.VISIBLE);
        initView();
    }

    private void initView() {
        findViewById(R.id.profileSettings).setOnClickListener(this);
        findViewById(R.id.change_password).setOnClickListener(this);
        findViewById(R.id.change_phone_no).setOnClickListener(this);
        findViewById(R.id.paymentMethods).setOnClickListener(this);
        applyActionbarProperties();
    }

    private void applyActionbarProperties() {
        actionbar = getActionBar();
        if(actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);
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
                actionbar.setTitle(getResources().getString(R.string.profile));
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
        }
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
            actionbar.setTitle(getResources().getString(R.string.action_settings));
            initView();
        }

    }
}
