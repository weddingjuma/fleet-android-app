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
import com.couchbase.lite.MutableArray;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.ModelBase;
import com.mapotempo.fleet.core.model.ModelType;
import com.mapotempo.fleet.utils.ModelUtils;

import java.util.List;

@ModelType(type = "user")
public class User extends ModelBase
{

    // MAPOTEMPO KEY
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String SYNC_USER = "sync_user";
    public static final String COMPANY_ID = "company_id";
    public static final String ROLES = "roles";

    public User(IDatabaseHandler databaseHandler, Document document) throws FleetException
    {
        super(databaseHandler, document);
    }

    public String getCompanyId()
    {
        String res = mDocument.getString(COMPANY_ID);
        return res != null ? res : "";
    }


    public String getSyncUser()
    {
        String res = mDocument.getString(SYNC_USER);
        return res != null ? res : "";
    }

    public String getName()
    {
        String res = mDocument.getString(NAME);
        return res != null ? res : "";
    }

    public String getEmail()
    {
        String res = mDocument.getString(EMAIL);
        return res != null ? res : "";
    }

    public List<String> getRoles()
    {
        MutableArray array = mDocument.getArray(ROLES);
        return ModelUtils.arrayToStringList(array);
    }
}
