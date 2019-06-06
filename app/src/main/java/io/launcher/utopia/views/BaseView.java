package io.launcher.utopia.views;

import android.content.Context;

public interface BaseView {
    void showMessage(String text);
    Context provideContext();
    void showProgress(Boolean show);
}
