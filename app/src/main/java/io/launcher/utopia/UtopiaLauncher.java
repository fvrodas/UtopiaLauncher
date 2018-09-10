package io.launcher.utopia;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.SparseArray;

import io.launcher.utopia.models.AppInfo;

/**
 * Created by fernando on 10/22/17.
 */

public class UtopiaLauncher extends Application {
    public static final String COLUMNS_SETTINGS = "columns";
    public SparseArray<AppInfo> applicationsInstalled = new SparseArray<>();
    public SharedPreferences launcherSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        launcherSettings = getSharedPreferences("UtopiaSettings", MODE_PRIVATE);
    }
}
