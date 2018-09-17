package io.launcher.utopia.breceivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import io.launcher.utopia.activities.AppsActivity;

/**
 * Created by fernando on 10/29/17.
 */

public class ApplicationsReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        AppsActivity.lastIntent = intent;
    }
}
