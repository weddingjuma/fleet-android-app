package com.mapotempo.lib.settings;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.UserPreferenceInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initMobileDataPrefsCallbacks();
        initNightModePrefsCallbacks();
        initAutomaticDataUpdatePrefsCallbacks();
        initMapZoomButtonPrefsCallbacks();
        initMapCurrentPositionPrefsCallbacks();
    }

    private void initMobileDataPrefsCallbacks() {
        Preference pref = findPreference(getString(R.string.pref_mobile_data));
        if (pref == null)
            return;

        ((SwitchPreferenceCompat) pref).setChecked(getUserPreferenceInterface().getBoolPreference(UserPreferenceInterface.Preference.MOBILE_DATA_USAGE));
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                UserPreferenceInterface userPreferenceInterface = getUserPreferenceInterface();
                userPreferenceInterface.setBoolPreference(UserPreferenceInterface.Preference.MOBILE_DATA_USAGE, (Boolean) newValue);
                userPreferenceInterface.save();

                // Enable manager if network data actif
                final ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mobile != null && mobile.isConnected() && (Boolean) newValue == true) {
                    final MapotempoFleetManagerInterface mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
                    mapotempoFleetManagerInterface.onlineStatus(true);
                } else if (wifi != null && !wifi.isConnected() && (Boolean) newValue == false) {
                    final MapotempoFleetManagerInterface mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
                    mapotempoFleetManagerInterface.onlineStatus(false);
                }
                return true;
            }
        });
    }

    private void initNightModePrefsCallbacks() {
        final ListPreference pref = (ListPreference) findPreference(getString(R.string.pref_night_mode));
        if (pref == null)
            return;
        String curPref = String.valueOf(getUserPreferenceInterface().getNightModePreference().ordinal());
        pref.setValue(curPref);
        pref.setSummary(pref.getEntry());
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String textValue = newValue.toString();

                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(textValue);
                CharSequence[] entries = listPreference.getEntries();

                if (index >= 0 && index < entries.length) {
                    UserPreferenceInterface userPreferenceInterface = getUserPreferenceInterface();
                    userPreferenceInterface.setNightModePreference(UserPreferenceInterface.NightModePreference.fromString(entries[index].toString()));
                    userPreferenceInterface.save();
                }

                // TODO implement ThemeSwithcher here !

                setPreferenceSummaryOnUiThread(preference, entries[index].toString());
                return true;
            }
        });
    }

    private void initAutomaticDataUpdatePrefsCallbacks() {
        Preference pref = findPreference(getString(R.string.pref_automatic_data_update));
        if (pref == null)
            return;

        ((SwitchPreferenceCompat) pref).setChecked(getUserPreferenceInterface().getBoolPreference(UserPreferenceInterface.Preference.AUTOMATIC_DATA_UPDATE));
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                UserPreferenceInterface userPreferenceInterface = getUserPreferenceInterface();
                userPreferenceInterface.setBoolPreference(UserPreferenceInterface.Preference.AUTOMATIC_DATA_UPDATE, (Boolean) newValue);
                userPreferenceInterface.save();
                return true;
            }
        });
    }

    private void initMapZoomButtonPrefsCallbacks() {
        Preference pref = findPreference(getString(R.string.pref_map_zoom_button));
        if (pref == null)
            return;

        ((SwitchPreferenceCompat) pref).setChecked(getUserPreferenceInterface().getBoolPreference(UserPreferenceInterface.Preference.MAP_DISPLAY_ZOOM_BUTTON));
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                UserPreferenceInterface userPreferenceInterface = getUserPreferenceInterface();
                userPreferenceInterface.setBoolPreference(UserPreferenceInterface.Preference.MAP_DISPLAY_ZOOM_BUTTON, (Boolean) newValue);
                userPreferenceInterface.save();
                return true;
            }
        });
    }

    private void initMapCurrentPositionPrefsCallbacks() {
        Preference pref = findPreference(getString(R.string.pref_map_current_position));
        if (pref == null)
            return;

        ((SwitchPreferenceCompat) pref).setChecked(getUserPreferenceInterface().getBoolPreference(UserPreferenceInterface.Preference.MAP_CURRENT_POSITION));
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                UserPreferenceInterface userPreferenceInterface = getUserPreferenceInterface();
                userPreferenceInterface.setBoolPreference(UserPreferenceInterface.Preference.MAP_CURRENT_POSITION, (Boolean) newValue);
                userPreferenceInterface.save();
                return true;
            }
        });
    }

    private UserPreferenceInterface getUserPreferenceInterface() {
        final MapotempoFleetManagerInterface mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
        return mapotempoFleetManagerInterface.getUserPreference();
    }

    private void setPreferenceSummaryOnUiThread(final Preference preference, final String value) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                preference.setSummary(value);

            }
        });
    }
}
