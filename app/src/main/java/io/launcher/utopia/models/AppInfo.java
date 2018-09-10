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
    private Bitmap cachedImage = null;

    public void setCachedImage(Bitmap cachedImage) {
        this.cachedImage = cachedImage;
    }

    public Bitmap getCachedImage() {
        return cachedImage;
    }
}
