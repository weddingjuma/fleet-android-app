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

package com.mapotempo.fleet.api;

import android.content.Context;

import com.mapotempo.fleet.core.DatabaseHandler;
import com.mapotempo.fleet.manager.LOGIN_STATUS;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.fleet.manager.Requirement.ConnectionRequirement;
import com.mapotempo.fleet.manager.Requirement.FleetConnectionRequirement;
import com.mapotempo.fleet.manager.SyncGatewayLogin;
import com.mapotempo.fleet.utils.HashUtils;

import java.util.HashMap;
import java.util.Map;

//**
// * ManagerFactory.
// * <p>{@link ManagerFactory} is the entry point for the Mapotempo Fleet Java Client.</p>
// * <p>This factory allow to get {@link com.mapotempo.fleet.api.dao.MapotempoFleetManagerInterface} for the entire java application.</p>
// * <p>Three methods getManager are implemented. One synchronous method and two asynchronous methods.
// * The two asynchronous methods return the {@link com.mapotempo.fleet.api.dao.MapotempoFleetManagerInterface} into
// * {@link com.mapotempo.fleet.api.dao.MapotempoFleetManagerInterface.ConnectionVerifyListener} callback method.</p>
// */
public class ManagerFactory
{

    //    /**
    //     * Try to create a connected {@link MapotempoFleetManagerInterface} asynchronously.
    //     * This factory method is asynchronous, and the result is return in the
    //     * {@link MapotempoFleetManagerInterface.ConnectionVerifyListener} provides.
    //     *
    //     * @param context                  The application context
    //     * @param user                     The user name
    //     * @param password                 The user password
    //     * @param onServerConnectionVerify A {@link MapotempoFleetManagerInterface.ConnectionVerifyListener}
    //     * @param url                      A custom url for syncgateway
    //     */

    private static String TAG = ManagerFactory.class.getName();

    private static Map tokenMpa = new HashMap<>();

    private static class FactoryToken
    {
        boolean called = false;
    }

    public interface OnManagerReadyListener
    {
        void onManagerReady(MapotempoFleetManager manager, LOGIN_STATUS errorStatus);
    }

    public static void getManagerAsync(final Context context, final String user, final String password, final OnManagerReadyListener onManagerReadyListener, final String url)
    {
        try
        {
            final String sha266User = HashUtils.sha256(user);

            /* 1) Syncgateway login validation */
            SyncGatewayLogin restLogin = new SyncGatewayLogin();
            SyncGatewayLogin.SyncGatewayLoginStatus status = restLogin.tryLogin(
                sha266User,
                password,
                url);

            switch (status)
            {
            /* 1.1) Correct password and login */
            case VALID:
                validLogin(context, sha266User, password, onManagerReadyListener, url);
                break;
            /* 1.2) Wrong password or login */
            case INVALID:
                onManagerReadyListener.onManagerReady(null, LOGIN_STATUS.LOGIN_ERROR);
                break;
            /* 1.3) Server is unreachable */
            case SERVER_UNREACHABLE:
            default:
                unreachableServer(context, sha266User, password, onManagerReadyListener, url);
                break;
            }


        } catch (FleetException e)
        {
            LOGIN_STATUS error = LOGIN_STATUS.UNKNOW_ERROR;
            error.setPayload(e.getMessage());
            onManagerReadyListener.onManagerReady(null, LOGIN_STATUS.UNKNOW_ERROR);
            return;
        }
    }

    private static void validLogin(final Context context, final String sha266User, final String password, final OnManagerReadyListener onManagerReadyListener, final String url) throws FleetException
    {
        final DatabaseHandler databaseHandler = new DatabaseHandler(context,
            sha266User,
            password,
            url,
            new DatabaseHandler.OnCatchLoginError()
            {
                @Override
                public void CatchLoginError()
                {
                }
            });
        databaseHandler.savePassword();

        final FleetConnectionRequirement connectionRequirement = new FleetConnectionRequirement(databaseHandler);

        boolean test = connectionRequirement.isSatisfy();
        if (test)
        {
            databaseHandler.configureMissionReplication();
            onManagerReadyListener.onManagerReady(new MapotempoFleetManager(context, sha266User, password, databaseHandler, url), null);
        }
        else
            connectionRequirement.asyncSatisfy(new ConnectionRequirement.SatisfyListener()
            {
                @Override
                public void satisfy(boolean status, String payload)
                {
                    try
                    {
                        if (status)
                        {
                            databaseHandler.configureMissionReplication();
                            onManagerReadyListener.onManagerReady(new MapotempoFleetManager(context, sha266User, password, databaseHandler, url), null);
                        }
                        else
                            onManagerReadyListener.onManagerReady(null, LOGIN_STATUS.LOGIN_ERROR);
                    } catch (FleetException e)
                    {
                        onManagerReadyListener.onManagerReady(null, LOGIN_STATUS.LOGIN_ERROR);
                    }
                }
            }, 10000);
    }

    private static void unreachableServer(final Context context, final String sha266User, final String password, final OnManagerReadyListener onManagerReadyListener, final String url) throws FleetException
    {
        DatabaseHandler databaseHandler = new DatabaseHandler(context,
            sha266User,
            password,
            url,
            new DatabaseHandler.OnCatchLoginError()
            {
                @Override
                public void CatchLoginError()
                {
                }
            });
        final FleetConnectionRequirement connectionRequirement = new FleetConnectionRequirement(databaseHandler);
        if (databaseHandler.checkPassword() && connectionRequirement.isSatisfy())
        {
            onManagerReadyListener.onManagerReady(new MapotempoFleetManager(context, sha266User, password, databaseHandler, url), null);
            return;
        }
        onManagerReadyListener.onManagerReady(null, LOGIN_STATUS.LOGIN_ERROR);
    }
}

