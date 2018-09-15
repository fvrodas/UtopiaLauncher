package io.launcher.utopia.breceivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.activities.AppsActivity;

/**
 * Created by fernando on 10/29/17.
 */

public class ApplicationsReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        UtopiaLauncher app = (UtopiaLauncher) context.getApplicationContext();

        app.applicationsInstalled = new SparseArray<>();

        Intent i = new Intent(context, AppsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
