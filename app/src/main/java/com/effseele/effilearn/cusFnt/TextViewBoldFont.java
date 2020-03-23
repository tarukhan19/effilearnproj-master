package com.effseele.effilearn.cusFnt;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewBoldFont extends TextView {

    public TextViewBoldFont(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public TextViewBoldFont(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public TextViewBoldFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(),"fonts/nunito-sans.semibold.ttf");
        setTypeface(customFont);
    }
}
