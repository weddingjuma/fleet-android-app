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

package com.mapotempo.fleet.rest.syncgateway;


import android.content.Context;

import com.mapotempo.fleet.api.FleetError;
import com.mapotempo.fleet.api.FleetException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Session
{
    // http://www.vogella.com/tutorials/JavaLibrary-OkHttp/article.html
    // https://developer.couchbase.com/documentation/mobile/2.0/references/sync-gateway/rest-api/index.html

    private static String TAG = Session.class.getName();

    public enum LoginStatus
    {
        SESSION_OPEN,
        SESSION_CLOSE,
        SESSION_ERROR,
        SERVER_UNREACHABLE
    }

    private LoginStatus mSessionStatus;

    private final OkHttpClient mOkHttpClient;

    private String mUrl;

    public Session(String url)
    {
        mUrl = url;
        mSessionStatus = LoginStatus.SESSION_CLOSE;
        mOkHttpClient = new OkHttpClient.Builder().cookieJar(new CookieJar()).build();
    }

    public LoginStatus getSessionStatus()
    {
        return mSessionStatus;
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

    private static final MediaType JSON
        = MediaType.parse("application/json; charset=utf-8");

    private static final String jsonTemplate = "" +
        "{" +
        "\"name\": \"%s\"," +
        "\"password\": \"%s\"" +
        "}";

    public static Session create(String url,
                                 final String login,
                                 String password,
                                 Context context) throws FleetException
    {
        final Session res = new Session(url);

        final CountDownLatch cdl = new CountDownLatch(1);

        Request request;
        try
        {
            // Create Session Request
            request = new Request.Builder().url(url + "/_session")
                .post(RequestBody.create(JSON, String.format(jsonTemplate, login, password)))
                .build();
        } catch (IllegalArgumentException e)
        {
            throw FleetError.asException(FleetError.URL_ERROR, "invalid url", e);
        }

        // Async post request to prevent main thread blocking
        res.mOkHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                cdl.countDown();
                res.mSessionStatus = LoginStatus.SERVER_UNREACHABLE;
            }

            @Override
            public void onResponse(Call call, Response response)
            {
                cdl.countDown();
                if (response.code() == 200)
                    res.mSessionStatus = LoginStatus.SESSION_OPEN;
                else
                    res.mSessionStatus = LoginStatus.SESSION_ERROR;
            }
        });

        try
        {
            cdl.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e)
        {
            res.mSessionStatus = LoginStatus.SERVER_UNREACHABLE;
        } finally
        {
            return res;
        }
    }

    public static void delete(Session session)
    {
        if (session.mSessionStatus.equals(LoginStatus.SESSION_OPEN))
        {
            Request request = new Request.Builder().url(session.mUrl + "/_session").delete().build();
            session.mOkHttpClient.newCall(request);
            session.mSessionStatus = LoginStatus.SESSION_CLOSE;
        }
    }
}
