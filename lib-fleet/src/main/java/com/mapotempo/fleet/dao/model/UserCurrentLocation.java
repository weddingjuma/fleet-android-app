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
import com.mapotempo.fleet.dao.model.nested.LocationDetails;

@ModelType(type = "user_current_location")
public class UserCurrentLocation extends ModelBase
{

    // MAPOTEMPO KEY
    public static final String LOCATION_DETAIL = "location_detail";
    public static final String COMPANY_ID = "company_id";

    public UserCurrentLocation(IDatabaseHandler databaseHandler, Document document) throws FleetException
    {
        super(databaseHandler, document);
    }

    public LocationDetails getLocation()
    {
        LocationDetails res = new LocationDetails(mDatabaseHandler, mDocument.getDictionary(LOCATION_DETAIL));
        return res;
    }

    public void setLocation(LocationDetails location)
    {
        if (location.isValid())
            mDocument = mDocument.setDictionary(LOCATION_DETAIL, location.getDictionary());
    }
}
