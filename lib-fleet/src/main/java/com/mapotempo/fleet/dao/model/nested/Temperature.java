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

import android.support.annotation.Nullable;

import com.couchbase.lite.Dictionary;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.NestedModelBase;
import com.mapotempo.fleet.utils.DateUtils;

import java.util.Date;

public class Temperature extends NestedModelBase
{
    public static final String VALUE = "value";
    public static final String DATE = "date";

    public Temperature(IDatabaseHandler iDatabaseHandler, @Nullable Dictionary dictionary)
    {
        super(iDatabaseHandler, dictionary);
    }

    public Temperature(IDatabaseHandler iDatabaseHandler, float value, Date date)
    {
        super(iDatabaseHandler, null);
        mDictionary.setFloat(VALUE, value);
        mDictionary.setString(DATE, DateUtils.toStringISO8601(date));
    }

    public float getValue()
    {
        return mDictionary.getFloat(VALUE);
    }

    public Date getDate()
    {
        return parseISO8601Field(DATE, new Date(0));
    }

    @Override
    public boolean isValid()
    {
        return mDictionary.contains(VALUE) && mDictionary.contains(DATE);
    }
}
