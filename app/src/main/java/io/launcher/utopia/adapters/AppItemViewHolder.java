package io.launcher.utopia.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.launcher.utopia.R;

/**
 * Created by fernando on 10/15/17.
 */

class AppItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    public ImageView ivicon;
    public TextView tvappname;
    private MenuInflater menuInflater = null;

    public AppItemViewHolder(View itemView) {
        super(itemView);
        ivicon = (ImageView) itemView.findViewById(R.id.ivIcon);
        tvappname = (TextView) itemView.findViewById(R.id.tvAppName);
        itemView.setOnCreateContextMenuListener(this);
        menuInflater = new MenuInflater(itemView.getContext());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(tvappname.getText());
        menuInflater.inflate(R.menu.menu_apps, menu);
    }
}
