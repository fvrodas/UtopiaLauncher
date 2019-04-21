package io.launcher.utopia.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.launcher.utopia.BuildConfig;
import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.utils.ItemTouchHelperAdapter;

/**
 * Created by fernando on 10/15/17.
 */

public abstract class ResolveInfoDockAdapter extends RecyclerView.Adapter<ShortcutViewHolder>
        implements ItemTouchHelperAdapter, AdapterPersistence {
    private Context mContext;
    private ArrayList<ResolveInfo> mItems;
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
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
        return true;
    }

    private void updateDataSet(List<ResolveInfo> apps) {
        mItems.clear();
        mItems.addAll(apps);
        notifyDataSetChanged();
    }

    @Override
    public void updateFromPreferences(SharedPreferences prefs) {
        String json = prefs.getString(UtopiaLauncher.DOCK, null);
        if (json != null) {
            try {

                ResolveInfo[] data = new Gson().fromJson(json, ResolveInfo[].class);
                updateDataSet(Arrays.asList(data));
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }
        }
    }

    @Override
    public void applyToPreferences(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UtopiaLauncher.DOCK, new Gson().toJson(mItems));
        editor.apply();
    }

    public void addItem(ResolveInfo app, SharedPreferences prefs) {
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

    protected ResolveInfoDockAdapter(Context c, ArrayList<ResolveInfo> appInfos) {
        mContext = c;
        mItems = appInfos;
    }

    @NonNull
    @Override
    public ShortcutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_shortcut, parent, false);
        return new ShortcutViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShortcutViewHolder holder, int position) {
        final ResolveInfo current = mItems.get(holder.getAdapterPosition());
        final String packageName = current.activityInfo.packageName;

        if (UtopiaLauncher.iconsCache.get(packageName) != null) {
            holder.ivicon.setImageBitmap(UtopiaLauncher.iconsCache.get(packageName));
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

    protected abstract void onAppPressed(ResolveInfo app);
    protected abstract void onAppLongPressed(ResolveInfo app);
    protected abstract void onItemRemoved(ArrayList<ResolveInfo> items);
    protected abstract void onItemSwapped(ArrayList<ResolveInfo> items);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
