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

    public int saveColumnSetting(int i) {
        SharedPreferences.Editor editor = mPrefs.get().edit();
        editor.putInt(COLUMNS_SETTINGS, i);
        editor.apply();
        return i;
    }

    public void getColumnsSetting() {
        mView.get().onColumnSettingRetrieved(mPrefs.get().getInt(COLUMNS_SETTINGS, 4));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPrefs.clear();
    }
}
