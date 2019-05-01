package io.launcher.utopia.adapters;

import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import io.launcher.utopia.R;

/**
 * Created by fernando on 10/15/17.
 */

class AppItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    final ImageView icon;
    final TextView appName;
    private final MenuInflater menuInflater;

    AppItemViewHolder(View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.ivIcon);
        appName = itemView.findViewById(R.id.tvAppName);
        itemView.setOnCreateContextMenuListener(this);
        menuInflater = new MenuInflater(itemView.getContext());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(appName.getText());
        menuInflater.inflate(R.menu.menu_apps, menu);
    }
}
