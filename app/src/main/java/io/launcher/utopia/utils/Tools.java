package io.launcher.utopia.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import io.launcher.utopia.adapters.*;

import java.io.ByteArrayOutputStream;

import io.launcher.utopia.R;

/**
 * Created by fernando on 10/22/17.
 */

public class Tools {
    private static Bitmap compress(Bitmap bitmap, int quality){
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
        return Tools.compress(bmp, 70);
    }

    private static int[] getColorsFromBitmap(Bitmap icon) {
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

    private static Drawable createBackground(int[] colors) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        d.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        d.setSize(4, 4);
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(12);
        return d;
    }

    public static Bitmap createIcon(Context ctx, Bitmap item) {
        LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.item_shortcut_renderer, null, false);
        ShortcutViewHolder shortcutViewHolder = new ShortcutViewHolder(view);
        shortcutViewHolder.ivicon.setImageBitmap(item);
        shortcutViewHolder.itemView.setBackground(createBackground(getColorsFromBitmap(item)));

        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_4444);

        Canvas c = new Canvas(bitmap);

        view.draw(c);
        return bitmap;
    }

}
