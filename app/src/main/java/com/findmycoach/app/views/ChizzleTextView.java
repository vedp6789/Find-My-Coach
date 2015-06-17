package com.findmycoach.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.findmycoach.app.util.AppFonts;

/**
 * Created by ShekharKG on 17/6/15.
 */
public class ChizzleTextView extends TextView {

    public ChizzleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

    public ChizzleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

    public ChizzleTextView(Context context) {
        super(context);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

}
