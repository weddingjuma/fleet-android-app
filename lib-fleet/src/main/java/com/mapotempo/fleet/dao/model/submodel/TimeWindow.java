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

package com.mapotempo.fleet.dao.model.submodel;

import com.couchbase.lite.Dictionary;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.SubModelBase;
import com.mapotempo.fleet.utils.DateUtils;

import java.util.Date;

public class TimeWindow extends SubModelBase {
    public static final String START = "start";
    public static final String END = "end";

    public TimeWindow(IDatabaseHandler iDatabaseHandler, Date start, Date end) throws FleetException {
        super(iDatabaseHandler, null);
        mDictionary.setString(START, DateUtils.toStringISO8601(start));
        mDictionary.setString(END, DateUtils.toStringISO8601(end));
    }

    public TimeWindow(IDatabaseHandler iDatabaseHandler, Dictionary dictionary) throws FleetException {
        super(iDatabaseHandler, dictionary);
    }

    public Date getStart() {
        String dateString = mDictionary.getString(START);
        String res = dateString != null ? dateString : "0";
        return DateUtils.fromStringISO8601(dateString);
    }

    public Date getEnd() {
        String dateString = mDictionary.getString(END);
        String res = dateString != null ? dateString : "0";
        return DateUtils.fromStringISO8601(dateString);
    }

    @Override
    public boolean isValid() {
        return mDictionary.contains(END) && mDictionary.contains(START);
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof TimeWindow))
            return false;

        TimeWindow cmp = (TimeWindow) obj;
        return (getStart().equals(cmp.getStart()) &&
                getEnd().equals(cmp.getEnd()) &&
                super.equals(obj));
    }
}
