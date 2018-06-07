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

package com.mapotempo.fleet.dao.model;

import com.couchbase.lite.Document;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.ModelBase;
import com.mapotempo.fleet.core.model.ModelType;
import com.mapotempo.fleet.dao.access.MissionStatusTypeAccess;

@ModelType(type = "mission_action_type")
public class MissionActionType extends ModelBase
{

    // MAPOTEMPO KEY
    public static final String LABEL = "label";
    public static final String GROUP = "group";
    public static final String PREVIOUS_STATUS_TYPE_ID = "previous_mission_status_type_id";
    public static final String NEXT_STATUS_TYPE_ID = "next_mission_status_type_id";

    public MissionActionType(IDatabaseHandler databaseHandler, Document document) throws FleetException
    {
        super(databaseHandler, document);
    }

    public String getLabel()
    {
        String res = mDocument.getString(LABEL);
        return res != null ? res : "";
    }

    public String getGroup()
    {
        String res = mDocument.getString(GROUP);
        return res != null ? res : "";
    }

    public MissionStatusType getPreviousStatus()
    {
        String status_id = mDocument.getString(PREVIOUS_STATUS_TYPE_ID);
        try
        {
            MissionStatusTypeAccess missionStatusTypeAccess = new MissionStatusTypeAccess(mDatabaseHandler);
            return missionStatusTypeAccess.get(status_id);
        } catch (FleetException e)
        {
            return null;
        }
    }

    public MissionStatusType getNextStatus()
    {
        String status_id = mDocument.getString(NEXT_STATUS_TYPE_ID);
        try
        {
            MissionStatusTypeAccess missionStatusTypeAccess = new MissionStatusTypeAccess(mDatabaseHandler);
            return missionStatusTypeAccess.get(status_id);
        } catch (FleetException e)
        {
            return null;
        }
    }
}
