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

import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.manager.FLEET_ERROR;

public abstract class Base
{
    protected IDatabaseHandler mDatabaseHandler;

    public Base(@NonNull IDatabaseHandler databaseHandler) throws FleetException
    {
        mDatabaseHandler = databaseHandler;
        if (mDatabaseHandler == null)
        {
            throw FLEET_ERROR.asException(FLEET_ERROR.INTERNAL_ERROR, "Error : Fleet model required DatabaseHandler was init", null);
        }
        if (mDatabaseHandler.isRelease())
        {
            throw FLEET_ERROR.asException(FLEET_ERROR.INTERNAL_ERROR, "Error : Fleet model required a valid DatabaseHandler", null);
        }
    }
}
