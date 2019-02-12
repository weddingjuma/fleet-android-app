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
import com.mapotempo.fleet.utils.DateUtils;

import java.util.Date;

public class LocationDetails extends Location
{

    public static final String DATE = "date";
    public static final String ACCURACY = "accuracy";
    public static final String SPEED = "speed";
    public static final String BEARING = "bearing";
    public static final String ALTITUDE = "altitude";
    public static final String SIGNAL_STRENGTH = "signal_strength";
    public static final String CID = "cid";
    public static final String LAC = "lac";
    public static final String MCC = "mcc";
    public static final String MNC = "mnc";

    public LocationDetails(IDatabaseHandler iDatabaseHandler,
                           double lat,
                           double lon,
                           Date date,
                           Double accuracy,
                           Double speed,
                           Double bearing,
                           Double elevation,
                           Integer signalStrength,
                           Integer cid,
                           Integer lac,
                           Integer mcc,
                           Integer mnc)
    {
        super(iDatabaseHandler, lat, lon);
        mDictionary.setString(DATE, DateUtils.toStringISO8601(date));
        mDictionary.setDouble(ACCURACY, accuracy);
        mDictionary.setDouble(SPEED, speed);
        mDictionary.setDouble(BEARING, bearing);
        mDictionary.setDouble(ALTITUDE, elevation);
        mDictionary.setInt(SIGNAL_STRENGTH, signalStrength);
        mDictionary.setInt(CID, cid);
        mDictionary.setInt(LAC, lac);
        mDictionary.setInt(MCC, mcc);
        mDictionary.setInt(MNC, mnc);
    }

    public LocationDetails(IDatabaseHandler iDatabaseHandler, Dictionary dictionary)
    {
        super(iDatabaseHandler, dictionary);
    }

    public Date getDate()
    {
        String dateString = mDictionary.getString(DATE);
        String res = dateString != null ? dateString : "0";
        return DateUtils.fromStringISO8601(dateString);
    }

    public Double getAccuracy()
    {
        return mDictionary.getDouble(ACCURACY);
    }

    public Double getSpeed()
    {
        return mDictionary.getDouble(SPEED);
    }

    public Double getBearing()
    {
        return mDictionary.getDouble(BEARING);
    }

    public Double getAltitude()
    {
        return mDictionary.getDouble(ALTITUDE);
    }

    public Integer getSignalStrength()
    {
        return mDictionary.getInt(SIGNAL_STRENGTH);
    }

    public Integer getCid()
    {
        return mDictionary.getInt(CID);
    }

    public Integer getLac()
    {
        return mDictionary.getInt(LAC);
    }

    public Integer getMcc()
    {
        return mDictionary.getInt(MCC);
    }

    public Integer getMnc()
    {
        return mDictionary.getInt(MNC);
    }

    @Override
    public boolean isValid()
    {
        if (super.isValid())
            return mDictionary.contains(DATE);
        return false;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof TimeWindow))
            return false;

        if (this == obj)
            return true;

        LocationDetails cmp = (LocationDetails) obj;
        return (getDate().equals(obj) &&
            getAccuracy().equals(cmp.getAccuracy()) &&
            getSpeed().equals(cmp.getAccuracy()) &&
            getBearing().equals(cmp.getBearing()) &&
            getAltitude().equals(cmp.getAltitude()) &&
            getSignalStrength().equals(cmp.getSignalStrength()) &&
            getCid().equals(cmp.getCid()) &&
            getLac().equals(cmp.getLac()) &&
            getMcc().equals(cmp.getMcc()) &&
            getMnc().equals(cmp.getMnc()) &&
            super.equals(obj));
    }
}
