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

package com.mapotempo.fleet.dao.model.nested;

import com.couchbase.lite.Dictionary;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.NestedModelBase;

public class Location extends NestedModelBase
{
    public static final String LAT = "lat";
    public static final String LON = "lon";

    public Location(IDatabaseHandler iDatabaseHandler, double lat, double lon)
    {
        super(iDatabaseHandler, null);
        mDictionary.setDouble(LAT, lat);
        mDictionary.setDouble(LON, lon);
    }

    public Location(IDatabaseHandler iDatabaseHandler, Dictionary dictionary)
    {
        super(iDatabaseHandler, dictionary);
    }

    public double getLat()
    {
        return mDictionary.getDouble(LAT);
    }

    public double getLon()
    {
        return mDictionary.getDouble(LON);
    }

    @Override
    public boolean isValid()
    {
        // Test the real value to ensure isn't null
        if (mDictionary.getValue(LAT) == null || mDictionary.getValue(LON) == null)
            return false;
        if ((getLat() < -90. || getLat() > 90.) && (getLon() < -180. || getLon() > 180.))
            return false;
        return true;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (!(obj instanceof Location))
            return false;

        Location cmp = (Location) obj;
        return (getLat() == cmp.getLat() &&
            getLon() == cmp.getLon()) &&
            super.equals(obj);
    }
}
