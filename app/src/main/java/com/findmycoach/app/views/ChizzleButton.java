package com.findmycoach.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.findmycoach.app.util.AppFonts;

/**
 * Created by ShekharKG on 17/6/15.
 */
public class ChizzleButton extends Button {

    public ChizzleButton(Context context) {
        super(context);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

    public ChizzleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

    public ChizzleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

}
