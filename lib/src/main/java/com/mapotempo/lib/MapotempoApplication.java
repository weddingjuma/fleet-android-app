package com.mapotempo.lib;

import android.app.Application;
import android.util.Log;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

public class MapotempoApplication extends Application implements MapotempoApplicationInterface {

    private MapotempoFleetManagerInterface iFleetManager;

    private final MapotempoFleetManagerInterface.OnServerConnexionVerify mOnUserAvailable = new MapotempoFleetManagerInterface.OnServerConnexionVerify() {
        @Override
        public void connexion(final Status status, final MapotempoFleetManagerInterface manager) {
            iFleetManager = manager;
        }
    };

    // ======================================
    // ==  Android Application Life cycle  ==
    // ======================================

    @Override
    public void onCreate() {
        super.onCreate();
        initSentry();
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
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void initSentry() {
        if (!BuildConfig.DEBUG) {
            Log.d(getClass().getName(), "PRODUCTION MODE sentry initialisation");
            // Use the Sentry DSN (client key) from the Project Settings page on Sentry
            String sentryDsn = getString(R.string.sentry_config);
            Sentry.init(sentryDsn, new AndroidSentryClientFactory(this));
        }
    }
}
