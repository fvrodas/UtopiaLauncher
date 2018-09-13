package io.launcher.utopia.utils;

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
    }

    public static Bitmap compress(Bitmap bitmap, int quality){
        ByteArrayOutputStream baos= new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, quality, baos);
        byte [] b=baos.toByteArray();
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }
}
