package com.findmycoach.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.findmycoach.app.util.AppFonts;

/**
 * Created by ShekharKG on 17/6/15.
 */
public class ChizzleAutoCompleteTextView extends AutoCompleteTextView {
    public ChizzleAutoCompleteTextView(Context context) {
        super(context);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

    public ChizzleAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

    public ChizzleAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(AppFonts.HelveticaNeueMedium);
    }

}
