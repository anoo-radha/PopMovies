package com.example.android.popularmovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/* This is the activity that opens when settings option is selected from the menu
*  It displays two options to sort the movies - by most-popular and highest-rated */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);
        Preference preference = findPreference(getString(R.string.pref_sort_key));
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ListPreference lPreference = (ListPreference) preference;
        int prefIndex = lPreference.findIndexOfValue(newValue.toString());
        if (prefIndex >= 0) {
            lPreference.setSummary(lPreference.getEntries()[prefIndex]);
        }
        return true;
    }

}