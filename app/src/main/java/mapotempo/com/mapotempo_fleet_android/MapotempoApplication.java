package mapotempo.com.mapotempo_fleet_android;

import android.app.Application;

import com.couchbase.lite.android.AndroidContext;
import com.mapotempo.fleet.MapotempoFleetManager;
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;

public class MapotempoApplication extends Application {

    private String dataBaseUrl = "http://192.168.1.108:4984/db";
    private String userLogin = "juju";
    private String userPassword = "juju";

    private MapotempoFleetManagerInterface iFleetManager;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< init couch base >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        iFleetManager = MapotempoFleetManager.getManager(new AndroidContext(getApplicationContext()),  userLogin, userPassword, dataBaseUrl);
    }

    public MapotempoFleetManagerInterface getManager() {
        return iFleetManager;
    }

    public void exemple_THREAD_KillMe () {

        // SAMPLE TO UPDATE ON THE GOOD THREAD

//        MapotempoFleetManagerInterface iMapo = MapotempoFleetManager.getManager(new AndroidContext(getApplicationContext()),  userLogin, userPassword, dataBaseUrl);
//        MissionAccessInterface missionAccess = iMapo.getMissionAccess();
//
//        missionAccess.addChangeListener(new Access.ChangeListener<Mission>() {
//            @Override
//            public void changed(List<Mission> missions) {
//                for(final Mission m : missions) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            System.out.println(m.getName());
//                            System.out.println(m.getAddress());
//                            System.out.println(m.getDeliveryDate());
//                            System.out.println(m.getCompanyId());
//                        }
//                    });
//                }
//            }
//        });
//
//        try {
//            List<Mission> missions = missionAccess.getAll();
//            for(Mission m : missions) {
//                System.out.println(m.getName());
//                System.out.println(m.getAddress());
//                System.out.println(m.getDeliveryDate());
//                System.out.println(m.getCompanyId());
//            }
//        } catch (CoreException e) {
//            e.printStackTrace();
//        }

        return;
    }

}
