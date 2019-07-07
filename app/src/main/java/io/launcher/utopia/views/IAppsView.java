package io.launcher.utopia.views;

import java.util.ArrayList;

import io.launcher.utopia.utils.ActivityInfo;

public interface IAppsView extends IView {
    void populateApplicationsList(ArrayList<ActivityInfo> apps);
    void onDockItemsRetrieved(ArrayList<ActivityInfo> dock);
    void onIntReadFromSettings(String key, int value);
}
