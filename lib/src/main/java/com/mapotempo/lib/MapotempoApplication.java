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

import com.mapbox.mapboxsdk.Mapbox;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.lib.utils.Toaster;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.android.AndroidSentryClientFactory;

public class MapotempoApplication extends Application implements MapotempoApplicationInterface
{
    private MapotempoFleetManager iFleetManager;

    private SentryClient mSentryClient;

    // ======================================
    // ==  Android Application Life cycle  ==
    // ======================================
    @Override
    public void onCreate()
    {
        super.onCreate();
        // Init Mapbox
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token_dummy));

        // Init Sentry
        initSentry();
    }

    // ==============
    // ==  Public  ==
    // ==============

    @Override
    public MapotempoFleetManager getManager()
    {
        return iFleetManager;
    }

    @Override
    public void setManager(MapotempoFleetManager manager)
    {
        if (iFleetManager != null)
        {
            try
            {
                iFleetManager.release();
            } catch (FleetException e)
            {
                e.printStackTrace();
            } finally
            {
                iFleetManager = null;
            }
        }
        iFleetManager = manager;

        if (iFleetManager != null)
        {
            setSentryInformation(iFleetManager); // Set Sentry extra information before all
            iFleetManager.purgeArchivedRoute(7);
            if (!iFleetManager.serverCompatibility())
            {
                displayErrorServerCompatibility();
            }
        }
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void initSentry()
    {
        Log.i(getClass().getName(), "PRODUCTION MODE : " + !BuildConfig.DEBUG);
        if (!BuildConfig.DEBUG)
        {
            Log.i(getClass().getName(), "sentry initialisation");
            // Use the Sentry DSN (client key) from the Project Settings page on Sentry
            String sentryDsn = getString(R.string.sentry_config);
            mSentryClient = Sentry.init(sentryDsn, new AndroidSentryClientFactory(this));
        }
    }

    private void setSentryInformation(MapotempoFleetManager manager)
    {
        if (mSentryClient != null)
        {
            mSentryClient.addTag("fleet_name", manager.getUser().getName());
            mSentryClient.addTag("fleet_sync_user", manager.getUser().getSyncUser());
            mSentryClient.addTag("fleet_email", manager.getUser().getEmail());
            mSentryClient.addTag("fleet_company", manager.getCompany().getName());
            mSentryClient.addTag("fleet_url", manager.getUrl());
        }
    }

    private void displayErrorServerCompatibility()
    {
        Toaster.getInstance().msgLong(this, getResources().getString(R.string.fleet_server_compatibility_error));
    }
}
