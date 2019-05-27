package io.launcher.utopia.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import io.launcher.utopia.R;
import io.launcher.utopia.views.ShortcutView;

/**
 * Created by fernando on 10/15/17.
 */

public class ShortcutViewHolder extends RecyclerView.ViewHolder implements ShortcutView {
    private final ImageView ivicon;

    public ShortcutViewHolder(View itemView) {
        super(itemView);
        ivicon = itemView.findViewById(R.id.ivIcon);
    }

    @Override
    public ImageView getImageView() {
        return ivicon;
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
        ivicon.setImageDrawable(null);
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
