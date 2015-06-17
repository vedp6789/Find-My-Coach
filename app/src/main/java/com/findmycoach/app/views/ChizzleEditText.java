package com.findmycoach.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.findmycoach.app.util.AppFonts;

/**
 * Created by ShekharKG on 17/6/15.
 */
public class ChizzleEditText extends EditText {
    public ChizzleEditText(Context context) {
        super(context);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

    public ChizzleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

    public ChizzleEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

}
