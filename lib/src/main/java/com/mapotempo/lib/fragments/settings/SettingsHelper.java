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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.UserPreferenceInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;

public class SettingsHelper {

    public enum DataConfiguration {
        WIFI,
        DATA,
        USECACHE
    }

    public static DataConfiguration dataAccess(Context context) {
        UserPreferenceInterface pref = SettingsHelper.getMapotempoPref(context);
        ConnectivityManager connMgr = SettingsHelper.getConnectivityManager(context);

        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        final boolean userAccessData = pref.getBoolPreference(UserPreferenceInterface.Preference.MOBILE_DATA_USAGE);

        if (wifi.isConnected()) {
            return DataConfiguration.WIFI;
        } else if (mobile != null && mobile.isConnected() && userAccessData) {
            return DataConfiguration.DATA;
        } else {
            return DataConfiguration.USECACHE;
        }
    }

    /**
     * Check if any connection are available
     * @param context
     * @return true if wifi enabled or data + authorization are both enabled
     */
    public static boolean connectionAvailableForDownload(Context context) {
        DataConfiguration conf = SettingsHelper.dataAccess(context);
        return (conf == DataConfiguration.WIFI ||
                (conf == DataConfiguration.DATA && allowDataUse(context)));
    }

    public static boolean allowDataUse(Context context) {
        return SettingsHelper.getMapotempoPref(context).getBoolPreference(UserPreferenceInterface.Preference.MOBILE_DATA_USAGE);
    }

    public static boolean allowPreloadUse(Context context) {
        return SettingsHelper.getMapotempoPref(context).getBoolPreference(UserPreferenceInterface.Preference.PRELOAD_DATA);
    }

    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
    }

    public static UserPreferenceInterface getMapotempoPref(Context context) {
        final MapotempoFleetManagerInterface mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) context.getApplicationContext()).getManager();
        return mapotempoFleetManagerInterface.getUserPreference();
    }
}
