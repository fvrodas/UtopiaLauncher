package io.launcher.utopia.adapters;

import android.content.SharedPreferences;

interface AdapterPersistence {
    void updateFromPreferences(SharedPreferences prefs);
    void applyToPreferences(SharedPreferences prefs);
}
