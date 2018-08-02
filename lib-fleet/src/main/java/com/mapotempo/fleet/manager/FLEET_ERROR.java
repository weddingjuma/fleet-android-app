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

package com.mapotempo.fleet.manager;

import android.support.annotation.Nullable;

import com.mapotempo.fleet.api.FleetException;

/**
 * Connection status :
 * 0 : verify
 * 2XX : document missing
 */
public enum FLEET_ERROR
{
    VERIFY(),
    LOGIN_ERROR(),
    URL_ERROR(),
    DOCUMENT_ERROR(),
    INTERNAL_ERROR(),
    UNKNOWN_ERROR();

    private String mPayload;

    public void setPayload(String payload)
    {
        mPayload = payload;
    }

    public String getPayload()
    {
        return mPayload;
    }

    public static FleetException asException(FLEET_ERROR error, String message, @Nullable Exception e)
    {
        error.setPayload(message);
        if (e != null)
            return new FleetException(error, e);
        return new FleetException(error);
    }
}
