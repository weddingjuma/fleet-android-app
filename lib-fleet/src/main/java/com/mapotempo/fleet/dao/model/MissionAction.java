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
import com.mapotempo.fleet.dao.access.MissionActionTypeAccess;
import com.mapotempo.fleet.dao.model.submodel.Location;
import com.mapotempo.fleet.utils.DateUtils;

import java.util.Date;

@ModelType(type = "mission_action")
public class MissionAction extends ModelBase {

    // MAPOTEMPO KEY
    public static final String COMPANY_ID = "company_id";
    public static final String MISSION_ID = "mission_id";
    public static final String MISSION_ACTION_TYPE_ID = "mission_action_type_id";
    public static final String ACTION_LOCATION = "action_location";
    public static final String DATE = "date";

    public MissionAction(IDatabaseHandler databaseHandler, Document document) throws FleetException {
        super(databaseHandler, document);
    }

    public void setActionType(MissionActionType statusType) {
        mDocument.setString(MISSION_ACTION_TYPE_ID, statusType.getId());
    }

    public void setCompany(Company company) {
        mDocument.setString(COMPANY_ID, company.getId());
    }

    public void setMission(Mission mission) {
        mDocument.setString(MISSION_ID, mission.getId());
    }

    public MissionActionType getActionType() {
        String action_type_id = mDocument.getString(MISSION_ACTION_TYPE_ID);
        try {
            MissionActionTypeAccess missionActionTypeAccess = new MissionActionTypeAccess(mDatabaseHandler);
            return missionActionTypeAccess.get(action_type_id);
        } catch (FleetException e) {
            return null;
        }
    }

    public Date getDate() {
        String dateString = mDocument.getString(DATE);
        return DateUtils.fromStringISO8601(dateString);
    }

    public void setDate(Date date) {
        mDocument.setString(DATE, DateUtils.toStringISO8601(date));
    }

    public Location getLocation() {
        try {
            Location res = new Location(mDatabaseHandler, mDocument.getDictionary(ACTION_LOCATION));
            return res;
        } catch (FleetException e) {
            return null;
        }
    }

    public void setLocation(Location location) {
        if (location.isValid())
            mDocument = mDocument.setDictionary(ACTION_LOCATION, location.getDictionary());
    }
}
