package io.launcher.utopia.threading;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import io.launcher.utopia.UtopiaLauncher;

public class ImageLoaderTask extends AsyncTask<Object, Void, Bitmap> {
    private WeakReference<ImageView> mImageView;

    public ImageLoaderTask(ImageView imageView) {
        mImageView = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... objects) {
        return UtopiaLauncher.iconsCache.get((String) mImageView.get().getTag());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mImageView.get().setImageBitmap(bitmap);
        mImageView.clear();
    }
}
