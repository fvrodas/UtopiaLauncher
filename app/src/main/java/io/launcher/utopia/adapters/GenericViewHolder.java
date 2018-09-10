package io.launcher.utopia.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by fernando on 10/15/17.
 */

class GenericViewHolder extends RecyclerView.ViewHolder {

    GenericViewHolder(View itemView) {
        super(itemView);
    }

    <T> T getView(int resId, Class<T> cls) {
        if (cls.isInstance(itemView.findViewById(resId))) {
            return cls.cast(itemView.findViewById(resId));
        } else return null;
    }
}
