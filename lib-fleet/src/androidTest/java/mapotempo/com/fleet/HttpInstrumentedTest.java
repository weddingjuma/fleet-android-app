/*
 * Copyright © Mapotempo, 2018
 *
 * This file is part of Mapotempo.
 *
 * Mapotempo is free software. You can redistribute it and/or
 * modify since you respect the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Mapotempo is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the Licenses for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Mapotempo. If not, see:
 * <http://www.gnu.org/licenses/agpl.html>
 */

package mapotempo.com.fleet;

import android.util.Log;

import com.mapotempo.fleet.manager.SyncGatewayLogin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(Parameterized.class)
public class HttpInstrumentedTest
{

    @Parameterized.Parameters
    public static Object[][] data()
    {
        return new Object[1][0];
    }

    private static String TAG = HttpInstrumentedTest.class.getName();

    @Test
    public void useAppContext() throws Exception
    {
        Log.i(TAG, "_____________________________________________________________");
        SyncGatewayLogin restLogin = new SyncGatewayLogin();
        SyncGatewayLogin.SyncGatewayLoginStatus syncGatewayLoginStatus = restLogin.tryLogin(
            "5ffbb992f9c44a4e7a50897f785c5f63d38e587130f7cf86a07359d609dc50dd",
            "123456",
            "ws://10.42.0.1:4984/db");
        Log.i(TAG, syncGatewayLoginStatus.name());
        Log.i(TAG, "_____________________________________________________________");
    }
}
