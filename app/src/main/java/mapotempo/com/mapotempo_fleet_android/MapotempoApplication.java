package mapotempo.com.mapotempo_fleet_android;

import android.app.Application;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;

public class MapotempoApplication extends Application {

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

    public MapotempoFleetManagerInterface getManager() {
        return iFleetManager;
    }

    public void setManager(MapotempoFleetManagerInterface manager) {
        if (manager != null) {
            if (iFleetManager != null)
                iFleetManager.release();

            iFleetManager = manager;
            iFleetManager.onlineStatus(false); // Default is false, wait validation from user.
        }
    }
}
