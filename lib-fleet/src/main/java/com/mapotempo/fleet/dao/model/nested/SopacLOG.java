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
import com.mapotempo.fleet.utils.ModelUtils;

import java.util.Date;
import java.util.List;

public class SopacLOG extends NestedModelBase
{
    public static final String BLUE_TAG_ID = "blue_tag_id";
    public static final String SURVEY_DATE = "survey_date";
    public static final String TEMPERATURES = "temperatures";
    public static final String TEMPERATURE_MAX = "temperature_max";
    public static final String TEMPERATURE_MIN = "temperature_min";
    public static final String UNITY = "unity";
    public static final String BREACHES = "breaches";

    public SopacLOG(IDatabaseHandler iDatabaseHandler, @Nullable Dictionary dictionary)
    {
        super(iDatabaseHandler, dictionary);
    }

    public SopacLOG(IDatabaseHandler iDatabaseHandler, long blue_tag_id,
                    Date survey_date,
                    List<Temperature> temperatures,
                    Float temperature_max,
                    Float temperature_min,
                    String unity,
                    int breaches)
    {
        super(iDatabaseHandler, null);
        mDictionary.setLong(BLUE_TAG_ID, blue_tag_id);
        mDictionary.setString(SURVEY_DATE, DateUtils.toStringISO8601(survey_date));
        mDictionary.setArray(TEMPERATURES, ModelUtils.submodelListToArray(iDatabaseHandler, temperatures, Temperature.class));
        mDictionary.setFloat(TEMPERATURE_MAX, temperature_max);
        mDictionary.setFloat(TEMPERATURE_MIN, temperature_min);
        mDictionary.setString(UNITY, unity);
        mDictionary.setInt(BREACHES, breaches);
    }

    public long getTagID()
    {
        return mDictionary.getLong(BLUE_TAG_ID);
    }

    public Date getSurveyDate()
    {
        return parseISO8601Field(SURVEY_DATE, new Date(0));
    }

    public List<Temperature> getTemperatures()
    {
        return ModelUtils.arrayToSubmodelList(mDatabaseHandler, mDictionary.getArray(TEMPERATURES), Temperature.class);
    }

    public float getTemperatureMax()
    {
        return mDictionary.getFloat(TEMPERATURE_MAX);
    }

    public float getTemperatureMin()
    {
        return mDictionary.getFloat(TEMPERATURE_MIN);
    }

    public String getUnity()
    {
        return mDictionary.getString(UNITY);
    }

    public int getBreaches()
    {
        return mDictionary.getInt(BREACHES);
    }

    @Override
    public boolean isValid()
    {
        return mDictionary.contains(BLUE_TAG_ID) &&
            mDictionary.contains(TEMPERATURES) &&
            mDictionary.contains(TEMPERATURE_MAX) &&
            mDictionary.contains(TEMPERATURE_MIN) &&
            mDictionary.contains(UNITY);
    }
}
