package io.launcher.utopia.views;

import android.view.View;
import android.widget.ImageView;

public interface IAppItemView extends IView {
    ImageView getImageView();
    void setLabel(String text);
    int getAdapterPosition();
    void onPressed(View.OnClickListener listener);
    void onLongPressed(View.OnLongClickListener listener);
    void clear();
}
