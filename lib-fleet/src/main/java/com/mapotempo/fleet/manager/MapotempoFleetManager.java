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

package com.mapotempo.fleet.manager;

import android.content.Context;
import android.support.annotation.Nullable;

import com.couchbase.lite.Database;
import com.mapotempo.fleet.Config;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.api.OnServerCompatibility;
import com.mapotempo.fleet.core.DatabaseHandler;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.dao.access.CompanyAccess;
import com.mapotempo.fleet.dao.access.MetaInfoAccess;
import com.mapotempo.fleet.dao.access.MissionAccess;
import com.mapotempo.fleet.dao.access.MissionActionAccess;
import com.mapotempo.fleet.dao.access.MissionActionTypeAccess;
import com.mapotempo.fleet.dao.access.MissionStatusTypeAccess;
import com.mapotempo.fleet.dao.access.RouteAccess;
import com.mapotempo.fleet.dao.access.UserAccess;
import com.mapotempo.fleet.dao.access.UserCurrentLocationAccess;
import com.mapotempo.fleet.dao.access.UserSettingsAccess;
import com.mapotempo.fleet.dao.model.Company;
import com.mapotempo.fleet.dao.model.MetaInfo;
import com.mapotempo.fleet.dao.model.User;
import com.mapotempo.fleet.dao.model.UserCurrentLocation;
import com.mapotempo.fleet.dao.model.UserSettings;
import com.mapotempo.fleet.manager.Requirement.FleetConnectionRequirement;
import com.mapotempo.fleet.utils.HashUtils;

import java.util.List;

/**
 * {@inheritDoc}
 */
public class MapotempoFleetManager implements IDatabaseHandler /*implements MapotempoFleetManagerInterface*/
{

    // == General ===========================================

    private MapotempoFleetManager INSTANCE = this;

    private String mUser;

    private String mPassword;

    private Context mContext;

    public DatabaseHandler mDatabaseHandler;

    // == Access ============================================

    private MetaInfoAccess mMetaInfoAccess;

    private CompanyAccess mCompanyAccess;

    private UserAccess mUserAccess;

    private UserSettingsAccess mUserSettingsAccess;

    private RouteAccess mRouteAccess;

    private MissionAccess mMissionAccess;

    private MissionActionAccess mMissionActionAccess;

    private MissionStatusTypeAccess mMissionStatusTypeAccess;

    private MissionActionTypeAccess mMissionActionTypeAccess;

    private UserCurrentLocationAccess mUserCurrentLocationAccess;

    private OnServerCompatibility mOnServerCompatibility;

    //    private final LiveAccessChangeListener<MetaInfo> mMetaInfoListener = new LiveAccessChangeListener<MetaInfo>() {
    //        @Override
    //        public void changed(List<MetaInfo> items) {
    //            ensureServerCompatibility(item);
    //        }
    //    };

    // == Location ==========================================

    private static int LOCATION_TIMEOUT = 30000; // Location timeout in ms

    //    private LocationManager mLocationManager = new LocationManager(null, LOCATION_TIMEOUT);


    // == Connection sequence ================================

    private FleetConnectionRequirement mConnectionRequirement;


    private boolean mLockOffline = false;

    // == Constructor =======================================

    /**
     * TODO
     *
     * @param context         context
     * @param user            user login
     * @param password        password
     * @param databaseHandler databaseHandler
     * @param url             server
     */
    public MapotempoFleetManager(Context context, String user, String password, DatabaseHandler databaseHandler, String url) throws FleetException
    {
        mContext = context;
        mUser = HashUtils.sha256(user); // Hash user to cover email case.
        mPassword = password;
        mDatabaseHandler = databaseHandler;
        mMetaInfoAccess = new MetaInfoAccess(this);
        mRouteAccess = new RouteAccess(this);
        mMissionAccess = new MissionAccess(this);
        mCompanyAccess = new CompanyAccess(this);
        mUserAccess = new UserAccess(this);
        mUserSettingsAccess = new UserSettingsAccess(this);
        mMissionActionAccess = new MissionActionAccess(this);
        mMissionStatusTypeAccess = new MissionStatusTypeAccess(this);
        mMissionActionTypeAccess = new MissionActionTypeAccess(this);
        mUserCurrentLocationAccess = new UserCurrentLocationAccess(this);
    }

