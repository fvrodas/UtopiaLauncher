package io.launcher.utopia;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.util.ArrayList;

/**
 * Created by fernando on 10/22/17.
 */

public class UtopiaLauncher extends Application {
    public static final String COLUMNS_SETTINGS = "columns";
    public static final String DOCK = "dock";
    private static final int cacheSize = 16 * 1024 * 1024;
    public ArrayList<ResolveInfo> applicationsInstalled = new ArrayList<>();
    public SharedPreferences launcherSettings;

    public static LruCache<String, Bitmap> iconsCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        launcherSettings = getSharedPreferences("UtopiaSettings", MODE_PRIVATE);
    }
}
