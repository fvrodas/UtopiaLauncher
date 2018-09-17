package io.launcher.utopia.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import io.launcher.utopia.R;
import io.launcher.utopia.UtopiaLauncher;
import io.launcher.utopia.utils.ItemTouchHelperAdapter;

/**
 * Created by fernando on 10/15/17.
 */

public abstract class ResolveInfoDockAdapter extends RecyclerView.Adapter<AppItemViewHolder> implements ItemTouchHelperAdapter {
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
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    protected ResolveInfoDockAdapter(Context c, ArrayList<ResolveInfo> appInfos) {
        mContext = c;
        mItems = appInfos;
    }

    @NonNull
    @Override
    public AppItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_shortcut, parent, false);
        return new AppItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AppItemViewHolder holder, int position) {
        final ResolveInfo current = mItems.get(holder.getAdapterPosition());
        final String packageName = current.activityInfo.packageName;

        if (UtopiaLauncher.iconsCache.get(packageName) != null) {
            holder.ivicon.setImageBitmap(UtopiaLauncher.iconsCache.get(packageName));
        }

        if (UtopiaLauncher.bgCache.get(packageName) != null ){
            holder.itemView.setBackground(UtopiaLauncher.bgCache.get(packageName));
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
    public void onViewRecycled(@NonNull AppItemViewHolder holder) {
        holder.ivicon.setImageDrawable(null);
        super.onViewRecycled(holder);
    }

    protected abstract void onAppPressed(ResolveInfo app);
    protected abstract void onAppLongPressed(ResolveInfo app);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
