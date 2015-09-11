package com.betasoft.ToyotaMobi.fe.setting;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ItalicTextView extends TextView {

public ItalicTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
}

public ItalicTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
}

public ItalicTextView(Context context) {
    super(context);
    init();
}

private void init() {
    if (!isInEditMode()) {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-BoldItalic.ttf");
        setTypeface(tf);
    }
}
}
