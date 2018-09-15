package io.launcher.utopia.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by fernando on 10/22/17.
 */

public class Tools {
    public static Bitmap compress(Bitmap bitmap, int quality){
        ByteArrayOutputStream baos= new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, quality, baos);
        byte [] b=baos.toByteArray();
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    @NonNull
    public static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable, Bitmap.Config config) {
        int width = Math.round(drawable.getIntrinsicWidth() * 0.9f);
        int height = Math.round(drawable.getIntrinsicHeight() * 0.9f);
        final Bitmap bmp = Bitmap.createBitmap(width, height, config);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return Tools.compress(bmp, 65);
    }

    public static int[] getColorsFromBitmap(Bitmap icon) {
        int[] colors = new int[3];
        Palette p = Palette.from(icon).generate();
        int color;
        if (p.getVibrantSwatch() != null) {
            color = p.getVibrantSwatch().getRgb();
        } else if (p.getLightVibrantSwatch() != null) {
            color = p.getLightVibrantSwatch().getRgb();
        } else {
            color = Color.LTGRAY;
        }

        float[] hsl1 = new float[3];
        ColorUtils.colorToHSL(color, hsl1);
        hsl1[0] = hsl1[0] * 0.85f;
        hsl1[1] = hsl1[1] * 0.6f;
        hsl1[2] = .8f;
        colors[0] = ColorUtils.HSLToColor(hsl1);

        float[] hsl = new float[3];
        ColorUtils.colorToHSL(color, hsl);
        hsl[0] = hsl[0];
        hsl[1] = hsl[1] * 0.7f;
        hsl[2] = .5f;
        colors[1] = ColorUtils.HSLToColor(hsl);

        float[] hsl2 = new float[3];
        ColorUtils.colorToHSL(color, hsl2);
        hsl2[0] = hsl2[0] * 1.1f;
        hsl2[1] = hsl2[1] * 0.8f;
        hsl2[2] = .3f;
        colors[2] = ColorUtils.HSLToColor(hsl2);

        return colors;
    }

    public static Drawable createBackground(int[] colors) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        d.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        d.setSize(4, 4);
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(8);
        return d;
    }

}
