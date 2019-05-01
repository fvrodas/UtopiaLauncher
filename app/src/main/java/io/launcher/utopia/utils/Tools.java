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
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

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

        float[] hsl = new float[3];
        ColorUtils.colorToHSL(color, hsl);
        hsl[0] = hsl[0] * 0.85f;
        hsl[1] = hsl[1] * 0.6f;
        hsl[2] = .8f;
        colors[0] = ColorUtils.HSLToColor(hsl);

        ColorUtils.colorToHSL(color, hsl);
        hsl[1] = hsl[1] * 0.7f;
        hsl[2] = .5f;
        colors[1] = ColorUtils.HSLToColor(hsl);

        ColorUtils.colorToHSL(color, hsl);
        hsl[0] = hsl[0] * 1.1f;
        hsl[1] = hsl[1] * 0.8f;
        hsl[2] = .3f;
        colors[2] = ColorUtils.HSLToColor(hsl);

        return colors;
    }

    private static Drawable createBackground(int[] colors) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
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
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);

        view.draw(c);
        return bitmap;
    }

}
