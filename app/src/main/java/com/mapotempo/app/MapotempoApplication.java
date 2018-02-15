package com.mapotempo.app;

import android.app.Application;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;

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
        tryInitSentry();
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
        //iFleetManager.onlineStatus(false); // Default is false, wait validation from user.
    }

    private void tryInitSentry() {
        // Use the Sentry DSN (client key) from the Project Settings page on Sentry
        String sentryDsn = getString(R.string.sentry_config);
        Sentry.init(sentryDsn, new AndroidSentryClientFactory(this));
    }
}
