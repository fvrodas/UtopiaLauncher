package io.launcher.utopia.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

import io.launcher.utopia.R;
import io.launcher.utopia.adapters.ShortcutViewHolder;

/**
 * Created by fernando on 10/22/17.
 */

public class Tools {
    private static Bitmap compress(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream= new  ByteArrayOutputStream();
        final int quality = 70;
        if (Build.VERSION.SDK_INT >= 21) {
            bitmap.compress(Bitmap.CompressFormat.WEBP, quality, byteArrayOutputStream);
        } else {
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, byteArrayOutputStream);
        }
        byte [] b=byteArrayOutputStream.toByteArray();
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    @NonNull
    public static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable, Bitmap.Config config) {
        int width = Math.round(drawable.getIntrinsicWidth() * 0.9f);
        int height = Math.round(drawable.getIntrinsicHeight() * 0.9f);
        final Bitmap bmp = Bitmap.createBitmap(width, height, config);
        bmp.setHasAlpha(true);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return Tools.compress(bmp);
    }

    public static int[] getColorsFromBitmap(Bitmap icon) {
        Palette p = Palette.from(icon).generate();
        int color;
        if (p.getDominantSwatch() != null) {
            color = p.getDominantSwatch().getRgb();
        } else if (p.getVibrantSwatch() != null) {
            color = p.getVibrantSwatch().getRgb();
        } else {
            color = Color.DKGRAY;
        }

        return generateColors(color, 2);
    }

    private static int[] generateColors(int color, int iteration) {
        int[] colors = new int[iteration];
        float[] hsl = new float[3];
        ColorUtils.colorToHSL(color, hsl);
        for (int i = 0; i < iteration; i ++) {
            if (hsl[1] > .25f) {
                float hue = hsl[0] - 25 * i;
                if (hue > 360) {
                    hue -= 360;
                } else if (hue < 0) {
                    hue = 360 + hue;
                }
                hsl[0] = hue;
                hsl[1] = .65f + (i / iteration);
            }
            hsl[2] = .65f - (i/iteration);
            colors[i] = ColorUtils.HSLToColor(hsl);
        }
        return colors;
    }

    public static Drawable createBackgroundShape(int[] colors, int shape) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        d.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        d.setSize(8, 8);
        d.setShape(shape);
        if (shape == GradientDrawable.RECTANGLE) {
            d.setCornerRadius(16f);
        }
        return d;
    }

    public static Bitmap createIcon(Context ctx, Bitmap item) {
        LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.item_shortcut_renderer, null, false);
        ShortcutViewHolder shortcutViewHolder = new ShortcutViewHolder(view);
        shortcutViewHolder.getImageView().setImageBitmap(item);
        shortcutViewHolder.itemView.setBackground(createBackgroundShape(getColorsFromBitmap(item), GradientDrawable.OVAL));

        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);

        view.draw(c);
        return bitmap;
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static void showSnackbar(AppCompatActivity ctx, String text) {
        Snackbar.make(ctx.getWindow().getDecorView().findViewById(android.R.id.content), text, 2000).show();
    }
}
