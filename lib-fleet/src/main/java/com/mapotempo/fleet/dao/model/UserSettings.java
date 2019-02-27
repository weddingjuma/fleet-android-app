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
import com.mapotempo.fleet.BuildConfig;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.ModelBase;
import com.mapotempo.fleet.core.model.ModelType;
import com.mapotempo.fleet.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;

@ModelType(type = "user_settings")
public class UserSettings extends ModelBase
{
    String TAG = UserSettings.class.getName();

    public static final String CURRENT_SIGN_IN_AT = "current_sign_in_at";
    public static final String CURRENT_SIGN_IN_IP = "current_sign_in_ip";
    public static final String CURRENT_APP_VERSION = "current_app_version";
    public static final String CURRENT_LIB_VERSION = "current_lib_version";
    public static final String CURRENT_SIGN_IN_CONNEXION_TYPE = "current_sign_in_connexion_type";
    public static final String CURRENT_SIGN_TIME_ZONE = "current_sign_in_time_zone";
    public static final String SIGN_IN_COUNT = "sign_in_count";

    // MAPOTEMPO KEY
    public static final String NIGHT_MODE = "night_mode";

    public UserSettings(IDatabaseHandler databaseHandler, Document document) throws FleetException
    {
        super(databaseHandler, document);
    }

    public enum Preference
    {
        MOBILE_DATA_USAGE("data_connection", true),
        AUTOMATIC_DATA_UPDATE("automatic_data_update", true),
        TRACKING_ENABLE("tracking_enable", true),
        MAP_CURRENT_POSITION("map_current_position", true),
        MAP_DISPLAY_ZOOM_BUTTON("map_display_zoom_button", true);

        private String mTag;

        private boolean mDef;

        Preference(String tag, boolean def)
        {
            mTag = tag;
            mDef = def;
        }

        public String getTag()
        {
            return mTag;
        }

        public boolean getDefault()
        {
            return mDef;
        }

        public String toString()
        {
            return mTag;
        }
    }

    public Boolean getBoolPreference(Preference preference)
    {
        if (!mDocument.contains(preference.getTag()))
            return preference.getDefault();
        return mDocument.getBoolean(preference.getTag());
    }

    public void setBoolPreference(Preference preference, Boolean status)
    {
        mDocument.setBoolean(preference.getTag(), status);
    }

    public enum NightModePreference
    {
        AUTOMATIC("automatic"),
        NIGHT("night"),
        DAY("day");

        private String preference;

        NightModePreference(String name)
        {
            preference = name;
        }

        public String toString()
        {
            return preference;
        }

        static public NightModePreference fromString(String value)
        {
            if ("".equals(value))
                return AUTOMATIC;

            try
            {
                return valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e)
            {
                return AUTOMATIC;
            }
        }
    }

    public NightModePreference getNightModePreference()
    {
        String nightMode = mDocument.getString(NIGHT_MODE);
        return NightModePreference.fromString(nightMode != null ? nightMode : "");
    }

    public void setNightModePreference(NightModePreference status)
    {
        mDocument.setString(NIGHT_MODE, status.toString());
    }

    public void setConnexionInformation(Date currentSignInAt,
                                        String currentSignInConnexiontype,
                                        String currentSignInIp,
                                        String currentAppVersion)
    {
        mDocument.setString(CURRENT_SIGN_IN_AT, DateUtils.toStringISO8601(currentSignInAt));
        mDocument.setString(CURRENT_SIGN_IN_IP, currentSignInIp);
        mDocument.setString(CURRENT_APP_VERSION, currentAppVersion);
        mDocument.setInt(SIGN_IN_COUNT, mDocument.getInt(SIGN_IN_COUNT) + 1);
        mDocument.setString(CURRENT_SIGN_IN_CONNEXION_TYPE, currentSignInConnexiontype);
        mDocument.setString(CURRENT_LIB_VERSION, BuildConfig.VERSION_NAME);
        mDocument.setString(CURRENT_SIGN_TIME_ZONE, Calendar.getInstance().getTimeZone().getID());
    }
}
