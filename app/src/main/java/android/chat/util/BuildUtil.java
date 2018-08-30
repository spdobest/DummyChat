/*
 * Copyright © 2016, Craftsvilla.com
 *  Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package android.chat.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by Mahesh Nayak on 26-02-2016.
 */
public class BuildUtil {

    public static boolean isLollipopAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static String getDeviceId( Context ctx) {
        return Settings.Secure.getString( ctx.getContentResolver(),
                                          Settings.Secure.ANDROID_ID);
    }
}
