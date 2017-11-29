package com.mapotempo.app;

import android.app.Application;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;

public class MapotempoApplication extends Application implements MapotempoApplicationInterface {

    private MapotempoFleetManagerInterface iFleetManager;

    // ======================================
    // ==  Android Application Life cycle  ==
    // ======================================

    @Override
    public void onCreate() {
        super.onCreate();
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
        if (manager != null) {
            if (iFleetManager != null)
                iFleetManager.release();

            iFleetManager = manager;
            //iFleetManager.onlineStatus(false); // Default is false, wait validation from user.
        }
    }
}
