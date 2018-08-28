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
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mapotempo.fleet.api.FleetError;
import com.mapotempo.fleet.api.FleetException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class DatabaseHandlerInstrumentedTest
{
    MockWebServer mMockServer;

    @Before
    public void beforeAll()
    {
        mMockServer = new MockWebServer();
    }

    @Test
    public void shouldCreateValidDatabaseHandler() throws Exception
    {
        Context appContext = InstrumentationRegistry.getTargetContext();
        // Syncgateway server mock
        mMockServer.enqueue(new MockResponse().setResponseCode(200));
        mMockServer.start();
        HttpUrl baseUrl = mMockServer.url("/test/db/");

        final DatabaseHandler databaseHandler = new DatabaseHandler(appContext, "driver1@mapotempo.com",
            "123456",
            baseUrl.toString());
    }

    @Test
    public void shouldThrowFleetExceptionWithLoginError() throws Exception
    {
        Context appContext = InstrumentationRegistry.getTargetContext();
        // Syncgateway server mock
        mMockServer = new MockWebServer();
        mMockServer.enqueue(new MockResponse().setResponseCode(400));
        mMockServer.start();
        HttpUrl baseUrl = mMockServer.url("/test/db/");

        try
        {
            final DatabaseHandler databaseHandler = new DatabaseHandler(appContext, "driver1@mapotempo.com",
                "123456",
                baseUrl.toString());
        } catch (FleetException e)
        {
            Assert.assertNotNull(e.getFleetError());
            Assert.assertEquals(e.getFleetError(), FleetError.LOGIN_ERROR);
        }
    }

    @Test
    public void shouldThrowFleetExceptionWithServerUnreachable() throws Exception
    {
        Context appContext = InstrumentationRegistry.getTargetContext();
        try
        {
            new DatabaseHandler(appContext, "driver1@mapotempo.com",
                "123456",
                "http://mapo-fail-url");
        } catch (FleetException e)
        {
            Assert.assertNotNull(e.getFleetError());
            Assert.assertEquals(e.getFleetError(), FleetError.SERVER_UNREACHABLE);
        }
    }

    @Test
    public void shouldThrowFleetExceptionWithURLError() throws Exception
    {
        Context appContext = InstrumentationRegistry.getTargetContext();
        try
        {
            new DatabaseHandler(appContext, "driver1@mapotempo.com",
                "123456",
                "://mapo-fail-url");
        } catch (FleetException e)
        {
            Assert.assertNotNull(e.getFleetError());
            Assert.assertEquals(e.getFleetError(), FleetError.URL_ERROR);
        }
    }
}
