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

import com.mapotempo.fleet.manager.FLEET_ERROR;

/**
 * FleetException.
 */
public class FleetException extends Exception
{
    private FLEET_ERROR mLoginStatus = FLEET_ERROR.UNKNOWN_ERROR;

    public FLEET_ERROR getFleetStatus()
    {
        return mLoginStatus;
    }

    public FleetException(FLEET_ERROR error_status)
    {
        super(error_status.getPayload());
        mLoginStatus = error_status;
    }

    public FleetException(FLEET_ERROR error_status, Exception e)
    {
        super(error_status.getPayload(), e);
        mLoginStatus = error_status;
    }
}
