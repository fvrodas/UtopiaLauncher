package io.launcher.utopia.ui.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import io.launcher.utopia.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

}
