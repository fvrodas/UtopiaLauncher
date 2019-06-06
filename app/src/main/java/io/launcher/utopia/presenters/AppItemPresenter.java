package io.launcher.utopia.presenters;

import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.threading.ImageLoaderTask;
import io.launcher.utopia.ui.ApplicationItemBehavior;
import io.launcher.utopia.utils.ActivityInfo;
import io.launcher.utopia.views.AppItemView;

public class AppItemPresenter extends BasePresenter<AppItemView> {
    private final ArrayList<ActivityInfo> mItems;
    private final ArrayList<ActivityInfo> mFiltered = new ArrayList<>();
    private ActivityInfo appSelected = null;
    private ApplicationItemBehavior mListener;

    public ActivityInfo getAppSelected() {
        return appSelected;
    }

    public void setAppSelected(ActivityInfo appSelected) {
        this.appSelected = appSelected;
    }

    public AppItemPresenter(ArrayList<ActivityInfo> mItems, ApplicationItemBehavior behavior) {
        this.mItems = mItems;
        this.mListener = behavior;
    }

    public void bindView(AppItemView view) {
        attachView(view);
        final ActivityInfo current = mFiltered.get(view.getAdapterPosition());
        final String packageName = current.getPackageName();
        final String label = current.getLabel();

        if (UtopiaLauncher.getInstance().iconsCache.get(packageName) != null) {
            view.getImageView().setTag(packageName);
            new ImageLoaderTask(view.getImageView()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        view.setLabel(label);


        view.onPressed(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) mListener.onAppPressed(current);
            }
        });

        view.onLongPressed(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                appSelected = current;
                return false;
            }
        });
    }

    public void unbindView(AppItemView view) {
        view.clear();
        mView = null;
    }

    public void filter(String searchText) {
        mFiltered.clear();
        for(int i =0; i < mItems.size(); i ++) {
            if(mItems.get(i).getLabel().toLowerCase().contains(searchText.toLowerCase())) {
                mFiltered.add(mItems.get(i));
            }
        }
    }

    public void update(List<ActivityInfo> apps) {
        mItems.clear();
        mItems.addAll(apps);
        mFiltered.clear();
        mFiltered.addAll(apps);
    }

    public int count() {
        return  mFiltered.size();
    }
}
