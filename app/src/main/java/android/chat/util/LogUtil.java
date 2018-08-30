/*
 * Copyright © 2016, Craftsvilla.com
 *  Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package android.chat.util;

import android.util.Log;


/**
 * Created by Sibaprasad on 18-11-2016.
 */
public class LogUtil {
    private static final boolean IS_DEBUG = BuildConfig.isDebugable;

    /**
     * To Show the Logs using tag and msg
     *
     * @param tag
     * @param msg
     */
    public static void Log( String tag, String msg) {
        if (IS_DEBUG)
            Log.d( tag, msg);
    }

}
