package io.launcher.utopia.views;

public interface ISettingsView extends IView {
    void onIntReadFromSettings(String key, int value);
}
