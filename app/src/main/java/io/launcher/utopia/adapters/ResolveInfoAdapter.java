package io.launcher.utopia.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.launcher.utopia.R;
import io.launcher.utopia.presenters.AppItemPresenter;
import io.launcher.utopia.ui.ApplicationItemBehavior;
import io.launcher.utopia.utils.ActivityInfo;

/**
 * Created by fernando on 10/15/17.
 */

public class ResolveInfoAdapter extends RecyclerView.Adapter<AppItemViewHolder> {

    private final AppItemPresenter mPresenter;

    public void updateDataSet(List<ActivityInfo> apps) {
        mPresenter.update(apps);
        notifyDataSetChanged();
    }

    public void saveInstanceState(Bundle state) {
        mPresenter.saveInstanceState(state);
    }

    public void filterDataSet(String searchText) {
        mPresenter.filter(searchText);
        notifyDataSetChanged();
    }

    public ActivityInfo getAppSelected() {
        return mPresenter.getAppSelected();
    }

    public void setAppSelected(ActivityInfo app) {
        mPresenter.setAppSelected(app);
    }

    public ResolveInfoAdapter(ArrayList<ActivityInfo> appsInfo, ApplicationItemBehavior behavior) {
        mPresenter = new AppItemPresenter(appsInfo, behavior);
    }

    @NonNull
    @Override
    public AppItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_square, parent, false);
        return new AppItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AppItemViewHolder holder, int position) {
        mPresenter.bindView(holder);
    }

    @Override
    public void onViewRecycled(@NonNull AppItemViewHolder holder) {
        mPresenter.unbindView(holder);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mPresenter.count();
    }

}
