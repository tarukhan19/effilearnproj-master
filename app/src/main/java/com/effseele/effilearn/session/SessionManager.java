package com.effseele.effilearn.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private Editor editor;
    // Shared pref file name
    private static final String PREF_NAME = "salesformPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USERID= "userid";
    public static final String KEY_NAME= "name";
    public static final String KEY_EMAILID= "emailid";
    public static final String KEY_ISACTIVE= "isactive";
    public static final String KEY_MOBILENO= "mobileno";
    public static final String KEY_ISPAYMENT= "ispayment";
    public static final String KEY_PAYMENTAMOUNT= "paymentamount";


    // Constructor
    public SessionManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }





    public void logoutUser() {
        editor.clear();
        editor.commit();
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


    public void setPaymentAmount(String paymentAmount)
    {
        editor.putString(KEY_PAYMENTAMOUNT, paymentAmount);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getPaymentAmount() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_PAYMENTAMOUNT, pref.getString(KEY_PAYMENTAMOUNT, ""));
        return user;
    }


    public void setMobileNo(String mobileNo)
    {
        editor.putString(KEY_MOBILENO, mobileNo);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getMobileNo() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_MOBILENO, pref.getString(KEY_MOBILENO, ""));
        return user;
    }


    public void setLoginSession(String userid,String name, String emailid, String isPayment,String isActive)
    {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USERID, userid);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAILID, emailid);
        editor.putString(KEY_ISPAYMENT, isPayment);
        editor.putString(KEY_ISACTIVE, isActive);

        editor.commit();
    }

    public HashMap<String, String> getLoginSession() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USERID, pref.getString(KEY_USERID, ""));
        user.put(KEY_NAME, pref.getString(KEY_NAME, ""));
        user.put(KEY_EMAILID, pref.getString(KEY_EMAILID, ""));
        user.put(KEY_ISPAYMENT, pref.getString(KEY_ISPAYMENT, ""));
        user.put(KEY_ISACTIVE, pref.getString(KEY_ISACTIVE, ""));

        return user;
    }

}