package com.findmycoach.mentor.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.findmycoach.mentor.fragment.HomeFragment;
import com.findmycoach.mentor.fragment.MyConnectionsFragment;
import com.findmycoach.mentor.fragment.MyScheduleFragment;
import com.findmycoach.mentor.fragment.NavigationDrawerFragment;
import com.findmycoach.mentor.fragment.NotificationsFragment;
import com.findmycoach.mentor.fragment.ProfileFragment;
import com.findmycoach.mentor.fragment.SettingsFragment;
import com.fmc.mentor.findmycoach.R;

public class DashboardActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initialize();
    }

    private void initialize() {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        onNavigationDrawerItemSelected(0);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (position == 0)
            fragmentTransaction.replace(R.id.container, new HomeFragment());
        if (position == 1)
            fragmentTransaction.replace(R.id.container, new ProfileFragment());
        if (position == 2)
            fragmentTransaction.replace(R.id.container, new NotificationsFragment());
        if (position == 3)
            fragmentTransaction.replace(R.id.container, new MyConnectionsFragment());
        if (position == 4)
            fragmentTransaction.replace(R.id.container, new MyScheduleFragment());
        if (position == 5)
            fragmentTransaction.replace(R.id.container, new SettingsFragment());

        fragmentTransaction.commit();
        onSectionAttached(position);
    }

    public void onSectionAttached(int number) {
        mTitle = getResources().getStringArray(R.array.navigation_items)[number];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.dashboard, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
