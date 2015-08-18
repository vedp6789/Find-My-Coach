package com.findmycoach.app.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.findmycoach.app.activity.MyPromotions;
import com.findmycoach.app.beans.Promotions.Offer;


import com.findmycoach.app.R;
import com.findmycoach.app.fragment.ActiveInactivePromotions;
import com.google.android.gms.analytics.ecommerce.Promotion;


import java.util.ArrayList;

/**
 * Created by ved on 10/8/15.
 */
public class PromotionsViewPagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<Offer> activePromotionsArrayList;
    ArrayList<Offer> inactivePromotionsArrayList;
    Context context;
    private String[] titles;
    private String TAG = "fmc";
    private MyPromotions myPromotions;

    public PromotionsViewPagerAdapter(FragmentManager fm, Context context, MyPromotions myPromotions, ArrayList<Offer> activePromotions, ArrayList<Offer> inactivePromotions) {
        super(fm);
        this.context = context;
        this.activePromotionsArrayList = activePromotions;
        this.inactivePromotionsArrayList = inactivePromotions;
        titles = context.getResources().getStringArray(R.array.promotions_pager_titles);
        this.myPromotions = myPromotions;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.e(TAG, "active promotions");
                /*if (myPromotions != null && !myPromotions.activePromotionsTabRefreshed) {
                    myPromotions.getAllPromotions(true);
                }*/
                return ActiveInactivePromotions.newInstance(activePromotionsArrayList, true);


            case 1:
                Log.e(TAG, "inactive promotions");
                /*if(myPromotions != null && !myPromotions.inactivePromotionsTabRefreshed){
                    myPromotions.getAllPromotions(false);
                }*/
                return ActiveInactivePromotions.newInstance(inactivePromotionsArrayList, false);

        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public void setArrayList(ArrayList<Offer> activePromotionsArrayList,ArrayList<Offer> inactivePromotionsArrayList) {
        this.activePromotionsArrayList=activePromotionsArrayList;
        this.inactivePromotionsArrayList=inactivePromotionsArrayList;
    }
}
