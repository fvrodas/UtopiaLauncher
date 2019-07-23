package io.launcher.utopia.threading;

import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import io.launcher.utopia.UtopiaLauncher;

public class ImageLoaderTask extends AsyncTask<Object, Void, Bitmap> {
    private final WeakReference<ImageView> mImageView;
    private final WeakReference<Integer> mShape;

    public ImageLoaderTask(ImageView imageView) {
        mImageView = new WeakReference<>(imageView);
        mShape = new WeakReference<>(GradientDrawable.OVAL);
    }
    public ImageLoaderTask(ImageView imageView, int shape) {
        mImageView = new WeakReference<>(imageView);
        mShape = new WeakReference<>(shape);
    }

    @Override
    protected Bitmap doInBackground(Object... objects) {
        return UtopiaLauncher.getInstance().iconsCache.get((String) mImageView.get().getTag());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mImageView.get().setImageBitmap(bitmap);
        mImageView.clear();
    }
}
