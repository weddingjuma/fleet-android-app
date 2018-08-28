///*
// * Copyright © Mapotempo, 2018
// *
// * This file is part of Mapotempo.
// *
// * Mapotempo is free software. You can redistribute it and/or
// * modify since you respect the terms of the GNU Affero General
// * Public License as published by the Free Software Foundation,
// * either version 3 of the License, or (at your option) any later version.
// *
// * Mapotempo is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// * or FITNESS FOR A PARTICULAR PURPOSE.  See the Licenses for more details.
// *
// * You should have received a copy of the GNU Affero General Public License
// * along with Mapotempo. If not, see:
// * <http://www.gnu.org/licenses/agpl.html>
// */
//
//package mapotempo.com.fleet;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.util.Log;
//
//import com.mapotempo.fleet.api.FleetError;
//import com.mapotempo.fleet.api.ManagerFactory;
//import com.mapotempo.fleet.core.DatabaseHandler;
//import com.mapotempo.fleet.core.accessor.LiveAccessChangeListener;
//import com.mapotempo.fleet.dao.model.Route;
//import com.mapotempo.fleet.manager.MapotempoFleetManager;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.Parameterized;
//
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
///**
// * Instrumented test, which will execute on an Android device.
// *
// * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//@RunWith(Parameterized.class)
//public class RouteTestInstrumentedTest
//{
//
//    @Parameterized.Parameters
//    public static Object[][] data()
//    {
//        return new Object[1][0];
//    }
//
//    private static String TAG = RouteTestInstrumentedTest.class.getName();
//    DatabaseHandler.OnCatchLoginError onCatchLoginError = new DatabaseHandler.OnCatchLoginError()
//    {
//        @Override
//        public void CatchLoginError()
//        {
//        }
//    };
//
//    MapotempoFleetManager manager = null;
//    CountDownLatch cdl = new CountDownLatch(1);
//    FleetError error = null;
//
//    @Before
//    public void init()
//    {
//        manager = null;
//        cdl = new CountDownLatch(1);
//    }
//
//    @Test
//    public void useAppContext() throws Exception
//    {
//        Context appContext = InstrumentationRegistry.getTargetContext();
//
//        ManagerFactory.getManagerAsync(appContext, "driver1@mapotempo.com",
//            "123456",
//            new ManagerFactory.OnManagerReadyListener()
//            {
//                @Override
//                public void onManagerReady(MapotempoFleetManager m, FleetError errorStatus)
//                {
//                    manager = m;
//                    cdl.countDown();
//                    error = errorStatus;
//                    Log.i(TAG, "_____________________________________________________________ READY");
//                }
//            },
//            //                "ws://192.168.1.252:4984/db"
//            "ws://10.42.0.1:4984/db"
//        );
//
//        Log.i(TAG, "_____________________________________________________________ " + (new Date()).getTime());
//        cdl.await(3, TimeUnit.SECONDS);
//        Log.i(TAG, "_____________________________________________________________ " + (new Date()).getTime());
//
//        Thread.sleep(3000);
//        if (manager != null)
//        {
//            manager.getRouteAccess().all_AddListener(new LiveAccessChangeListener<Route>()
//            {
//                @Override
//                public void changed(List<Route> items)
//                {
//                    for (Route r : items)
//                    {
//                        Log.i(TAG, "route : _______________  " + r.getName() + r.getDate() + "______________________");
//                    }
//                }
//            });
//            Log.i(TAG, "_____________________________________________________________ manager successfully create");
//            //            for (Route r : manager.getRouteAccess().all())
//            //            {
//            //                Log.i(TAG, "route : _______________  " + r.getName() + "______________________");
//            //            }
//        }
//        else
//            Log.i(TAG, "_____________________________________________________________ fail create manager : " + (error != null ? error.getPayload() : ""));
//
//
//        Log.i(TAG, "______________________ENNNNNNNNNNNNNNND______________________");
//    }
//}
