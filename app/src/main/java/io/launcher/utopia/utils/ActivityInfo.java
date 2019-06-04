package io.launcher.utopia.utils;

import android.graphics.Bitmap;

import java.io.Serializable;

import io.launcher.utopia.UtopiaLauncher;

public class ActivityInfo implements Serializable {
    private String packageName;
    private String label;

    public ActivityInfo(String packageName, String label) {
        this.packageName = packageName;
        this.label = label;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label.toUpperCase();
    }

    public Bitmap getIcon() {
        return UtopiaLauncher.getInstance().iconsCache.get(packageName);
    }
}

