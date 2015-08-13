package com.findmycoach.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.findmycoach.app.R;
import com.findmycoach.app.adapter.PromotionsViewPagerAdapter;
import com.findmycoach.app.beans.Promotions.Offer;
import com.findmycoach.app.beans.Promotions.Promotions;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.android.gms.wallet.OfferWalletObject;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

public class MyPromotions extends FragmentActivity implements Callback {
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ViewPager promotionsViewpager;
    private PromotionsViewPagerAdapter promotionsViewPagerAdapter;
    private ProgressDialog progressDialog;
    private String TAG = "FMC";
    //private String tag;
    //boolean after_action;  /* using for tab change after reload from action accetp or reject*/
    int move_to_TAB;  /* this will help in moving to different view pager page*/
    public static MyPromotions myPromotions;
    private static int tab_to_show = 0;
    private ImageView iv_add_promotions, iv_back_button;
    private TextView tv_title;
    private ArrayList<Offer> activePromotionArrayList, inactivePromotionArrayList;
    public boolean activePromotions, activePromotionsTabRefreshed, inactivePromotionsTabRefreshed;   /* activePromotionsTabRefreshed,inactivePromotionsTabRefreshed are used to get the status that whether api call for these conditions get successed or not*/
    int promotions_tab_state/*, gettingPromotionsForActive*/;  /* SharedPreference will state its value,   gettingPromotionsForActive is using to get the right arrayListPopulated */
    boolean newPromotionAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_promotions);
        initViews();
    }

    private void initViews() {
        tv_title = (TextView) findViewById(R.id.title);
        iv_add_promotions = (ImageView) findViewById(R.id.menuItem);
        iv_back_button = (ImageView) findViewById(R.id.backButton);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.promotionsTab);
        promotionsViewpager = (ViewPager) findViewById(R.id.vp_promotions);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        myPromotions = this;
        activePromotionArrayList = new ArrayList<>();
        inactivePromotionArrayList = new ArrayList<>();
        tv_title.setText(getResources().getString(R.string.my_promotions));
        iv_add_promotions.setImageDrawable(getResources().getDrawable(R.drawable.add_icon1));
        promotionsViewPagerAdapter = new PromotionsViewPagerAdapter(getSupportFragmentManager(), this, myPromotions, activePromotionArrayList, inactivePromotionArrayList);
        promotionsViewpager.setAdapter(promotionsViewPagerAdapter);
        pagerSlidingTabStrip.setShouldExpand(true);
        pagerSlidingTabStrip.setViewPager(promotionsViewpager);
        promotions_tab_state = StorageHelper.getPromotionsTabState(this);
        /*gettingPromotionsForActive = -1;*/
        if (promotions_tab_state == 0 || promotions_tab_state == 1) {
            promotionsViewpager.setCurrentItem(0);
        } else {
            promotionsViewpager.setCurrentItem(1);
        }

        promotionsViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e(TAG, "position: " + position + " positionOffset: " + positionOffset + "positionOffsetPixels");
            }

            @Override
            public void onPageSelected(int position) {
                promotionsViewpager.setCurrentItem(position);
                pagerSlidingTabStrip.setViewPager(promotionsViewpager);
                if (position == 0 && !activePromotionsTabRefreshed) {
                    getAllPromotions(true);
                }
                if (position == 1 && !inactivePromotionsTabRefreshed) {
                    getAllPromotions(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e(TAG, "state: " + state);
            }
        });


        iv_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_add_promotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyPromotions.this, AddPromotion.class));
            }
        });
    }


    public void onActivityCompletion() {
        newPromotionAdded = true;
       // getAllPromotions(true);
    }


    public void getAllPromotions(boolean activePromotions) {
        this.activePromotions = activePromotions;

        RequestParams requestParams = new RequestParams();
        if (activePromotions) {
            requestParams.add("is_active", "1");
      /*      gettingPromotionsForActive = 1;*/
        } else {
            requestParams.add("is_active", "0");
  /*          gettingPromotionsForActive = 0;*/
        }

        Log.e(TAG, "requestParams: " + requestParams.toString());
        progressDialog.show();
        NetworkClient.getAllPromotions(this, requestParams, StorageHelper.getUserGroup(MyPromotions.this, "auth_token"), this, 58);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (promotionsViewpager.getCurrentItem() == 0) {
            Log.e(TAG,"onResume");
            getAllPromotions(true);
        } else {
            getAllPromotions(false);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            myPromotions = null;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_promotions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        if (calledApiValue == 58) {

            // Log.e(TAG,"response 58: "+(String) object);
            Promotions promotions = (Promotions) object;

            if (activePromotions /*|| gettingPromotionsForActive == 1*/) {

                activePromotionArrayList = (ArrayList<Offer>) promotions.getPromotions();
                /*Log.e(TAG, "size fo arraylist: " + activePromotionArrayList.size());
                for(Offer offer:activePromotionArrayList ){
                    Log.e(TAG,offer.getId()+", "+offer.getUser_id()+", "+offer.getPromotion_title()+", "+offer.getPromotion_type()+", "+offer.getDiscount_percentage()+", "+offer.getFree_classes()+", "+offer.getFree_min_classes());
                }
                */
                activePromotionsTabRefreshed = true;
                if (newPromotionAdded) {
                    Log.e(TAG, "new promotion added");
                    newPromotionAdded = false;
                    promotionsViewpager.setCurrentItem(0);  /* when new promotion added then showing updated values as active promotion*/

                }
            } else {

                    if (newPromotionAdded) {
                        Log.e(TAG, "new promotion added");
                        activePromotionArrayList = (ArrayList<Offer>) promotions.getPromotions();
                        newPromotionAdded = false;
                        promotionsViewpager.setCurrentItem(0);  /* when new promotion added then showing updated values as active promotion*/

                    } else {
                        inactivePromotionArrayList = (ArrayList<Offer>) promotions.getPromotions();
                        inactivePromotionsTabRefreshed = true;
                    }


            }


            promotionsViewPagerAdapter.setArrayList(activePromotionArrayList, inactivePromotionArrayList);

            promotionsViewPagerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        if (calledApiValue == 48) {
            if (newPromotionAdded) {
                newPromotionAdded = false;  /* Making newPromotionAdded flag false in the condition, when promotion get added but getting all active promotion is having some problem*/
            }


        }


        Log.e(TAG, " Error while promotions api call: " + (String) object);
    }
}
