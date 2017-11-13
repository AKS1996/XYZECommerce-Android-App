package com.xyzecommerce;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class Utils {
    static boolean IS_USER_lOGGEDIN;
    static String PROFILE_NAME = "firstname";
    static String PROFILE_ID = "facebook_id";
    static String CELL = "cell_number";
    static String SHARED_PREF = "mSharedPref";
    static String LoggedIn = "loggedIn";

    static boolean isOnline(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo info2 = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return (info != null && info.isConnectedOrConnecting())||(info2 != null && info2.isConnectedOrConnecting());
        }
        return false;
    }
}