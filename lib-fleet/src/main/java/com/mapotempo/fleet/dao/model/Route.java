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

import android.support.annotation.Nullable;

import com.couchbase.lite.Document;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.ModelBase;
import com.mapotempo.fleet.core.model.ModelType;
import com.mapotempo.fleet.dao.access.MissionAccess;
import com.mapotempo.fleet.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ModelType(type = "route")
public class Route extends ModelBase
{
    final static private String TAG = Route.class.toString();

    // MAPOTEMPO KEY
    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String ARCHIVED_AT = "archived_at";

    public Route(IDatabaseHandler databaseHandler, Document document) throws FleetException
    {
        super(databaseHandler, document);
    }

    public String getName()
    {
        String name = mDocument.getString(NAME);
        return name != null ? name : "Nameless mission";
    }

    public Date getDate()
    {
        return parseISO8601Field(DATE, new Date(0));
    }

    public List<Mission> getMissions()
    {
        try
        {
            MissionAccess missionAccess;
            missionAccess = new MissionAccess(mDatabaseHandler);
            return missionAccess.byRoute(this);
        } catch (FleetException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean isArchived()
    {
        return mDocument.contains(ARCHIVED_AT);
    }

    public @Nullable
    Date archivedDate()
    {
        return parseISO8601Field(ARCHIVED_AT, null);
    }

    public void archived()
    {
        Date date = new Date();
        mDocument.setString(ARCHIVED_AT, DateUtils.toStringISO8601(date));
        save();
        for (Mission m : getMissions())
        {
            m.archived();
        }
    }

    public void unArchived()
    {
        mDocument.remove(ARCHIVED_AT);
        save();
        for (Mission m : getMissions())
        {
            m.unArchived();
        }
    }
}

