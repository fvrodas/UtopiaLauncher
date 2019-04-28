package io.launcher.utopia.utils;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import io.launcher.utopia.BuildConfig;

public class SerializeHelper<T> {

    public String serialize(Object obj) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(obj);
            oos.flush();
            oos.close();

            String encoded = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            if (BuildConfig.DEBUG) Log.d(this.getClass().getCanonicalName(), "encoded base64: " + encoded);
            return encoded;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public T deserialize (String base64) {
        byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
        if (BuildConfig.DEBUG) Log.d(this.getClass().getCanonicalName(), "base64 to decode: " + base64);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decoded);
        try {
            ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);
            return (T) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }
}