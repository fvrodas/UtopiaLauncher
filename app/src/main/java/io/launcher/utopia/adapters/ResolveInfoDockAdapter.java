package io.launcher.utopia.adapters;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.launcher.utopia.BuildConfig;
import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.threading.ImageLoaderTask;
import io.launcher.utopia.utils.ActivityInfo;
import io.launcher.utopia.utils.ItemTouchHelperAdapter;
import io.launcher.utopia.utils.SerializeHelper;

/**
 * Created by fernando on 10/15/17.
 */

public abstract class ResolveInfoDockAdapter extends RecyclerView.Adapter<ShortcutViewHolder>
        implements ItemTouchHelperAdapter, AdapterPersistence {
    private final ArrayList<ActivityInfo> mItems;
    private final SerializeHelper<ArrayList<ActivityInfo>> helper = new SerializeHelper<>();
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
        onItemSwapped(mItems);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void updateDataSet(List<ActivityInfo> apps) {
        mItems.clear();
        mItems.addAll(apps);
        notifyDataSetChanged();
    }

    @Override
    public void updateFromPreferences(SharedPreferences prefs) {
        String json = prefs.getString(UtopiaLauncher.DOCK, null);
        if (json != null) {
            try {
                ArrayList<ActivityInfo> data = helper.deserialize(json);
                updateDataSet(data);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }
        }
    }

    @Override
    public void applyToPreferences(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UtopiaLauncher.DOCK, helper.serialize(mItems));
        editor.apply();
    }

    public void addItem(ActivityInfo app, SharedPreferences prefs) {
        mItems.add(app);
        notifyItemInserted(mItems.size() - 1);
        applyToPreferences(prefs);
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        onItemRemoved(mItems);
        notifyItemRemoved(position);
    }

    public void removeShortcut(String app) {
        for (ActivityInfo item: mItems
             ) {
            if (item.getPackageName().equals(app)) {
                int index = mItems.indexOf(item);
                mItems.remove(item);
                notifyItemRemoved(index);
            }
        }
    }

    protected ResolveInfoDockAdapter(ArrayList<ActivityInfo> appsInfo) {
        mItems = appsInfo;
    }

    @NonNull
    @Override
    public ShortcutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shortcut, parent, false);
        return new ShortcutViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShortcutViewHolder holder, int position) {
        final ActivityInfo current = mItems.get(holder.getAdapterPosition());
        final String packageName = current.getPackageName();

        if (UtopiaLauncher.iconsCache.get(packageName) != null) {
            holder.ivicon.setTag(packageName);
            new ImageLoaderTask(holder.ivicon).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAppPressed(current);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                onAppLongPressed(mItems.get(holder.getAdapterPosition()));
                return true;
            }
        });

    }

    @Override
    public void onViewRecycled(@NonNull ShortcutViewHolder holder) {
        holder.ivicon.setImageDrawable(null);
        holder.itemView.setOnClickListener(null);
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    protected abstract void onAppPressed(ActivityInfo app);
    @SuppressWarnings("EmptyMethod")
    protected abstract void onAppLongPressed(ActivityInfo app);
    protected abstract void onItemRemoved(ArrayList<ActivityInfo> items);
    protected abstract void onItemSwapped(ArrayList<ActivityInfo> items);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