    // == MapotempoFleetManagerInterface =====================

    /**
     * {@inheritDoc}
     */
    //    @Override
    public MetaInfo getMetaInfo()
    {
        List<MetaInfo> metaInfos = mMetaInfoAccess.all();
        if (metaInfos.size() > 0)
            return metaInfos.get(0);
        else
            return null;
    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    public Company getCompany()
    {
        List<Company> companies = mCompanyAccess.all();
        if (companies.size() > 0)
            return companies.get(0);
        else
            return null;
    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    public User getUser()
    {
        List<User> users = mUserAccess.all();
        if (users.size() > 0)
            return users.get(0);
        else
            return null;
    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    public UserSettings getUserPreference()
    {
        List<UserSettings> users = mUserSettingsAccess.all();
        if (users.size() > 0)
            return users.get(0);
        else
            return null;
    }

    public UserCurrentLocation getCurrentLocation()
    {
        List<UserCurrentLocation> userCurrentLocations = mUserCurrentLocationAccess.all();
        if (userCurrentLocations.size() > 0)
            return userCurrentLocations.get(0);
        else
            return null;
    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    public RouteAccess getRouteAccess()
    {
        return mRouteAccess;
    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    public MissionAccess getMissionAccess()
    {
        return mMissionAccess;
    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    public MissionActionAccess getMissionActionAccessInterface()
    {
        return mMissionActionAccess;
    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    public MissionStatusTypeAccess getMissionStatusTypeAccessInterface()
    {
        return mMissionStatusTypeAccess;
    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    public MissionActionTypeAccess getMissionActionTypeAccessInterface()
    {
        return mMissionActionTypeAccess;
    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    //    public UserTrackAccess getTrackAccess() {
    //        return mUserTrackAccess;
    //    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    public void onlineStatus(boolean status)
    {
        if (!mLockOffline)
        {
            mDatabaseHandler.onlineStatus(status);
        }
    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    public boolean isOnline()
    {
        return mDatabaseHandler.isOnline();
    }

    /**
     * {@inheritDoc}
     */
    //    @Override
    public boolean serverCompatibility()
    {
        return serverCompatibility(getMetaInfo());
    }

    /**
     * Attache a callback to be notify if server version up during app run.
     */
    //    @Override
    public void addOnServerCompatibilityChange(OnServerCompatibility onServerCompatibility)
    {
        mOnServerCompatibility = onServerCompatibility;
    }

    // == Private method =====================================

    /* return true if server compatibility is ensure */
    private boolean serverCompatibility(@Nullable MetaInfo metaInfo)
    {
        if (metaInfo != null)
            return (metaInfo.getMinimalClientVersion() <= Config.CLIENT_VERSION);
        else
            return false;
    }

    /* switch off the syncgateway synchronisation if the client version is lower than minimal client version required */
    private void ensureServerCompatibility(@Nullable MetaInfo metaInfo)
    {
        boolean status = serverCompatibility(metaInfo);
        mDatabaseHandler.onlineStatus(status);
        mLockOffline = !status;

        // Notify user
        if (mOnServerCompatibility != null)
        {
            mOnServerCompatibility.serverCompatibility(status);
        }
    }

    @Override
    public Database getDatabase()
    {
        return mDatabaseHandler.getDatabase();
    }

    @Override
    public boolean isRelease()
    {
        return mDatabaseHandler.isRelease();
    }

    public void release() throws FleetException
    {
        mDatabaseHandler.release(false);
        // Release ask by user, we don't delete database
        // mLocationManager.releaseManager(); // Release location manager before all
    }
}
