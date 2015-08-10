package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.findmycoach.app.R;
import com.findmycoach.app.adapter.MentorNotificationTabsPagerAdapter;
import com.findmycoach.app.adapter.PromotionsViewPagerAdapter;
import com.findmycoach.app.beans.Promotions;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.google.android.gms.analytics.ecommerce.Promotion;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

public class MyPromotions extends FragmentActivity implements Callback{
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ViewPager promotionsViewpager;
    private PromotionsViewPagerAdapter promotionsViewPagerAdapter;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String TAG = "FMC";
    private String tag;
    boolean after_action;  /* using for tab change after reload from action accetp or reject*/
    int move_to_TAB;  /* this will help in moving to different view pager page*/
    public static MyPromotions myPromotions;
    private static int tab_to_show = 0;
    private ImageView iv_add_promotions, iv_back_button;
    private TextView tv_title;
    private ArrayList<Promotions> promotionArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_promotions);
        init();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.title);
        iv_add_promotions = (ImageView) findViewById(R.id.menuItem);
        iv_back_button = (ImageView) findViewById(R.id.backButton);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.promotionsTab);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        promotionsViewpager = (ViewPager) findViewById(R.id.vp_promotions);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        myPromotions = this;
        promotionArrayList = new ArrayList<>();

        tv_title.setText(getResources().getString(R.string.my_promotions));
        promotionsViewPagerAdapter = new PromotionsViewPagerAdapter(getSupportFragmentManager(), this, promotionArrayList);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
    }

    private void getAllPromotions(boolean activePromotions) {
        RequestParams requestParams = new RequestParams();
        if (activePromotions)
            requestParams.add("is_active", "1");
        else
            requestParams.add("is_active", "0");

        if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
            progressDialog.show();
        NetworkClient.getAllPromotions(this,requestParams, StorageHelper.getUserGroup(MyPromotions.this,"auth_token"),this,58);
    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myPromotions=null;
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

    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {

    }
}
