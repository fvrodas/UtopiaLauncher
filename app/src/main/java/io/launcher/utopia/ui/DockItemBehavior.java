package io.launcher.utopia.ui;

import java.util.ArrayList;
import io.launcher.utopia.utils.ActivityInfo;

public interface DockItemBehavior extends ApplicationItemBehavior {
    void onAppLongPressed(final ActivityInfo app);
    void onItemRemoved(ArrayList<ActivityInfo> items);
}
