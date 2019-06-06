package io.launcher.utopia.views;

public interface SettingsView extends BaseView {
    void onIntReadFromSettings(String key, int value);
}
