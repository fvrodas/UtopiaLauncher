package io.launcher.utopia.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import io.launcher.utopia.UtopiaLauncher;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.content.Intent.ACTION_PACKAGE_REPLACED;

public class UtopiaService extends Service {
    public static boolean isRunning = false;

    private static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    private BroadcastReceiver packageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UtopiaLauncher app = (UtopiaLauncher) getApplication();
            app.refreshNeeded = true;
        }
    };
    private IntentFilter intentFilter = new IntentFilter();

    public UtopiaService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter.addDataScheme("package");
        intentFilter.addAction(ACTION_PACKAGE_ADDED);
        intentFilter.addAction(ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(ACTION_PACKAGE_REPLACED);
        registerReceiver(packageReceiver, intentFilter);
        isRunning = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(packageReceiver);
        isRunning = false;
    }

    @androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
