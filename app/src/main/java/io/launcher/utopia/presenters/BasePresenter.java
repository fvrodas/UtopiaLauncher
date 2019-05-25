package io.launcher.utopia.presenters;

import java.lang.ref.WeakReference;

import io.launcher.utopia.views.BaseView;

public class BasePresenter<T extends BaseView> {
    protected WeakReference<T> mView = null;


    public void attachView(T view) {
        mView = new WeakReference<>(view);
    }

    public void onDestroy() {
        mView.clear();
    }
}
