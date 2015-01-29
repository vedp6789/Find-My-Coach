package com.findmycoach.mentor.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;

import com.findmycoach.mentor.fragment.ProfileFragment;
import com.fmc.mentor.findmycoach.R;

/**
 * Created by prem on 29/1/15.
 */
public class Settings extends FragmentActivity implements View.OnClickListener, FragmentManager.OnBackStackChangedListener {

    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    private void initView() {
        findViewById(R.id.profileSettings).setOnClickListener(this);
        applyActionbarProperties();
    }

    private void applyActionbarProperties() {
        actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.profileSettings:
                setContentView(R.layout.profile_edit);
                getSupportFragmentManager().beginTransaction().add(R.id.profileContainer,new ProfileFragment()).addToBackStack("edit_profile").commit();
                actionbar.setTitle("Profile");
                getSupportFragmentManager().addOnBackStackChangedListener(this);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(getSupportFragmentManager().getBackStackEntryCount() == 0)
                finish();
            else
                getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            setContentView(R.layout.activity_settings);
            actionbar.setTitle(getResources().getString(R.string.action_settings));
            initView();
        }

    }
}
