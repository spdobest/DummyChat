/*
 * Copyright © 2016, Craftsvilla.com
 * Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package android.chat.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Mahesh Nayak on 19-01-2016.
 */
public class FragmentUtil {

    /**
     * Replaces fragment without adding it to the back stack .
     */
    public static void replaceFragment(FragmentActivity activity, Fragment fragment,
                                       int containerId) {
        FragmentManager manager = activity.getSupportFragmentManager();
        manager.beginTransaction().replace(containerId, fragment).commitAllowingStateLoss();
    }

    /**
     * Replaces fragment without adding it to the back stack .
     */
    public static void replaceFragment(FragmentActivity activity, Fragment fragment,
                                       int containerId, String tag) {
        FragmentManager manager = activity.getSupportFragmentManager();
        manager.beginTransaction().replace(containerId, fragment, tag).commitAllowingStateLoss();
    }


    /**
     * Replaces and adds the fragment to the back stack.
     */
    public static void replaceAndAddFragment(FragmentActivity activity, Fragment fragment, int containerId) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }

    public static void replaceAndAddFragment(FragmentActivity activity, Fragment fragment, int containerId, String tag) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, tag);
            transaction.addToBackStack(tag);
            transaction.commitAllowingStateLoss();
        }
    }

    public static void addFragment(FragmentActivity activity, Fragment fragment, int containerId) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.add(containerId, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }

    public static void popBackStackImmediate(FragmentActivity activity) {
        /*activity.getSupportFragmentManager().popBackStackImmediate(tag,
                code);*/
        activity.getSupportFragmentManager().popBackStackImmediate();
    }

    public static void replaceChildFragment(Fragment parentFragment, int containerId, Fragment frgmt) {
        if (parentFragment != null && !parentFragment.isDetached())
            parentFragment.getChildFragmentManager().beginTransaction().replace(containerId, frgmt).commitAllowingStateLoss();
    }

}
