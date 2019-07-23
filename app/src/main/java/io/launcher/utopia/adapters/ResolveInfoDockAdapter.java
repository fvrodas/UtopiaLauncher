package io.launcher.utopia.adapters;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.launcher.utopia.R;
import io.launcher.utopia.presenters.ShortcutPresenter;
import io.launcher.utopia.ui.IDockItem;
import io.launcher.utopia.utils.ActivityInfo;
import io.launcher.utopia.utils.ItemTouchHelperAdapter;
/**
 * Created by fernando on 10/15/17.
 */

public class ResolveInfoDockAdapter extends RecyclerView.Adapter<ShortcutViewHolder>
        implements ItemTouchHelperAdapter {

    private final ShortcutPresenter mPresenter;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mPresenter.swapItems(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateDataSet(List<ActivityInfo> apps) {
        mPresenter.update(apps);
        notifyDataSetChanged();
    }

    public boolean exists(ActivityInfo app) {
        return mPresenter.contains(app);
    }

    public void addItem(ActivityInfo app) {
        if (getItemCount() < 5) {
            int added = mPresenter.append(app);
            notifyItemInserted(added);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        mPresenter.remove(position);
        notifyItemRemoved(position);
    }

    public void removeShortcut(String app) {
        int i = mPresenter.remove(app);
        if (i >= 0)notifyItemRemoved(i);
    }

    public ResolveInfoDockAdapter(ArrayList<ActivityInfo> appsInfo, IDockItem behavior, SharedPreferences prefs) {
        mPresenter = new ShortcutPresenter(appsInfo, behavior, prefs);
    }

    @NonNull
    @Override
    public ShortcutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shortcut, parent, false);
        return new ShortcutViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShortcutViewHolder holder, int position) {
        mPresenter.attachView(holder);
    }

    @Override
    public void onViewRecycled(@NonNull ShortcutViewHolder holder) {
        mPresenter.detach(holder);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mPresenter.count();
    }

}
