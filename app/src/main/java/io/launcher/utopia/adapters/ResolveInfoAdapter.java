package io.launcher.utopia.adapters;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.threading.ImageLoaderTask;
import io.launcher.utopia.ui.ApplicationItemBehavior;
import io.launcher.utopia.utils.ActivityInfo;
import io.launcher.utopia.utils.ItemTouchHelperAdapter;

/**
 * Created by fernando on 10/15/17.
 */

public class ResolveInfoAdapter extends RecyclerView.Adapter<AppItemViewHolder>
        implements ItemTouchHelperAdapter {
    private final ArrayList<ActivityInfo> mItems;
    private final ArrayList<ActivityInfo> mFiltered = new ArrayList<>();

    private ActivityInfo appSelected = null;
    private ApplicationItemBehavior mListener = null;

    public ActivityInfo getAppSelected() {
        return appSelected;
    }

    public void setAppSelected(ActivityInfo appSelected) {
        this.appSelected = appSelected;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateDataSet(List<ActivityInfo> apps) {
        mItems.clear();
        mItems.addAll(apps);
        mFiltered.clear();
        mFiltered.addAll(apps);
        notifyDataSetChanged();
    }

    public void filterDataSet(String searchText) {
        mFiltered.clear();
        for(int i =0; i < mItems.size(); i ++) {
            if(mItems.get(i).getLabel().toLowerCase().contains(searchText.toLowerCase())) {
                mFiltered.add(mItems.get(i));
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public void onItemDismiss(int position) {

    }

    public ResolveInfoAdapter(ArrayList<ActivityInfo> appsInfo, ApplicationItemBehavior behavior) {
        mItems = appsInfo;
        mListener = behavior;
    }

    @NonNull
    @Override
    public AppItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_square, parent, false);
        return new AppItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AppItemViewHolder holder, int position) {
        final ActivityInfo current = mFiltered.get(holder.getAdapterPosition());
        final String packageName = current.getPackageName();
        final String label = current.getLabel();

        if (UtopiaLauncher.getInstance().iconsCache.get(packageName) != null) {
            holder.icon.setTag(packageName);
            new ImageLoaderTask(holder.icon).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        holder.appName.setText(label);
        holder.appName.setShadowLayer(5, 1, 1, Color.BLACK);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) mListener.onAppPressed(current);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                setAppSelected(current);
                return false;
            }
        });

    }

    @Override
    public void onViewRecycled(@NonNull AppItemViewHolder holder) {
        holder.icon.setImageDrawable(null);
        holder.appName.setText("");
        holder.itemView.setOnClickListener(null);
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mFiltered.size();
    }

}
