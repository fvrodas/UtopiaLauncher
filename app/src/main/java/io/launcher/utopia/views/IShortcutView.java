package io.launcher.utopia.views;

import android.view.View;
import android.widget.ImageView;

public interface IShortcutView extends IView {
    ImageView getImageView();
    int getAdapterPosition();
    void vibrate();
    void onPressed(View.OnClickListener listener);
    void onLongPressed(View.OnLongClickListener listener);
    void clear();
}
