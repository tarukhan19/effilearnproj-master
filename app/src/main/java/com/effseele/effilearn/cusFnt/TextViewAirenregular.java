package com.effseele.effilearn.cusFnt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;


@SuppressLint("AppCompatCustomView")
public class TextViewAirenregular extends AppCompatTextView {

    public TextViewAirenregular(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public TextViewAirenregular(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public TextViewAirenregular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/nunito-sans.extralight.ttf");
        setTypeface(customFont);
    }
}
