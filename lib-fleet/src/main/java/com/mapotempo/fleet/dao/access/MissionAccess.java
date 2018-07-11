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

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Expression;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.accessor.AccessBase;
import com.mapotempo.fleet.core.accessor.LiveAccessChangeListener;
import com.mapotempo.fleet.core.accessor.LiveAccessToken;
import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.fleet.dao.model.Route;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MissionAccess extends AccessBase<Mission>
{
    private static final int HOUR_PURGE_OFFSET = -96;

    public MissionAccess(IDatabaseHandler databaseHandler) throws FleetException
    {
        super(databaseHandler, Mission.class, null);
    }

    public void purgeOutdated()
    {
        Date d = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(calendar.HOUR, HOUR_PURGE_OFFSET);
        final Date date = calendar.getTime();

        List<Mission> missions = byDateLessThan(date);
        for (Mission m : missions)
        {
            try
            {
                mDatabaseHandler.getDatabase().purge(m.mDocument);
            } catch (CouchbaseLiteException e)
            {
                e.printStackTrace();
            }
        }
    }

    public List<Mission> byRoute(Route route)
    {
        return runQuery(Expression.property(Mission.ROUTE).equalTo(Expression.string(route.getId())), null);
    }

    public LiveAccessToken byRoute_AddListener(final LiveAccessChangeListener<Mission> changeListener, Route route)
    {
        return createLiveQuery(changeListener, Expression.property(Mission.ROUTE).equalTo(Expression.string(route.getId())), null);
    }
}
