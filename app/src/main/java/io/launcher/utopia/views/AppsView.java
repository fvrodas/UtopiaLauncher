package io.launcher.utopia.views;

import java.util.ArrayList;

import io.launcher.utopia.utils.ActivityInfo;

public interface AppsView extends BaseView {
    void populateApplicationsList(ArrayList<ActivityInfo> apps);
}