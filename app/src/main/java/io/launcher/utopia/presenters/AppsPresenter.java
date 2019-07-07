package io.launcher.utopia.presenters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import io.launcher.utopia.BuildConfig;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.utils.ActivityInfo;
import io.launcher.utopia.utils.SerializeHelper;
import io.launcher.utopia.utils.Tools;
import io.launcher.utopia.views.IAppsView;

public class AppsPresenter extends BasePresenter<IAppsView> {
    private UtopiaLauncher mApp;
    private SerializeHelper<ArrayList<ActivityInfo>> mHelper;

    public AppsPresenter(UtopiaLauncher app, SerializeHelper<ArrayList<ActivityInfo>> helper) {
        mApp = app;
        mHelper = helper;
    }

    @Deprecated
    public void updatePersistentDockList(ArrayList<ActivityInfo> apps) {
        SharedPreferences.Editor editor = mApp.launcherSettings.edit();
        editor.putString(UtopiaLauncher.DOCK, mHelper.serialize(apps));
        editor.apply();
    }

    public void readIntFromSettings(String key, int defaultValue) {
        int value = mApp.launcherSettings.getInt(key, defaultValue);
        mView.get().onIntReadFromSettings(key, value);
    }

    public void readPersistentDockList() {
        String json = mApp.launcherSettings.getString(UtopiaLauncher.DOCK, null);
        if (json != null) {
            try {
                ArrayList<ActivityInfo> data = mHelper.deserialize(json);
                mView.get().onDockItemsRetrieved(data);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }
        }
    }

    public void removeFromIconCache(ActivityInfo activityInfo) {
        if (mApp.iconsCache.get(activityInfo.getPackageName()) != null) {
            mApp.iconsCache.remove(activityInfo.getPackageName());
        }
    }

    public void removeFromIconCache(String app) {
        if (mApp.iconsCache.get(app) != null) {
            mApp.iconsCache.remove(app);
        }
    }

    public void retrieveApplicationsList(Bundle savedState, PackageManager pm) {
        if (savedState != null && savedState.getSerializable(AppItemPresenter.STATE_APPS) != null) {
            final ArrayList<ActivityInfo> apps =
                    (ArrayList<ActivityInfo>) savedState.getSerializable(AppItemPresenter.STATE_APPS);

            Collections.sort(apps, new Comparator<ActivityInfo>() {
                @Override
                public int compare(ActivityInfo appInfo, ActivityInfo t1) {
                    return appInfo.getLabel().toLowerCase()
                            .compareTo(t1.getLabel().toLowerCase());
                }
            });

            mView.get().populateApplicationsList(apps);
        } else {
            retrieveApplicationsList(pm);
        }
    }

    public void retrieveApplicationsList(final PackageManager pm) {
        mView.get().showProgress(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);

                final ArrayList<ActivityInfo> apps = new ArrayList<>();

                Iterator<ResolveInfo> it = pm.queryIntentActivities(intent, 0).iterator();
                if (it.hasNext()) {
                    do {
                        ResolveInfo item = it.next();
                        if (!BuildConfig.APPLICATION_ID.contains(item.activityInfo.packageName)) {
                            apps.add(new ActivityInfo(item.activityInfo.packageName,
                                    item.loadLabel(pm).toString()));
                            if (mApp.iconsCache.get(item.activityInfo.packageName) == null) {
                                mApp.iconsCache.put(
                                        item.activityInfo.packageName,
                                        Tools.createIcon(mApp.getApplicationContext(),
                                                Tools.getBitmapFromDrawable(
                                                        item.activityInfo.loadIcon(pm),
                                                        Bitmap.Config.ARGB_8888
                                                )
                                        )
                                );
                            }
                        }
                    } while (it.hasNext());
                }

                Collections.sort(apps, new Comparator<ActivityInfo>() {
                    @Override
                    public int compare(ActivityInfo appInfo, ActivityInfo t1) {
                        return appInfo.getLabel().toLowerCase()
                                .compareTo(t1.getLabel().toLowerCase());
                    }
                });

                mView.get().populateApplicationsList(apps);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApp = null;
        mHelper = null;
    }
}
