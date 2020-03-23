package com.effseele.effilearn.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class


HideKeyboard {
    public static void hideKeyboard(Context mContext)
    {
        try {
            InputMethodManager imm = (InputMethodManager) mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(((Activity) mContext).getWindow()
                    .getCurrentFocus().getWindowToken(), 0);
        }catch (Exception ex)
        {}
    }
}
