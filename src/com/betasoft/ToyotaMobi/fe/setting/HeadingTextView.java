package com.betasoft.ToyotaMobi.fe.setting;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class HeadingTextView extends TextView {

public HeadingTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
}

public HeadingTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
}

public HeadingTextView(Context context) {
    super(context);
    init();
}

private void init() {
    if (!isInEditMode()) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Bold.ttf");
        setTypeface(tf);
    }
}
}