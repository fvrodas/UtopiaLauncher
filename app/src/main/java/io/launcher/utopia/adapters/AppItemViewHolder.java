package io.launcher.utopia.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import io.launcher.utopia.R;
import io.launcher.utopia.views.IAppItemView;

/**
 * Created by fernando on 10/15/17.
 */

class AppItemViewHolder extends RecyclerView.ViewHolder implements IAppItemView, View.OnCreateContextMenuListener {
    private final ImageView icon;
    private final TextView appName;
    private final MenuInflater menuInflater;

    AppItemViewHolder(View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.ivIcon);
        appName = itemView.findViewById(R.id.tvAppName);
        itemView.setOnCreateContextMenuListener(this);
        menuInflater = new MenuInflater(itemView.getContext());

        itemView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.animate().alpha(0.8f).scaleX(1.25f).scaleY(1.25f).setDuration(125);
                        return false;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(125);
                        return false;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        v.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(125);
                        return false;
                    }
                    default: {
                        return false;
                    }
                }
            }
        });
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
