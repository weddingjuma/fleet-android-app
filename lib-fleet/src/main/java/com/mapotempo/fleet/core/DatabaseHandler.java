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

package com.mapotempo.fleet.core;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.Endpoint;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;
import com.mapotempo.fleet.R;
import com.mapotempo.fleet.api.FleetError;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.rest.syncgateway.Session;
import com.mapotempo.fleet.utils.HashUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabaseHandler implements IDatabaseHandler
{

    private static String TAG = DatabaseHandler.class.getName();

    private final int RELEASE_TIMEOUT = 5;

    private Context mContext;

    private boolean release = false;

    // Database
    private String mDbname;
    public Database mDatabase;
    private DatabaseConfiguration mDatabaseConfiguration;

    // Connexion params
    private Endpoint mTargetEndpoint;
    final private String mUser;
    final private String mPassword;
    final private String mUrl;

    // Replicators
    private Replicator mUserReplicator = null;
    private Replicator mCompanyReplicator = null;
    private Replicator mMissionReplicator = null;

    private List<Replicator> mReplicatorList = new ArrayList<>();

    private Document mLocalMeta;
    private String PASSWORD_SHA256 = "PASSWORD_SHA256";

    public DatabaseHandler(Context context,
                           String user,
                           String password,
                           String url) throws FleetException
    {
        mContext = context;
        mUser = user;
        mPassword = password;
        mUrl = urlWSConverter(url);

        mDbname = databaseNameGenerator(mUser, mUrl, password);
        mDatabaseConfiguration = new DatabaseConfiguration(mContext);

        try
        {
            mDatabase = new Database(mDbname, mDatabaseConfiguration);
        } catch (CouchbaseLiteException e)
        {
            throw FleetError.asException(FleetError.UNKNOWN_ERROR, "Error : Can't open database", e);
        }

        mLocalMeta = mDatabase.getDocument("fleet_local_meta");
        if (mLocalMeta == null)
        {
            MutableDocument mutableDocument = new MutableDocument("fleet_local_meta");
            try
            {
                mDatabase.save(mutableDocument);
                mLocalMeta = mutableDocument;
            } catch (CouchbaseLiteException e)
            {
                throw FleetError.asException(FleetError.UNKNOWN_ERROR, "Error : Can't create/access to fleet_local_meta_document", e);
            }
        }

        Log.d(TAG, mUrl);
        try
        {
            mTargetEndpoint = new URLEndpoint(new URI(mUrl));
        } catch (Exception e)
        {
            throw FleetError.asException(FleetError.URL_ERROR, context.getString(R.string.invalid_url) + " : " + e.getMessage(), e);
        }

        verifyLogin();
    }

    @Override
    public Database getDatabase()
    {
        return mDatabase;
    }

    @Override
    public boolean isRelease()
    {
        return release;
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void configureUserReplication()
    {
        ReplicatorConfiguration replConfig = new ReplicatorConfiguration(mDatabase, mTargetEndpoint);
        replConfig.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
        replConfig.setContinuous(true);
        replConfig.setAuthenticator(new BasicAuthenticator(mUser, mPassword));
        List<String> channels = new ArrayList<>();
        channels.add("!");
        channels.add("user:" + mUser);
        replConfig.setChannels(channels);
        mUserReplicator = new Replicator(replConfig);
        ListenerToken tk = mUserReplicator.addChangeListener(new ReplicatorChangeListener()
        {
            @Override
            public void changed(ReplicatorChange change)
            {
                if (change.getStatus().getError() != null)
                {
                    Log.i(TAG, "Error code ::  " + change.getStatus().getError().getCode());
                    //                    switch (change.getStatus().getError().getCode())
                    //                    {
                    //                    case 10401:
                    //                        break;
                    //                    default:
                    //                        break;
                    //                    }
                }
            }
        });
        mUserReplicator.start();

        mReplicatorList.add(mUserReplicator);
    }

    public void configureCompanyReplication(String companyId)
    {
        ReplicatorConfiguration replConfig = new ReplicatorConfiguration(mDatabase, mTargetEndpoint);
        replConfig.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
        replConfig.setContinuous(true);
        replConfig.setAuthenticator(new BasicAuthenticator(mUser, mPassword));
        List<String> channels = new ArrayList<>();
        channels.add("company:" + companyId);
        channels.add("mission_status_type:" + companyId);
        channels.add("mission_action_type:" + companyId);
        replConfig.setChannels(channels);
        mCompanyReplicator = new Replicator(replConfig);
        mCompanyReplicator.start();
        mReplicatorList.add(mCompanyReplicator);
    }

    public void configureMissionReplication()
    {
        ReplicatorConfiguration replConfig = new ReplicatorConfiguration(mDatabase, mTargetEndpoint);
        replConfig.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
        replConfig.setContinuous(true);
        replConfig.setAuthenticator(new BasicAuthenticator(mUser, mPassword));
        List<String> channels = new ArrayList<>();
        channels.add("mission:" + mUser);
        replConfig.setChannels(channels);
        mMissionReplicator = new Replicator(replConfig);
        mMissionReplicator.start();
        mReplicatorList.add(mMissionReplicator);
    }

    /**
     * Set all replicators status.
     *
     * @param status The new status
     */
    public void setReplicatorsStatus(boolean status)
    {
        Log.d(TAG, (status ? "Start" : "Stop") + " all replicator");
        for (Replicator r : mReplicatorList)
        {
            if (!status)
            {
                r.stop();
                continue;
            }
            r.start();
        }
    }

    /**
     * Get replicators status.
     *
     * @return true if at least one of replicators is started, false else
     */
    public boolean getReplicatorsStatus()
    {
        for (Replicator r : mReplicatorList)
        {
            if (!r.getStatus().getActivityLevel().equals(Replicator.ActivityLevel.STOPPED))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Close database and delete if required.
     * This function is blocking.
     *
     * @param deleteDB database deleting option
     * @throws FleetException
     */
    public void release(final boolean deleteDB) throws FleetException
    {
        Log.i(TAG, "Log all replicators status: ");
        for (Replicator r : mReplicatorList)
        {
            printRepl(r);
        }

        for (Replicator r : mReplicatorList)
        {
            try
            {
                stopContinuousReplicator(r);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "Log all replicators before close database: ");
        for (Replicator r : mReplicatorList)
        {
            printRepl(r);
        }
        try
        {
            if (deleteDB)
                mDatabase.delete();
            else
                mDatabase.close();
        } catch (CouchbaseLiteException e)
        {
            throw new FleetException(FleetError.UNKNOWN_ERROR, e);
        }
        release = true;
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void stopContinuousReplicator(final Replicator repl) throws InterruptedException
    {
        final CountDownLatch latch = new CountDownLatch(1);
        ListenerToken token = repl.addChangeListener(new ReplicatorChangeListener()
        {
            @Override
            public void changed(ReplicatorChange change)
            {
                Log.i(TAG, "Log replicator in changelistener: ");
                printRepl(change.getReplicator());
                Replicator.Status status = change.getStatus();
                if (status.getActivityLevel() == Replicator.ActivityLevel.STOPPED)
                {
                    latch.countDown();
                }
            }
        });
        boolean res = false;
        try
        {
            Log.i(TAG, "Log replicator before stop: ");
            printRepl(repl);
            res = latch.await(RELEASE_TIMEOUT, TimeUnit.SECONDS);
        } finally
        {
            repl.removeChangeListener(token);
        }
        Log.i(TAG, "Log replicator after latch: ");
        printRepl(repl);
    }

    private void printRepl(Replicator repl)
    {
        Log.d(TAG, "        replicator ref : " + System.identityHashCode(repl) + " status : " + repl.getStatus().getActivityLevel());
    }

    private void verifyLogin() throws FleetException
    {
        Session session = Session.create(
            mUrl,
            mUser,
            mPassword,
            mContext);

        switch (session.getSessionStatus())
        {
        case SESSION_OPEN:
            savePassword();
            Session.delete(session);
            break;
        case SERVER_UNREACHABLE:
            if (!mLocalMeta.contains(PASSWORD_SHA256))
                throw FleetError.asException(FleetError.SERVER_UNREACHABLE, "Error : Server is unreachable", null);
            if (!HashUtils.sha256(mPassword).equals(mLocalMeta.getString(PASSWORD_SHA256)))
                throw FleetError.asException(FleetError.LOGIN_ERROR, "", null);
            break;
        case SESSION_ERROR:
            throw FleetError.asException(FleetError.LOGIN_ERROR, "Error : password or login invalid", null);
        case SESSION_CLOSE:
        default:
            throw FleetError.asException(FleetError.UNKNOWN_ERROR, "", null);
        }
    }

    private void savePassword() throws FleetException
    {
        MutableDocument mutableDocument = mLocalMeta.toMutable();
        mutableDocument.setString(PASSWORD_SHA256, HashUtils.sha256(mPassword));
        try
        {
            mDatabase.save(mutableDocument);
            mLocalMeta = mutableDocument;
        } catch (CouchbaseLiteException e)
        {
            throw FleetError.asException(FleetError.UNKNOWN_ERROR, "Error : Can't save to fleet_local_meta_document : \n", e);
        }
    }

    private String databaseNameGenerator(String userName, String url, String mPassword) throws FleetException
    {
        // Database name must be unique for a username, password and specific url.
        return "database2_" + HashUtils.sha256(userName).substring(0, 5) + HashUtils.sha256(url).substring(0, 5) /*+ HashUtils.sha256(mPassword).substring(0,
         5)*/;
    }

    private String urlWSConverter(String url)
    {
        // Silently replace web  HTTP URLs with socket URLs.
        if (url.regionMatches(true, 0, "http:", 0, 5))
        {
            return "ws:" + url.substring(5);
        }
        else if (url.regionMatches(true, 0, "https:", 0, 5))
        {
            return "wss:" + url.substring(6);
        }
        else
            return url;
    }
}
