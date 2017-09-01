package mapotempo.com.mapotempo_fleet_android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;

import mapotempo.com.mapotempo_fleet_android.utils.ConnectionManager;

public class MapotempoApplication extends Application implements ConnectionManager.OnConnectionSetup {

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
                iFleetManager.release();

            iFleetManager = manager;
            iFleetManager.onlineStatus(false); // Default is false, wait validation from user.
        }
    }

    public void setUserPref(Context context) {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.askUserPreference(context, this, R.layout.user_data_pref);
    }

    @Override
    public void onSelectedDataProviderWifi(ConnectionManager.ConnectionType connectionType, Context context) {
        iFleetManager.onlineStatus(false);
        Activity activity = (Activity) context;

        activity.onBackPressed();
        activity.finish();
    }

    @Override
    public void onSelectedDataProviderBoth(ConnectionManager.ConnectionType connectionType, Context context) {
        iFleetManager.onlineStatus(true);
        Activity activity = (Activity) context;

        activity.onBackPressed();
        activity.finish();
    }
}
