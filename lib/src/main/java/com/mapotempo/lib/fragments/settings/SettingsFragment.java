/*
 * Copyright © Mapotempo, 2018
 *
 * This file is part of Mapotempo.
 *
 * Mapotempo is free software. You can redistribute it and/or
 * modify since you respect the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Mapotempo is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the Licenses for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Mapotempo. If not, see:
 * <http://www.gnu.org/licenses/agpl.html>
 */

package com.mapotempo.lib.fragments.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.UserPreferenceInterface;
import com.mapotempo.fleet.utils.DateHelper;
import com.mapotempo.lib.MapotempoApplication;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseSettingsFragment;
import com.mapotempo.lib.fragments.map.OfflineMapManager;
import com.mapotempo.lib.utils.DateHelpers;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class SettingsFragment extends MapotempoBaseSettingsFragment {

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
        initMapPreloadPrefsCallbacks();
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

    private void initMapPreloadPrefsCallbacks() {
        Preference pref = findPreference(getString(R.string.pref_map_preload_tiles));
        if (pref == null) return;

        boolean canPreload = getUserPreferenceInterface().getBoolPreference(UserPreferenceInterface.Preference.PRELOAD_DATA);

        ((SwitchPreferenceCompat) pref).setChecked(canPreload);
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                UserPreferenceInterface pref = getUserPreferenceInterface();
                pref.setBoolPreference(UserPreferenceInterface.Preference.PRELOAD_DATA, (boolean) newValue);
                setOfflinePrefsVisibility((boolean) newValue);

                boolean saved = pref.save();
                MapotempoApplicationInterface IMapApp = (MapotempoApplicationInterface) getActivity().getApplicationContext();

                if (saved && !(boolean) newValue) {
                    IMapApp.getOfflineManager().deleteOfflineCacheRegions();
                }

                return saved;
            }
        });

        setOfflinePrefsVisibility(canPreload);
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

    private void setOfflinePrefsVisibility(boolean visibility) {
        Preference dlAction = findPreference(getString(R.string.pref_map_preload_dl_action));
        Preference deleteAction = findPreference(getString(R.string.pref_map_preload_delete_action));
        Preference info = findPreference(getString(R.string.pref_map_preload_infos));
        Preference consumption = findPreference(getString(R.string.pref_map_preload_consumption));

        dlAction.setVisible(visibility);
        deleteAction.setVisible(visibility);
        info.setVisible(visibility);
        consumption.setVisible(visibility);

        if (visibility) {
            dlAction.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ((MapotempoApplication) getContext().getApplicationContext())
                            .getOfflineManager()
                            .alertBuilderForMapDownloading(getContext(), onOk, null);
                    return true;
                }
            });

            deleteAction.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ((MapotempoApplication) getContext().getApplicationContext())
                                                        .getOfflineManager()
                                                        .deleteOfflineCacheRegions();
                    setConsumptionInfo();
                    setDateInfo();
                    return true;
                }
            });

            setConsumptionInfo();
            setDateInfo();
        } else {
            dlAction.setOnPreferenceClickListener(null);
            deleteAction.setOnPreferenceClickListener(null);
        }
    }

    private void setConsumptionInfo() {
        Preference consumption = findPreference(getString(R.string.pref_map_preload_consumption));
        if (consumption == null) return;

        OfflineMapManager manager = ((MapotempoApplication) getContext().getApplicationContext()).getOfflineManager();

        long size = manager.getOfflineDbSize();
        String summary = getString(R.string.preload_map_consumption_prefix) + " " + String.valueOf(size) + " Mo";

        consumption.setSummary(summary);
    }

    private void setDateInfo() {
        Preference info = findPreference(getString(R.string.pref_map_preload_infos));
        if (info == null) return;

        OfflineMapManager manager = ((MapotempoApplication) getContext().getApplicationContext()).getOfflineManager();

        String lastCachedDate = DateHelpers.parse(manager.getLastCachedDate(), DateHelpers.DateStyle.FULLDATE);

        if (lastCachedDate == null) {
            lastCachedDate = getString(R.string.preload_consumption_default_desc);
        }

        info.setSummary(lastCachedDate);
    }


    // Listeners -------------------------------------------------------------------

    DialogInterface.OnClickListener onOk = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            MapotempoFleetManagerInterface fleetManager = ((MapotempoApplication) getContext().getApplicationContext()).getManager();
            LatLngBounds.Builder bounds = new LatLngBounds.Builder();

            // Construct bounds of the current missions set
            for(MissionInterface mission : fleetManager.getMissionAccess().getAll()) {
                bounds.include(new LatLng(mission.getLocation().getLat(), mission.getLocation().getLon()));
            }

            // Initialize Caching using the Mapbox Service
            ((MapotempoApplication) getContext().getApplicationContext())
               .getOfflineManager()
               .initCachForZone(bounds.build(), new OfflineMapManager.IMapLoading() {
                   @Override
                   public void OnDownloadingCompleted() {
                      setConsumptionInfo();
                      setDateInfo();
                   }

                   @Override
                   public void OnDownloadingFailed(String message) { /*...*/ }
               });
        }
    };
}
