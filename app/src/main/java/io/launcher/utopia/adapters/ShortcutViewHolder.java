package io.launcher.utopia.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import io.launcher.utopia.R;
import io.launcher.utopia.views.IShortcutView;

/**
 * Created by fernando on 10/15/17.
 */

public class ShortcutViewHolder extends RecyclerView.ViewHolder implements IShortcutView {
    private final ImageView ivicon;
    private Vibrator mVibrator;
    private final int VIBRATION_DURATION = 50;

    public ShortcutViewHolder(View itemView) {
        super(itemView);
        ivicon = itemView.findViewById(R.id.ivIcon);
        mVibrator = (Vibrator) itemView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        itemView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.animate().alpha(0.7f).setDuration(125);
                        return false;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.animate().alpha(1f).setDuration(125);
                        return false;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        v.animate().alpha(1f).setDuration(125);
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
    public void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mVibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            mVibrator.vibrate(VIBRATION_DURATION);
        }
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
