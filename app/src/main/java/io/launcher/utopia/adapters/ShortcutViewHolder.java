package io.launcher.utopia.adapters;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import io.launcher.utopia.R;

/**
 * Created by fernando on 10/15/17.
 */

public class ShortcutViewHolder extends RecyclerView.ViewHolder {
    public final ImageView ivicon;

    public ShortcutViewHolder(View itemView) {
        super(itemView);
        ivicon = itemView.findViewById(R.id.ivIcon);
    }

}
