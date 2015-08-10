package com.findmycoach.app.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.Promotions;

import java.util.ArrayList;

/**
 * Created by ved on 10/8/15.
 */
public class PromotionsViewPagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<Promotions> promotions;
    Context context;
    private String[] titles;
    private String TAG = "fmc";

    public PromotionsViewPagerAdapter(FragmentManager fm, Context context, ArrayList<Promotions> promotions) {
        super(fm);
        this.promotions = promotions;
        this.context = context;
        titles = context.getResources().getStringArray(R.array.promotions_pager_titles);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.d(TAG, "active promotions");

                return null;


            case 1:
                Log.d(TAG, "inactive promotions");

                return null;

        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
