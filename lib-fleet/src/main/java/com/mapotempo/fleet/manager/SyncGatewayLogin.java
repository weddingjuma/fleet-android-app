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

import com.mapotempo.fleet.api.FleetException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import mapotempo.com.fleet.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SyncGatewayLogin
{
    // http://www.vogella.com/tutorials/JavaLibrary-OkHttp/article.html
    // https://developer.couchbase.com/documentation/mobile/2.0/references/sync-gateway/rest-api/index.html

    public enum SyncGatewayLoginStatus
    {
        VALID,
        INVALID,
        SERVER_UNREACHABLE
    }

    private static String TAG = SyncGatewayLogin.class.getName();


    private static final MediaType JSON
        = MediaType.parse("application/json; charset=utf-8");

    private String jsonTemplate = "" +
        "{" +
        "\"name\": \"%s\"," +
        "\"password\": \"%s\"" +
        "}";

    private SyncGatewayLoginStatus res = SyncGatewayLoginStatus.SERVER_UNREACHABLE;

    public SyncGatewayLoginStatus tryLogin(final String login,
                                           String password,
                                           final String url,
                                           Context context) throws FleetException
    {
        final CountDownLatch cdl = new CountDownLatch(1);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(new CookieJar()).build();

        Request request;
        try
        {
            // Create Session
            request = new Request.Builder().url(url + "/_session")
                .post(RequestBody.create(JSON, String.format(jsonTemplate, login, password)))
                .build();
        } catch (IllegalArgumentException e)
        {
            throw FLEET_ERROR.asException(FLEET_ERROR.URL_ERROR, context.getString(R.string.invalid_url), e);
        }

        // Async post request to prevent main thread blocking
        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                cdl.countDown();
                res = SyncGatewayLoginStatus.SERVER_UNREACHABLE;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                cdl.countDown();
                if (response.code() == 200)
                    res = SyncGatewayLoginStatus.VALID;
                else
                    res = SyncGatewayLoginStatus.INVALID;
                Request request = new Request.Builder().url(url + "/_session").delete().build();
                okHttpClient.newCall(request);
            }
        });

        try
        {
            cdl.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e)
        {
            res = SyncGatewayLoginStatus.SERVER_UNREACHABLE;
        } finally
        {
            return res;
        }
    }

    private class CookieJar implements okhttp3.CookieJar
    {
        private List<Cookie> cookies;

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
        {
            this.cookies = cookies;
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url)
        {
            if (cookies != null)
                return cookies;
            return new ArrayList<Cookie>();

        }
    }
}
