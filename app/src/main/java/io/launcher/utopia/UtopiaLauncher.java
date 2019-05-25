package io.launcher.utopia;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

import io.launcher.utopia.utils.IntentObservable;

/**
 * Created by fernando on 10/22/17.
 */

public class UtopiaLauncher extends Application {
    public static final String COLUMNS_SETTINGS = "columns";
    public static final String DOCK = "dock";
    private static final int cacheSize = 16 * 1024 * 1024;
    public SharedPreferences launcherSettings;
    public final IntentObservable observable = new IntentObservable();
    private static UtopiaLauncher sInstance = null;

    public static UtopiaLauncher getInstance() {
         return sInstance;
    }

    public LruCache<String, Bitmap> iconsCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(@NonNull String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        launcherSettings = getSharedPreferences("UtopiaSettings", MODE_PRIVATE);
        sInstance = this;
    }
}
