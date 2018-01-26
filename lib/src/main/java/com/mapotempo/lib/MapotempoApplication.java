package com.mapotempo.lib;

import android.app.Application;
import android.util.Log;

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

    private void tryToInitLastSession() {
        ManagerFactory.getManager(new AndroidContext(this.getApplicationContext()),
                mLoginPrefManager.getLoginPref(),
                mLoginPrefManager.getPasswordPref(),
                mOnServerConnexionVerify,
                mLoginPrefManager.getFullURL());
    }
}
