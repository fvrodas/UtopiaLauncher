package io.launcher.utopia;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.util.SparseArray;

import java.util.ArrayList;

import io.launcher.utopia.models.AppInfo;

/**
 * Created by fernando on 10/22/17.
 */

public class UtopiaLauncher extends Application {
    public static final String COLUMNS_SETTINGS = "columns";
    public ArrayList<ResolveInfo> applicationsInstalled = new ArrayList<>();
    public SharedPreferences launcherSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        launcherSettings = getSharedPreferences("UtopiaSettings", MODE_PRIVATE);
    }
}
