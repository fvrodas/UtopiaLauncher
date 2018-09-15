package io.launcher.utopia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import io.launcher.utopia.R;
import io.launcher.utopia.models.AppInfo;
import io.launcher.utopia.utils.ItemTouchHelperAdapter;

/**
 * Created by fernando on 10/15/17.
 */

public abstract class ApplicationsAdapter extends RecyclerView.Adapter<AppItemViewHolder> implements ItemTouchHelperAdapter {
    private Context mContext;
    private ArrayList<AppInfo> mItems;

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

    protected ApplicationsAdapter(Context c, ArrayList<AppInfo> appInfos) {
        mContext = c;
        mItems = appInfos;
    }

    @NonNull
    @Override
    public AppItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_square, parent, false);
        return new AppItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AppItemViewHolder holder, int position) {
        final AppInfo current = mItems.get(holder.getAdapterPosition());
        holder.ivicon.setImageDrawable(current.icon);
        holder.itemView.setBackground(current.getCachedDrawable());

        holder.tvappname.setText(current.label.toString().toUpperCase());
        holder.tvappname.setShadowLayer(5, 1, 1, Color.BLACK);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAppPressed(mItems.get(holder.getAdapterPosition()));
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

    protected abstract void onAppPressed(AppInfo app);
    protected abstract void onAppLongPressed(AppInfo app);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
