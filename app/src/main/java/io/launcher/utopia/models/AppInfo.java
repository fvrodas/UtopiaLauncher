package io.launcher.utopia.models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by fernando on 10/15/17.
 */

public class AppInfo {
    public CharSequence name;
    public CharSequence label;
    public Drawable icon;
    public int bgColor;
    public int bgColorDark;
    public int textColor;
    private Drawable cachedDrawable = null;

    public void setCachedBackground(Drawable cachedImage) {
        this.cachedDrawable = cachedImage;
    }

    public Drawable getCachedDrawable() {
        return cachedDrawable;
    }
}
