package io.launcher.utopia.views;

import android.content.Context;

public interface IView {
    void showMessage(String text);
    Context provideContext();
    void showProgress(Boolean show);
}
