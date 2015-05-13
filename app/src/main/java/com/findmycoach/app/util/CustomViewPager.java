package com.findmycoach.app.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ved on 13/5/15.
 */
public class CustomViewPager extends ViewPager{
    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        return false;
    }
}
