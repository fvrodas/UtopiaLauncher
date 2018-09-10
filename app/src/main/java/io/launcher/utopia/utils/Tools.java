package io.launcher.utopia.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by fernando on 10/22/17.
 */

public class Tools {

    public static class ColorTools {

        public static int getContrastColor(int colorIntValue) {
            int red = Color.red(colorIntValue);
            int green = Color.green(colorIntValue);
            int blue = Color.blue(colorIntValue);
            double lum = (((0.299 * red) + ((0.587 * green) + (0.114 * blue))));
            return lum > 186 ? 0xFF000000 : 0xFFFFFFFF;
        }

        public static int invertColor(int color) {
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            int alpha = Color.alpha(color);
            return Color.argb(alpha, 255-red, 255-green, 255-blue);
        }
    }

    public static int dp2px(Context context, int dp){
        return (int) ((dp * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos= new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap compress(Bitmap bitmap, int quality){
        ByteArrayOutputStream baos= new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, quality, baos);
        byte [] b=baos.toByteArray();
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
