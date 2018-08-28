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

package com.mapotempo.fleet.api;

import android.support.annotation.Nullable;

/**
 * Connection status :
 * 0 : verify
 * 2XX : document missing
 */
public enum FleetError
{
    VERIFY(),
    LOGIN_ERROR(),
    URL_ERROR(),
    SERVER_UNREACHABLE(),
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

    public static FleetException asException(FleetError error, String payload, @Nullable Exception e)
    {
        error.setPayload(payload);
        if (e != null)
            return new FleetException(error, e);
        return new FleetException(error);
    }
}
