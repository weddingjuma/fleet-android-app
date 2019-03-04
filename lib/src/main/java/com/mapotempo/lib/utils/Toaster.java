/*
 * Copyright © Mapotempo, 2019
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

package com.mapotempo.lib.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.Toast;

public class Toaster
{

    private static final int SHORT_TOAST_DURATION = 2000;
    private static final long TOAST_DURATION_MILLS = 15000; //change if need longer

    // Private constructor. Prevents instantiation from other classes.
    private Toaster() { }

    /**
     * Initializes singleton.
     * <p>
     * ToasterHolder is loaded on the first execution of Toaster.getInstance()
     * or the first access to ToasterHolder.INSTANCE, not before.
     */
    private static class ToasterHolder
    {
        private static final Toaster INSTANCE = new Toaster();
    }

    public static Toaster getInstance()
    {
        return ToasterHolder.INSTANCE;
    }

    /**
     * Long toast message
     * TOAST_DURATION_MILLS controls the duration
     * currently set to 6 seconds
     *
     * @param context Application Context
     * @param msg     Message to send
     */
    public static void msgLong(final Context context, final String msg)
    {
        if (context != null && msg != null)
        {
            new CountDownTimer(Math.max(TOAST_DURATION_MILLS - SHORT_TOAST_DURATION, 1000), 1000)
            {
                @Override
                public void onFinish()
                {
                    makeToast(context, msg).show();
                }

                @Override
                public void onTick(long millisUntilFinished)
                {
                    makeToast(context, msg).show();
                }
            }.start();
        }
    }

    private static Toast makeToast(Context context, String msg)
    {
        final Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        return t;
    }
}
