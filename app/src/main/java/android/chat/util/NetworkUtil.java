/*
 * Copyright © 2016, Craftsvilla.com
 * Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package android.chat.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Mahesh Nayak on 19-01-2016.
 */
public class NetworkUtil {

    public static boolean isAvailable(Context context) {
        ConnectivityManager cm          = (ConnectivityManager ) context.getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo         networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
