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

package com.mapotempo.fleet.manager.Requirement;

import android.util.Log;

import com.mapotempo.fleet.api.FleetError;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.DatabaseHandler;
import com.mapotempo.fleet.dao.access.CompanyAccess;
import com.mapotempo.fleet.dao.access.MetaInfoAccess;
import com.mapotempo.fleet.dao.access.UserAccess;
import com.mapotempo.fleet.dao.access.UserCurrentLocationAccess;
import com.mapotempo.fleet.dao.access.UserSettingsAccess;
import com.mapotempo.fleet.dao.model.Company;
import com.mapotempo.fleet.dao.model.MetaInfo;
import com.mapotempo.fleet.dao.model.User;
import com.mapotempo.fleet.dao.model.UserCurrentLocation;
import com.mapotempo.fleet.dao.model.UserSettings;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class ensure that after login all minimals documents data are download.
 * The minimal documents data required are :
 * --------------------------------------------------------------
 * |       Document         |   Channel                         |
 * --------------------------------------------------------------
 * |    User                |   user:[sync gateway user name]   |
 * |    UserSettings        |   user:[sync gateway user name]   |
 * |    UserCurrentLocation |   user:[sync gateway user name]   |
 * |    Company             |   company:[company id]            |
 * |    MetaInfo            |   !                               |
 * --------------------------------------------------------------
 * Company document should be access only when the user channel are download, because [company id]
 * information require to set channel are only accessible in User.
 */
public class FleetConnectionRequirement implements ConnectionRequirement
{

    private static String TAG = DatabaseHandler.class.getName();

    // == Private attribute =================================
    private FleetConnectionRequirement INSTANCE = this;

    private DatabaseHandler mDatabaseHandler;

    private UserAccess mUserAccess;

    private UserSettingsAccess mUserSettingsAccess;

    private UserCurrentLocationAccess mUserCurrentLocationAccess;

    private CompanyAccess mCompanyAccess;

    private MetaInfoAccess mMetaInfoAccess;

    private boolean mChannelInit = false;

    private Timer mVerifyTimer = new Timer();

    private final int TIMER_DELAY = 2000;

    private final int TIMER_PERIOD = 2000;

    private int mVerifyCounter = 0;

    // == Private Interface =================================

    public interface ConnectionRequirementVerifyListener
    {
        void onConnectionVerify(User user, UserCurrentLocation userCurrentLocation, UserSettings userSettings, Company company, MetaInfo metaInfo);

        void onConnectionFail(FleetError code);
    }

    public FleetConnectionRequirement(DatabaseHandler databaseHandler) throws FleetException
    {
        mDatabaseHandler = databaseHandler;
        mUserAccess = new UserAccess(mDatabaseHandler);
        mUserCurrentLocationAccess = new UserCurrentLocationAccess(mDatabaseHandler);
        mUserSettingsAccess = new UserSettingsAccess(mDatabaseHandler);
        mCompanyAccess = new CompanyAccess(mDatabaseHandler);
        mMetaInfoAccess = new MetaInfoAccess(mDatabaseHandler);

        mDatabaseHandler.configureUserReplication();
    }

    @Override
    public boolean isSatisfy()
    {
        User user = mUserAccess.all().size() > 0 ? (User) mUserAccess.all().get(0) : null;
        UserCurrentLocation userCurrentLocation = mUserCurrentLocationAccess.all().size() > 0 ? (UserCurrentLocation) mUserCurrentLocationAccess.all().get(0) : null;
        UserSettings userSettings = mUserSettingsAccess.all().size() > 0 ? (UserSettings) mUserSettingsAccess.all().get(0) : null;
        Company company = mCompanyAccess.all().size() > 0 ? (Company) mCompanyAccess.all().get(0) : null;
        MetaInfo metaInfo = mMetaInfoAccess.all().size() > 0 ? (MetaInfo) mMetaInfoAccess.all().get(0) : null;

        Log.d(TAG, "user: " + user +
            "\nuserCurrentLocation: " + userCurrentLocation +
            "\nuserSettings:" + userSettings +
            "\ncompany:" + company +
            "\nmetaInfo:" + metaInfo);
        if (user == null || userCurrentLocation == null || userSettings == null || company == null || metaInfo == null)
            return false;
        return true;
    }

    @Override
    public void asyncSatisfy(final SatisfyListener listener, final int timeout)
    {
        // ================================================================
        // == 2) - Verify connection to ensure user document already present
        // ================================================================
        if (!isSatisfy())
        {

            // ===========================
            // == 4) - Launch Verify timer
            // ===========================
            final VerifyTimerTask timerTask = new VerifyTimerTask(listener, timeout);
            mVerifyTimer.schedule(timerTask, TIMER_DELAY, TIMER_PERIOD);
        }
        else
            listener.satisfy(true, "");
    }

    @Override
    public boolean syncSatisfy(int timeout)
    {
        if (true)
            throw new UnknownError("Not yet implemted");
        return false;
    }

    public void stopAllConnectionRequirementSequence()
    {
        mVerifyTimer.cancel();
        mVerifyTimer.purge();
    }

    private class VerifyTimerTask extends TimerTask
    {
        int mTimeout;
        SatisfyListener mListener;

        VerifyTimerTask(SatisfyListener listener, int timeout)
        {
            mTimeout = timeout;
            mListener = listener;
        }

        @Override
        public void run()
        {
            User user = mUserAccess.all().size() > 0 ? (User) mUserAccess.all().get(0) : null;
            UserCurrentLocation userCurrentLocation = mUserCurrentLocationAccess.all().size() > 0 ? (UserCurrentLocation) mUserCurrentLocationAccess.all().get(0) : null;
            UserSettings userSettings = mUserSettingsAccess.all().size() > 0 ? (UserSettings) mUserSettingsAccess.all().get(0) : null;
            Company company = mCompanyAccess.all().size() > 0 ? (Company) mCompanyAccess.all().get(0) : null;
            MetaInfo metaInfo = mMetaInfoAccess.all().size() > 0 ? (MetaInfo) mMetaInfoAccess.all().get(0) : null;
            // Timeout check
            if (System.currentTimeMillis() - scheduledExecutionTime() >= mTimeout)
            {
                cancel();
                mListener.satisfy(isSatisfy(), "");
            }
            else
            {
                if (user != null)
                {
                    if (!mChannelInit)
                    {
                        mDatabaseHandler.configureMissionReplication();
                        mDatabaseHandler.configureCompanyReplication(user.getCompanyId());
                        mChannelInit = true;
                    }

                    // The others documents require are present
                    if (isSatisfy())
                    {
                        cancel();
                        mListener.satisfy(true, "");
                    }
                }
            }
        }
    }
}
