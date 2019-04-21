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
    ImageView icon;
    TextView appName;
    private MenuInflater menuInflater = null;

    AppItemViewHolder(View itemView) {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.ivIcon);
        appName = (TextView) itemView.findViewById(R.id.tvAppName);
        itemView.setOnCreateContextMenuListener(this);
        menuInflater = new MenuInflater(itemView.getContext());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(appName.getText());
        menuInflater.inflate(R.menu.menu_apps, menu);
    }
}
