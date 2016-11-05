package com.denovo.denovo;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by abhinavkhushalani on 11/4/16.
 */

public class BoldTextView extends TextView {

    public BoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoldTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/JosefinSans-Bold" +
                ".ttf");
        setTypeface(tf);
    }

}