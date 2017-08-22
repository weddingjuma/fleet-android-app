package mapotempo.com.mapotempo_fleet_android;

import android.app.Application;

import com.couchbase.lite.android.AndroidContext;
import com.mapotempo.fleet.MapotempoFleetManager;
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;

public class MapotempoApplication extends Application {

    private String dataBaseUrl = "http://192.168.1.108:4984/db";
//    private String dataBaseUrl = "http://192.168.1.135:4984/db";
    private String userLogin = "juju";
//    private String userLogin = "static";
    private String userPassword = "juju";
//    private String userPassword = "static";

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
