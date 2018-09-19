package io.launcher.utopia.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import io.launcher.utopia.R;

/**
 * Created by fernando on 10/15/17.
 */

public class ShortcutViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivicon;

    public ShortcutViewHolder(View itemView) {
        super(itemView);
        ivicon = (ImageView) itemView.findViewById(R.id.ivIcon);
    }


}
