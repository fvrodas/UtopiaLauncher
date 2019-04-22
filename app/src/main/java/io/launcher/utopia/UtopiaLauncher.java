package io.launcher.utopia;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

/**
 * Created by fernando on 10/22/17.
 */

public class UtopiaLauncher extends Application {
    public static final String COLUMNS_SETTINGS = "columns";
    public static final String DOCK = "dock";
    public static final String LAUNCHER = "appsList";
    private static final int cacheSize = 16 * 1024 * 1024;
    public SharedPreferences launcherSettings;
    public boolean refreshNeeded = false;

    public static LruCache<String, Bitmap> iconsCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(@NonNull String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        launcherSettings = getSharedPreferences("UtopiaSettings", MODE_PRIVATE);
    }
}
