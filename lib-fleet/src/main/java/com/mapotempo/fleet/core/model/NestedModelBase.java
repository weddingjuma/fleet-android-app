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

package com.mapotempo.fleet.core.model;

import android.support.annotation.Nullable;

import com.couchbase.lite.Dictionary;
import com.couchbase.lite.MutableDictionary;
import com.mapotempo.fleet.core.Base;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.utils.DateUtils;

import java.util.Date;

/**
 * NestedModelBase
 */
public abstract class NestedModelBase extends Base
{
    private static String TAG = ModelBase.class.getName();

    protected MutableDictionary mDictionary;

    public NestedModelBase(IDatabaseHandler iDatabaseHandler, @Nullable Dictionary dictionary)
    {
        super(iDatabaseHandler);
        if (dictionary == null)
            mDictionary = new MutableDictionary();
        else
            mDictionary = dictionary.toMutable();
    }

    public MutableDictionary getDictionary()
    {
        return mDictionary;
    }

    protected Date parseISO8601Field(String tag, Date _default)
    {
        String dateString = mDictionary.getString(tag);
        if (dateString == null)
        {
            android.util.Log.d(TAG, "dateless dictionary");
            return _default;
        }
        return DateUtils.fromStringISO8601(dateString);
    }

    public abstract boolean isValid();
}
