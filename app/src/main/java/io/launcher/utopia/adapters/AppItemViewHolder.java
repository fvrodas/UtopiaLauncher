package io.launcher.utopia.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.launcher.utopia.R;

/**
 * Created by fernando on 10/15/17.
 */

class AppItemViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivicon;
    public TextView tvappname;

    public AppItemViewHolder(View itemView) {
        super(itemView);
        ivicon = itemView.findViewById(R.id.ivIcon);
        tvappname = itemView.findViewById(R.id.tvAppName);
    }


}
