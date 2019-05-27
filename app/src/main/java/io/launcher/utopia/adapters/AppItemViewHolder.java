package io.launcher.utopia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import io.launcher.utopia.R;
import io.launcher.utopia.ui.ApplicationItemBehavior;
import io.launcher.utopia.views.AppItemView;

/**
 * Created by fernando on 10/15/17.
 */

class AppItemViewHolder extends RecyclerView.ViewHolder implements AppItemView, View.OnCreateContextMenuListener {
    private final ImageView icon;
    private final TextView appName;
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

    @Override
    public ImageView getImageView() {
        return icon;
    }

    @Override
    public void setLabel(String text) {
        appName.setText(text);
        appName.setShadowLayer(5, 1, 1, Color.BLACK);
    }

    @Override
    public void onPressed(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }

    @Override
    public void onLongPressed(View.OnLongClickListener listener) {
        itemView.setOnLongClickListener(listener);
    }

    @Override
    public void clear() {
        icon.setImageDrawable(null);
        appName.setText("");
        itemView.setOnClickListener(null);
        itemView.setOnLongClickListener(null);
    }

    @Override
    public void showMessage(String text) {

    }

    @Override
    public Context provideContext() {
        return itemView.getContext();
    }

    @Override
    public void showProgress(Boolean show) {

    }
}
