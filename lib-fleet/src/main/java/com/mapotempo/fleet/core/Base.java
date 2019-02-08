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

package com.mapotempo.fleet.core;

import android.support.annotation.NonNull;

import com.mapotempo.fleet.core.model.ModelBase;

public abstract class Base
{
    private static String TAG = ModelBase.class.getName();

    protected IDatabaseHandler mDatabaseHandler;

    public Base(@NonNull IDatabaseHandler databaseHandler)
    {
        mDatabaseHandler = databaseHandler;
        if (mDatabaseHandler == null)
        {
            throw new RuntimeException("Error : Fleet model required DatabaseHandler was init");
        }
        if (mDatabaseHandler.isRelease())
        {
            throw new RuntimeException("Error : Fleet model required a valid DatabaseHandler");
        }
    }
}
