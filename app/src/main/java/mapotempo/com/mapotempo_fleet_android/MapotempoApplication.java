package mapotempo.com.mapotempo_fleet_android;

import android.app.Application;

import com.couchbase.lite.android.AndroidContext;
import com.mapotempo.fleet.MapotempoFleetManager;
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;

public class MapotempoApplication extends Application {

    private boolean connectionActive = false;
    private MapotempoFleetManagerInterface iFleetManager;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public MapotempoFleetManagerInterface getManager() {
        return iFleetManager;
    }

    public void setManager(MapotempoFleetManagerInterface manager) {
        if (manager != null) {
            if (iFleetManager != null)
                iFleetManager.close();

            iFleetManager = manager;
        }
    }

    public void setConnectionTo(boolean value) {
        connectionActive = value;
    }

    public boolean isConnected() {
        return connectionActive;
    }

}
