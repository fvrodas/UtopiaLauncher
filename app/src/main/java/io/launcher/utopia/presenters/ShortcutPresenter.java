package io.launcher.utopia.presenters;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.threading.ImageLoaderTask;
import io.launcher.utopia.ui.DockItemBehavior;
import io.launcher.utopia.utils.ActivityInfo;
import io.launcher.utopia.utils.SerializeHelper;
import io.launcher.utopia.views.ShortcutView;

public class ShortcutPresenter extends BasePresenter<ShortcutView> {
    private final ArrayList<ActivityInfo> mItems;
    private final SerializeHelper<ArrayList<ActivityInfo>> mHelper = new SerializeHelper<>();
    private DockItemBehavior mListener;
    private SharedPreferences mPrefs;

    public ShortcutPresenter(ArrayList<ActivityInfo> mItems, DockItemBehavior mListener, SharedPreferences prefs) {
        this.mItems = mItems;
        this.mListener = mListener;
        this.mPrefs = prefs;
    }

    @Override
    public void attachView(final ShortcutView view) {
        super.attachView(view);
        final ActivityInfo current = mItems.get(view.getAdapterPosition());
        final String packageName = current.getPackageName();

        if (UtopiaLauncher.getInstance().iconsCache.get(packageName) != null) {
            view.getImageView().setTag(packageName);
            new ImageLoaderTask(view.getImageView()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        view.onPressed(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) mListener.onAppPressed(current);
            }
        });

        view.onLongPressed(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                if (mListener != null) mListener.onAppLongPressed(mItems.get(view.getAdapterPosition()));
                return true;
            }
        });
    }

    private void save(ArrayList<ActivityInfo> apps) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(UtopiaLauncher.DOCK, mHelper.serialize(apps));
        editor.apply();
    }

    public void update(List<ActivityInfo> apps) {
        mItems.clear();
        mItems.addAll(apps);
        save(mItems);
    }

    public boolean contains(ActivityInfo app) {
        for (ActivityInfo i : mItems) {
            if (app.getPackageName().equals(i.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public void remove(int position) {
        mItems.remove(position);
        save(mItems);
        if (mListener != null) mListener.onItemRemoved(mItems);
    }

    public int remove(String app) {
        for (ActivityInfo item: mItems
        ) {
            if (item.getPackageName().equals(app)) {
                int index = mItems.indexOf(item);
                mItems.remove(item);
                return index;
            }
        }
        return -1;
    }

    public void swapItems(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
            }
        }
    }

    public void detach(ShortcutView view) {
        view.clear();
        mView = null;
    }

    public int append(ActivityInfo app) {
        mItems.add(app);
        save(mItems);
        return count() - 1;
    }

    public int count() {
        return mItems.size();
    }
}
