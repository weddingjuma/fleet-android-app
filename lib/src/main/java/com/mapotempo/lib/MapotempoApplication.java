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

package com.mapotempo.lib;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

public class MapotempoApplication extends Application implements MapotempoApplicationInterface {

    private MapotempoFleetManagerInterface iFleetManager;

    // ======================================
    // ==  Android Application Life cycle  ==
    // ======================================

    @Override
    public void onCreate() {
        super.onCreate();
        // Init Mapbox
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token_dummy));

        // Init Sentry
        initSentry();

        // Init Location
        initLocation();
    }

    // ==============
    // ==  Public  ==
    // ==============

    @Override
    public MapotempoFleetManagerInterface getManager() {
        return iFleetManager;
    }

    @Override
    public void setManager(MapotempoFleetManagerInterface manager) {
        if (iFleetManager != null)
            iFleetManager.release();
        iFleetManager = manager;

        if (iFleetManager != null && !iFleetManager.serverCompatibility()) {
            displayErrorServerCompatibility();
        }
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void initSentry() {
        Log.i(getClass().getName(), "PRODUCTION MODE : " + !BuildConfig.DEBUG);
        if (!BuildConfig.DEBUG) {
            Log.i(getClass().getName(), "sentry initialisation");
            // Use the Sentry DSN (client key) from the Project Settings page on Sentry
            String sentryDsn = getString(R.string.sentry_config);
            Sentry.init(sentryDsn, new AndroidSentryClientFactory(this));
        }
    }

    private void displayErrorServerCompatibility() {
        Toast.makeText(this, getResources().getString(R.string.fleet_server_compatibility_error), Toast.LENGTH_LONG).show();
    }

    private void initLocation() {
        LocationManager locMngr = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !locMngr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.enable_location_services);
            dialog.setMessage(R.string.location_is_disabled_long_text);
            dialog.setPositiveButton(R.string.connection_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            });
            dialog.show();
        }
    }
}
