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

@ModelType(type = "meta_info")
public class MetaInfo extends ModelBase
{

    // MAPOTEMPO KEY
    private static final String SERVER_VERSION = "server_version";
    private static final String MINIMAL_CLIENT_VERSION = "minimal_client_version";

    public MetaInfo(IDatabaseHandler databaseHandler, Document document) throws FleetException
    {
        super(databaseHandler, document);
    }

    public Integer getServerVersion()
    {
        return mDocument.getInt(SERVER_VERSION);
    }

    public Integer getMinimalClientVersion()
    {
        return mDocument.getInt(MINIMAL_CLIENT_VERSION);
    }
}
