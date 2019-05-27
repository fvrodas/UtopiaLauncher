package io.launcher.utopia.views;

import android.view.View;
import android.widget.ImageView;

public interface ShortcutView extends BaseView {
    ImageView getImageView();
    int getAdapterPosition();
    void onPressed(View.OnClickListener listener);
    void onLongPressed(View.OnLongClickListener listener);
    void clear();
}
