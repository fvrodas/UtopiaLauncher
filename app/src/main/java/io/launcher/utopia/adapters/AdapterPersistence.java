package io.launcher.utopia.adapters;

import android.content.SharedPreferences;

public interface AdapterPersistence {
    void updateFromPreferences(SharedPreferences prefs);
    void applyToPreferences(SharedPreferences prefs);
}
