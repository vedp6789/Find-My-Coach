package com.findmycoach.app.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ved on 17/8/15.
 */
public class CustomViewPagerSwipeAllowed extends ViewPager {
    public CustomViewPagerSwipeAllowed(Context context) {
        super(context);
    }

    public CustomViewPagerSwipeAllowed(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

}
