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

package com.mapotempo.fleet.dao.access;

import android.support.annotation.Nullable;

import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.accessor.AccessBase;
import com.mapotempo.fleet.dao.model.Company;
import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.fleet.dao.model.MissionAction;
import com.mapotempo.fleet.dao.model.MissionActionType;
import com.mapotempo.fleet.dao.model.submodel.Location;

import java.util.Date;

public class MissionActionAccess extends AccessBase<MissionAction>
{
    public MissionActionAccess(IDatabaseHandler databaseHandler) throws FleetException
    {
        super(databaseHandler, MissionAction.class, null);
    }

    /**
     * {@inheritDoc}
     */
    public MissionAction create(Company company,
                                Mission mission,
                                MissionActionType actionType,
                                Date date,
                                @Nullable Location location)
    {
        MissionAction res = getNew();
        res.setCompany(company);
        res.setMission(mission);
        res.setActionType(actionType);
        res.setDate(date);

        if (location != null)
            res.setLocation(location);

        res.save();
        return res;
    }
}
