package io.launcher.utopia.ui;

import java.util.ArrayList;
import io.launcher.utopia.utils.ActivityInfo;

public interface IDockItem extends IApplicationItem {
    void onAppLongPressed(final ActivityInfo app);
    void onItemRemoved(ArrayList<ActivityInfo> items);
}
