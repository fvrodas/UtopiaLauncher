package io.launcher.utopia.presenters;

import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

import io.launcher.utopia.views.SettingsView;

import static io.launcher.utopia.UtopiaLauncher.COLUMNS_SETTINGS;

public class SettingsPresenter extends BasePresenter<SettingsView> {
    WeakReference<SharedPreferences> mPrefs;

    public SettingsPresenter(SharedPreferences prefs) {
        this.mPrefs = new WeakReference<>(prefs);
    }

    public void readIntFromSettings(String key, int defaultValue) {
        int value = mPrefs.get().getInt(key, defaultValue);
        mView.get().onIntReadFromSettings(key, value);
    }

    public int writeIntIntoSettings(String key, int value) {
        SharedPreferences.Editor editor = mPrefs.get().edit();
        editor.putInt(key, value);
        editor.apply();
        return value;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPrefs.clear();
    }
}
