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
//import com.mapotempo.fleet.core.DatabaseHandler;
//import com.mapotempo.fleet.dao.access.CompanyAccess;
//import com.mapotempo.fleet.dao.access.MissionAccess;
//import com.mapotempo.fleet.dao.access.MissionActionTypeAccess;
//import com.mapotempo.fleet.dao.access.UserAccess;
//import com.mapotempo.fleet.dao.model.Company;
//import com.mapotempo.fleet.dao.model.Mission;
//import com.mapotempo.fleet.dao.model.MissionActionType;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.Parameterized;
//
//import java.util.ArrayList;
//
///**
// * Instrumented test, which will execute on an Android device.
// *
// * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//@RunWith(Parameterized.class)
//public class AccessInstrumentedTest
//{
//
//    @Parameterized.Parameters
//    public static Object[][] data()
//    {
//        return new Object[1][0];
//    }
//
//    private static String TAG = AccessInstrumentedTest.class.getName();
//    DatabaseHandler.OnCatchLoginError onCatchLoginError = new DatabaseHandler.OnCatchLoginError()
//    {
//        @Override
//        public void CatchLoginError()
//        {
//        }
//    };
//
//    @Test
//    public void useAppContext() throws Exception
//    {
//        // Database.setLogLevel(LogDomain.ALL, LogLevel.DEBUG);
//
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getTargetContext();
//
//        DatabaseHandler dbHandler = new DatabaseHandler(appContext,
//            "5ffbb992f9c44a4e7a50897f785c5f63d38e587130f7cf86a07359d609dc50dd",
//            //                "0a7f20167d08b0b9833db8c59cba553f93f28e047ab412057c29088eed39a76a",
//            "123456",
//            "ws://10.42.0.1:4984/db",
//            onCatchLoginError);
//
//        dbHandler.configureUserReplication();
//        dbHandler.configureCompanyReplication("company-dzC-REbyrC");
//        dbHandler.configureMissionReplication(new ArrayList<Mission>());
//
//        UserAccess userAccess = new UserAccess(dbHandler);
//        CompanyAccess companyAccess = new CompanyAccess(dbHandler);
//        MissionAccess missionAccess = new MissionAccess(dbHandler);
//        MissionActionTypeAccess missionActionTypeAccess = new MissionActionTypeAccess(dbHandler);
//
//        // Wait for sync replication
//        //        Thread.sleep(8000);
//        //        Log.i(TAG, "_User_____________________________________________________");
//        //        for (User u : userAccess.all()) {
//        //            Log.i(TAG, u.getId());
//        //        }
//        //
//        Log.i(TAG, "_Company_____________________________________________________");
//        for (Company u : companyAccess.all())
//        {
//            Log.i(TAG, u.getId());
//        }
//        Log.i(TAG, "_Mission_Action_Type______________________________________");
//        for (MissionActionType u : missionActionTypeAccess.all())
//        {
//            Log.i(TAG, u.getId());
//            Log.i(TAG, u.getPreviousStatus().getId());
//        }
//        Log.i(TAG, "_Mission_____________________________________________________");
//        for (Mission u : missionAccess.all())
//        {
//            Log.i(TAG, u.getId());
//            Log.i(TAG, u.getDate().toString());
//        }
//        //
//        //        Log.i(TAG, "_Mission_by_date_____________________________________________");
//        //        for (Mission m : missionAccess.byDateLessThan(new Date())) {
//        //            Log.i(TAG, m.getId());
//        //            Log.i(TAG, m.getDate().toString());
//        //        }
//        //
//        //
//        //        Log.i(TAG, "_Mission_by_name_____________________________________________");
//        //        for (Mission m : missionAccess.byName("Pharmacie Burdigala")) {
//        //            Log.i(TAG, m.getId());
//        //            Log.i(TAG, m.getAddress().getCity());
//        //            Log.i(TAG, m.getDate().toString());
//        //            Log.i(TAG, "    current status");
//        //            Log.i(TAG, m.getStatusType().getLabel());
//        //            Log.i(TAG, "    next action");
//        //            for (MissionActionType ma : m.getStatusType().getNextActionType()) {
//        //                Log.i(TAG, ma.getNextStatus().getLabel());
//        //            }
//        //            Log.i(TAG, "    time windows");
//        //            for (TimeWindow tm : m.getTimeWindows()) {
//        //                Log.i(TAG, tm.getEnd() + " " + tm.getStart());
//        //            }
//        //
//        //            missionAccess.addListener(m, new ModelAccessChangeListener<Mission>() {
//        //                @Override
//        //                public void changed(Mission item) {
//        //                    Log.i(TAG, "mission : " + item.getId() + " change");
//        //                    Log.i(TAG, item.getAddress().getCity());
//        //                }
//        //            });
//        //        }
//
//        //        missionAccess.byDateGreaterThan_AddListener(new LiveAccessChangeListener<Mission>() {
//        //            @Override
//        //            public void changed(List<Mission> items) {
//        //                Log.i(TAG, "LiveAccessChangeListener");
//        //                for (Mission m : items) {
//        //                    Log.i(TAG, m.getId() + " - " + m.getDate());
//        //                }
//        //            }
//        //        }, new Date());
//
//        Log.i(TAG, "WAITING FOR CHANGE");
//        Thread.sleep(30000);
//        Log.i(TAG, "STOP WAITING");
//
//        Log.i(TAG, "_____________________________________________________________");
//
//        dbHandler.release(false);
//
//        Log.i(TAG, "______________________ENNNNNNNNNNNNNNND______________________");
//    }
//}
