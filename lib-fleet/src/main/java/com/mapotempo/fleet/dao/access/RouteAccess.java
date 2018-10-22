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

import com.couchbase.lite.Expression;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.accessor.AccessBase;
import com.mapotempo.fleet.core.accessor.LiveAccessChangeListener;
import com.mapotempo.fleet.core.accessor.LiveAccessToken;
import com.mapotempo.fleet.dao.model.Route;

import java.util.List;

public class RouteAccess extends AccessBase<Route>
{
    public RouteAccess(IDatabaseHandler databaseHandler) throws FleetException
    {
        super(databaseHandler, Route.class, "date");
    }

    public List<Route> archived(boolean archived)
    {
        Expression expression = Expression.property(Route.ARCHIVED_AT);
        expression = archived ? expression.notNullOrMissing() : expression.isNullOrMissing();
        return runQuery(expression, null);
    }


    public LiveAccessToken archived_AddListener(final LiveAccessChangeListener<Route> changeListener, boolean archived)
    {
        Expression expression = Expression.property(Route.ARCHIVED_AT);
        expression = archived ? expression.notNullOrMissing() : expression.isNullOrMissing();
        return createLiveQuery(changeListener, expression, null);
    }
}
