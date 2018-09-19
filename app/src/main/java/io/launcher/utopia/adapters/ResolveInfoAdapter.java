package io.launcher.utopia.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.support.annotation.NonNull;
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

public abstract class ResolveInfoAdapter extends RecyclerView.Adapter<AppItemViewHolder> implements ItemTouchHelperAdapter {
    private Context mContext;
    private ArrayList<ResolveInfo> mItems;
    private PackageManager mPkgManager;

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

    }

    protected ResolveInfoAdapter(Context c, ArrayList<ResolveInfo> appInfos, PackageManager pm) {
        mContext = c;
        mItems = appInfos;
        mPkgManager = pm;
    }

    @NonNull
    @Override
    public AppItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_square, parent, false);
        return new AppItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AppItemViewHolder holder, int position) {
        final ResolveInfo current = mItems.get(holder.getAdapterPosition());
        final String packageName = current.activityInfo.packageName;
        final String label = current.loadLabel(mPkgManager).toString();

        if (UtopiaLauncher.iconsCache.get(packageName) != null) {
            holder.ivicon.setImageBitmap(UtopiaLauncher.iconsCache.get(packageName));
        }

        holder.tvappname.setText(label.toUpperCase());
        holder.tvappname.setShadowLayer(5, 1, 1, Color.BLACK);


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
        holder.tvappname.setText("");
        super.onViewRecycled(holder);
    }

    protected abstract void onAppPressed(ResolveInfo app);
    protected abstract void onAppLongPressed(ResolveInfo app);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
