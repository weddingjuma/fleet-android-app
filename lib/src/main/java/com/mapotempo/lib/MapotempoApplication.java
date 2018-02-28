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

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.couchbase.lite.android.AndroidContext;
import com.mapotempo.fleet.api.ManagerFactory;
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.lib.fragments.login.LoginPrefManager;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

public class MapotempoApplication extends Application implements MapotempoApplicationInterface {

    private MapotempoFleetManagerInterface iFleetManager;

    private LoginPrefManager mLoginPrefManager;

    private MapotempoFleetManagerInterface.OnServerConnexionVerify mOnServerConnexionVerify = new MapotempoFleetManagerInterface.OnServerConnexionVerify() {
        @Override
        public void connexion(Status status, MapotempoFleetManagerInterface mapotempoFleetManagerInterface) {
            iFleetManager = mapotempoFleetManagerInterface;
        }
    };

    // ======================================
    // ==  Android Application Life cycle  ==
    // ======================================

    @Override
    public void onCreate() {
        super.onCreate();
        initSentry();

        mLoginPrefManager = new LoginPrefManager(this);
        tryToInitLastSession();
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

    private void tryToInitLastSession() {
        ManagerFactory.getManager(new AndroidContext(this.getApplicationContext()),
                mLoginPrefManager.getLoginPref(),
                mLoginPrefManager.getPasswordPref(),
                mOnServerConnexionVerify,
                mLoginPrefManager.getFullURL());
    }

    private void displayErrorServerCompatibility() {
        Toast.makeText(this, getResources().getString(R.string.fleet_server_compatibility_error), Toast.LENGTH_LONG).show();
    }
}
